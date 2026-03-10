package com.abdullahnadeem.minecraftandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abdullahnadeem.minecraftandroid.data.repository.CatalogRepository
import com.abdullahnadeem.minecraftandroid.ui.categories.CategoriesScreen
import com.abdullahnadeem.minecraftandroid.ui.categories.CategoriesUiState
import com.abdullahnadeem.minecraftandroid.ui.categorydetail.CategoryDetailScreen
import com.abdullahnadeem.minecraftandroid.ui.categorydetail.CategoryDetailViewModel
import com.abdullahnadeem.minecraftandroid.ui.categorydetail.CategoryDetailViewModelFactory
import com.abdullahnadeem.minecraftandroid.ui.videos.VideoPlaybackPlaceholder

@Composable
fun AppNavHost(
    catalogRepository: CatalogRepository,
    categoriesUiState: CategoriesUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.CategoriesRoute,
        modifier = modifier
    ) {
        composable(AppDestinations.CategoriesRoute) {
            CategoriesScreen(
                uiState = categoriesUiState,
                onRetry = onRetry,
                onCategoryClick = { categoryId ->
                    navController.navigate(AppDestinations.categoryDetailRoute(categoryId))
                }
            )
        }

        composable(
            route = AppDestinations.CategoryDetailRoute,
            arguments = listOf(
                navArgument(AppDestinations.CategoryIdArg) {
                    type = NavType.StringType
                }
            )
        ) {
            val viewModel: CategoryDetailViewModel = viewModel(
                factory = CategoryDetailViewModelFactory(catalogRepository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            CategoryDetailScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onVideoClick = { youtubeUrl ->
                    navController.navigate(AppDestinations.videoPlaceholderRoute(youtubeUrl))
                }
            )
        }

        composable(
            route = AppDestinations.VideoPlaceholderRoute,
            arguments = listOf(
                navArgument(AppDestinations.VideoUrlArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val videoUrl = backStackEntry.arguments
                ?.getString(AppDestinations.VideoUrlArg)
                .orEmpty()

            VideoPlaybackPlaceholder(
                videoTitle = videoUrl,
                onBack = { navController.popBackStack() }
            )
        }
    }
}