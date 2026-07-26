package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.OptionSelector
import com.harshdeep.jasnify.theme.*

enum class NavBarStyleOption(val label: String) {
    PILL_SHAPED("Pill Shaped"),
    BASIC("Basic")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBarStyleBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    currentStyle: NavBarStyleOption,
    onStyleSelected: (NavBarStyleOption) -> Unit
) {
    CustomBottomSheet(
        heading = "Nav Bar Style",
        sheetState = sheetState,
        onDismiss = onDismiss
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
                    .clip(RoundedCornerShape(CornerExtraLarge))
                    .background(SurfaceBrandSecondary)
                    .innerShadow(
                        shape = RoundedCornerShape(CornerExtraLarge),
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
                            .clip(
                                RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 0.dp,
                                    bottomStart = CornerExtraLarge,
                                    bottomEnd = CornerExtraLarge,
                                )
                            )
                            .background(ContentInvPrimary)
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = CornerLargeIncrease,
                                        bottomEnd = CornerLargeIncrease
                                    )
                                )
                                .background(SurfaceSecondary)
                        ) {
                            // Main Mockup Content
                            if (selectedStyle == NavBarStyleOption.PILL_SHAPED) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Floating Pill Navigation Bar
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp)
                                                .shadow(
                                                    elevation = 10.dp,
                                                    shape = CircleShape,
                                                    spotColor = ContentPrimary.copy(alpha = 0.20f),
                                                    ambientColor = ContentPrimary.copy(alpha = 0.08f)
                                                )
                                                .clip(CircleShape)
                                                .background(SurfacePrimary)
                                                .padding(horizontal = 4.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Item 1: Home (Selected Pill Inset)
                                            Box(
                                                modifier = Modifier.weight(1f),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(38.dp)
                                                        .clip(CircleShape)
                                                        .background(SurfaceBrandPrimary),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_home),
                                                        contentDescription = "Home",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(19.dp)
                                                    )
                                                }
                                            }

                                            // Item 2: Inspirations
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_inspirations),
                                                    contentDescription = "Inspirations",
                                                    tint = ContentSecondary,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }

                                            // Item 3: Checklists
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_checklists),
                                                    contentDescription = "Checklists",
                                                    tint = ContentSecondary,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }

                                            // Item 4: Vendors
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_vendor),
                                                    contentDescription = "Vendors",
                                                    tint = ContentSecondary,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }

                                            // Item 5: Profile
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_profile),
                                                    contentDescription = "Profile",
                                                    tint = ContentSecondary,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Bottom Handle Bar Indicator
                                        Box(
                                            modifier = Modifier
                                                .width(64.dp)
                                                .height(3.dp)
                                                .clip(CircleShape)
                                                .background(SurfaceSecondary)
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

                                    // Bottom Navigation Area with White Background in Basic style
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(
                                                elevation = 10.dp,
                                                spotColor = ContentPrimary.copy(alpha = 0.20f),
                                                ambientColor = ContentPrimary.copy(alpha = 0.08f)
                                            )
                                            .background(SurfacePrimary)
                                            .padding(top = 6.dp, bottom = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Selected Item
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .width(44.dp)
                                                            .height(24.dp)
                                                            .clip(CircleShape)
                                                            .background(SurfaceBrandSecondary),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.ic_home),
                                                            contentDescription = "Home",
                                                            tint = ContentBrandDark,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "Home",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        ),
                                                        color = ContentBrandDark
                                                    )
                                                }

                                                // Item 2: Inspiration
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_inspirations),
                                                        contentDescription = "Inspiration",
                                                        tint = ContentSecondary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "Inspiration",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = ContentSecondary
                                                    )
                                                }

                                                // Item 3: Checklist
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_checklists),
                                                        contentDescription = "Checklist",
                                                        tint = ContentSecondary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "Checklist",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = ContentSecondary
                                                    )
                                                }

                                                // Item 4: Vendors
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_vendor),
                                                        contentDescription = "Vendors",
                                                        tint = ContentSecondary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "Vendors",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = ContentSecondary
                                                    )
                                                }

                                                // Item 5: Profile
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_profile),
                                                        contentDescription = "Profile",
                                                        tint = ContentSecondary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "Profile",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = ContentSecondary
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            // Bottom Handle Bar Indicator
                                            Box(
                                                modifier = Modifier
                                                    .width(64.dp)
                                                    .height(3.dp)
                                                    .clip(CircleShape)
                                                    .background(SurfaceSecondary)
                                            )
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

                // Top vertical gradient inner shadow overlay preserved at the top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ContentPrimary.copy(alpha = 0.15f),
                                    ContentPrimary.copy(alpha = 0.04f),
                                    Color.Transparent
                                )
                            )
                        )
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
            color = MaterialTheme.colorScheme.outline.copy(0.16f)
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

    // Clip all inner shadow drawing within the bounding shape path
    clipPath(Path().apply { addOutline(outline) }) {
        drawIntoCanvas { canvas ->
            val strokeWidth = blur.toPx() * 2f + spread.toPx()

            val shadowPaint = Paint().apply {
                this.color = color
                this.style = PaintingStyle.Stroke
                this.strokeWidth = strokeWidth
            }

            // Apply blur filter on the inner stroke edge
            shadowPaint.asFrameworkPaint().apply {
                isAntiAlias = true
                if (blur.toPx() > 0) {
                    maskFilter = BlurMaskFilter(
                        blur.toPx(),
                        BlurMaskFilter.Blur.NORMAL
                    )
                }
            }

            val shadowPath = Path().apply {
                addOutline(outline)
            }

            // Offset canvas if needed for directional inner shadows
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