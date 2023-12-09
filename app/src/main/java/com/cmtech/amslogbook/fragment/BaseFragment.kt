package com.cmtech.amslogbook.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

import com.cmtech.amslogbook.widget.ProgressView

abstract class BaseFragment : Fragment() {
     var mProgres:ProgressView?=null
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgres= ProgressView(activity!!)

        fun showProgress(){
            mProgres?.setCancelable(false)
            mProgres?.show()
        }

        fun dismissProgress() {
            mProgres?.dismiss()
        }

    }
    private fun onDistory(){
        mProgres?.dismiss()
    }

}