package eco.com.gurunanak

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.android.gms.location.places.ui.PlacePicker
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.tudle.utils.BaseActivity
import com.tudle.utils.DataModel
import eco.com.gurunanak.fragment.*
import eco.com.gurunanak.ftp.MyFTPClientFunctions
import eco.com.gurunanak.utlity.ImageFilePath
import kotlinx.android.synthetic.main.activity__home.*
import kotlinx.android.synthetic.main.app_bar_activity__home.*
import java.io.*


class Activity_Home : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    var frgNewpl:FrgAddNewPl?=null
    private var realPath: String? = null
    internal var destination: File? = null
    var ftpclient: MyFTPClientFunctions? = null
    internal var isConnected: Boolean = false
    val REQUEST_CAMERA = 2001
    val SELECT_FILE = 2002
    val selectVideo=2003
    internal lateinit var dialog_progress: ACProgressFlower

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__home)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        displaySelectedScreen(R.id.op2);

        ftpclient = MyFTPClientFunctions()
        Thread(Runnable {
            isConnected = ftpclient!!.ftpConnect("pixeldropinc.com",
                    "ecosikh@pixeldropinc.com",
                    "ecosikh", 21)

            Log.i("FTP-Connect", "" + isConnected)

        }).start()

        dialog_progress = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .bgColor(Color.TRANSPARENT)
                .bgAlpha(0f)
                .bgCornerRadius(0f)
                .fadeColor(Color.DKGRAY).build()
        dialog_progress.setCanceledOnTouchOutside(true)

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    override fun onResume() {
//        super.onResume()
//        ftpclient = MyFTPClientFunctions()
//        Thread(Runnable {
//            isConnected = ftpclient!!.ftpConnect("pixeldropinc.com",
//                    "ecosikh@pixeldropinc.com",
//                    "ecosikh", 21)
//
//            Log.i("FTP-Connect", "" + isConnected)
//
//        }).start()
//
//    }

    private fun displaySelectedScreen(itemId: Int) {

        //creating fragment object
        var fragment: Fragment? = null

        //initializing the fragment object which is selected
        when (itemId) {
            R.id.op1 -> fragment = FrgAddNewPl(this)
            R.id.op2 -> fragment = FragmentPlantationReq()
            R.id.op3 -> fragment = FrgMyPlMap()
            R.id.op4 -> fragment = FrgAbt550()
            R.id.op5 -> fragment = FrgAbtEco()
            R.id.op6 -> fragment = FrgDonate()
            R.id.op7 -> fragment = FrgContact()
            R.id.op8 -> fragment = FrgResources()
            R.id.op9 -> fragment = FrgFAQ()


        }

        //replacing the fragment
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.content_frame, fragment)
            ft.commit()
        }


        drawer_layout.closeDrawer(GravityCompat.START)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
//        when (item.itemId) {
//            R.id.op1 -> {
//                // Handle the camera action
//            }
//            R.id.op2 -> {
//
//            }
//            R.id.op3 -> {
//
//            }
//            R.id.op4 -> {
//
//            }
//            R.id.op5 -> {
//
//            }
//            R.id.op6 -> {
//
//            }
//            R.id.op7 -> {
//
//            }
//            R.id.op8 -> {
//
//            }
//            R.id.op9 -> {
//
//            }
//            R.id.op10 -> {
//
//            }
//        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun someMethodToLoadFragment(context:FrgAddNewPl){
        frgNewpl = context

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1001) {

                val place = PlacePicker.getPlace(data, this!!)
                frgNewpl!!.setPlaces(place)


            } else if (requestCode == 2002) {
                // onSelectFromGalleryResult(data)

                var uri = data!!.getData()
                realPath = ImageFilePath.getPath(this, data!!.getData());
                Log.i("IMAGE--PATH", realPath);
                val imageDownloader = ImageDownloader()
                imageDownloader.execute(realPath)
                dialog_progress.show()
            } else if (requestCode == 2001) {
                onCaptureImageResult(data!!)
                dialog_progress.show()
            } else if (requestCode == 2003) {
                realPath = ImageFilePath.getPath(this, data!!.getData());
                Log.i("IMAGE--Vid", realPath);
                val imageDownloader = ImageDownloader2()
                imageDownloader.execute(realPath)
                dialog_progress.show()
            }
        }
    }

    fun browseLoc(){
        val builder = PlacePicker.IntentBuilder()
        startActivityForResult(builder.build(this!!), 1001)
    }

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



     fun galleryIntent() {

        if(DataModel.checkPermission(this!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                && DataModel.checkPermission(this!!, android.Manifest.permission.CAMERA)
                && DataModel.checkPermission(this!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
        else{


            requestCameraAndStorage(102)
        }
    }

     fun cameraIntent() {

        if(DataModel.checkPermission(this!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                && DataModel.checkPermission(this!!, android.Manifest.permission.CAMERA)
                && DataModel.checkPermission(this!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
        }
        else{
//            DataModel.requestPermissionMultiple(this!!, arrayOf(android.Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    1022, "Enable permission")
            requestCameraAndStorage(101)



        }
    }



    fun requestCameraAndStorage(al:Int) {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Permissions.check(this, permissions, null, null, object : PermissionHandler() {
            override fun onGranted() {
               // Toast.makeText(applicationContext, "Camera+Storage granted.", Toast.LENGTH_SHORT).show()

                if(al==101){
                    cameraIntent()
                }else  if(al==102){
                    galleryIntent()
                }
            }
        })
    }



    private inner class ImageDownloader : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg PathHolder: String): String? {

            Log.i("NAME_ATT", PathHolder[0].substring(PathHolder[0].lastIndexOf("/") + 1))
          var ststus=  ftpclient!!.ftpUploadProfile(
                    PathHolder[0],
                    PathHolder[0].substring(
                            PathHolder[0].lastIndexOf("/") + 1), "/", this@Activity_Home)
            if(ststus) {
                return "http://pixeldropinc.com/ecosikh/" + PathHolder[0].substring(PathHolder[0].lastIndexOf("/") + 1)
            }else{
                return  ""
            }
        }

        override fun onProgressUpdate(vararg values: String)

        {
            Log.e("value","os  "+values)
        }

        override fun onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called")
            //dialog.show()
        }

        override fun onPostExecute(result: String) {
            Log.i("Async-Example", "onPostExecute Called"+result)

            frgNewpl!!.setUrl(result)
            dialog_progress.dismiss()
            //dialog.dismiss()
        }

    }



    private inner class ImageDownloader2 : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg PathHolder: String): String? {

            Log.i("NAME_ATT", PathHolder[0].substring(PathHolder[0].lastIndexOf("/") + 1))
            var ststus=  ftpclient!!.ftpUploadProfile(
                    PathHolder[0],
                    PathHolder[0].substring(
                            PathHolder[0].lastIndexOf("/") + 1), "/", this@Activity_Home)
            if(ststus) {
                return "http://pixeldropinc.com/ecosikh/" + PathHolder[0].substring(PathHolder[0].lastIndexOf("/") + 1)
            }else{
                return  ""
            }
        }

        override fun onProgressUpdate(vararg values: String)

        {
            Log.e("value","os  "+values)
        }

        override fun onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called")
            //dialog.show()
        }

        override fun onPostExecute(result: String) {
            Log.i("Async-Example", "onPostExecute Called"+result)

            frgNewpl!!.setUrl2(result)
            dialog_progress.dismiss()
            //dialog.dismiss()
        }

    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//      //  super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 1021) {
//            if (grantResults.size>0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                cameraIntent()
//
//            } else {
//
//                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
//            }
//        }
//        else if (requestCode == 1022) {
//            if (grantResults.size>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                galleryIntent()
//
//            } else {
//
//                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//    }

    fun browseVideo(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), selectVideo)

    }
}


