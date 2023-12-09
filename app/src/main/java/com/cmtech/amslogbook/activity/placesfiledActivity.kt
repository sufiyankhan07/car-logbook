package com.cmtech.amslogbook.activity

import android.app.Activity
import android.content.Intent
import android.database.DatabaseUtils
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.ActivitySettingPageBinding
import com.cmtech.amslogbook.global.DB
import com.google.android.gms.maps.model.LatLng

private const val SOURCE_REQUEST_CODE=100
class placesfiledActivity:AppCompatActivity() {
    var sourceLatLong: LatLng?=null
    var db: DB?=null
    lateinit var binding: ActivitySettingPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySettingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Place"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db= DB(this)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ic_add,menu)
        return super.onCreateOptionsMenu(menu)
    }


    fun init(){
        binding.getLocation.setOnClickListener{
            val intent = Intent(this,MapsActivity::class.java)
            intent.putExtra(MapsActivity.MODE,MapsActivity.SOURCE)
            startActivityForResult(intent, SOURCE_REQUEST_CODE)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                SOURCE_REQUEST_CODE -> {
                    binding.getLocation.setText(data?.getStringExtra(MapsActivity.ADDRESS))
                    sourceLatLong = LatLng(
                        data?.getDoubleExtra(MapsActivity.LATITUDE, 0.0) ?: 0.0,
                        data?.getDoubleExtra(MapsActivity.LONGITUDE, 0.0) ?: 0.0
                    )
                }

            }
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return false
        }else if(id == R.id.menuadd){
            if(binding.place.text.toString().trim().isEmpty()){
                binding.place.error="Required"
            }else{
                insertData()
                onBackPressed()
            }
        }
        return false
    }
    private fun insertData(){
        try {
            val sqlquery="INSERT INTO PLACES(ID,NAME,ADDRESS)VALUES((SELECT MAX(ID)+1 FROM PLACES),"+ DatabaseUtils.sqlEscapeString(binding.place.text.toString().trim())+","+
                    ""+ DatabaseUtils.sqlEscapeString(binding.getLocation.text.toString().trim())+")"
            db?.ExecuteQuery(sqlquery)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}
