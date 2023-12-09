package com.cmtech.amslogbook.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.ActivityHomeBinding
import com.cmtech.amslogbook.databinding.BottomDialogBinding
import com.cmtech.amslogbook.fragment.*
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubVar
import com.example.mypracticamstestko.Manager.SessionManager
import com.example.mypracticamstestko.Manager.SharedPrefs
import com.example.mypracticamstestko.Manager.SharedPrefs.Companion.SELECED_VEHICLE_NAME
import com.example.mypracticamstestko.Manager.SharedPrefs.Companion.SELECTED_GROUP_NAME
import es.dmoral.toasty.Toasty


class HomeActivity : AppCompatActivity() {
    lateinit var bottDialog:BottomDialogBinding
    lateinit var binding:ActivityHomeBinding
    lateinit var homeFragment:HomeFragment
    lateinit var tripFragment : Trip_list_fragment
    lateinit var Reposts: ReportFragment
    lateinit var Other_expense:Other_expense_fragment
    lateinit var profileFragment: ProfileFragment
    lateinit var vehiclelist:VehicleListFragment
    var backPressCount = 1
    var db:DB?=null
    var manager:SessionManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DB(this)
        homeFragment = HomeFragment()
        tripFragment = Trip_list_fragment()
         Reposts= ReportFragment()
        Other_expense = Other_expense_fragment()
        profileFragment = ProfileFragment()
        vehiclelist=VehicleListFragment()
        setSupportActionBar(binding.toolbar)
        setFragment(homeFragment)
        title="Dashboard"

        binding.bottomNavigation1.setOnNavigationItemSelectedListener {
            setUpBottomNavigationView()
            when (it.itemId) {
                R.id.nav_dashboard ->{
                    setFragment(homeFragment)
                title="Dashboard"
                }
                R.id.nav_report ->{
                    setFragment(Reposts)
                    title="Reports"
                }
                R.id.nav_vehicles ->{
                    setFragment(vehiclelist)
                    title="Vehicle"
                }
                R.id.nav_profile->{
                    setFragment(profileFragment)
                    title="Profile"
                }
            }
            true
        }
        binding.btnFloated.setOnClickListener {
            bottomDialog()
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ic_logout,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id==android.R.id.home){
            setFragment(homeFragment)
            onBackPressed()
        }
        if(id == R.id.logout){
           showdialoag()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setFragment(fragment: Fragment) {
        val FragmentManager: FragmentManager
        FragmentManager = supportFragmentManager
        FragmentManager.beginTransaction().replace(R.id.main_container, fragment, "Fragment")
            .commit()
    }


    override fun onStart() {
        Log.d("Testing","Onstart")
        super.onStart()
    }

    override fun onDestroy() {
        Log.d("Testing","OnDestroy")
        super.onDestroy()
    }
    private fun logout(){
        manager?.setLogin(false)
        val intent= Intent(this,Loginpage::class.java)
        startActivity(intent)
        Toasty.error(this,"Logout",Toasty.LENGTH_SHORT).show()
        finish()
    }
    private fun showdialoag(){
        val dialog= AlertDialog.Builder(this)
        dialog.setTitle("Logout")
        dialog.setMessage("Do you want to exit?")
        dialog.setPositiveButton("Yes"){  DialogInterface, i ->
            logout()
        }
        dialog.setNegativeButton("Cancel"){ DialogInterface, i ->
            DialogInterface.dismiss()
        }
        val alertdialog = dialog.create()
        alertdialog.show()
    }

    override fun onBackPressed() {
        if (backPressCount == 1){
                backPressCount = 2
                val toasty = Toasty.info(this, "Press back again to exit!", Toasty.LENGTH_SHORT)
                toasty.show()
                Handler().postDelayed({
                    toasty.cancel()
                    backPressCount = 1
                }, 1000)
            } else {
                backPressCount = 1
                super.onBackPressed()
            }
    }
    @SuppressLint("SuspiciousIndentation")
    fun bottomDialog(){
        bottDialog= BottomDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bottDialog.root)
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.dialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        bottDialog.dialogFire.setOnClickListener{
            val intent = Intent(this, AddNewTrip::class.java)
                intent.putExtra("triptype", "business")
                startActivity(intent)
            dialog.dismiss()
        }
        bottDialog.dialogGolive.setOnClickListener {
            val intent = Intent(this, AddFuelActivity::class.java)
                startActivity(intent)
            dialog.dismiss()
        }

    }
//    fun setUpBottomNavigationView() {
//        val sharedpref= SharedPrefs.getString(this ,SELECTED_GROUP_NAME)
//        val getprojectLabel = SharedPrefs.getString(this, SELECTED_GROUP_LABEL)
//        binding.bottomNavigation1.menu.findItem(R.id.nav_vehicles).title = "Vehicle"
//        if (sharedpref != null) {
//            if (sharedpref.trim().isNotEmpty() && sharedpref!=""){
//                if (!sharedpref.equals("")) {
//                    binding.bottomNavigation1.menu.findItem(R.id.nav_vehicles).title = sharedpref
//                } else {
//                    binding.bottomNavigation1.menu.findItem(R.id.nav_vehicles).title = getprojectLabel
//                }
//            } else {
//                binding.bottomNavigation1.menu.findItem(R.id.nav_vehicles).title = getprojectLabel
//            }
//        }
//    }

    fun setUpBottomNavigationView() {
        val sharedpref = PubVar.Trip_type
        val getprojectLabel = SharedPrefs.getString(this, SELECED_VEHICLE_NAME)
        binding.bottomNavigation1.menu.findItem(R.id.nav_vehicles).title = sharedpref.takeIf { it.trim().isNotEmpty() } ?:
        getprojectLabel?.takeIf { it.trim().isNotEmpty() } ?: "Vehicle"
    }


}








