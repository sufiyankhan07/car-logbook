package com.cmtech.amslogbook.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.cmtech.amslogbook.R

class ActivityModule : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page_body)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val moduleval = intent.getStringExtra("module")

//        if(moduleval!!.equals("vehicle")){
//            val vehiclelist = VehicleListFragment()
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container,vehiclelist)
//            transaction.commit()
//        }


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
