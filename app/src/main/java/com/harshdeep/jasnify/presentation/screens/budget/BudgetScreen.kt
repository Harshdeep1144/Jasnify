package com.harshdeep.jasnify.presentation.screens.budget

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
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
import com.harshdeep.jasnify.presentation.components.bottomdrawer.budget.AddCustomCategoryBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.budget.AddExpenseBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.budget.EditBudgetBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.PieChartSlice
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.buttons.AskAiButton
import com.harshdeep.jasnify.presentation.screens.chats.AiChatScreen
import com.harshdeep.jasnify.presentation.components.states.BudgetLoadingState
import com.harshdeep.jasnify.presentation.screens.chats.GroupChatScreen
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import kotlinx.coroutines.delay
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
    MANAGE_ROOM_ACCESS,
    GROUP_CHAT,
    HELP_FEEDBACK
}

private val DefaultCategoryList = listOf(
    "Venue",
    "Catering",
    "Gifts",
    "Staff & Crew",
    "Costumes",
    "Vendors",
    "Transportation",
    "Entertainment",
    "Equipment Rentals",
    "Unplanned Costs"
)

private val SortOptionsList = listOf(
    "Newest First",
    "Oldest First",
    "Highest Amount",
    "Lowest Amount"
)

private val FilterOptionsList = listOf(
    "Vendors",
    "Catering",
    "Beauty",
    "Stationery",
    "Apparel",
    "Beverages",
    "Transport",
    "Equipment Rentals"
)

private val PaletteColors = listOf(
    Color(0xFF1D5590), Color(0xFFFF1E56), Color(0xFFE56B8F), Color(0xFF0FAD48),
    Color(0xFF2FA4C4), Color(0xFF8D16FF), Color(0xFFFFB020), Color(0xFF00C9A7),
    Color(0xFF6C5B7B), Color(0xFF355C7D), Color(0xFFF67280), Color(0xFFC06C84),
    Color(0xFFFF8C94), Color(0xFF45B6FE), Color(0xFF50B498), Color(0xFF9B59B6),
    Color(0xFFE67E22), Color(0xFF16A085)
)

private fun parseExpenseAmount(amountStr: String): Double {
    return amountStr.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onBackClick: () -> Unit,
    navController: androidx.navigation.NavHostController? = null,
    viewModel: BudgetViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    profileViewModel: com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    var showAiChat by remember { mutableStateOf(false) }
    var currentView by remember { mutableStateOf(BudgetScreenView.BUDGET_TRACKER) }
    var aiChatContext by remember { mutableStateOf("") }

    val expensesEntities by viewModel.expenses.collectAsStateWithLifecycle()
    val budgetEntity by viewModel.budgetSettings.collectAsStateWithLifecycle()
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUserUid = remember(auth.currentUser) { auth.currentUser?.uid.orEmpty() }

    val currentUserInRoom = remember(roomUsers, currentUserUid) {
        roomUsers.find { it.uid == currentUserUid }
    }
    val currentUserName = remember(currentUserInRoom, auth.currentUser) {
        currentUserInRoom?.name ?: auth.currentUser?.displayName ?: "Anonymous"
    }

    val isOwner = remember(activeEvent, currentUserUid) {
        activeEvent?.ownerId == currentUserUid
    }
    val currentUserRole = remember(isOwner, currentUserInRoom) {
        when {
            isOwner -> UserRole.OWNER
            currentUserInRoom != null -> currentUserInRoom.role
            else -> UserRole.VIEWER
        }
    }
    val isViewer = currentUserRole == UserRole.VIEWER

    LaunchedEffect(Unit) {
        roomViewModel.resetAccessState()
        eventViewModel.fetchUserEvents()
    }

    LaunchedEffect(activeEventId) {
        val id = activeEventId
        if (id != null) {
            viewModel.setEventId(id)
            roomViewModel.verifyAccess(id, "Budget", currentUserUid)
            roomViewModel.loadRoomUsers(id, "Budget")
        }
    }

    if (activeEvent == null) {
        BudgetLoadingState()
        return
    }

    val indianLocale = remember { Locale("en", "IN") }
    val formatter = remember(indianLocale) { NumberFormat.getNumberInstance(indianLocale) }
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy, hh:mma", Locale.ENGLISH) }

    val allExpenses = remember(expensesEntities, formatter, dateFormatter) {
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

    var toastData by remember { mutableStateOf(ToastData()) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            if (toastData.type == ToastType.ERROR) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (toastData.message?.contains("Please", ignoreCase = true) == true ||
                    toastData.message?.contains("enter", ignoreCase = true) == true ||
                    toastData.message?.contains("select", ignoreCase = true) == true
                ) {
                    delay(80.milliseconds)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    BackHandler(enabled = currentView != BudgetScreenView.BUDGET_TRACKER || showAiChat) {
        if (showAiChat) {
            showAiChat = false
        } else {
            currentView = when (currentView) {
                BudgetScreenView.EXPENSE_SUMMARY -> BudgetScreenView.BUDGET_TRACKER
                BudgetScreenView.EXPENSE_CATEGORY -> BudgetScreenView.BUDGET_TRACKER
                BudgetScreenView.CATEGORY_DETAIL -> BudgetScreenView.EXPENSE_CATEGORY
                BudgetScreenView.MANAGE_ROOM_ACCESS -> BudgetScreenView.GROUP_CHAT
                BudgetScreenView.GROUP_CHAT -> BudgetScreenView.BUDGET_TRACKER
                BudgetScreenView.BUDGET_TRACKER -> BudgetScreenView.BUDGET_TRACKER
                BudgetScreenView.HELP_FEEDBACK -> BudgetScreenView.BUDGET_TRACKER
            }
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

    var showBottomSheet by remember { mutableStateOf(false) }
    var showAddExpenseSheet by remember { mutableStateOf(false) }
    var showAddCustomCategorySheet by remember { mutableStateOf(false) }
    var showEditBudgetSheet by remember { mutableStateOf(false) }
    var showMenuBottomSheet by remember { mutableStateOf(false) }
    var showCategoryMenuBottomSheet by remember { mutableStateOf(false) }
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }
    var expenseToEdit by remember { mutableStateOf<ExpenseItem?>(null) }
    var expenseToDelete by remember { mutableStateOf<ExpenseItem?>(null) }
    var categoryToDeleteConfirm by remember { mutableStateOf<String?>(null) }
    var categoryToRename by remember { mutableStateOf<String?>(null) }

    val budgetValue = remember(budgetEntity, activeEvent) {
        val rawValue = budgetEntity?.totalBudget ?: activeEvent?.budget
        if (rawValue == null) "INR" else {
            val plainString = BigDecimal.valueOf(rawValue).toPlainString()
            val cleanString = if (plainString.endsWith(".0")) plainString.substringBefore(".0") else plainString
            "INR$cleanString"
        }
    }

    var selectedCategoryForMenu by remember { mutableStateOf<String?>(null) }
    var selectedCategoryForDetails by remember { mutableStateOf<String?>(null) }
    var selectedCategoryChips by remember { mutableStateOf(setOf("Recent First")) }
    var sheetMotionProgress by remember { mutableFloatStateOf(1.0f) }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            showBottomSheet || showAddExpenseSheet || showAddCustomCategorySheet ||
                    showEditBudgetSheet || showMenuBottomSheet || showCategoryMenuBottomSheet ||
                    showRoomMenuBottomSheet || expenseToDelete != null || categoryToDeleteConfirm != null ||
                    userToRemove != null || showLeaveConfirmation
        }
    }

    val targetScale = if (isAnyBottomSheetOpen) 0.92f + (0.08f * sheetMotionProgress) else 1.0f

    val backdropScaleState = animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadiusState = animateDpAsState(
        targetValue = if (isAnyBottomSheetOpen) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    var selectedSortOption by remember { mutableStateOf("Newest First") }
    var selectedFilterOptions by remember { mutableStateOf(emptySet<String>()) }

    var defaultCategories by remember {
        mutableStateOf(DefaultCategoryList)
    }

    val isBudgetNotSet = remember(budgetEntity, activeEvent) {
        budgetEntity?.totalBudget == null && activeEvent?.budget == null
    }

    val totalBudget = remember(budgetValue) {
        val numericPart = budgetValue.dropWhile { !it.isDigit() }
        numericPart.toDoubleOrNull() ?: 0.0
    }

    val totalSpent = remember(allExpenses) {
        allExpenses.sumOf { parseExpenseAmount(it.amount) }
    }

    val remainingFunds = remember(totalBudget, totalSpent) {
        (totalBudget - totalSpent).coerceAtLeast(0.0)
    }

    val remainingPercentage = remember(totalBudget, remainingFunds) {
        if (totalBudget > 0) (remainingFunds / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    }

    val spentPercentage = remember(totalBudget, totalSpent) {
        if (totalBudget > 0) (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    }

    val formattedRemaining = remember(remainingFunds, formatter) {
        "₹${formatter.format(remainingFunds.toLong())}"
    }
    val formattedTotalSpent = remember(totalSpent, formatter) {
        "₹${formatter.format(totalSpent.toLong())}"
    }
    val formattedTotalBudget = remember(totalBudget, formatter) {
        formatter.format(totalBudget.toLong())
    }

    val centerTextPrimaryValue = remember(totalSpent, formatter) {
        when {
            totalSpent >= 10000000.0 -> "₹ ${String.format(Locale.ENGLISH, "%.1f", totalSpent / 10000000.0)} Cr"
            totalSpent >= 100000.0 -> "₹ ${String.format(Locale.ENGLISH, "%.1f", totalSpent / 100000.0)} L"
            totalSpent >= 1000.0 -> "₹ ${String.format(Locale.ENGLISH, "%.1f", totalSpent / 1000.0)} K"
            else -> "₹ ${formatter.format(totalSpent.toLong())}"
        }
    }

    val filteredExpenses = remember(allExpenses, searchQuery, selectedFilterOptions, selectedSortOption) {
        val query = searchQuery.trim()
        allExpenses.filter { item ->
            val matchesSearch = query.isEmpty() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)
            val matchesCategory = selectedFilterOptions.isEmpty() || selectedFilterOptions.contains(item.category)
            matchesSearch && matchesCategory
        }.let { list ->
            when (selectedSortOption) {
                "Highest Amount" -> list.sortedByDescending { parseExpenseAmount(it.amount) }
                "Lowest Amount" -> list.sortedBy { parseExpenseAmount(it.amount) }
                "Oldest First" -> list.sortedBy { it.id.toIntOrNull() ?: 0 }
                else -> list.sortedByDescending { it.id.toIntOrNull() ?: 0 }
            }
        }
    }

    val computedCategories = remember(allExpenses, defaultCategories, formatter) {
        val grouped = allExpenses.groupBy { it.category }
        val finalCategories = (grouped.keys + defaultCategories).distinct()
        finalCategories.map { catName ->
            val items = grouped[catName].orEmpty()
            val totalAmt = items.sumOf { parseExpenseAmount(it.amount) }
            CategorySummaryData(
                name = catName,
                amountFormatted = "₹${formatter.format(totalAmt.toLong())}",
                amountRaw = totalAmt,
                emojis = items.map { it.emoji },
                totalCount = items.size
            )
        }.sortedByDescending { it.amountRaw }
    }

    val filteredCategorySummary = remember(computedCategories, categorySearchQuery) {
        val query = categorySearchQuery.trim()
        if (query.isEmpty()) {
            computedCategories
        } else {
            computedCategories.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    val categoryColors = remember(allExpenses, defaultCategories) {
        (allExpenses.map { it.category } + defaultCategories).distinct().mapIndexed { index, category ->
            category to PaletteColors[index % PaletteColors.size]
        }.toMap()
    }

    val getCategoryColor: (String) -> Color = remember(categoryColors) {
        { categoryName -> categoryColors[categoryName] ?: ContentSecondary }
    }

    val pieSlices = remember(allExpenses, categoryColors) {
        allExpenses.groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { parseExpenseAmount(it.amount) } }
            .toList()
            .sortedByDescending { it.second }
            .map { (cat, amt) -> PieChartSlice(amt.toFloat(), getCategoryColor(cat), cat) }
    }

    val addExpenseIcon = painterResource(R.drawable.ic_plus)
    val editIcon = painterResource(R.drawable.ic_edit)
    val userDefaultIcon = painterResource(R.drawable.ic_user_default)
    val categoryIcon = painterResource(R.drawable.ic_category)
    val pieChartIcon = painterResource(R.drawable.ic_pie_chart)
    val deleteIcon = painterResource(R.drawable.ic_delete)
    val logoutIcon = painterResource(R.drawable.ic_logout)

    RoomAccessGuardian(hasAccess = hasAccess, roomName = "Budget", onBackClick = onBackClick) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Box(modifier = Modifier.fillMaxSize().graphicsLayer {
                scaleX = backdropScaleState.value
                scaleY = backdropScaleState.value
                val radius = backdropCornerRadiusState.value
                clip = isAnyBottomSheetOpen || radius > 0.dp
                shape = RoundedCornerShape(radius.coerceAtLeast(0.dp))
            }) {
                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            focusManager.clearFocus()
                        },
                    floatingActionButton = {
                        if (currentView == BudgetScreenView.BUDGET_TRACKER && !isViewer && !showAiChat) {
                            CustomIconButton(
                                onClick = {
                                    expenseToEdit = null
                                    showAddExpenseSheet = true
                                },
                                icon = addExpenseIcon,
                                size = ButtonSize.Large,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 24.dp)
                                    .shadow(16.dp, CircleShape)
                            )
                        }
                    },
                ) { paddingValues ->
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
                        AnimatedContent(
                            targetState = currentView,
                            transitionSpec = { fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200)) },
                            label = "BudgetScreenTransition"
                        ) { targetScreen ->
                            when (targetScreen) {
                                BudgetScreenView.BUDGET_TRACKER -> BudgetTrackerContent(
                                    isBudgetNotSet = isBudgetNotSet,
                                    formattedTotalBudget = formattedTotalBudget,
                                    formattedRemaining = formattedRemaining,
                                    remainingPercentage = remainingPercentage,
                                    isOwner = isOwner,
                                    isViewer = isViewer,
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = { searchQuery = it },
                                    filteredExpenses = filteredExpenses,
                                    expandedCardId = expandedCardId,
                                    onExpandedCardIdChange = { expandedCardId = it },
                                    onBackClick = onBackClick,
                                    onMenuClick = { showMenuBottomSheet = true },
                                    onChatClick = {
                                        currentView = BudgetScreenView.GROUP_CHAT
                                    },
                                    onEditBudgetClick = { showEditBudgetSheet = true },
                                    onViewSummaryClick = { currentView = BudgetScreenView.EXPENSE_SUMMARY },
                                    onFilterClick = { showBottomSheet = true },
                                    onDeleteExpenseClick = { expenseToDelete = it },
                                    onModifyExpenseClick = {
                                        expenseToEdit = it
                                        showAddExpenseSheet = true
                                    },
                                    listState = listState,
                                    isSearchBarFocused = isSearchBarFocused,
                                    onSearchBarFocusChange = { isSearchBarFocused = it }
                                )

                                BudgetScreenView.EXPENSE_SUMMARY -> ExpenseSummaryContent(
                                    pieSlices = pieSlices,
                                    centerTextPrimaryValue = centerTextPrimaryValue,
                                    formattedRemaining = formattedRemaining,
                                    remainingPercentageText = String.format(Locale.ENGLISH, "%.0f", remainingPercentage * 100),
                                    formattedTotalSpent = formattedTotalSpent,
                                    spentPercentageText = String.format(Locale.ENGLISH, "%.0f", spentPercentage * 100),
                                    processedCategories = allExpenses.groupBy { it.category }
                                        .mapValues { (_, items) -> items.sumOf { parseExpenseAmount(it.amount) } }
                                        .toList()
                                        .sortedByDescending { it.second },
                                    getCategoryColor = getCategoryColor,
                                    isViewer = isViewer,
                                    onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                    onManageCategoriesClick = { currentView = BudgetScreenView.EXPENSE_CATEGORY },
                                    onAiOverviewClick = {
                                        aiChatContext = """
                                            Budget Summary for ${activeEvent?.name ?: "Event"}:
                                            Total Budget: ₹$formattedTotalBudget
                                            Total Spent: $formattedTotalSpent
                                            Remaining: $formattedRemaining (${(remainingPercentage * 100).toInt()}%)
                                            
                                            Category Breakdown:
                                            ${computedCategories.joinToString("\n") { "${it.name}: ${it.amountFormatted} (${it.totalCount} items)" }}
                                            
                                            Recent Expenses:
                                            ${allExpenses.take(10).joinToString("\n") { "- ${it.title}: ${it.amount} (${it.category})" }}
                                        """.trimIndent()
                                        showAiChat = true
                                    },
                                    formatAmount = { formatter.format(it.toLong()) }
                                )

                                BudgetScreenView.EXPENSE_CATEGORY -> ExpenseCategoryContent(
                                    categorySearchQuery = categorySearchQuery,
                                    onCategorySearchQueryChange = { categorySearchQuery = it },
                                    filteredCategorySummary = filteredCategorySummary,
                                    isViewer = isViewer,
                                    onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                    onCategoryClick = {
                                        selectedCategoryForDetails = it
                                        currentView = BudgetScreenView.CATEGORY_DETAIL
                                    },
                                    onCategoryMenuClick = {
                                        selectedCategoryForMenu = it
                                        showCategoryMenuBottomSheet = true
                                    },
                                    onAddCategoryClick = {
                                        categoryToRename = null
                                        showAddCustomCategorySheet = true
                                    },
                                    eventId = activeEvent?.id
                                )

                                BudgetScreenView.CATEGORY_DETAIL -> {
                                    val catName = selectedCategoryForDetails ?: "Category"
                                    val catExpenses = allExpenses.filter { it.category == catName }
                                    val catTotal = catExpenses.sumOf { parseExpenseAmount(it.amount) }
                                    val sortedCatExpenses = catExpenses.sortedWith { a, b ->
                                        val amtA = parseExpenseAmount(a.amount)
                                        val amtB = parseExpenseAmount(b.amount)
                                        val res = when {
                                            selectedCategoryChips.contains("Most Expensive") && !selectedCategoryChips.contains("Least Expensive") -> amtB.compareTo(amtA)
                                            selectedCategoryChips.contains("Least Expensive") && !selectedCategoryChips.contains("Most Expensive") -> amtA.compareTo(amtB)
                                            else -> 0
                                        }
                                        if (res == 0) (b.id.toIntOrNull() ?: 0).compareTo(a.id.toIntOrNull() ?: 0) else res
                                    }
                                    CategoryDetailContent(
                                        selectedCategoryName = catName,
                                        formattedCategoryTotal = "₹${formatter.format(catTotal.toLong())}",
                                        categoryProgressRatio = if (totalBudget > 0) (catTotal / totalBudget).toFloat().coerceIn(0f, 1f) else 0f,
                                        sortedCategoryExpenses = sortedCatExpenses,
                                        selectedCategoryChips = selectedCategoryChips,
                                        onCategoryChipsChange = { selectedCategoryChips = it },
                                        expandedCardId = expandedCardId,
                                        onExpandedCardIdChange = { expandedCardId = it },
                                        isViewer = isViewer,
                                        onBackClick = { currentView = BudgetScreenView.EXPENSE_CATEGORY },
                                        onRenameCategoryClick = {
                                            categoryToRename = catName
                                            showAddCustomCategorySheet = true
                                        },
                                        onDeleteExpenseClick = { expenseToDelete = it },
                                        onModifyExpenseClick = {
                                            expenseToEdit = it
                                            showAddExpenseSheet = true
                                        }
                                    )
                                }

                                BudgetScreenView.MANAGE_ROOM_ACCESS -> BudgetRoomContent(
                                    eventId = activeEventId.orEmpty(),
                                    roomUsers = roomUsers,
                                    currentUserUid = currentUserUid,
                                    currentUserRole = currentUserRole,
                                    searchResults = searchResults,
                                    roomViewModel = roomViewModel,
                                    onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                    onMenuClick = {
                                        focusManager.clearFocus()
                                        showRoomMenuBottomSheet = true
                                    },
                                    onRemoveClick = { userToRemove = it },
                                    onLeaveClick = { showLeaveConfirmation = true },
                                    onToastShow = { toastData = it }
                                )

                                BudgetScreenView.HELP_FEEDBACK -> {
                                    com.harshdeep.jasnify.presentation.screens.main.tabs.profile.HelpFeedbackScreen(
                                        profileViewModel = profileViewModel,
                                        onBack = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                        onShowAiChat = { showAiChat = true }
                                    )
                                }

                                BudgetScreenView.GROUP_CHAT -> {
                                    GroupChatScreen(
                                        eventId = activeEventId.orEmpty(),
                                        roomType = "Budget",
                                        onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                        onMembersClick = { currentView = BudgetScreenView.MANAGE_ROOM_ACCESS }
                                    )
                                }
                            }
                        }
                    }

                    if (!isAnyBottomSheetOpen && !showAiChat && expensesEntities.isNotEmpty()) {
                        AskAiButton(
                            onClick = {
                                aiChatContext = """
                                    Budget Overview for ${activeEvent?.name ?: "Event"}:
                                    Total Budget: ₹$formattedTotalBudget
                                    Total Spent: $formattedTotalSpent
                                    Remaining: $formattedRemaining (${(remainingPercentage * 100).toInt()}%)
                                    
                                    Category Breakdown:
                                    ${computedCategories.joinToString("\n") { "${it.name}: ${it.amountFormatted}" }}
                                """.trimIndent()
                                showAiChat = true
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 156.dp)
                                .zIndex(150f)
                        )
                    }

                    AnimatedVisibility(
                        visible = showAiChat,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier.zIndex(200f)
                    ) {
                        AiChatScreen(
                            eventId = activeEvent?.id,
                            initialContext = aiChatContext,
                            shouldStartNewSession = true,
                            onBackClick = {
                                showAiChat = false
                                focusManager.clearFocus()
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = toastData.message != null && !isAnyBottomSheetOpen,
                        enter = slideInVertically(initialOffsetY = { -it - 500 }),
                        exit = slideOutVertically(targetOffsetY = { -it - 500 }),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .zIndex(99f)
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                    ) {
                        CustomToast(message = toastData.message.orEmpty(), type = toastData.type)
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().zIndex(100f)) {
            if (showBottomSheet) {
                SortFilterBottomSheet(
                    sortOptions = SortOptionsList,
                    initialSortOption = selectedSortOption,
                    filterByOptions = FilterOptionsList,
                    initialFilterOptions = selectedFilterOptions,
                    onDismiss = { showBottomSheet = false },
                    onApply = { sort, filters ->
                        selectedSortOption = sort
                        selectedFilterOptions = filters
                        showBottomSheet = false
                    },
                    onProgress = { sheetMotionProgress = it }
                )
            }

            if (showAddExpenseSheet) {
                AddExpenseBottomSheet(
                    onDismiss = {
                        showAddExpenseSheet = false
                        expenseToEdit = null
                    },
                    onSave = { amount, receiver, category, emoji, phone, notes ->
                        val editingItem = expenseToEdit
                        if (editingItem != null) {
                            viewModel.updateExpense(
                                editingItem.id,
                                receiver.ifBlank { "Unnamed Receiver" },
                                category.ifBlank { "Misc" },
                                amount.toDouble(),
                                emoji.ifBlank { "💸" },
                                currentUserName,
                                phone,
                                notes
                            )
                            toastData = ToastData("Expense Updated!", ToastType.SUCCESS)
                        } else {
                            viewModel.addExpense(
                                receiver.ifBlank { "Unnamed Receiver" },
                                category.ifBlank { "Misc" },
                                amount.toDouble(),
                                emoji.ifBlank { "💸" },
                                currentUserName,
                                phone,
                                notes
                            )
                            toastData = ToastData("Expense Added!", ToastType.SUCCESS)
                        }
                        showAddExpenseSheet = false
                        expenseToEdit = null
                    },
                    categories = defaultCategories,
                    onAddCategory = {
                        if (!defaultCategories.contains(it)) defaultCategories = defaultCategories + it
                    },
                    initialAmount = expenseToEdit?.amount?.replace("₹", "")?.replace(",", "").orEmpty(),
                    initialReceiver = expenseToEdit?.title.orEmpty(),
                    initialCategory = expenseToEdit?.category.orEmpty(),
                    initialEmoji = expenseToEdit?.emoji.orEmpty(),
                    initialPhoneNumber = expenseToEdit?.phoneNumber.orEmpty(),
                    initialNote = expenseToEdit?.note.orEmpty(),
                    onProgress = { sheetMotionProgress = it }
                )
            }

            if (showAddCustomCategorySheet) {
                AddCustomCategoryBottomSheet(
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
                        } else if (!defaultCategories.contains(inputName)) {
                            defaultCategories = defaultCategories + inputName
                        }
                        showAddCustomCategorySheet = false
                        categoryToRename = null
                    },
                    initialCategoryName = categoryToRename.orEmpty(),
                    heading = if (categoryToRename != null) "Rename category" else "Add custom category",
                    onProgress = { sheetMotionProgress = it }
                )
            }
        }

        if (expenseToDelete != null) {
            ConfirmationBottomSheet(
                heading = "Are you sure?",
                subHeading = "The expense amount will be added back to the total budget.",
                confirmButtonText = "Delete Expense",
                onDismiss = { expenseToDelete = null },
                onConfirm = {
                    expenseToDelete?.id?.let { viewModel.deleteExpense(it) }
                    expenseToDelete = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showEditBudgetSheet) {
            EditBudgetBottomSheet(
                initialBudgetValue = budgetValue,
                isBudgetNotSet = isBudgetNotSet,
                onDismiss = { showEditBudgetSheet = false },
                onUpdateBudget = { updatedValue ->
                    val numericPart = updatedValue.dropWhile { !it.isDigit() }
                    viewModel.updateBudget(numericPart.toDoubleOrNull() ?: 0.0)
                    showEditBudgetSheet = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showMenuBottomSheet) {
            val menuItems = listOf(
                listOfNotNull(
                    if (isOwner) {
                        MenuSheetActionItem(
                            text = if (isBudgetNotSet) "Add Budget" else "Edit Budget",
                            icon = if (isBudgetNotSet) addExpenseIcon else editIcon,
                            onClick = {
                                showMenuBottomSheet = false
                                showEditBudgetSheet = true
                            },
                            iconPlacement = IconPlacement.Top
                        )
                    } else null,
                    MenuSheetActionItem(
                        text = "Categories",
                        icon = categoryIcon,
                        onClick = {
                            showMenuBottomSheet = false
                            currentView = BudgetScreenView.EXPENSE_CATEGORY
                        },
                        iconPlacement = if(isOwner) IconPlacement.Top else IconPlacement.Left
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = painterResource(R.drawable.ic_help_feedback),
                        onClick = {
                            showMenuBottomSheet = false
                            currentView = BudgetScreenView.HELP_FEEDBACK
                        }
                    )
                )
            )

            MenuBottomSheet(
                items = menuItems,
                onCancelClick = { showMenuBottomSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showCategoryMenuBottomSheet) {
            val categoryMenuItems = listOfNotNull(
                listOfNotNull(
                    MenuSheetActionItem(
                        text = "View Expenses",
                        icon = pieChartIcon,
                        onClick = {
                            showCategoryMenuBottomSheet = false
                            selectedCategoryForDetails = selectedCategoryForMenu
                            currentView = BudgetScreenView.CATEGORY_DETAIL
                        },
                        iconPlacement = IconPlacement.Top
                    ),
                    if (!isViewer) {
                        MenuSheetActionItem(
                            text = "Rename Category",
                            icon = editIcon,
                            onClick = {
                                showCategoryMenuBottomSheet = false
                                categoryToRename = selectedCategoryForMenu
                                showAddCustomCategorySheet = true
                            },
                            iconPlacement = IconPlacement.Top
                        )
                    } else null
                ),
                if (!isViewer) {
                    listOf(
                        MenuSheetActionItem(
                            text = "Delete Category",
                            icon = deleteIcon,
                            contentColor = MaterialTheme.colorScheme.error,
                            onClick = {
                                categoryToDeleteConfirm = selectedCategoryForMenu
                                showCategoryMenuBottomSheet = false
                                selectedCategoryForMenu = null
                            }
                        )
                    )
                } else null
            )

            MenuBottomSheet(
                items = categoryMenuItems,
                onCancelClick = {
                    showCategoryMenuBottomSheet = false
                    selectedCategoryForMenu = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (categoryToDeleteConfirm != null) {
            ConfirmationBottomSheet(
                heading = "Are you sure?",
                subHeading = "The category will be deleted permanently.",
                confirmButtonText = "Delete Category",
                onDismiss = { categoryToDeleteConfirm = null },
                onConfirm = {
                    val cat = categoryToDeleteConfirm
                    if (cat != null) {
                        viewModel.deleteExpensesByCategory(cat)
                        defaultCategories = defaultCategories.filter { it != cat }
                    }
                    categoryToDeleteConfirm = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showRoomMenuBottomSheet) {
            val roomMenuItems = listOf(
                listOf(
                    MenuSheetActionItem(
                        text = "Leave Room",
                        icon = logoutIcon,
                        contentColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            showLeaveConfirmation = true
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = painterResource(R.drawable.ic_help_feedback),
                        onClick = {
                            showMenuBottomSheet = false
                            currentView = BudgetScreenView.HELP_FEEDBACK
                        }
                    )
                )
            )

            MenuBottomSheet(
                items = roomMenuItems,
                onCancelClick = { showRoomMenuBottomSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        userToRemove?.let { user ->
            ConfirmationBottomSheet(
                heading = "Remove ${user.name} from Budget Tracker?",
                subHeading = "They will not be able to access this room anymore.",
                confirmButtonText = "Remove",
                onDismiss = { userToRemove = null },
                onConfirm = {
                    activeEvent?.id?.let { id ->
                        roomViewModel.removeAccess(id, "Budget", user.uid)
                        toastData = ToastData("${user.name} removed from room", ToastType.SUCCESS)
                    }
                    userToRemove = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showLeaveConfirmation) {
            ConfirmationBottomSheet(
                heading = "Leaving Budget Room?",
                subHeading = "You will lose access to this room and won't be able to see updates.",
                confirmButtonText = "Leave",
                onDismiss = { showLeaveConfirmation = false },
                onConfirm = {
                    activeEvent?.id?.let { id ->
                        roomViewModel.removeAccess(id, "Budget", currentUserUid)
                    }
                    toastData = ToastData("You left the room", ToastType.DEFAULT)
                    currentView = BudgetScreenView.BUDGET_TRACKER
                    showLeaveConfirmation = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}