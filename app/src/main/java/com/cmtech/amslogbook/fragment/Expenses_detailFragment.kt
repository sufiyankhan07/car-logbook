package com.cmtech.amslogbook.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.Trip_history
import com.cmtech.amslogbook.databinding.FragmentExpensesDetailBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import es.dmoral.toasty.Toasty
import java.util.*
import kotlin.collections.ArrayList

private const val REQUIES_TO_SPEECH=103
class Expenses_detailFragment : Fragment() {
    lateinit var binding:FragmentExpensesDetailBinding
    lateinit var Expenses:Other_expense_fragment
    var db:DB?=null
    var ID=""
    var expense=""
    var result :ArrayList<String> = ArrayList()
    var call = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null){
            ID = bundle.getString("ID").toString()
            Log.d("Expenses","Exfreagement"+ ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=FragmentExpensesDetailBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db =activity?.let { DB(it)}
        Expenses = Other_expense_fragment()
        init()
        filldata()
        otherdata()
    }
    @SuppressLint("UseRequireInsteadOfGet")
    fun init(){
        binding.eidtflMic.setOnClickListener {
            speectext()
        }
        binding.groupradio.setOnCheckedChangeListener{ radioGroup, id ->
            when(id){
                R.id.rdfuel_business ->{
                    expense="Business"
                }
                R.id.rdfuel_personal ->{
                    expense="Personal"
                }

            }

        }
        binding.btnupdate.setOnClickListener {
            updatedata()
            activity!!.finish()
        }
        binding.btncancel.setOnClickListener {
            activity!!.finish()
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
            }else{
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
        val datePicker = object :DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                call.set(Calendar.YEAR,year)
                call.set(Calendar.MONTH,month)
                call.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                dateview()
            }

        }
        binding.expenseDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker,
                call.get(Calendar.YEAR),
                call.get(Calendar.MONTH),
                call.get(Calendar.DAY_OF_MONTH)
            ).show()

        }

    }
    @SuppressLint("NewApi")
    fun dateview(){
        val format = "dd/MM/yyyy"
        val sdf=SimpleDateFormat(format, Locale.US)
        binding.txtfuelDate.setText(sdf.format(call.time))
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUIES_TO_SPEECH->{
                    result=data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!
                    if(result.size>0){
                        val cap=result[0].substring(0,1).toUpperCase(Locale.ROOT) + result[0].substring(1)
                        binding.editfuelNote.setText(cap)
                    }
                }
            }
        }
    }
    fun otherdata(){
        if(binding.txtparking.text.toString()!="0"){
            binding.chkparking.isChecked=true
            binding.txtparking.visibility=View.VISIBLE
        }else{
            binding.txtparking.visibility=View.GONE
        }
        if(binding.txtoil.text.toString()!="0"){
            binding.chkoil.isChecked=true
            binding.txtoil.visibility=View.VISIBLE
        }
        else{
            binding.txtoil.visibility=View.GONE
        }
        if(binding.txtcarwash.text.toString()!="0"){
            binding.chkcarwash.isChecked=true
            binding.txtcarwash.visibility=View.VISIBLE
        }else{
            binding.txtcarwash.visibility=View.GONE
        }
        if(binding.txtrepair.text.toString()!="0"){
                binding.chkrepair.isChecked=true
                binding.txtrepair.visibility=View.VISIBLE
            }else{
               binding.txtrepair.visibility=View.GONE
            }
        if(binding.txttiers.text.toString()!="0"){
                binding.chktires.isChecked=true
                binding.txttiers.visibility=View.VISIBLE
            }else{
             binding.txttiers.visibility=View.GONE
            }
        if(binding.txttoll.text.toString()!="0"){
                binding.chktoll.isChecked=true
                binding.txttoll.visibility=View.VISIBLE
            }else {
            binding.txttoll.visibility=View.GONE
        }
        if(binding.txtothers.text.toString()!="0"){
                binding.chkothers.isChecked=true
                binding.txtothers.visibility=View.VISIBLE
            }else{
                binding.txtothers.visibility=View.GONE
        }
    }

    fun filldata(){
        val cursor:Cursor?=null
        try {
            val sqlquery="SELECT * FROM FUEL_DATA WHERE ID='$ID' "
            db?.FireQuery(sqlquery).use{
                if(it?.count!! > 0){
                    binding.edtfuelVehicle.setText(PubFun.isCursorNULL(it,"REGISTRATION_NO",""))
                    binding.edtfuelFuel.setText(PubFun.isCursorNULL(it,"FUEL",""))
                    binding.edtfuelAmount.setText(PubFun.isCursorNULL(it,"AMOUNT",""))
                    binding.edtfuelLocation.setText(PubFun.isCursorNULL(it,"LOCATION",""))
                    binding.editfuelNote.setText(PubFun.isCursorNULL(it,"COMMENT",""))
                    binding.txtfuelDate.setText(PubFun.returnuserformat(PubFun.isCursorNULL(it,"FUEL_FILLING_DATE","")))
                    binding.txtparking.setText(PubFun.isCursorNULL(it,"PARKING_AMOUNT",""))
                    binding.txtcarwash.setText(PubFun.isCursorNULL(it,"CAR_WASH_AMOUNT",""))
                    binding.txtoil.setText(PubFun.isCursorNULL(it,"OIL_AMOUNT",""))
                    binding.txtrepair.setText(PubFun.isCursorNULL(it,"REPAIR_AMOUNT",""))
                    binding.txtothers.setText(PubFun.isCursorNULL(it,"OTHERS_AMOUNT",""))
                    binding.txttiers.setText(PubFun.isCursorNULL(it,"TIRES_AMOUNT",""))
                    binding.txttoll.setText(PubFun.isCursorNULL(it,"TOLL_AMOUNT",""))
                    expense=PubFun.isCursorNULL(it,"TYPE_OF_EXPENSE","")
                    if(expense=="Business"){
                        binding.groupradio.check(R.id.rdfuel_business)
                    }else{
                        binding.groupradio.check(R.id.rdfuel_personal)
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        finally {
            cursor?.close()
        }
    }
    private fun updatedata(){
        try {
            val sqlqurey="UPDATE FUEL_DATA SET FUEL="+DatabaseUtils.sqlEscapeString(binding.edtfuelFuel.text.toString().trim())+","+
                    "AMOUNT ="+DatabaseUtils.sqlEscapeString(binding.edtfuelAmount.text.toString().trim())+",LOCATION = "+DatabaseUtils.sqlEscapeString(binding.edtfuelLocation.text.toString().trim())+","+
                    "COMMENT ="+DatabaseUtils.sqlEscapeString(binding.editfuelNote.text.toString().trim())+",PARKING_AMOUNT ="+DatabaseUtils.sqlEscapeString(binding.txtparking.text.toString().trim())+","+
                    "CAR_WASH_AMOUNT="+DatabaseUtils.sqlEscapeString(binding.txtcarwash.text.toString().trim())+",OIL_AMOUNT ="+DatabaseUtils.sqlEscapeString(binding.txtoil.text.toString().trim())+","+
                    "REPAIR_AMOUNT="+DatabaseUtils.sqlEscapeString(binding.txtrepair.text.toString().trim())+",OTHERS_AMOUNT ="+DatabaseUtils.sqlEscapeString(binding.txtothers.text.toString().trim())+","+
                    "TIRES_AMOUNT="+DatabaseUtils.sqlEscapeString(binding.txttiers.text.toString().trim())+",TOLL_AMOUNT= "+DatabaseUtils.sqlEscapeString(binding.txttoll.text.toString().trim())+","+
                    "TYPE_OF_EXPENSE= "+DatabaseUtils.sqlEscapeString(expense)+" , FUEL_FILLING_DATE= "+DatabaseUtils.sqlEscapeString(PubFun.returnsqlformat(binding.txtfuelDate.text.toString().trim()))+
                    "WHERE ID='$ID'"
            db?.ExecuteQuery(sqlqurey)
            Toasty.success(requireContext(),"Data updated Successfully",Toasty.LENGTH_SHORT).show()

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