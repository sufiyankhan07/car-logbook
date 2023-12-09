package com.cmtech.amslogbook.fragment

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.adapter.FuelListAdapter
import com.cmtech.amslogbook.databinding.ExpHistoryBinding
import com.cmtech.amslogbook.databinding.FragmentVehicleFuelListBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import com.cmtech.amslogbook.model.Fuel_list
import java.lang.Exception

class vehicle_Fuel_list_Fragment : Fragment() {
    lateinit var binding:FragmentVehicleFuelListBinding
    var vehicle_fule_list:ArrayList<Fuel_list> = ArrayList()
    var db:DB?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentVehicleFuelListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
        load_list()
    }

    override fun onResume() {
        super.onResume()
        load_list()
        binding.fuelReference.setOnRefreshListener {
            load_list()
            binding.fuelReference.isRefreshing=false
        }
    }

    fun load_list(){
        vehicle_fule_list.clear()
        var cursor: Cursor?=null
        try{
            val sqlqury="SELECT\n" +
                    "FL.ID AS ID,\n" +
                    "FL.REGISTRATION_NO AS REGISTRATION_NO,\n" +
                    "FL.FUEL AS FUEL,\n" +
                    "FL.AMOUNT AS AMOUNT,\n" +
                    "FL.FUEL_FILLING_DATE AS FILLING_DATE,\n" +
                    "FL.PARKING_AMOUNT AS PARKING,\n"+
                    "FL.OIL_AMOUNT AS OIL,\n"+
                    "FL.CAR_WASH_AMOUNT AS CAR_WASH,\n"+
                    "FL.REPAIR_AMOUNT AS REPAIR,\n"+
                    "FL.TIRES_AMOUNT AS TIRES,\n"+
                    "FL.TOLL_AMOUNT AS TOLL,\n" +
                    "FL.LOCATION AS LOCATION,\n" +
                    "FL.TYPE_OF_EXPENSE AS TYPE_OF_EXPENSE,\n" +
                    "FL.COMMENT AS NOTE,\n" +
                    "VH.VEHICLE_NAME AS VEHICLE_NAME\n" +
                    "FROM FUEL_DATA FL,VEHICLE VH WHERE FL.REGISTRATION_NO = '"+PubVar.numberplate+"'\n" +
                    "GROUP BY FL.ID \n" +
                    "ORDER BY FL.FUEL_FILLING_DATE"
            cursor = db?.FireQuery(sqlqury)
            if(cursor?.count!! > 0){
                cursor.moveToFirst()
                do {
                    val fuelList = Fuel_list(ID = PubFun.isCursorNULL(cursor,"ID",""),
                        REGISTRATION_NO = PubFun.isCursorNULL(cursor,"REGISTRATION_NO",""),
                        FUEL = PubFun.isCursorNULL(cursor,"FUEL",""),
                        AMOUNT = "$"+PubFun.isCursorNULL(cursor,"AMOUNT",""),
                        FUEL_FILLING_DATE = PubFun.returnuserformat(PubFun.isCursorNULL(cursor,"FILLING_DATE","")),
                        LOCATION = PubFun.isCursorNULL(cursor,"LOCATION",""),
                        TYPE_OF_EXPENSE = PubFun.isCursorNULL(cursor,"TYPE_OF_EXPENSE",""),
                        NOTE = PubFun.isCursorNULL(cursor,"NOTE",""),
                        VEHICLE_NAME = PubFun.isCursorNULL(cursor,"VEHICLE_NAME",""),
                        PARKING = PubFun.isCursorNULL(cursor,"PARKING",""),
                        OIL_AMOUNT = PubFun.isCursorNULL(cursor,"OIL",""),
                        CAR_WASH_AMOUNT = PubFun.isCursorNULL(cursor,"CAR_WASH",""),
                        REPAIR_AMOUNT = PubFun.isCursorNULL(cursor,"REPAIR",""),
                        TIRES_AMOUNT = PubFun.isCursorNULL(cursor,"TIRES",""),
                        TOLL_AMOUNT = PubFun.isCursorNULL(cursor,"TOLL",""))
                    vehicle_fule_list.add(fuelList)
                }
                while (cursor.moveToNext())
            }
            else{
                binding.nodatafound.visibility = View.VISIBLE
                binding.recycler.visibility = View.GONE
            }
            binding.recycler.layoutManager = LinearLayoutManager(activity)
            val horizontalLayoutManagaer = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.recycler.layoutManager = horizontalLayoutManagaer
            binding.recycler.adapter = activity?.let { FuelListAdapter(it, vehicle_fule_list) }

        }catch (e: Exception){
            e.printStackTrace()
        }
        finally {
            cursor?.close()
        }
    }
}
