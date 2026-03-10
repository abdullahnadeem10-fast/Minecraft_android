package com.abdullahnadeem.minecraftandroid.ui.categorydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdullahnadeem.minecraftandroid.data.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryDetailViewModel(
    private val repository: CatalogRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: String? = savedStateHandle["categoryId"]

    private val _uiState = MutableStateFlow<CategoryDetailUiState>(CategoryDetailUiState.Loading)
    val uiState: StateFlow<CategoryDetailUiState> = _uiState.asStateFlow()

    init {
        loadCategory()
    }

    fun retry() {
        loadCategory()
    }

    private fun loadCategory() {
        viewModelScope.launch {
            val resolvedCategoryId = categoryId
            if (resolvedCategoryId.isNullOrBlank()) {
                _uiState.value = CategoryDetailUiState.CategoryNotFound
                return@launch
            }

            _uiState.value = CategoryDetailUiState.Loading
            runCatching {
                repository.getCategoryById(resolvedCategoryId)
            }.onSuccess { category ->
                _uiState.value = if (category != null) {
                    CategoryDetailUiState.Success(category)
                } else {
                    CategoryDetailUiState.CategoryNotFound
                }
            }.onFailure { throwable ->
                _uiState.value = CategoryDetailUiState.Error(
                    message = throwable.message ?: "Unknown category detail error."
                )
            }
        }
    }
}