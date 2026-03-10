package com.abdullahnadeem.minecraftandroid.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abdullahnadeem.minecraftandroid.R
import com.abdullahnadeem.minecraftandroid.ui.categories.CategoriesScreen
import com.abdullahnadeem.minecraftandroid.ui.categories.CategoriesUiState

@Composable
fun AppNavHost(
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
                    navController.navigate(AppDestinations.categoryPlaceholderRoute(categoryId))
                }
            )
        }

        composable(
            route = AppDestinations.CategoryPlaceholderRoute,
            arguments = listOf(
                navArgument(AppDestinations.CategoryIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString(AppDestinations.CategoryIdArg).orEmpty()
            val categoryTitle = (categoriesUiState as? CategoriesUiState.Success)
                ?.categories
                ?.firstOrNull { it.id == categoryId }
                ?.title
                ?: categoryId

            CategoryPlaceholderScreen(
                categoryTitle = categoryTitle,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun CategoryPlaceholderScreen(
    categoryTitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = categoryTitle) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = stringResource(R.string.navigation_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.category_placeholder_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.category_placeholder_message, categoryTitle),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
            Button(
                onClick = onBack,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(text = stringResource(R.string.navigation_back_to_categories))
            }
        }
    }
}