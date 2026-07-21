package com.harshdeep.jasnify.presentation.components.others

import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    isMuted: Boolean = false,
    autoPlay: Boolean = true,
    isLooping: Boolean = true,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isPlaying by remember { mutableStateOf(autoPlay) }
    var showIcon by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    // Instantiate ExoPlayer
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        }
    }

    // Add listener to detect buffering, error, play state, and completion
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isLoading = true
                        hasError = false
                    }
                    Player.STATE_READY -> {
                        isLoading = false
                        hasError = false
                    }
                    Player.STATE_ENDED -> {
                        isLoading = false
                        if (isLooping) {
                            exoPlayer.seekTo(0)
                            exoPlayer.play()
                        } else {
                            isPlaying = false
                        }
                    }
                    Player.STATE_IDLE -> {
                        isLoading = false
                    }
                }
            }

            override fun onIsPlayingChanged(isPlayingState: Boolean) {
                isPlaying = isPlayingState
            }

            override fun onPlayerError(error: PlaybackException) {
                isLoading = false
                hasError = true
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Lifecycle Management: Pause video when screen goes into background
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.playWhenReady = false
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (isPlaying && exoPlayer.playbackState != Player.STATE_ENDED) {
                        exoPlayer.playWhenReady = true
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Synchronize media item changes
    LaunchedEffect(videoUrl) {
        if (videoUrl.isNotBlank()) {
            hasError = false
            isLoading = true
            exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = isPlaying
        }
    }

    // Synchronize play/pause toggle
    LaunchedEffect(isPlaying) {
        if (exoPlayer.playbackState == Player.STATE_ENDED && isPlaying) {
            exoPlayer.seekTo(0)
        }
        exoPlayer.playWhenReady = isPlaying
    }

    // Synchronize looping configuration
    LaunchedEffect(isLooping) {
        exoPlayer.repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }

    // Synchronize audio mute state
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    // Auto-hide play/pause indicator overlay
    LaunchedEffect(showIcon) {
        if (showIcon) {
            delay(800.milliseconds)
            showIcon = false
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (exoPlayer.playbackState == Player.STATE_ENDED) {
                    exoPlayer.seekTo(0)
                    isPlaying = true
                    exoPlayer.play()
                } else {
                    isPlaying = !isPlaying
                }
                showIcon = true
            },
        contentAlignment = Alignment.Center
    ) {
        // Embedded Native Android PlayerView
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    // Keep the last frame on screen when playback completes/resets instead of turning blank
                    setKeepContentOnPlayerReset(true)
                    // Set transparent shutter to prevent sudden black/blank flickering
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    // RESIZE_MODE_ZOOM fills container
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { view ->
                view.player = exoPlayer
            },
            modifier = Modifier.fillMaxSize()
        )

        // Loading Spinner during video buffering
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        // Error State Indicator if media failed to load or network error occurred
        if (hasError) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = "Playback Error",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
        }

        // Animated Play/Pause feedback icon overlay
        AnimatedVisibility(
            visible = showIcon && !isLoading && !hasError,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
            TopBarIconButton(
                icon = TopIcon.CustomPainter(painter = rememberVectorPainter(icon)),
                onClick = { },
                backgroundStyle = ButtonBackground.TRANSLUCENT,
                translucentAlpha = 0.5f,
                size = 60.dp,
                iconSize = 32.dp
            )
        }
    }
}