package com.cmtech.amslogbook.Retrofit

import com.cmtech.amslogbook.model.car_list
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface Retrofit {
    @GET("http://192.168.1.16/student/test.php")
    suspend fun getRequest():retrofit2.Response<car_list>

    @FormUrlEncoded
    @POST("")
    suspend fun postRequest():retrofit2.Response<car_list>
}