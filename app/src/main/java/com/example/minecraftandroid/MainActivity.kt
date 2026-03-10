package com.abdullahnadeem.minecraftandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.abdullahnadeem.minecraftandroid.data.remote.ApiModule
import com.abdullahnadeem.minecraftandroid.data.repository.CatalogRepositoryImpl
import com.abdullahnadeem.minecraftandroid.data.repository.PlaybackRepositoryImpl

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val catalogRepository = CatalogRepositoryImpl(ApiModule.catalogApiService)
        val playbackRepository = PlaybackRepositoryImpl(ApiModule.playbackApiService)

        setContent {
            MinecraftVideosApp(
                catalogRepository = catalogRepository,
                playbackRepository = playbackRepository
            )
        }
    }
}