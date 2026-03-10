package com.abdullahnadeem.minecraftandroid.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.abdullahnadeem.minecraftandroid.R
import com.abdullahnadeem.minecraftandroid.domain.model.Category

@Composable
fun CatalogDebugScreen(
    uiState: CatalogUiState,
    playbackResolverReady: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = stringResource(R.string.catalog_debug_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.catalog_endpoint_note),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    ) { innerPadding ->
        when (uiState) {
            CatalogUiState.Loading -> LoadingState(modifier = Modifier.padding(innerPadding))
            CatalogUiState.Empty -> MessageState(
                message = stringResource(R.string.catalog_empty),
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )
            is CatalogUiState.Error -> MessageState(
                message = uiState.message,
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )
            is CatalogUiState.Success -> SuccessState(
                categories = uiState.categories,
                totalVideos = uiState.totalVideos,
                playbackResolverReady = playbackResolverReady,
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.catalog_loading),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun MessageState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
                Text(text = stringResource(R.string.catalog_retry))
            }
        }
    }
}

@Composable
private fun SuccessState(
    categories: List<Category>,
    totalVideos: Int,
    playbackResolverReady: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.catalog_status_success, categories.size, totalVideos),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (playbackResolverReady) {
                        Text(
                            text = stringResource(R.string.catalog_playback_ready),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
                        Text(text = stringResource(R.string.catalog_retry))
                    }
                }
            }
        }

        items(categories, key = { it.id }) { category ->
            CategoryCard(category = category)
        }
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (category.thumbnailUrl != null) {
                    AsyncImage(
                        model = category.thumbnailUrl,
                        contentDescription = stringResource(R.string.catalog_thumbnail),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(84.dp)
                            .width(112.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .height(84.dp)
                            .width(112.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${category.videos.size} videos",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    category.videos.take(2).forEach { video ->
                        Text(
                            text = "• ${video.title}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}