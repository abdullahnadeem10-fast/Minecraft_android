package com.abdullahnadeem.minecraftandroid.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.abdullahnadeem.minecraftandroid.R
import com.abdullahnadeem.minecraftandroid.domain.model.Category

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            if (category.thumbnailUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.25f)
                        .clip(CardDefaults.shape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.category_thumbnail_placeholder),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                AsyncImage(
                    model = category.thumbnailUrl,
                    contentDescription = stringResource(R.string.category_thumbnail_content_description, category.title),
                    contentScale = ContentScale.Crop,
                    error = ColorPainter(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                    placeholder = ColorPainter(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.25f)
                )
            }

            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}