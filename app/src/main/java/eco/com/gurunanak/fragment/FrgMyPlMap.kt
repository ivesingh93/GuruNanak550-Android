package eco.com.gurunanak.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
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
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.tudle.utils.DataModel
import eco.com.gurunanak.ActivityPlantationDetail
import eco.com.gurunanak.R
import eco.com.gurunanak.adapter.PlacesAdapter
import eco.com.gurunanak.http.OkHttpGetHandler
import eco.com.gurunanak.http.OkHttpListener
import eco.com.gurunanak.model.JBJBMarker
import eco.com.gurunanak.sharedprefrences.Prefs
import eco.com.gurunanak.sharedprefrences.SharedPreferencesName
import eco.com.gurunanak.utlity.Constant
import eco.com.gurunanak.utlity.LocationFetcher
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
private var googleMap: GoogleMap? = null
private var mapView:MapView?=null
private var latLng: LatLng?=null
private lateinit var mLocationRequest: LocationRequest
private lateinit var mLocationCallback: LocationCallback
private var isAutoCompleteLocation = false
private var mGson: Gson? = null
private  lateinit var mJBMaker: JBJBMarker
private lateinit var placesAdapter: PlacesAdapter
private lateinit var mGeoDataClient: GeoDataClient

private var myloc:FloatingActionButton?=null
private var isMapMoved=false
private var isLocationFteched:Boolean=false
class FrgMyPlMap : Fragment(), OnMapReadyCallback, OkHttpListener ,
        LocationFetcher.OnLocationChangedListener{

    var   locationFetcher:LocationFetcher?=null;

        override fun onLocationChanged(location: Location, priority: Int) {
            latLng = LatLng(location.latitude, location.longitude)

            if(!isMapMoved) {
                assignToMap()
            }
            if(!isLocationFteched) {
                if(latLng!=null) {
                    Log.e("map is ","ready2")
                    getDataLatLng("all", "all", latLng!!)
                }
            }

            isMapMoved=true

        }

    override fun onMapReady(p0: GoogleMap?) {

        googleMap = p0;
        isLocationFteched=false
        Log.e("map is ","ready")

        if(checkPermissions()) {
            locationFetcher = LocationFetcher(this, 1000, 1,activity!!);
            locationFetcher!!.connect();
        }
        else{
            EnableRuntimePermission()
        }

    }

    override fun onResume() {
        super.onResume()
        activity!!.title = "MAP"
    }
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {




        return inflater.inflate(R.layout.frg_map, container, false)

    }


    fun EnableRuntimePermission() {

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        Permissions.check(activity, permissions, null, null, object : PermissionHandler() {
            override fun onGranted() {
                locationFetcher = LocationFetcher(this@FrgMyPlMap, 1000, 1,activity!!);
                locationFetcher!!.connect();

            }
        })

    }


    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView =view.findViewById(R.id.map) as MapView
        mapView!!.onCreate(savedInstanceState);
        mapView!!.onResume();
        mapView!!.getMapAsync(this)
        mGeoDataClient = Places.getGeoDataClient(activity!!, null);
        myloc=view.findViewById(R.id.myloc) as FloatingActionButton



        myloc!!.setOnClickListener({

            if(latLng!=null){
                assignToMap()
            }


        })

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)



        val builder = LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)


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
                    val place = places!!.get(0)!!

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
                Prefs.with(activity!!).getString(SharedPreferencesName.EMAIL,"")!! + "&status=" + status, this, 1, latLng).execute()


    }


    override fun onOkHttpResponse(callResponse: String, pageId: Int, latLng1: LatLng) {
        isLocationFteched=true
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
                    googleMap!!.addMarker(markerOptions.position(latLng).snippet(mJBMaker!!.master[i].id.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                    googleMap!!.addMarker(markerOptions)
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 5.0f))
                }
                if (mJBMaker.master[i].status.equals("Approved") || mJBMaker.master[i].status.equals("all")) {
                    val markerOptions = MarkerOptions()
                    googleMap!!.addMarker(markerOptions.position(latLng).snippet(mJBMaker!!.master[i].id.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                    googleMap!!.addMarker(markerOptions)
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 5.0f))
                }

                if (mJBMaker.master[i].status.equals("Denied") || mJBMaker.master[i].status.equals("all")) {
                    val markerOptions = MarkerOptions()
                    googleMap!!.addMarker(markerOptions.position(latLng).snippet(mJBMaker!!.master[i].id.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                    googleMap!!.addMarker(markerOptions)
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 5.0f))

                }

            }

            googleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                override fun onMarkerClick(marker: Marker): Boolean {

                    Log.i("MARKER-CLICK", "" + marker.id)
                    var intent=Intent(activity,ActivityPlantationDetail::class.java)
                    intent.putExtra("id",marker.getSnippet())
                    marker.hideInfoWindow()
                    startActivity(intent)
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

        if(googleMap!=null) {
            googleMap?.apply {
                moveCamera(CameraUpdateFactory.newLatLng(latLng))
                animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f))
            }
        }
    }

}