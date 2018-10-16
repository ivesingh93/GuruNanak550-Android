package eco.com.gurunanak

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.okhttp.MediaType
import com.tudle.utils.BaseActivity
import eco.com.gurunanak.http.OkHttpListener
import eco.com.gurunanak.network.RestClient
import eco.com.gurunanak.utlity.UtilityCommon
import kotlinx.android.synthetic.main.activity_forgot_p.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ActivityForgot : BaseActivity(), OkHttpListener {

    val JSON = MediaType.parse("application/json; charset=utf-8")
    override fun onOkHttpResponse(callResponse: String, pageId: Int, latLng: LatLng) {

    }

    internal lateinit var dialog_progress: ACProgressFlower


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_p)
        initial()


        btn_login1.setOnClickListener { view ->
            if (UtilityCommon.isNetworkAvailable(this@ActivityForgot) === true) {
                var confirTxt=input_password_c.text.toString()
                if (input_email1.text.equals("")) {
                    input_email1.error = "Please enter email"
                } else if (!UtilityCommon.isValidEmail(input_email1.text.toString())) {
                    input_email1.error = "Please enter valid email"
                } else if (input_password1.text.length<4) {
                    input_password1.error = "Please enter password"
                }
                else if(!input_password1.text.toString().equals(confirTxt)){
                    input_password_c.error = "Please erify your password"
                }



                else {
                    postLogin()
                }
            } else {
                UtilityCommon.showSnackBar(this@ActivityForgot, "No internet connection")
            }
        }

    }

    private fun postLogin() {
        dialog_progress.show()
        val params = HashMap<String, String>()
        params["email"] = input_email1.text.toString()
        params["updated_password"] = input_password1.text.toString()

        val call1 = RestClient.create().updatePassword(params)
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
                        BackBtn(input_password1)
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

    private fun initial() {
        dialog_progress = ACProgressFlower.Builder(this@ActivityForgot)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .bgColor(Color.TRANSPARENT)
                .bgAlpha(0f)
                .bgCornerRadius(0f)
                .fadeColor(Color.DKGRAY).build()
        dialog_progress.setCanceledOnTouchOutside(true)

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
                        val toast = Toast.makeText(this@ActivityForgot, msg, Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)

                        finish()

                    } else {

                        val toast = Toast.makeText(this@ActivityForgot, msg, Toast.LENGTH_LONG)
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
