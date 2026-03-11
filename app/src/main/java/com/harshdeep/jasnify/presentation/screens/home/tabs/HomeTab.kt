package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.presentation.components.cards.BudgetTrackerCard
import com.harshdeep.jasnify.presentation.components.cards.HomeCard
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.HomeTopBar
import com.harshdeep.jasnify.presentation.components.sections.VendorsCarousel
import com.harshdeep.jasnify.theme.BackgroundSecondary
import kotlin.math.roundToInt

private val FADE_DISTANCE_DP = 160.dp
private val HEADER_HEIGHT = 350.dp
private const val PARALLAX_RATE = 0.5f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeTab(
    onBudgetClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val fadeDistancePx = with(LocalDensity.current) { FADE_DISTANCE_DP.toPx() }
    val scrollOffset = scrollState.value

    val topBarAlpha = (scrollOffset / fadeDistancePx).coerceIn(0f, 1f)
    val bgImageAlpha = (1f - (scrollOffset / fadeDistancePx)).coerceIn(0f, 1f)
    val parallaxOffset = (scrollOffset * PARALLAX_RATE).roundToInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSecondary)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = "Background image of a crowd",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(HEADER_HEIGHT)
                .align(Alignment.TopCenter)
                .offset { IntOffset(x = 0, y = -parallaxOffset) }
                .alpha(bgImageAlpha)
        )

        Scaffold(
            topBar = {
                HomeTopBar(title = "Taylor & Travis’s Wedding", dateString = "2026-11-21", alpha = topBarAlpha)
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 12.dp, 12.dp, 0.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Spacer(modifier = Modifier.height(HEADER_HEIGHT - 210.dp))

                    BudgetTrackerCard(
                        insight = "See your budget",
                        heading = "Budget Tracker",
                        illustration = painterResource(R.drawable.ill_budget_tracker_card),
                        progress = 0.45f,
                        amountText = "₹46L",
                        labelText = "left",
                        onClick = onBudgetClick
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HomeCard(
                            insight = "Delicious and Elegant",
                            heading = "Catering Menu",
                            illustration = painterResource(R.drawable.ill_catering_menu_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFC4D4C2),
                            waveColor = Color(0x1A14570C).copy(alpha = 0.9f),
                            insightColor = Color(0xFF47671A),
                            onClick = {}
                        )
                        HomeCard(
                            insight = "Perfect Event Spaces",
                            heading = "Venue",
                            illustration = painterResource(R.drawable.ill_venue_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFD3CDE8),
                            waveColor = Color(0x1A2C186C).copy(alpha = 0.9f),
                            insightColor = Color(0xFF6448D6),
                            onClick = {}
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HomeCard(
                            insight = "Capture and Smile",
                            heading = "Moments",
                            illustration = painterResource(R.drawable.ill_moments_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFC3D4E8),
                            waveColor = Color(0x1A014594).copy(alpha = 0.9f),
                            insightColor = Color(0xFF3D58B4),
                            onClick = {}
                        )

                        HomeCard(
                            insight = "Invite and Celebrate",
                            heading = "Cards & Guests",
                            illustration = painterResource(R.drawable.ill_cards_and_guests_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFE8D0CE),
                            waveColor = Color(0x1A5D0501).copy(alpha = 0.9f),
                            insightColor = Color(0xFF5D1D1B),
                            onClick = {}
                        )
                    }

                    OrDivider(dividerGap = 12.dp, text = "EXPLORE")
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    VendorsCarousel(
                        title = "Trending Venues in Patna",
                        vendors = MockData.sampleVenues1,
                        onVendorClick = { },
                        onFavoriteToggle = { },
                        onOfferClick = { }
                    )

                    Spacer(Modifier.height(12.dp))

                    VendorsCarousel(
                        title = "Trending Venues in Patna",
                        vendors = MockData.sampleVenues2,
                        onVendorClick = { },
                        onFavoriteToggle = { },
                        onOfferClick = { }
                    )
                }
                FooterJansify()
            }
        }
    }
}