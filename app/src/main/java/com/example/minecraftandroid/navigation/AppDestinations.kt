package com.abdullahnadeem.minecraftandroid.navigation

import android.net.Uri

object AppDestinations {
    const val CategoriesRoute = "categories"

    // Category detail destination
    const val CategoryIdArg = "categoryId"
    const val CategoryDetailRoute = "category/{$CategoryIdArg}"

    fun categoryDetailRoute(categoryId: String): String =
        "category/${Uri.encode(categoryId)}"

    // Video playback placeholder destination (replaced by real player in Phase 4)
    const val VideoUrlArg = "videoUrl"
    const val VideoPlaceholderRoute = "video/{$VideoUrlArg}"

    fun videoPlaceholderRoute(youtubeUrl: String): String =
        "video/${Uri.encode(youtubeUrl)}"
}