package com.abdullahnadeem.minecraftandroid.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdullahnadeem.minecraftandroid.data.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: CatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading
            runCatching {
                repository.getCatalog(forceRefresh = true)
            }.onSuccess { categories ->
                _uiState.value = if (categories.isEmpty()) {
                    CatalogUiState.Empty
                } else {
                    CatalogUiState.Success(categories)
                }
            }.onFailure { throwable ->
                _uiState.value = CatalogUiState.Error(
                    message = throwable.message ?: "Unknown catalog error."
                )
            }
        }
    }
}