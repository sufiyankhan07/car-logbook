package com.cmtech.amslogbook.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.fragment.Expenses_detailFragment
import com.cmtech.amslogbook.fragment.Trip_detailFragment
import com.cmtech.amslogbook.global.DB
import es.dmoral.toasty.Toasty

class AddOtherExpense : AppCompatActivity() {
    lateinit var expense:Expenses_detailFragment
    lateinit var trip:Trip_detailFragment
    var db: DB? = null
    var ID = ""
    var tripid=""
    val TAG = "AddOtherExpense"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_other_expense)
        title = "Expenses Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db=DB(this)
        ID = intent?.getStringExtra("ID").toString()
        tripid = intent.getStringExtra("trip").toString()
        expense= Expenses_detailFragment()
        trip=Trip_detailFragment()


        if(ID != "null"){
            loadfragment(expense)
            val bundle = Bundle()
            bundle.putString("ID",ID)
            expense.arguments =  bundle
        }else{
            title="Trip Details"
            loadfragment(trip)
            val tripbundle = Bundle()
            tripbundle.putString("trip",tripid)
            trip.arguments=tripbundle
        }
    }
   private fun loadfragment(fragment:Fragment){
       val fragmentmaganer:FragmentManager = supportFragmentManager
       fragmentmaganer.popBackStack()
       fragmentmaganer.beginTransaction().replace(R.id.framenlayout,fragment,"Fragment").commit()
   }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
           onBackPressed()
            return false
        }
        return false
    }
}




