package com.cmtech.amslogbook.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Response {
    const val BASE_URL=""

    fun getresponse():Retrofit{
        val reponseRto= Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return reponseRto
    }

}