package com.cmtech.amslogbook.activity

import androidx.appcompat.app.AppCompatActivity
import com.cmtech.amslogbook.widget.ProgressView

abstract class BaseActivity : AppCompatActivity() {

    private val progressView : ProgressView get() = ProgressView(this)

    fun showProgress(){
        progressView.setCancelable(false)
        progressView.show()
    }

    fun dismissProgress(){
        progressView.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgress()
    }

}