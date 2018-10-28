package eco.com.gurunanak.utlity

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


class LocationFetcher(private val onLocationChangeListener: OnLocationChangedListener,
                      private val requestInterval: Long, var priority: Int, var contexst:Context) :
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private val TAG = this.javaClass.simpleName

    private var googleApiClient: GoogleApiClient? = null
    private var locationrequest: LocationRequest? = null
    private var location: Location? = null

    private val context: Context

    private val LOCATION_SP = "location_sp"
    private val LOCATION_LAT = "location_lat"
    private val LOCATION_LNG = "location_lng"

    private var checkLocationUpdateStartedHandler: Handler? = null
    private var checkLocationUpdateStartedRunnable: Runnable? = null


    private// Log.d("SAVED LAST LAT", "==" + latitude);
    val savedLatFromSP: Double
        @Synchronized get() {

            val preferences = context.getSharedPreferences(LOCATION_SP, 0)
            val latitude = preferences.getString(LOCATION_LAT, "" + 0)
            return java.lang.Double.parseDouble(latitude)
        }

    private val savedLngFromSP: Double
        @Synchronized get() {

            val preferences = context.getSharedPreferences(LOCATION_SP, 0)
            val longitude = preferences.getString(LOCATION_LNG, "" + 0)
            return java.lang.Double.parseDouble(longitude)
        }


    /**
     * Function to get latitude
     */
    //            Log.e("e", "=" + e.toString());
    val latitude: Double
        get() {
            try {
                val loc = getLocation()
                if (loc != null) {
                    return loc.latitude
                }
            } catch (e: Exception) {
            }

            return savedLatFromSP
        }

    /**
     * Function to get longitude
     */
    //            Log.e("e", "=" + e.toString());
    val longitude: Double
        get() {

            try {
                val loc = getLocation()
                if (loc != null) {
                    return loc.longitude
                }
            } catch (e: Exception) {
            }

            return savedLngFromSP
        }

    init {
        this.context = contexst
        connect()
    }

    @Synchronized
    fun connect() {

        destroy()

        val resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)

        if (resp == ConnectionResult.SUCCESS) { // google play services working

            if (isLocationEnabled(context)) {   // location fetching enabled

                buildGoogleApiClient(context)
            } else {                            // location disabled

            }
        } else {                                // google play services not working

            Log.e("Google Play error", "=$resp")
        }
        Log.e("Calledd", "aaa")
        startCheckingLocationUpdates()
    }

    @Synchronized
    fun destroyWaitAndConnect() {

        destroy()
        Handler().postDelayed({ connect() }, 2000)
    }

    @Synchronized
    private fun saveLatLngToSP(latitude: Double, longitude: Double) {

        val preferences = context.getSharedPreferences(LOCATION_SP, 0)
        val editor = preferences.edit()
        editor.putString(LOCATION_LAT, "" + latitude)
        editor.putString(LOCATION_LNG, "" + longitude)
        editor.commit()
    }


    /**
     * Checks if location fetching is enabled in device or not
     *
     * @param context application context
     * @return true if any location provider is enabled else false
     */
    @Synchronized
    private fun isLocationEnabled(context: Context): Boolean {

        try {
            val contentResolver = context.contentResolver

            val gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver,
                    LocationManager.GPS_PROVIDER)

            val netStatus = Settings.Secure.isLocationProviderEnabled(contentResolver,
                    LocationManager.NETWORK_PROVIDER)

            return gpsStatus || netStatus

        } catch (e: Exception) {

            e.printStackTrace()
            return false
        }

    }


    protected fun createLocationRequest(interval: Long, priority: Int) {

        locationrequest = LocationRequest()
        locationrequest!!.interval = interval
        locationrequest!!.fastestInterval = interval / 2

        if (priority == 1) {

            locationrequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        } else if (priority == 2) {

            locationrequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        } else {

            locationrequest!!.priority = LocationRequest.PRIORITY_LOW_POWER
        }
    }


    @Synchronized
    protected fun buildGoogleApiClient(context: Context) {

        googleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()
        googleApiClient!!.connect()
    }

    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates(interval: Long, priority: Int) {

        createLocationRequest(interval, priority)
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationrequest, this)
    }

    @SuppressLint("LongLogTag", "MissingPermission")
    fun getLocation(): Location? {

        try {
            if (location != null) {
                return location
            } else {
                if (googleApiClient != null && googleApiClient!!.isConnected) {
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
                    //                    Log.e("Fetching last fused location", "=" + location);
                    return location
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    @Synchronized
    fun destroy() {

        try {
            this.location = null
            //            Log.e("location", "destroy");
            if (googleApiClient != null) {
                if (googleApiClient!!.isConnected) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
                    googleApiClient!!.disconnect()
                } else if (googleApiClient!!.isConnecting) {
                    googleApiClient!!.disconnect()
                }
            }
        } catch (e: Exception) {
            //            Log.e("e", "=" + e.toString());
        }

        stopCheckingLocationUpdates()
    }


    @Synchronized
    private fun startRequest() {

        try {
            startLocationUpdates(requestInterval, priority)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConnected(connectionHint: Bundle?) {

        //        Log.e(TAG, "onConnected");
        val loc = getLocation()
        if (loc != null) {
            onLocationChangeListener.onLocationChanged(loc, priority)
        }
        startRequest()
    }

    override fun onConnectionSuspended(i: Int) {

        this.location = null
    }

    override fun onConnectionFailed(result: ConnectionResult) {

        //        Log.e(TAG, "onConnectionFailed");
        this.location = null
    }


    override fun onLocationChanged(location: Location?) {

        try {
            if (location != null) {
                //                Log.e("-----------------------"+location.getLatitude(),
                //                        location.getAccuracy()+"-----------------------"+location.getLongitude());

                //                Log.i("LOCATION_CHANGED", location.toString());
                //                Log.i("-----------------------", "-----------------------");
                this.location = location
                onLocationChangeListener.onLocationChanged(location, priority)
                saveLatLngToSP(location.latitude, location.longitude)
            }
        } catch (e: Exception) {

            e.printStackTrace()
        }

    }


    @Synchronized
    private fun startCheckingLocationUpdates() {

        checkLocationUpdateStartedHandler = Handler()

        checkLocationUpdateStartedRunnable = Runnable {
            if (this@LocationFetcher.location == null) {

                //                    destroyWaitAndConnect();
            } else {

                val timeSinceLastLocationFix = System.currentTimeMillis() - this@LocationFetcher.location!!.time

                if (timeSinceLastLocationFix > LAST_LOCATION_TIME_THRESHOLD) {

                    //                        destroyWaitAndConnect();
                } else {

                    checkLocationUpdateStartedHandler!!
                            .postDelayed(checkLocationUpdateStartedRunnable,
                                    CHECK_LOCATION_INTERVAL)
                }
            }
        }
        checkLocationUpdateStartedHandler!!.postDelayed(checkLocationUpdateStartedRunnable, CHECK_LOCATION_INTERVAL)
    }


    @Synchronized
    fun stopCheckingLocationUpdates() {
        try {
            if (checkLocationUpdateStartedHandler != null && checkLocationUpdateStartedRunnable != null) {
                checkLocationUpdateStartedHandler!!.removeCallbacks(checkLocationUpdateStartedRunnable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            checkLocationUpdateStartedHandler = null
            checkLocationUpdateStartedRunnable = null
        }
    }

    interface OnLocationChangedListener {

        /**
         * Override this method to listen to the Location Updates
         *
         * @param location
         * @param priority
         */
        fun onLocationChanged(location: Location, priority: Int)
    }

    companion object {

        private val CHECK_LOCATION_INTERVAL: Long = 20000
        private val LAST_LOCATION_TIME_THRESHOLD = (2 * 60000).toLong()
    }
}