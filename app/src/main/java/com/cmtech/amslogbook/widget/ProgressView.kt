package com.cmtech.amslogbook.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import com.cmtech.amslogbook.R

class ProgressView(context: Context) : Dialog(context) {

    private var mMessage : String? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val message=findViewById<TextView>(R.id.message)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.progress_view)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (mMessage!=null)
        message.text = mMessage
    }

    fun setMessage(text: String) : ProgressView{
        this.mMessage = text
        return this
    }

}
