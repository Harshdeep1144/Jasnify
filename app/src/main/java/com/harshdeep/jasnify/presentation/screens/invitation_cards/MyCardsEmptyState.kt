package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.FontStyleType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape
import kotlin.math.abs

private val CardDrawables = listOf(
    R.drawable.bg_invitation_card_01,
    R.drawable.bg_invitation_card_03,
    R.drawable.bg_invitation_card_02,
    R.drawable.bg_invitation_card_04,
    R.drawable.bg_invitation_card_05,
    R.drawable.bg_invitation_card_01,
    R.drawable.bg_invitation_card_03,
    R.drawable.bg_invitation_card_02
)

private val FontItems = listOf(
    "Cal Sans" to FontStyleType.DEFAULT,
    "Grenze Gotisch" to FontStyleType.PATTAYA,
    "Pacifico" to FontStyleType.PATTAYA,
    "Marcellus" to FontStyleType.SERIF
)

private val DisplayFontItems = FontItems + FontItems + FontItems

private val RadialGradientColorStops = arrayOf(
    0.00f to Color(0xFFF7CBBA).copy(alpha = 0.75f),
    0.28f to Color(0xFFF3C4B0).copy(alpha = 0.55f),
    0.55f to Color(0xFFF6D6C7).copy(alpha = 0.25f),
    0.75f to Color(0xFFFBF0EA).copy(alpha = 0.15f),
    1.00f to BackgroundPrimary
)

private val CardShape = SquircleShape(CornerMedium, CornerSmoothingDefault)
private val SelectionPurple = Color(0xFF6750A4)
private val FontPillShape = RoundedCornerShape(100)
private val FontPillBorder = BorderStroke(1.dp, Color(0x2B000000))

@Composable
fun MyCardsEmptyState(
    canEdit: Boolean,
    onStartEditing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition(label = "EmptyStateLoop")

    val cardScrollProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CardArcLoop"
    )

    val fontScrollProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FontMarqueeLoop"
    )

    val textBoxFloatY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TextBoxFloatY"
    )
    val textBoxFloatX by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TextBoxFloatX"
    )
    val textBoxRotation by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TextBoxRotation"
    )

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
            .drawWithCache {
                // Darker and more concentrated gradient near top
                val radialBrush = Brush.radialGradient(
                    colorStops = RadialGradientColorStops,
                    radius = 480.dp.toPx()
                )
                onDrawBehind {
                    drawRect(brush = radialBrush)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-80).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Curved Card Arc
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                val totalCards = CardDrawables.size
                val cardSpacingDp = 120f
                val totalWidth = totalCards * cardSpacingDp

                CardDrawables.forEachIndexed { index, drawableRes ->
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                val rawX = (cardScrollProgress * totalWidth + (index * cardSpacingDp)) % totalWidth
                                val xPos = rawX - (totalWidth / 2f)

                                val yPos = (xPos * xPos) / 1300f
                                val rotationDeg = xPos * 0.082f
                                val edgeFade = (1f - (abs(xPos) - 130f) / 100f).coerceIn(0f, 1f)

                                translationX = xPos * density.density
                                translationY = (yPos - 6f) * density.density
                                rotationZ = rotationDeg

                                // Reduced card opacity
                                alpha = if (abs(xPos) < 230f) (edgeFade * 0.5f) else 0f
                            }
                            .size(90.dp, 120.dp)
                            .clip(CardShape)
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

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Floating Dashed Text Box with Transformation Handles
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = textBoxFloatX * density.density
                        translationY = textBoxFloatY * density.density
                        rotationZ = textBoxRotation
                    }
                    .wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                            drawRect(
                                color = SelectionPurple,
                                style = Stroke(width = strokeWidth, pathEffect = pathEffect)
                            )
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "New Text",
                            style = JasnifyTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontStyleType.SERIF.fontFamily,
                                fontSize = 19.sp
                            ),
                            color = ContentPrimary
                        )
                        Text(
                            text = "|",
                            style = JasnifyTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Light,
                                fontSize = 20.sp
                            ),
                            color = if (cursorBlink > 0.45f) ContentPrimary else Color.Transparent
                        )
                    }
                }

                // Four Corner Handles
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(8.dp)
                        .background(SelectionPurple)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                        .size(8.dp)
                        .background(SelectionPurple)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(8.dp)
                        .background(SelectionPurple)
                )

                // Code Pill Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 4.dp)
                        .size(width = 24.dp, height = 16.dp)
                        .background(SelectionPurple, RoundedCornerShape(100)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        tint = ContentInvPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }

                // Grab Hand Icon
                Icon(
                    painter = painterResource(id = R.drawable.ic_grab_hand),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 12.dp, y = 12.dp)
                        .size(24.dp)
                        .rotate(-15f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 3. Horizontal Font Pills Marquee
            InfiniteHorizontalMarquee(
                progress = fontScrollProgress,
                modifier = Modifier.fillMaxWidth()
            ) {
                DisplayFontItems.forEach { (name, type) ->
                    Surface(
                        color = Color(0x20FFFFFF),
                        shape = FontPillShape,
                        border = FontPillBorder,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = name,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                            style = JasnifyTheme.typography.labelLarge.copy(
                                fontFamily = type.fontFamily,
                                fontSize = 14.sp
                            ),
                            color = ContentSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 4. Highlighted Multi-color Title
            val annotatedTitle = buildAnnotatedString {
                append("Choose a ")
                withStyle(SpanStyle(color = Color(0xFFA65C2B), fontWeight = FontWeight.Medium)) {
                    append("Theme")
                }
                append(", Explore\n")
                withStyle(SpanStyle(color = Color(0xFF3B82F6), fontWeight = FontWeight.Medium)) {
                    append("Fonts")
                }
                append(", and Tweak ")
                withStyle(SpanStyle(color = Color(0xFF6B53B4), fontWeight = FontWeight.Medium)) {
                    append("Texts")
                }
                append("\nhowever you like.")
            }

            Text(
                text = annotatedTitle,
                style = JasnifyTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                ),
                color = ContentPrimary,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Button
            if (canEdit) {
                CustomTextButton(
                    onClick = onStartEditing,
                    text = "Start Editing",
                    leadingIcon = painterResource(R.drawable.ic_pen),
                    containerColor = ContentPrimary,
                    contentColor = ContentInvPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Subtitle
            Text(
                text = "Choose a template and create\ncards that match your vibe.",
                style = JasnifyTheme.typography.labelMedium.copy(
                    lineHeight = 14.sp
                ),
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