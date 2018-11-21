package eco.com.gurunanak.fragment

import android.content.Context
import android.content.SharedPreferences
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
import com.example.admin.recyclerviewkotlin.AdapPls
import com.google.gson.Gson
import com.tudle.utils.DataModel
import eco.com.gurunanak.R
import eco.com.gurunanak.model.data_pls
import eco.com.gurunanak.network.RestClient
import eco.com.gurunanak.sharedprefrences.Prefs
import eco.com.gurunanak.sharedprefrences.SharedPreferencesName
import kotlinx.android.synthetic.main.fra_my_plantation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentPlantationReq : Fragment() {

    var isPendingLoaded:Boolean?=false
    var isApprovedLoaded:Boolean?=false
    var isDeniedLoaded:Boolean?=false

    internal lateinit var dialog_progress: ACProgressFlower
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fra_my_plantation, container, false)
    }

    override fun onResume() {
        super.onResume()
        // Set title
        activity!!.title = "My Plantations"
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initial()
        getPendingReqs()
        txtPending.setTextColor(activity!!.resources.getColor(R.color.colorAccent))
        txtDenied.setTextColor(activity!!.resources.getColor(R.color.black))
        txtApproved.setTextColor(activity!!.resources.getColor(R.color.black))

        txtApproved.setOnClickListener({
            list2.visibility=View.VISIBLE
            list3.visibility=View.GONE
            list_1.visibility=View.GONE

            txtApproved.setTextColor(activity!!.resources.getColor(R.color.colorAccent))
            txtDenied.setTextColor(activity!!.resources.getColor(R.color.black))
            txtPending.setTextColor(activity!!.resources.getColor(R.color.black))

            if(!isApprovedLoaded!!){
                getApproved()
            }
        })


    txtPending.setOnClickListener({
        list2.visibility=View.GONE
        list3.visibility=View.GONE
        list_1.visibility=View.VISIBLE
        txtPending.setTextColor(activity!!.resources.getColor(R.color.colorAccent))
        txtDenied.setTextColor(activity!!.resources.getColor(R.color.black))
        txtApproved.setTextColor(activity!!.resources.getColor(R.color.black))
        if(!isApprovedLoaded!!){
            getPendingReqs()
        }
    })


        txtDenied.setOnClickListener({
            list2.visibility=View.GONE
            list3.visibility=View.VISIBLE
            list_1.visibility=View.GONE
            txtDenied.setTextColor(activity!!.resources.getColor(R.color.colorAccent))
            txtPending.setTextColor(activity!!.resources.getColor(R.color.black))
            txtApproved.setTextColor(activity!!.resources.getColor(R.color.black))
            if(!isApprovedLoaded!!){
                getDenied()
            }
        })
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

    fun getPendingReqs(){


        val call1 = RestClient.create().deniedPlant("plantationRecord/email=" +
                Prefs.with(activity!!).getString(SharedPreferencesName.EMAIL,"")+
                "&status=Pending" )
        dialog_progress.show()

        call1.enqueue(object : Callback<List<data_pls>> {
            override fun onFailure(call: Call<List<data_pls>>, t: Throwable) {
                Log.e("error", "error" + t.toString())
                 dialog_progress.dismiss()

            }

            override fun onResponse(call: Call<List<data_pls>>, response: Response<List<data_pls>>) {
                dialog_progress.dismiss()


                if (response.code() == 200) {
                  //  if(ob.getInt("ResponseCode")==200){

                        val llm = LinearLayoutManager(activity!!)
                        llm.orientation = LinearLayoutManager.VERTICAL
                    list_1!!.setLayoutManager(llm)
                        var dataApps=response!!.body()
                        var adapList = AdapPls((dataApps as List<data_pls>?)!!,
                                activity!!)
                    list_1!!.adapter = adapList
                        isPendingLoaded=true
//                    }else{
//                        Toast.makeText(activity!!,ob.getString("Message"), Toast.LENGTH_LONG).show()
//                    }


                }

            }

        })
    }


    fun getApproved(){

        val call1 = RestClient.create().deniedPlant("plantationRecord/email=" +
                Prefs.with(activity!!).getString(SharedPreferencesName.EMAIL,"")!!+
                "&status=Approved" )
        dialog_progress.show()

        call1.enqueue(object : Callback<List<data_pls>> {
            override fun onFailure(call: Call<List<data_pls>>, t: Throwable) {
                Log.e("error", "error" + t.toString())
                dialog_progress.dismiss()

            }

            override fun onResponse(call: Call<List<data_pls>>, response: Response<List<data_pls>>) {
                dialog_progress.dismiss()

                var gson = Gson()


                if (response.code() == 200) {
                    //  if(ob.getInt("ResponseCode")==200){

                    val llm = LinearLayoutManager(activity!!)
                    llm.orientation = LinearLayoutManager.VERTICAL
                    list2!!.setLayoutManager(llm)
                    var dataApps=response!!.body()
                    var adapList = AdapPls((dataApps as List<data_pls>?)!!,
                            activity!!)
                    list2!!.adapter = adapList
                    isApprovedLoaded=true
//                    }else{
//                        Toast.makeText(activity!!,ob.getString("Message"), Toast.LENGTH_LONG).show()
//                    }


                }

            }

        })
    }

    fun getDenied(){

        val call1 = RestClient.create().deniedPlant("plantationRecord/email=" +
                Prefs.with(activity!!).getString(SharedPreferencesName.EMAIL,"")!!+
                "&status=Denied" )
        dialog_progress.show()

        call1.enqueue(object : Callback<List<data_pls>> {
            override fun onFailure(call: Call<List<data_pls>>, t: Throwable) {
                Log.e("error", "error" + t.toString())
                dialog_progress.dismiss()

            }

            override fun onResponse(call: Call<List<data_pls>>, response: Response<List<data_pls>>) {
                DataModel.loading_box_stop()

                var gson = Gson()
                dialog_progress.dismiss()

                if (response.code() == 200) {
                    //  if(ob.getInt("ResponseCode")==200){

                    val llm = LinearLayoutManager(activity!!)
                    llm.orientation = LinearLayoutManager.VERTICAL
                    list3!!.setLayoutManager(llm)
                    var dataApps=response!!.body()
                    var adapList = AdapPls((dataApps as List<data_pls>?)!!,
                            activity!!)
                    list3!!.adapter = adapList
                    isDeniedLoaded=true
//                    }else{
//                        Toast.makeText(activity!!,ob.getString("Message"), Toast.LENGTH_LONG).show()
//                    }


                }

            }

        })
    }

}