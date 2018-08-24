package eco.com.gurunanak

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import eco.com.gurunanak.adapter.PlacesAdapter

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener {


    private var mMap: GoogleMap? = null
    internal lateinit var mGoogleApiClient: GoogleApiClient
    //To store longitude and latitude from map
    private var longitude: Double = 0.toDouble()
    private var latitude: Double = 0.toDouble()
    //Google ApiClient
    private var googleApiClient: GoogleApiClient? = null
    private val TAG = "gps"
    internal lateinit var mLocationRequest: LocationRequest
    internal var INTERVAL = 1000
    internal var FASTEST_INTERVAL = 500
    internal lateinit var floatingActionButton: FloatingActionButton
    private var mBottomSheetBehavior1: BottomSheetBehavior<*>? = null
    internal lateinit var tapactionlayout: LinearLayout
    internal lateinit var bottomSheet: View

    //google places
    lateinit var placesAdapter: PlacesAdapter
    lateinit var latLng: LatLng
    lateinit var mGeoDataClient: GeoDataClient

    // The callback for the management of the user settings regarding location
    private val mResultCallbackFromSettings = ResultCallback<LocationSettingsResult> { result ->
        val status = result.status
        //final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> {
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                // Location settings are not satisfied. But could be fixed by showing the user
                // a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            this@MapsActivity,
                            REQUEST_CHECK_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(TAG, "Settings change unavailable. We have no way to fix the settings so we won't show the dialog.")
        }// All location settings are satisfied. The client can initialize location
        // requests here.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mGeoDataClient = Places.getGeoDataClient(this, null);

        floatingActionButton = findViewById<View>(R.id.fab) as FloatingActionButton
        tapactionlayout = findViewById<View>(R.id.tap_action_layout) as LinearLayout
        bottomSheet = findViewById(R.id.bottom_sheet1)
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior1!!.peekHeight = 120
        mBottomSheetBehavior1!!.state = BottomSheetBehavior.STATE_COLLAPSED
        mBottomSheetBehavior1!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tapactionlayout.visibility = View.VISIBLE
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tapactionlayout.visibility = View.GONE
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tapactionlayout.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        tapactionlayout.setOnClickListener {
            if (mBottomSheetBehavior1!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior1!!.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        // Find the toolbar view inside the activity layout
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.search)
        supportActionBar!!.title = "Search here"


        mapFragment.getMapAsync(this)
        //Initializing googleapi client
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mLocationRequest = LocationRequest()
        //        mLocationRequest.setNumUpdates(1);
        //        mLocationRequest.setExpirationTime(6000);
        mLocationRequest.interval = INTERVAL.toLong()
        mLocationRequest.fastestInterval = FASTEST_INTERVAL.toLong()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        floatingActionButton.setOnClickListener { getCurrentLocation() }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //Setting onMarkerDragListener to track the marker drag
        mMap!!.setOnMarkerDragListener(this)
        //Adding a long click listener to the map
        mMap!!.setOnMapLongClickListener(this)

        if (checkPermission()) {
            buildGoogleApiClient()
            // Check the location settings of the user and create the callback to react to the different possibilities
            val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest)
            val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build())
            result.setResultCallback(mResultCallbackFromSettings)
        } else {
            requestPermission()
        }
    }


    private fun requestPermission() {

        ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), RequestPermissionCode)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            RequestPermissionCode ->

                if (grantResults.size > 0) {

                    val finelocation = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val coarselocation = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (finelocation && coarselocation) {

                        if (checkPermission())
                            buildGoogleApiClient()
                        Toast.makeText(this@MapsActivity, "Permission Granted", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@MapsActivity, "Permission Denied", Toast.LENGTH_LONG).show()

                    }
                }
        }
    }

    fun checkPermission(): Boolean {

        val FirstPermissionResult = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        val SecondPermissionResult = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED

    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient.connect()
    }


    override fun onLocationChanged(location: Location) {


    }

    override fun onConnected(bundle: Bundle?) {

        getCurrentLocation()
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onStart() {
        googleApiClient!!.connect()
        super.onStart()
    }

    override fun onStop() {
        googleApiClient!!.disconnect()
        super.onStop()
    }

    override fun onMapLongClick(latLng: LatLng) {

        //Clearing all the markers
        mMap!!.clear()

        //Adding a new marker to the current pressed position we are also making the draggable true
        mMap!!.addMarker(MarkerOptions()
                .position(latLng)
                .draggable(true))

    }

    override fun onMarkerDragStart(marker: Marker) {

    }

    override fun onMarkerDrag(marker: Marker) {

    }

    override fun onMarkerDragEnd(marker: Marker) {
        //Getting the coordinates
        latitude = marker.position.latitude
        longitude = marker.position.longitude

        //Moving the map
        moveMap()

    }

    //Getting current location
    private fun getCurrentLocation() {
        var location: Location? = null
        if (checkPermission()) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        }

        if (location != null) {
            //Getting longitude and latitude
            longitude = location.longitude
            latitude = location.latitude

            //moving the map to location
            moveMap()
        }
    }

    //Function to move the map
    private fun moveMap() {
        //String to display current latitude and longitude
        val msg = latitude.toString() + ", " + longitude

        //Creating a LatLng Object to store Coordinates
        val latLng = LatLng(latitude, longitude)

        //Adding marker to map
        mMap!!.addMarker(MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")) //Adding a title

        //Moving the camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))

        //Animating the camera
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))

        //Displaying current coordinates in toast
        //  Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    companion object {
        val RequestPermissionCode = 1
        val REQUEST_CHECK_SETTINGS = 123
    }


}

