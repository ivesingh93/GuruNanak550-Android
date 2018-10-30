package eco.com.gurunanak

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.okhttp.MediaType
import com.tudle.utils.BaseActivity
import eco.com.gurunanak.http.OkHttpListener
import eco.com.gurunanak.model.data_resources
import eco.com.gurunanak.network.RestClient
import eco.com.gurunanak.sharedprefrences.Prefs
import eco.com.gurunanak.sharedprefrences.SharedPreferencesName
import eco.com.gurunanak.utlity.UtilityCommon
import kotlinx.android.synthetic.main.activity_forgot_p.*
import kotlinx.android.synthetic.main.activity_plantation_detail.*
import kotlinx.android.synthetic.main.frg_new_pl.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import android.widget.ArrayAdapter
import com.example.admin.recyclerviewkotlin.AdapPls
import com.example.admin.recyclerviewkotlin.AdapResDetail
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import java.text.SimpleDateFormat


class ActivityPlantationDetail : BaseActivity() {

    var imageLoader: ImageLoader? = null


    internal lateinit var dialog_progress: ACProgressFlower
   var plId="";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantation_detail)

        initial()
        plId=intent.getStringExtra("id")
        getPlDetail()



    }





    private fun initial() {
        imageLoader = ImageLoader.getInstance();
        imageLoader!!.init(ImageLoaderConfiguration.createDefault(this))
        dialog_progress = ACProgressFlower.Builder(this@ActivityPlantationDetail)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .bgColor(Color.TRANSPARENT)
                .bgAlpha(0f)
                .bgCornerRadius(0f)
                .fadeColor(Color.DKGRAY).build()
        dialog_progress.setCanceledOnTouchOutside(true)

    }

    fun getPlDetail(){
        val call1 = RestClient.create().getPlantationDetail(Prefs.with(this!!).getString(SharedPreferencesName.JWT_TOKEN,""),
                "plantationRecord/" +plId)
        dialog_progress.show()

        call1.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("error", "error" + t.toString())
                dialog_progress.dismiss()

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                dialog_progress.dismiss()

                Log.e("error", "error" + response.body().toString())

                if (response.code() == 200) {
                    var arra = JSONObject(response.body().toString())
                    var arra2 = JSONArray(arra.getString("urls"))

                    var obData = JSONObject(arra.getString("plantation_record"))

                    try {
                        txtUName.setText("Name - " + obData.getString("full_name"))
                        txtUEmail.setText("Email - " + obData.getString("email"))
                        txtOrgName.setText("Organization -  " + obData.getString("organization_name"))
                        txtName.setText("Remark - " + obData.getString("remarks"))
                        txtEmail.setText("Location - " + obData.getString("location"))

                        var date =obData.getString("date")
                        var spf = SimpleDateFormat("yyyy-MM-dd")
                        val newDate = spf.parse(date)
                        spf = SimpleDateFormat("dd-MMM-yyyy")
                        date = spf.format(newDate)
                        println(date)


                        txtCompany.setText("Date Planted - " + date)
                        txtAddress.setText("Planted Trees - " + obData.getString("planted_trees"))
                        txtPlantedTypes.setText("Plants Types - " + obData.getString("plants_types"))
                        txtStatus.setText("Status - " + obData.getString("status"))


                        val llm = GridLayoutManager(this@ActivityPlantationDetail,3)
                        llm.orientation = LinearLayoutManager.VERTICAL
                        lv!!.setLayoutManager(llm)
                        lv.setNestedScrollingEnabled(false);


                        var listdata = ArrayList<String>();

                        if (arra2 != null) {
                            for (i in 0 until arra2.length()) {
                                listdata.add(arra2.getString(i))
                            }
                        }

                        var adapList = AdapResDetail(listdata!!,
                                imageLoader!!,this@ActivityPlantationDetail)
                        lv!!.adapter = adapList

                    } catch (E: Exception) {
                        E.printStackTrace()
                    }


                }


            }



        })
    }

}