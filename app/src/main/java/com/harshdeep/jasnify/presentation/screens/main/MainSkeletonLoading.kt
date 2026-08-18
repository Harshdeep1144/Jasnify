package com.harshdeep.jasnify.presentation.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.harshdeep.jasnify.presentation.components.states.shimmerBrush
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.ChecklistViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.GuestViewModel
import com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

private val SkeletonSquircleShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)

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

    // Trigger data loading in all major ViewModels while showing the skeleton
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

@Composable
fun MainSkeletonContent(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush()
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Bar Skeleton
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(30.dp)
                            .clip(SkeletonSquircleShape)
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(brush)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main Content Skeleton (Mirroring Home Tab)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(SkeletonSquircleShape)
                        .background(brush)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(172.dp)
                            .clip(SkeletonSquircleShape)
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(172.dp)
                            .clip(SkeletonSquircleShape)
                            .background(brush)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(172.dp)
                            .clip(SkeletonSquircleShape)
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(172.dp)
                            .clip(SkeletonSquircleShape)
                            .background(brush)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(all = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
            }
        }
    }
}

@Preview(
    name = "Main Skeleton Content Preview",
    showBackground = true
)
@Composable
private fun MainSkeletonContentPreview() {
    MainSkeletonContent()
}