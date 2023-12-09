package com.cmtech.amslogbook.fragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.os.Binder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager

import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.AddOtherExpense
import com.cmtech.amslogbook.adapter.OtherListAdapter
import com.cmtech.amslogbook.databinding.ActivityFilterBinding
import com.cmtech.amslogbook.databinding.FragmentOtherExpenseFragmentBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import com.cmtech.amslogbook.model.Fuel_list
import com.cmtech.amslogbook.model.Trip_list
import com.example.mypracticamstestko.Manager.SharedPrefs
import java.util.Objects
import kotlin.Exception

class Other_expense_fragment : Fragment(),OtherListAdapter.onItemClickCallBack{
    lateinit var bindingDialog: ActivityFilterBinding
    lateinit var binder:FragmentOtherExpenseFragmentBinding
    lateinit var otherListAdapter: OtherListAdapter
    val array_list:ArrayList<Fuel_list> = ArrayList()
    var db: DB?=null
    var allStatus:Boolean= true
    var personalStatus:Boolean = false
    var businessStatus:Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binder=FragmentOtherExpenseFragmentBinding.inflate(inflater,container,false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
        load_list()
        binder.imgFilter.setOnClickListener {
            showDialog()
        }
        
        binder.inputSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
               applyFilter(s.toString().trim())
            }

        })
    }
    override fun onResume() {
        super.onResume()
        load_list()
        binder.referOtherexp.setOnRefreshListener {
            load_list()
            binder.referOtherexp.isRefreshing= false
        }

    }
    @SuppressLint("UseRequireInsteadOfGet", "NotifyDataSetChanged")
    fun load_list(){
        array_list.clear()
        val sharedpref=PubVar.Trip_type
        var cursor: Cursor?=null
        try{
            if(allStatus){
                val sqlqury="SELECT\n"+
                        "FL.ID AS ID,\n"+
                        "FL.REGISTRATION_NO AS REGISTRATION_NO,\n"+
                        "IFNULL(SUM(FL.AMOUNT + FL.PARKING_AMOUNT + FL.OIL_AMOUNT + FL.CAR_WASH_AMOUNT + FL.REPAIR_AMOUNT + FL.TIRES_AMOUNT + FL.TOLL_AMOUNT + FL.OTHERS_AMOUNT),'0') AS AMOUNT,\n" +
                        "FL.FUEL_FILLING_DATE AS FILLING_DATE,\n" +
                        "FL.PARKING_AMOUNT AS PARKING,\n"+
                        "FL.OIL_AMOUNT AS OIL,\n"+
                        "FL.CAR_WASH_AMOUNT AS CAR_WASH,\n"+
                        "FL.REPAIR_AMOUNT AS REPAIR,\n"+
                        "FL.TIRES_AMOUNT AS TIRES,\n"+
                        "FL.TOLL_AMOUNT AS TOLL,\n" +
                        "FL.TYPE_OF_EXPENSE AS TYPE_OF_EXPENSE,\n"+
                        "FL.COMMENT AS NOTE,\n"+
                        "VH.VEHICLE_NAME AS VEHICLE_NAME\n"+
                        "FROM FUEL_DATA FL,VEHICLE VH WHERE FL.REGISTRATION_NO = '$sharedpref' AND VH.NUMBER_PLAT = '$sharedpref'\n"+
                        "GROUP BY FL.ID\n"+
                        "ORDER BY FL.ID DESC"
                cursor = db?.FireQuery(sqlqury)
            }else{
                var StatusString=""
                if(personalStatus && !businessStatus){
                    StatusString="Personal"
                }else if(businessStatus && !personalStatus){
                    StatusString="Business"
                }
                val sqlqury="SELECT\n"+
                        "FL.ID AS ID,\n"+
                        "FL.REGISTRATION_NO AS REGISTRATION_NO,\n"+
                        "IFNULL(SUM(FL.AMOUNT + FL.PARKING_AMOUNT + FL.OIL_AMOUNT + FL.CAR_WASH_AMOUNT + FL.REPAIR_AMOUNT + FL.TIRES_AMOUNT + FL.TOLL_AMOUNT + FL.OTHERS_AMOUNT),'0') AS AMOUNT,\n" +
                        "FL.FUEL_FILLING_DATE AS FILLING_DATE,\n" +
                        "FL.PARKING_AMOUNT AS PARKING,\n"+
                        "FL.OIL_AMOUNT AS OIL,\n"+
                        "FL.CAR_WASH_AMOUNT AS CAR_WASH,\n"+
                        "FL.REPAIR_AMOUNT AS REPAIR,\n"+
                        "FL.TIRES_AMOUNT AS TIRES,\n"+
                        "FL.TOLL_AMOUNT AS TOLL,\n" +
                        "FL.TYPE_OF_EXPENSE AS TYPE_OF_EXPENSE,\n"+
                        "FL.COMMENT AS NOTE,\n"+
                        "VH.VEHICLE_NAME AS VEHICLE_NAME\n"+
                        "FROM FUEL_DATA FL,VEHICLE VH WHERE FL.REGISTRATION_NO = '$sharedpref' AND VH.NUMBER_PLAT = '$sharedpref' AND FL.TYPE_OF_EXPENSE='$StatusString' \n"+
                        "GROUP BY FL.ID\n"+
                        "ORDER BY FL.ID DESC"
                cursor = db?.FireQuery(sqlqury)
            }

            if(cursor?.count!! > 0){
                cursor.moveToFirst()
                do {
                    val otherList = Fuel_list(ID = PubFun.isCursorNULL(cursor,"ID",""),
                        REGISTRATION_NO = PubFun.isCursorNULL(cursor,"REGISTRATION_NO",""),
                        AMOUNT = "$"+ PubFun.isCursorNULL(cursor,"AMOUNT",""),
                        FUEL_FILLING_DATE = PubFun.returnuserformat(PubFun.isCursorNULL(cursor,"FILLING_DATE","")),
                        TYPE_OF_EXPENSE = PubFun.isCursorNULL(cursor,"TYPE_OF_EXPENSE",""),
                        NOTE = PubFun.isCursorNULL(cursor,"NOTE",""),
                        VEHICLE_NAME = PubFun.isCursorNULL(cursor,"VEHICLE_NAME",""),
                        PARKING = "$"+PubFun.isCursorNULL(cursor,"PARKING",""),
                        OIL_AMOUNT = "$"+PubFun.isCursorNULL(cursor,"OIL",""),
                        CAR_WASH_AMOUNT = "$"+PubFun.isCursorNULL(cursor,"CAR_WASH",""),
                        REPAIR_AMOUNT ="$"+ PubFun.isCursorNULL(cursor,"REPAIR",""),
                        TIRES_AMOUNT ="$"+ PubFun.isCursorNULL(cursor,"TIRES",""),
                        TOLL_AMOUNT ="$"+ PubFun.isCursorNULL(cursor,"TOLL","")
                    )
                    array_list.add(otherList)
                }while(cursor.moveToNext())
            }else{
                binder.nodatafoundOtherlist.visibility = View.VISIBLE
                binder.recyclerviewOtherlist.visibility = View.GONE
            }
            otherListAdapter= OtherListAdapter(activity!!,array_list,this)
            binder.recyclerviewOtherlist.layoutManager = LinearLayoutManager(activity)
            val horizontallayoutmanage= LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
            binder.recyclerviewOtherlist.layoutManager= horizontallayoutmanage
            binder.recyclerviewOtherlist.adapter = otherListAdapter
            otherListAdapter.onSetItemClick(array_list)
            otherListAdapter.notifyDataSetChanged()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        finally{
            cursor?.close()
        }
    }
    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
            PubVar.pagevalue = "O"
    }

    @SuppressLint("DefaultLocale")
    private fun applyFilter(searchValue: String) {
        val temp: ArrayList<Fuel_list> = ArrayList()
        if (array_list.size > 0) {
            for (list in array_list ) {
                if (list.ID!!.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.FUEL_FILLING_DATE!!.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.FUEL!!.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.LOCATION!!.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.TYPE_OF_EXPENSE!!.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.PARKING!!.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.REGISTRATION_NO!!.toLowerCase().contains(searchValue.toLowerCase())
                ) {
                    temp.add(list)
                }
            }
            otherListAdapter.onSetItemClick(temp)
        }
    }
    fun showDialog(){
        bindingDialog= ActivityFilterBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext(), R.style.Alertdialogcuts)
        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(false)
        dialog.show()
        if (allStatus){
            bindingDialog.all.isChecked = true
        }
        if(personalStatus){
            bindingDialog.chkpersonal.isChecked= true
        }
        if(businessStatus){
            bindingDialog.chkBusiness.isChecked = true
        }
        bindingDialog.all.setOnCheckedChangeListener { compoundButton: CompoundButton?, isChecked: Boolean ->
            allStatus = isChecked
        }
        bindingDialog.chkpersonal.setOnCheckedChangeListener { compoundButton: CompoundButton?, isChecked: Boolean ->
            personalStatus = isChecked
        }
        bindingDialog.chkBusiness.setOnCheckedChangeListener { compoundButton: CompoundButton?, isChecked: Boolean ->
            businessStatus = isChecked
        }
        bindingDialog.imgBackButton.setOnClickListener {
            dialog.dismiss()
        }
        bindingDialog.btnSearch.setOnClickListener {
            if(!allStatus && !personalStatus && !businessStatus){
                Toast.makeText(activity ,"Select at least one filed", Toast.LENGTH_SHORT).show()
            }else{
                dialog.dismiss()
                load_list()
            }
        }
    }

    override fun onItemClick1(id: String) {
        try {
            val sql = "DELETE FROM FUEL_DATA WHERE ID=$id"
            db!!.ExecuteQuery(sql)
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}



