package com.cmtech.amslogbook.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.ActivityVehicleDetailBinding
import com.cmtech.amslogbook.fragment.Fuel_list_fragment
import com.cmtech.amslogbook.fragment.Vehicle_detailFragment
import com.cmtech.amslogbook.fragment.vehicle_Fuel_list_Fragment
import com.cmtech.amslogbook.global.PubVar
import java.util.ArrayList

class Vehicle_detailActivity : AppCompatActivity() {
    val TAG = "Vehicle_detailActivity"
    lateinit var binding:ActivityVehicleDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVehicleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = PubVar.numberplate
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d(TAG,"numberplate"+PubVar.numberplate)
        setupViewPager(binding.viewPager)
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val mFragments = ArrayList<Fragment>()
        private val mFragmentTitles = ArrayList<String>()

        fun addFragment(fragment: Fragment , title: String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence{
            return mFragmentTitles[position]
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(supportFragmentManager)
        adapter.addFragment(Vehicle_detailFragment(),"Vehicle")
        adapter.addFragment(vehicle_Fuel_list_Fragment(),"Fuel")
        viewPager.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == android.R.id.home){
            onBackPressed()
            return false
        }
        return false
    }

}
