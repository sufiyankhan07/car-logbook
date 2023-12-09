package com.cmtech.amslogbook.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cmtech.amslogbook.Permission.PermissionRequest
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.ActivityUpdateprofileBinding
import com.cmtech.amslogbook.databinding.ChangeProfilePasswordBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import com.cmtech.amslogbook.manager.CaptureImage
import com.example.mypracticamstestko.Manager.SharedPrefs
import com.example.mypracticamstestko.Manager.SharedPrefs.Companion.SELECTED_WJFILE_ID
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateProfileActivity:BaseActivity() {
    val TAG="UpdateProfileActivity"
    private lateinit var binding:ActivityUpdateprofileBinding
    private lateinit var Changebind:ChangeProfilePasswordBinding
    private val REQUEST_GALLERY=101
    private val REQUEST_CAMERA=102
    var captureimage:CaptureImage?=null
    var imagepath=""
    var db:DB?=null
    var permissionrequest:PermissionRequest?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityUpdateprofileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.enterTransition = null
        setContentView(binding.root)
        setToolbar()
        captureimage= CaptureImage(this)
        db=DB(this)
        permissionrequest= PermissionRequest(this)
        binding.update.setOnClickListener {
            if(validation()){
               getUpdateData()
            }
        }
        binding.profilePhoto.setOnClickListener {
            Takepicture()
        }
        binding.changeProfile.setOnClickListener {
            Takepicture()
        }
        binding.changePassword.setOnClickListener {
            ChangePasswordDialog()
        }
        getRegistedData()


    }
    private fun Takepicture(){
        val item : Array<CharSequence>
        try {
            item = arrayOf("Take photo","Choose from Gallery","Cancel")
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Select Image")
            dialog.setCancelable(false)
            dialog.setItems(item){ dialogInterface, i ->
                if(item[i] =="Take photo"){
                    permissionrequest?.askCameraPermission(object : PermissionRequest.PermissionCallBack{
                        override fun onPermissionGranted(type: String) {
                            fromcamera()
                        }

                    })
                }else if (item[i]=="Choose from Gallery"){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        permissionrequest?.askStoragePermissionOld(object : PermissionRequest.PermissionCallBack{
                            override fun onPermissionGranted(type: String) {
                                Fromgallery()
                            }

                        })
                    }else{
                        permissionrequest?.askStoragePermissionOld(object :PermissionRequest.PermissionCallBack{
                            override fun onPermissionGranted(type: String) {
                                Fromgallery()
                            }

                        })
                    }

                }
                    else if(item[i]=="Cancel"){
                    dialogInterface.dismiss()
                }
            }
            dialog.show()

        }catch(e:Exception){
            e.printStackTrace()

        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if(requestCode == REQUEST_CAMERA && resultCode== Activity.RESULT_OK){
                getimage(captureimage?.getRightAngleImage(captureimage?.imagePath).toString())

            }else if(requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK){
                getimage(captureimage?.getRightAngleImage(captureimage?.getPath(data?.data,this)).toString())
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun Progressbar(ImagePath:String){
        showProgress()
        GlobalScope.launch(Dispatchers.Main){
            getimage(ImagePath)

            dismissProgress()
        }

    }

    private fun fromcamera(){
            val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, captureimage!!.setImageUri())
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, REQUEST_CAMERA)
    }


    private fun Fromgallery(){
        val intent= Intent()
        intent.type="image/*"
        intent.action= Intent.ACTION_PICK
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    fun getimage(path:String){
        getpath(captureimage!!.decodeFile(path))
        Log.d(TAG,path)
    }

    @SuppressLint("SuspiciousIndentation")
    fun getpath(bitmap: Bitmap?){
        val tempuri:Uri = captureimage!!.getImageUri(this,bitmap)
        imagepath= captureimage!!.getRealPathFromURI(tempuri,this)
        Log.d("image","Image_path:$imagepath")
            Glide.with(this)
                .load(imagepath)
                .into(binding.profilePhoto)

        SharedPrefs.save(this,SELECTED_WJFILE_ID,imagepath)
    }

    private fun setToolbar(){
        title = ""
        setSupportActionBar(binding.toolbar)
        if(supportActionBar!=null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id == android.R.id.home){
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validation():Boolean{
        var b=true
        var message=""
        if(binding.name.text.toString().trim().isEmpty()){
            b=false
            message="Name can not be empty"
        }else if(binding.personalGroupName.toString().trim().isEmpty()){
            b=false
            message="Group Name(Personal) can note be empty"
        }else if(binding.enterpriseName.text.toString().trim().isEmpty()){
            b=false
            message="Group Name(Business) can note be empty"
        }else if(binding.email.text.toString().trim().isEmpty()){
            b=false
            message="Email Address can note be empty"
        }else if(binding.contact.text.toString().trim().isEmpty()){
            b=false
            message="Contact Number can note be empty"
        }else{
            message="Success"
        }
        if(message!="Success"){
            Toasty.error(this,message,Toasty.LENGTH_SHORT).show()
        }
        return b
    }
    private fun ChangePasswordDialog(){
        Changebind=ChangeProfilePasswordBinding.inflate(layoutInflater)
        val Dialog=Dialog(this, R.style.Alertdialogcuts)
        Dialog.setContentView(Changebind.root)
        Dialog.show()
        Changebind.btnChangeClose.setOnClickListener {
            Dialog.dismiss()
        }
        Changebind.btnChangeAdd.setOnClickListener {
            val oldpassword=Changebind.oldPassword.text.toString()
            val newpassword=Changebind.newPassword.text.toString()
            val confirmpassword=Changebind.confirmPassword.text.toString()

            if(oldpassword.isNotEmpty() && newpassword.isNotEmpty() && confirmpassword.isNotEmpty()){
                newPassword(oldpassword,newpassword,confirmpassword)
            }else{
                Toasty.error(this,"field is not empty",Toasty.LENGTH_LONG).show()
            }
        }
    }

    private fun getRegistedData(){
        try {
            val sqlQuery="SELECT * FROM REGISTER_USER WHERE ID ='1'"
            db?.FireQuery(sqlQuery).use {
                if(it?.count!!>0){
                    binding.name.setText( PubFun.isCursorNULL(it,"USER_NAME",""))
                    binding.email.setText( PubFun.isCursorNULL(it,"EMAIL_ID",""))
                    binding.contact.setText( PubFun.isCursorNULL(it,"CONTACTS",""))
                    binding.personalGroupName.setText(PubFun.isCursorNULL(it,"GROUP_PERSONAL",""))
                    binding.enterpriseName.setText(PubFun.isCursorNULL(it,"GROUP_BUSINESS",""))
                    val imagePicker= PubFun.isCursorNULL(it,"IMAGE_PATH","")
                    Glide.with(this)
                        .load(imagePicker)
                        .placeholder(R.drawable.user).error(R.drawable.user)
                        .into(binding.profilePhoto)
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getUpdateData(){
        try {
            val sqlquery="UPDATE REGISTER_USER SET USER_NAME="+DatabaseUtils.sqlEscapeString(binding.name.text.toString().trim())+","+
                    "EMAIL_ID="+DatabaseUtils.sqlEscapeString(binding.email.text.toString().trim())+",CONTACTS="+DatabaseUtils.sqlEscapeString(binding.contact.text.toString().trim())+","+
                    "GROUP_PERSONAL="+DatabaseUtils.sqlEscapeString(binding.personalGroupName.text.toString().trim())+",GROUP_BUSINESS="+DatabaseUtils.sqlEscapeString(binding.enterpriseName.text.toString().trim())+","+
                    "IMAGE_PATH="+DatabaseUtils.sqlEscapeString(imagepath)+"WHERE ID='1'"
            db?.ExecuteQuery(sqlquery)
            Toasty.success(this,"Data Save Successfully",Toasty.LENGTH_SHORT).show()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun newPassword(oldpassword:String,newpassword:String,confirm:String){
        try {
            val sqlquery="SELECT * FROM REGISTER_USER WHERE ID='1'"
            db?.FireQuery(sqlquery).use {
                if(it?.count!!>0){
                     val testing=PubFun.isCursorNULL(it,"PASSWORD","")
                    if(testing!=oldpassword){
                       Toasty.error(this,"please insert valid password",Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        try {
            if(newpassword==confirm){
                val sqlUpdate="UPDATE REGISTER_USER SET PASSWORD="+DatabaseUtils.sqlEscapeString(newpassword)+",CONFIRM_PASSWORD="+DatabaseUtils.sqlEscapeString(confirm)+"WHERE ID='1'"
                db?.ExecuteQuery(sqlUpdate)
            }else{
                Toasty.error(this,"Please insert correct data",Toasty.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }




    }
}