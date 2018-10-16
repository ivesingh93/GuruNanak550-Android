package com.example.admin.recyclerviewkotlin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import eco.com.gurunanak.R
import eco.com.gurunanak.adapter.data_faq


class AdapFaqs(val data_list: List<data_faq>,
               val context: Context ) : RecyclerView.Adapter<AdapFaqs.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

       Log.e("daat",""+data_list.size)
        holder.title1.setText(data_list[position].question!!)
        holder.title2.setText(data_list[position].answer!!)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_faq, parent, false)

        return ViewHolder(v);
    }


    override fun getItemCount(): Int {
        return data_list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title1 = itemView.findViewById<TextView>(R.id.txtQues)

        val title2 = itemView.findViewById<TextView>(R.id.txtAnswer)


    }
    class TextWatch: TextWatcher{
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    }

}




