package com.cmtech.amslogbook.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmtech.amslogbook.databinding.FuelListItemResourceBinding
import com.cmtech.amslogbook.model.Fuel_list
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FuelListAdapter(val context:Context,var item:ArrayList<Fuel_list>):RecyclerView.Adapter<FuelListAdapter.Viewholder2>() {

    class Viewholder2(view: View):RecyclerView.ViewHolder(view) {
        val binding=FuelListItemResourceBinding.bind(view)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setonChange(tirp:ArrayList<Fuel_list>){
        this.item=tirp
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder2 {
        val binding=FuelListItemResourceBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder2(binding.root)
    }

    override fun getItemCount(): Int {
      return item.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: Viewholder2, position: Int) {
        holder.binding.idFuelResource.text = item[position].ID
        holder.binding.vehicleNameFuelResource.text= item[position].VEHICLE_NAME
        holder.binding.registrationNoFuelResource.text= item[position].REGISTRATION_NO
        holder.binding.dateFuelResource.text= item[position].FUEL_FILLING_DATE
        holder.binding.amountFuelResource.text= item[position].AMOUNT
        holder.binding.fuelTypeFuelResource.text= item[position].TYPE_OF_EXPENSE
        holder.binding.locationFuelResource.text= item[position].LOCATION
        holder.binding.noteFuelListResource.text= item[position].NOTE

        // set the date format
        val format=SimpleDateFormat("dd/MM/yyyy")
        val date = format.parse(holder.binding.dateFuelResource.text.toString().trim())
        holder.binding.dayofmonthFuelResource.text = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date?.time)
    }
}

