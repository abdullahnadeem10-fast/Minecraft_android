package com.abdullahnadeem.minecraftandroid.navigation

import android.net.Uri

object AppDestinations {
    const val CategoriesRoute = "categories"
    const val CategoryIdArg = "categoryId"
    const val CategoryPlaceholderRoute = "category/{$CategoryIdArg}"

    fun categoryPlaceholderRoute(categoryId: String): String {
        return "category/${Uri.encode(categoryId)}"
    }
}