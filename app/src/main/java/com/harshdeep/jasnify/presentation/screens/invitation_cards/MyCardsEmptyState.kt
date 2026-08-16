package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.FontStyleType
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun MyCardsEmptyState(
    canEdit: Boolean,
    onStartEditing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFDFD0).copy(alpha = 0.65f),
                        Color(0xFFFFF3EC).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset.Unspecified,
                    radius = with(density) { 340.dp.toPx() }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Spaced 5-Card Fan Arc
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                contentAlignment = Alignment.Center
            ) {
                // Far Left Card
                DecorativeFanCard(
                    drawableRes = R.drawable.bg_invitation_card_01,
                    offsetX = (-220).dp,
                    offsetY = 36.dp,
                    rotation = -20f,
                    width = 88.dp,
                    height = 124.dp
                )

                // Mid Left Card
                DecorativeFanCard(
                    drawableRes = R.drawable.bg_invitation_card_03,
                    offsetX = (-118).dp,
                    offsetY = 12.dp,
                    rotation = -10f,
                    width = 92.dp,
                    height = 130.dp
                )

                // Far Right Card
                DecorativeFanCard(
                    drawableRes = R.drawable.bg_invitation_card_04,
                    offsetX = 220.dp,
                    offsetY = 36.dp,
                    rotation = 20f,
                    width = 88.dp,
                    height = 124.dp
                )

                // Mid Right Card
                DecorativeFanCard(
                    drawableRes = R.drawable.bg_invitation_card_05,
                    offsetX = 118.dp,
                    offsetY = 12.dp,
                    rotation = 10f,
                    width = 92.dp,
                    height = 130.dp
                )

                // Center Highlight Card
                DecorativeFanCard(
                    drawableRes = R.drawable.bg_invitation_card_02,
                    offsetX = 0.dp,
                    offsetY = (-6).dp,
                    rotation = 0f,
                    width = 98.dp,
                    height = 138.dp,
                    elevation = 8.dp
                )
            }

            // 2. Decorative Selected Text Badge with Pen Pointer
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .wrapContentSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(6.dp))
                        .border(1.5.dp, Color(0xFF6750A4), RoundedCornerShape(6.dp))
                        .padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Text|",
                        style = JasnifyTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontStyleType.DEFAULT.fontFamily
                        ),
                        color = Color(0xFF1E1E1E)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color(0xFF6750A4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Icon(
                    painter = painterResource(R.drawable.ic_edit_pen),
                    contentDescription = null,
                    tint = Color(0xFF6750A4),
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = 10.dp, y = 28.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 3. Horizontal Font Style Pills
            val fonts = listOf(
                "Grenz Gotisch" to FontStyleType.PATTAYA,
                "Cal Sans" to FontStyleType.DEFAULT,
                "Grenze Gotisch" to FontStyleType.PATTAYA,
                "Pacifico" to FontStyleType.PATTAYA,
                "Marcellus" to FontStyleType.SERIF
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                fonts.forEach { (name, type) ->
                    Surface(
                        color = Color.White.copy(alpha = 0.85f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Text(
                            text = name,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                            style = JasnifyTheme.typography.labelLarge.copy(fontFamily = type.fontFamily),
                            color = ContentSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 4. Headline
            Text(
                text = "Choose a theme, Explore\nfonts, and Tweak texts\nhowever you like.",
                style = JasnifyTheme.typography.headingXLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                ),
                color = ContentPrimary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Action Button & Subtitle
            if (canEdit) {
                CustomTextButton(
                    text = "Start Editing",
                    onClick = onStartEditing,
                    modifier = Modifier
                        .width(180.dp)
                        .height(52.dp),
                    containerColor = Color(0xFF141218),
                    contentColor = Color.White,
                    shapeStyle = ButtonShapeStyle.Round
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Choose a template and create\ncards that match your vibe.",
                style = JasnifyTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp
                ),
                color = ContentSecondary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 36.dp)
            )
        }
    }
}

@Composable
private fun DecorativeFanCard(
    drawableRes: Int,
    offsetX: Dp,
    offsetY: Dp,
    rotation: Float,
    width: Dp,
    height: Dp,
    elevation: Dp = 6.dp
) {
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .graphicsLayer { rotationZ = rotation }
            .size(width, height)
            .shadow(elevation, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
