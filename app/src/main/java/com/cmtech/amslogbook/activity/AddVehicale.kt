package com.cmtech.amslogbook.activity

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cmtech.amslogbook.MainActivity
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.ActivityAddVehicaleBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import es.dmoral.toasty.Toasty
import java.util.*

private const val REQUIES_TO_SPEECH = 103
class AddVehicale : AppCompatActivity() {
    var db: DB?=null
    var result: ArrayList<String> = ArrayList()
    val TAG ="AddVehicle"
    lateinit var binding:ActivityAddVehicaleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddVehicaleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Add vehicle"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DB(this)
        init()
    }

    fun init(){
        binding.avMic.setOnClickListener {
            speectext()
        }
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.ownership, android.R.layout.simple_spinner_item
        )
        binding.spownership.adapter=adapter

        val adapter1 = ArrayAdapter.createFromResource(
            this,
            R.array.gear_type, android.R.layout.simple_spinner_item
        )
        binding.spgeartype.adapter=adapter1

        val adapter2= ArrayAdapter.createFromResource(
            this, R.array.Make,android.R.layout.simple_spinner_item
        )
        binding.spmaketype.adapter=adapter2


        val adapter4 = ArrayAdapter.createFromResource(
            this,
            R.array.vehicle_fuel, android.R.layout.simple_spinner_item
        )
        binding.sptank1fuel.adapter=adapter4

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.ic_add, menu)
        return true
}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == android.R.id.home){
            onBackPressed()
            return false
        }
        if (id == R.id.menuadd){
          if(validateat_addvehicle()){
              if (binding.spmaketype.selectedItem.toString().trim()!="Select Make"){
                  if(check_number_plate()){
                      insertvehical_detail()
                      refreshActivity()
                  }else{
                      showAlertdialog()
                  }
              }else{
                  Toasty.error(this,"Select Make also",Toasty.LENGTH_LONG).show()
              }


          }

            return true
        }

        return false
    }
    private fun insertvehical_detail(){
        try {
            val sqlqury = "INSERT OR REPLACE INTO VEHICLE(ID,VEHICLE_NAME,MODEL_NAME,OWNERSHIP,GEAR_TYPE,NUMBER_PLAT,MODEL_NO,MFG_YEAR,SEATING_CAPACITY,FUEL,ODOMETER,DESCRIPTION,DATETIME)" +
                    "VALUES((SELECT MAX(ID)+1 AS ID FROM VEHICLE),"+DatabaseUtils.sqlEscapeString(binding.edtvehiclename.text.toString().trim())+","+DatabaseUtils.sqlEscapeString(binding.spmaketype.selectedItem.toString().trim())+","+DatabaseUtils.sqlEscapeString(binding.spownership.selectedItem.toString().trim())+"," +
                    ""+DatabaseUtils.sqlEscapeString(binding.spgeartype.selectedItem.toString().trim())+","+DatabaseUtils.sqlEscapeString(binding.edtregisternum.text.toString().trim())+","+DatabaseUtils.sqlEscapeString(binding.edtmodelno.text.toString().trim())+","+DatabaseUtils.sqlEscapeString(binding.edtmanufacture.text.toString().trim())+"," +
                    ""+DatabaseUtils.sqlEscapeString(binding.edtseatingcapacity.text.toString().trim())+","+DatabaseUtils.sqlEscapeString(binding.sptank1fuel.selectedItem.toString().trim())+",'"+binding.edtodometerAddVehicle.text.toString().trim()+"',"+DatabaseUtils.sqlEscapeString(binding.txtdescriptionAddvehicle.text.toString().trim())+",'"+PubFun.getUTCDateTime()+"')"
            db!!.ExecuteQuery(sqlqury)
            Log.d(TAG,"sqlqury "+sqlqury)
            Toasty.success(this,resources.getString(R.string.vehicle_saved_msg),Toasty.LENGTH_LONG).show()

//            val intent = Intent(this,Vehicle_detailActivity::class.java)
//            startActivity(intent)
//            PubVar.numberplate = edtregisternum.text.toString().trim()

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun validateat_addvehicle():Boolean{
        var b = true
        var msg =""
        if(binding.edtvehiclename.text.toString().trim().isEmpty()){
            b = false
            binding.edtvehiclename.error= "Required"
            msg = "Enter vehicle name"
        }else if(binding.edtregisternum.text.toString().trim().isEmpty()){
            b = false
            binding.edtregisternum.error= "Required"
            msg = "Enter vehicle number plate"
        }else if(binding.edtodometerAddVehicle.text.toString().trim().isEmpty()){
            b = false
            binding.edtodometerAddVehicle.error= "Required"
            msg = "Enter Odometer"
        }
        else{
            msg="Success"
        }
        if(!msg.equals("Success")){
            Toasty.error(this,msg,Toasty.LENGTH_LONG).show()
        }
        return b
    }

    private fun check_number_plate():Boolean{
        var cursor:Cursor?=null
        var b = true
        try{
            val sqlqury="SELECT * FROM VEHICLE WHERE NUMBER_PLAT='"+binding.edtregisternum.text.toString().trim()+"'"
            cursor = db?.FireQuery(sqlqury)
            if(cursor?.count!! >0){
                b=false
            }
        }catch(e:Exception){
            e.printStackTrace()
        }finally{
                cursor?.close()
        }
        return b
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            when(requestCode){
               REQUIES_TO_SPEECH ->{
                    result=data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!
                    if(result.size>0){
                        val cap=result[0].substring(0,1).toUpperCase(Locale.ROOT) + result[0].substring(1)
                        binding.txtdescriptionAddvehicle.setText(cap)

                    }
                }
            }
        }
    }

    private fun showAlertdialog(){
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("This vehicle is already added, do you want to show vehicle details?")
        builder.setIcon(R.drawable.alertdialog)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            PubVar.numberplate = binding.edtregisternum.text.toString().trim()
            val intent = Intent(this,Vehicle_detailActivity::class.java)
            startActivity(intent)
        }
        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    fun refreshActivity() {
        val i = Intent(this, HomeActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
    }

    private fun speectext(){
        if(!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this ,"Speech Text is not available for this device", Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CANADA)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something!!")
            startActivityForResult(intent,REQUIES_TO_SPEECH)

        }

    }


}
