package com.cmtech.amslogbook.fragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.AddOtherExpense
import com.cmtech.amslogbook.adapter.TripListAdapter
import com.cmtech.amslogbook.databinding.ActivityFilterBinding
import com.cmtech.amslogbook.databinding.FragmentTripListFragmentBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import com.cmtech.amslogbook.model.Trip_list
import com.example.mypracticamstestko.Manager.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Trip_list_fragment : BaseFragment(),TripListAdapter.onItemClickCallBack{
    lateinit var bindingDialog: ActivityFilterBinding
    lateinit var binding:FragmentTripListFragmentBinding
    val trip_arry_list:ArrayList<Trip_list> = ArrayList()
    var db: DB?=null
    var ID=""
    var adapter:TripListAdapter?=null
    var allStatus:Boolean= true
    var personalStatus:Boolean = false
    var businessStatus:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding=FragmentTripListFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
            load_list()
        binding.imgFilter.setOnClickListener {
            showdialog()
        }
        adapter=activity?.let { TripListAdapter(it,trip_arry_list,this)}
        binding.inputSearch.addTextChangedListener(object: TextWatcher{
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
        binding.referTrip.setOnRefreshListener {
            load_list()
            binding.referTrip.isRefreshing= false
        }
    }
     fun load_list(){
        trip_arry_list.clear()
         val sharedpref= PubVar.Trip_type
        var cursor: Cursor?=null
        try{
            if(allStatus){
                val sqlqury = "SELECT\n" +
                        "TP.ID AS ID,\n" +
                        "TP.START_DATE AS START_DATE,\n" +
                        "VH.VEHICLE_NAME AS VEHICLE_NAME,\n" +
                        "TP.VEHICLE_REGISTRATION_NO AS REGISTRATION_NO,\n" +
                        "TP.DISTANCE AS DISTANCE,\n" +
                        "TP.TRIP_TYPE AS TRIP_TYPE,\n" +
                        "TP.STARTING_ADDRESS AS START_ADDRESS,\n" +
                        "TP.END_ADDRESS AS END_ADDRESS\n" +
                        "FROM TRIPS TP, VEHICLE VH WHERE TP.VEHICLE_REGISTRATION_NO = '$sharedpref' AND VH.NUMBER_PLAT='$sharedpref'\n" +
                        "ORDER BY TP.ID DESC"
                cursor = db!!.FireQuery(sqlqury)
            }
            else{
                var StatusString=""
                if(personalStatus && !businessStatus){
                    StatusString="Personal"
                }else if(businessStatus && !personalStatus){
                    StatusString="Business"
                }
                val sqlqury1 = "SELECT\n" +
                        "TP.ID AS ID,\n" +
                        "VH.VEHICLE_NAME AS VEHICLE_NAME,\n" +
                        "TP.START_DATE AS START_DATE,\n" +
                        "TP.VEHICLE_REGISTRATION_NO AS REGISTRATION_NO,\n" +
                        "TP.DISTANCE AS DISTANCE,\n" +
                        "TP.TRIP_TYPE AS TRIP_TYPE,\n" +
                        "TP.STARTING_ADDRESS AS START_ADDRESS,\n" +
                        "TP.END_ADDRESS AS END_ADDRESS\n" +
                        "FROM TRIPS TP,VEHICLE VH WHERE TP.VEHICLE_REGISTRATION_NO ='$sharedpref' AND VH.NUMBER_PLAT='$sharedpref' AND TP.TRIP_TYPE='$StatusString'\n" +
                        "ORDER BY TP.ID DESC"
                cursor= db?.FireQuery(sqlqury1)
            }
            if(cursor?.count!! > 0){
                cursor.moveToFirst()
                do{
                    val tripList = Trip_list(start_date = PubFun.returnuserformat(PubFun.isCursorNULL(cursor,"START_DATE","").toString()),
                        distance = PubFun.isCursorNULL(cursor,"DISTANCE","").toString()+"Km",
                        trip_type = PubFun.isCursorNULL(cursor,"TRIP_TYPE","").toString(),
                        start_address = PubFun.isCursorNULL(cursor,"START_ADDRESS","").toString(),
                        end_address = PubFun.isCursorNULL(cursor,"END_ADDRESS","").toString(),
                        vehicle = PubFun.isCursorNULL(cursor,"VEHICLE_NAME","").toString(),
                        registration_no = PubFun.isCursorNULL(cursor,"REGISTRATION_NO","").toString(),
                        id = PubFun.isCursorNULL(cursor,"ID","").toString())
                    trip_arry_list.add(tripList)
                }while (cursor.moveToNext())
                binding.recyclerviewTriplist.visibility=View.VISIBLE
                binding.nodatafoundTriplist.visibility= View.GONE
            }else{
                binding.nodatafoundTriplist.visibility = View.VISIBLE
                binding.recyclerviewTriplist.visibility = View.GONE
            }
            binding.recyclerviewTriplist.layoutManager = LinearLayoutManager(activity)
            val horizontalLayoutManagaer = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.recyclerviewTriplist.layoutManager = horizontalLayoutManagaer
            binding.recyclerviewTriplist.setHasFixedSize(true)
            binding.recyclerviewTriplist.adapter = adapter
            adapter?.updateList(trip_arry_list)
            adapter?.notifyDataSetChanged()
        }catch (e: Exception){
            e.printStackTrace()
        }finally{
            cursor?.close()
        }

    }
    @SuppressLint("DefaultLocale")
    private fun applyFilter(searchValue: String) {
        val temp: ArrayList<Trip_list> = ArrayList()
        if (trip_arry_list.size > 0) {
            for (list in trip_arry_list ) {
                if (list.id.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.trip_type.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.start_date.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.distance.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.odometer_start.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.registration_no.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.odometer_end.toLowerCase().contains(searchValue.toLowerCase())
                ) {
                    temp.add(list)
                }
            }
            adapter?.updateList(temp)
        }
    }


    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
            PubVar.pagevalue = "T"
    }

    override fun onItemClick(id: String) {
        val intent = Intent(context, AddOtherExpense::class.java)
        intent.putExtra("trip", id)
        Log.d("trip_list",id)
        startActivity(intent)
    }

    private fun showdialog() {
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
                Toast.makeText(activity ,"Select at least one filed",Toast.LENGTH_SHORT).show()
            }else{
                dialog.dismiss()
                load_list()
            }
        }
    }
}
