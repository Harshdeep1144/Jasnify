package com.harshdeep.jasnify.presentation.screens.budget

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CategorySummaryData
import com.harshdeep.jasnify.domain.model.ExpenseItem
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddCustomCategoryBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddExpenseBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.EditBudgetBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.BudgetSummaryCard
import com.harshdeep.jasnify.presentation.components.cards.CategoryCard
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
import com.harshdeep.jasnify.presentation.components.others.CustomPieChart
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.PieChartSlice
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

enum class BudgetScreenView {
    BUDGET_TRACKER,
    EXPENSE_SUMMARY,
    EXPENSE_CATEGORY,
    CATEGORY_DETAIL,
    MANAGE_ROOM_ACCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onBackClick: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var currentView by remember { mutableStateOf(BudgetScreenView.BUDGET_TRACKER) }

    val expensesEntities by viewModel.expenses.collectAsStateWithLifecycle()
    val budgetEntity by viewModel.budgetSettings.collectAsStateWithLifecycle()
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    
    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""

    val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
    val currentUserName = currentUserInRoom?.name ?: auth.currentUser?.displayName ?: "Anonymous"

    val isOwner = activeEvent?.ownerId == currentUserUid
    val currentUserRole = when {
        isOwner -> UserRole.OWNER
        currentUserInRoom != null -> currentUserInRoom.role
        else -> UserRole.VIEWER
    }
    val isViewer = currentUserRole == UserRole.VIEWER

    // Fetch user events to ensure we have an active event
    LaunchedEffect(Unit) {
        eventViewModel.fetchUserEvents()
    }

    // Sync eventId to BudgetViewModel and RoomViewModel using the most direct source (the ID)
    LaunchedEffect(activeEventId) {
        activeEventId?.let { id ->
            Log.d("BudgetScreen", "Syncing with Event ID: $id")
            viewModel.setEventId(id)
            roomViewModel.verifyAccess(id, "Budget", currentUserUid)
            roomViewModel.loadRoomUsers(id, "Budget")
        }
    }

    val indianLocale = Locale("en", "IN")
    val formatter = NumberFormat.getNumberInstance(indianLocale)
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy, hh:mma", Locale.ENGLISH) }

    val allExpenses = remember(expensesEntities) {
        expensesEntities.map { entity ->
            ExpenseItem(
                id = entity.id,
                title = entity.title,
                category = entity.category,
                amount = "₹${formatter.format(entity.amount)}",
                emoji = entity.emoji,
                lastUpdatedBy = entity.lastUpdatedBy,
                lastUpdatedDate = dateFormatter.format(Date(entity.lastUpdatedDate)),
                phoneNumber = entity.phoneNumber,
                note = entity.note
            )
        }
    }

    // --- Toast State Management ---
    var toastData by remember { mutableStateOf(ToastData()) }
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    // SYSTEM BACK BUTTON HANDLER
    BackHandler(enabled = currentView != BudgetScreenView.BUDGET_TRACKER) {
        currentView = when (currentView) {
            BudgetScreenView.EXPENSE_SUMMARY -> BudgetScreenView.BUDGET_TRACKER
            BudgetScreenView.EXPENSE_CATEGORY -> BudgetScreenView.BUDGET_TRACKER
            BudgetScreenView.CATEGORY_DETAIL -> BudgetScreenView.EXPENSE_CATEGORY
            BudgetScreenView.MANAGE_ROOM_ACCESS -> BudgetScreenView.BUDGET_TRACKER
            BudgetScreenView.BUDGET_TRACKER -> BudgetScreenView.BUDGET_TRACKER
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var categorySearchQuery by remember { mutableStateOf("") }
    var expandedCardId by remember { mutableStateOf<String?>("0") }
    var isSearchBarFocused by remember { mutableStateOf(false) }

    BackHandler(enabled = isSearchBarFocused) {
        isSearchBarFocused = false
        focusManager.clearFocus()
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }

    var showAddExpenseSheet by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<ExpenseItem?>(null) }
    val addExpenseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var expenseToDelete by remember { mutableStateOf<ExpenseItem?>(null) }

    // Dynamic Edit Budget Sheet state integrations
    var showEditBudgetSheet by remember { mutableStateOf(false) }
    val budgetValue = remember(budgetEntity, activeEvent) {
        val rawValue = budgetEntity?.totalBudget ?: activeEvent?.budget
        if (rawValue == null) "INR" else {
            val plainString = BigDecimal.valueOf(rawValue).toPlainString()
            // Remove trailing .0 if it's an integer value for cleaner input
            val cleanString = if (plainString.endsWith(".0")) plainString.substringBefore(".0") else plainString
            "INR$cleanString"
        }
    }
    val editBudgetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showMenuBottomSheet by remember { mutableStateOf(false) }

    var showCategoryMenuBottomSheet by remember { mutableStateOf(false) }
    var selectedCategoryForMenu by remember { mutableStateOf<String?>(null) }

    var selectedCategoryForDetails by remember { mutableStateOf<String?>(null) }
    var selectedCategoryChips by remember { mutableStateOf(setOf("Recent First")) }

    var categoryToDeleteConfirm by remember { mutableStateOf<String?>(null) }

    var showAddCustomCategorySheet by remember { mutableStateOf(false) }
    var categoryToRename by remember { mutableStateOf<String?>(null) }
    val addCustomCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }

    val sortOptions = remember { listOf("Newest First", "Oldest First", "Highest Amount", "Lowest Amount") }
    val filterOptions = remember { listOf("Vendors", "Catering", "Beauty", "Stationery", "Apparel", "Beverages", "Transport", "Equipment Rentals") }

    var selectedSortOption by remember { mutableStateOf("Newest First") }
    var selectedFilterOptions by remember { mutableStateOf(emptySet<String>()) }

    var defaultCategories by remember {
        mutableStateOf(
            listOf(
                "Venue", "Catering", "Gifts", "Staff & Crew",
                "Costumes", "Vendors", "Transportation",
                "Entertainment", "Equipment Rentals", "Unplanned Costs"
            )
        )
    }

    val parseAmount = { amountStr: String ->
        amountStr.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0
    }

    val isBudgetNotSet = remember(budgetEntity, activeEvent) {
        budgetEntity?.totalBudget == null && activeEvent?.budget == null
    }

    val totalBudget = remember(budgetValue) {
        val numericPart = budgetValue.dropWhile { !it.isDigit() }
        numericPart.toDoubleOrNull() ?: 0.0
    }

    val totalSpent = remember(allExpenses) {
        allExpenses.sumOf { parseAmount(it.amount) }
    }

    val remainingFunds = (totalBudget - totalSpent).coerceAtLeast(0.0)
    val remainingPercentage = if (totalBudget > 0) (remainingFunds / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    val spentPercentage = if (totalBudget > 0) (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f) else 0f

    val formattedRemaining = "₹${formatter.format(remainingFunds.toLong())}"
    val formattedTotalSpent = "₹${formatter.format(totalSpent.toLong())}"
    val formattedTotalBudget = formatter.format(totalBudget.toLong())

    val centerTextPrimaryValue = remember(totalSpent) {
        when {
            totalSpent >= 10000000.0 -> {
                val spentInCrores = totalSpent / 10000000.0
                "₹ ${String.format(Locale.ENGLISH, "%.1f", spentInCrores)} Cr"
            }
            totalSpent >= 100000.0 -> {
                val spentInLakhs = totalSpent / 100000.0
                "₹ ${String.format(Locale.ENGLISH, "%.1f", spentInLakhs)} L"
            }
            totalSpent >= 1000.0 -> {
                val spentInThousands = totalSpent / 1000.0
                "₹ ${String.format(Locale.ENGLISH, "%.1f", spentInThousands)} K"
            }
            else -> {
                "₹ ${formatter.format(totalSpent.toLong())}"
            }
        }
    }

    val filteredExpenses = allExpenses.filter { item ->
        val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedFilterOptions.isEmpty() || selectedFilterOptions.contains(item.category)

        matchesSearch && matchesCategory
    }.let { list ->
        when (selectedSortOption) {
            "Highest Amount" -> list.sortedByDescending { parseAmount(it.amount) }
            "Lowest Amount" -> list.sortedBy { parseAmount(it.amount) }
            "Oldest First" -> list.sortedBy { it.id.toIntOrNull() ?: 0 }
            else -> list.sortedByDescending { it.id.toIntOrNull() ?: 0 }
        }
    }

    val computedCategories = remember(allExpenses, defaultCategories) {
        val grouped = allExpenses.groupBy { it.category }
        val finalCategories = (grouped.keys + defaultCategories).distinct()

        finalCategories.map { catName ->
            val items = grouped[catName] ?: emptyList()
            val totalAmt = items.sumOf { parseAmount(it.amount) }
            val emojis = items.map { it.emoji }
            CategorySummaryData(
                name = catName,
                amountFormatted = "₹${formatter.format(totalAmt.toLong())}",
                amountRaw = totalAmt,
                emojis = emojis,
                totalCount = items.size
            )
        }.sortedByDescending { it.amountRaw }
    }

    val filteredCategorySummary = computedCategories.filter {
        it.name.contains(categorySearchQuery, ignoreCase = true)
    }

    val colorPalette = remember {
        listOf(
            Color(0xFF1D5590),
            Color(0xFFFF1E56),
            Color(0xFFE56B8F),
            Color(0xFF0FAD48),
            Color(0xFF2FA4C4),
            Color(0xFF8D16FF),
            Color(0xFFFFB020),
            Color(0xFF00C9A7),
            Color(0xFF6C5B7B),
            Color(0xFF355C7D),
            Color(0xFFF67280),
            Color(0xFFC06C84),
            Color(0xFFFF8C94),
            Color(0xFF45B6FE),
            Color(0xFF50B498),
            Color(0xFF9B59B6),
            Color(0xFFE67E22),
            Color(0xFF16A085)
        )
    }

    val uniqueCategories = remember(allExpenses, defaultCategories) {
        (allExpenses.map { it.category } + defaultCategories).distinct()
    }

    val categoryColors = remember(uniqueCategories, colorPalette) {
        uniqueCategories.mapIndexed { index, category ->
            category to colorPalette[index % colorPalette.size]
        }.toMap()
    }

    val getCategoryColor = remember(categoryColors) {
        { categoryName: String ->
            categoryColors[categoryName] ?: ContentSecondary
        }
    }

    val pieSlices = remember(allExpenses, getCategoryColor) {
        val grouped = allExpenses.groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { parseAmount(it.amount) } }
            .toList()
            .sortedByDescending { it.second }

        grouped.map { pair ->
            PieChartSlice(
                value = pair.second.toFloat(),
                color = getCategoryColor(pair.first),
                label = pair.first
            )
        }
    }

    RoomAccessGuardian(
        hasAccess = hasAccess,
        roomName = "Budget",
        onBackClick = onBackClick
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            },
        floatingActionButton = {
            if (currentView == BudgetScreenView.BUDGET_TRACKER && !isViewer) {
                CustomIconButton(
                    onClick = {
                        expenseToEdit = null
                        showAddExpenseSheet = true
                    },
                    icon = painterResource(R.drawable.ic_plus),
                    size = ButtonSize.Large,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 24.dp)
                        .shadow(16.dp, CircleShape)
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            AnimatedContent(
                targetState = currentView,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "BudgetScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {


// ============================================================================================================================================
// SCREEN 1: BUDGET_TRACKER (Core Tracker Dashboard)
// ============================================================================================================================================


                    BudgetScreenView.BUDGET_TRACKER -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SurfaceSecondary)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfacePrimary)
                                    .statusBarsPadding()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        focusManager.clearFocus()
                                    }
                            ) {
                                CustomTopBar(
                                    title = "Budget Tracker",
                                    onBackClick = { onBackClick() },
                                    onMenuClick = { showMenuBottomSheet = true },
                                    isLargeTitle = true
                                )
                            }

                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        focusManager.clearFocus()
                                    },
                            ) {
                                // Budget Summary Card Section
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(SurfacePrimary, SurfaceSecondary)
                                                )
                                            )
                                            .padding(12.dp)
                                    ) {
                                        BudgetSummaryCard(
                                            isBudgetNotSet = isBudgetNotSet,
                                            formattedTotalBudget = formattedTotalBudget,
                                            formattedRemaining = formattedRemaining,
                                            remainingPercentage = remainingPercentage,
                                            onEditBudgetClick = { showEditBudgetSheet = true },
                                            onViewSummaryClick = { currentView = BudgetScreenView.EXPENSE_SUMMARY },
                                            showEditButton = isOwner
                                        )
                                    }
                                }

                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "All Expenses",
                                            style = JasnifyTheme.typography.headingXLarge.copy(fontWeight = FontWeight.Medium),
                                            color = ContentPrimary
                                        )

                                        if (!isBudgetNotSet && allExpenses.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                CustomSearchBar(
                                                    value = searchQuery,
                                                    placeholder = "Search with AI",
                                                    onValueChange = { searchQuery = it },
                                                    isAiSearch = true,
                                                    modifier = Modifier
                                                        .weight(1.0f)
                                                        .onFocusChanged { focusState ->
                                                            if (focusState.isFocused && !isSearchBarFocused) {
                                                                coroutineScope.launch {
                                                                    delay(150)
                                                                    listState.animateScrollToItem(
                                                                        index = 1,
                                                                        scrollOffset = -8
                                                                    )
                                                                }
                                                            }
                                                            isSearchBarFocused = focusState.isFocused
                                                        },
                                                    backgroundColor = SurfacePrimary
                                                )

                                                FilterButton(
                                                    onClick = { showBottomSheet = true },
                                                    backgroundColor = SurfacePrimary
                                                )
                                            }
                                        }
                                    }
                                }

                                if (allExpenses.isEmpty()) {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 80.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_receipt),
                                                contentDescription = "No expenses",
                                                tint = ContentTertiary,
                                                modifier = Modifier.size(84.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "Your expenses will \n appear here",
                                                style = JasnifyTheme.typography.displayMedium.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = ContentTertiary,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 32.dp)
                                            )
                                        }
                                    }
                                } else {
                                    items(filteredExpenses, key = { it.id }) { item ->
                                        val isFirst = filteredExpenses.firstOrNull()?.id == item.id
                                        val isLast = filteredExpenses.lastOrNull()?.id == item.id

                                        val itemShape = when {
                                            isFirst && isLast -> SquircleShape(CornerLarge, CornerSmoothingDefault)
                                            isFirst -> SquircleShape(CornerLarge, CornerLarge, CornerExtraSmall, CornerExtraSmall, CornerSmoothingDefault)
                                            isLast -> SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLarge, CornerLarge, CornerSmoothingDefault)
                                            else -> SquircleShape(CornerExtraSmall, CornerSmoothingDefault)
                                        }

                                        ExpenseCard(
                                            title = item.title,
                                            category = item.category,
                                            amount = item.amount,
                                            emoji = item.emoji,
                                            lastUpdatedBy = item.lastUpdatedBy,
                                            lastUpdatedDate = item.lastUpdatedDate,
                                            showActions = (expandedCardId == item.id),
                                            cardShape = itemShape,
                                            isEditable = !isViewer,
                                            modifier = Modifier
                                                .padding(horizontal = 12.dp, vertical = 1.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    focusManager.clearFocus()
                                                    expandedCardId = if (expandedCardId == item.id) null else item.id
                                                },
                                            onDeleteClick = {
                                                expenseToDelete = item
                                            },
                                            onModifyClick = {
                                                expenseToEdit = item
                                                showAddExpenseSheet = true
                                            }
                                        )
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(124.dp))
                                }
                            }
                        }
                    }



// ============================================================================================================================================
// SCREEN 2: EXPENSE_SUMMARY (Visual Breakdown & Pie Slices)
// ============================================================================================================================================


                    BudgetScreenView.EXPENSE_SUMMARY -> {
                        val scrollState = rememberScrollState()
                        var isCategoryListExpanded by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SurfacePrimary)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfacePrimary)
                                    .statusBarsPadding()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        focusManager.clearFocus()
                                    }
                            ) {
                                CustomTopBar(
                                    title = "Expense Summary",
                                    onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        focusManager.clearFocus()
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                        .padding(horizontal = 12.dp)
                                        .padding(top = 12.dp, bottom = 100.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CustomPieChart(
                                            slices = pieSlices,
                                            centerTextPrimary = centerTextPrimaryValue,
                                            centerTextSecondary = "TOTAL EXPENSES",
                                        )
                                    }

                                    val remainingPercentageText = String.format(Locale.ENGLISH, "%.0f", remainingPercentage * 100)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(
                                                elevation = 12.dp,
                                                shape = RoundedCornerShape(20.dp),
                                            )
                                            .clip(SquircleShape(CornerLargeIncrease))
                                            .background(SurfacePrimary)
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row {
                                                Text(
                                                    text = "Remaining Funds",
                                                    style = JasnifyTheme.typography.labelXLarge,
                                                    color = ContentPrimary
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    text = "($remainingPercentageText%)",
                                                    style = JasnifyTheme.typography.labelXLarge,
                                                    color = ContentPrimary
                                                )
                                            }
                                            Text(
                                                text = formattedRemaining,
                                                style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                                                color = Color(0xFF137935)
                                            )
                                        }
                                    }

                                    val spentPercentageText = String.format(Locale.ENGLISH, "%.0f", spentPercentage * 100)
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(
                                                elevation = 16.dp,
                                                shape = RoundedCornerShape(24.dp),
                                                spotColor = ContentPrimary.copy(alpha = 0.35f),
                                                ambientColor = ContentPrimary.copy(alpha = 0.15f)
                                            )
                                            .clip(SquircleShape(CornerLargeIncrease))
                                            .background(SurfacePrimary)
                                            .padding(16.dp),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row {
                                                Text(
                                                    text = "Total Spent",
                                                    style = JasnifyTheme.typography.labelXLarge,
                                                    color = ContentPrimary
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    text = "($spentPercentageText%)",
                                                    style = JasnifyTheme.typography.labelXLarge,
                                                    color = ContentPrimary
                                                )
                                            }
                                            Text(
                                                text = formattedTotalSpent,
                                                style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                                                color = Color(0xFFBF3C34)
                                            )
                                        }
                                        Spacer(Modifier.height(16.dp))

                                        DashedDivider(
                                            color = MaterialTheme.colorScheme.outline.copy(0.16f),
                                            dashLength = 12f,
                                            gapLength = 6f
                                        )

                                        val processedCategories = remember(allExpenses) {
                                            allExpenses.groupBy { it.category }
                                                .mapValues { (_, items) -> items.sumOf { parseAmount(it.amount) } }
                                                .toList()
                                                .sortedByDescending { it.second }
                                        }

                                        val displayedCategories = if (isCategoryListExpanded) {
                                            processedCategories
                                        } else {
                                            processedCategories.take(4)
                                        }

                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            displayedCategories.forEach { (categoryName, totalCategorySpent) ->
                                                val categoryPct = if (totalBudget > 0) (totalCategorySpent / totalBudget) * 100 else 0.0
                                                val formattedCategoryPctText = String.format(Locale.ENGLISH, "%.2f", categoryPct)
                                                val formattedCategorySpentText = "₹${formatter.format(totalCategorySpent.toLong())}"
                                                val indicatorColor = getCategoryColor(categoryName)

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .width(6.dp)
                                                                .height(24.dp)
                                                                .clip(SquircleShape(100))
                                                                .background(indicatorColor)
                                                        )

                                                        Spacer(modifier = Modifier.width(12.dp))

                                                        Text(
                                                            text = "$categoryName ($formattedCategoryPctText%)",
                                                            style = JasnifyTheme.typography.labelLarge,
                                                            color = ContentPrimary
                                                        )
                                                    }

                                                    Text(
                                                        text = formattedCategorySpentText,
                                                        style = JasnifyTheme.typography.labelLarge,
                                                        color = ContentPrimary
                                                    )
                                                }
                                            }
                                        }

                                        if (processedCategories.size > 4) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(40.dp)
                                                    .clickable { isCategoryListExpanded = !isCategoryListExpanded }
                                                    .padding(vertical = 11.dp, horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = if (isCategoryListExpanded) "View less" else "View all",
                                                    style = JasnifyTheme.typography.bodyLarge,
                                                    color = ContentBrandDark
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(
                                                    imageVector = if (isCategoryListExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                    contentDescription = null,
                                                    tint = ContentBrandDark,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 16.dp,
                                            spotColor = ContentPrimary.copy(alpha = 0.1f),
                                            ambientColor = ContentPrimary.copy(alpha = 0.05f)
                                        )
                                        .background(SurfacePrimary)
                                        .padding(horizontal = 12.dp, vertical = 16.dp)
                                        .navigationBarsPadding(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CustomTextButton(
                                        onClick = { },
                                        text = "AI Overview",
                                        shapeStyle = ButtonShapeStyle.Square,
                                        type = ButtonType.Secondary,
                                        modifier = Modifier.weight(1f),
                                        leadingIcon = painterResource(R.drawable.ic_ai)
                                    )

                                    if (!isViewer) {
                                        CustomTextButton(
                                            onClick = {
                                                expenseToEdit = null
                                                showAddExpenseSheet = true
                                            },
                                            text = "Add Expense",
                                            shapeStyle = ButtonShapeStyle.Square,
                                            type = ButtonType.Primary,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }



// ============================================================================================================================================
// SCREEN 3: EXPENSE_CATEGORY (List of Expense Categories)
// ============================================================================================================================================


                    BudgetScreenView.EXPENSE_CATEGORY -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SurfaceSecondary)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceSecondary)
                                    .statusBarsPadding()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        focusManager.clearFocus()
                                    }
                            ) {
                                CustomTopBar(
                                    title = "Expense Category",
                                    onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                    buttonStyle = ButtonBackground.TRANSLUCENT,
                                    translucentAlpha = 0.5f
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        focusManager.clearFocus()
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    CustomSearchBar(
                                        value = categorySearchQuery,
                                        placeholder = "Search",
                                        onValueChange = { categorySearchQuery = it },
                                        backgroundColor = SurfacePrimary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    )

                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        items(filteredCategorySummary, key = { it.name }) { categoryItem ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        selectedCategoryForDetails = categoryItem.name
                                                        currentView = BudgetScreenView.CATEGORY_DETAIL
                                                    }
                                            ) {
                                                CategoryCard(
                                                    title = categoryItem.name,
                                                    amount = categoryItem.amountFormatted,
                                                    emojis = categoryItem.emojis,
                                                    totalItemCount = categoryItem.totalCount,
                                                    onMenuClick = {
                                                        selectedCategoryForMenu = categoryItem.name
                                                        showCategoryMenuBottomSheet = true
                                                    },
                                                    showMenu = !isViewer
                                                )
                                            }
                                        }

                                        item { Spacer(modifier = Modifier.height(12.dp)) }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(elevation = 12.dp, spotColor = ContentPrimary, ambientColor = ContentPrimary)
                                            .background(SurfacePrimary)
                                            .padding(12.dp)
                                            .navigationBarsPadding(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CustomTextButton(
                                            onClick = { currentView = BudgetScreenView.EXPENSE_SUMMARY },
                                            text = "View Summary",
                                            type = ButtonType.Secondary,
                                            shapeStyle = ButtonShapeStyle.Square,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (!isViewer) {
                                            CustomTextButton(
                                                onClick = {
                                                    categoryToRename = null
                                                    showAddCustomCategorySheet = true
                                                },
                                                text = "Add Category",
                                                type = ButtonType.Primary,
                                                shapeStyle = ButtonShapeStyle.Square,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }



// ============================================================================================================================================
// SCREEN 4: CATEGORY_DETAIL (Detailed view of transactions per category)
// ============================================================================================================================================


                    BudgetScreenView.CATEGORY_DETAIL -> {
                        val selectedCategoryName = selectedCategoryForDetails ?: "Category"
                        val selectedCategoryExpenses = remember(allExpenses, selectedCategoryForDetails) {
                            allExpenses.filter { it.category == selectedCategoryName }
                        }
                        val selectedCategoryTotal = remember(selectedCategoryExpenses) {
                            selectedCategoryExpenses.sumOf { parseAmount(it.amount) }
                        }
                        val formattedCategoryTotal = "₹${formatter.format(selectedCategoryTotal.toLong())}"

                        val sortedCategoryExpenses = remember(selectedCategoryExpenses, selectedCategoryChips) {
                            selectedCategoryExpenses.sortedWith(
                                Comparator { a, b ->
                                    val amtA = parseAmount(a.amount)
                                    val amtB = parseAmount(b.amount)
                                    val idA = a.id.toIntOrNull() ?: 0
                                    val idB = b.id.toIntOrNull() ?: 0

                                    val hasMostExpensive = selectedCategoryChips.contains("Most Expensive")
                                    val hasLeastExpensive = selectedCategoryChips.contains("Least Expensive")

                                    var result = 0

                                    if (hasMostExpensive && !hasLeastExpensive) {
                                        result = amtB.compareTo(amtA)
                                    } else if (hasLeastExpensive && !hasMostExpensive) {
                                        result = amtA.compareTo(amtB)
                                    } else if (hasMostExpensive && hasLeastExpensive) {
                                        result = amtB.compareTo(amtA)
                                    }

                                    if (result == 0) {
                                        result = idB.compareTo(idA)
                                    }
                                    result
                                }
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SurfaceSecondary)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceSecondary)
                                    .statusBarsPadding()
                            ) {
                                CustomTopBar(
                                    onBackClick = { currentView = BudgetScreenView.EXPENSE_CATEGORY },
                                    buttonStyle = ButtonBackground.TRANSLUCENT,
                                    translucentAlpha = 0.5f
                                )
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                            ) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = selectedCategoryName,
                                                style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                                                color = ContentPrimary
                                            )
                                            if (!isViewer) {
                                                Box(
                                                    modifier = Modifier.padding(8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_edit),
                                                        contentDescription = "Rename Category",
                                                        tint = ContentPrimary,
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .clickable {
                                                                categoryToRename = selectedCategoryName
                                                                showAddCustomCategorySheet = true
                                                            }
                                                    )
                                                }
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Total Category Expenses",
                                                style = JasnifyTheme.typography.labelLarge,
                                                color = ContentSecondary
                                            )
                                            Text(
                                                text = formattedCategoryTotal,
                                                style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                                                color = ContentPrimary
                                            )
                                        }
                                        Row {
                                            val categoryProgressRatio = if (totalBudget > 0) (selectedCategoryTotal / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
                                            LinearProgressIndicator(
                                                progress = { categoryProgressRatio },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(4.dp)
                                                    .clip(CircleShape),
                                                color = ContentBrand,
                                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                            )
                                        }
                                    }
                                }

                                item {
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        item {
                                            val isRecentActive = selectedCategoryChips.contains("Recent First")
                                            FilterChip(
                                                label = "Recent First",
                                                isSelected = isRecentActive,
                                                shapeStyle = ChipShapeStyle.Round,
                                                size = ChipSize.Small,
                                                hasStroke = true,
                                                leadingIcon = ImageVector.vectorResource(R.drawable.ic_clock_forward),
                                                trailingIcon = if (isRecentActive) Icons.Default.Close else null,
                                                onClick = {
                                                    selectedCategoryChips = if (isRecentActive) {
                                                        selectedCategoryChips - "Recent First"
                                                    } else {
                                                        selectedCategoryChips + "Recent First"
                                                    }
                                                },
                                                onTrailingIconClick = {
                                                    selectedCategoryChips = selectedCategoryChips - "Recent First"
                                                }
                                            )
                                        }

                                        item {
                                            val isMostActive = selectedCategoryChips.contains("Most Expensive")
                                            FilterChip(
                                                label = "Most Expensive",
                                                isSelected = isMostActive,
                                                shapeStyle = ChipShapeStyle.Round,
                                                size = ChipSize.Small,
                                                hasStroke = true,
                                                leadingIcon = ImageVector.vectorResource(R.drawable.ic_line_chart_up),
                                                trailingIcon = if (isMostActive) Icons.Default.Close else null,
                                                onClick = {
                                                    selectedCategoryChips = if (isMostActive) {
                                                        selectedCategoryChips - "Most Expensive"
                                                    } else {
                                                        selectedCategoryChips + "Most Expensive"
                                                    }
                                                },
                                                onTrailingIconClick = {
                                                    selectedCategoryChips = selectedCategoryChips - "Most Expensive"
                                                }
                                            )
                                        }

                                        item {
                                            val isLeastActive = selectedCategoryChips.contains("Least Expensive")
                                            FilterChip(
                                                label = "Least Expensive",
                                                isSelected = isLeastActive,
                                                shapeStyle = ChipShapeStyle.Round,
                                                size = ChipSize.Small,
                                                hasStroke = true,
                                                leadingIcon = ImageVector.vectorResource(R.drawable.ic_line_chart_down),
                                                trailingIcon = if (isLeastActive) Icons.Default.Close else null,
                                                onClick = {
                                                    selectedCategoryChips = if (isLeastActive) {
                                                        selectedCategoryChips - "Least Expensive"
                                                    } else {
                                                        selectedCategoryChips + "Least Expensive"
                                                    }
                                                },
                                                onTrailingIconClick = {
                                                    selectedCategoryChips = selectedCategoryChips - "Least Expensive"
                                                }
                                            )
                                        }
                                    }
                                }

                                if (sortedCategoryExpenses.isEmpty()) {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 100.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_receipt),
                                                contentDescription = "No expenses",
                                                tint = ContentTertiary,
                                                modifier = Modifier.size(84.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "No expenses",
                                                style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                                                color = ContentTertiary
                                            )
                                        }
                                    }
                                } else {
                                    items(sortedCategoryExpenses, key = { it.id }) { item ->
                                        val isFirst = sortedCategoryExpenses.firstOrNull()?.id == item.id
                                        val isLast = sortedCategoryExpenses.lastOrNull()?.id == item.id

                                        val itemShape = when {
                                            isFirst && isLast -> SquircleShape(CornerLarge, CornerSmoothingDefault)
                                            isFirst -> SquircleShape(CornerLarge, CornerLarge, CornerExtraSmall, CornerExtraSmall, CornerSmoothingDefault)
                                            isLast -> SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLarge, CornerLarge, CornerSmoothingDefault)
                                            else -> SquircleShape(CornerExtraSmall, CornerSmoothingDefault)
                                        }

                                        ExpenseCard(
                                            title = item.title,
                                            category = item.category,
                                            amount = item.amount,
                                            emoji = item.emoji,
                                            lastUpdatedBy = item.lastUpdatedBy,
                                            lastUpdatedDate = item.lastUpdatedDate,
                                            showActions = (expandedCardId == item.id),
                                            cardShape = itemShape,
                                            isEditable = !isViewer,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp, 1.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    focusManager.clearFocus()
                                                    expandedCardId = if (expandedCardId == item.id) null else item.id
                                                },
                                            onDeleteClick = {
                                                expenseToDelete = item
                                            },
                                            onModifyClick = {
                                                expenseToEdit = item
                                                showAddExpenseSheet = true
                                            }
                                        )
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(100.dp))
                                }
                            }
                        }
                    }



// ============================================================================================================================================
// SCREEN 5: MANAGE_ROOM_ACCESS (Manage room users and access rights)
// ============================================================================================================================================



                    BudgetScreenView.MANAGE_ROOM_ACCESS -> {
                        val currentUserUid = auth.currentUser?.uid ?: ""
                        val isOwner = activeEvent?.ownerId == currentUserUid
                        val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
                        val currentUserRole = when {
                            isOwner -> UserRole.OWNER
                            currentUserInRoom != null -> currentUserInRoom.role
                            else -> UserRole.VIEWER
                        }

                        // Ensure current user is in the list shown, even if not yet in Firestore access collection
                        val displayUsers = if (currentUserInRoom == null && currentUserUid.isNotEmpty()) {
                            val self = User(
                                uid = currentUserUid,
                                name = auth.currentUser?.displayName ?: "Me",
                                email = auth.currentUser?.email ?: "",
                                role = currentUserRole,
                                username = auth.currentUser?.email?.substringBefore("@") ?: "me"
                            )
                            (listOf(self) + roomUsers).distinctBy { it.uid }
                        } else {
                            roomUsers
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SurfaceSecondary)
                        ) {
                            RoomScreen(
                                allUsers = displayUsers,
                                currentUserRole = currentUserRole,
                                isSelf = { it.uid == currentUserUid },
                                onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                onMenuClick = {
                                    focusManager.clearFocus()
                                    showRoomMenuBottomSheet = true
                                },
                                onRoleChange = { targetUser, newRole ->
                                    activeEvent?.id?.let { id ->
                                        roomViewModel.updateRole(id, "Budget", targetUser, newRole)
                                    }
                                },
                                onRemove = { targetUser ->
                                    userToRemove = targetUser
                                },
                                onReport = { targetUser ->
                                    toastData = ToastData("${targetUser.name} reported", ToastType.DEFAULT)
                                },
                                onLeave = {
                                    showLeaveConfirmation = true
                                },
                                searchResults = searchResults,
                                onSearch = { roomViewModel.searchUsers(it) },
                                onGrantAccess = { email, role ->
                                    activeEvent?.id?.let { id ->
                                        roomViewModel.grantAccess(id, "Budget", email, role)
                                        toastData = ToastData("Access granted to $email", ToastType.SUCCESS)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }


// ============================================================================================================================================
// HELPER: BOTTOM_SHEETS
// ============================================================================================================================================


            // --- Screen-level CustomToast Display (Shown ONLY when no Bottom Sheets are visible) ---
            val isAnySheetVisible = showBottomSheet || showAddExpenseSheet || showAddCustomCategorySheet ||
                    showEditBudgetSheet || showMenuBottomSheet || showCategoryMenuBottomSheet ||
                    showRoomMenuBottomSheet || expenseToDelete != null || categoryToDeleteConfirm != null || userToRemove != null

            AnimatedVisibility(
                visible = toastData.message != null && !isAnySheetVisible,
                enter = slideInVertically(initialOffsetY = { -it - 500 }),
                exit = slideOutVertically(targetOffsetY = { -it - 500 }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .zIndex(99f)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                CustomToast(
                    message = toastData.message ?: "",
                    type = toastData.type
                )
            }
        }
    }
    }

    if (showBottomSheet) {
        SortFilterBottomSheet(
            sheetState = filterSheetState,
            sortOptions = sortOptions,
            initialSortOption = selectedSortOption,
            filterByOptions = filterOptions,
            initialFilterOptions = selectedFilterOptions,
            onDismiss = { showBottomSheet = false },
            onApply = { sort, filters ->
                selectedSortOption = sort
                selectedFilterOptions = filters
                showBottomSheet = false
            }
        )
    }

    if (showAddExpenseSheet) {
        AddExpenseBottomSheet(
            sheetState = addExpenseSheetState,
            onDismiss = {
                showAddExpenseSheet = false
                expenseToEdit = null
            },
            onSave = { amount, receiver, category, emoji, phone, notes ->
                val editingItem = expenseToEdit
                if (editingItem != null) {
                    viewModel.updateExpense(
                        id = editingItem.id,
                        title = receiver.ifBlank { "Unnamed Receiver" },
                        category = category.ifBlank { "Misc" },
                        amount = amount.toDouble(),
                        emoji = emoji.ifBlank { "💸" },
                        userName = currentUserName,
                        phoneNumber = phone,
                        note = notes
                    )
                    toastData = ToastData("Expense Updated!", ToastType.SUCCESS)
                } else {
                    viewModel.addExpense(
                        title = receiver.ifBlank { "Unnamed Receiver" },
                        category = category.ifBlank { "Misc" },
                        amount = amount.toDouble(),
                        emoji = emoji.ifBlank { "💸" },
                        userName = currentUserName,
                        phoneNumber = phone,
                        note = notes
                    )
                    toastData = ToastData("Expense Added!", ToastType.SUCCESS)
                }
                showAddExpenseSheet = false
                expenseToEdit = null
            },
            categories = defaultCategories,
            onAddCategory = { newCategory ->
                if (!defaultCategories.contains(newCategory)) {
                    defaultCategories = defaultCategories + newCategory
                }
            },
            initialAmount = expenseToEdit?.amount?.replace("₹", "")?.replace(",", "") ?: "",
            initialReceiver = expenseToEdit?.title ?: "",
            initialCategory = expenseToEdit?.category ?: "",
            initialEmoji = expenseToEdit?.emoji ?: "",
            initialPhoneNumber = expenseToEdit?.phoneNumber ?: "",
            initialNote = expenseToEdit?.note ?: ""
        )
    }

    if (showAddCustomCategorySheet) {
        AddCustomCategoryBottomSheet(
            sheetState = addCustomCategorySheetState,
            onDismiss = {
                showAddCustomCategorySheet = false
                categoryToRename = null
            },
            onAddCategory = { inputName ->
                val originalName = categoryToRename
                if (originalName != null) {
                    if (originalName != inputName) {
                        if (defaultCategories.contains(originalName)) {
                            defaultCategories = defaultCategories.map { if (it == originalName) inputName else it }
                        } else if (!defaultCategories.contains(inputName)) {
                            defaultCategories = defaultCategories + inputName
                        }
                        viewModel.renameCategory(originalName, inputName)
                        if (selectedCategoryForDetails == originalName) {
                            selectedCategoryForDetails = inputName
                        }
                    }
                } else {
                    if (!defaultCategories.contains(inputName)) {
                        defaultCategories = defaultCategories + inputName
                    }
                }
                showAddCustomCategorySheet = false
                categoryToRename = null
            },
            initialCategoryName = categoryToRename ?: "",
            heading = if (categoryToRename != null) "Rename category" else "Add custom category"
        )
    }

    if (expenseToDelete != null) {
        ConfirmationBottomSheet(
            heading = "Are you sure?",
            subHeading = "The expense amount will be added back to the total budget.",
            confirmButtonText = "Delete Expense",
            onDismiss = {
                expenseToDelete = null
            },
            onConfirm = {
                val currentExpenseId = expenseToDelete?.id
                if (currentExpenseId != null) {
                    viewModel.deleteExpense(currentExpenseId)
                }
                expenseToDelete = null
            }
        )
    }

    if (showEditBudgetSheet) {
        EditBudgetBottomSheet(
            initialBudgetValue = budgetValue,
            sheetState = editBudgetSheetState,
            isBudgetNotSet = isBudgetNotSet,
            onDismiss = {
                showEditBudgetSheet = false
            },
            onUpdateBudget = { updatedValue ->
                val numericPart = updatedValue.dropWhile { !it.isDigit() }
                val totalBudget = numericPart.toDoubleOrNull() ?: 0.0
                viewModel.updateBudget(totalBudget)
                showEditBudgetSheet = false
            }
        )
    }

    if (showMenuBottomSheet) {
        val addBudgetIcon = painterResource(R.drawable.ic_plus)
        val editBudgetIcon = painterResource(R.drawable.ic_edit)
        val roomAccessIcon = painterResource(R.drawable.ic_user_default)
        val categoryIcon = painterResource(R.drawable.ic_category)

        MenuBottomSheet(
            items = listOfNotNull(
                if (isOwner) {
                    listOf(
                        MenuSheetActionItem(
                            text = if (isBudgetNotSet) "Add Budget" else "Edit Budget",
                            icon = if (isBudgetNotSet) addBudgetIcon else editBudgetIcon,
                            iconPlacement = IconPlacement.Left,
                            onClick = {
                                showMenuBottomSheet = false
                                showEditBudgetSheet = true
                            }
                        )
                    )
                } else null,
                listOf(
                    MenuSheetActionItem(
                        text = if(isOwner) "Manage Room Access" else "Room Members",
                        icon = roomAccessIcon,
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuBottomSheet = false
                            currentView = BudgetScreenView.MANAGE_ROOM_ACCESS
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Expense Categories",
                        icon = categoryIcon,
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuBottomSheet = false
                            currentView = BudgetScreenView.EXPENSE_CATEGORY
                        },
                    )
                )
            ),
            onCancelClick = {
                showMenuBottomSheet = false
            }
        )
    }

    if (showCategoryMenuBottomSheet) {
        val pieChartIcon = painterResource(R.drawable.ic_pie_chart)
        val editIcon = painterResource(R.drawable.ic_edit)
        val deleteIcon = painterResource(R.drawable.ic_delete)

        MenuBottomSheet(
            items = listOfNotNull(
                listOf(
                    MenuSheetActionItem(
                        text = "View Expenses",
                        icon = pieChartIcon,
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showCategoryMenuBottomSheet = false
                            selectedCategoryForDetails = selectedCategoryForMenu
                            currentView = BudgetScreenView.CATEGORY_DETAIL
                        }
                    )
                ),
                if (!isViewer) {
                    listOf(
                        MenuSheetActionItem(
                            text = "Rename Category",
                            icon = editIcon,
                            iconPlacement = IconPlacement.Left,
                            onClick = {
                                showCategoryMenuBottomSheet = false
                                categoryToRename = selectedCategoryForMenu
                                showAddCustomCategorySheet = true
                            }
                        )
                    )
                } else null,
                if (!isViewer) {
                    listOf(
                        MenuSheetActionItem(
                            text = "Delete Category",
                            icon = deleteIcon,
                            iconPlacement = IconPlacement.Left,
                            contentColor = MaterialTheme.colorScheme.error,
                            onClick = {
                                categoryToDeleteConfirm = selectedCategoryForMenu
                                showCategoryMenuBottomSheet = false
                                selectedCategoryForMenu = null
                            }
                        )
                    )
                } else null
            ),
            onCancelClick = {
                showCategoryMenuBottomSheet = false
                selectedCategoryForMenu = null
            }
        )
    }

    if (categoryToDeleteConfirm != null) {
        ConfirmationBottomSheet(
            heading = "Are you sure?",
            subHeading = "The category will be deleted permanently.",
            confirmButtonText = "Delete Category",
            onDismiss = {
                categoryToDeleteConfirm = null
            },
            onConfirm = {
                val categoryToDelete = categoryToDeleteConfirm
                if (categoryToDelete != null) {
                    viewModel.deleteExpensesByCategory(categoryToDelete)
                    defaultCategories = defaultCategories.filter { it != categoryToDelete }
                }
                categoryToDeleteConfirm = null
            }
        )
    }

    if (showRoomMenuBottomSheet) {
        MenuBottomSheet(
            items = listOf(
                listOf(
                    MenuSheetActionItem(
                        text = "Leave Room",
                        icon = painterResource(R.drawable.ic_logout),
                        iconPlacement = IconPlacement.Left,
                        contentColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            showLeaveConfirmation = true
                        }
                    )
                )
            ),
            onCancelClick = {
                showRoomMenuBottomSheet = false
            }
        )
    }

    if (userToRemove != null) {
        ConfirmationBottomSheet(
            heading = "Remove Member from Budget Tracker?",
            subHeading = "They will not be able to access this room anymore.",
            confirmButtonText = "Remove",
            onDismiss = {
                userToRemove = null
            },
            onConfirm = {
                val target = userToRemove
                if (target != null) {
                    activeEvent?.id?.let { id ->
                        roomViewModel.removeAccess(id, "Budget", target.uid)
                        toastData = ToastData("${target.name} removed from room", ToastType.SUCCESS)
                    }
                }
                userToRemove = null
            }
        )
    }

    if (showLeaveConfirmation) {
        ConfirmationBottomSheet(
            heading = "Leaving Budget Room?",
            subHeading = "You will lose access to this room and won't be able to see updates.",
            confirmButtonText = "Leave",
            onDismiss = {
                showLeaveConfirmation = false
            },
            onConfirm = {
                activeEvent?.id?.let { id ->
                    roomViewModel.removeAccess(id, "Budget", currentUserUid)
                }
                toastData = ToastData("You left the room", ToastType.DEFAULT)
                currentView = BudgetScreenView.BUDGET_TRACKER
                showLeaveConfirmation = false
            }
        )
    }
}

@Preview(name = "Budget Screen Preview", showBackground = true)
@Composable
fun BudgetScreenPreview() {
    JasnifyTheme {
        BudgetScreen(
            onBackClick = {},
        )
    }
}
