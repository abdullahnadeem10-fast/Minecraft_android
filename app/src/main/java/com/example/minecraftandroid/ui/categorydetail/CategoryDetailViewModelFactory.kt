package com.abdullahnadeem.minecraftandroid.ui.categorydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.abdullahnadeem.minecraftandroid.data.repository.CatalogRepository

class CategoryDetailViewModelFactory(
    private val repository: CatalogRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(CategoryDetailViewModel::class.java)) {
            val savedStateHandle: SavedStateHandle = extras.createSavedStateHandle()

            @Suppress("UNCHECKED_CAST")
            return CategoryDetailViewModel(repository, savedStateHandle) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}