package com.cmtech.amslogbook.model

data class Trip_list(
    val id:String = "",
    val trip_type :String = "",
    val vehicle :String = "",
    val odometer_start :String = "",
    val odometer_end :String = "",
    val start_date :String = "",
    val end_date :String = "",
    val start_time :String = "",
    val end_time :String = "",
    val start_address :String = "",
    val end_address :String = "",
    val note :String = "",
    val registration_no : String = "",
    val distance:String = ""
)