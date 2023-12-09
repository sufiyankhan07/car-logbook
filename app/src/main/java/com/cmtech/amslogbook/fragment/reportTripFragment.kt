package com.cmtech.amslogbook.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.adapter.reportTripAdapter
import com.cmtech.amslogbook.databinding.FragmentReportTripBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import com.cmtech.amslogbook.model.Fuel_list

class reportTripFragment : Fragment(), reportTripAdapter.onitemclick {
    private lateinit var binding:FragmentReportTripBinding
    val trip_repo:ArrayList<Fuel_list> = ArrayList()
    var db:DB?=null
    var adapter:reportTripAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentReportTripBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db=activity?.let { DB(it) }
        loadData()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun loadData(){
        trip_repo.clear()
        val sharedpref= PubVar.Trip_type
        try {
            val sqlquery="SELECT\n" +
                    "FL.ID AS ID,\n" +
                    "FL.REGISTRATION_NO AS REGISTRATION_NO,\n" +
                    "IFNULL(SUM(FL.AMOUNT + FL.PARKING_AMOUNT + FL.OIL_AMOUNT + FL.CAR_WASH_AMOUNT + FL.REPAIR_AMOUNT + FL.TIRES_AMOUNT + FL.TOLL_AMOUNT + FL.OTHERS_AMOUNT),'0') AS AMOUNT,\n" +
                    "FL.FUEL_FILLING_DATE AS FILLING_DATE,\n" +
                    "FL.PARKING_AMOUNT AS PARKING,\n" +
                    "FL.OIL_AMOUNT AS OIL,\n" +
                    "FL.CAR_WASH_AMOUNT AS CAR_WASH,\n" +
                    "FL.REPAIR_AMOUNT AS REPAIR,\n" +
                    "FL.TIRES_AMOUNT AS TIRES,\n" +
                    "FL.TOLL_AMOUNT AS TOLL,\n" +
                    "FL.TYPE_OF_EXPENSE AS TYPE_OF_EXPENSE,\n" +
                    "FL.COMMENT AS NOTE,\n" +
                    "VH.VEHICLE_NAME AS VEHICLE_NAME,\n" +
                    "VH.ODOMETER AS ODOMETER\n" +
                    "FROM FUEL_DATA FL,VEHICLE VH WHERE FL.REGISTRATION_NO = '$sharedpref' AND VH.NUMBER_PLAT = '$sharedpref'\n" +
                    "ORDER BY FL.ID DESC"
            db?.FireQuery(sqlquery).let {
                if(it!!.count>0){
                    it.moveToFirst()
                    do {
                        val list=Fuel_list(ID = PubFun.isCursorNULL(it,"ID",""),
                            FUEL_FILLING_DATE = PubFun.isCursorNULL(it,"FILLING_DATE",""),
                            LOCATION = PubFun.isCursorNULL(it,"LOCATION",""),
                            AMOUNT = PubFun.isCursorNULL(it,"AMOUNT",""),
                            ODOMETER = PubFun.isCursorNULL(it,"ODOMETER","")
                        )
                        trip_repo.add(list)

                    }while(it.moveToNext())
                    binding.reportRecycler.visibility=View.VISIBLE
                    binding.nodatafound.visibility=View.GONE
                }else{
                    binding.reportRecycler.visibility=View.GONE
                    binding.nodatafound.visibility=View.VISIBLE
                }
                adapter = reportTripAdapter(activity!!,trip_repo,this)
                binding.reportRecycler.layoutManager= LinearLayoutManager(activity)
                binding.reportRecycler.adapter=adapter
                adapter!!.updateList(trip_repo)
                adapter!!.notifyDataSetChanged()

            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onclick(id: String) {
        TODO("Not yet implemented")
    }


}