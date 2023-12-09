package com.cmtech.amslogbook.activity

import android.content.Intent
import android.database.DatabaseUtils
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.RegistrationFormBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubVar
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegistrationActivity: AppCompatActivity() {
    lateinit var binding:RegistrationFormBinding
    var db:DB?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=RegistrationFormBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.enterTransition = null
        setContentView(binding.root)
        db= DB(this)
        binding.txtSignin.setOnClickListener {
            val intent=Intent(this,Loginpage::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnRgback.setOnClickListener {
           loginPage()
        }
        binding.btnRegister.setOnClickListener {
            if(binding.editCapasswrd.text.toString().trim() == binding.editConfirmpass.text.toString().trim()){
            if(validation()){
                inserResgisteruser()
                GlobalScope.launch(Dispatchers.Main) {
                    binding.editUsername.setText("")
                    binding.editCaemail.setText("")
                    binding.editCapasswrd.setText("")
                    binding.editConfirmpass.setText("")
                    binding.editContact.setText("")
                }
                Toasty.success(this ,"Data save successfully",Toasty.LENGTH_SHORT).show()
                }
            }else{
                Toasty.error(this,"Please insert correct password",Toasty.LENGTH_SHORT).show()
            }

        }
    }
    private fun inserResgisteruser(){
        try{
            val sqlquery="INSERT INTO REGISTER_USER(ID,USER_NAME,EMAIL_ID,PASSWORD,CONFIRM_PASSWORD,IMAGE_PATH,CONTACTS)"+
                    "VALUES((SELECT MAX(ID)+1 FROM REGISTER_USER),'"+binding.editUsername.text.toString().trim()+"','"+binding.editCaemail.text.toString().trim()+"',"+DatabaseUtils.sqlEscapeString(binding.editCapasswrd.text.toString().trim())+","+
                    "'"+binding.editConfirmpass.text.toString().trim()+"','"+DatabaseUtils.sqlEscapeString(PubVar.image_path)+"','"+binding.editContact.text.toString().trim()+"')"
            db?.ExecuteQuery(sqlquery)
        }catch(e:Exception){
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id= item.itemId
        if(id==android.R.id.home){
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        loginPage()
    }
    private fun validation():Boolean{
        var b=true
        var message=""
        if(binding.editUsername.text.toString().trim().isEmpty()){
            b=false
            message="Enter the Username"

        }else if(binding.editCaemail.text.toString().trim().isEmpty()){
            b=false
            message="Enter the Email"
        }else if (binding.editCapasswrd.text.trim().toString().isEmpty()){
            b=false
            message="Enter the Password"
        }else if(binding.editConfirmpass.text.toString().trim().isEmpty()){
            b=false
            message="Enter the confirm password"
        }else if(binding.editContact.text.trim().toString().trim().isEmpty()){
            b=false
            message="Enter the confirm Password"
        }else{
            message="Success"
        }
        if(!message.equals("Success")){
            Toasty.error(this,message,Toasty.LENGTH_SHORT).show()
        }
        return b
    }
    private fun loginPage(){
        val intent=Intent(this,Loginpage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}