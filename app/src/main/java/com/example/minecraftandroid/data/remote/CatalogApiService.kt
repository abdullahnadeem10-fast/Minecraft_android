package com.abdullahnadeem.minecraftandroid.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface CatalogApiService {

    @GET("minecraft_videos.json")
    suspend fun getCatalog(
        @Header("User-Agent") userAgent: String = "MinecraftAndroid/Phase1",
        @Header("Accept") accept: String = "application/json,text/plain,*/*"
    ): Response<String>
}