package com.harshdeep.jasnify.presentation.components.bottomdrawer.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSuccessBottomSheet(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    coolDownMillis: Long = 3000L,
    height: Dp = 400.dp,
    onProgress: ((Float) -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.ani_success_brand)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        iterations = 1
    )

    LaunchedEffect(Unit) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    LaunchedEffect(coolDownMillis) {
        delay(coolDownMillis.milliseconds)
        onDismiss()
    }

    CustomBottomSheet(
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = false,
        showCloseButton = false
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(160.dp)
                )
            }

            Text(
                text = message,
                style = JasnifyTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                color = ContentBrandDark
            )
        }
    }
}