package com.abdullahnadeem.minecraftandroid.ui.catalog

import com.abdullahnadeem.minecraftandroid.domain.model.Category

sealed interface CatalogUiState {
    data object Loading : CatalogUiState

    data object Empty : CatalogUiState

    data class Success(
        val categories: List<Category>
    ) : CatalogUiState {
        val totalVideos: Int = categories.sumOf { it.videos.size }
    }

    data class Error(
        val message: String
    ) : CatalogUiState
}