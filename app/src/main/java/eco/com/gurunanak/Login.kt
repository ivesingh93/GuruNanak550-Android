package eco.com.gurunanak

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.android.gms.maps.model.LatLng
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import eco.com.gurunanak.http.OkHttpListener
import eco.com.gurunanak.http.OkHttpPostHandler
import eco.com.gurunanak.sharedprefrences.GurunanakPreferences
import eco.com.gurunanak.sharedprefrences.JBGurunanakPreferences
import eco.com.gurunanak.utlity.Constant
import eco.com.gurunanak.utlity.UtilityCommon
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.util.HashMap


class Login : Activity(), OkHttpListener {
    override fun onOkHttpResponse(callResponse: String, pageId: Int, latLng: LatLng) {

    }

    internal lateinit var dialog_progress: ACProgressFlower
    val JSON = MediaType.parse("application/json; charset=utf-8")
    internal lateinit var mSharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initial()
        if (!JBGurunanakPreferences.getLoginId(mSharedPref).equals("")) {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }


        btn_login.setOnClickListener { view ->
            if (UtilityCommon.isNetworkAvailable(this@Login) === true) {
                if (input_email.text.equals("")) {
                    input_email.error = "Please enter email"
                } else if (!UtilityCommon.isValidEmail(input_email.text.toString())) {
                    input_email.error = "Please enter valid email"
                } else if (input_password.text.equals("")) {
                    input_password.error = "Please enter password"
                } else {
                    postLogin()
                }
            } else {
                UtilityCommon.showSnackBar(this@Login, "No internet connection")
            }
        }
        link_signup.setOnClickListener { view ->
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }

    private fun postLogin() {
        dialog_progress.show()
        val params = HashMap<String, String>()
        params["email"] = input_email.text.toString()
        params["password"] = input_password.text.toString()
        val parameter = JSONObject(params)
        val formBody = RequestBody.create(JSON, parameter.toString())

        OkHttpPostHandler(Constant.BASE_URL + Constant.LOGIN_URL, formBody, this,
                1).execute()
    }

    private fun initial() {
        dialog_progress = ACProgressFlower.Builder(this@Login)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .bgColor(Color.TRANSPARENT)
                .bgAlpha(0f)
                .bgCornerRadius(0f)
                .fadeColor(Color.DKGRAY).build()
        dialog_progress.setCanceledOnTouchOutside(true)
        mSharedPref = getSharedPreferences(
                GurunanakPreferences.Gurunanak_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun onOkHttpResponse(callResponse: String, pageId: Int) {
        if (pageId == 1) {
            dialog_progress.dismiss()
            Log.i("LOGIN_RESPONSE", callResponse)
            if (!callResponse.equals("")) {
                try {
                    val json = JSONTokener(callResponse).nextValue() as JSONObject
                    val responseCode = json.get("ResponseCode") as Int
                    val msg = json.get("Message") as String
                    if (responseCode == 200) {
                        val toast = Toast.makeText(this@Login, msg, Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        JBGurunanakPreferences.setLoginId(mSharedPref, json.get("email") as String)
                        JBGurunanakPreferences.setOrgName(mSharedPref, json.get("organization_name") as String)
                        JBGurunanakPreferences.setJWTToken(mSharedPref, json.get("token") as String)


                        val intent = Intent(this, MapsActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        val toast = Toast.makeText(this@Login, msg, Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }

    }

    override fun onOkHttpError(error: String) {

    }


}
