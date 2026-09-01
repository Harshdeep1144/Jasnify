package com.harshdeep.jasnify.presentation.components.others

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

data class OnboardingStep(
    val stepKey: String,
    val title: String,
    val description: String,
    val iconRes: Int = R.drawable.ill_vendor_grooming,
    val highlightPadding: Dp = 8.dp,
    val isCircleHighlight: Boolean = false,
    val cornerRadius: Dp = 16.dp
)

fun Modifier.onboardingTarget(
    stepKey: String,
    currentStepKey: String?,
    onTargetPositioned: (Rect) -> Unit
): Modifier = this.onGloballyPositioned { coordinates ->
    if (stepKey == currentStepKey && coordinates.isAttached) {
        val bounds = coordinates.boundsInWindow()
        if (bounds.width > 0 && bounds.height > 0) {
            onTargetPositioned(bounds)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR2)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun FeatureOnboardingOverlay(
    steps: List<OnboardingStep>,
    currentStepIndex: Int,
    targetRectMap: Map<String, Rect>,
    onNextStep: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (steps.isEmpty() || currentStepIndex !in steps.indices) return

    val currentStep = steps[currentStepIndex]
    val targetRect = targetRectMap[currentStep.stepKey]

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
            .fillMaxSize()
            .zIndex(9999f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onNextStep()
                }
        ) {
            // Dark scrim overlay with spotlight cutout
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.99f }
            ) {
                drawRect(color = Color.Black.copy(alpha = 0.75f))

                targetRect?.let { rect ->
                    val pad = currentStep.highlightPadding.toPx()
                    val inflated = Rect(
                        rect.left - pad,
                        rect.top - pad,
                        rect.right + pad,
                        rect.bottom + pad
                    )

                    if (currentStep.isCircleHighlight) {
                        val center = inflated.center
                        val radius = maxOf(inflated.width, inflated.height) / 2f
                        drawCircle(
                            color = Color.Transparent,
                            radius = radius,
                            center = center,
                            blendMode = BlendMode.Clear
                        )
                    } else {
                        val squircleShape = SquircleShape(currentStep.cornerRadius)
                        val outline = squircleShape.createOutline(
                            size = inflated.size,
                            layoutDirection = LayoutDirection.Ltr,
                            density = this
                        )
                        val path = Path().apply {
                            when (outline) {
                                is androidx.compose.ui.graphics.Outline.Generic -> addPath(outline.path)
                                is androidx.compose.ui.graphics.Outline.Rounded -> addRoundRect(outline.roundRect)
                                is androidx.compose.ui.graphics.Outline.Rectangle -> addRect(outline.rect)
                            }
                            translate(androidx.compose.ui.geometry.Offset(inflated.left, inflated.top))
                        }
                        drawPath(
                            path = path,
                            color = Color.Transparent,
                            blendMode = BlendMode.Clear
                        )
                    }
                }
            }

            // Tooltip Callout Box
            val isAboveTarget = remember(targetRect, screenHeightPx) {
                if (targetRect == null) false
                else targetRect.top > screenHeightPx * 0.45f
            }

            val cardYOffsetPx = remember(targetRect, isAboveTarget) {
                if (targetRect == null) screenHeightPx * 0.3f
                else {
                    if (isAboveTarget) {
                        (targetRect.top - 240.dp.value * density.density).coerceAtLeast(40.dp.value * density.density)
                    } else {
                        (targetRect.bottom + 20.dp.value * density.density).coerceAtMost(screenHeightPx - 260.dp.value * density.density)
                    }
                }
            }

            val cardYDp = with(density) { cardYOffsetPx.toDp() }

            val cornerRadiusPx = with(density) { currentStep.cornerRadius.toPx() }
            val padPx = with(density) { currentStep.highlightPadding.toPx() }
            val insetTargetX = if (targetRect != null) {
                val rawX = targetRect.center.x
                val leftEdge = (targetRect.left - padPx) + cornerRadiusPx + 4.dp.value * density.density
                val rightEdge = (targetRect.right + padPx) - cornerRadiusPx - 4.dp.value * density.density
                if (leftEdge < rightEdge) rawX.coerceIn(leftEdge, rightEdge) else rawX
            } else 0f
            val arrowX = with(density) { insetTargetX.toDp() }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = cardYDp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pointer arrow pointing up if card is below target
                if (!isAboveTarget && targetRect != null) {
                    val maxArrowOffset = (configuration.screenWidthDp.dp / 2 - 32.dp).coerceAtLeast(0.dp)
                    Canvas(
                        modifier = Modifier
                            .size(24.dp, 12.dp)
                            .offset(x = (arrowX - configuration.screenWidthDp.dp / 2).coerceIn(-maxArrowOffset, maxArrowOffset))
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, 0f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }
                        drawPath(path, Color(0xFF29B6F6))
                    }
                }

                // Duolingo-styled Callout Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF29B6F6),
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = currentStep.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(54.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = currentStep.title,
                                    style = JasnifyTheme.typography.headingLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = currentStep.description,
                                    style = JasnifyTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    ),
                                    color = Color.White.copy(alpha = 0.95f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${currentStepIndex + 1} of ${steps.size}",
                                style = JasnifyTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (steps.size > 1) {
                                    Text(
                                        text = "Skip",
                                        color = Color.White.copy(alpha = 0.8f),
                                        style = JasnifyTheme.typography.labelMedium,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onSkip() }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    onClick = onNextStep,
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    shadowElevation = 4.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = if (currentStepIndex == steps.lastIndex) "Got it!" else "Next",
                                            style = JasnifyTheme.typography.headingMedium.copy(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            ),
                                            color = Color(0xFF0288D1)
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color(0xFF0288D1),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Pointer arrow pointing down if card is above target
                if (isAboveTarget && targetRect != null) {
                    val maxArrowOffset = (configuration.screenWidthDp.dp / 2 - 32.dp).coerceAtLeast(0.dp)
                    Canvas(
                        modifier = Modifier
                            .size(24.dp, 12.dp)
                            .offset(x = (arrowX - configuration.screenWidthDp.dp / 2).coerceIn(-maxArrowOffset, maxArrowOffset))
                    ) {
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width / 2f, size.height)
                            close()
                        }
                        drawPath(path, Color(0xFF29B6F6))
                    }
                }
            }
        }
    }
}


// ============================================ Preview =====================================================


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeatureOnboardingOverlayPreview() {
    val steps = remember {
        listOf(
            OnboardingStep(
                stepKey = "search_bar",
                title = "Quick Search",
                description = "Easily search through your vendors, appointments, and services here.",
                iconRes = R.drawable.ill_vendor_grooming,
                highlightPadding = 16.dp,
                isCircleHighlight = false
            ),
            OnboardingStep(
                stepKey = "add_fab",
                title = "Create New Booking",
                description = "Tap the plus icon anytime to schedule an instant appointment.",
                iconRes = R.drawable.ill_vendor_grooming,
                highlightPadding = 8.dp,
                isCircleHighlight = true
            )
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val targetRectMap = remember { mutableStateMapOf<String, Rect>() }
    val currentStepKey = steps.getOrNull(currentStepIndex)?.stepKey

    JasnifyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {},
                        containerColor = Color(0xFF0288D1),
                        modifier = Modifier.onboardingTarget(
                            stepKey = "add_fab",
                            currentStepKey = currentStepKey,
                            onTargetPositioned = { bounds ->
                                targetRectMap["add_fab"] = bounds
                            }
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F0F0))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .onboardingTarget(
                                stepKey = "search_bar",
                                currentStepKey = currentStepKey,
                                onTargetPositioned = { bounds ->
                                    targetRectMap["search_bar"] = bounds
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Search anything...", color = Color.Gray, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Dashboard Content",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            FeatureOnboardingOverlay(
                steps = steps,
                currentStepIndex = currentStepIndex,
                targetRectMap = targetRectMap,
                onNextStep = {
                    if (currentStepIndex < steps.lastIndex) {
                        currentStepIndex++
                    } else {
                        currentStepIndex = 0
                    }
                },
                onSkip = {
                    currentStepIndex = 0
                }
            )
        }
    }
}