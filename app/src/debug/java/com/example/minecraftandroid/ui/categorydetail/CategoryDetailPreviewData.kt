package com.abdullahnadeem.minecraftandroid.ui.categorydetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.abdullahnadeem.minecraftandroid.domain.model.Category
import com.abdullahnadeem.minecraftandroid.domain.model.Video
import com.abdullahnadeem.minecraftandroid.ui.theme.MinecraftAndroidTheme

internal val PreviewCategory: Category = Category(
    id = "survival",
    title = "Survival Basics",
    thumbnailUrl = "https://example.com/survival-basics.jpg",
    videos = listOf(
        Video(
            id = "survival-1",
            title = "Punching Trees and Crafting Tools",
            description = null,
            thumbnailUrl = null,
            youtubeUrl = "https://www.youtube.com/watch?v=survival-1",
            categoryId = "survival",
            categoryTitle = "Survival Basics",
            durationText = "9:12"
        ),
        Video(
            id = "survival-2",
            title = "First Night Shelter in 5 Minutes",
            description = null,
            thumbnailUrl = null,
            youtubeUrl = "https://www.youtube.com/watch?v=survival-2",
            categoryId = "survival",
            categoryTitle = "Survival Basics",
            durationText = "7:45"
        ),
        Video(
            id = "survival-3",
            title = "Early Food and Iron Route",
            description = null,
            thumbnailUrl = null,
            youtubeUrl = "https://www.youtube.com/watch?v=survival-3",
            categoryId = "survival",
            categoryTitle = "Survival Basics",
            durationText = "11:28"
        )
    )
)

@Preview(showBackground = true)
@Composable
private fun CategoryDetailSuccessPreview() {
    MinecraftAndroidTheme {
        CategoryDetailScreen(
            uiState = CategoryDetailUiState.Success(PreviewCategory),
            onBack = {},
            onVideoClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryDetailLoadingPreview() {
    MinecraftAndroidTheme {
        CategoryDetailScreen(
            uiState = CategoryDetailUiState.Loading,
            onBack = {},
            onVideoClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryDetailEmptyPreview() {
    MinecraftAndroidTheme {
        CategoryDetailScreen(
            uiState = CategoryDetailUiState.Success(
                PreviewCategory.copy(
                    id = "survival-empty",
                    title = "Survival Basics",
                    videos = emptyList()
                )
            ),
            onBack = {},
            onVideoClick = {}
        )
    }
}
