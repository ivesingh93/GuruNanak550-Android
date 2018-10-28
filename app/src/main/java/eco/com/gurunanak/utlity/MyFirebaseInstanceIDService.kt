package eco.com.gurunanak.utlity

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import android.content.Intent
import android.app.IntentService
import android.R.id.edit
import android.preference.PreferenceManager
import android.content.SharedPreferences



class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e(TAG, "Refreshed token: " + refreshedToken!!)
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
       //Prefs.with(this).save(SharedPreferencesName.DEVICETOKEN,refreshedToken)
    }

    companion object {
        private val TAG = "MyFirebaseIIDService"
    }
}
