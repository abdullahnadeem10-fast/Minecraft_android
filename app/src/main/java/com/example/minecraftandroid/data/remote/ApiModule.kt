package com.abdullahnadeem.minecraftandroid.data.remote

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object ApiModule {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private fun createClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun createRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(createClient())
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val catalogApiService: CatalogApiService by lazy {
        createRetrofit(baseUrl = "https://d37oz74k5exkdg.cloudfront.net/")
            .create(CatalogApiService::class.java)
    }

    val playbackApiService: PlaybackApiService by lazy {
        createRetrofit(baseUrl = "http://16.170.165.189:8001/")
            .create(PlaybackApiService::class.java)
    }
}