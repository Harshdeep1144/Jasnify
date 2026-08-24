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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.harshdeep.jasnify.theme.BackgroundPrimary
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
    onFavoriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
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

    val formattedDate = remember(moment.timestamp) {
        val sdf = SimpleDateFormat("EEEE, hh:mm a", Locale.getDefault())
        val dateStr = sdf.format(Date(moment.timestamp))
        // Simple logic for \"Today\"/\"Yesterday\"
        val now = System.currentTimeMillis()
        val diff = now - moment.timestamp
        val days = diff / (1000 * 60 * 60 * 24)
        when (days) {
            0L -> "Today, ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(moment.timestamp))}"
            1L -> "Yesterday, ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(moment.timestamp))}"
            else -> dateStr
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
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
                title = formattedDate,
                onBackClick = onBackClick,
                onMenuClick = { /* More menu */ },
                backIcon = TopIcon.Predefined.BACK_2,
                menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                textColor = Color.White,
                buttonStyle = ButtonBackground.TRANSPARENT
            )
        }

        // Media Container with sharedBounds
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 80.dp,
                    bottom = 120.dp,
                    start = 0.dp,
                    end = 0.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            with(sharedTransitionScope) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // Adjust based on image or use wrap content
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = transitionKey),
                            animatedVisibilityScope = animatedVisibilityScope,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(0.dp))
                        )
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
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Bottom Action Pill
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .padding(horizontal = 48.dp)
        ) {
            MomentsActionBar(
                onShareClick = { onShareTrigger(moment) },
                onFavoriteClick = onFavoriteClick,
                onDownloadClick = onDownloadClick,
                containerColor = Color(0xFF1E1E1E).copy(alpha = 0.9f)
            )
        }
    }
}
