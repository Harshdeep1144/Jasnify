package com.harshdeep.jasnify.presentation.screens.moments

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.others.VideoPlayer
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.utils.ShareUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sv.lib.squircleshape.SquircleShape
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.time.Duration.Companion.milliseconds

private val FullMomentShape = SquircleShape(CornerMedium, CornerSmoothingDefault)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MomentItemFullView(
    moment: Moment,
    transitionKey: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SetStatusBarTheme(useDarkIcons = false)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    var momentToCapture by remember { mutableStateOf<Moment?>(null) }

    val onShareTrigger: (Moment) -> Unit = remember(context, graphicsLayer) {
        { item: Moment ->
            coroutineScope.launch {
                if (item.isVideo) {
                    withContext(Dispatchers.IO) {
                        try {
                            val tempFile = File(context.cacheDir, "moment_${System.currentTimeMillis()}.mp4")
                            URL(item.imageUrl).openStream().use { input ->
                                FileOutputStream(tempFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            ShareUtils.shareFile(context, tempFile, mimeType = "video/*")
                        } catch (_: Exception) {
                            ShareUtils.shareText(context, item.imageUrl)
                        }
                    }
                } else {
                    momentToCapture = item
                    delay(100.milliseconds)
                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                    ShareUtils.shareImage(context, bitmap, whatsappOnly = false)
                    momentToCapture = null
                }
            }
        }
    }

    val sharePainter = painterResource(R.drawable.ic_share)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ContentPrimary)
    ) {
        // Hidden Capture Area for High-Res Drawing/Share (Images only)
        if (!moment.isVideo) {
            Box(
                modifier = Modifier
                    .size(360.dp, 480.dp)
                    .offset(x = (-2000).dp)
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                    }
            ) {
                momentToCapture?.let { item ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(false)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            CustomTopBar(
                onBackClick = onBackClick,
                onMenuClick = { onShareTrigger(moment) },
                backIcon = TopIcon.Predefined.BACK_2,
                menuIcon = TopIcon.CustomPainter(sharePainter),
                textColor = ContentInvPrimary,
                buttonStyle = ButtonBackground.OPAQUE,
                buttonColor = Color(0xE53D3D3D)
            )
        }

        // Media Container with sharedBounds
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 80.dp,
                    bottom = 40.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            with(sharedTransitionScope) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = transitionKey),
                            animatedVisibilityScope = animatedVisibilityScope,
                            clipInOverlayDuringTransition = OverlayClip(FullMomentShape)
                        )
                        .clip(FullMomentShape)
                ) {
                    if (moment.isVideo) {
                        VideoPlayer(
                            videoUrl = moment.imageUrl,
                            autoPlay = true,
                            isLooping = true,
                            isMuted = false,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(moment.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}