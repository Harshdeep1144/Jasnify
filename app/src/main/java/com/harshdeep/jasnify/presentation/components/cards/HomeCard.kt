package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import sv.lib.squircleshape.SquircleShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.TextStyle
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
    insight: String,
    heading: String,
    illustration: Painter,
    cardBgColor : Color,
    waveColor : Color,
    insightColor : Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .width(190.dp)
            .defaultMinSize(minWidth = 180.dp, minHeight = 172.dp)
            .height(172.dp)
            .border(
                width = 1.dp,
                color = insightColor.copy(alpha = 0.1f),
                shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
            )
            .clickable(
                onClick = onClick
            )
            .clip(SquircleShape(CornerLarge, CornerSmoothingDefault)),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor,
            contentColor = ContentPrimary
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(130.dp)
                    .zIndex(1f)
                    .align(Alignment.BottomCenter)
                    .background(color = Color.Transparent)
            ){
                Image(
                    painter = painterResource(R.drawable.bg_wave),
                    contentDescription = "wave background",
                    alignment = Alignment.BottomCenter,
                    colorFilter = ColorFilter.tint(waveColor),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(3f)
                    .padding(16.dp)
            ) {
                Text(
                    text = heading,
                    color = ContentPrimary,
                    style = JasnifyTheme.typography.headingLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = insight,
                    color = insightColor,
                    style = JasnifyTheme.typography.labelSmall
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = illustration,
                        contentDescription = null,
                        modifier = Modifier.height(80.dp),
                        contentScale = ContentScale.FillHeight
                    )
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeCard(
                    insight = "See cards for guests",
                    heading = "Cards & Guests",
                    illustration = painterResource(R.drawable.ill_budget_tracker_card),
                    modifier = Modifier.fillMaxWidth(),
                    insightColor = Color.White,
                    cardBgColor = Color.Gray,
                    waveColor = Color.DarkGray,
                    onClick = {}
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeCard(
                    insight = "See cards for guests",
                    heading = "Cards & Guests",
                    illustration = painterResource(R.drawable.ill_cards_and_guests_card),
                    modifier = Modifier.weight(1f),
                    insightColor = Color.White,
                    cardBgColor = Color.Gray,
                    waveColor = Color.DarkGray,
                    onClick = {}
                )

                HomeCard(
                    insight = "See cards for guests",
                    heading = "Cards & Guests",
                    illustration = painterResource(R.drawable.ill_cards_and_guests_card),
                    modifier = Modifier.weight(1f),
                    insightColor = Color.White,
                    cardBgColor = Color.Gray,
                    waveColor = Color.DarkGray,
                    onClick = {}
                )
            }

        }
    }
}
