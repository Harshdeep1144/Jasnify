package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.OptionSelector
import com.harshdeep.jasnify.presentation.components.scaffold.BottomNavBarContent
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary

private val PreviewContainerShape = RoundedCornerShape(CornerExtraLarge)

private val MockupOuterShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = CornerExtraLarge,
    bottomEnd = CornerExtraLarge
)

private val MockupInnerShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = CornerLargeIncrease,
    bottomEnd = CornerLargeIncrease
)

private val StaticNavItems = listOf(
    Screen.HomeTabScreen.Home,
    Screen.HomeTabScreen.Vendors,
    Screen.HomeTabScreen.Checklists,
    Screen.HomeTabScreen.Guests,
    Screen.HomeTabScreen.Profile
)

private val TopInnerShadowBrush = Brush.verticalGradient(
    colors = listOf(
        ContentPrimary.copy(alpha = 0.15f),
        ContentPrimary.copy(alpha = 0.04f),
        Color.Transparent
    )
)

enum class NavBarStyleOption(val label: String) {
    PILL_SHAPED("Pill Shaped"),
    BASIC("Basic")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBarStyleBottomSheet(
    onDismiss: () -> Unit,
    currentStyle: NavBarStyleOption,
    onStyleSelected: (NavBarStyleOption) -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    CustomBottomSheet(
        heading = "Nav Bar Style",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = true,
        showCloseButton = true
    ) {
        NavBarStyleContent(
            currentStyle = currentStyle,
            onStyleSelected = onStyleSelected,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun NavBarStyleContent(
    currentStyle: NavBarStyleOption,
    onStyleSelected: (NavBarStyleOption) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStyle by remember { mutableStateOf(currentStyle) }
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Preview Box Container with subtle inner shadow on all sides + top shadow gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PreviewContainerShape)
                    .background(SurfaceBrandSecondary)
                    .innerShadow(
                        shape = PreviewContainerShape,
                        color = ContentPrimary.copy(alpha = 0.05f),
                        blur = 10.dp,
                        spread = 6.dp
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Device Mockup Frame Outer Bezel
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.90f)
                            .height(145.dp)
                            .clip(MockupOuterShape)
                            .background(ContentInvPrimary)
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(MockupInnerShape)
                                .background(SurfaceSecondary)
                        ) {
                            // Main Mockup Content
                            AnimatedContent(
                                targetState = selectedStyle,
                                label = "nav_bar_style_transition",
                                transitionSpec = {
                                    (fadeIn() + slideInVertically { it / 2 }) togetherWith
                                            (fadeOut() + slideOutVertically { it / 2 })
                                },
                                modifier = Modifier.fillMaxSize()
                            ) { targetStyle ->
                                if (targetStyle == NavBarStyleOption.PILL_SHAPED) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                                        ) {
                                            // Actual Pill Navigation Bar scaled down to fit mockup width
                                            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                                val scale = 0.8f
                                                BottomNavBarContent(
                                                    selectedIndex = selectedNavIndex,
                                                    onItemSelected = { selectedScreen ->
                                                        val index = StaticNavItems.indexOf(selectedScreen)
                                                        if (index != -1) {
                                                            selectedNavIndex = index
                                                        }
                                                    },
                                                    navItems = StaticNavItems,
                                                    style = NavBarStyleOption.PILL_SHAPED,
                                                    applyPadding = false,
                                                    modifier = Modifier
                                                        .requiredWidth(maxWidth / scale)
                                                        .graphicsLayer {
                                                            scaleX = scale
                                                            scaleY = scale
                                                            transformOrigin = TransformOrigin(0.5f, 1f)
                                                        }
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(18.dp))

                                            // Bottom Handle Bar Indicator
                                            Box(
                                                modifier = Modifier
                                                    .width(108.dp)
                                                    .height(4.dp)
                                                    .clip(CircleShape)
                                                    .background(SurfacePrimary)
                                            )
                                        }
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .background(SurfaceSecondary)
                                        )

                                        // Actual Basic Navigation Bar scaled down
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                                    val scale = 0.8f
                                                    BottomNavBarContent(
                                                        selectedIndex = selectedNavIndex,
                                                        onItemSelected = { selectedScreen ->
                                                            val index = StaticNavItems.indexOf(selectedScreen)
                                                            if (index != -1) {
                                                                selectedNavIndex = index
                                                            }
                                                        },
                                                        navItems = StaticNavItems,
                                                        style = NavBarStyleOption.BASIC,
                                                        applyPadding = false,
                                                        modifier = Modifier
                                                            .requiredWidth(maxWidth / scale)
                                                            .graphicsLayer {
                                                                scaleX = scale
                                                                scaleY = scale
                                                                transformOrigin = TransformOrigin(0.5f, 1f)
                                                            }
                                                    )
                                                }

                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(BackgroundPrimary),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    // Bottom Handle Bar Indicator
                                                    Box(
                                                        modifier = Modifier
                                                            .width(108.dp)
                                                            .height(4.dp)
                                                            .clip(CircleShape)
                                                            .background(SurfaceSecondary)
                                                    )

                                                    Spacer(modifier = Modifier.height(8.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preview Text Label
                    Text(
                        text = "Preview",
                        style = JasnifyTheme.typography.headingMedium,
                        color = ContentBrandDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Top vertical gradient inner shadow overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(brush = TopInnerShadowBrush)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Option 1: Pill Shaped
                OptionSelector(
                    label = NavBarStyleOption.PILL_SHAPED.label,
                    isSelected = selectedStyle == NavBarStyleOption.PILL_SHAPED,
                    onClick = { selectedStyle = NavBarStyleOption.PILL_SHAPED },
                    modifier = Modifier.weight(1f)
                )

                // Option 2: Basic
                OptionSelector(
                    label = NavBarStyleOption.BASIC.label,
                    isSelected = selectedStyle == NavBarStyleOption.BASIC,
                    onClick = { selectedStyle = NavBarStyleOption.BASIC },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            CustomTextButton(
                onClick = {
                    onStyleSelected(selectedStyle)
                    onDismiss()
                },
                text = "Confirm",
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun Modifier.innerShadow(
    shape: Shape = RoundedCornerShape(24.dp),
    color: Color = Color.Black.copy(alpha = 0.05f),
    blur: Dp = 10.dp,
    spread: Dp = 6.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp
): Modifier = this.drawWithContent {
    drawContent()

    val outline = shape.createOutline(size, layoutDirection, this)
    val shadowPath = Path().apply { addOutline(outline) }

    clipPath(shadowPath) {
        drawIntoCanvas { canvas ->
            val strokeWidth = blur.toPx() * 2f + spread.toPx()

            val shadowPaint = Paint().apply {
                this.color = color
                this.style = PaintingStyle.Stroke
                this.strokeWidth = strokeWidth
            }

            shadowPaint.asFrameworkPaint().apply {
                isAntiAlias = true
                if (blur.toPx() > 0) {
                    maskFilter = BlurMaskFilter(
                        blur.toPx(),
                        BlurMaskFilter.Blur.NORMAL
                    )
                }
            }

            if (offsetX.toPx() != 0f || offsetY.toPx() != 0f) {
                canvas.save()
                canvas.translate(offsetX.toPx(), offsetY.toPx())
                canvas.drawPath(shadowPath, shadowPaint)
                canvas.restore()
            } else {
                canvas.drawPath(shadowPath, shadowPaint)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun NavBarStyleBottomSheetPreview() {
    JasnifyTheme {
        Surface {
            NavBarStyleContent(
                currentStyle = NavBarStyleOption.BASIC,
                onStyleSelected = {},
                onDismiss = {}
            )
        }
    }
}