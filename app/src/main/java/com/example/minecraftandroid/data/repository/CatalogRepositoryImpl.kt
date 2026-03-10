package com.abdullahnadeem.minecraftandroid.data.repository

import android.util.Log
import com.abdullahnadeem.minecraftandroid.data.mapper.parseCatalogPayload
import com.abdullahnadeem.minecraftandroid.data.mapper.toDomainCategories
import com.abdullahnadeem.minecraftandroid.data.remote.CatalogApiService
import com.abdullahnadeem.minecraftandroid.domain.model.Category
import java.io.IOException

class CatalogRepositoryImpl(
    private val apiService: CatalogApiService,
    private val fallbackPayload: String? = null
) : CatalogRepository {

    private companion object {
        const val TAG = "CatalogRepo"
    }

    private var cachedCategories: List<Category> = emptyList()

    override suspend fun getCatalog(forceRefresh: Boolean): List<Category> {
        if (cachedCategories.isNotEmpty() && !forceRefresh) {
            return cachedCategories
        }

        val payload = fetchCatalogPayload()

        val categories = parseCatalogPayload(payload).toDomainCategories()
        Log.d(TAG, "Parsed ${categories.size} categories")
        categories.forEach { cat ->
            Log.d(TAG, "  Category '${cat.title}' — ${cat.videos.size} videos")
        }
        if (categories.isEmpty()) {
            throw IOException("Catalog payload parsed successfully but did not contain any categories or videos.")
        }

        cachedCategories = categories
        return categories
    }

    override suspend fun getCategoryById(id: String): Category? {
        return getCatalog(forceRefresh = false).firstOrNull { it.id == id }
    }

    private suspend fun fetchCatalogPayload(): String {
        val response = runCatching { apiService.getCatalog() }.getOrElse { throwable ->
            Log.w(TAG, "Network request threw exception, using fallback. Cause: ${throwable.javaClass.simpleName}: ${throwable.message}")
            return fallbackPayload?.takeIf { it.isNotBlank() }
                ?: throw IOException(
                    "Catalog network request failed and no fallback data is available.",
                    throwable
                )
        }

        if (response.isSuccessful) {
            Log.d(TAG, "HTTP ${response.code()} — body length=${response.body()?.length ?: 0}")
            val preview = response.body()?.take(500) ?: "(null)"
            Log.d(TAG, "Payload preview: $preview")
            return response.body()?.takeIf { it.isNotBlank() }
                ?: fallbackPayload?.takeIf { it.isNotBlank() }
                ?: throw IOException("Catalog response body was empty.")
        }

        Log.w(TAG, "HTTP ${response.code()} ${response.message()} — falling back")
        return fallbackPayload?.takeIf { it.isNotBlank() }
            ?: throw IOException("Catalog request failed with HTTP ${response.code()} ${response.message()}.")
    }
}