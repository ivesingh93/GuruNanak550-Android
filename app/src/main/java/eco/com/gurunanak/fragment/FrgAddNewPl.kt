package eco.com.gurunanak.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.android.gms.location.places.Place
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import eco.com.gurunanak.Activity_Home
import eco.com.gurunanak.R
import eco.com.gurunanak.network.RestClient
import eco.com.gurunanak.sharedprefrences.Prefs
import eco.com.gurunanak.sharedprefrences.SharedPreferencesName
import eco.com.gurunanak.utlity.UtilityCommon
import kotlinx.android.synthetic.main.frg_new_pl.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


@SuppressLint("ValidFragment")
class FrgAddNewPl(context: Context) : Fragment() {

    internal lateinit var dialog_progress: ACProgressFlower
    var txtLocation:TextView?=null
    var lat:Double?=0.0
    var lng :Double?=0.0
    var address:String?=""
    var urlsArr:JsonArray?=null


//    var url1:String=""
//    var url2Video:String=""



     var userChoosenTask: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frg_new_pl, container, false)
    }

    override fun onResume() {
        super.onResume()
        // Set title
        activity!!.title = "Add new Plantation"

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
        urlsArr= JsonArray()

    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
        //you can set the title for your toolbar here for different fragments different titles
        val act = activity as Activity_Home
        act.someMethodToLoadFragment(this!!)
        txtLocation = view.findViewById(R.id.txtLocation)
        txtLocation!!.setOnClickListener({

            //            val builder = PlacePicker.IntentBuilder()

            act.browseLoc()
        })


        val c = Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = df.format(c)
        txtDate.setText(formattedDate)

        txtDate.setOnClickListener({

            var c = Calendar.getInstance();
            var mYear = c.get(Calendar.YEAR);
            var mMonth = c.get(Calendar.MONTH);
            var mDay = c.get(Calendar.DAY_OF_MONTH);


            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                txtDate.setText(""+ year+ "-"+( monthOfYear +1)+"-"+dayOfMonth)
                txtDate.setError(null)
            }, mYear, mMonth, mDay)
            dpd.show()


        })

        imgVideo.setOnClickListener({
            val act = activity as Activity_Home
            act!!.browseVideo()
        })
        imgImage.setOnClickListener({ selectImage() })

        btnSave.setOnClickListener({
            if (address!!.length<1) {
                txtLocation!!.error = "Please select address"
            } else if (txtDate.text.toString().length<1) {
                txtDate.error = "Please select a  date"
            } else if (txtNoOfTree.text.toString().length<1) {
                txtNoOfTree.error = "Please enter no of plants"
            }

//            else if (txtTypePlant.text.equals("")) {
//                txtTypePlant.error = "Please enter type of plants"
//            }
//
            else if(urlsArr!!.size()<1){
                    txtImage.error = "Please select atleast an image"
                }
            else{
                savetreeData()

            }
        })
    }




    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            val result = UtilityCommon.checkPermission(activity!!)

            if (items[item] == "Take Photo") {
                userChoosenTask = "Take Photo"
                val act = activity as Activity_Home
                act!!.cameraIntent()
                // }
            } else if (items[item] == "Choose from Library") {
                userChoosenTask = "Choose from Library"
                val act = activity as Activity_Home
                act!!.galleryIntent()
                // }
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }
    fun savetreeData(){
        dialog_progress.show()
        var params =JsonObject()
        params.addProperty("email", Prefs.with(activity!!).getString(SharedPreferencesName.EMAIL,""))!!
        params.addProperty("location", address!!)
        params.addProperty("latitude", lat)
        params.addProperty("longitude", lng)
        params.addProperty("date", txtDate.text.toString())
        params.addProperty("planted_trees",txtNoOfTree.text.toString())
        params.addProperty("plants_types", txtTypePlant.text.toString())
        params.addProperty("remarks",txtRemark.text.toString())
        params.add("images_videos", urlsArr)
        params.addProperty("status", "Pending")


        val call1 = RestClient.create().plantationRecord("application/json","application/json",params)
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
                if (response.code() == 200) {

                    var ob= JSONObject(response.body().toString())
                    if(ob.getInt("ResponseCode")==200){
                        dialog_progress.dismiss()

                        Toast.makeText(activity!!,ob.getString("Message"),Toast.LENGTH_LONG).show()
                       urlsArr= JsonArray()
                        address=""
                        txtLocation!!.setText("")
                        txtDate.setText("")
                        txtNoOfTree.setText("")
                        txtTypePlant.setText("")
                        txtRemark.setText("")

                        var act=activity as Activity_Home
                        act.onBackPressed()
                    }
                    else{
                        Toast.makeText(activity!!,ob.getString("Message"),Toast.LENGTH_LONG).show()
                    }

                }

            }

        })
    }







    fun setPlaces(userLocation:Place){
        txtLocation!!.setText(userLocation.address)
        lat=userLocation.latLng.latitude
        lng=userLocation.latLng.longitude
        address=""+userLocation.address
        txtLocation!!.setError(null);
    }

    fun setUrl(urdl1:String,fileName:String){
        urlsArr!!.add(urdl1)
        txtImage.setError(null)
        txtImage.setText(txtImage.text.toString()+"\n"+fileName)

    }

    fun setUrl2(urdl2:String,fileName:String){
        urlsArr!!.add(urdl2)
        txtVideo.setError(null)
        txtVideo.setText(txtVideo.text.toString()+"\n"+fileName)
    }


}