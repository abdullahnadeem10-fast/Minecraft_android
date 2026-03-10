package com.abdullahnadeem.minecraftandroid.data.repository

import com.abdullahnadeem.minecraftandroid.data.model.PlaybackResponseDto
import com.abdullahnadeem.minecraftandroid.data.remote.PlaybackApiService
import java.io.IOException
import kotlinx.serialization.json.Json

class PlaybackRepositoryImpl(
    private val apiService: PlaybackApiService,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }
) : PlaybackRepository {

    override suspend fun resolvePlaybackUrl(youtubeUrl: String): String {
        val response = apiService.resolvePlaybackUrl(youtubeUrl)
        if (!response.isSuccessful) {
            throw IOException("Playback resolver failed with HTTP ${response.code()} ${response.message()}.")
        }

        val payload = response.body()?.takeIf { it.isNotBlank() }
            ?: throw IOException("Playback resolver returned an empty body.")
        val dto = json.decodeFromString<PlaybackResponseDto>(payload)
        return dto.tag?.url ?: throw IOException("Playback resolver response did not include tag.url.")
    }
}