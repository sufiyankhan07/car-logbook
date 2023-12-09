package com.cmtech.amslogbook.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.model.car_list
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import com.cmtech.amslogbook.databinding.ActivityAddFuelBinding
import com.cmtech.amslogbook.global.PubVar
import es.dmoral.toasty.Toasty
import kotlin.Exception
import kotlin.collections.ArrayList


class AddFuelActivity : AppCompatActivity() {
    lateinit var binding:ActivityAddFuelBinding
    val vehicle_list: ArrayList<car_list> = ArrayList()
    @SuppressLint("SimpleDateFormat")
    private val date = SimpleDateFormat("dd/MM/yyyy").format(Date())
    lateinit var ratetypeAdapter: ArrayAdapter<String>
    var cal= Calendar.getInstance()
    var db:DB? = null
    val RECOD_SPEECH=102
    var result:ArrayList<String> = ArrayList()
    var expense_type = "Business"
    var trip:String?=null
    var ID=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddFuelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Add fuel"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        trip = intent.getStringExtra("Dashboard")
        trip.equals("Expenses")
        db = DB(this)
        ID=intent?.getStringExtra("place").toString()
        init()
    }

    fun init() {
        binding.txtfuelDate.setText(date)
        binding.flMic.setOnClickListener {
            speechtext()
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        binding.imgfuelDate.setOnClickListener {
            DatePickerDialog(
                this@AddFuelActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


//        if (get_vehicle()) {
//            binding.spaddfuelVehical.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//
//                }
//
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                    if (binding.spaddfuelVehical.selectedItem.toString().trim().equals("Select")) {
//                        binding.edtfuelOdometer.setText("0")
//                    } else {
//                        val position = binding.spaddfuelVehical.selectedItemPosition
//                        val odometerval = vehicle_list.get(position - 1).odometer
//                        vehicle_registration_no = vehicle_list.get(position - 1).numberplat
//                        binding.edtfuelOdometer.setText(odometerval)
//                    }
//                }
//
//            }
//        }
        if(binding.txtparking.text.toString().trim().isEmpty()){
            binding.txtparking.setText("0")
            binding.txtcarwash.setText("0")
            binding.txtoil.setText("0")
            binding.txtrepair.setText("0")
            binding.txttiers.setText("0")
            binding.txttoll.setText("0")
            binding.txtothers.setText("0")
        }


        binding.groupradio.setOnCheckedChangeListener { radioGroup, i ->
            when(i) {
                R.id.rdfuel_business -> {
                    expense_type = "Business"
                }
                R.id.rdfuel_personal -> {
                    expense_type = "Personal"
                }
            }

        }

        binding.clickhere.setOnClickListener {
            binding.layoutotherexpense.visibility = View.VISIBLE
        }

        binding.chkparking.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtparking.visibility = View.VISIBLE
            } else {
                binding.txtparking.visibility = View.GONE
            }
        }
        binding.chkoil.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtoil.visibility = View.VISIBLE
            } else {
                binding.txtoil.visibility = View.GONE
            }
        }
        binding.chkcarwash.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtcarwash.visibility = View.VISIBLE
            } else {
                binding.txtcarwash.visibility = View.GONE
            }
        }
        binding.chkrepair.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
               binding.txtrepair.visibility = View.VISIBLE
            } else {
                binding.txtrepair.visibility = View.GONE
            }
        }

        binding.chktires.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txttiers.visibility = View.VISIBLE
            } else {
                binding.txttiers.visibility = View.GONE
            }
        }

        binding.chktoll.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txttoll.visibility = View.VISIBLE
            } else {
                binding.txttoll.visibility = View.GONE
            }
        }

        binding.chkothers.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtothers.visibility = View.VISIBLE
            } else {
                binding.txtothers.visibility = View.GONE
            }
        }
        binding.edtfuelLocation.setOnClickListener{
            val intent=Intent(this,placesActivity::class.java)
            startActivity(intent)
        }
        getlocation()
        get_vehicle()
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== RECOD_SPEECH && resultCode== RESULT_OK){
            result= data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!
            if(result.size >0){
                val cap= result[0].substring(0,1).toUpperCase(Locale.ROOT) + result[0].substring(1)
                binding.edtfuelNote.setText(cap)
            }
        }
    }

    fun updateDateInView(){
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.txtfuelDate.setText(sdf.format(cal.getTime()))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ic_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        } else if (id == R.id.menuadd) {
            if (validation()) {
                add_details()
                val intent=Intent(this,Trip_history::class.java)
                intent.putExtra("FuelData","FuelExpenses")
                startActivity(intent)
                finish()
            }

        }
        return false
    }


    fun add_details(){
        val vehicle_registration_no = PubVar.Trip_type
        try{
            val sqlqury = "INSERT OR REPLACE INTO FUEL_DATA(ID,REGISTRATION_NO,FUEL,AMOUNT,FUEL_FILLING_DATE,LOCATION,TYPE_OF_EXPENSE,COMMENT," +
                    "UTC_TIME,PARKING_AMOUNT,OIL_AMOUNT,CAR_WASH_AMOUNT,REPAIR_AMOUNT,TIRES_AMOUNT,TOLL_AMOUNT,OTHERS_AMOUNT)" +
                    "VALUES((SELECT MAX(ID)+1 FROM FUEL_DATA),'"+vehicle_registration_no+"',"+DatabaseUtils.sqlEscapeString(binding.edtfuelFuel.text.toString().trim())+"," +
                    "'"+binding.edtfuelAmount.text.toString().trim()+"','"+PubFun.returnsqlformat(binding.txtfuelDate.text.toString().trim())+"',"+DatabaseUtils.sqlEscapeString(binding.edtfuelLocation.text.toString().trim())+"," +
                    "'"+expense_type+"',"+DatabaseUtils.sqlEscapeString(binding.edtfuelNote.text.toString().trim())+",'"+PubFun.getUTCDateTime()+"','"+binding.txtparking.text.toString().trim()+"'," +
                    "'"+binding.txtoil.text.toString().trim()+"','"+binding.txtcarwash.text.toString().trim()+"','"+binding.txtrepair.text.toString().trim()+"','"+binding.txttiers.text.toString().trim()+"','"+binding.txttoll.text.toString().trim()+"','"+binding.txtothers.text.toString().trim()+"')"
            db?.ExecuteQuery(sqlqury)
            Toasty.success(this,getString(R.string.vehicle_saved_msg),Toasty.LENGTH_SHORT).show()
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }

    }

//    fun get_vehicle(): Boolean {
//        vehicle_list.clear()
//        val vehicle = true
//        var cursorvehicle: Cursor? = null
//        try {
//            val sqlqury = "SELECT NUMBER_PLAT,MODEL_NO,ODOMETER FROM VEHICLE"
//            cursorvehicle = db?.FireQuery(sqlqury)!!
//            if (cursorvehicle.count > 0){
//                do {
//                    val carList = car_list(
//                        numberplat = PubFun.isCursorNULL(cursorvehicle, "NUMBER_PLAT", "").toString(),
//                        model = PubFun.isCursorNULL(cursorvehicle, "MODEL_NO", "").toString(),
//                        odometer = PubFun.isCursorNULL(cursorvehicle, "ODOMETER", "").toString()
//                    )
//                    vehicle_list.add(carList)
//                } while (cursorvehicle.moveToNext())
//            }
//            val nameList = ArrayList<String>()
//            nameList.add(0, "Select")
//            for (i in 1..vehicle_list.size) {
//                nameList.add(
//                    i,
//                    vehicle_list.get(i - 1).numberplat + " - " + vehicle_list.get(i - 1).model
//                )
//            }
//            ratetypeAdapter = ArrayAdapter(this, R.layout.spinner_dropdown_item, nameList)
//            binding.spaddfuelVehical.adapter = ratetypeAdapter
//        } catch (e: Exception){
//            e.printStackTrace()
//        } finally {
//            cursorvehicle?.close()
//        }
//
//        return vehicle
//    }

    private fun get_vehicle(){
        val vehicle_registration_no = PubVar.Trip_type
        try {
            val sqlquery="SELECT * FROM VEHICLE WHERE NUMBER_PLAT='$vehicle_registration_no'"
            db?.FireQuery(sqlquery).use {
                if(it!!.count>0){
                    binding.spaddfuelVehical.setText(PubFun.isCursorNULL(it,"NUMBER_PLAT",""))
                    binding.edtfuelOdometer.setText(PubFun.isCursorNULL(it,"ODOMETER",""))
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    fun validation(): Boolean {
        var b = true
        var msg = ""
        if (binding.spaddfuelVehical.text.toString() == "Select") {
            b = false
            msg = "Select vehicle"
        } else if (binding.edtfuelFuel.text.toString().trim().isEmpty()) {
            binding.edtfuelFuel.error = "Required"
            b = false
            msg = "Enter fuel detail"
        } else if (binding.edtfuelAmount.text.toString().trim().isEmpty()) {
            binding.edtfuelAmount.error = "Required"
            b = false
            msg = "Enter fuel amount"
        } else if (binding.edtfuelLocation.text.toString().trim().isEmpty()) {
            binding.edtfuelLocation.error = "Required"
            b = false
            msg = "Enter location"
        } else {
            msg = "success"
        }
        if (msg != "success") {
            Toasty.error(this, msg, Toast.LENGTH_SHORT).show()
        }
        return b
    }
    private fun speechtext(){
        if(!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this,"Speech text are not available",Toast.LENGTH_LONG).show()
        }else{
            val intent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_RESULTS,Locale.CANADA)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something!")
            startActivityForResult(intent,RECOD_SPEECH)
        }
    }

    private fun getlocation(){
        try {
            val sqlquery="SELECT * FROM PLACES WHERE ID='$ID'"
            db!!.FireQuery(sqlquery).let {
                binding.edtfuelLocation.setText(PubFun.isCursorNULL(it,"NAME",""))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}

