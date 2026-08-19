package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.math.roundToInt

/**
 * CustomBottomSheet
 *
 * A highly configurable, high-performance fluid bottom sheet component designed for app-wide use.
 * Employs spring physics and real-time motion progress reporting to drive dynamic background scaling.
 *
 * @param heading Optional title text displayed in the header.
 * @param headingStyle Custom typography style for the header text. Defaults to [JasnifyTheme.typography.displayLarge].
 * @param headingLineHeight Custom line height for the header text. Defaults to 40.sp.
 * @param closeButtonBackgroundStyle Background style for the header close button. Defaults to [ButtonBackground.OPAQUE].
 * @param onDismiss Callback invoked once the slide-down dismissal animation completes.
 * @param isVisible Visibility controller for the bottom sheet.
 * @param sheetHeight Explicit height for the content area, or null for dynamic height.
 * @param sheetGesturesEnabled Toggles drag-to-dismiss touch gestures across the entire sheet body. If false, drag gestures remain active on the top header/handle area.
 * @param showDragHandle Controls visibility of the top drag handle indicator.
 * @param showCloseButton Controls visibility of the top-right header close button.
 * @param dismissOnBackdropClick Toggles dismiss trigger when clicking background backdrop overlay.
 * @param dampingRatio Custom spring damping ratio (default 0.82f for clean bounce).
 * @param stiffness Custom spring stiffness (default 300f for fluid motion).
 * @param containerColor Background solid color of the sheet container (used if [containerBrush] is null).
 * @param containerBrush Background brush (gradient/pattern) of the sheet container. Takes priority over [containerColor].
 * @param headerBackgroundImage Optional composable slot to render background image/pattern behind top header elements.
 * @param onProgress Real-time callback emitting sheet position ratio (0f = open, 1f = closed).
 * @param hasToast Dynamic flag indicating if a toast is active, increasing top padding to allow full visibility.
 * @param toast Composable slot for displaying toast banners above/relative to the sheet.
 * @param content Composable body rendered inside the sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    heading: String = "",
    headingStyle: TextStyle = JasnifyTheme.typography.displayLarge,
    headingLineHeight: TextUnit = 40.sp,
    closeButtonBackgroundStyle: ButtonBackground = ButtonBackground.OPAQUE,
    onDismiss: () -> Unit,
    isVisible: Boolean = true,
    sheetHeight: Dp? = 400.dp,
    sheetGesturesEnabled: Boolean = true,
    showDragHandle: Boolean = true,
    showCloseButton: Boolean = true,
    dismissOnBackdropClick: Boolean = true,
    dampingRatio: Float = 0.82f,
    stiffness: Float = 300f,
    containerColor: Color = SurfacePrimary,
    containerBrush: Brush? = null,
    headerBackgroundImage: (@Composable () -> Unit)? = null,
    onProgress: ((Float) -> Unit)? = null,
    hasToast: Boolean = false,
    toast: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    if (!isVisible) return

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    // Dynamic height fallback calculation
    val defaultHeightPx = with(density) { (sheetHeight ?: 400.dp).toPx() + 80.dp.toPx() }
    var actualSheetHeightPx by remember(sheetHeight) { mutableFloatStateOf(defaultHeightPx) }

    // Spring physics spec configured from parameters
    val springSpec = remember(dampingRatio, stiffness) {
        spring<Float>(
            dampingRatio = dampingRatio,
            stiffness = stiffness
        )
    }

    val dpSpringSpec = remember(dampingRatio, stiffness) {
        spring<Dp>(
            dampingRatio = dampingRatio,
            stiffness = stiffness
        )
    }

    val topPadding by animateDpAsState(
        targetValue = if (hasToast) 84.dp else 16.dp,
        animationSpec = dpSpringSpec,
        label = "TopPaddingAnimation"
    )

    // Offset 0f = Fully Open, actualSheetHeightPx = Hidden off-screen
    val sheetOffsetY = remember { Animatable(defaultHeightPx) }
    var isDismissing by remember { mutableStateOf(false) }

    // Calculate normalized progress (0f = fully open, 1f = fully down/hidden)
    val progress = (sheetOffsetY.value / actualSheetHeightPx.coerceAtLeast(1f)).coerceIn(0f, 1f)
    val scrimAlpha = (0.55f * (1f - progress)).coerceIn(0f, 0.55f)

    val backgroundBrush = containerBrush ?: SolidColor(containerColor)
    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    LaunchedEffect(progress) {
        onProgress?.invoke(progress)
        // Auto-dismiss if we've reached the bottom, and we are in dismissing state
        // This is a safety measure in case the animation coroutine was interrupted
        if (progress >= 1f && isDismissing) {
            onDismiss()
        }
    }

    // Helper to dismiss with velocity-aware spring dismissal
    val dismissWithAnimation: (velocity: Float) -> Unit = { velocity ->
        if (!isDismissing) {
            isDismissing = true
            coroutineScope.launch {
                try {
                    sheetOffsetY.animateTo(
                        targetValue = actualSheetHeightPx,
                        animationSpec = springSpec,
                        initialVelocity = velocity
                    )
                } finally {
                    onDismiss()
                }
            }
        }
    }

    // Entrance animation when sheet becomes visible
    LaunchedEffect(Unit) {
        sheetOffsetY.snapTo(actualSheetHeightPx)
        sheetOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = springSpec
        )
    }

    val nestedScrollConnection = remember(actualSheetHeightPx, isDismissing, sheetGesturesEnabled) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!sheetGesturesEnabled || isDismissing) return Offset.Zero
                val delta = available.y
                if (delta < 0 && sheetOffsetY.value > 0f) {
                    val newOffset = (sheetOffsetY.value + delta).coerceAtLeast(0f)
                    val consumed = newOffset - sheetOffsetY.value
                    coroutineScope.launch { sheetOffsetY.snapTo(newOffset) }
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (!sheetGesturesEnabled || isDismissing) return Offset.Zero
                val delta = available.y
                if (delta > 0 && source == NestedScrollSource.UserInput) {
                    val newOffset = (sheetOffsetY.value + delta).coerceAtLeast(0f)
                    coroutineScope.launch { sheetOffsetY.snapTo(newOffset) }
                    return Offset(0f, delta)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (!sheetGesturesEnabled || isDismissing) return Velocity.Zero
                val velocity = available.y
                if (velocity > 800f || sheetOffsetY.value > actualSheetHeightPx * 0.28f) {
                    dismissWithAnimation(velocity)
                    return available
                } else if (sheetOffsetY.value > 0f) {
                    sheetOffsetY.animateTo(0f, springSpec)
                    return available
                }
                return Velocity.Zero
            }
        }
    }

    // Vertical drag gesture detector attached to body or top bar
    val dragGestureModifier = Modifier.pointerInput(Unit) {
        val velocityTracker = VelocityTracker()
        detectVerticalDragGestures(
            onDragStart = { velocityTracker.resetTracking() },
            onDragEnd = {
                val velocity = velocityTracker.calculateVelocity().y
                val currentOffset = sheetOffsetY.value

                if (velocity > 800f || currentOffset > actualSheetHeightPx * 0.28f) {
                    dismissWithAnimation(velocity)
                } else {
                    coroutineScope.launch {
                        sheetOffsetY.animateTo(0f, springSpec)
                    }
                }
            },
            onDragCancel = {
                coroutineScope.launch {
                    sheetOffsetY.animateTo(0f, springSpec)
                }
            },
            onVerticalDrag = { change, dragAmount ->
                change.consume()
                velocityTracker.addPosition(change.uptimeMillis, change.position)
                val newOffset = (sheetOffsetY.value + dragAmount).coerceAtLeast(0f)
                coroutineScope.launch {
                    sheetOffsetY.snapTo(newOffset)
                }
            }
        )
    }

    BackHandler(enabled = !isDismissing) {
        dismissWithAnimation(0f)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Synchronized backdrop scrim overlay
        if (scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .then(
                        if (dismissOnBackdropClick && !isDismissing) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                dismissWithAnimation(0f)
                            }
                        } else Modifier
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    val visibleSheetHeight = (actualSheetHeightPx - sheetOffsetY.value).coerceAtLeast(0f)
                    IntOffset(0, -(visibleSheetHeight).roundToInt())
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            toast()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = topPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .onGloballyPositioned { coordinates ->
                        if (coordinates.size.height > 0) {
                            actualSheetHeightPx = coordinates.size.height.toFloat()
                        }
                    }
                    .nestedScroll(nestedScrollConnection)
                    .offset { IntOffset(0, sheetOffsetY.value.roundToInt()) }
                    .then(
                        if (sheetGesturesEnabled && !isDismissing) {
                            dragGestureModifier
                        } else Modifier
                    )
                    .background(
                        brush = backgroundBrush,
                        shape = sheetShape
                    )
                    .clip(sheetShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .windowInsetsPadding(WindowInsets.ime)
                    .navigationBarsPadding()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Optional composable slot for rendering header background image/pattern
                    headerBackgroundImage?.invoke()

                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Top bar / Drag Handle & Header Area
                        // Attaches drag gesture if full-sheet gestures are disabled
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (!sheetGesturesEnabled && !isDismissing) {
                                        dragGestureModifier
                                    } else Modifier
                                )
                        ) {
                            if (showDragHandle) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(56.dp)
                                            .height(4.dp)
                                            .background(ContentTertiary, shape = SquircleShape(100))
                                    )
                                }
                            }

                            if (heading.isNotEmpty() || showCloseButton) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 12.dp,
                                            top = if (showDragHandle) 0.dp else 12.dp,
                                            bottom = 0.dp,
                                            end = 12.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (heading.isNotEmpty()) {
                                        Text(
                                            text = heading,
                                            style = headingStyle,
                                            color = ContentPrimary,
                                            lineHeight = headingLineHeight
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }

                                    if (showCloseButton) {
                                        TopBarIconButton(
                                            backgroundStyle = closeButtonBackgroundStyle,
                                            icon = TopIcon.Predefined.CLOSE,
                                            onClick = { dismissWithAnimation(0f) },
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Sheet Content Area
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (sheetHeight != null) Modifier.height(sheetHeight)
                                    else Modifier.wrapContentHeight()
                                )
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Custom Bottom Sheet Preview")
@Composable
fun CustomBottomSheetPreview() {
    var isSheetVisible by remember { mutableStateOf(true) }
    var sheetProgress by remember { mutableFloatStateOf(0f) }

    val backdropScale = 0.92f + (0.08f * sheetProgress)

    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfacePrimary)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Background Screen Scale: ${"%.2f".format(backdropScale)}")
                    Spacer(Modifier.height(16.dp))
                    androidx.compose.material3.Button(onClick = { isSheetVisible = true }) {
                        Text("Open Bottom Sheet")
                    }
                }
            }

            CustomBottomSheet(
                heading = "App-Wide Bottom Sheet",
                headingStyle = JasnifyTheme.typography.displayLarge,
                headingLineHeight = 40.sp,
                closeButtonBackgroundStyle = ButtonBackground.OPAQUE,
                isVisible = isSheetVisible,
                dampingRatio = 0.82f,
                stiffness = 300f,
                showDragHandle = false,
                showCloseButton = true,
                containerBrush = Brush.verticalGradient(
                    colors = listOf(
                        ContentTertiary,
                        ContentTertiary.copy(alpha = 0.85f)
                    )
                ),
                onProgress = { progress -> sheetProgress = progress },
                onDismiss = { isSheetVisible = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Clean, responsive spring dynamics configured with dampingRatio = 0.82f and stiffness = 300f.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}