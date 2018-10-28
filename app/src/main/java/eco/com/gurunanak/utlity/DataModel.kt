package com.tudle.utils


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.constraint.BuildConfig
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import eco.com.gurunanak.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DataModel {


    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"


    private var pd_st: ProgressDialog? = null




    fun loading_box_stop() {
        if (pd_st != null)
            if (pd_st!!.isShowing) {
                pd_st!!.dismiss()
            }
    }


    @SuppressLint("SimpleDateFormat")
    fun timeZoneConverterReverse(timeAndDate: String): String {

        Log.e("===", "==$timeAndDate")
        // Date will return local time in Javayyyy-MM-dd'T'HH:mm:ssz
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        try {
            val myDate = sdf.parse(timeAndDate)
            val converter = SimpleDateFormat("EEE, dd MMM yyyy")
            // converter.setTimeZone(TimeZone.getTimeZone("GMT"));

            //System.out.println("local time : " + myDate);
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            Log.e("time in GMT : ===", converter.format(myDate))

            return converter.format(myDate)

        } catch (e: ParseException) {

            e.printStackTrace()

        }

        Log.e("===", "==$timeAndDate")
        return timeAndDate

    }

    fun Internetcheck(act: Activity): Boolean {
        val connectivity = act.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null)
                for (i in info.indices)
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }

        }
        return false
    }

    fun getAlertDialogWithbothMessage(act: Activity, title: String, msg: String) {

        AlertDialog.Builder(act).setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, id ->
                    dialog.cancel()
                    // dialogNoInternet.cancel();
                }.show()

    }

    fun getAlertDialogWithbothMessageFinish(act: Activity, title: String, msg: String) {

        AlertDialog.Builder(act).setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, id ->
                    dialog.cancel()
                    act.finish()
                    // dialogFinish.cancel();
                }.show()

    }

    fun getAlertDialogWithMessage(act: Activity, msg: String) {
        AlertDialog.Builder(act).setTitle("OOPS!").setMessage(msg).setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, id -> dialog.cancel() }.show()
    }


    fun hideKeyBoard(ed: EditText, c: Activity) {
        val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(ed.windowToken, 0)

    }


    fun ChangeStatusBarColor(context: Activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val window = context.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = context.resources.getColor(R.color.colorAccent)
        }
    }

    fun isGoogleMapsInstalled(context: Context): Boolean {
        try {
            val info = context.packageManager.getApplicationInfo("com.google.android.apps.maps", 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }


    fun hideKeyBoard(mContext: Activity) {
        try {
            val inputManager = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (mContext.currentFocus != null)
                inputManager.hideSoftInputFromWindow(mContext.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
        }

    }



    fun checkPermission(ctx: Activity, permission: String): Boolean {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {


            val result = ContextCompat.checkSelfPermission(ctx, permission)
            //        Manifest.permission.ACCESS_FINE_LOCATION
            return if (result == PackageManager.PERMISSION_GRANTED) {

                true

            } else {

                false

            }
        } else {
            return true
        }
    }

    fun requestPermission(context: Activity, permission: String, requestCode: Int, message: String) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {

            Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        } else {

            ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
        }
    }




    fun requestPermissionMultiple(context: Activity, permission: Array<String>, requestCode: Int, message: String) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission[0]) &&
                ActivityCompat.shouldShowRequestPermissionRationale(context, permission[1])) {

            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package",
                    BuildConfig.APPLICATION_ID, null)
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)

        } else {

            ActivityCompat.requestPermissions(context, permission, requestCode)
        }

    }







    fun rgb(hex: String): Int {
        val color = java.lang.Long.parseLong(hex.replace("#", ""), 16).toInt()
        val r = color shr 16 and 0xFF
        val g = color shr 8 and 0xFF
        val b = color shr 0 and 0xFF
        return Color.rgb(r, g, b)
    }





}
