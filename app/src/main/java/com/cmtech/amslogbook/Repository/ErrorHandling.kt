package com.cmtech.amslogbook.Repository

sealed class ErrorHandling<T>(val data:T?=null,val error:String?=null) {
 class Loading<T>():ErrorHandling<T>()
    class Success<T>(data: T? =null):ErrorHandling<T>(data = data )
    class Error<T>(error: String?=null):ErrorHandling<T>(error = error)
}