package com.abdullahnadeem.minecraftandroid.ui.player

sealed interface PlayerUiState {
    data object Idle : PlayerUiState
    data object ResolvingUrl : PlayerUiState
    data class Ready(val playbackUrl: String) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}
