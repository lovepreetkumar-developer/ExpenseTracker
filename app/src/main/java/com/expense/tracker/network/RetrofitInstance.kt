package com.expense.tracker.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object RetrofitInstance {
    private const val BASE_URL = "https://v6.exchangerate-api.com/v6/e5ace05ce1297a7969b9a3c2/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ExchangeRateApi = retrofit.create(ExchangeRateApi::class.java)
}

interface ExchangeRateApi {
    @GET("latest/USD")
    suspend fun getExchangeRates(): ExchangeRateResponse
}
