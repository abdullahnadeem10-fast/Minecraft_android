package com.abdullahnadeem.minecraftandroid.domain.model

data class Category(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val videos: List<Video>
)