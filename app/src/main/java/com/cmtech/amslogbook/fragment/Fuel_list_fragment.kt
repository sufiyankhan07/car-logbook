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
import com.cmtech.amslogbook.adapter.FuelListAdapter
import com.cmtech.amslogbook.databinding.FragmentFuelListFragmentBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import com.cmtech.amslogbook.model.Fuel_list
import com.example.mypracticamstestko.Manager.SharedPrefs
import java.lang.Exception

class Fuel_list_fragment : Fragment() {
    lateinit var binding:FragmentFuelListFragmentBinding
    lateinit var FuelListAdapter:FuelListAdapter
    val fuel_arry_list:ArrayList<Fuel_list> = ArrayList()
    var db: DB?=null
    var ID=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=FragmentFuelListFragmentBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
        load_list()
    }
    override fun onResume() {
        super.onResume()
        load_list()
        binding.reference.setOnRefreshListener {
            load_list()
            binding.reference.isRefreshing= false
        }

    }

    @SuppressLint("NotifyDataSetChanged", "UseRequireInsteadOfGet")
    fun load_list(){
        fuel_arry_list.clear()
        val sharedpref= PubVar.Trip_type
        var cursor:Cursor?=null
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
                    "FROM FUEL_DATA FL,VEHICLE VH WHERE FL.REGISTRATION_NO ='$sharedpref' AND VH.NUMBER_PLAT='$sharedpref'\n" +
                    "GROUP BY FL.ID \n" +
                    "ORDER BY FL.ID DESC"
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
                        TOLL_AMOUNT = PubFun.isCursorNULL(cursor,"TOLL","")
                    )
                    fuel_arry_list.add(fuelList)
                }
                while (cursor.moveToNext())
                binding.recyclerviewFuellist.visibility = View.VISIBLE
                binding.nodatafoundFuellist.visibility = View.GONE
            }
            else{
                binding.nodatafoundFuellist.visibility = View.VISIBLE
                binding.recyclerviewFuellist.visibility = View.GONE
            }
            binding.recyclerviewFuellist.layoutManager = LinearLayoutManager(activity)
            FuelListAdapter = activity!!.let { FuelListAdapter(it, fuel_arry_list) }
            val horizontalLayoutManagaer = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.recyclerviewFuellist.layoutManager = horizontalLayoutManagaer
            binding.recyclerviewFuellist.adapter = FuelListAdapter
            FuelListAdapter.setonChange(fuel_arry_list)
            FuelListAdapter.notifyDataSetChanged()

        }catch (e:Exception){
            e.printStackTrace()
        }
        finally {
            cursor?.close()
        }
    }
    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
            PubVar.pagevalue = "F"
    }

}
