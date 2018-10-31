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
import eco.com.gurunanak.ActivityWebPages
import eco.com.gurunanak.model.data_news
import java.text.SimpleDateFormat


class AdapNews(val data_list: List<data_news>,
               val context: Context, val imageLoader:ImageLoader ) : RecyclerView.Adapter<AdapNews.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.title1.setText(data_list[position].title!!)
        holder.title2.setText(data_list[position].brief_description!!)
        var date = data_list[position].date_posted
        var spf = SimpleDateFormat("yyyy-MM-dd")
        val newDate = spf.parse(date)
        spf = SimpleDateFormat("dd-MMM-yyyy")
        date = spf.format(newDate)
        println(date)
        holder.title3.setText(""+date)
        imageLoader.displayImage(data_list[position].featured_img_url, holder.thumb,
                UIL_Image_options.options)

        holder.itemView.setOnClickListener({



            val newIntent = Intent(context, ActivityWebPages::class.java)
           // val mimeType = myMime.getMimeTypeFromExtension(fileExt(data_list[position].url!!)!!.substring(1))
           // newIntent.setDataAndType(Uri.parse(data_list[position].url!!), mimeType)
            newIntent.putExtra("url",data_list[position].url)
            try {
                context.startActivity(newIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show()
            }


        })
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_news, parent, false)

        return ViewHolder(v);
    }


    override fun getItemCount(): Int {
        return data_list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title1 = itemView.findViewById<TextView>(R.id.txtTitle)

        val title2 = itemView.findViewById<TextView>(R.id.txtDesc)
        val title3 = itemView.findViewById<TextView>(R.id.txtDates)
        val thumb = itemView.findViewById<ImageView>(R.id.thumb)


    }

}




