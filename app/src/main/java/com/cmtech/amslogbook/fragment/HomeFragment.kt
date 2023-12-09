package com.cmtech.amslogbook.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.AddVehicale
import com.cmtech.amslogbook.activity.Trip_history

import com.cmtech.amslogbook.databinding.HomePageBodyBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.global.PubVar
import com.cmtech.amslogbook.model.car_list
import com.example.mypracticamstestko.Manager.SessionManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(),OnChartValueSelectedListener {
    lateinit var binding:HomePageBodyBinding
    val TAG = "HomeActivity"
    @SuppressLint("SimpleDateFormat")
    private val date = SimpleDateFormat("dd/MM/yyyy").format(Date())
    val vehicle_list: ArrayList<car_list> = ArrayList()

    //    var adapter2:ArrayAdapter<CharSequence>?=null
    var manager: SessionManager? = null
//    lateinit var vehicleAdapter: ArrayAdapter<String>
//    lateinit var vehiclelist: ArrayAdapter<String>
    var call = Calendar.getInstance()
    var db: DB? = null
    var trip_type = ""
    var registration_no = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=HomePageBodyBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
        val datepicker = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                call.set(Calendar.YEAR, year)
                call.set(Calendar.MONTH, month)
                call.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                datepicker()
            }
        }
        binding.layoutstartdate.setOnClickListener {
            val picker1 = activity?.let { it1->
                DatePickerDialog(
                    it1,
                    datepicker,
                    call.get(Calendar.YEAR),
                    call.get(Calendar.MONTH),
                    call.get(Calendar.DAY_OF_MONTH)
                )}
            picker1!!.datePicker.maxDate = System.currentTimeMillis()
            picker1.show()
        }
        val datepicker1 = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                call.set(Calendar.YEAR, year)
                call.set(Calendar.MONTH, month)
                call.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                datepicker1()
            }
        }
        binding.layoutendtime.setOnClickListener {
            val picker = activity?.let {it2 ->
                DatePickerDialog(
                    it2,
                    datepicker1,
                    call.get(Calendar.YEAR),
                    call.get(Calendar.MONTH),
                    call.get(Calendar.DAY_OF_MONTH)
                )}
            picker!!.datePicker.maxDate = System.currentTimeMillis()
            picker.show()
        }
        init()
        if (binding.txtstartdate.toString().trim().isNotEmpty() && binding.txtenddate.toString().trim().isNotEmpty()) {
            chart1()
            chart2()
        } else {
            Toast.makeText(activity, "Select start date and end date", Toast.LENGTH_LONG).show()
        }
//        binding.btnManulatrip.setOnClickListener{
//            val intent = Intent(activity, VehicleListActivity::class.java)
//            intent.putExtra("module", "logbook")
//            startActivity(intent)
//        }
        binding.linearDashboardHistory.setOnClickListener {
            val intent= Intent(activity,Trip_history::class.java)
            intent.putExtra("Dashboard","History")
            startActivity(intent)
        }
        binding.linearDashboardExpenses.setOnClickListener {
            val intent= Intent(activity,Trip_history::class.java)
            intent.putExtra("Dashboard","Expenses")
            startActivity(intent)
        }
        binding.addVehicle.setOnClickListener {
            val intent = Intent(activity, AddVehicale::class.java)
            startActivity(intent)
        }
    }

    fun init() {
        binding.txtstartdate.text = date
        binding.txtenddate.text = date
        if(getregistration()){
            binding.spvehicle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    (parent!!.getChildAt(0) as? TextView)?.setTextColor(Color.WHITE)
                    if(binding.spvehicle.selectedItem.toString().trim() == "Select Registration_No"){
                        registration_no=""
                        PubVar.Trip_type = registration_no
                        Log.d("Select","select vehicle")
                    }
                    else{
                        val vehicle_position=binding.spvehicle.selectedItemPosition
                        registration_no = vehicle_list [vehicle_position - 1].numberplat
                        PubVar.Trip_type = registration_no
                        Log.d("Trip","Registration_No:$registration_no")


                    }
                    if(binding.spvehicle.selectedItem.toString().trim().isNotEmpty()){
//                        getVehicleDetails()
                        storeData()
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?){

                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        val selectedPosition = binding.spvehicle.selectedItemPosition
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("selectedPosition", selectedPosition)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
       storeData()

    }

    private fun storeData(){
        getvalue()
        trip_type="Personal"
        getpersonal()
        trip_type="Business"
        getpersonal()
        chart1()
        chart2()
    }

    private fun datepicker() {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.US)
        binding.txtstartdate.text = sdf.format(call.time)
    }

    private fun datepicker1() {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.US)
        binding.txtenddate.text = sdf.format(call.time)
    }


    private fun getvalue() {
        try {
            val sqlquery = "SELECT\n" +
                    "IFNULL(SUM(TR.DISTANCE),'0') AS DISTANCE,\n" +
                    "IFNULL (FD.AMOUNT,'0') AS AMOUNT\n" +
                    "FROM TRIPS AS TR\n" +
                    "LEFT JOIN (SELECT REGISTRATION_NO AS FUEL_REGISTRATION , SUM(PARKING_AMOUNT + CAR_WASH_AMOUNT + OIL_AMOUNT+ AMOUNT + REPAIR_AMOUNT + TIRES_AMOUNT + TOLL_AMOUNT + OTHERS_AMOUNT) AS AMOUNT \n" +
                    "FROM FUEL_DATA WHERE REGISTRATION_NO='$registration_no' AND FUEL_FILLING_DATE BETWEEN '${
                        PubFun.returnsqlformat(
                            binding.txtstartdate.text.toString()
                        )
                    }' AND '${PubFun.returnsqlformat(binding.txtenddate.text.toString())}'\n" +
                    "GROUP BY REGISTRATION_NO) AS FD ON TR.VEHICLE_REGISTRATION_NO = FD.FUEL_REGISTRATION \n" +
                    "WHERE TR.VEHICLE_REGISTRATION_NO='$registration_no' AND START_DATE BETWEEN '${
                        PubFun.returnsqlformat(
                            binding.txtstartdate.text.toString()
                        )
                    }' AND '${PubFun.returnsqlformat(binding.txtenddate.text.toString())}'"
            db?.FireQuery(sqlquery).use {
                Log.d(TAG,registration_no)
                val amount = PubFun.isCursorNULL(it, "AMOUNT", "")
                val distance = PubFun.isCursorNULL(it, "DISTANCE", "")
                binding.txtdistanceval.text = distance
                binding.homeexpense.text = amount
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun chart1() {
        binding.chart1.setUsePercentValues(true)
        binding.chart1.description.isEnabled = false
        binding.chart1.setExtraOffsets(1f, 1f, 1f, 1f)

        binding.chart1.dragDecelerationFrictionCoef = 0.95f
        binding.chart1.centerText = generateCenterSpannableText()

        binding.chart1.isDrawHoleEnabled = true
        binding.chart1.setHoleColor(Color.WHITE)

        binding.chart1.setTransparentCircleColor(Color.WHITE)
        binding.chart1.setTransparentCircleAlpha(110)

        binding.chart1.holeRadius = 58f
        binding.chart1.transparentCircleRadius = 61f

        binding.chart1.setDrawCenterText(true)
        binding.chart1.rotationAngle = 0f
        binding.chart1.isRotationEnabled = true
        binding.chart1.isHighlightPerTapEnabled = true

        binding.chart1.setOnChartValueSelectedListener(this)

        setData()


        binding.chart1.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);
        val l = binding.chart1.legend
        l.form = Legend.LegendForm.NONE
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL

        l.setDrawInside(true)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling
        binding.chart1.setEntryLabelColor(Color.BLUE)
        binding.chart1.setEntryLabelTextSize(12f)
    }

    private fun chart2() {
        binding.chart2.setUsePercentValues(true)
        binding.chart2.description.setEnabled(false)
        binding.chart2.setExtraOffsets(1f, 1f, 1f, 1f)

        setData2()

        binding.chart2.dragDecelerationFrictionCoef = 0.95f
        binding.chart2.centerText = generateCenterSpannableText2()

        binding.chart2.isDrawHoleEnabled = true
        binding.chart2.setHoleColor(Color.WHITE)

        binding.chart2.setTransparentCircleColor(Color.WHITE)
        binding.chart2.setTransparentCircleAlpha(110)

        binding.chart2.holeRadius = 58f
        binding.chart2.transparentCircleRadius = 61f

        binding.chart2.setDrawCenterText(true)

        binding.chart2.rotationAngle = 0f
        // enable rotation of the chart by touch
        binding.chart2.isRotationEnabled = true
        binding.chart2.isHighlightPerTapEnabled = true
        binding.chart2.setOnChartValueSelectedListener(this)


        binding.chart2.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);
        val l = binding.chart2.legend
        l.form = Legend.LegendForm.NONE
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL

        l.setDrawInside(true)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling
        binding.chart2.setEntryLabelColor(Color.WHITE)
        //chart.setEntryLabelTypeface(tfRegular);
        binding.chart2.setEntryLabelTextSize(12f)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setData() {
        val set:Float
        val set2:Float
        val businessExpense= binding.homebusiness.text.toString().toFloat()
        val personalExpense= binding.homepersonal.text.toString().toFloat()
        val HomeExpense = binding.txtdistanceval.text.toString().toFloat()
        if(HomeExpense!=0f){
            set=(businessExpense / HomeExpense) * 100
            set2 =(personalExpense / HomeExpense) * 100
        }else{
            set=0f
            set2=0f
        }

        val entries = ArrayList<PieEntry>()
        if(set==0f && set2==0f){
            entries.add(PieEntry(1f,""))
        }
        entries.add(PieEntry(set.toString().toFloat(), 1))
        entries.add(PieEntry(set2.toString().toFloat(), 2))
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors = ArrayList<Int>()
        if(set==0f && set2==0f){
           colors.add(Color.RED)
        }
        colors.add(resources.getColor(R.color.light_green))
        colors.add(resources.getColor(R.color.sky_color))
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.chart1))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        binding.chart1.data = data

        // undo all highlights
        binding.chart1.highlightValues(null)
        binding.chart1.invalidate()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setData2() {
        val set:Float
        val set2:Float
        val businessDistance= binding.expensebusiness.text.toString().toFloat()
        val personalDistance= binding.expensepersonal.text.toString().toFloat()
        val HomeDistance = binding.homeexpense.text.toString().toFloat()
        if(HomeDistance!=0f){
            set=(businessDistance / HomeDistance) * 100
            set2 =(personalDistance / HomeDistance) * 100
        }else{
            set=0f
            set2=0f
        }

        val entries = ArrayList<PieEntry>()
        if(set==0f && set2==0f){
            entries.add(PieEntry(1f,""))
        }
        entries.add(PieEntry(set.toString().toFloat(), 1))
        entries.add(PieEntry(set2.toString().toFloat(), 2))
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors = ArrayList<Int>()
        if(set==0f && set2==0f){
            colors.add(Color.RED)
        }
        colors.add(resources.getColor(R.color.equa))
        colors.add(resources.getColor(R.color.purple))
        //colors.add(ColorTemplate.getHoloBlue());

        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);


        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.chart2))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        //data.setValueTypeface(tfLight);
        binding.chart2.data = data

        // undo all highlights
        binding.chart2.highlightValues(null)
        binding.chart2.invalidate()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun generateCenterSpannableText(): SpannableString {
        return  SpannableString("Distance(km) \n ${binding.txtdistanceval.text.trim()} ")
    }

    private fun generateCenterSpannableText2(): SpannableString {
        return SpannableString("Expenses($) \n ${binding.homeexpense.text.trim()} ")
    }

    override fun onNothingSelected() {
        Log.i("PieChart", "nothing selected")
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e == null)
            return
        Log.i(
            "VAL SELECTED",
            "Value: " + e.y + ", index: " + h!!.getX()
                    + ", DataSet index: " + h.getDataSetIndex()
        )
    }
    @SuppressLint("UseRequireInsteadOfGet")
    fun getregistration():Boolean{
        vehicle_list.clear()
        val vehicle = true
        var cursor: Cursor? = null
        try {
            val sqlqury = "SELECT NUMBER_PLAT,VEHICLE_NAME FROM VEHICLE "
            cursor = db?.FireQuery(sqlqury)!!
            if (cursor.count > 0) {
                do {
                    val carList = car_list(
                        numberplat = PubFun.isCursorNULL(cursor,"NUMBER_PLAT",""),
                        car_name = PubFun.isCursorNULL(cursor,"VEHICLE_NAME","")
                    )
                    vehicle_list.add(carList)
                } while(cursor.moveToNext())
            }
            val sharedPreferences =requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val selectedPosition = sharedPreferences.getInt("selectedPosition", 0)
            val nameList = ArrayList<String>()
            val white = "Select Registration_No"
            nameList.add(0,white)
            nameList.addAll(vehicle_list.map { it.car_name + " - " + it.numberplat })
            val customAdapter = CustomSpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item, nameList)
            customAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spvehicle.adapter = customAdapter
            binding.spvehicle.setSelection(selectedPosition)

        } catch (e: Exception){
            e.printStackTrace()
        } finally{
            cursor?.close()
        }
        return vehicle
    }


    private fun getpersonal() {
        val cursor: Cursor? = null
        try {
            val sqlquery = "SELECT\n" +
                    "IFNULL(SUM(TR.DISTANCE),'0') AS DISTANCE,\n" +
                    "IFNULL(FD.AMOUNT,'0') AS AMOUNT\n" +
                    "FROM TRIPS AS TR\n" +
                    "LEFT JOIN (SELECT REGISTRATION_NO AS FUEL_REGISTRATION , SUM(PARKING_AMOUNT + CAR_WASH_AMOUNT + OIL_AMOUNT+ AMOUNT + REPAIR_AMOUNT + TIRES_AMOUNT + TOLL_AMOUNT + OTHERS_AMOUNT) AS AMOUNT \n" +
                    "FROM FUEL_DATA WHERE TYPE_OF_EXPENSE ='$trip_type' AND REGISTRATION_NO='$registration_no' AND FUEL_FILLING_DATE  BETWEEN '${
                        PubFun.returnsqlformat(
                            binding.txtstartdate.text.toString()
                        )
                    }' AND '${PubFun.returnsqlformat(binding.txtenddate.text.toString())}'\n" +
                    "GROUP BY REGISTRATION_NO) AS FD ON TR.VEHICLE_REGISTRATION_NO = FD.FUEL_REGISTRATION \n" +
                    "WHERE TRIP_TYPE='$trip_type' AND TR.VEHICLE_REGISTRATION_NO ='$registration_no' AND START_DATE BETWEEN '${
                        PubFun.returnsqlformat(
                            binding.txtstartdate.text.toString()
                        )
                    }' AND '${PubFun.returnsqlformat(binding.txtenddate.text.toString())}'"
            db?.FireQuery(sqlquery).use {
                val amount = PubFun.isCursorNULL(it, "AMOUNT", "")
                val distance = PubFun.isCursorNULL(it, "DISTANCE", "")
                when (trip_type) {
                    "Personal"->{
                        binding.homepersonal.text=distance
                        binding.expensepersonal.text=amount
                    }
                    "Business"->{
                        binding.homebusiness.text = distance
                        binding.expensebusiness.text = amount
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            cursor?.close()
        }
    }
    inner class CustomSpinnerAdapter(context: Context, resource: Int, items: List<String>) :
    ArrayAdapter<String>(context, resource, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)
            if (position == 0) {
                val white = "Select Registration_No"
                val whiteSpannable = SpannableString(white)
                whiteSpannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, white.length, 0)
                textView.text = whiteSpannable
            }
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)
            if (position == 0) {
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setTextColor(Color.BLACK)
            }
            return view
        }
    }
}
