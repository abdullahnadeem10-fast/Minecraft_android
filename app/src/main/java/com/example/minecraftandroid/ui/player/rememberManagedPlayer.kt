package com.abdullahnadeem.minecraftandroid.ui.player

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun rememberManagedPlayer(context: Context): ExoPlayer {
    val player = remember { ExoPlayer.Builder(context).build() }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    return player
}
