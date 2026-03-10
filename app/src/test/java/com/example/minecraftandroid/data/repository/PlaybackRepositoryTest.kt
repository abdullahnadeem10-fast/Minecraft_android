package com.abdullahnadeem.minecraftandroid.data.repository

import com.abdullahnadeem.minecraftandroid.data.remote.PlaybackApiService
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class PlaybackRepositoryTest {

    private val apiService: PlaybackApiService = mock()
    private val repository = PlaybackRepositoryImpl(apiService)

    @Test
    fun resolvePlaybackUrl_success_returnsTagUrl() = runTest {
        whenever(apiService.resolvePlaybackUrl("https://youtu.be/test"))
            .thenReturn(Response.success("""{"tag":{"url":"https://cdn.example.com/audio.m4a"}}"""))

        val result = repository.resolvePlaybackUrl("https://youtu.be/test")

        assertEquals("https://cdn.example.com/audio.m4a", result)
    }

    @Test
    fun resolvePlaybackUrl_httpError_throwsIOException() = runTest {
        whenever(apiService.resolvePlaybackUrl("https://youtu.be/test"))
            .thenReturn(Response.error(404, "".toResponseBody(null)))

        var thrown: IOException? = null
        try {
            repository.resolvePlaybackUrl("https://youtu.be/test")
        } catch (error: IOException) {
            thrown = error
        }

        assertTrue(thrown != null)
    }

    @Test
    fun resolvePlaybackUrl_missingTagUrl_throwsIOException() = runTest {
        whenever(apiService.resolvePlaybackUrl("https://youtu.be/test"))
            .thenReturn(Response.success("""{"tag":{}}"""))

        var thrown: IOException? = null
        try {
            repository.resolvePlaybackUrl("https://youtu.be/test")
        } catch (error: IOException) {
            thrown = error
        }

        assertTrue(thrown != null)
        assertTrue(thrown?.message?.contains("tag.url") == true)
    }

    @Test
    fun resolvePlaybackUrl_emptyBody_throwsIOException() = runTest {
        whenever(apiService.resolvePlaybackUrl("https://youtu.be/test"))
            .thenReturn(Response.success(""))

        var thrown: IOException? = null
        try {
            repository.resolvePlaybackUrl("https://youtu.be/test")
        } catch (error: IOException) {
            thrown = error
        }

        assertTrue(thrown != null)
    }
}
