package eco.com.gurunanak

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody

import eco.com.gurunanak.http.OkHttpListener
import eco.com.gurunanak.http.OkHttpPostHandler
import eco.com.gurunanak.utlity.Constant
import eco.com.gurunanak.utlity.UtilityCommon

import kotlinx.android.synthetic.main.signup.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.util.HashMap

class Signup : AppCompatActivity(), OkHttpListener {

    internal lateinit var dialog_progress: ACProgressFlower
    val JSON = MediaType.parse("application/json; charset=utf-8")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        initial()

        btn_signup.setOnClickListener { view ->
            if (UtilityCommon.isNetworkAvailable(this@Signup) === true) {
                if (input_name.text.equals("")) {
                    input_name.error = "Please enter name"
                } else if (input_email.text.equals("")) {
                    input_email.error = "Please enter email"
                } else if (!UtilityCommon.isValidEmail(input_email.text.toString())) {
                    input_email.error = "Please enter valid email"
                } else if (input_password.text.equals("")) {
                    input_password.error = "Please enter password"
                } else if (input_phone.text.equals("")) {
                    input_phone.error = "Please enter phone number"
                } else if (input_phone.length() < 10) {
                    input_phone.error = "Please enter valid phone number"
                } else if (input_age.text.equals("")) {
                    input_age.error = "Please enter age"
                } else if (input_Address.text.equals("")) {
                    input_Address.error = "Please enter address"
                } else {
                    postSignup()
                }

            } else {
                UtilityCommon.showSnackBar(this@Signup, "No internet connection")
            }

        }

        link_login.setOnClickListener { view ->
            finish()
        }
    }

    private fun initial() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.title = "Sign-Up"
        dialog_progress = ACProgressFlower.Builder(this@Signup)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .bgColor(Color.TRANSPARENT)
                .bgAlpha(0f)
                .bgCornerRadius(0f)
                .fadeColor(Color.DKGRAY).build()
        dialog_progress.setCanceledOnTouchOutside(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun postSignup() {
        dialog_progress.show()
        val params = HashMap<String, String>()
        params["full_name"] = input_name.text.toString()
        params["email"] = input_email.text.toString()
        params["password"] = input_password.text.toString()
        params["phone_number"] = input_phone.text.toString()

        params["age"] = input_age.toString()
        params["address"] = input_Address.text.toString()
        params["organization_name"] = input_Org_name.text.toString()

        val parameter = JSONObject(params)
        val formBody = RequestBody.create(JSON, parameter.toString())

        OkHttpPostHandler(Constant.BASE_URL + Constant.REGISTER_URL, formBody, this,
                1).execute()

    }


    override fun onOkHttpResponse(callResponse: String, pageId: Int) {
        if (pageId == 1) {
            Log.i("SIGNUP_RESPONSE", callResponse);
            dialog_progress.dismiss()

            if (!callResponse.equals("")) {
                try {
                    val json = JSONTokener(callResponse).nextValue() as JSONObject
                    val responseCode = json.get("ResponseCode") as Int
                    val msg = json.get("Message") as String
                    if (responseCode == 200) {
                        val toast = Toast.makeText(this@Signup, msg, Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        finish()
                    } else {

                        val toast = Toast.makeText(this@Signup, msg, Toast.LENGTH_LONG)
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