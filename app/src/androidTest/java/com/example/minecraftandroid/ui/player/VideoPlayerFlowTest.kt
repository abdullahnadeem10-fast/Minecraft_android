package com.abdullahnadeem.minecraftandroid.ui.player

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abdullahnadeem.minecraftandroid.data.repository.PlaybackRepository
import com.abdullahnadeem.minecraftandroid.ui.theme.MinecraftAndroidTheme
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoPlayerFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun videoPlayerScreen_showsLoadingWhileResolving() {
        val fakeRepo = object : PlaybackRepository {
            override suspend fun resolvePlaybackUrl(youtubeUrl: String): String {
                return CompletableDeferred<String>().await()
            }
        }

        composeRule.setContent {
            MinecraftAndroidTheme {
                VideoPlayerScreen(
                    youtubeUrl = "https://youtu.be/test",
                    videoTitle = "Test Video",
                    playbackRepository = fakeRepo,
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Loading video…").assertIsDisplayed()
    }

    @Test
    fun videoPlayerScreen_showsErrorOnFailure() {
        val fakeRepo = object : PlaybackRepository {
            override suspend fun resolvePlaybackUrl(youtubeUrl: String): String {
                throw IOException("Resolver unavailable")
            }
        }

        composeRule.setContent {
            MinecraftAndroidTheme {
                VideoPlayerScreen(
                    youtubeUrl = "https://youtu.be/test",
                    videoTitle = "Test Video",
                    playbackRepository = fakeRepo,
                    onBack = {}
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Resolver unavailable").assertIsDisplayed()
    }

    @Test
    fun videoPlayerScreen_showsRetryButtonOnError() {
        val fakeRepo = object : PlaybackRepository {
            override suspend fun resolvePlaybackUrl(youtubeUrl: String): String {
                throw IOException("Network error")
            }
        }

        composeRule.setContent {
            MinecraftAndroidTheme {
                VideoPlayerScreen(
                    youtubeUrl = "https://youtu.be/test",
                    videoTitle = "Test Video",
                    playbackRepository = fakeRepo,
                    onBack = {}
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}
