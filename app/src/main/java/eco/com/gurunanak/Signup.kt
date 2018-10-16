package eco.com.gurunanak

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.okhttp.MediaType
import com.tudle.utils.BaseActivity
import com.tudle.utils.DataModel
import eco.com.gurunanak.network.RestClient
import eco.com.gurunanak.utlity.UtilityCommon
import kotlinx.android.synthetic.main.signup.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Signup : BaseActivity() {

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
                }
                else if (!input_password_confirm.text.toString().equals(input_password.text.toString())) {
                    input_password_confirm.error = "Please confirm your password"
                    Log.e("er"+input_password_confirm.text,"er2 "+input_password.text)
                }

//                else if (input_mobile.text.equals("")) {
//                    input_mobile.error = "Please enter your mobile number"
//
//                }
                else if (input_Address.text.length<4) {
                    input_Address.error = "Please enter address"
                } else {
                    postSignup()
                }

            } else {
                UtilityCommon.showSnackBar(this@Signup, "No internet connection")
            }

        }


    }

    @SuppressLint("MissingPermission")
    private fun initial() {

        dialog_progress = ACProgressFlower.Builder(this@Signup)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .bgColor(Color.TRANSPARENT)
                .bgAlpha(0f)
                .bgCornerRadius(0f)
                .fadeColor(Color.DKGRAY).build()
        dialog_progress.setCanceledOnTouchOutside(true)



        if(DataModel.checkPermission(this, android.Manifest.permission.READ_PHONE_STATE)){
            val tMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val mPhoneNumber = tMgr.line1Number
            input_mobile.setText(mPhoneNumber)
        }else{
            DataModel.requestPermission(this, android.Manifest.permission.READ_PHONE_STATE,101,
                    "enable permission")
        }



    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val tMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val mPhoneNumber = tMgr!!.line1Number
                input_mobile.setText(mPhoneNumber)
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
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
        params["address"] = input_Address.text.toString()
        params["organization_name"] = input_Org_name.text.toString()
        params["phone_number"] = input_mobile.text.toString()
        params["age"] = "27"

        val call1 = RestClient.create().register(params)
        call1.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("error", "error" + t.toString())

                dialog_progress.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                dialog_progress.dismiss()
                var gson = Gson()
                Log.e("data ", "body" + response.code())
                Log.e("data ", "body" + response.body().toString())
                var ob=JSONObject(response.body().toString())
                if (response.code() == 200) {
                    if(ob.getInt("ResponseCode")==200){
                        dialog_progress.dismiss()
                        BackBtn(input_password)
                        Toast.makeText(applicationContext,ob.getString("Message"),Toast.LENGTH_LONG).show()
                    }
                    else{
                        try {
                            Toast.makeText(applicationContext, ob.getString("Message"), Toast.LENGTH_LONG).show()
                        }catch (E:Exception){}
                    }

                }

            }

        })


    }



//    override fun onOkHttpResponse(callResponse: String, pageId: Int) {
//        if (pageId == 1) {
//            Log.i("SIGNUP_RESPONSE", callResponse);
//            dialog_progress.dismiss()
//
//            if (!callResponse.equals("")) {
//                try {
//                    val json = JSONTokener(callResponse).nextValue() as JSONObject
//                    Log.e("Data","IS "+json.toString())
//                    val responseCode = json.get("ResponseCode") as Int
//                    val msg = json.get("Message") as String
//                    if (responseCode == 200) {
//                        val toast = Toast.makeText(this@Signup, msg, Toast.LENGTH_LONG)
//                        toast.setGravity(Gravity.CENTER, 0, 0)
//                        toast.show()
//                        finish()
//                    } else {
//
//                        val toast = Toast.makeText(this@Signup, msg, Toast.LENGTH_LONG)
//                        toast.setGravity(Gravity.CENTER, 0, 0)
//                        toast.show()
//                    }
//
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//
//            }
//        }
//
//    }

}