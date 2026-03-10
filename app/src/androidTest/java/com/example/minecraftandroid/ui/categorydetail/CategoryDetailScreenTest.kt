package com.abdullahnadeem.minecraftandroid.ui.categorydetail

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abdullahnadeem.minecraftandroid.domain.model.Category
import com.abdullahnadeem.minecraftandroid.domain.model.Video
import com.abdullahnadeem.minecraftandroid.ui.theme.MinecraftAndroidTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDetailScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun categoryDetailScreen_successState_showsCategoryTitle() {
        composeRule.setContent {
            MinecraftAndroidTheme {
                CategoryDetailScreen(
                    uiState = CategoryDetailUiState.Success(sampleCategory()),
                    onBack = {},
                    onVideoClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Survival Basics").assertIsDisplayed()
    }

    @Test
    fun categoryDetailScreen_successState_showsVideoTitles() {
        composeRule.setContent {
            MinecraftAndroidTheme {
                CategoryDetailScreen(
                    uiState = CategoryDetailUiState.Success(sampleCategory()),
                    onBack = {},
                    onVideoClick = {}
                )
            }
        }

        composeRule.onNodeWithText("First Night Guide").assertIsDisplayed()
        composeRule.onNodeWithText("Starter House Blueprint").assertIsDisplayed()
    }

    @Test
    fun categoryDetailScreen_loadingState_showsProgress() {
        composeRule.setContent {
            MinecraftAndroidTheme {
                CategoryDetailScreen(
                    uiState = CategoryDetailUiState.Loading,
                    onBack = {},
                    onVideoClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Loading category…").assertIsDisplayed()
    }

    @Test
    fun categoryDetailScreen_backCallback_invokedOnBackPress() {
        var backPressCount = 0

        composeRule.setContent {
            MinecraftAndroidTheme {
                CategoryDetailScreen(
                    uiState = CategoryDetailUiState.Success(sampleCategory()),
                    onBack = { backPressCount += 1 },
                    onVideoClick = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.runOnIdle {
            assertEquals(1, backPressCount)
        }
    }

    private fun sampleCategory(): Category {
        return Category(
            id = "survival",
            title = "Survival Basics",
            thumbnailUrl = null,
            videos = listOf(
                Video(
                    id = "video-1",
                    title = "First Night Guide",
                    description = null,
                    thumbnailUrl = null,
                    youtubeUrl = "https://www.youtube.com/watch?v=video-1",
                    categoryId = "survival",
                    categoryTitle = "Survival Basics",
                    durationText = "8:34"
                ),
                Video(
                    id = "video-2",
                    title = "Starter House Blueprint",
                    description = null,
                    thumbnailUrl = null,
                    youtubeUrl = "https://www.youtube.com/watch?v=video-2",
                    categoryId = "survival",
                    categoryTitle = "Survival Basics",
                    durationText = "10:02"
                )
            )
        )
    }
}
