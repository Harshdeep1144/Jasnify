package com.harshdeep.jasnify.presentation.screens.main.tabs.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.harshdeep.jasnify.presentation.screens.main.MainSkeletonContent
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.CardViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.HomeViewModel
import com.harshdeep.jasnify.presentation.viewmodels.MomentsViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@SuppressLint("FrequentlyChangingValue")
@Composable
fun HomeTab(
    mainNavController: NavHostController,
    internalNavController: NavHostController,
    onMenuClick: () -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    eventViewModel: EventViewModel = hiltViewModel(),
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    venueViewModel: VenueViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    momentsViewModel: MomentsViewModel = hiltViewModel(),
    cardViewModel: CardViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val homeConfig by homeViewModel.homeScreenConfig.collectAsStateWithLifecycle()
    val isVenuesLoading by venueViewModel.isLoading.collectAsStateWithLifecycle()
    val allVenues by venueViewModel.allVenues.collectAsStateWithLifecycle()
    val savedVenuesFromCloud by venueViewModel.savedVenues.collectAsStateWithLifecycle()
    val savedVendorsFromCloud by vendorViewModel.savedVendors.collectAsStateWithLifecycle()

    val venueSavedDestinations = remember(savedVenuesFromCloud) {
        savedVenuesFromCloud.associateBy({ it.venueName }, { it.destination })
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        (context as? com.harshdeep.jasnify.MainActivity)?.triggerNotificationPermissionCheck()
    }

    val vendorSavedDestinations = remember(savedVendorsFromCloud) {
        savedVendorsFromCloud.associate { "${it.vendorName}-${it.category}" to it.destination }
    }

    LaunchedEffect(Unit) {
        eventViewModel.fetchUserEvents()
    }

    LaunchedEffect(activeEvent?.id) {
        activeEvent?.id?.let { id ->
            budgetViewModel.setEventId(id)
            venueViewModel.setEventId(id)
            vendorViewModel.setEventId(id)
        }
    }

    val budgetEntity by budgetViewModel.budgetSettings.collectAsStateWithLifecycle()
    val expensesEntities by budgetViewModel.expenses.collectAsStateWithLifecycle()

    val totalSpent = remember(expensesEntities) {
        expensesEntities.sumOf { it.amount }
    }

    val totalBudget = remember(budgetEntity, activeEvent) {
        budgetEntity?.totalBudget ?: activeEvent?.budget ?: 0.0
    }

    val remainingFunds = remember(totalBudget, totalSpent) {
        (totalBudget - totalSpent).coerceAtLeast(0.0)
    }

    val remainingPercentage = remember(totalBudget, remainingFunds) {
        if (totalBudget > 0) (remainingFunds / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    }

    val formatBudgetShorthand: (Double) -> String = remember {
        { amount ->
            when {
                amount >= 10_000_000 -> "${(amount / 10_000_000).toLong()}Cr"
                amount >= 100_000 -> "${(amount / 100_000).toLong()}L"
                amount >= 1000 -> "${(amount / 1000).toLong()}k"
                else -> amount.toLong().toString()
            }
        }
    }

    val amountText = remember(remainingFunds) { "₹${formatBudgetShorthand(remainingFunds)}" }

    if (activeEvent == null) {
        MainSkeletonContent()
        return
    }

    val eventDateString = remember(activeEvent) {
        val now = System.currentTimeMillis()
        val effectiveDate = if (activeEvent?.multiDay == true) {
            val dates = activeEvent?.subEvents?.mapNotNull { it.date } ?: emptyList()
            if (dates.isEmpty()) null
            else {
                val upcoming = dates.filter { it >= now }.minOrNull()
                upcoming ?: dates.maxOrNull()
            }
        } else {
            activeEvent?.date
        }

        effectiveDate?.let {
            try {
                Instant.ofEpochMilli(it)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            } catch (_: Exception) {
                ""
            }
        } ?: ""
    }

    HomeTabContent(
        eventName = activeEvent?.name ?: "",
        eventDateString = eventDateString,
        remainingPercentage = remainingPercentage,
        amountText = amountText,
        mainNavController = mainNavController,
        internalNavController = internalNavController,
        onMenuClick = onMenuClick,
        onBottomBarVisibilityChange = onBottomBarVisibilityChange,
        eventViewModel = eventViewModel,
        venueViewModel = venueViewModel,
        vendorViewModel = vendorViewModel,
        roomViewModel = roomViewModel,
        momentsViewModel = momentsViewModel,
        cardViewModel = cardViewModel,
        isVenuesLoading = isVenuesLoading,
        venueSavedDestinations = venueSavedDestinations,
        vendorSavedDestinations = vendorSavedDestinations,
        allVenues = allVenues,
        activeEvent = activeEvent,
        homeConfig = homeConfig
    )
}
