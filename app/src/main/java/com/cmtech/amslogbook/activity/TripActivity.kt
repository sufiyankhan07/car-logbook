package com.cmtech.amslogbook.activity

//import com.cmtech.amslogbook.fragment.Other_expense_fragment
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.ActivityTripBinding
import com.cmtech.amslogbook.fragment.Fuel_list_fragment
import com.cmtech.amslogbook.fragment.Other_expense_fragment
import com.cmtech.amslogbook.fragment.Trip_list_fragment
import com.cmtech.amslogbook.global.PubVar

class TripActivity : AppCompatActivity() {
    lateinit var binding:ActivityTripBinding
    private val tabIcons = intArrayOf(
        R.drawable.ic_tripcar_tab,
        R.drawable.ic_fuel_tab,
        R.drawable.expenses
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Logbook"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0F
        setupViewPager(binding.viewPager)
        binding.tabs.setupWithViewPager(binding.viewPager)
        settabicon()
    }

    fun settabicon(){
        binding.tabs.getTabAt(0)!!.setIcon(tabIcons[0])
        binding.tabs.getTabAt(1)!!.setIcon(tabIcons[1])
        binding.tabs.getTabAt(2)!!.setIcon(tabIcons[2])
    }

    class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val mFragments = ArrayList<Fragment>()
       // private val mFragmentTitles = ArrayList<String>()

        fun addFragment(fragment: Fragment){
            mFragments.add(fragment)
//            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

//        override fun getPageTitle(position: Int): CharSequence? {
//            return mFragmentTitles[position]
//        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(supportFragmentManager)
        adapter.addFragment(Trip_list_fragment())
        adapter.addFragment(Fuel_list_fragment())
        adapter.addFragment(Other_expense_fragment())
        viewPager.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_vehicle,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return false
        } else if (id == R.id.menuadd) {
            when (PubVar.pagevalue) {
                "F" -> {
                    val intent = Intent(applicationContext, AddFuelActivity::class.java)
                    startActivity(intent)
                    return false
                }
                "T" -> {
                    val intent = Intent(applicationContext, AddNewTrip::class.java)
                    startActivity(intent)
                    return false
                }
                "O" -> {
                    Toast.makeText(this, "Not For Expenses", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return true
    }

}
