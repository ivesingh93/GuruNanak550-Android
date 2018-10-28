package eco.com.gurunanak.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tudle.utils.DataModel
import eco.com.gurunanak.R
import eco.com.gurunanak.network.RestClient
import eco.com.gurunanak.sharedprefrences.Prefs
import eco.com.gurunanak.sharedprefrences.SharedPreferencesName
import kotlinx.android.synthetic.main.frg_contact.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FrgContact : Fragment() {


    internal lateinit var dialog_progress: ACProgressFlower

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frg_contact, container, false)
    }

    override fun onResume() {
        super.onResume()
        // Set title
        activity!!.title = "Contact"

    }
    private fun initial() {
        dialog_progress = ACProgressFlower.Builder(activity)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .bgColor(Color.TRANSPARENT)
                .bgAlpha(0f)
                .bgCornerRadius(0f)
                .fadeColor(Color.DKGRAY).build()
        dialog_progress.setCanceledOnTouchOutside(true)


    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
        //you can set the title for your toolbar here for different fragments different titles
        activity!!.title = "Contact"

        btnSubmitQuery.setOnClickListener({

            if(etQuery.text.toString().length<4){
                etQuery.error = "Please enter your query"
            }else{
                dialog_progress.show()
                val params = HashMap<String, String>()
                params["query"] = etQuery.text.toString()
                params["query_by"] = Prefs.with(activity!!).getString(SharedPreferencesName.EMAIL,"")
                params["status"] = ""+false
                params["token"] = Prefs.with(activity!!).getString(SharedPreferencesName.JWT_TOKEN,"")



                val call1 = RestClient.create().putQuery(Prefs.with(activity!!).getString(SharedPreferencesName.JWT_TOKEN,""),params)
                call1.enqueue(object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("error", "error" + t.toString())

                        dialog_progress.dismiss()
                    }

                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        DataModel.loading_box_stop()
                        dialog_progress.dismiss()
                        var gson = Gson()
                        Log.e("data ", "body" + response.code())
                        Log.e("data ", "body" + response.body().toString())
                        var ob= JSONObject(response.body().toString())
                        if (response.code() == 200) {
                            if(ob.getInt("ResponseCode")==200){
                                etQuery.setText("")
                                dialog_progress.dismiss()
                                Toast.makeText(activity!!,ob.getString("Message"), Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(activity!!,ob.getString("Message"), Toast.LENGTH_LONG).show()
                            }


                        }

                    }

                })


            }
        })
    }

}