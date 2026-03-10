package com.abdullahnadeem.minecraftandroid.ui.categorydetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.abdullahnadeem.minecraftandroid.R
import com.abdullahnadeem.minecraftandroid.domain.model.Category
import com.abdullahnadeem.minecraftandroid.ui.videos.VideoListItem

@Composable
fun CategoryDetailScreen(
    uiState: CategoryDetailUiState,
    onBack: () -> Unit,
    onVideoClick: (youtubeUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        CategoryDetailUiState.Loading -> CategoryDetailLoadingState(modifier = modifier)
        CategoryDetailUiState.CategoryNotFound -> CategoryDetailMessageState(
            message = stringResource(R.string.category_detail_not_found),
            onBack = onBack,
            modifier = modifier
        )
        is CategoryDetailUiState.Error -> CategoryDetailMessageState(
            message = uiState.message.ifBlank { stringResource(R.string.category_detail_error) },
            onBack = onBack,
            modifier = modifier
        )
        is CategoryDetailUiState.Success -> CategoryDetailSuccessState(
            category = uiState.category,
            onBack = onBack,
            onVideoClick = onVideoClick,
            modifier = modifier
        )
    }
}

@Composable
private fun CategoryDetailLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.category_detail_loading),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun CategoryDetailMessageState(
    message: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onBack,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = stringResource(R.string.navigation_back_to_categories))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDetailSuccessState(
    category: Category,
    onBack: () -> Unit,
    onVideoClick: (youtubeUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = category.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.category_detail_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (category.videos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.category_detail_empty_videos),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    end = 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = category.videos,
                    key = { video -> video.id }
                ) { video ->
                    VideoListItem(
                        video = video,
                        onPlayClick = { onVideoClick(video.youtubeUrl.orEmpty()) }
                    )
                }
            }
        }
    }
}