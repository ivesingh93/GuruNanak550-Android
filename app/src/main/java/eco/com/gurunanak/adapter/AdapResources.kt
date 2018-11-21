package com.example.admin.recyclerviewkotlin

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import eco.com.gurunanak.R
import eco.com.gurunanak.adapter.data_faq
import eco.com.gurunanak.fragment.imageLoader
import eco.com.gurunanak.model.data_resources
import eco.com.gurunanak.utlity.UIL_Image_options
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.support.v4.content.ContextCompat.startActivity
import android.webkit.MimeTypeMap






class AdapResources(val data_list: List<data_resources>,
                    val context: Context,val imageLoader:ImageLoader,val Str:String ) : RecyclerView.Adapter<AdapResources.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.title1.setText(data_list[position].title!!)
        holder.title2.setText(data_list[position].description!!)
        imageLoader.displayImage(data_list[position].thumbnail_url, holder.thumb,
                UIL_Image_options.options)

        holder.itemView.setOnClickListener({

            if(Str.equals("Videos")){
              try {
                  val videoClient = Intent(Intent.ACTION_VIEW)
                  videoClient.setData(Uri.parse(data_list[position].url))
//                videoClient.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity")
                  context.startActivity(videoClient)
              }catch (E:Exception){}
            }
            if(Str.equals("Documents")){

                val myMime = MimeTypeMap.getSingleton()
                val newIntent = Intent(Intent.ACTION_VIEW)
                val mimeType = myMime.getMimeTypeFromExtension(fileExt(data_list[position].url!!)!!.substring(1))
                newIntent.setDataAndType(Uri.parse(data_list[position].url!!), mimeType)
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                try {
                    context.startActivity(newIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show()
                }


            }

            if(Str.equals("Posters")){

                val myMime = MimeTypeMap.getSingleton()
                val newIntent = Intent(Intent.ACTION_VIEW)
                val mimeType = myMime.getMimeTypeFromExtension(fileExt(data_list[position].url!!)!!.substring(1))
                newIntent.setDataAndType(Uri.parse(data_list[position].url!!), mimeType)
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                try {
                    context.startActivity(newIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show()
                }

            }

        })



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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_resources, parent, false)

        return ViewHolder(v);
    }


    override fun getItemCount(): Int {
        return data_list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title1 = itemView.findViewById<TextView>(R.id.txtTitle)

        val title2 = itemView.findViewById<TextView>(R.id.txtDesc)
       // val title3 = itemView.findViewById<TextView>(R.id.txtDetail)
        val thumb = itemView.findViewById<ImageView>(R.id.thumb)


    }

}




