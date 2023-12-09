package com.cmtech.amslogbook

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.cmtech.amslogbook.activity.HomeActivity
import com.cmtech.amslogbook.activity.Loginpage
import com.cmtech.amslogbook.databinding.ActivityMainBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.utils.MIDrawerView
import com.example.mypracticamstestko.Manager.SessionManager
import com.example.mypracticamstestko.Manager.SharedPrefs

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    private var mDelayHandler: Handler? = null
    var dblogin:DB?=null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    var Manager:SessionManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SharedPrefs.save(Login_Activity@ this,SharedPrefs.slideType, MIDrawerView.MI_TYPE_SLIDE_WITH_CONTENT.toString())
        mDelayHandler = Handler()
        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        dblogin = DB(this)
        Manager= SessionManager(this)
        insertAdmindata()
        Glide.with(this)
            .load(R.drawable.car)
            .into(binding.splaceImage);
    }

    internal val mRunnable: Runnable = Runnable {
        if(Manager?.isLoggedIn == true){
            val intent = Intent(applicationContext,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent=Intent(applicationContext,Loginpage::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun insertAdmindata(){
        try {
            val sqlcheck ="SELECT * FROM ADMIN"
           dblogin?.FireQuery(sqlcheck)?.use {
               if(it.count>0){
                   Log.d("Testing","Data Save")
               }else{
                   val sqlquery ="INSERT OR REPLACE INTO ADMIN (ID,EMAIL_ID,PASSWORD)VALUES('1','sufiyankhan07k@gmail.com','1234')"
                   dblogin?.ExecuteQuery(sqlquery)
               }

           }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDelayHandler?.removeCallbacks(mRunnable)
    }
}
