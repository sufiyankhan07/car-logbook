package com.cmtech.amslogbook.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MenuItem
import com.cmtech.amslogbook.R
import java.text.SimpleDateFormat
import java.util.*
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.model.car_list
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import kotlin.collections.ArrayList
import android.util.Log
import android.widget.Toast
import com.cmtech.amslogbook.databinding.ActivityAddNewTripBinding
import com.cmtech.amslogbook.global.PubVar
import com.example.mypracticamstestko.Manager.SharedPrefs
import com.example.mypracticamstestko.Manager.SharedPrefs.Companion.SELECTED_GROUP_LABEL
import es.dmoral.toasty.Toasty
import java.sql.Time
import java.text.Format

private const val DESTINATION_REQUEST_CODE = 100
private const val SOURCE_REQUEST_CODE = 101
private const val REQUIES_TO_SPEECH=102

@Suppress("UNSAFE_CALL_ON_PARTIALLY_DEFINED_RESOURCE")
class AddNewTrip : AppCompatActivity() {
    lateinit var binding:ActivityAddNewTripBinding
    private var sourceLatLong : LatLng? = null
    private var destinationLatLong : LatLng? = null
    var cal = Calendar.getInstance()
    var db: DB?=null
    val TAG = "AddNewTrip"
    @SuppressLint("SimpleDateFormat")
    private val date = SimpleDateFormat("dd/MM/yyyy").format(Date())
    @SuppressLint("SimpleDateFormat")
    private val time = SimpleDateFormat("h:mm a").format(Date())
    var trip :String? = ""
    var result: ArrayList<String> = ArrayList()
    val vehicle_list:ArrayList<car_list> = ArrayList()
    lateinit var ratetypeAdapter: ArrayAdapter<String>
    var trip_type=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddNewTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "New trip"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        trip = intent.getStringExtra("triptype")
        db = DB(this)
        trip_type = "Business"
        binding.imgbusiness.setColorFilter(this.resources.getColor(R.color.green))
        binding.lblbusiness.setTextColor(this.resources.getColor(R.color.green))
        get_vehicle()
        binding.mic.setOnClickListener {
            speectext()
        }
        binding.layoutbusiness.setOnClickListener {
            trip_type = "Business"
            binding.imgbusiness.setColorFilter(this.resources.getColor(R.color.green));
            binding.lblbusiness.setTextColor(this.resources.getColor(R.color.green))

            binding.imgpersonal.setColorFilter(this.resources.getColor(R.color.icon_background));
            binding.lblpersonal.setTextColor(this.resources.getColor(R.color.colorPrimaryDark))
        }

        binding.layoutpersonal.setOnClickListener{
            trip_type = "Personal"
            binding.imgpersonal.setColorFilter(this.resources.getColor(R.color.green));
            binding.lblpersonal.setTextColor(this.resources.getColor(R.color.green))

            binding.imgbusiness.setColorFilter(this.resources.getColor(R.color.icon_background))
            binding.lblbusiness.setTextColor(this.resources.getColor(R.color.colorPrimaryDark))
        }

        if(trip.equals("personal")){
            trip_type = "Personal"
            binding.imgpersonal.setColorFilter(this.resources.getColor(R.color.green))
            binding.lblpersonal.setTextColor(this.resources.getColor(R.color.green))

            binding.imgbusiness.setColorFilter(this.resources.getColor(R.color.icon_background))
            binding.lblbusiness.setTextColor(this.resources.getColor(R.color.colorPrimaryDark))
        }
        else if(trip.equals("business")){
            trip_type = "Business"
            binding.imgbusiness.setColorFilter(this.resources.getColor(R.color.green))
            binding.lblbusiness.setTextColor(this.resources.getColor(R.color.green))

            binding.imgpersonal.setColorFilter(this.resources.getColor(R.color.icon_background));
            binding.lblpersonal.setTextColor(this.resources.getColor(R.color.colorPrimaryDark))
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun init(){
        binding.txtstarttime.text = date
        binding.txtendtime.text = date
        binding.txtstarttime2.text = time
        binding.txtendtime2.text = time

        binding.destinationmap.setOnClickListener {
                val intent = Intent(this,MapsActivity::class.java)
                intent.putExtra(MapsActivity.MODE,MapsActivity.DESTINATION)
                startActivityForResult(intent, DESTINATION_REQUEST_CODE)
            }
        binding.sourceMap.setOnClickListener {
                val intent = Intent(this,MapsActivity::class.java)
                intent.putExtra(MapsActivity.MODE,MapsActivity.SOURCE)
                startActivityForResult(intent, SOURCE_REQUEST_CODE)
            }



//        txtstarttime.setOnClickListener(View.OnClickListener {
//            clickTimePicker(View(applicationContext))
//        })
//
//        val adapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.Add_trip_vehicle, android.R.layout.simple_spinner_item
//        )
//        spaddtrip_vehical.adapter=adapter

//        if(get_vehicle()){
//            binding.spaddtripVehical.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//                override fun onNothingSelected(p0: AdapterView<*>?){
//
//                }
//
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                   if( binding.spaddtripVehical.selectedItem.toString().trim().equals("Select")){
//                       binding.edtodometerstart.setText("0")
//                   }
//                    else{
//                       val position =  binding.spaddtripVehical.selectedItemPosition
//                       val odometerval = vehicle_list.get(position - 1).odometer
//                       vehicle_registration_no = vehicle_list.get(position - 1).numberplat
//                       binding.edtodometerstart.setText(odometerval)
//                   }
//                }
//
//            }


//
//        }

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }
        val dateSetListener1 = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView1()
            }
        }

        binding.layoutstarttime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@AddNewTrip,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        binding.layoutendtime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@AddNewTrip,
                    dateSetListener1,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })
        binding.txtaddNewtrip.setOnClickListener {
            val intent = Intent(this,AddVehicale::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.txtstarttime.text = sdf.format(cal.time)
        clickTimePicker()
    }

    private fun updateDateInView1() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.txtendtime.text = sdf.format(cal.getTime())
        clickTimePicker2()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                DESTINATION_REQUEST_CODE -> {
                    binding.edtendaddress.setText(data?.getStringExtra(MapsActivity.ADDRESS))
                    destinationLatLong = LatLng(
                        data?.getDoubleExtra(MapsActivity.LATITUDE, 0.0) ?: 0.0,
                        data?.getDoubleExtra(MapsActivity.LONGITUDE, 0.0) ?: 0.0
                    )
//                    calculateDistance()
                }
                SOURCE_REQUEST_CODE -> {
                    binding.edtstartingaddress.setText(data?.getStringExtra(MapsActivity.ADDRESS))
                    sourceLatLong = LatLng(
                        data?.getDoubleExtra(MapsActivity.LATITUDE, 0.0) ?: 0.0,
                        data?.getDoubleExtra(MapsActivity.LONGITUDE, 0.0) ?: 0.0
                    )
                    //calculateDistance()
                }
                REQUIES_TO_SPEECH -> {
                    result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!
                    if (result.size > 0){
                        val cap = result[0].substring(0, 1).toUpperCase(Locale.ROOT) + result[0].substring(1)
                        binding.edtnote.setText(cap)
                    }
                }
            }
        }
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
            if(validation()){
                if (validate_odometer_end()) {
                    save_trip_detail()
                    val intent=Intent(this,Trip_history::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, R.string.odometer_validation_msg, Toast.LENGTH_LONG).show()
                }
            }
            return true
        }
        return false
    }


//    fun get_vehicle():Boolean{
//        vehicle_list.clear()
//        val sharedpref= SharedPrefs.getString(this , SharedPrefs.SELECTED_GROUP_NAME)
//        val vehicle = true
//        var cursorvehicle:Cursor?=null
//        try{
//            val sqlqury = "SELECT NUMBER_PLAT,MODEL_NO,ODOMETER FROM VEHICLE WHERE='$sharedpref'"
//            cursorvehicle = db?.FireQuery(sqlqury)!!
//            if(cursorvehicle.count>0){
//                do{
//                    val carList = car_list(numberplat = PubFun.isCursorNULL(cursorvehicle,"NUMBER_PLAT","").toString(),
//                        model =PubFun.isCursorNULL(cursorvehicle,"MODEL_NO","").toString(),
//                        odometer = PubFun.isCursorNULL(cursorvehicle,"ODOMETER","").toString())
//                    vehicle_list.add(carList)
//                }
//                while(cursorvehicle.moveToNext())
//            }
//                val nameList = ArrayList<String>()
//                nameList.add(0, "Select")
//                for (i in 1..vehicle_list.size){
//                    nameList.add(i, vehicle_list[i - 1].numberplat + " - " + vehicle_list[i - 1].model)
//                }
//                ratetypeAdapter = ArrayAdapter(this,R.layout.spinner_dropdown_item,nameList)
//            binding.spaddtripVehical.adapter = ratetypeAdapter
//
//        }catch (e:Exception){
//            e.printStackTrace()
//        }
//        finally {
//            cursorvehicle?.close()
//        }
//        return vehicle
//    }



    fun get_vehicle(){
        val vehicle_registration_no = PubVar.Trip_type
        try {
            val sqlquery="SELECT * FROM VEHICLE WHERE NUMBER_PLAT='$vehicle_registration_no'"
            db?.FireQuery(sqlquery).use {
                if(it!!.count>0){
                    binding.spaddtripVehical.setText(PubFun.isCursorNULL(it,"NUMBER_PLAT",""))
                    binding.edtodometerstart.setText(PubFun.isCursorNULL(it,"ODOMETER",""))
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun clickTimePicker() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { _, h, m ->

            val tme = Time(h, m, 0)//seconds by default set to zero
            val formatter: Format
            formatter = SimpleDateFormat("h:mm a")
           val value =  formatter.format(tme)
            Log.d(TAG, "time : $value")
            binding.txtstarttime2.text = value
        }),hour,minute,false)

        tpd.show()
    }

    @SuppressLint("SimpleDateFormat")
    fun clickTimePicker2() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { _, h, m ->

            val tme = Time(h, m, 0)//seconds by default set to zero
            val formatter: Format
            formatter = SimpleDateFormat("h:mm a")
            val value =  formatter.format(tme)
            Log.d(TAG,"time : "+value)
            binding.txtendtime2.text = value

        }),hour,minute,false)

        tpd.show()
    }

    fun validate_odometer_end():Boolean{
        var b = true
        val odometerstart =  binding.edtodometerstart.text.toString().trim()
        val odometerend =  binding.edtodometerend.text.toString().trim()

        if(odometerstart.toInt() >= odometerend.toInt()){
            b = false
        }
        return  b
    }

    fun save_trip_detail(){
        val vehicle_registration_no = PubVar.Trip_type
        try{
            val sqlqury = "INSERT INTO TRIPS(ID,TRIP_TYPE,VEHICLE_REGISTRATION_NO,ODOMETER_START,ODOMETER_END,START_DATE,END_DATE,START_TIME,END_TIME,STARTING_ADDRESS,END_ADDRESS,NOTE,UTC_TIME,DISTANCE)" +
                    "VALUES((SELECT MAX(ID)+1 FROM TRIPS),'"+trip_type+"','"+vehicle_registration_no+"','"+binding.edtodometerstart.text.toString().trim()+"','"+binding.edtodometerend.text.toString().trim()+"'," +
                    "'"+PubFun.returnsqlformat(binding.txtstarttime.text.toString().trim())+"','"+PubFun.returnsqlformat(binding.txtendtime.text.toString().trim())+"','"+binding.txtstarttime2.text.toString().trim()+"','"+binding.txtendtime2.text.toString().trim()+"'," +
                    ""+DatabaseUtils.sqlEscapeString(binding.edtstartingaddress.text.toString().trim())+","+DatabaseUtils.sqlEscapeString(binding.edtendaddress.text.toString().trim())+","+DatabaseUtils.sqlEscapeString(binding.edtnote.text.toString().trim())+",'"+PubFun.getUTCDateTime()+"','"+binding.edtdistance.text.toString().trim()+"')"
            db?.ExecuteQuery(sqlqury)

            val sqlqury2 = "UPDATE VEHICLE SET ODOMETER ='"+binding.edtodometerend.text.toString().trim()+"'"+"WHERE NUMBER_PLAT='"+vehicle_registration_no+"'"
            db?.ExecuteQuery(sqlqury2)

            Toasty.success(this,"Trip data inserted successfully",Toasty.LENGTH_SHORT).show()

        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun validation():Boolean{
        var b = true
        var msg =""
        if(binding.spaddtripVehical.text.toString().equals("Select")){
            b = false
            msg = "Select vehicle"
        }else if(binding.edtstartingaddress.text.toString().trim().isEmpty()){
            binding.edtstartingaddress.error="Required"
            b = false
            msg = "Enter starting address"
        }else if(binding.edtendaddress.text.toString().trim().isEmpty()){
            binding.edtendaddress.error="Required"
            b = false
            msg = "Enter destination address"
        }else if(binding.edtdistance.text.toString().trim().isEmpty()){
            binding.edtdistance.error="Required"
            b=false
            msg="Enter distance"
        }else if(trip_type.toString().trim().isEmpty()){
            b=false
            msg="Please Also Select Purpose"
        }
        else{
            msg = "success"
        }
        if(msg != "success"){
            Toasty.error(this,msg,Toast.LENGTH_SHORT).show()
        }
        return  b
    }
    private fun speectext(){
        if(!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this,"Speech Text is not available for this device",Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.CANADA)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something!!")
            startActivityForResult(intent,REQUIES_TO_SPEECH)

        }

    }
}
