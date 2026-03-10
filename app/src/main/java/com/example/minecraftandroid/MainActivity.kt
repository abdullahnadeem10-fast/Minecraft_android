package com.abdullahnadeem.minecraftandroid

import android.os.Bundle
import androidx.annotation.RawRes
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.abdullahnadeem.minecraftandroid.data.remote.ApiModule
import com.abdullahnadeem.minecraftandroid.data.repository.CatalogRepositoryImpl
import com.abdullahnadeem.minecraftandroid.data.repository.PlaybackRepositoryImpl

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fallbackCatalogJson = readRawText(R.raw.catalog_fallback)
        val catalogRepository = CatalogRepositoryImpl(
            apiService = ApiModule.catalogApiService,
            fallbackPayload = fallbackCatalogJson
        )
        val playbackRepository = PlaybackRepositoryImpl(ApiModule.playbackApiService)

        setContent {
            MinecraftVideosApp(
                catalogRepository = catalogRepository,
                playbackRepository = playbackRepository
            )
        }
    }

    private fun readRawText(@RawRes resourceId: Int): String {
        return resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
    }
}