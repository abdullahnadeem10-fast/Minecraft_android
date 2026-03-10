package com.abdullahnadeem.minecraftandroid.ui.catalog

import com.abdullahnadeem.minecraftandroid.domain.model.Category
import com.abdullahnadeem.minecraftandroid.ui.categories.CategoriesUiState

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

fun CatalogUiState.toCategoriesUiState(): CategoriesUiState {
    return when (this) {
        CatalogUiState.Loading -> CategoriesUiState.Loading
        CatalogUiState.Empty -> CategoriesUiState.Empty
        is CatalogUiState.Error -> CategoriesUiState.Error(message = message)
        is CatalogUiState.Success -> CategoriesUiState.Success(categories = categories)
    }
}