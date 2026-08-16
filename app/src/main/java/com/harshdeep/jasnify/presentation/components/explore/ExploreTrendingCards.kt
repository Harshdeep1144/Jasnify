package com.harshdeep.jasnify.presentation.components.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.getGoogleFontFamily
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary

@Composable
fun ExploreTrendingCards(modifier: Modifier = Modifier) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(102.48.dp)
            .background(Color.Transparent)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Left Card Fan ---
        Box {
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = "EXPLORE",
                style = JasnifyTheme.typography.labelSmall,
                color = Color(0xFF4D2A15),
            )

            Text(
                text = "TRENDING CARDS",
                style = JasnifyTheme.typography.displayLarge.copy(
                    fontFamily = getGoogleFontFamily("Afacad Flux"),
                    shadow = Shadow(
                        color = Color(0x664D2A15),
                        offset = Offset(0f, -5f),
                        blurRadius = 6f
                    )
                ),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4D2A15),
            )
        }

        // --- Right Card Fan ---
        Box {
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
    width: Dp = 41.44.dp,
    height: Dp = 55.26.dp,
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