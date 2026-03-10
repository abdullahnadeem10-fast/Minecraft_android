package com.abdullahnadeem.minecraftandroid.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdullahnadeem.minecraftandroid.data.repository.PlaybackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playbackRepository: PlaybackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Idle)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var lastRequestedYoutubeUrl: String? = null

    fun resolveAndPlay(youtubeUrl: String) {
        if (youtubeUrl.isBlank()) {
            _uiState.value = PlayerUiState.Error("Video URL is missing")
            return
        }

        // If input is already a direct stream URL, skip resolver and play immediately.
        if (!looksLikeYouTubeUrl(youtubeUrl)) {
            _uiState.value = PlayerUiState.Ready(youtubeUrl)
            lastRequestedYoutubeUrl = youtubeUrl
            return
        }

        val currentState = _uiState.value
        if (currentState is PlayerUiState.ResolvingUrl) return
        if (currentState is PlayerUiState.Ready && lastRequestedYoutubeUrl == youtubeUrl) return

        _uiState.value = PlayerUiState.ResolvingUrl
        lastRequestedYoutubeUrl = youtubeUrl

        viewModelScope.launch {
            runCatching {
                playbackRepository.resolvePlaybackUrl(youtubeUrl)
            }.onSuccess { playbackUrl ->
                _uiState.value = if (playbackUrl.isBlank()) {
                    PlayerUiState.Error("Resolved playback URL was empty")
                } else {
                    PlayerUiState.Ready(playbackUrl)
                }
            }.onFailure { throwable ->
                _uiState.value = PlayerUiState.Error(
                    message = throwable.message ?: "Unknown error"
                )
            }
        }
    }

    fun reset() {
        lastRequestedYoutubeUrl = null
        _uiState.value = PlayerUiState.Idle
    }

    private fun looksLikeYouTubeUrl(url: String): Boolean {
        val normalized = url.lowercase()
        return normalized.contains("youtube.com") || normalized.contains("youtu.be")
    }
}
