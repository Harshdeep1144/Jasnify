package com.harshdeep.jasnify.presentation.screens.main.tabs.home

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.others.VideoPlayer
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderMediaSlider(
    mediaList: List<HeaderMedia>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onMediaClick: (HeaderMedia) -> Unit = {},
    autoSlideIntervalMs: Long = 8000L
) {
    if (mediaList.isEmpty()) return

    val context = LocalContext.current

    val customImageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(SvgDecoder.Factory())
            }
            .build()
    }

    LaunchedEffect(pagerState, mediaList.size) {
        if (mediaList.size > 1) {
            while (true) {
                delay(autoSlideIntervalMs.milliseconds)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = pagerState.currentPage + 1
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(durationMillis = 2000)
                    )
                }
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val actualIndex = page % mediaList.size
        val media = mediaList[actualIndex]

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = media.actionType.isNotEmpty()) {
                    onMediaClick(media)
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_hero_display_default),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            when (media) {
                is HeaderMedia.ImageResource -> {
                    Image(
                        painter = painterResource(id = media.resId),
                        contentDescription = "Header Slide Image ${actualIndex + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is HeaderMedia.ImageUrl, is HeaderMedia.GifUrl -> {
                    val url = if (media is HeaderMedia.ImageUrl) media.url else (media as HeaderMedia.GifUrl).url
                    var hasImageError by remember(url) { mutableStateOf(false) }

                    if (!hasImageError) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(url)
                                .crossfade(true)
                                .placeholder(R.drawable.app_hero_display_default)
                                .error(R.drawable.app_hero_display_default)
                                .build(),
                            imageLoader = customImageLoader,
                            contentDescription = "Header Slide Remote Asset ${actualIndex + 1}",
                            contentScale = ContentScale.Crop,
                            onError = { hasImageError = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                is HeaderMedia.VideoResource -> {
                    VideoPlayer(
                        videoUrl = "android.resource://" + context.packageName + "/" + media.resId,
                        modifier = Modifier.fillMaxSize(),
                        isMuted = true,
                        autoPlay = true,
                        isLooping = true
                    )
                }

                is HeaderMedia.VideoUrl -> {
                    VideoPlayer(
                        videoUrl = media.url,
                        modifier = Modifier.fillMaxSize(),
                        isMuted = true,
                        autoPlay = true,
                        isLooping = true
                    )
                }

                is HeaderMedia.LottieUrl -> {
                    val compositionResult = rememberLottieComposition(
                        spec = LottieCompositionSpec.Url(media.url)
                    )

                    if (compositionResult.value != null && !compositionResult.isFailure) {
                        LottieAnimation(
                            composition = compositionResult.value,
                            modifier = Modifier.fillMaxSize(),
                            iterations = LottieConstants.IterateForever,
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                is HeaderMedia.LottieResource -> {
                    val compositionResult = rememberLottieComposition(
                        spec = LottieCompositionSpec.RawRes(media.resId)
                    )

                    if (compositionResult.value != null && !compositionResult.isFailure) {
                        LottieAnimation(
                            composition = compositionResult.value,
                            modifier = Modifier.fillMaxSize(),
                            iterations = LottieConstants.IterateForever,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}