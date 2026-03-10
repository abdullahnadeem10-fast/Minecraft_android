package com.abdullahnadeem.minecraftandroid.domain.model

data class Video(
    val id: String,
    val title: String,
    val description: String?,
    val thumbnailUrl: String?,
    val youtubeUrl: String?,
    val categoryId: String,
    val categoryTitle: String,
    val durationText: String?
)