package com.cmtech.amslogbook.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.AddOtherExpense
import com.cmtech.amslogbook.databinding.TripListItemResourceBinding
import com.cmtech.amslogbook.model.Trip_list
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TripListAdapter(val context:Context,var item:ArrayList<Trip_list>,val callback:onItemClickCallBack):RecyclerView.Adapter<TripListAdapter.Viewholder1>() {

    class Viewholder1(view: View):RecyclerView.ViewHolder(view) {
        val binding=TripListItemResourceBinding.bind(view)
        // layout boarder
        val test = view.setBackgroundResource(R.drawable.list_selector)
    }
    interface onItemClickCallBack{
        fun onItemClick(id:String){

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder1 {
        val binding=TripListItemResourceBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder1(binding.root)
    }

    override fun getItemCount(): Int {
      return item.size
    }
    fun updateList(trip:ArrayList<Trip_list>){
        this.item=trip
        notifyDataSetChanged()
    }


    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: Viewholder1, position: Int) {
        holder.binding.idTripResource.text = item[position].id
        holder.binding.startdateTripResource.text = item[position].start_date
        holder.binding.distanceTripResource.text = item[position].distance
        holder.binding.tripTypeTripResource.text = item[position].trip_type
        holder.binding.startAddressTripResource.text = item[position].start_address
        holder.binding.endAddressTripResource.text = item[position].end_address
        holder.binding.vehicleNameTripResource.text = item[position].vehicle
        holder.binding.registrationNoTripResource.text = item[position].registration_no

        // set the date formate
        val c = Calendar.getInstance()
        val format = SimpleDateFormat("dd/MM/yyyy")
        val date = format.parse(holder.binding.startdateTripResource.text.toString().trim())
        holder.binding.dayofmonthTripResource.text = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date?.time)

        holder.binding.tripLayoutid.setOnClickListener {
            callback.onItemClick(item[position].id)
        }

//        holder.trip_layoutid.setOnClickListener{
//            val intent = Intent(context, AddOtherExpense::class.java)
//            intent.putExtra("ID", item[position].id)
//            context.startActivity(intent)
//        }

    }

}


