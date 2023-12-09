package com.cmtech.amslogbook.global

import android.annotation.SuppressLint
import android.database.Cursor
import java.text.SimpleDateFormat
import java.util.*

class PubFun {

    companion object {

        @SuppressLint("SimpleDateFormat")
        fun getUTCDateTime(): String {
            val f = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            f.timeZone = TimeZone.getTimeZone("UTC")
            println(f.format(Date()))
            return f.format(Date())
        }
        fun isCursorNULL(TempCursor: Cursor?, FldName: String, DefaultValue: String): String {
            try {
                if (TempCursor == null){
                    return DefaultValue
                } else {
                    val col = TempCursor.getColumnIndex(FldName)
                    val Value=
                        if (TempCursor.getString(col) == null) DefaultValue else TempCursor.getString(
                            col
                        ).trim { it <=' '}
                    return if (Value.length == 0)
                        DefaultValue
                    else
                        Value
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return DefaultValue
            }

        }
       @SuppressLint("SimpleDateFormat")
       fun returnsqlformat(date:String):String{
           try {
               if(date.trim().isNotEmpty()){
                   val format= SimpleDateFormat("dd/MM/yyyy")
                   val form=format.parse(date)
                   val format1=SimpleDateFormat("yyyy-MM-dd")
                   return format1.format(form!!)
               }

           }catch (e:Exception){
               e.printStackTrace()
           }
           return ""
       }

        @SuppressLint("SimpleDateFormat")
        fun returnuserformat(date:String):String{
            try {
                if(date.trim().isNotEmpty()){
                    val format=SimpleDateFormat("yyyy-MM-dd")
                    val form=format.parse(date)
                    val format2=SimpleDateFormat("dd/MM/yyyy")
                    return format2.format(form!!)
                }

            }catch (e:Exception){
                e.printStackTrace()
            }
            return " "
        }
    }

}