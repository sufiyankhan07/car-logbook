package com.cmtech.amslogbook.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmtech.amslogbook.databinding.ExpHistoryBinding
import com.cmtech.amslogbook.model.Fuel_list
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class reportTripAdapter(val context:Context, var items:ArrayList<Fuel_list>, val onclick:onitemclick): RecyclerView.Adapter<reportTripAdapter.viewholder>() {

    interface onitemclick{
        fun onclick(id:String)
    }

    class viewholder(view: View):RecyclerView.ViewHolder(view){
        val binding = ExpHistoryBinding.bind(view)
    }

    fun updateList(trip:ArrayList<Fuel_list>){
        this.items = trip
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding= ExpHistoryBinding.inflate(LayoutInflater.from(context),parent,false)
        return viewholder(binding.root)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.binding.idTripResource.text=items[position].ID
        holder.binding.tripExpenses.text=items[position].PARKING
        holder.binding.location.text=items[position].LOCATION
        holder.binding.odometer.text=items[position].ODOMETER
        holder.binding.totalAmount.text=items[position].AMOUNT

//        val format= SimpleDateFormat("dd MMM")
//        val Date= format.parse( holder.binding.startDate.toString().trim())
//        holder.binding.startDate.text=SimpleDateFormat("dd MM", Locale.ENGLISH).format(Date?.time)

        holder.binding.reportTrip.setOnClickListener {
            onclick.onclick(items[position].ID!!)
        }



    }
}