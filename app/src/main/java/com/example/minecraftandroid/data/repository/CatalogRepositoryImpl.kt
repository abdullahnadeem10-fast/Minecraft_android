package com.abdullahnadeem.minecraftandroid.data.repository

import com.abdullahnadeem.minecraftandroid.data.mapper.parseCatalogPayload
import com.abdullahnadeem.minecraftandroid.data.mapper.toDomainCategories
import com.abdullahnadeem.minecraftandroid.data.remote.CatalogApiService
import com.abdullahnadeem.minecraftandroid.domain.model.Category
import java.io.IOException

class CatalogRepositoryImpl(
    private val apiService: CatalogApiService
) : CatalogRepository {

    private var cachedCategories: List<Category> = emptyList()

    override suspend fun getCatalog(forceRefresh: Boolean): List<Category> {
        if (cachedCategories.isNotEmpty() && !forceRefresh) {
            return cachedCategories
        }

        val response = apiService.getCatalog()
        if (!response.isSuccessful) {
            throw IOException("Catalog request failed with HTTP ${response.code()} ${response.message()}.")
        }

        val payload = response.body()?.takeIf { it.isNotBlank() }
            ?: throw IOException("Catalog response body was empty.")

        val categories = parseCatalogPayload(payload).toDomainCategories()
        if (categories.isEmpty()) {
            throw IOException("Catalog payload parsed successfully but did not contain any categories or videos.")
        }

        cachedCategories = categories
        return categories
    }

    override suspend fun getCategoryById(id: String): Category? {
        return getCatalog(forceRefresh = false).firstOrNull { it.id == id }
    }
}