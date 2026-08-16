package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.FontStyleType
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

@Composable
fun MyCardsEmptyState(
    canEdit: Boolean,
    onStartEditing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition(label = "EmptyStateLoop")

    // 1. Continuous Left-to-Right Card Arc Progress (0f -> 1f)
    val cardScrollProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 50000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CardArcLoop"
    )

    // 2. Continuous Left-to-Right Font Pills Marquee Progress
    val fontScrollProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FontMarqueeLoop"
    )

    // 3. Water Floating/Buoyancy Animation for Text Box (Y-drift, X-sway, slight tilt)
    val textBoxFloatY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TextBoxFloatY"
    )
    val textBoxFloatX by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TextBoxFloatX"
    )
    val textBoxRotation by infiniteTransition.animateFloat(
        initialValue = -2.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TextBoxRotation"
    )

    // 4. Cursor Blink Animation
    val cursorBlink by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CursorBlink"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFDFD0).copy(alpha = 0.70f),
                        Color(0xFFFFE2D5).copy(alpha = 0.64f),
                        Color(0xFFFFEAE0).copy(alpha = 0.50f),
                        Color(0xFFFFEFE8).copy(alpha = 0.40f),
                        Color(0xFFFFF8F4).copy(alpha = 0.15f),
                        BackgroundPrimary
                    ),
                    center = Offset.Unspecified,
                    radius = with(density) { 450.dp.toPx() }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-40).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // -------------------------------------------------------------
            // 1. Gentle Curved Card Arc (Moving L -> R) - Shadow Removed
            // -------------------------------------------------------------
            val cardDrawables = listOf(
                R.drawable.bg_invitation_card_01,
                R.drawable.bg_invitation_card_03,
                R.drawable.bg_invitation_card_02,
                R.drawable.bg_invitation_card_04,
                R.drawable.bg_invitation_card_05,
                R.drawable.bg_invitation_card_01,
                R.drawable.bg_invitation_card_03,
                R.drawable.bg_invitation_card_02
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                val totalCards = cardDrawables.size
                val cardSpacingDp = 120f
                val totalWidth = totalCards * cardSpacingDp

                cardDrawables.forEachIndexed { index, drawableRes ->
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                val rawX = (cardScrollProgress * totalWidth + (index * cardSpacingDp)) % totalWidth
                                val xPos = rawX - (totalWidth / 2f)

                                val yPos = (xPos * xPos) / 1250f
                                val rotationDeg = xPos * 0.082f
                                val edgeFade = (1f - (kotlin.math.abs(xPos) - 130f) / 100f).coerceIn(0f, 1f)

                                translationX = xPos * density.density
                                translationY = (yPos - 6f) * density.density
                                rotationZ = rotationDeg
                                alpha = if (kotlin.math.abs(xPos) < 230f) edgeFade else 0f
                            }
                            .size(91.53.dp, 122.04.dp)
                            .clip(SquircleShape(CornerMedium, CornerSmoothingDefault)) // Shadow modifier removed
                    ) {
                        Image(
                            painter = painterResource(id = drawableRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // -------------------------------------------------------------
            // 2. Water Floating Text Box
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = textBoxFloatX * density.density
                        translationY = textBoxFloatY * density.density
                        rotationZ = textBoxRotation
                    }
                    .wrapContentSize()
            ) {
                // Main Text Box
                Box(
                    modifier = Modifier
                        .background(Color.Transparent, SquircleShape(CornerExtraSmall))
                        .border(2.dp, Color(0xFF6750A4), SquircleShape(CornerExtraSmall))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "New Text",
                            style = JasnifyTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontStyleType.DEFAULT.fontFamily,
                                fontSize = 18.sp
                            ),
                            color = ContentPrimary
                        )
                        Text(
                            text = "|",
                            style = JasnifyTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp
                            ),
                            color = if (cursorBlink > 0.45f) ContentPrimary else Color.Transparent
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                // Code Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 12.dp)
                        .size(width = 24.dp, height = 16.dp)
                        .background(Color(0xFF6750A4), RoundedCornerShape(100)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        tint = ContentInvPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Pen Icon
                Icon(
                    painter = painterResource(R.drawable.ic_pen),
                    contentDescription = null,
                    tint = Color(0xFF6750A4),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 24.dp, y = 24.dp)
                        .size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            val fontItems = listOf(
                "Grenz Gotisch" to FontStyleType.PATTAYA,
                "Cal Sans" to FontStyleType.DEFAULT,
                "Grenze Gotisch" to FontStyleType.PATTAYA,
                "Pacifico" to FontStyleType.PATTAYA,
                "Marcellus" to FontStyleType.SERIF
            )

            InfiniteHorizontalMarquee(
                progress = fontScrollProgress,
                modifier = Modifier.fillMaxWidth()
            ) {
                val displayList = fontItems + fontItems + fontItems
                displayList.forEach { (name, type) ->
                    Surface(
                        color = Color(0x40FFFFFF),
                        shape = RoundedCornerShape(100),
                        border = BorderStroke(1.dp, ContentSecondary),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = name,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = JasnifyTheme.typography.labelLarge.copy(
                                fontFamily = type.fontFamily,
                                fontSize = 13.sp
                            ),
                            color = ContentSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Choose a theme, Explore\nfonts, and Tweak texts\nhowever you like.",
                style = JasnifyTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                ),
                color = ContentPrimary,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (canEdit) {
                CustomTextButton(
                    text = "Start Editing",
                    onClick = onStartEditing,
                    modifier = Modifier
                        .width(149.dp)
                        .height(56.dp),
                    containerColor = ContentPrimary,
                    contentColor = ContentInvPrimary,
                    shapeStyle = ButtonShapeStyle.Round
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Choose a template and create\ncards that match your vibe.",
                style = JasnifyTheme.typography.labelMedium,
                color = ContentTertiary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun InfiniteHorizontalMarquee(
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier.clipToBounds()
    ) { measurables, constraints ->
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(childConstraints) }

        val totalWidth = placeables.sumOf { it.width }
        val maxHeight = placeables.maxOfOrNull { it.height } ?: 0

        val singleCycleWidth = if (totalWidth > 0) totalWidth / 3 else 1
        val currentOffset = (progress * singleCycleWidth).toInt()

        layout(constraints.maxWidth, maxHeight) {
            var xPosition = -singleCycleWidth + currentOffset

            placeables.forEach { placeable ->
                placeable.placeRelative(x = xPosition, y = (maxHeight - placeable.height) / 2)
                xPosition += placeable.width
            }
        }
    }
}