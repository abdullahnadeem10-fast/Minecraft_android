package com.abdullahnadeem.minecraftandroid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abdullahnadeem.minecraftandroid.data.repository.CatalogRepository
import com.abdullahnadeem.minecraftandroid.data.repository.PlaybackRepository
import com.abdullahnadeem.minecraftandroid.ui.catalog.CatalogDebugScreen
import com.abdullahnadeem.minecraftandroid.ui.catalog.CatalogViewModel
import com.abdullahnadeem.minecraftandroid.ui.catalog.CatalogViewModelFactory
import com.abdullahnadeem.minecraftandroid.ui.theme.MinecraftAndroidTheme

private const val CatalogDebugRoute = "catalog_debug"

@Composable
fun MinecraftVideosApp(
    catalogRepository: CatalogRepository,
    playbackRepository: PlaybackRepository,
    modifier: Modifier = Modifier
) {
    MinecraftAndroidTheme {
        val navController = rememberNavController()
        val viewModel: CatalogViewModel = viewModel(
            factory = remember(catalogRepository) {
                CatalogViewModelFactory(catalogRepository)
            }
        )
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val playbackResolverReady = remember(playbackRepository) { true }

        NavHost(
            navController = navController,
            startDestination = CatalogDebugRoute,
            modifier = modifier
        ) {
            composable(CatalogDebugRoute) {
                CatalogDebugScreen(
                    uiState = uiState,
                    playbackResolverReady = playbackResolverReady,
                    onRetry = viewModel::refresh
                )
            }
        }
    }
}