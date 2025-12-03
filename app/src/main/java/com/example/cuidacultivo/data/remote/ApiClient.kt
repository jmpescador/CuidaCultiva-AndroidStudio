package com.example.cuidacultivo.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Solo la IP y puerto, termina en /
//    private const val BASE_URL = "http://10.106.43.150:5000/"

    private const val BASE_URL = "http://10.42.0.1:5000/"

    val service: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)  // ✅ Debe terminar en /
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
