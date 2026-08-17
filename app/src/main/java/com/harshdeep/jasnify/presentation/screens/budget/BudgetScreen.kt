package com.harshdeep.jasnify.presentation.screens.budget

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.draw.shadow
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
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
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddCustomCategoryBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddExpenseBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.EditBudgetBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.PieChartSlice
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.theme.ContentSecondary
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

    LaunchedEffect(Unit) {
        roomViewModel.resetAccessState()
        eventViewModel.fetchUserEvents()
    }

    LaunchedEffect(activeEventId) {
        activeEventId?.let { id ->
            viewModel.setEventId(id)
            roomViewModel.verifyAccess(id, "Budget", currentUserUid)
            roomViewModel.loadRoomUsers(id, "Budget")
        }
    }

    val indianLocale = remember { Locale("en", "IN") }
    val formatter = remember(indianLocale) { NumberFormat.getNumberInstance(indianLocale) }
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy, hh:mma", Locale.ENGLISH) }

    val allExpenses = remember(expensesEntities) {
        derivedStateOf {
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
    }.value

    var toastData by remember { mutableStateOf(ToastData()) }
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

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

    val targetScale by remember {
        derivedStateOf { if (isAnyBottomSheetOpen) 0.92f + (0.08f * sheetMotionProgress) else 1.0f }
    }

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

    val sortOptions = remember { listOf("Newest First", "Oldest First", "Highest Amount", "Lowest Amount") }
    val filterOptions = remember { listOf("Vendors", "Catering", "Beauty", "Stationery", "Apparel", "Beverages", "Transport", "Equipment Rentals") }

    var selectedSortOption by remember { mutableStateOf("Newest First") }
    var selectedFilterOptions by remember { mutableStateOf(emptySet<String>()) }

    var defaultCategories by remember {
        mutableStateOf(listOf("Venue", "Catering", "Gifts", "Staff & Crew", "Costumes", "Vendors", "Transportation", "Entertainment", "Equipment Rentals", "Unplanned Costs"))
    }

    val parseAmount = { amountStr: String -> amountStr.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0 }

    val isBudgetNotSet = remember(budgetEntity, activeEvent) { budgetEntity?.totalBudget == null && activeEvent?.budget == null }

    val totalBudget = remember(budgetValue) {
        val numericPart = budgetValue.dropWhile { !it.isDigit() }
        numericPart.toDoubleOrNull() ?: 0.0
    }

    val totalSpent = remember(allExpenses) {
        derivedStateOf { allExpenses.sumOf { parseAmount(it.amount) } }
    }.value

    val remainingFunds = remember(totalBudget, totalSpent) {
        derivedStateOf { (totalBudget - totalSpent).coerceAtLeast(0.0) }
    }.value

    val remainingPercentage = remember(totalBudget, remainingFunds) {
        derivedStateOf { if (totalBudget > 0) (remainingFunds / totalBudget).toFloat().coerceIn(0f, 1f) else 0f }
    }.value

    val spentPercentage = remember(totalBudget, totalSpent) {
        derivedStateOf { if (totalBudget > 0) (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f) else 0f }
    }.value

    val formattedRemaining = "₹${formatter.format(remainingFunds.toLong())}"
    val formattedTotalSpent = "₹${formatter.format(totalSpent.toLong())}"
    val formattedTotalBudget = formatter.format(totalBudget.toLong())

    val centerTextPrimaryValue = remember(totalSpent) {
        when {
            totalSpent >= 10000000.0 -> "₹ ${String.format(Locale.ENGLISH, "%.1f", totalSpent / 10000000.0)} Cr"
            totalSpent >= 100000.0 -> "₹ ${String.format(Locale.ENGLISH, "%.1f", totalSpent / 100000.0)} L"
            totalSpent >= 1000.0 -> "₹ ${String.format(Locale.ENGLISH, "%.1f", totalSpent / 1000.0)} K"
            else -> "₹ ${formatter.format(totalSpent.toLong())}"
        }
    }

    val filteredExpenses = remember(allExpenses, searchQuery, selectedFilterOptions, selectedSortOption) {
        derivedStateOf {
            allExpenses.filter { item ->
                val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) || item.category.contains(searchQuery, ignoreCase = true)
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
        }
    }.value

    val computedCategories = remember(allExpenses, defaultCategories) {
        derivedStateOf {
            val grouped = allExpenses.groupBy { it.category }
            val finalCategories = (grouped.keys + defaultCategories).distinct()
            finalCategories.map { catName ->
                val items = grouped[catName] ?: emptyList()
                val totalAmt = items.sumOf { parseAmount(it.amount) }
                CategorySummaryData(catName, "₹${formatter.format(totalAmt.toLong())}", totalAmt, items.map { it.emoji }, items.size)
            }.sortedByDescending { it.amountRaw }
        }
    }.value

    val filteredCategorySummary = remember(computedCategories, categorySearchQuery) {
        derivedStateOf {
            computedCategories.filter { it.name.contains(categorySearchQuery, ignoreCase = true) }
        }
    }.value

    val colorPalette = remember { listOf(Color(0xFF1D5590), Color(0xFFFF1E56), Color(0xFFE56B8F), Color(0xFF0FAD48), Color(0xFF2FA4C4), Color(0xFF8D16FF), Color(0xFFFFB020), Color(0xFF00C9A7), Color(0xFF6C5B7B), Color(0xFF355C7D), Color(0xFFF67280), Color(0xFFC06C84), Color(0xFFFF8C94), Color(0xFF45B6FE), Color(0xFF50B498), Color(0xFF9B59B6), Color(0xFFE67E22), Color(0xFF16A085)) }

    val categoryColors = remember(allExpenses, defaultCategories, colorPalette) {
        (allExpenses.map { it.category } + defaultCategories).distinct().mapIndexed { index, category ->
            category to colorPalette[index % colorPalette.size]
        }.toMap()
    }

    val getCategoryColor = { categoryName: String -> categoryColors[categoryName] ?: ContentSecondary }

    val pieSlices = remember(allExpenses, categoryColors) {
        derivedStateOf {
            allExpenses.groupBy { it.category }
                .mapValues { (_, items) -> items.sumOf { parseAmount(it.amount) } }
                .toList()
                .sortedByDescending { it.second }
                .map { (cat, amt) -> PieChartSlice(amt.toFloat(), getCategoryColor(cat), cat) }
        }
    }.value

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
                    modifier = Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { focusManager.clearFocus() },
                    floatingActionButton = {
                        if (currentView == BudgetScreenView.BUDGET_TRACKER && !isViewer) {
                            CustomIconButton(
                                onClick = { expenseToEdit = null; showAddExpenseSheet = true },
                                icon = painterResource(R.drawable.ic_plus),
                                size = ButtonSize.Large,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 24.dp).shadow(16.dp, CircleShape)
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
                                    onEditBudgetClick = { showEditBudgetSheet = true },
                                    onViewSummaryClick = { currentView = BudgetScreenView.EXPENSE_SUMMARY },
                                    onFilterClick = { showBottomSheet = true },
                                    onDeleteExpenseClick = { expenseToDelete = it },
                                    onModifyExpenseClick = { expenseToEdit = it; showAddExpenseSheet = true },
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
                                    processedCategories = allExpenses.groupBy { it.category }.mapValues { (_, items) -> items.sumOf { parseAmount(it.amount) } }.toList().sortedByDescending { it.second },
                                    getCategoryColor = getCategoryColor,
                                    isViewer = isViewer,
                                    onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                    onAddExpenseClick = { expenseToEdit = null; showAddExpenseSheet = true },
                                    onAiOverviewClick = { },
                                    formatAmount = { formatter.format(it.toLong()) }
                                )
                                BudgetScreenView.EXPENSE_CATEGORY -> ExpenseCategoryContent(
                                    categorySearchQuery = categorySearchQuery,
                                    onCategorySearchQueryChange = { categorySearchQuery = it },
                                    filteredCategorySummary = filteredCategorySummary,
                                    isViewer = isViewer,
                                    onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                    onCategoryClick = { selectedCategoryForDetails = it; currentView = BudgetScreenView.CATEGORY_DETAIL },
                                    onCategoryMenuClick = { selectedCategoryForMenu = it; showCategoryMenuBottomSheet = true },
                                    onViewSummaryClick = { currentView = BudgetScreenView.EXPENSE_SUMMARY },
                                    onAddCategoryClick = { categoryToRename = null; showAddCustomCategorySheet = true }
                                )
                                BudgetScreenView.CATEGORY_DETAIL -> {
                                    val catName = selectedCategoryForDetails ?: "Category"
                                    val catExpenses = allExpenses.filter { it.category == catName }
                                    val catTotal = catExpenses.sumOf { parseAmount(it.amount) }
                                    val sortedCatExpenses = catExpenses.sortedWith { a, b ->
                                        val amtA = parseAmount(a.amount); val amtB = parseAmount(b.amount)
                                        var res = when {
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
                                        onRenameCategoryClick = { categoryToRename = catName; showAddCustomCategorySheet = true },
                                        onDeleteExpenseClick = { expenseToDelete = it },
                                        onModifyExpenseClick = { expenseToEdit = it; showAddExpenseSheet = true }
                                    )
                                }
                                BudgetScreenView.MANAGE_ROOM_ACCESS -> BudgetRoomContent(
                                    eventId = activeEventId ?: "",
                                    roomUsers = roomUsers,
                                    currentUserUid = currentUserUid,
                                    currentUserRole = currentUserRole,
                                    searchResults = searchResults,
                                    roomViewModel = roomViewModel,
                                    onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                                    onMenuClick = { focusManager.clearFocus(); showRoomMenuBottomSheet = true },
                                    onRemoveClick = { userToRemove = it },
                                    onLeaveClick = { showLeaveConfirmation = true },
                                    onToastShow = { toastData = it }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = toastData.message != null && !isAnyBottomSheetOpen,
                        enter = slideInVertically(initialOffsetY = { -it - 500 }),
                        exit = slideOutVertically(targetOffsetY = { -it - 500 }),
                        modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().fillMaxWidth().zIndex(99f).padding(horizontal = 12.dp, vertical = 16.dp)
                    ) {
                        CustomToast(message = toastData.message ?: "", type = toastData.type)
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().zIndex(100f)) {
            if (showBottomSheet) {
                SortFilterBottomSheet(
                    sortOptions = sortOptions,
                    initialSortOption = selectedSortOption,
                    filterByOptions = filterOptions,
                    initialFilterOptions = selectedFilterOptions,
                    onDismiss = { showBottomSheet = false },
                    onApply = { sort, filters -> selectedSortOption = sort; selectedFilterOptions = filters; showBottomSheet = false },
                    onProgress = { sheetMotionProgress = it }
                )
            }

            if (showAddExpenseSheet) {
                AddExpenseBottomSheet(
                    onDismiss = { showAddExpenseSheet = false; expenseToEdit = null },
                    onSave = { amount, receiver, category, emoji, phone, notes ->
                        val editingItem = expenseToEdit
                        if (editingItem != null) {
                            viewModel.updateExpense(editingItem.id, receiver.ifBlank { "Unnamed Receiver" }, category.ifBlank { "Misc" }, amount.toDouble(), emoji.ifBlank { "💸" }, currentUserName, phone, notes)
                            toastData = ToastData("Expense Updated!", ToastType.SUCCESS)
                        } else {
                            viewModel.addExpense(receiver.ifBlank { "Unnamed Receiver" }, category.ifBlank { "Misc" }, amount.toDouble(), emoji.ifBlank { "💸" }, currentUserName, phone, notes)
                            toastData = ToastData("Expense Added!", ToastType.SUCCESS)
                        }
                        showAddExpenseSheet = false; expenseToEdit = null
                    },
                    categories = defaultCategories,
                    onAddCategory = { if (!defaultCategories.contains(it)) defaultCategories = defaultCategories + it },
                    initialAmount = expenseToEdit?.amount?.replace("₹", "")?.replace(",", "") ?: "",
                    initialReceiver = expenseToEdit?.title ?: "",
                    initialCategory = expenseToEdit?.category ?: "",
                    initialEmoji = expenseToEdit?.emoji ?: "",
                    initialPhoneNumber = expenseToEdit?.phoneNumber ?: "",
                    initialNote = expenseToEdit?.note ?: "",
                    onProgress = { sheetMotionProgress = it }
                )
            }

            if (showAddCustomCategorySheet) {
                AddCustomCategoryBottomSheet(
                    onDismiss = { showAddCustomCategorySheet = false; categoryToRename = null },
                    onAddCategory = { inputName ->
                        val originalName = categoryToRename
                        if (originalName != null) {
                            if (originalName != inputName) {
                                if (defaultCategories.contains(originalName)) defaultCategories = defaultCategories.map { if (it == originalName) inputName else it }
                                else if (!defaultCategories.contains(inputName)) defaultCategories = defaultCategories + inputName
                                viewModel.renameCategory(originalName, inputName)
                                if (selectedCategoryForDetails == originalName) selectedCategoryForDetails = inputName
                            }
                        } else if (!defaultCategories.contains(inputName)) {
                            defaultCategories = defaultCategories + inputName
                        }
                        showAddCustomCategorySheet = false; categoryToRename = null
                    },
                    initialCategoryName = categoryToRename ?: "",
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
                onConfirm = { expenseToDelete?.id?.let { viewModel.deleteExpense(it) }; expenseToDelete = null },
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
            MenuBottomSheet(
                items = listOfNotNull(
                    if (isOwner) listOf(
                        MenuSheetActionItem(
                            text = if (isBudgetNotSet) "Add Budget" else "Edit Budget",
                            icon = if (isBudgetNotSet) painterResource(R.drawable.ic_plus) else painterResource(R.drawable.ic_edit),
                            onClick = { showMenuBottomSheet = false; showEditBudgetSheet = true }
                        )
                    ) else null,
                    listOf(
                        MenuSheetActionItem(
                            text = if (isOwner) "Manage Room Access" else "Room Members",
                            icon = painterResource(R.drawable.ic_user_default),
                            onClick = { showMenuBottomSheet = false; currentView = BudgetScreenView.MANAGE_ROOM_ACCESS }
                        )
                    ),
                    listOf(
                        MenuSheetActionItem(
                            text = "Expense Categories",
                            icon = painterResource(R.drawable.ic_category),
                            onClick = { showMenuBottomSheet = false; currentView = BudgetScreenView.EXPENSE_CATEGORY }
                        )
                    )
                ),
                onCancelClick = { showMenuBottomSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showCategoryMenuBottomSheet) {
            MenuBottomSheet(
                items = listOfNotNull(
                    listOfNotNull(
                        MenuSheetActionItem(
                            text = "View Expenses",
                            icon = painterResource(R.drawable.ic_pie_chart),
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
                                icon = painterResource(R.drawable.ic_edit),
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
                                icon = painterResource(R.drawable.ic_delete),
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
                onCancelClick = { showCategoryMenuBottomSheet = false; selectedCategoryForMenu = null },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (categoryToDeleteConfirm != null) {
            ConfirmationBottomSheet(
                heading = "Are you sure?",
                subHeading = "The category will be deleted permanently.",
                confirmButtonText = "Delete Category",
                onDismiss = { categoryToDeleteConfirm = null },
                onConfirm = { val cat = categoryToDeleteConfirm; if (cat != null) { viewModel.deleteExpensesByCategory(cat); defaultCategories = defaultCategories.filter { it != cat } }; categoryToDeleteConfirm = null },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showRoomMenuBottomSheet) {
            MenuBottomSheet(
                items = listOf(
                    listOf(
                        MenuSheetActionItem(
                            text = "Leave Room",
                            icon = painterResource(R.drawable.ic_logout),
                            contentColor = MaterialTheme.colorScheme.error,
                            onClick = { showRoomMenuBottomSheet = false; showLeaveConfirmation = true }
                        )
                    )
                ),
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
                onConfirm = { activeEvent?.id?.let { id -> roomViewModel.removeAccess(id, "Budget", user.uid); toastData = ToastData("${user.name} removed from room", ToastType.SUCCESS) }; userToRemove = null },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showLeaveConfirmation) {
            ConfirmationBottomSheet(
                heading = "Leaving Budget Room?",
                subHeading = "You will lose access to this room and won't be able to see updates.",
                confirmButtonText = "Leave",
                onDismiss = { showLeaveConfirmation = false },
                onConfirm = { activeEvent?.id?.let { id -> roomViewModel.removeAccess(id, "Budget", currentUserUid) }; toastData = ToastData("You left the room", ToastType.DEFAULT); currentView = BudgetScreenView.BUDGET_TRACKER; showLeaveConfirmation = false },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}
