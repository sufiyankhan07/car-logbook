package com.cmtech.amslogbook.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.AddOtherExpense
import com.cmtech.amslogbook.databinding.OtherExpencesListItemBinding
import com.cmtech.amslogbook.model.Fuel_list
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OtherListAdapter(val context: Context,var item:ArrayList<Fuel_list>, val callback:onItemClickCallBack): RecyclerView.Adapter<OtherListAdapter.Viewholder3>() {

    class Viewholder3(view: View): RecyclerView.ViewHolder(view){
        val binding=OtherExpencesListItemBinding.bind(view)
        // layout boarder
        val test=view.setBackgroundResource(R.drawable.list_selector)
    }

    interface onItemClickCallBack{
    fun onItemClick1(id:String){}
    }
    fun onSetItemClick(fuel:ArrayList<Fuel_list>){
        this.item = fuel
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder3 {
        val binding=OtherExpencesListItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder3(binding.root)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: Viewholder3, position: Int) {
        holder.binding.otherResource.text= item[position].ID
        holder.binding.careNameOtherResource.text= item[position].VEHICLE_NAME
        holder.binding.careNoOtherResource.text= item[position].REGISTRATION_NO
        holder.binding.dateOtherResource.text= item[position].FUEL_FILLING_DATE
        holder.binding.amountOtherResource.text= "Total: "+item[position].AMOUNT
        holder.binding.otherTypeOtherResourc.text= item[position].TYPE_OF_EXPENSE
        holder.binding.noteOtherListResource.text= item[position].NOTE
        holder.binding.parkingOtherResource.text= "Parking: "+item[position].PARKING
        holder.binding.oilOtherResource.text="Oil: "+item[position].OIL_AMOUNT
        holder.binding.carwashOtherResource.text="Car_Wash: "+item[position].CAR_WASH_AMOUNT
        holder.binding.repandmainOtherResource.text="Repair And Maintenance: "+item[position].REPAIR_AMOUNT
        holder.binding.tiresOtherResource.text="Tires: "+item[position].TIRES_AMOUNT
        holder.binding.tollOtherResource.text="Toll: "+item[position].TOLL_AMOUNT

        holder.binding.lineatlayoutOtherlist.setOnClickListener{
            val intent = Intent(context,AddOtherExpense::class.java)
            intent.putExtra("ID", item[position].ID)
            context.startActivity(intent)
        }
        holder.binding.btnDelet.setOnClickListener {
            val builder= AlertDialog.Builder(context)
            builder.setTitle("Alert!")
            builder.setMessage("Are you sure you have to deleted this Expenses")
            builder.setCancelable(false)
            builder.setIcon(R.drawable.alertdialog)
            builder.setPositiveButton("Yes"){ DialogInterface, i ->
                callback.onItemClick1(item[position].ID.toString())
                item.removeAt(position)
                notifyDataSetChanged()
            }
            builder.setNegativeButton("No"){ DialogInterface, i ->
                DialogInterface.dismiss()
            }
            val alertdiloag= builder.create()
            alertdiloag.show()
        }
        // set the date format
        val format = SimpleDateFormat("dd/MM/yyyy")
        val date = format.parse(holder.binding.dateOtherResource.text.toString().trim())
        holder.binding.dayofmonthOtherResource.text = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date?.time)
    }

}

