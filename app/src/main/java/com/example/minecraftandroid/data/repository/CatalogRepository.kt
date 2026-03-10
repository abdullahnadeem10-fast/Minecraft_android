package com.abdullahnadeem.minecraftandroid.data.repository

import com.abdullahnadeem.minecraftandroid.domain.model.Category

interface CatalogRepository {

    suspend fun getCatalog(forceRefresh: Boolean = false): List<Category>
}