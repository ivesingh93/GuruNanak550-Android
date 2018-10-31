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
import com.example.admin.recyclerviewkotlin.AdapNews
import com.example.admin.recyclerviewkotlin.AdapResources
import com.google.gson.JsonArray
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import eco.com.gurunanak.http.OkHttpGetHandler
import eco.com.gurunanak.model.data_news
import eco.com.gurunanak.model.data_pls
import eco.com.gurunanak.model.data_resources
import eco.com.gurunanak.sharedprefrences.Prefs
import eco.com.gurunanak.sharedprefrences.SharedPreferencesName
import eco.com.gurunanak.utlity.Constant



private  lateinit var dialog_progress2: ACProgressFlower
private var imageLoader2: ImageLoader? = null
class FrgNews : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frg_resources, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
        initial()
        linTop.visibility=View.GONE
        getResource()
    }

    override fun onResume() {
        super.onResume()


        activity!!.title = "News"
    }

    private fun initial() {
        dialog_progress2 = ACProgressFlower.Builder(activity)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .bgColor(Color.TRANSPARENT)
                .bgAlpha(0f)
                .bgCornerRadius(0f)
                .fadeColor(Color.DKGRAY).build()
        dialog_progress2.setCanceledOnTouchOutside(true)


        imageLoader2 = ImageLoader.getInstance();
        imageLoader2!!.init(ImageLoaderConfiguration.createDefault(activity))

    }


    fun getResource() {


        val call1 = RestClient.create().getNews()
        dialog_progress2.show()

        call1.enqueue(object : Callback<List<data_news>> {
            override fun onFailure(call: Call<List<data_news>>, t: Throwable) {
                Log.e("error", "error" + t.toString())
                dialog_progress2.dismiss()
            }

            override fun onResponse(call: Call<List<data_news>>, response: Response<List<data_news>>) {
                dialog_progress2.dismiss()

                Log.e("error", "error" + response.body().toString())
                if (response.code() == 200) {
                    //  if(ob.getInt("ResponseCode")==200){

                    try {
                        val llm = LinearLayoutManager(activity!!)
                        llm.orientation = LinearLayoutManager.VERTICAL
                        list_1!!.setLayoutManager(llm)
                        var dataApps = response!!.body()
                        var adapList = AdapNews((dataApps as List<data_news>?)!!,
                                activity!!, imageLoader2!!)
                        list_1!!.adapter = adapList
                    } catch (E: Exception) {
                    }
                } else {
                    // Toast.makeText(activity!!,ob.getString("Message"), Toast.LENGTH_LONG).show()
                }


            }
        })
    }
}