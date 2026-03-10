package com.abdullahnadeem.minecraftandroid.navigation

import android.net.Uri

object AppDestinations {
    const val CategoriesRoute = "categories"

    // Category detail destination
    const val CategoryIdArg = "categoryId"
    const val CategoryDetailRoute = "category/{$CategoryIdArg}"

    fun categoryDetailRoute(categoryId: String): String =
        "category/${Uri.encode(categoryId)}"

    // Video player destination
    const val VideoUrlArg = "videoUrl"
    const val VideoPlayerRoute = "player/{$VideoUrlArg}"

    fun videoPlayerRoute(youtubeUrl: String): String =
        "player/${Uri.encode(youtubeUrl)}"
}