package com.cmtech.amslogbook.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.database.DatabaseUtils
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.MapsActivity
import com.cmtech.amslogbook.databinding.FragmentTripDetailBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.example.mypracticamstestko.Manager.SessionManager
import com.google.android.gms.maps.model.LatLng
import es.dmoral.toasty.Toasty
import java.sql.Time
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

private const val DESTINATION_REQUEST_CODE = 100
private const val SOURCE_REQUEST_CODE = 101
private const val REQUIES_TO_SPEECH = 103

class Trip_detailFragment : Fragment() {
    lateinit var binding:FragmentTripDetailBinding
    private var sourceLatLong : LatLng? = null
    private var destinationLatLong : LatLng? = null
    lateinit var TripList:Trip_list_fragment
    var db:DB?=null
    var ID=""
    var Trip_type=""
    var result: ArrayList<String> = ArrayList()
    val cal=Calendar.getInstance()
    var manager:SessionManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null){
            ID = bundle.getString("trip").toString()
            Log.d("first_trip","Tripid"+ ID)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding=FragmentTripDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db= activity?.let { DB(it) }
        manager=activity?.let { SessionManager(it) }
        TripList=Trip_list_fragment()
        filldata()
        init()

    }
    @SuppressLint("UseRequireInsteadOfGet")
    private fun init(){
        binding.tfMic.setOnClickListener {
            speectext()
        }

        binding.updateTrip.setOnClickListener{
            if (validate_odometer_end()){
                UpdateData()
                activity!!.finish()
            }
        }
        binding.cancleTrip.setOnClickListener {
            activity!!.finish()
        }

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

        binding.startTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(requireContext(),
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        binding.endTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(requireContext(),
                    dateSetListener1,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        binding.mapDastination.setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra(MapsActivity.MODE, MapsActivity.DESTINATION)
            startActivityForResult(intent, DESTINATION_REQUEST_CODE)
        }
        binding.mapSource.setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra(MapsActivity.MODE, MapsActivity.SOURCE)
            startActivityForResult(intent, SOURCE_REQUEST_CODE)
        }

        binding.businesslayout.setOnClickListener {
            Trip_type = "Business"
            binding.business.setColorFilter(this.resources.getColor(R.color.green));
            binding.TRlblbusiness.setTextColor(this.resources.getColor(R.color.green))

            binding.personal.setColorFilter(this.resources.getColor(R.color.icon_background));
            binding.TRlblpersonal.setTextColor(this.resources.getColor(R.color.colorPrimaryDark))
        }

        binding.personallayout.setOnClickListener{
            Trip_type = "Personal"
            binding.personal.setColorFilter(this.resources.getColor(R.color.green));
            binding.TRlblpersonal.setTextColor(this.resources.getColor(R.color.green))

            binding.business.setColorFilter(this.resources.getColor(R.color.icon_background))
            binding.TRlblbusiness.setTextColor(this.resources.getColor(R.color.colorPrimaryDark))
        }

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
    fun validate_odometer_end():Boolean{
        var b = true
        val odometerstart =  binding.starodometer.text.toString().trim()
        val odometerend =  binding.endodometer.text.toString().trim()

        if(odometerstart.toInt() >= odometerend.toInt()){
            b = false
        }
        return  b
    }

    @SuppressLint("SimpleDateFormat")
    fun clickTimePicker() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val tpd = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener(function = { _, h, m ->
            val tme = Time(h, m, 0)//seconds by default set to zero
            val formatter: Format
            formatter = SimpleDateFormat("h:mm a")
            val value =  formatter.format(tme)
            binding.txtstarttime2.text = value
        }),hour,minute,false)
        tpd.show()
    }

    @SuppressLint("SimpleDateFormat")
    fun clickTimePicker2() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val tpd = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener(function = { _, h, m ->
            val tme = Time(h, m, 0)//seconds by default set to zero
            val formatter: Format
            formatter = SimpleDateFormat("h:mm a")
            val value =  formatter.format(tme)
            binding.txtendtime2.text = value
        }),hour,minute,false)

        tpd.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SOURCE_REQUEST_CODE -> {
                    binding.startAddress.setText(data?.getStringExtra(MapsActivity.ADDRESS))
                    sourceLatLong = LatLng(
                        data?.getDoubleExtra(MapsActivity.LATITUDE, 0.0) ?: 0.0,
                        data?.getDoubleExtra(MapsActivity.LONGITUDE, 0.0) ?: 0.0
                    )
                    //calculateDistance()
                }
                DESTINATION_REQUEST_CODE -> {
                    binding.endAddress.setText(data?.getStringExtra(MapsActivity.ADDRESS))
                    destinationLatLong = LatLng(
                        data?.getDoubleExtra(MapsActivity.LATITUDE, 0.0) ?: 0.0,
                        data?.getDoubleExtra(MapsActivity.LONGITUDE, 0.0) ?: 0.0
                    )
//                    calculateDistance()
                }
                REQUIES_TO_SPEECH ->{
                    result=data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!
                    if(result.size>0){
                        val cap=result[0].substring(0,1).toUpperCase(Locale.ROOT) + result[0].substring(1)
                        binding.tfComment.setText(cap)

                    }
                }

            }
        }

    }

    private fun filldata(){
        try{
            val sqlquery="SELECT * FROM TRIPS WHERE ID='$ID'"
            db?.FireQuery(sqlquery).use {
                if(it!!.count>0){
                    binding.vehicle.setText(PubFun.isCursorNULL(it,"VEHICLE_REGISTRATION_NO",""))
                    binding.starodometer.setText(PubFun.isCursorNULL(it,"ODOMETER_START",""))
                    binding.endodometer.setText(PubFun.isCursorNULL(it,"ODOMETER_END",""))
                    binding.txtstarttime.setText(PubFun.returnuserformat(PubFun.isCursorNULL(it,"START_DATE","")))
                    binding.txtstarttime2.setText(PubFun.isCursorNULL(it,"START_TIME",""))
                    binding.txtendtime.setText(PubFun.returnuserformat(PubFun.isCursorNULL(it,"END_DATE","")))
                    binding.txtendtime2.setText(PubFun.isCursorNULL(it,"END_TIME",""))
                    binding.startAddress.setText(PubFun.isCursorNULL(it,"STARTING_ADDRESS",""))
                    binding.endAddress.setText(PubFun.isCursorNULL(it,"END_ADDRESS",""))
                    binding.distance.setText(PubFun.isCursorNULL(it,"DISTANCE",""))
                    binding.tfComment.setText(PubFun.isCursorNULL(it,"NOTE",""))
                    Trip_type = PubFun.isCursorNULL(it,"TRIP_TYPE","")
                    if(Trip_type=="Business"){
                        binding.business.setColorFilter(this.resources.getColor(R.color.green));
                        binding.TRlblbusiness.setTextColor(this.resources.getColor(R.color.green))

                        binding.personal.setColorFilter(this.resources.getColor(R.color.icon_background));
                        binding.TRlblpersonal.setTextColor(this.resources.getColor(R.color.colorPrimaryDark))
                    }else{
                        binding.personal.setColorFilter(this.resources.getColor(R.color.green));
                        binding.TRlblpersonal.setTextColor(this.resources.getColor(R.color.green))

                        binding.business.setColorFilter(this.resources.getColor(R.color.icon_background))
                        binding.TRlblpersonal.setTextColor(this.resources.getColor(R.color.colorPrimaryDark))
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun UpdateData(){
        try {
            val Sqlquery="UPDATE TRIPS SET TRIP_TYPE="+DatabaseUtils.sqlEscapeString(Trip_type)+",ODOMETER_END ="+DatabaseUtils.sqlEscapeString(binding.endodometer.text.toString().trim())+","+
                    "START_DATE="+DatabaseUtils.sqlEscapeString(PubFun.returnsqlformat(binding.txtstarttime.text.toString().trim()))+",START_TIME="+DatabaseUtils.sqlEscapeString(binding.txtstarttime2.text.toString().trim())+","+
                    "END_DATE="+DatabaseUtils.sqlEscapeString(PubFun.returnsqlformat(binding.txtendtime.text.toString().trim()))+",END_TIME="+DatabaseUtils.sqlEscapeString(binding.txtendtime2.text.toString().trim())+","+
                    "STARTING_ADDRESS="+DatabaseUtils.sqlEscapeString(binding.startAddress.text.toString().trim())+",END_ADDRESS="+DatabaseUtils.sqlEscapeString(binding.endAddress.text.toString().trim())+","+
                    "DISTANCE ="+DatabaseUtils.sqlEscapeString(binding.distance.text.toString().trim())+",NOTE="+DatabaseUtils.sqlEscapeString(binding.tfComment.text.toString().trim())+"WHERE ID='$ID'"
            db?.ExecuteQuery(Sqlquery)
            Toasty.success(requireContext(),"Data updated successfully",Toasty.LENGTH_SHORT).show()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun speectext(){
        if(!SpeechRecognizer.isRecognitionAvailable(requireContext())){
            Toast.makeText(activity,"Speech Text is not available for this device", Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.CANADA)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something!!")
            startActivityForResult(intent, REQUIES_TO_SPEECH)

        }

    }

}