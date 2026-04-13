package com.example.cuidacultivo.data.remote

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    //private const val BASE_URL = "http://157.137.215.14:5039/"
     //private const val BASE_URL = "http://127.0.0.1:5000/"

    //mi pc 192.168.137.64
    private const val BASE_URL = "http://192.168.137.64:5000/"
    // 🔥 NUEVO → función que recibe context
    fun getService(context: Context): ApiService {

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // 🔐 token automático
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // 🔥 importante
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}