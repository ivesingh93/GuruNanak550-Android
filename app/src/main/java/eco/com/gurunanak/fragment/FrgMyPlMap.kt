package eco.com.gurunanak.fragment

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceBufferResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.tudle.utils.DataModel
import eco.com.gurunanak.R
import eco.com.gurunanak.adapter.PlacesAdapter
import eco.com.gurunanak.http.OkHttpGetHandler
import eco.com.gurunanak.http.OkHttpListener
import eco.com.gurunanak.model.JBJBMarker
import eco.com.gurunanak.sharedprefrences.GurunanakPreferences
import eco.com.gurunanak.sharedprefrences.JBGurunanakPreferences
import eco.com.gurunanak.utlity.Constant
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
var googleMap: GoogleMap? = null
private var mapView:MapView?=null
lateinit var latLng: LatLng
lateinit var mLocationRequest: LocationRequest
lateinit var mLocationCallback: LocationCallback
var isAutoCompleteLocation = false
lateinit var location: Location
private var mGson: Gson? = null
internal lateinit var mJBMaker: JBJBMarker
lateinit var placesAdapter: PlacesAdapter
lateinit var mGeoDataClient: GeoDataClient
lateinit var mSettingsClient: SettingsClient
var mFusedLocationClient: FusedLocationProviderClient?=null
lateinit var mLocationSettingsRequest: LocationSettingsRequest
private val REQUEST_CHECK_SETTINGS = 0x1
val REQUEST_LOCATION = 1011
val RequestPermissionCode = 1
var myloc:FloatingActionButton?=null
internal lateinit var mSharedPref: SharedPreferences
class FrgMyPlMap : Fragment(), OnMapReadyCallback, OkHttpListener {
    override fun onMapReady(p0: GoogleMap?) {

        googleMap = p0;

    }


    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }


    private fun initLocation() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            getLastLocation()
            try {

                mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                        .addOnSuccessListener(activity!!, object : OnSuccessListener<LocationSettingsResponse> {
                            override fun onSuccess(p0: LocationSettingsResponse?) {
                                mFusedLocationClient!!.requestLocationUpdates(mLocationRequest,
                                        mLocationCallback, Looper.myLooper());
                            }

                        }).addOnFailureListener(activity!!, object : OnFailureListener {
                            override fun onFailure(p0: java.lang.Exception) {
                                val statusCode = (p0 as ApiException).getStatusCode();
                                when (statusCode) {
                                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                        Log.i("Location", "Location settings are not satisfied. Attempting to upgrade " +
                                                "location settings ");
                                        try {
                                            // Show the dialog by calling startResolutionForResult(), and check the
                                            // result in onActivityResult().
                                            val rae = p0 as ResolvableApiException
                                            rae.startResolutionForResult(activity!!, REQUEST_CHECK_SETTINGS);
                                        } catch (sie: IntentSender.SendIntentException) {
                                            Log.i("Location", "PendingIntent unable to execute request.");
                                        }
                                    }

                                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                                        Toast.makeText(activity!!, "Location settings are inadequate, and cannot be \"+\n" +
                                                "                                    \"fixed here. Fix in Settings.", Toast.LENGTH_LONG).show();


                                }
                            }

                        })

            } catch (unlikely: SecurityException) {
                Log.e("Location", "Lost location permission. Could not request updates. " + unlikely)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient!!.getLastLocation()?.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    location = task.getResult()
                    latLng = LatLng(location.latitude, location.longitude)
                    assignToMap()

                } else {
                    Log.w("Location", "Failed to get location.")
                }
            }
        } catch (unlikely: SecurityException) {
            Log.e("Location", "Lost location permission." + unlikely)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mSharedPref = activity!!.getSharedPreferences(
                GurunanakPreferences.Gurunanak_PREFERENCES, Context.MODE_PRIVATE)

        if (checkPermissions()) {
            initLocation()
        } else {
            EnableRuntimePermission()
        }
        return inflater.inflate(R.layout.frg_map, container, false)

    }


    fun EnableRuntimePermission() {

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        Permissions.check(activity, permissions, null, null, object : PermissionHandler() {
            override fun onGranted() {
                // Toast.makeText(applicationContext, "Camera+Storage granted.", Toast.LENGTH_SHORT).show()
                initLocation()
            }
        })


    }


    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.title = "MAP"
        mapView =view.findViewById(R.id.map) as MapView
        mapView!!.onCreate(savedInstanceState);
        mapView!!.onResume();
        mapView!!.getMapAsync(this);
        mGeoDataClient = Places.getGeoDataClient(activity!!, null);
        myloc=view.findViewById(R.id.myloc) as FloatingActionButton


        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val loc = locationResult!!.lastLocation
                if (!isAutoCompleteLocation) {
                    location = loc
                    latLng = LatLng(location.latitude, location.longitude)
                    getDataLatLng("all", "all", latLng)
                }
            }

        }
        myloc!!.setOnClickListener({getLastLocation()})

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


        mSettingsClient = LocationServices.getSettingsClient(activity!!)
        val builder = LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()

        placesAdapter = PlacesAdapter(activity!!, android.R.layout.simple_list_item_1, mGeoDataClient, null)
        enter_place.setAdapter(placesAdapter)
        enter_place.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    cancel.visibility = View.VISIBLE
                } else {
                    cancel.visibility = View.GONE
                }
            }
        })
        enter_place.setOnItemClickListener({ parent, view, position, id ->
            //getLatLong(placesAdapter.getPlace(position))
            DataModel.hideKeyBoard(activity!!)
            val item = placesAdapter.getItem(position)
            val placeId = item?.getPlaceId()
            val primaryText = item?.getPrimaryText(null)

            Log.i("Autocomplete", "Autocomplete item selected: " + primaryText)


            val placeResult = mGeoDataClient.getPlaceById(placeId)
            placeResult.addOnCompleteListener(object : OnCompleteListener<PlaceBufferResponse> {
                override fun onComplete(task: Task<PlaceBufferResponse>) {
                    val places = task.getResult()
                    val place = places.get(0)

                    val placeId = place.id
                    isAutoCompleteLocation = true
                    latLng = place.latLng
                    places.release()
                    assignToMap()
                }
            })
        })
        cancel.setOnClickListener {
            enter_place.setText("")
        }

    }

    fun getDataLatLng(email: String, status: String, latLng: LatLng) {

        OkHttpGetHandler(Constant.BASE_URL + Constant.SAVE_PLANT_RECORDS + "/email=" +
                JBGurunanakPreferences.getLoginId(mSharedPref)!! + "&status=" + status, this, 1, latLng).execute()


    }


    override fun onOkHttpResponse(callResponse: String, pageId: Int, latLng1: LatLng) {
        if (pageId == 0) {
            Log.e("RESPONSE","res  "+ callResponse)
            //{"ResponseCode":200,"Message":"Success"}
            try {
                val json = JSONTokener(callResponse).nextValue() as JSONObject
                val responseCode = json.get("ResponseCode") as Int
                val msg = json.get("Message") as String
                if (responseCode == 200) {
                    val toast = Toast.makeText(activity, msg, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else {
                    val toast = Toast.makeText(activity, msg, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        if (pageId == 1) {
            googleMap?.clear()
            mGson = Gson()
            var myCustom_JSONResponse = ""
            myCustom_JSONResponse = "{\"master\":$callResponse}"
            mJBMaker = mGson!!.fromJson<JBJBMarker>(myCustom_JSONResponse, JBJBMarker::class.java!!)
            Log.i("RESPONSE--2", myCustom_JSONResponse)
            //mMapBean.add(mJBMaker)

            for (i in mJBMaker.master.indices) {
                Log.i("lat", "" + mJBMaker.master[i].latitude.toDouble());
                val latLng = LatLng(mJBMaker.master[i].latitude.toDouble(),
                        mJBMaker.master[i].longitude.toDouble())

                if (mJBMaker.master[i].status.equals("Pending") || mJBMaker.master[i].status.equals("all")) {
                    val markerOptions = MarkerOptions()
                    googleMap!!.addMarker(markerOptions.position(latLng).title(mJBMaker.master[i].full_name).
                            snippet(mJBMaker.master[i].id.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                    googleMap!!.addMarker(markerOptions)
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 5.0f))
                }
                if (mJBMaker.master[i].status.equals("Approved") || mJBMaker.master[i].status.equals("all")) {
                    val markerOptions = MarkerOptions()
                    googleMap!!.addMarker(markerOptions.position(latLng).title(mJBMaker.master[i].full_name).snippet(mJBMaker.master[i].id.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                    googleMap!!.addMarker(markerOptions)
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 5.0f))
                }

                if (mJBMaker.master[i].status.equals("Denied") || mJBMaker.master[i].status.equals("all")) {
                    val markerOptions = MarkerOptions()
                    googleMap!!.addMarker(markerOptions.position(latLng).title(mJBMaker.master[i].full_name).snippet(mJBMaker.master[i].id.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                    googleMap!!.addMarker(markerOptions)
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 5.0f))

                }

            }

            googleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                override fun onMarkerClick(marker: Marker): Boolean {

                    Log.i("MARKER-CLICK", "" + marker.getSnippet())
                    return false
                }
            })
//            mBottomSheetBehavior1!!.state = BottomSheetBehavior.STATE_COLLAPSED
//            //tapactionlayout.visibility = View.GONE
//            tapactionlayout.visibility = View.VISIBLE
//            ln_top.visibility = View.GONE
        }
    }

    override fun onOkHttpError(error: String) {

    }

    override fun onOkHttpResponse(callResponse: String, pageId: Int) {

    }

    private fun assignToMap() {
        //googleMap?.clear()


        googleMap?.apply {
            moveCamera(CameraUpdateFactory.newLatLng(latLng))
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f))
        }
    }

}