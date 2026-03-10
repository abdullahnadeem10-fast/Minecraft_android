package com.abdullahnadeem.minecraftandroid.data.repository

interface PlaybackRepository {

    suspend fun resolvePlaybackUrl(youtubeUrl: String): String
}