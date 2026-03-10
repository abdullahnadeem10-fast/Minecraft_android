package com.abdullahnadeem.minecraftandroid.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaybackResponseDto(
    @SerialName("url") val url: String? = null
)