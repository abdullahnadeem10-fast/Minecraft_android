package com.abdullahnadeem.minecraftandroid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abdullahnadeem.minecraftandroid.data.repository.CatalogRepository
import com.abdullahnadeem.minecraftandroid.data.repository.PlaybackRepository
import com.abdullahnadeem.minecraftandroid.navigation.AppNavHost
import com.abdullahnadeem.minecraftandroid.ui.catalog.CatalogViewModel
import com.abdullahnadeem.minecraftandroid.ui.catalog.CatalogViewModelFactory
import com.abdullahnadeem.minecraftandroid.ui.theme.MinecraftAndroidTheme

@Composable
fun MinecraftVideosApp(
    catalogRepository: CatalogRepository,
    playbackRepository: PlaybackRepository,
    modifier: Modifier = Modifier
) {
    MinecraftAndroidTheme {
        val viewModel: CatalogViewModel = viewModel(
            factory = remember(catalogRepository) {
                CatalogViewModelFactory(catalogRepository)
            }
        )
        val categoriesUiState by viewModel.categoriesUiState.collectAsStateWithLifecycle()

        AppNavHost(
            catalogRepository = catalogRepository,
            playbackRepository = playbackRepository,
            categoriesUiState = categoriesUiState,
            onRetry = viewModel::refresh,
            modifier = modifier
        )
    }
}