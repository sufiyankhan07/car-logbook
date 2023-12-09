package com.cmtech.amslogbook.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.ActivityLoginBinding
import com.cmtech.amslogbook.databinding.BottomDialogBinding
import com.cmtech.amslogbook.databinding.ChangePasswordBinding
import com.cmtech.amslogbook.global.DB
import com.example.mypracticamstestko.Manager.SessionManager
import es.dmoral.toasty.Toasty
import java.util.*

class Loginpage: AppCompatActivity() {
    private var mDelayhadler:Handler?=null
    lateinit var bindingDialog: ChangePasswordBinding
    lateinit var binding:ActivityLoginBinding
    var currentPage = 0
    val DELAY_MS: Long = 5000
    val PERIOD_MS: Long = 5000
    var Manager:SessionManager?=null
    var db:DB?=null
    var email_id:EditText?=null
    var password:EditText?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.enterTransition = null
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val images= listOf(
            R.drawable.resource1,
            R.drawable.resource2,
            R.drawable.resource3
        )
//        val adapter= ViewpagerAdapter(images)
//        viewpager.adapter = adapter
        mDelayhadler= Handler()
        val timer=Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                mDelayhadler?.post(mRunnable)
            }
        } ,DELAY_MS,PERIOD_MS)

        Manager = SessionManager(this)
        db= DB(this)
        email_id =binding.editEmailid
        password =binding.editPassword

        binding.btnLogin.setOnClickListener{
            if(validation()) {
                getlogin()
            }
        }
        binding.ForgotPassword.setOnClickListener{
            showdialog()
        }
        binding.gtLogin.setOnClickListener {
            val intent=Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.txtSignup.setOnClickListener {
            val intent=Intent(this,RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
//    class ViewpagerAdapter(val image:List<Int>): RecyclerView.Adapter<ViewpagerAdapter.Viewpager>() {
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewpager {
//            return Viewpager(
//                LayoutInflater.from(parent.context).inflate(R.layout.viewpage_item, parent, false)
//            )
//        }
//        override fun getItemCount(): Int {
//            return image.size
//        }
//        override fun onBindViewHolder(holder: Viewpager, position: Int) {
//            val curimage=image[position]
//            holder.itemView.view_item.setImageResource(curimage)
//        }
//        inner class Viewpager(view: View) : RecyclerView.ViewHolder(view)
//    }

    internal val mRunnable: Runnable = Runnable{
        run{
            if (currentPage == 3) {
                currentPage = 0
            }
//            viewpager.setCurrentItem(currentPage++, true)
        }
    }

    private fun getlogin(){
        try {
            val sqlquery="SELECT * FROM ADMIN WHERE EMAIL_ID='"+email_id?.text.toString().trim()+"'"+
                    "AND PASSWORD='"+password?.text.toString().trim()+"'"+"AND ID='1'"
            db?.FireQuery(sqlquery)?.use {
                if(it.count>0){
                    Manager?.setLogin(true)
                    Toasty.success(this,"Login Successfully",Toasty.LENGTH_SHORT).show()
                    val intent=Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if(email_id?.text.toString().trim().isNotEmpty()){
                    Toasty.error(this,"Please Enter Valid Email And Password",Toasty.LENGTH_SHORT).show()
                }
                else{
                    Manager?.setLogin(false)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun validation():Boolean{
        if(email_id?.text.toString().trim().isEmpty()){
            email_id?.error="Required"
            Toasty.error(this,"Please Enter Your Email",Toasty.LENGTH_SHORT).show()
            return false
        }else if(password?.text.toString().trim().isEmpty()){
            Toasty.error(this,"Enter Password",Toasty.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun showdialog() {
        bindingDialog=ChangePasswordBinding.inflate(layoutInflater)
        val dialog = Dialog(this, R.style.Alertdialogcuts)
        dialog.setContentView(bindingDialog.root)
        dialog.show()
        bindingDialog.btnClose.setOnClickListener {
            dialog.dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        getlogin()
    }

    override fun onDestroy(){
        super.onDestroy()
        mDelayhadler?.removeCallbacks(mRunnable)
    }
}