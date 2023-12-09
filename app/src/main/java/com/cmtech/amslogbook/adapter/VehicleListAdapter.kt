package com.cmtech.amslogbook.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.HomeActivity
import com.cmtech.amslogbook.activity.Vehicle_detailActivity
import com.cmtech.amslogbook.databinding.VehicleListItemResourceBinding
import com.cmtech.amslogbook.global.PubVar
import com.cmtech.amslogbook.model.car_list

class VehicleListAdapter(val context:Context, var item:ArrayList<car_list>,var callbacks:onItemCallback):RecyclerView.Adapter<VehicleListAdapter.Viewholder>(){

    class Viewholder(view:View):RecyclerView.ViewHolder(view) {
        val binding=VehicleListItemResourceBinding.bind(view)
        // layout boarder
        val test = view.setBackgroundResource(R.drawable.list_selector)
    }

    interface onItemCallback{
        fun onItemdelet(id:String)
    }
    fun onitmesclickupdate(cars:ArrayList<car_list>){
        this.item = cars
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding=VehicleListItemResourceBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding.root)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.binding.carnameCarlist.text = item[position].car_name
        holder.binding.ownershipCarlist.text = item[position].ownership
        holder.binding.numberplatCarlist.text = item[position].numberplat
        holder.binding.makeCarlist.text = item[position].make
        holder.binding.modelCarlist.text = item[position].model

        holder.binding.linearlayoutCarlist.setOnClickListener {
            val intent = Intent(context, Vehicle_detailActivity::class.java)
            context.startActivity(intent)
            PubVar.numberplate = holder.binding.numberplatCarlist.text.toString()
        }

        holder.binding.vldelete.setOnClickListener {

            val builder= AlertDialog.Builder(context)
            builder.setTitle("Alert!")
            builder.setMessage("Are you sure you have to deleted that vehicle")
            builder.setCancelable(false)
            builder.setIcon(R.drawable.alertdialog)
            builder.setPositiveButton("Yes"){  DialogInterface, i ->
                callbacks.onItemdelet(item[position].numberplat)
                item.removeAt(position)
                notifyDataSetChanged()
            }
            builder.setNegativeButton("No"){DialogInterface, i ->
                DialogInterface.dismiss()
            }
            val alertdiloag= builder.create()
            alertdiloag.show()
        }
    }
}


