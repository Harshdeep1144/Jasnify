package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.utils.SessionState
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceInvPrimary
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AskAiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shouldShowGlobalAnimation = SessionState.shouldShowAiButtonLaunchAnimation
    var isExpanded by remember { mutableStateOf(shouldShowGlobalAnimation) }
    var isDragging by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    var offsetY by remember { mutableFloatStateOf(0f) }
    var initialY by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current
    val topSpacingPx = with(density) { 100.dp.toPx() }
    val startOffsetX = with(density) { 240.dp.toPx() } // Slide-in distance from right

    val launchOffsetX = remember { Animatable(if (shouldShowGlobalAnimation) startOffsetX else 0f) }

    val minOffsetY = remember(initialY, topSpacingPx) {
        if (initialY > 0) {
            topSpacingPx - initialY
        } else 0f
    }

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.08f else 1.0f,
        animationSpec = spring(),
        label = "scaleAnimation"
    )

    // Smooth, fast slide-in animation on launch
    LaunchedEffect(shouldShowGlobalAnimation) {
        if (shouldShowGlobalAnimation) {
            launchOffsetX.snapTo(startOffsetX)
            launchOffsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            )
            SessionState.markAiButtonAnimationShown()
        } else {
            launchOffsetX.snapTo(0f)
        }
    }

    // Handles text collapse delay
    LaunchedEffect(isDragging, shouldShowGlobalAnimation) {
        if (isDragging) {
            isExpanded = true
        } else if (shouldShowGlobalAnimation || isExpanded) {
            // Delay for initial launch or after dragging
            delay(2000.milliseconds)
            isExpanded = false
        }
    }

    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    x = launchOffsetX.value.roundToInt(),
                    y = offsetY.roundToInt()
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onGloballyPositioned {
                if (initialY == 0f) {
                    initialY = it.positionInWindow().y
                }
            }
            .pointerInput(initialY, topSpacingPx) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDragEnd = {
                        isDragging = false
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = offsetY + dragAmount.y
                        offsetY = newOffset.coerceIn(minOffsetY, 0f)
                    }
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .clip(RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp))
            .background(SurfaceInvPrimary)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ai),
                contentDescription = "AI Icon",
                tint = ContentInvPrimary,
                modifier = Modifier.size(28.dp)
            )

            AnimatedVisibility(
                visible = isExpanded || isDragging,
                enter = expandHorizontally(expandFrom = Alignment.Start),
                exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                Row {
                    Spacer(modifier = Modifier.width(11.2.dp))
                    Text(
                        text = "Ask AI",
                        color = ContentInvPrimary,
                        style = JasnifyTheme.typography.labelXLarge.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp
                        )
                    )
                }
            }
        }
    }
}