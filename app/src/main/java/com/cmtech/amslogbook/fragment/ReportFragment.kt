package com.cmtech.amslogbook.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.Vehicle_detailActivity
import com.cmtech.amslogbook.databinding.FragmentReportBinding
import com.wojohq.horizontalcalendar.HorizontalCalendar
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment() {
    lateinit var binding:FragmentReportBinding
    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding= FragmentReportBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager(binding.viewpager)
        binding.tabs.setupWithViewPager(binding.viewpager)
    }
    @SuppressLint("SimpleDateFormat")
    private fun validateDates() {
        if (startDate == null && endDate == null) {
            val date = Calendar.getInstance()
            date.set(Calendar.HOUR_OF_DAY, 0)
            date.set(Calendar.MINUTE, 0)
            date.set(Calendar.SECOND, 0)
            date.set(Calendar.MILLISECOND, 0)
            val format = SimpleDateFormat()
            startDate = format.format(date.time)
            date.add(Calendar.HOUR_OF_DAY, 24)
            endDate = format.format(date.time)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter,menu)
        super.onCreateOptionsMenu(menu, inflater)
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
        val adapter =Adapter(childFragmentManager)
        adapter.addFragment(ExpensesFragment(),"Expenses")
        adapter.addFragment(reportTripFragment(),"Trips")
        viewPager.adapter = adapter
    }
}