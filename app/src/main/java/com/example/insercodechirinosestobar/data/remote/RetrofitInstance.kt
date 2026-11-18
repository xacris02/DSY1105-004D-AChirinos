package com.example.insercodechirinosestobar.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton con la configuración de Retrofit
object RetrofitInstance {

    // Retrofit exige que la URL base termine en "/"
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // Se instancia el servicio una sola vez
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
