package com.harshdeep.jasnify.presentation.components.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun ExploreTrendingCards(modifier: Modifier = Modifier) {
    val gradientColors = listOf(
        Color(0xFFFBF8F5),
        Color(0xFFF7E9DE),
        Color(0xFFEFE0D3)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .background(brush = Brush.verticalGradient(colors = gradientColors))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- Left Card Fan ---
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            // Back Card
            TrendingCardItem(
                imageResId = R.drawable.img_trending_card_01,
                rotationDegrees = -12f,
                offsetX = 8.dp,
                offsetY = (-12).dp
            )

            // Front Card
            TrendingCardItem(
                imageResId = R.drawable.img_trending_card_04,
                rotationDegrees = -28f,
                offsetX = (-4).dp,
                offsetY = 8.dp
            )
        }

        // --- Center Typography ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = "EXPLORE",
                style = JasnifyTheme.typography.labelSmall,
                color = Color(0xFF4A2B20),
                letterSpacing = 2.sp
            )

            Text(
                text = "TRENDING CARDS",
                style = JasnifyTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF331B14),
                letterSpacing = 0.5.sp
            )
        }

        // --- Right Card Fan ---
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            // Back Card
            TrendingCardItem(
                imageResId = R.drawable.img_trending_card_03,
                rotationDegrees = 12f,
                offsetX = (-8).dp,
                offsetY = (-12).dp
            )
            // Front Card
            TrendingCardItem(
                imageResId = R.drawable.img_trending_card_02,
                rotationDegrees = 28f,
                offsetX = 4.dp,
                offsetY = 8.dp
            )
        }
    }
}

@Composable
private fun TrendingCardItem(
    imageResId: Int,
    rotationDegrees: Float,
    modifier: Modifier = Modifier,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    width: Dp = 46.dp,
    height: Dp = 62.dp,
) {
    val cardShape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .offset(x = offsetX, y = offsetY)
            .graphicsLayer {
                this.rotationZ = rotationDegrees
                this.clip = false
            }
            .shadow(
                elevation = 6.dp,
                shape = cardShape,
                clip = false
            )
            .size(width = width, height = height)
            .background(color = ContentInvPrimary, shape = cardShape)
            .border(width = 2.dp, color = ContentInvPrimary, shape = cardShape)
            .clip(cardShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
private fun ExploreTrendingCardsPreview() {
    ExploreTrendingCards()
}