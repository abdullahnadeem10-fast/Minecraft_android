package com.abdullahnadeem.minecraftandroid.ui.categories

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.abdullahnadeem.minecraftandroid.domain.model.Category
import com.abdullahnadeem.minecraftandroid.domain.model.Video
import com.abdullahnadeem.minecraftandroid.ui.theme.MinecraftAndroidTheme

internal val PreviewCategories: List<Category> = listOf(
    Category(
        id = "survival",
        title = "Survival Basics",
        thumbnailUrl = "https://example.com/survival.jpg",
        videos = listOf(
            Video(
                id = "survival-1",
                title = "First Night Guide",
                description = null,
                thumbnailUrl = null,
                youtubeUrl = null,
                categoryId = "survival",
                categoryTitle = "Survival Basics",
                durationText = "8:34"
            )
        )
    ),
    Category(
        id = "builds",
        title = "Creative Builds",
        thumbnailUrl = null,
        videos = listOf(
            Video(
                id = "builds-1",
                title = "Castle Layout",
                description = null,
                thumbnailUrl = null,
                youtubeUrl = null,
                categoryId = "builds",
                categoryTitle = "Creative Builds",
                durationText = "12:11"
            )
        )
    )
)

@Preview(showBackground = true)
@Composable
private fun CategoriesScreenSuccessPreview() {
    MinecraftAndroidTheme {
        CategoriesScreen(
            uiState = CategoriesUiState.Success(PreviewCategories),
            onRetry = {},
            onCategoryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriesScreenLoadingPreview() {
    MinecraftAndroidTheme {
        CategoriesScreen(
            uiState = CategoriesUiState.Loading,
            onRetry = {},
            onCategoryClick = {}
        )
    }
}
