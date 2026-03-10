package com.abdullahnadeem.minecraftandroid.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaybackApiService {

    @GET("api/youtube/best-audio")
    suspend fun resolvePlaybackUrl(@Query("url") youtubeUrl: String): Response<String>
}