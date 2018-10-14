package eco.com.gurunanak

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent.getActivity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.*
import com.google.android.gms.location.*
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceBufferResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import eco.com.gurunanak.adapter.CustomInfoWindowGoogleMap
import eco.com.gurunanak.adapter.PlacesAdapter
import eco.com.gurunanak.ftp.MyFTPClientFunctions
import eco.com.gurunanak.http.OkHttpGetHandler
import eco.com.gurunanak.http.OkHttpListener
import eco.com.gurunanak.http.OkHttpPostHandler
import eco.com.gurunanak.http.OkHttpPostHandlerExpense
import eco.com.gurunanak.model.InfoWindowData
import eco.com.gurunanak.model.JBJBMarker
import eco.com.gurunanak.sharedprefrences.GurunanakPreferences
import eco.com.gurunanak.sharedprefrences.JBGurunanakPreferences
import eco.com.gurunanak.utlity.Constant
import eco.com.gurunanak.utlity.ImageFilePath
import eco.com.gurunanak.utlity.PathUtil
import eco.com.gurunanak.utlity.UtilityCommon
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OkHttpListener {


    var googleMap: GoogleMap? = null
    lateinit var placesAdapter: PlacesAdapter
    lateinit var latLng: LatLng
    lateinit var mLocationRequest: LocationRequest
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mLocationCallback: LocationCallback
    lateinit var mGeoDataClient: GeoDataClient
    lateinit var mSettingsClient: SettingsClient
    lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private val REQUEST_CHECK_SETTINGS = 0x1
    var isAutoCompleteLocation = false
    lateinit var location: Location
    val REQUEST_LOCATION = 1011
    internal lateinit var tapactionlayout: LinearLayout
    internal lateinit var bottomSheet: View
    private var mBottomSheetBehavior1: BottomSheetBehavior<*>? = null
    lateinit var btnAll: Button
    lateinit var btnApproved: Button
    lateinit var btnAssign: Button
    lateinit var btnDenied: Button
    lateinit var btnAllYou: Button
    lateinit var btnCancel: Button
    lateinit var btnSave: Button
    lateinit var image1: ImageView
    lateinit var image3: ImageView

    lateinit var enter_place_bottomsheet: AutoCompleteTextView
    lateinit var cancel_bottomsheet: AppCompatImageView
    lateinit var editDate: EditText
    lateinit var editNumberTreePlant: EditText
    lateinit var editPlantType: EditText
    lateinit var editContactPerson: EditText
    lateinit var editContactNumber: EditText
    lateinit var editRemarksr: EditText
    lateinit var btnFTP: Button
    lateinit var ln_top: LinearLayout
    internal lateinit var mSharedPref: SharedPreferences
    private var fromDatePickerDialog: DatePickerDialog? = null
    private var newDate: Calendar? = null
    internal lateinit var sd2: SimpleDateFormat
    val JSON = MediaType.parse("application/json; charset=utf-8")
    private var mGson: Gson? = null
    internal lateinit var mJBMaker: JBJBMarker
    private val mMapBean = ArrayList<JBJBMarker.MasterBean>()
    private val hashMapMarker = HashMap<Marker, String>()

    val REQUEST_CAMERA = 0
    val SELECT_FILE = 1
    internal var destination: File? = null

    private val INITIAL_REQUEST = 1338
    private val LOCATION_REQUEST = INITIAL_REQUEST + 1
    private var userChoosenTask: String? = null
    private var realPath: String? = null

    val RequestPermissionCode = 1
    var ftpclient: MyFTPClientFunctions? = null
    internal var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        MapsInitializer.initialize(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        mGeoDataClient = Places.getGeoDataClient(this, null);



        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val loc = locationResult!!.lastLocation
                if (!isAutoCompleteLocation) {
                    location = loc
                    latLng = LatLng(location.latitude, location.longitude)
                    //assignToMap()
                    getDataLatLng("all", "all", latLng)
                }
            }

        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


        mSettingsClient = LocationServices.getSettingsClient(this)
        val builder = LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()

        placesAdapter = PlacesAdapter(this, android.R.layout.simple_list_item_1, mGeoDataClient, null)
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
            hideKeyboard()
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

        mSharedPref = getSharedPreferences(
                GurunanakPreferences.Gurunanak_PREFERENCES, Context.MODE_PRIVATE)
        tapactionlayout = findViewById<View>(R.id.tap_action_layout) as LinearLayout
        bottomSheet = findViewById(R.id.bottom_sheet1)
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior1!!.peekHeight = 80

        btnAll = findViewById(R.id.btnAll)
        btnAssign = findViewById(R.id.btnAssign)
        btnApproved = findViewById(R.id.btnApproved)
        btnDenied = findViewById(R.id.btnDenied)
        btnAllYou = findViewById(R.id.btnAllYou)
        btnFTP = findViewById(R.id.btnFTP)
        sd2 = SimpleDateFormat("MM/dd/yyyy")
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)


        editDate = findViewById(R.id.editDate)
        editDate.setFocusable(false)
        editDate.setClickable(true)
        editNumberTreePlant = findViewById(R.id.editNumberTreePlant)
        editPlantType = findViewById(R.id.editPlantType)
        editContactPerson = findViewById(R.id.editContactPerson)
        editContactNumber = findViewById(R.id.editContactNumber)
        editRemarksr = findViewById(R.id.editRemarksr)
        image1 = findViewById(R.id.image1)
        image3 = findViewById(R.id.image3)

        cancel_bottomsheet = findViewById(R.id.cancel_bottomsheet)
        enter_place_bottomsheet = findViewById(R.id.enter_place_bottomsheet)


        ln_top = findViewById(R.id.ln_top)
        setDateTimeField()
        mBottomSheetBehavior1!!.state = BottomSheetBehavior.STATE_COLLAPSED
        mBottomSheetBehavior1!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //  if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                tapactionlayout.visibility = View.VISIBLE
                ln_top.visibility = View.VISIBLE
                enter_place_bottomsheet.setAdapter(placesAdapter)
                enter_place_bottomsheet.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (count > 0) {
                            cancel_bottomsheet.visibility = View.VISIBLE
                        } else {
                            cancel_bottomsheet.visibility = View.GONE
                        }
                    }
                })
                enter_place_bottomsheet.setOnItemClickListener({ parent, view, position, id ->
                    //getLatLong(placesAdapter.getPlace(position))
                    hideKeyboard()
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

                            Log.i("LAT-LNG", "" + latLng);

                            places.release()
                            assignToMap()


                        }

                    })

                    /* Toast.makeText(applicationContext, "Clicked: " + primaryText,
                             Toast.LENGTH_SHORT).show()*/
                })
                cancel_bottomsheet.setOnClickListener {
                    enter_place_bottomsheet.setText("")
                }

                //  }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tapactionlayout.visibility = View.GONE
                    ln_top.visibility = View.VISIBLE
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tapactionlayout.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        ftpclient = MyFTPClientFunctions()
        Thread(Runnable {
            isConnected = ftpclient!!.ftpConnect("pixeldropinc.com",
                    "ecosikh@pixeldropinc.com",
                    "ecosikh", 21)

            Log.i("FTP-Connect", "" + isConnected)

        }).start()



        btnAll.setOnClickListener { view ->
            getDataLatLng("all", "all", latLng)
        }
        btnAllYou.setOnClickListener { view ->
            getDataLatLng(JBGurunanakPreferences.getLoginId(mSharedPref).toString(), "all", latLng)
        }

        btnAssign.setOnClickListener { view ->
            getDataLatLng(JBGurunanakPreferences.getLoginId(mSharedPref).toString(), "Pending", latLng)
        }

        btnApproved.setOnClickListener { view ->
            getDataLatLng(JBGurunanakPreferences.getLoginId(mSharedPref).toString(), "Approved", latLng)
        }

        btnDenied.setOnClickListener { view ->
            getDataLatLng(JBGurunanakPreferences.getLoginId(mSharedPref).toString(), "Denied", latLng)
        }

        btnFTP.setOnClickListener { view ->
            selectImage()
        }


        editDate.setOnClickListener { view ->
            fromDatePickerDialog?.show()
        }

        image1.setOnClickListener { view ->

        }

        image3.setOnClickListener { view ->
            selectImage()
        }


        btnCancel.setOnClickListener { view ->
            mBottomSheetBehavior1!!.state = BottomSheetBehavior.STATE_COLLAPSED
            //tapactionlayout.visibility = View.GONE
            tapactionlayout.visibility = View.VISIBLE
            ln_top.visibility = View.GONE

            //  NestedScrollView.scrollTo(0, 0);
        }

        btnSave.setOnClickListener { view ->
            if (enter_place_bottomsheet.text.toString().equals("")) {
                enter_place_bottomsheet.error = "Please enter location"
            } else if (editDate.text.toString().equals("")) {
                editDate.error = "Please enter date"
            } else if (editNumberTreePlant.text.toString().equals("")) {
                editNumberTreePlant.error = "Please enter number of trees"
            } else if (editContactPerson.text.toString().equals("")) {
                editContactPerson.error = "Please enter contact person name"
            } else if (editContactNumber.text.toString().equals("")) {
                editContactNumber.error = "Please enter contact person number"
            } else {
                postSaveJson()
            }
        }



        tapactionlayout.setOnClickListener {
            if (mBottomSheetBehavior1!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior1!!.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

    }

    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")

        val builder = AlertDialog.Builder(this@MapsActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            val result = UtilityCommon.checkPermission(this@MapsActivity)

            if (items[item] == "Take Photo") {
                userChoosenTask = "Take Photo"
                // if (result == true) {
                cameraIntent()
                // }
            } else if (items[item] == "Choose from Library") {
                userChoosenTask = "Choose from Library"
                //if (result == true) {
                galleryIntent()
                // }
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    fun EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity,
                        Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(this@MapsActivity, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show()

        } else {

            ActivityCompat.requestPermissions(this@MapsActivity,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE), RequestPermissionCode)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                // onSelectFromGalleryResult(data)

                var uri = data!!.getData()
                realPath = ImageFilePath.getPath(this, data!!.getData());
                Log.i("IMAGE--PATH", realPath);
                val imageDownloader = ImageDownloader()
                imageDownloader.execute(realPath)
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data!!)
            }
        }
    }

    fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }
    /*private fun onSelectFromGalleryResult(data:Intent) {
        val wholeID = DocumentsContract.getDocumentId(uriThatYouCurrentlyHave)
        // Split at colon, use second item in the array
        val id = wholeID.split((":").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
        val column = arrayOf<String>(MediaStore.Images.Media.DATA)
        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf<String>(id), null)
        val filePath = ""
        val columnIndex = cursor.getColumnIndex(column[0])
        if (cursor.moveToFirst())
        {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
    }*/


    private fun onCaptureImageResult(data: Intent) {
        var thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
        val bytes = ByteArrayOutputStream()

        thumbnail = Bitmap.createScaledBitmap(thumbnail, 500, 500, false)

        thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes)

        destination = File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis().toString() + ".jpg")
        val imageDownloader = ImageDownloader()
        imageDownloader.execute(destination!!.getPath())
        val fo: FileOutputStream
        try {
            destination!!.createNewFile()
            fo = FileOutputStream(destination)

            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun galleryIntent() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var intent = Intent()
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FILE);
        } else {
            var intent = Intent()
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FILE);
        }
    }

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun assignToMap() {
        //googleMap?.clear()

        val options = MarkerOptions()
                .position(latLng)
                .title("My Location")
        googleMap?.apply {
            // addMarker(options)
            moveCamera(CameraUpdateFactory.newLatLng(latLng))
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
        }
    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()?.addOnCompleteListener { task ->
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

    private fun initLocation() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MapsActivity)
            getLastLocation()
            try {

                mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                        .addOnSuccessListener(this, object : OnSuccessListener<LocationSettingsResponse> {
                            override fun onSuccess(p0: LocationSettingsResponse?) {
                                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                        mLocationCallback, Looper.myLooper());
                            }

                        }).addOnFailureListener(this, object : OnFailureListener {
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
                                            rae.startResolutionForResult(this@MapsActivity, REQUEST_CHECK_SETTINGS);
                                        } catch (sie: IntentSender.SendIntentException) {
                                            Log.i("Location", "PendingIntent unable to execute request.");
                                        }
                                    }

                                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                                        Toast.makeText(this@MapsActivity, "Location settings are inadequate, and cannot be \"+\n" +
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

    override fun onMapReady(p0: GoogleMap?) {
        Log.v("googleMap", "googleMap==" + googleMap)
        googleMap = p0
        googleMap?.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        googleMap?.getUiSettings()?.apply {
            isZoomControlsEnabled = false
            isCompassEnabled = false
            isMyLocationButtonEnabled = false
        }
    }


    /* To hide Keyboard */
    fun hideKeyboard() {
        try {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation()
            } else {
                Toast.makeText(this@MapsActivity, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }

        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) run {
            if (userChoosenTask == "Take Photo")

                cameraIntent()
            else if (userChoosenTask == "Choose from Library")
                galleryIntent()
        }

    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)

    }

    override fun onResume() {
        super.onResume()

        if (checkPermissions()) {
            initLocation()
        } else {
            requestPermissions();
            EnableRuntimePermission()
        }

    }


    private fun setDateTimeField() {
        //binding.editExp.setOnClickListener(this)
        val newCalendar = Calendar.getInstance()
        fromDatePickerDialog = DatePickerDialog(this@MapsActivity, DatePickerDialog.OnDateSetListener { view,
                                                                                                        year,
                                                                                                        monthOfYear,
                                                                                                        dayOfMonth ->
            newDate = Calendar.getInstance()
            // int mYear = Calendar.YEAR;
            val mYear = newDate?.get(Calendar.YEAR)
            val mMonth = newDate?.get(Calendar.MONTH)
            val mDay = newDate?.get(Calendar.DAY_OF_MONTH)
            newDate?.set(year, monthOfYear, dayOfMonth)
            editDate.setText(sd2.format(newDate?.getTime()))
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH))
        fromDatePickerDialog!!.getDatePicker().maxDate = System.currentTimeMillis()
        // mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    fun postSaveJson(): String {
        val email = "\"email\"" + ":" + "\"" + JBGurunanakPreferences.getLoginId(mSharedPref) + "\""
        val geoLocation = "\"location\"" + ":" + "\"" + enter_place_bottomsheet.text.toString() + "\""
        val lng = "\"longitude\"" + ":" + "\"" + latLng.longitude + "\""
        val lat = "\"latitude\"" + ":" + "\"" + latLng.latitude + "\""
        val date = "\"date\"" + ":" + "\"" + editDate.text.toString() + "\""
        val plantedTree = "\"planted_trees\"" + ":" + "\"" + editNumberTreePlant.text.toString() + "\""
        val treeType: String
        treeType = editPlantType.text.toString().replace(" ", ",");
        val plantedType = "\"plants_types\"" + ":" + "\"" + treeType + "\""
        val remarkss = "\"remarks\"" + ":" + "\"" + editRemarksr.text.toString() + "\""
        val status: String = "\"status\"" + ":" + "\"" + "Pending" + "\""

        var json = ""
        json = "{$email,$geoLocation,$lng,$lat,$date,$plantedTree,$plantedType,$remarkss,$status,\"images_videos\": [\n" +
                "  \t\"url1\",\n" +
                "  \t\"url2\"\n" +
                "  ]}"
        Log.i("RESPONSE", json)
        val formBody = RequestBody.create(JSON, json)
        OkHttpPostHandler(Constant.BASE_URL + Constant.SAVE_PLANT_RECORDS, formBody, this, 0).execute()


        return json
    }

    fun getDataLatLng(email: String, status: String, latLng: LatLng) {
        //http://13.232.178.193/api/userRoutes/plantationRecord/email=singh@gmail.com&status=Pending


        OkHttpGetHandler(Constant.BASE_URL + Constant.SAVE_PLANT_RECORDS + "/email=" +
                email + "&status=" + status, this, 1, latLng).execute()


    }


    override fun onOkHttpResponse(callResponse: String, pageId: Int, latLng1: LatLng) {
        if (pageId == 0) {
            Log.i("RESPONSE", callResponse)
            //{"ResponseCode":200,"Message":"Success"}
            try {
                val json = JSONTokener(callResponse).nextValue() as JSONObject
                val responseCode = json.get("ResponseCode") as Int
                val msg = json.get("Message") as String
                if (responseCode == 200) {
                    val toast = Toast.makeText(this@MapsActivity, msg, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else {
                    val toast = Toast.makeText(this@MapsActivity, msg, Toast.LENGTH_LONG)
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
                    googleMap!!.addMarker(markerOptions.position(latLng).title(mJBMaker.master[i].full_name).snippet(mJBMaker.master[i].id.toString())
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
                    // animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
                }

            }

            googleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                override fun onMarkerClick(marker: Marker): Boolean {

                    Log.i("MARKER-CLICK", "" + marker.getSnippet())
                    return false
                }
            })
            mBottomSheetBehavior1!!.state = BottomSheetBehavior.STATE_COLLAPSED
            //tapactionlayout.visibility = View.GONE
            tapactionlayout.visibility = View.VISIBLE
            ln_top.visibility = View.GONE
        }
    }

    override fun onOkHttpError(error: String) {

    }

    override fun onOkHttpResponse(callResponse: String, pageId: Int) {

    }

    private inner class ImageDownloader : AsyncTask<String, String, Void>() {

        override fun doInBackground(vararg PathHolder: String): Void? {
            /*new Thread(new Runnable() {
                public void run() {
                    boolean status = false;*/

            Log.i("NAME_ATT", PathHolder[0].substring(PathHolder[0].lastIndexOf("/") + 1))
            // ftpclient!!.ftpChangeDirectory(DirName)
            ftpclient!!.ftpUploadProfile(
                    PathHolder[0],
                    PathHolder[0].substring(PathHolder[0].lastIndexOf("/") + 1), "/", this@MapsActivity)


            /*     }
            }).start();*/
            return null
        }

        override fun onProgressUpdate(vararg values: String) {}

        override fun onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called")
            //dialog.show()
        }

        override fun onPostExecute(result: Void) {
            Log.i("Async-Example", "onPostExecute Called")

            //dialog.dismiss()
        }

    }
}