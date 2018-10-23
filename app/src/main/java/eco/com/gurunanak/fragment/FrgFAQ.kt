package eco.com.gurunanak.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.recyclerviewkotlin.AdapFaqs
import com.google.gson.Gson
import com.tudle.utils.DataModel
import eco.com.gurunanak.R
import eco.com.gurunanak.adapter.data_faq
import eco.com.gurunanak.network.RestClient
import kotlinx.android.synthetic.main.frg_faq.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FrgFAQ : Fragment() {

    private var param1: String? = null
    private var param2: String? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frg_faq, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity!!.title = "FAQs"
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles


        val call1 = RestClient.create().getQuery()
        call1.enqueue(object : Callback<List<data_faq>> {
            override fun onFailure(call: Call<List<data_faq>>, t: Throwable) {
                Log.e("error", "error" + t.toString())


            }

            override fun onResponse(call: Call<List<data_faq>>, response: Response<List<data_faq>>) {
                DataModel.loading_box_stop()

                var gson = Gson()
                Log.e("data ", "body" + response.code())
                Log.e("data ", "body" + response.body().toString())
                if (response.code() == 200) {
//                    if(ob.getInt("ResponseCode")==200){

                        val llm = LinearLayoutManager(activity!!)
                        llm.orientation = LinearLayoutManager.VERTICAL
                        list!!.setLayoutManager(llm)
                       var dataApps=response!!.body()
                        var adapList = AdapFaqs((dataApps as List<data_faq>?)!!,
                                activity!!)
                        list!!.adapter = adapList
//                    }else{
//                        Toast.makeText(activity!!,ob.getString("Message"), Toast.LENGTH_LONG).show()
//                    }


                }

            }

        })


    }

}