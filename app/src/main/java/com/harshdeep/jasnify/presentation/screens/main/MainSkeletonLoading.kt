package com.harshdeep.jasnify.presentation.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.harshdeep.jasnify.presentation.components.states.shimmerBrush
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.ChecklistViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.GuestViewModel
import com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmall
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfacePrimary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

private val CardSquircleShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
private val BigCardSquircleShape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault)

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun MainSkeletonLoading(
    navController: NavController
) {
    val graphEntry = remember(navController) { navController.getBackStackEntry(Screen.MainAppGraph.route) }

    val eventViewModel: EventViewModel = hiltViewModel(graphEntry)
    val budgetViewModel: BudgetViewModel = hiltViewModel(graphEntry)
    val checklistViewModel: ChecklistViewModel = hiltViewModel(graphEntry)
    val vendorViewModel: VendorViewModel = hiltViewModel(graphEntry)
    val venueViewModel: VenueViewModel = hiltViewModel(graphEntry)
    val guestViewModel: GuestViewModel = hiltViewModel(graphEntry)
    val profileViewModel: ProfileViewModel = hiltViewModel(graphEntry)
    val roomViewModel: RoomViewModel = hiltViewModel(graphEntry)

    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()

    LaunchedEffect(activeEventId) {
        activeEventId?.let { id ->
            budgetViewModel.setEventId(id)
            checklistViewModel.setEventId(id)
            vendorViewModel.setEventId(id)
            venueViewModel.setEventId(id)
            guestViewModel.setEventId(id)
        }
        profileViewModel.fetchProfile()
    }

    LaunchedEffect(Unit) {
        delay(1400.milliseconds)
        navController.navigate(Screen.MainAppScreen.route) {
            popUpTo(Screen.MainSkeletonLoading.route) { inclusive = true }
        }
    }

    MainSkeletonContent()
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MainSkeletonContent(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush()
) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Matched with exact dimensions from HomeTab
    val headerHeight = screenHeight * 0.42f
    val visibleBackgroundOffset = screenHeight * 0.34f

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Hero Top Image Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .background(brush)
            )

            // Top Header Info Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .zIndex(2f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(CornerSmall))
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(CornerSmall))
                            .background(Color.White.copy(alpha = 0.35f))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                )
            }

            // Scrollable Content Sheet
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(visibleBackgroundOffset))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = BackgroundPrimary,
                            shape = RoundedCornerShape(topStart = CornerExtraLarge, topEnd = CornerExtraLarge)
                        )
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Budget Tracker Card Skeleton
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(172.dp)
                            .clip(BigCardSquircleShape)
                            .background(ContentTertiary.copy(alpha = 0.08f))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(CornerSmall))
                                        .background(brush)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(CornerSmall))
                                        .background(brush)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(brush)
                                )
                            }
                        }
                    }

                    // Row 1: Catering Menu & Venue
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SmallCardSkeleton(Modifier.weight(1f), brush)
                        SmallCardSkeleton(Modifier.weight(1f), brush)
                    }

                    // Row 2: Moments & Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SmallCardSkeleton(Modifier.weight(1f), brush)
                        SmallCardSkeleton(Modifier.weight(1f), brush)
                    }

                    Spacer(modifier = Modifier.height(90.dp))
                }
            }

            // Bottom Navigation Bar Floating Skeleton
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(brush = BottomGradientBrush)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .pill360Shadow(
                            ambientColor = Color.Black.copy(alpha = 0.08f),
                            ambientBlur = 12.dp,
                            ambientSpread = 2.dp,
                            spotColor = Color.Black.copy(alpha = 0.12f),
                            spotBlur = 18.dp,
                            spotOffsetY = 4.dp
                        ),
                    color = SurfacePrimary,
                    shape = CircleShape
                ) {

                }
            }
        }
    }
}

@Composable
private fun SmallCardSkeleton(
    modifier: Modifier = Modifier,
    brush: Brush
) {
    Box(
        modifier = modifier
            .height(180.dp)
            .clip(CardSquircleShape)
            .background(ContentTertiary.copy(alpha = 0.08f))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(CornerSmall))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(11.dp)
                        .clip(RoundedCornerShape(CornerSmall))
                        .background(brush)
                )
            }
        }
    }
}

@Preview(
    name = "Main Skeleton Preview",
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun MainSkeletonContentPreview() {
    MainSkeletonContent()
}