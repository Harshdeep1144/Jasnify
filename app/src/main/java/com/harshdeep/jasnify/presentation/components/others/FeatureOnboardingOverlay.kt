package com.harshdeep.jasnify.presentation.components.others

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

data class OnboardingStep(
    val stepKey: String,
    val title: String,
    val description: String,
    val iconRes: Int = R.drawable.ill_vendor_grooming,
    val highlightPadding: Dp = 8.dp,
    val isCircleHighlight: Boolean = false,
    val shape: Shape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault),
    val cardShape: Shape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault),
    val cardBackgroundColor: Color = ContentBrand,
    val forceCardAbove: Boolean? = null,
    val arrowXShift: Dp = 0.dp,
    val cardYShift: Dp = 0.dp
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

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun FeatureOnboardingOverlay(
    steps: List<OnboardingStep>,
    currentStepIndex: Int,
    targetRectMap: Map<String, Rect>,
    onNextStep: () -> Unit,
    onPreviousStep: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (steps.isEmpty() || currentStepIndex !in steps.indices) return

    val currentStep = steps[currentStepIndex]
    val currentTargetRect = targetRectMap[currentStep.stepKey]
    var isStepSettled by remember(currentStepIndex) { mutableStateOf(currentTargetRect != null) }

    LaunchedEffect(currentStepIndex, currentTargetRect) {
        if (currentTargetRect != null) {
            isStepSettled = true
        } else {
            isStepSettled = false
            delay(100.milliseconds)
            var attempts = 0
            while (targetRectMap[steps[currentStepIndex].stepKey] == null && attempts < 15) {
                delay(30.milliseconds)
                attempts++
            }
            isStepSettled = true
        }
    }

    val cutoutScale by animateFloatAsState(
        targetValue = if (isStepSettled && currentTargetRect != null) 1f else 0.85f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cutoutScaleAnim"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (currentTargetRect != null) 1f else 0f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "cardAlphaAnim"
    )

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val screenWidthDp = configuration.screenWidthDp.dp

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
            // Dark scrim overlay with spotlight cutout and glowing white border
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.99f }
            ) {
                drawRect(color = Color.Black.copy(alpha = 0.78f))

                currentTargetRect?.let { rect ->
                    val pad = currentStep.highlightPadding.toPx()
                    val rawInflated = Rect(
                        rect.left - pad,
                        rect.top - pad,
                        rect.right + pad,
                        rect.bottom + pad
                    )
                    val center = rawInflated.center
                    val scaledW = rawInflated.width * cutoutScale
                    val scaledH = rawInflated.height * cutoutScale
                    val inflated = Rect(
                        center.x - scaledW / 2f,
                        center.y - scaledH / 2f,
                        center.x + scaledW / 2f,
                        center.y + scaledH / 2f
                    )

                    val borderStrokeWidth = 3.dp.toPx()

                    if (currentStep.isCircleHighlight) {
                        val radius = maxOf(inflated.width, inflated.height) / 2f
                        drawCircle(
                            color = Color.Transparent,
                            radius = radius,
                            center = center,
                            blendMode = BlendMode.Clear
                        )
                        drawCircle(
                            color = Color.White,
                            radius = radius,
                            center = center,
                            style = Stroke(width = borderStrokeWidth)
                        )
                    } else {
                        val outline = currentStep.shape.createOutline(
                            size = inflated.size,
                            layoutDirection = LayoutDirection.Ltr,
                            density = this
                        )
                        val path = Path().apply {
                            when (outline) {
                                is Outline.Generic -> addPath(outline.path)
                                is Outline.Rounded -> addRoundRect(outline.roundRect)
                                is Outline.Rectangle -> addRect(outline.rect)
                            }
                            translate(Offset(inflated.left, inflated.top))
                        }
                        drawPath(
                            path = path,
                            color = Color.Transparent,
                            blendMode = BlendMode.Clear
                        )
                        drawPath(
                            path = path,
                            color = Color.White,
                            style = Stroke(width = borderStrokeWidth)
                        )
                    }
                }
            }

            // Tooltip Callout Box
            val isAboveTarget = remember(currentTargetRect, screenHeightPx, currentStep.forceCardAbove) {
                when (currentStep.forceCardAbove) {
                    true -> true
                    false -> false
                    null -> if (currentTargetRect == null) false else currentTargetRect.top > screenHeightPx * 0.45f
                }
            }

            var calloutColumnHeightPx by remember { mutableFloatStateOf(0f) }
            val padPx = with(density) { currentStep.highlightPadding.toPx() }
            val customYShiftPx = with(density) { currentStep.cardYShift.toPx() }

            val cardYOffsetPx = remember(currentTargetRect, isAboveTarget, calloutColumnHeightPx, customYShiftPx, padPx, screenHeightPx) {
                if (currentTargetRect == null) {
                    screenHeightPx * 0.3f
                } else {
                    if (isAboveTarget) {
                        val targetCutoutTop = currentTargetRect.top - padPx
                        val heightToUse = if (calloutColumnHeightPx > 0f) calloutColumnHeightPx else (210.dp.value * density.density)
                        val calculatedY = targetCutoutTop - heightToUse - (8.dp.value * density.density) + customYShiftPx
                        calculatedY.coerceAtLeast(40.dp.value * density.density)
                    } else {
                        val targetCutoutBottom = currentTargetRect.bottom + padPx
                        val calculatedY = targetCutoutBottom + (8.dp.value * density.density) + customYShiftPx
                        val heightToUse = if (calloutColumnHeightPx > 0f) calloutColumnHeightPx else (210.dp.value * density.density)
                        calculatedY.coerceAtMost(screenHeightPx - heightToUse - (20.dp.value * density.density))
                    }
                }
            }

            val cardYDp = with(density) { cardYOffsetPx.toDp() }

            // Calculate pointer arrow's horizontal offset relative to the centered column
            val arrowXOffsetDp = remember(currentTargetRect, screenWidthDp, currentStep.arrowXShift) {
                if (currentTargetRect != null) {
                    val targetCenterX = currentTargetRect.center.x
                    val screenCenterX = with(density) { (screenWidthDp / 2f).toPx() }
                    val offsetPx = targetCenterX - screenCenterX
                    val offsetDp = with(density) { offsetPx.toDp() }
                    val maxOffset = (screenWidthDp / 2f - 68.dp).coerceAtLeast(0.dp)
                    (offsetDp + currentStep.arrowXShift).coerceIn(-maxOffset, maxOffset)
                } else {
                    0.dp
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = cardYDp)
                    .onGloballyPositioned { coordinates ->
                        if (coordinates.size.height > 0) {
                            calloutColumnHeightPx = coordinates.size.height.toFloat()
                        }
                    }
                    .graphicsLayer { alpha = cardAlpha },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top pointer arrow (if card is below target)
                if (!isAboveTarget && currentTargetRect != null) {
                    Canvas(
                        modifier = Modifier
                            .size(24.dp, 12.dp)
                            .offset(x = arrowXOffsetDp, y = 1.dp)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, 0f)
                            lineTo(size.width, size.height + 1f)
                            lineTo(0f, size.height + 1f)
                            close()
                        }
                        drawPath(path, currentStep.cardBackgroundColor)
                    }
                }

                // Callout Card
                Surface(
                    shape = currentStep.cardShape,
                    color = currentStep.cardBackgroundColor,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier.size(72.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.20f))
                                )

                                Image(
                                    painter = painterResource(id = currentStep.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.requiredHeight(60.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Text(
                                text = "${currentStepIndex + 1} of ${steps.size}",
                                style = JasnifyTheme.typography.labelLarge.copy(
                                    lineHeight = 24.sp
                                ),
                                color = ContentTertiary,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentStep.title,
                            style = JasnifyTheme.typography.headingXLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = ContentInvPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentStep.description,
                            style = JasnifyTheme.typography.labelLarge.copy(
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Light
                            ),
                            color = ContentInvPrimary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentStepIndex > 0 && onPreviousStep != null) {
                                CustomTextButton(
                                    onClick = onPreviousStep,
                                    text = "Previous",
                                    size = ButtonSize.Small,
                                    type = ButtonType.Secondary,
                                    shapeStyle = ButtonShapeStyle.Square,
                                    leadingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
                                    containerColor = ContentInvPrimary.copy(alpha = 0.1f),
                                    contentColor = ContentInvPrimary
                                )
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            CustomTextButton(
                                onClick = onNextStep,
                                text = if (currentStepIndex == steps.lastIndex) "Got it!" else "Next",
                                size = ButtonSize.Small,
                                type = ButtonType.Primary,
                                shapeStyle = ButtonShapeStyle.Square,
                                trailingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowForward),
                                containerColor = ContentInvPrimary,
                                contentColor = ContentPrimary
                            )
                        }
                    }
                }

                // Bottom pointer arrow (if card is above target)
                if (isAboveTarget && currentTargetRect != null) {
                    Canvas(
                        modifier = Modifier
                            .size(24.dp, 12.dp)
                            .offset(x = arrowXOffsetDp, y = (-1).dp)
                    ) {
                        val path = Path().apply {
                            moveTo(-1f, -1f)
                            lineTo(size.width + 1f, -1f)
                            lineTo(size.width / 2f, size.height)
                            close()
                        }
                        drawPath(path, currentStep.cardBackgroundColor)
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
                title = "Track Event Budget",
                description = "Set your budget, add expenses, keep track of your finances.",
                iconRes = R.drawable.ill_vendor_grooming,
                highlightPadding = 12.dp,
                isCircleHighlight = false
            ),
            OnboardingStep(
                stepKey = "add_fab",
                title = "Explore Venues",
                description = "Tap to view & explore the best venues near you.",
                iconRes = R.drawable.ill_vendor_grooming,
                highlightPadding = 8.dp,
                isCircleHighlight = false
            )
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val targetRectMap = remember {
        mutableStateMapOf<String, Rect>(
            "search_bar" to Rect(48f, 120f, 1032f, 260f),
            "add_fab" to Rect(850f, 1700f, 1000f, 1850f)
        )
    }
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
                onPreviousStep = {
                    if (currentStepIndex > 0) {
                        currentStepIndex--
                    }
                }
            )
        }
    }
}
