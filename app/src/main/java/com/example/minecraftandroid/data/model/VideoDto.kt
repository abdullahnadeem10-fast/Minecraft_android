package com.abdullahnadeem.minecraftandroid.data.model

data class VideoDto(
    val id: String,
    val title: String,
    val description: String?,
    val thumbnailUrl: String?,
    val youtubeUrl: String?,
    val categoryId: String,
    val categoryTitle: String,
    val durationText: String?
)