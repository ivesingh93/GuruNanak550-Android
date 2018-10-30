package com.example.admin.recyclerviewkotlin

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.nostra13.universalimageloader.core.ImageLoader
import eco.com.gurunanak.R
import eco.com.gurunanak.adapter.data_faq
import eco.com.gurunanak.utlity.UIL_Image_options
import java.lang.Exception
import java.util.concurrent.ExecutionException


class AdapResDetail(val data_list: List<String>,val imgLoader:ImageLoader,
                    val context: Context ) : RecyclerView.Adapter<AdapResDetail.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

try {
//    holder.title1.setText(data_list[position])
    imgLoader.displayImage(data_list[position], holder.img,
            UIL_Image_options.options2)
    holder.itemView.setOnClickListener({

        val myMime = MimeTypeMap.getSingleton()
        val newIntent = Intent(Intent.ACTION_VIEW)
try {
    val mimeType = myMime.getMimeTypeFromExtension(fileExt(data_list[position])!!.substring(1))
    newIntent.setDataAndType(Uri.parse(data_list[position]), mimeType)
    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    try {
        context.startActivity(newIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show()
    }
}catch (e:Exception){e.printStackTrace()}


    })
}catch (E:Exception){E.printStackTrace()}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_res, parent, false)

        return ViewHolder(v);
    }


    override fun getItemCount(): Int {

        try{

    }catch (E:Exception){}
        return data_list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        val title1 = itemView.findViewById<TextView>(R.id.txtTitle)

        val img = itemView.findViewById<ImageView>(R.id.thumb)


    }

    private fun fileExt(url: String): String? {
        var url = url
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"))
        }
        if (url.lastIndexOf(".") == -1) {
            return null
        } else {
            var ext = url.substring(url.lastIndexOf(".") + 1)
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"))
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"))
            }
            return ext.toLowerCase()

        }
    }
}




