package com.abdullahnadeem.minecraftandroid.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdullahnadeem.minecraftandroid.data.repository.PlaybackRepository

class PlayerViewModelFactory(
    private val playbackRepository: PlaybackRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(playbackRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
