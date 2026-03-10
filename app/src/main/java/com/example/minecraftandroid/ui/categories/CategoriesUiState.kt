package com.abdullahnadeem.minecraftandroid.ui.categories

import com.abdullahnadeem.minecraftandroid.domain.model.Category

sealed interface CategoriesUiState {
    data object Loading : CategoriesUiState

    data object Empty : CategoriesUiState

    data class Error(
        val message: String
    ) : CategoriesUiState

    data class Success(
        val categories: List<Category>
    ) : CategoriesUiState
}