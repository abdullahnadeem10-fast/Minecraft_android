package com.abdullahnadeem.minecraftandroid.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import com.abdullahnadeem.minecraftandroid.R
import com.abdullahnadeem.minecraftandroid.data.repository.PlaybackRepository

@Composable
fun VideoPlayerScreen(
    youtubeUrl: String,
    videoTitle: String,
    playbackRepository: PlaybackRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: PlayerViewModel = viewModel(
        factory = PlayerViewModelFactory(playbackRepository)
    )
    val uiState = vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(youtubeUrl) {
        vm.resolveAndPlay(youtubeUrl)
    }

    PlayerScaffold(
        title = videoTitle,
        onBack = onBack,
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState.value) {
            PlayerUiState.Idle,
            PlayerUiState.ResolvingUrl -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.player_resolving),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            is PlayerUiState.Ready -> {
                val player = rememberManagedPlayer(context = LocalContext.current)

                LaunchedEffect(state.playbackUrl) {
                    player.setMediaItem(MediaItem.fromUri(state.playbackUrl))
                    player.prepare()
                    player.playWhenReady = true
                }

                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).also {
                            it.player = player
                            it.useArtwork = false
                            it.setShutterBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is PlayerUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.player_error_title),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { vm.resolveAndPlay(youtubeUrl) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = stringResource(R.string.player_retry))
                        }
                    }
                }
            }
        }
    }
}
