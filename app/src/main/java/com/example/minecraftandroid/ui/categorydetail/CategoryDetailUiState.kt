package com.abdullahnadeem.minecraftandroid.ui.categorydetail

import com.abdullahnadeem.minecraftandroid.domain.model.Category

sealed interface CategoryDetailUiState {
    data object Loading : CategoryDetailUiState
    data object CategoryNotFound : CategoryDetailUiState
    data class Error(val message: String) : CategoryDetailUiState
    data class Success(val category: Category) : CategoryDetailUiState
}