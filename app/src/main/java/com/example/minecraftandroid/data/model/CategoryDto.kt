package com.abdullahnadeem.minecraftandroid.data.model

data class CategoryDto(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val videos: List<VideoDto>
)