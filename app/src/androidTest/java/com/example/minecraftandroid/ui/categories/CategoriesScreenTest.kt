package com.abdullahnadeem.minecraftandroid.ui.categories

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
class CategoriesScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun categoriesScreen_displaysCategoryTitles() {
        composeRule.setContent {
            MinecraftAndroidTheme {
                CategoriesScreen(
                    uiState = CategoriesUiState.Success(sampleCategories()),
                    onRetry = {},
                    onCategoryClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Survival Basics").assertIsDisplayed()
        composeRule.onNodeWithText("Creative Builds").assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_invokesCallbackWhenCategoryTapped() {
        var selectedCategoryId = ""

        composeRule.setContent {
            MinecraftAndroidTheme {
                CategoriesScreen(
                    uiState = CategoriesUiState.Success(sampleCategories()),
                    onRetry = {},
                    onCategoryClick = { selectedCategoryId = it }
                )
            }
        }

        composeRule.onNodeWithText("Survival Basics").performClick()
        composeRule.runOnIdle {
            assertEquals("survival", selectedCategoryId)
        }
    }

    private fun sampleCategories(): List<Category> {
        return listOf(
            Category(
                id = "survival",
                title = "Survival Basics",
                thumbnailUrl = null,
                videos = listOf(
                    Video(
                        id = "video-1",
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
                        id = "video-2",
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
    }
}