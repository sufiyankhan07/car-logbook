package com.cmtech.amslogbook.fragment

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.adapter.VehicleListAdapter
import com.cmtech.amslogbook.databinding.FragmentVehicleList2Binding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import com.cmtech.amslogbook.model.car_list
import java.lang.Exception

class VehicleListFragment : Fragment(),VehicleListAdapter.onItemCallback  {
    lateinit var binding:FragmentVehicleList2Binding
    lateinit var Adapter : VehicleListAdapter
    val arrayList: ArrayList<car_list> = ArrayList()
    var db: DB?=null

    var id= PubVar.numberplate
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentVehicleList2Binding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db=activity?.let { DB(it) }
        populate_list()
    }
    @SuppressLint("UseRequireInsteadOfGet", "NotifyDataSetChanged")
    fun populate_list(){
        arrayList.clear()
        var cursor: Cursor?=null
        try{
            val sqlqury = "SELECT VEHICLE_NAME,OWNERSHIP,NUMBER_PLAT,MODEL_NAME,MODEL_NO FROM VEHICLE"
            cursor = db!!.FireQuery(sqlqury)
            if(cursor?.count!! > 0){
                cursor.moveToFirst()
                do{
                    arrayList.add(car_list(
                        PubFun.isCursorNULL(cursor,"VEHICLE_NAME","").toString(),
                        PubFun.isCursorNULL(cursor,"OWNERSHIP","").toString(),
                        PubFun.isCursorNULL(cursor,"NUMBER_PLAT","").toString(),
                        PubFun.isCursorNULL(cursor,"MODEL_NAME","").toString(),
                        PubFun.isCursorNULL(cursor,"MODEL_NO","").toString()))
                }while (cursor.moveToNext())

                binding.recyclerviewVehiclelist.visibility=View.VISIBLE
                binding.nodatafound.visibility = View.GONE
            }
            else{
                binding.nodatafound.visibility = View.VISIBLE
                binding.recyclerviewVehiclelist.visibility = View.GONE
            }
            Adapter=VehicleListAdapter(activity!!,arrayList,this)
            binding.recyclerviewVehiclelist.layoutManager = LinearLayoutManager(activity)
            val horizontalLayoutManagaer = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.recyclerviewVehiclelist.layoutManager = horizontalLayoutManagaer
            binding.recyclerviewVehiclelist.adapter = Adapter
            Adapter.onitmesclickupdate(arrayList)
            Adapter.notifyDataSetChanged()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            cursor?.close()
        }
    }

    override fun onItemdelet(id: String) {
        PubVar.numberplate=id
        try {
            val sqlquery="DELETE FROM VEHICLE WHERE NUMBER_PLAT='$id' "
            db?.ExecuteQuery(sqlquery)
        }catch(e:Exception){
            e.printStackTrace()
        }
    }

}