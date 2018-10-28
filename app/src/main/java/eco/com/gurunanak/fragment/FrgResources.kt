package eco.com.gurunanak.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.gson.JsonObject
import eco.com.gurunanak.R
import eco.com.gurunanak.network.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.frg_resources.*
import java.lang.Exception
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Toast
import com.example.admin.recyclerviewkotlin.AdapResources
import com.google.gson.JsonArray
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import eco.com.gurunanak.http.OkHttpGetHandler
import eco.com.gurunanak.model.data_pls
import eco.com.gurunanak.model.data_resources
import eco.com.gurunanak.sharedprefrences.Prefs
import eco.com.gurunanak.sharedprefrences.SharedPreferencesName
import eco.com.gurunanak.utlity.Constant


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

internal lateinit var dialog_progress: ACProgressFlower
var imageLoader: ImageLoader? = null
class FrgResources : Fragment() {





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frg_resources, container, false)
    }


    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
        initial()
        getResource()
    }

    override fun onResume() {
        super.onResume()


        activity!!.title = "Resources"
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


        imageLoader = ImageLoader.getInstance();
        imageLoader!!.init(ImageLoaderConfiguration.createDefault(activity))

    }


     fun getResource(){


        val call1 = RestClient.create().getResourceCAt()
        dialog_progress.show()

        call1.enqueue(object : Callback<List<String>> {
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("error", "error" + t.toString())
                dialog_progress.dismiss()

            }

            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                dialog_progress.dismiss()

                Log.e("error", "error" + response.body().toString())
                if (response.code() == 200) {
                    //  if(ob.getInt("ResponseCode")==200){

                    try {
                        val dataAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, response.body())
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spin1.setAdapter(dataAdapter)

                        spin1?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                getDetailList(response.body()!!.get(position))

                            }

                        }
                    }catch (E:Exception){}



                }

            }

        })
    }

    fun getDetailList(str:String){

        val params = HashMap<String, String>()

        params["query_by"] = Prefs.with(activity!!).getString(SharedPreferencesName.EMAIL,"")
        params["token"] = Prefs.with(activity!!).getString(SharedPreferencesName.JWT_TOKEN,"")


        val call1 = RestClient.create().getResourceDEtail(Prefs.with(activity!!).getString(SharedPreferencesName.JWT_TOKEN,""),
                "resource/" +str)
        dialog_progress.show()

        call1.enqueue(object : Callback<List<data_resources>> {
            override fun onFailure(call: Call<List<data_resources>>, t: Throwable) {
                Log.e("error", "error" + t.toString())
                dialog_progress.dismiss()

            }

            override fun onResponse(call: Call<List<data_resources>>, response: Response<List<data_resources>>) {
                dialog_progress.dismiss()

                Log.e("error", "error" + response.body().toString())
                if (response.code() == 200) {
                    //  if(ob.getInt("ResponseCode")==200){

                    val llm = LinearLayoutManager(activity!!)
                    llm.orientation = LinearLayoutManager.VERTICAL
                    list_1!!.setLayoutManager(llm)
                    var dataApps=response!!.body()
                    var adapList = AdapResources((dataApps as List<data_resources>?)!!,
                            activity!!, imageLoader!!,str)
                    list_1!!.adapter = adapList

                    }else{
                       // Toast.makeText(activity!!,ob.getString("Message"), Toast.LENGTH_LONG).show()
                    }


                }



        })

    }



}

