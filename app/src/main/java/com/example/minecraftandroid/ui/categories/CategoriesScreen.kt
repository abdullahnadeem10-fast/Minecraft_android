package com.abdullahnadeem.minecraftandroid.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abdullahnadeem.minecraftandroid.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    uiState: CategoriesUiState,
    onRetry: () -> Unit,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.categories_title)) }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            CategoriesUiState.Loading -> CategoriesLoadingState(modifier = Modifier.padding(innerPadding))
            CategoriesUiState.Empty -> CategoriesMessageState(
                message = stringResource(R.string.categories_empty),
                actionLabel = stringResource(R.string.catalog_retry),
                onAction = onRetry,
                modifier = Modifier.padding(innerPadding)
            )
            is CategoriesUiState.Error -> CategoriesMessageState(
                message = uiState.message,
                actionLabel = stringResource(R.string.catalog_retry),
                onAction = onRetry,
                modifier = Modifier.padding(innerPadding)
            )
            is CategoriesUiState.Success -> CategoriesGrid(
                uiState = uiState,
                onCategoryClick = onCategoryClick,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    end = 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                )
            )
        }
    }
}

@Composable
private fun CategoriesLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.categories_loading),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun CategoriesMessageState(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge)
            Button(
                onClick = onAction,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = actionLabel)
            }
        }
    }
}

@Composable
private fun CategoriesGrid(
    uiState: CategoriesUiState.Success,
    onCategoryClick: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = uiState.categories,
            key = { category -> category.id }
        ) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}
