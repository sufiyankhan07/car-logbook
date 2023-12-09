package com.cmtech.amslogbook.fragment


import android.annotation.SuppressLint
import android.database.Cursor
import android.database.DatabaseUtils
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast

import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.FragmentVehicleBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import es.dmoral.toasty.Toasty
import java.lang.Exception

class Vehicle_detailFragment : Fragment() {
    lateinit var binding:FragmentVehicleBinding
    var db:DB?=null;
    var adapter:ArrayAdapter<CharSequence>?=null
    var adapter1:ArrayAdapter<CharSequence>?=null
    var adapter2:ArrayAdapter<CharSequence>?=null
    var adapter4:ArrayAdapter<CharSequence>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View{
        binding=FragmentVehicleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
        init()
        filldata()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun init(){
         adapter = activity?.let {
             ArrayAdapter.createFromResource(it,R.array.ownership, android.R.layout.simple_spinner_item)
        }
        binding.spownership.adapter=adapter

        adapter1 = activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.gear_type, android.R.layout.simple_spinner_item
            )
        }
        binding.spgeartype.adapter=adapter1
        adapter2= activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.Make, android.R.layout.simple_spinner_item)
        }
        binding.spmake.adapter = adapter2

         adapter4 = activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.vehicle_fuel, android.R.layout.simple_spinner_item
            )
        }
        binding.sptank1fuel.adapter=adapter4

        binding.btnupdate.setOnClickListener {
            if(validateat_addvehicle()) {
                if( binding.spmake.selectedItem.toString().trim()!="Select Make"){
                    update_vehicle()
                }else{
                    Toast.makeText(activity,"Select Make",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btncancel.setOnClickListener {
            activity!!.finish()
        }
    }

    private fun validateat_addvehicle():Boolean{
        var b = true
        var msg =""
        if( binding.edtvehiclename.text.toString().trim().isEmpty()){
            b = false
            binding.edtvehiclename.error= "Required"
            msg = "Enter vehicle name"
        }else if( binding.edtregisternum.text.toString().trim().isEmpty()){
            b = false
            binding.edtregisternum.error= "Required"
            msg = "Enter vehicle number plate"
        }else if( binding.edtodometerAddVehicle.text.toString().trim().isEmpty()) {
            b = false
            binding.edtodometerAddVehicle.error = "Required"
            msg = "Enter Odometer"
        }
        else{
            msg="Success"
        }
        if(msg != "Success"){
            Toasty.error(requireContext(),msg,Toasty.LENGTH_LONG).show()
        }
        return b;
    }

    fun filldata(){
        var cursor:Cursor?=null
        try{
            val sqlqury="SELECT * FROM VEHICLE WHERE NUMBER_PLAT='"+PubVar.numberplate+"'"
            cursor = db?.FireQuery(sqlqury)
            if(cursor?.count!! >0){
                binding.edtvehiclename.setText(PubFun.isCursorNULL(cursor,"VEHICLE_NAME",""))
                binding.edtregisternum.setText(PubFun.isCursorNULL(cursor,"NUMBER_PLAT",""))
                binding.edtmodelno.setText(PubFun.isCursorNULL(cursor,"MODEL_NO",""))
                binding.edtmanufacture.setText(PubFun.isCursorNULL(cursor,"MFG_YEAR",""))
                binding.edtseatingcapacity.setText(PubFun.isCursorNULL(cursor,"SEATING_CAPACITY",""))
                binding.edtodometerAddVehicle.setText(PubFun.isCursorNULL(cursor,"ODOMETER",""))
                binding.edtdescriptionAddvehicle.setText(PubFun.isCursorNULL(cursor,"DESCRIPTION",""))
                val ownership = PubFun.isCursorNULL(cursor,"OWNERSHIP","")
                if(!ownership.equals("")){
                    binding.spownership.setSelection(adapter!!.getPosition(ownership))
                }
                val geartype = PubFun.isCursorNULL(cursor,"GEAR_TYPE","")
                if(!geartype.equals("")){
                    binding.spgeartype.setSelection(adapter1!!.getPosition(geartype))
                }
                val spfuel = PubFun.isCursorNULL(cursor,"FUEL","")
                if(spfuel != ""){
                    binding.sptank1fuel.setSelection(adapter4!!.getPosition(spfuel))
                }
                val make= PubFun.isCursorNULL(cursor,"MODEL_NAME","")
                if(make !=""){
                    binding.spmake.setSelection(adapter2!!.getPosition(make))
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }finally{
            cursor?.close()
        }
    }

    private fun update_vehicle(){
        try{
            val sqlqury="UPDATE VEHICLE SET VEHICLE_NAME="+DatabaseUtils.sqlEscapeString( binding.edtvehiclename.text.toString().trim())+"," +
                    "MODEL_NAME ="+DatabaseUtils.sqlEscapeString( binding.spmake.selectedItem.toString().trim())+",MODEL_NO="+DatabaseUtils.sqlEscapeString( binding.edtmodelno.text.toString().trim())+"," +
                    "MFG_YEAR="+DatabaseUtils.sqlEscapeString( binding.edtmanufacture.text.toString().trim())+"," +
                    "SEATING_CAPACITY="+DatabaseUtils.sqlEscapeString( binding.edtseatingcapacity.text.toString().trim())+"," +
                    "ODOMETER="+DatabaseUtils.sqlEscapeString( binding.edtodometerAddVehicle.text.toString().trim())+"," +
                    "OWNERSHIP='"+ binding.spownership.selectedItem.toString().trim()+"',GEAR_TYPE='"+ binding.spgeartype.selectedItem.toString().trim()+"'," +
                    "FUEL='"+ binding.sptank1fuel.selectedItem.toString().trim()+"' WHERE NUMBER_PLAT="+DatabaseUtils.sqlEscapeString( binding.edtregisternum.text.toString().trim())+""
            db?.ExecuteQuery(sqlqury)
            Toasty.success(requireContext(),"Vehicle details updated",Toasty.LENGTH_LONG).show()

        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}
