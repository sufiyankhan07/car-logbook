package com.cmtech.amslogbook.model

data class Fuel_list(
    val ID :String? = "",
    val VEHICLE_NAME:String?="",
    val REGISTRATION_NO :String?="",
    val FUEL:String?="",
    val AMOUNT:String?="",
    val FUEL_FILLING_DATE:String?="",
    val LOCATION:String?="",
    val TYPE_OF_EXPENSE:String?="",
    val NOTE:String?="",
    val PARKING:String?="",
    val OIL_AMOUNT:String?="",
    val CAR_WASH_AMOUNT:String?="",
    val REPAIR_AMOUNT:String?="",
    val TIRES_AMOUNT:String?="",
    val TOLL_AMOUNT:String?="",
    val ODOMETER:String?=""
)