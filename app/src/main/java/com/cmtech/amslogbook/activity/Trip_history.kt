package com.cmtech.amslogbook.activity

import android.app.Dialog
import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.ActivityFilterBinding
import com.cmtech.amslogbook.databinding.ChangePasswordBinding
import com.cmtech.amslogbook.fragment.Other_expense_fragment
import com.cmtech.amslogbook.fragment.Trip_list_fragment
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.model.Trip_list
import java.util.*
import kotlin.collections.ArrayList

class Trip_history:AppCompatActivity(){
    lateinit var TripList:Trip_list_fragment
    lateinit var Expenses:Other_expense_fragment
    lateinit var binding: ActivityFilterBinding
    val Trip_list: ArrayList<Trip_list> = ArrayList()
    var trip:String?=null
    var fuel:String?=null
    var Trip_type=""
    var db:DB?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Trip History"
        trip = intent.getStringExtra("Dashboard")
        fuel= intent.getStringExtra("FuelData")
        TripList = Trip_list_fragment()
        Expenses = Other_expense_fragment()
        setFragment(TripList)
        if (trip.equals("Expenses") || fuel.equals("FuelExpenses")) {
            title = "Expenses History"
            setFragment(Expenses)
        }
        db= DB(this)

    }
//    private fun init() {
//        val endDate = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }
//        val startDate = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
//
//        val horizontalCalendar = HorizontalCalendar.Builder(this, R.id.Calendar_view)
//            .range(startDate,endDate)
//            .datesNumberOnScreen(5)
//            .build()
//
//        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
//            override fun onDateSelected(date: Calendar?, position: Int) {
//
//            }
//        }
//
//    }
    private fun setFragment(fragment: Fragment){
        val FragmentManager:FragmentManager = supportFragmentManager
        FragmentManager.beginTransaction().replace(R.id.trip_history,fragment,"Fragment").commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            onBackPressed()
            return false
        }
        return super.onOptionsItemSelected(item)
    }


}
