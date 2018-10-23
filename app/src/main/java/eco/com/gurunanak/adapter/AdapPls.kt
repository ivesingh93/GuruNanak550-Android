package com.example.admin.recyclerviewkotlin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import eco.com.gurunanak.R
import eco.com.gurunanak.model.data_pls
import java.text.SimpleDateFormat


class AdapPls(val data_list: List<data_pls>,
              val context: Context ) : RecyclerView.Adapter<AdapPls.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var pos = holder.adapterPosition
        holder.title1.setText(data_list[position].location!!)
        holder.title2.setText(""+data_list[position].total_trees_planted!!)


        var date = data_list[position].date_planted
        var spf = SimpleDateFormat("yyyy-MM-dd")
        val newDate = spf.parse(date)
        spf = SimpleDateFormat("dd-MMM-yyyy")
        date = spf.format(newDate)
        println(date)
        holder.title3.setText(""+date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_pl_req, parent, false)

        return ViewHolder(v);
    }


    override fun getItemCount(): Int {
        return data_list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title1 = itemView.findViewById<TextView>(R.id.txtAddress)
        val title2 = itemView.findViewById<TextView>(R.id.txtNoOfPlants)
        val title3 = itemView.findViewById<TextView>(R.id.txtDates)


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




