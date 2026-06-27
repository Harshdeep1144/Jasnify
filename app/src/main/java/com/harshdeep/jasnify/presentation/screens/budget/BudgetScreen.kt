package com.harshdeep.jasnify.presentation.screens.budget

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddCustomCategoryBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddExpenseBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomDeleteSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.EditBudgetBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CategoryCard
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
import com.harshdeep.jasnify.presentation.components.others.CustomPieChart
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.PieChartSlice
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.ContentTertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import java.text.NumberFormat
import java.util.Locale

enum class BudgetScreenView {
    BUDGET_TRACKER,
    EXPENSE_SUMMARY,
    EXPENSE_CATEGORY,
    CATEGORY_DETAIL
}

data class ExpenseItem(
    val id: String,
    val title: String,
    val category: String,
    val amount: String,
    val emoji: String = "💸",
    val lastUpdatedBy: String? = null,
    val lastUpdatedDate: String? = null
)

// Helper structure to hold dynamically computed category values
data class CategorySummaryData(
    val name: String,
    val amountFormatted: String,
    val amountRaw: Double,
    val emojis: List<String>,
    val totalCount: Int
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onBackClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var currentView by remember { mutableStateOf(BudgetScreenView.BUDGET_TRACKER) }

    // SYSTEM BACK BUTTON HANDLER
    // It steps backward logically matching BudgetScreenView navigation flow.
    BackHandler(enabled = currentView != BudgetScreenView.BUDGET_TRACKER) {
        currentView = when (currentView) {
            BudgetScreenView.EXPENSE_SUMMARY -> BudgetScreenView.BUDGET_TRACKER
            BudgetScreenView.EXPENSE_CATEGORY -> BudgetScreenView.BUDGET_TRACKER
            BudgetScreenView.CATEGORY_DETAIL -> BudgetScreenView.EXPENSE_CATEGORY
            BudgetScreenView.BUDGET_TRACKER -> BudgetScreenView.BUDGET_TRACKER
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var categorySearchQuery by remember { mutableStateOf("") } // Separate state for categories
    var expandedCardId by remember { mutableStateOf<String?>("0") }
    var isSearchBarFocused by remember { mutableStateOf(false) }

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
    var budgetValue by remember { mutableStateOf("INR10000000") } // Default Budget to 10,000,000
    val editBudgetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showMenuBottomSheet by remember { mutableStateOf(false) }

    // States added to support dynamically launching a menu specific to a chosen Category Card
    var showCategoryMenuBottomSheet by remember { mutableStateOf(false) }
    var selectedCategoryForMenu by remember { mutableStateOf<String?>(null) }

    // State holding selected category for dedicated detail card views
    var selectedCategoryForDetails by remember { mutableStateOf<String?>(null) }
    var selectedCategoryChips by remember { mutableStateOf(setOf("Recent First")) }

    // State to hold the category queued for deletion confirmation
    var categoryToDeleteConfirm by remember { mutableStateOf<String?>(null) }

    // Category Screen Add / Rename Bottom Sheet integration
    var showAddCustomCategorySheet by remember { mutableStateOf(false) }
    var categoryToRename by remember { mutableStateOf<String?>(null) }
    val addCustomCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sortOptions = remember { listOf("Newest First", "Oldest First", "Highest Amount", "Lowest Amount") }
    val filterOptions = remember { listOf("Vendors", "Catering", "Beauty", "Stationery", "Apparel", "Beverages", "Transport", "Equipment Rentals") }

    var selectedSortOption by remember { mutableStateOf("Newest First") }
    var selectedFilterOptions by remember { mutableStateOf(emptySet<String>()) }

    var allExpenses by remember {
        mutableStateOf(
            listOf(
                ExpenseItem(id = "1", title = "The Divine Frames", category = "Vendors", amount = "₹26,10,660", emoji = "📸", lastUpdatedBy = "Anand K.", lastUpdatedDate = "Aug 24, 2025, 01:04pm"),
                ExpenseItem(id = "2", title = "Varun Catering", category = "Catering", amount = "₹12,45,000", emoji = "🍔"),
                ExpenseItem(id = "3", title = "GenX Entertainment", category = "Vendors", amount = "₹45,000", emoji = "🥂"),
                ExpenseItem(id = "4", title = "Nupur Makeup Artist", category = "Vendors", amount = "₹35,000", emoji = "🧑"),
                ExpenseItem(id = "5", title = "Decor Elements", category = "Vendors", amount = "₹1,50,000", emoji = "🎈"),
                ExpenseItem(id = "6", title = "Shine & Glow Makeup Studio", category = "Beauty", amount = "₹15,000", emoji = "💄"),
                ExpenseItem(id = "7", title = "Elite Invitations & Prints", category = "Stationery", amount = "₹12,500", emoji = "✉️"),
                ExpenseItem(id = "8", title = "Classic Gowns & Tuxedos", category = "Apparel", amount = "₹85,000", emoji = "👔"),
                ExpenseItem(id = "9", title = "Signature Mocktail Bar", category = "Beverages", amount = "₹18,000", emoji = "🍸"),
                ExpenseItem(id = "10", title = "Uber", category = "Transportation", amount = "₹68,000", emoji = "🚗", lastUpdatedBy = "Meera J.", lastUpdatedDate = "Aug 26, 2025, 04:30pm"),
                ExpenseItem(id = "11", title = "Happy Travels", category = "Transportation", amount = "₹1,84,600", emoji = "🚌"),
                ExpenseItem(id = "12", title = "Vikas Equipment Rentals", category = "Equipment Rentals", amount = "₹4,79,990", emoji = "⚙️", lastUpdatedBy = "Harsh Deep", lastUpdatedDate = "Aug 27, 2025, 11:15am"),
                ExpenseItem(id = "13", title = "Audio Stage Setup", category = "Equipment Rentals", amount = "₹1,20,000", emoji = "🔊"),
                ExpenseItem(id = "14", title = "LED Display Walls", category = "Equipment Rentals", amount = "₹2,40,000", emoji = "📺"),
                ExpenseItem(id = "15", title = "Main Cook & Chef", category = "Staff & Crew", amount = "₹25,000", emoji = "🧑‍🍳"),
                ExpenseItem(id = "16", title = "Event Coordinators", category = "Staff & Crew", amount = "₹13,000", emoji = "🙋‍♂️"),
                ExpenseItem(id = "17", title = "Sundry Unplanned", category = "Unplanned Costs", amount = "₹24,650", emoji = "💳")
            )
        )
    }

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

    // Dynamic calculations based on live budgetValue state
    val totalBudget = remember(budgetValue) {
        val numericPart = budgetValue.dropWhile { !it.isDigit() }
        numericPart.toDoubleOrNull() ?: 10000000.0 // Fallback to 1Cr
    }

    val totalSpent = remember(allExpenses) {
        allExpenses.sumOf { parseAmount(it.amount) }
    }

    val remainingFunds = (totalBudget - totalSpent).coerceAtLeast(0.0)
    val remainingPercentage = if (totalBudget > 0) (remainingFunds / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    val spentPercentage = if (totalBudget > 0) (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f) else 0f

    val indianLocale = Locale("en", "IN")
    val formatter = NumberFormat.getNumberInstance(indianLocale)
    val formattedRemaining = "₹${formatter.format(remainingFunds.toLong())}"
    val formattedTotalSpent = "₹${formatter.format(totalSpent.toLong())}"
    val formattedTotalBudget = formatter.format(totalBudget.toLong())

    // Convert dynamic total expenses into (Cr, L, K, or raw units)
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
            else -> list.sortedByDescending { it.id.toIntOrNull() ?: 0 } // "Newest First" (Default)
        }
    }

    // Dynamic extraction & grouping of Categories to feed Category view
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

    // Filtered categories for the search logic on Category Screen
    val filteredCategorySummary = computedCategories.filter {
        it.name.contains(categorySearchQuery, ignoreCase = true)
    }

    // ----------------- DYNAMIC COLOR MAPPINGS -----------------
    val colorPalette = remember {
        listOf(
            Color(0xFF1D5590), // Deep Blue
            Color(0xFFFF1E56), // Crimson Red
            Color(0xFFE56B8F), // Pink Rose
            Color(0xFF0FAD48), // Emerald Green
            Color(0xFF2FA4C4), // Ocean Teal
            Color(0xFF8D16FF), // Neon Purple
            Color(0xFFFFB020), // Honey Yellow
            Color(0xFF00C9A7), // Mint Green
            Color(0xFF6C5B7B), // Slate Violet
            Color(0xFF355C7D), // Classic Indigo
            Color(0xFFF67280), // Pastel Coral
            Color(0xFFC06C84), // Crimson Grey
            Color(0xFFFF8C94), // Soft Pink Rose
            Color(0xFF45B6FE), // Electric Sky Blue
            Color(0xFF50B498), // Sage Eucalyptus
            Color(0xFF9B59B6), // Radiant Amethyst
            Color(0xFFE67E22), // Pumpkin Orange
            Color(0xFF16A085)  // Cool Pine Green
        )
    }

    // Extract all unique categories dynamically from both database lists to ensure proper mapping
    val uniqueCategories = remember(allExpenses, defaultCategories) {
        (allExpenses.map { it.category } + defaultCategories).distinct()
    }

    // Map each unique category dynamically with color
    val categoryColors = remember(uniqueCategories, colorPalette) {
        uniqueCategories.mapIndexed { index, category ->
            category to colorPalette[index % colorPalette.size]
        }.toMap()
    }

    // Dynamic resolution function keeping colors stable throughout the compose session lifecycle
    val getCategoryColor = remember(categoryColors) {
        { categoryName: String ->
            categoryColors[categoryName] ?: ContentSecondary // Safe fallback grey
        }
    }

    // Dynamic compilation of Pie Chart Slices matching current database state
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
        topBar = {
            val topBarBg = if(currentView == BudgetScreenView.EXPENSE_CATEGORY || currentView == BudgetScreenView.CATEGORY_DETAIL) SurfaceSecondary else SurfacePrimary
            Column(
                modifier = Modifier
                    .background(topBarBg)
                    .statusBarsPadding()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    }
            ) {
                when (currentView) {
                    BudgetScreenView.BUDGET_TRACKER -> {
                        CustomTopBar(
                            title = "Budget Tracker",
                            onBackClick = { onBackClick() },
                            onMenuClick = { showMenuBottomSheet = true },
                            isLargeTitle = true
                        )
                    }
                    BudgetScreenView.EXPENSE_SUMMARY -> {
                        CustomTopBar(
                            title = "Expense Summary",
                            onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                        )
                    }
                    BudgetScreenView.EXPENSE_CATEGORY -> {
                        CustomTopBar(
                            title = "Expense Category",
                            onBackClick = { currentView = BudgetScreenView.BUDGET_TRACKER },
                            buttonStyle = ButtonBackground.TRANSLUCENT,
                            translucentAlpha = 0.5f
                        )
                    }
                    BudgetScreenView.CATEGORY_DETAIL -> {
                        CustomTopBar(
                            onBackClick = { currentView = BudgetScreenView.EXPENSE_CATEGORY },
                            buttonStyle = ButtonBackground.TRANSLUCENT,
                            translucentAlpha = 0.5f
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentView == BudgetScreenView.BUDGET_TRACKER) {
                CustomIconButton(
                    onClick = {
                        expenseToEdit = null
                        showAddExpenseSheet = true
                    },
                    icon = painterResource(R.drawable.ic_plus),
                    size = ButtonSize.Large,
                    modifier = Modifier.shadow(16.dp, CircleShape)
                )
            }
        },
    ) { paddingValues ->


        // ============================================================================================================================================
        // SCREEN 1: BUDGET_TRACKER
        // ============================================================================================================================================


        AnimatedVisibility(
            visible = (currentView == BudgetScreenView.BUDGET_TRACKER),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(SurfaceSecondary)
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SquircleShape(CornerExtraLarge, CornerSmoothingDefault))
                                .background(SurfaceBrandSecondary)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                    SquircleShape(CornerExtraLarge, CornerSmoothingDefault)
                                )
                        ) {
                            Image(
                                painter = painterResource(R.drawable.bg_budget_pattern),
                                contentDescription = null,
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop,
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = "TOTAL BUDGET",
                                            style = JasnifyTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                            color = ContentSecondary,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = formattedTotalBudget,
                                            style = JasnifyTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                                            color = ContentPrimary
                                        )
                                    }

                                    TopBarIconButton(
                                        icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_edit)),
                                        onClick = { showEditBudgetSheet = true },
                                        backgroundStyle = ButtonBackground.TRANSPARENT,
                                        iconSize = 20.dp,
                                    )
                                }

                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(0.16f)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "REMAINING FUNDS",
                                        style = JasnifyTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                        color = ContentSecondary,
                                        letterSpacing = 1.sp
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = formattedRemaining,
                                            style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                                            color = ContentPrimary
                                        )
                                        Icon(
                                            painter = painterResource(R.drawable.ic_info),
                                            contentDescription = "Remaining Funds Info",
                                            tint = ContentPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    LinearProgressIndicator(
                                        progress = { remainingPercentage },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(CircleShape),
                                        color = ContentBrand,
                                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                    )
                                }

                                CustomTextButton(
                                    onClick = { currentView = BudgetScreenView.EXPENSE_SUMMARY },
                                    text = "View Summary",
                                    size = ButtonSize.Medium,
                                    type = ButtonType.Primary,
                                    shapeStyle = ButtonShapeStyle.Square,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
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
                                                listState.animateScrollToItem(index = 1, scrollOffset = -8)
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

                item {
                    Spacer(modifier = Modifier.height(124.dp))
                }
            }
        }



        // ============================================================================================================================================
        // SCREEN 2: EXPENSE_SUMMARY (Visual Breakdown & Pie Slices)
        // ============================================================================================================================================



        AnimatedVisibility(
            visible = (currentView == BudgetScreenView.EXPENSE_SUMMARY),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val scrollState = rememberScrollState()
            var isCategoryListExpanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(SurfacePrimary)
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
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Center Pie Chart Segment
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
                            Row{
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

                    // Total Spent Breakdown Card
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
                            Row{
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

                        // Compile and sort categories dynamically based on expenditure
                        val processedCategories = remember(allExpenses) {
                            allExpenses.groupBy { it.category }
                                .mapValues { (_, items) -> items.sumOf { parseAmount(it.amount) } }
                                .toList()
                                .sortedByDescending { it.second }
                        }

                        // Determine active list bounds based on visual Expand option state
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
                                        // Colored Rounded Category Tag Indicator
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

                        // Toggle Dropdown Button ("View all")
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

                    // AI Overview & Back to Categories Row matches perfect alignment
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextButton(
                            onClick = {  },
                            text = "AI Overview",
                            shapeStyle = ButtonShapeStyle.Square,
                            type = ButtonType.Secondary,
                            modifier = Modifier.weight(1f),
                            leadingIcon = painterResource(R.drawable.ic_ai)
                        )

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


        // ============================================================================================================================================
        // SCREEN 3: EXPENSE_CATEGORY
        // ============================================================================================================================================


        AnimatedVisibility(
            visible = (currentView == BudgetScreenView.EXPENSE_CATEGORY),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(SurfaceSecondary)
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
                    // Search bar section
                    CustomSearchBar(
                        value = categorySearchQuery,
                        placeholder = "Search",
                        onValueChange = { categorySearchQuery = it },
                        backgroundColor = SurfacePrimary,
                        modifier = Modifier.fillMaxWidth()
                            .padding(12.dp)
                    )

                    // Dynamically compiled list of category summaries
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
                                        // Binds selected category context and triggers bottom sheet visibility
                                        selectedCategoryForMenu = categoryItem.name
                                        showCategoryMenuBottomSheet = true
                                    }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }

                    // Bottom Navigation Button Sticky Row
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
                            modifier = Modifier
                                .weight(1f)
                        )

                        CustomTextButton(
                            onClick = {
                                // Clear rename queue, and open the category-only bottom sheet directly
                                categoryToRename = null
                                showAddCustomCategorySheet = true
                            },
                            text = "Add Category",
                            type = ButtonType.Primary,
                            shapeStyle = ButtonShapeStyle.Square,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }
        }


        // ============================================================================================================================================
        // SCREEN 4: CATEGORY_DETAIL
        // ============================================================================================================================================


        AnimatedVisibility(
            visible = (currentView == BudgetScreenView.CATEGORY_DETAIL),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val selectedCategoryName = selectedCategoryForDetails ?: "Category"
            val selectedCategoryExpenses = remember(allExpenses, selectedCategoryForDetails) {
                allExpenses.filter { it.category == selectedCategoryName }
            }
            val selectedCategoryTotal = remember(selectedCategoryExpenses) {
                selectedCategoryExpenses.sumOf { parseAmount(it.amount) }
            }
            val formattedCategoryTotal = "₹${formatter.format(selectedCategoryTotal.toLong())}"

            // Multi-sorted implementation chaining comparator criteria depending on active selected set
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
                            result = amtB.compareTo(amtA) // Amount descending
                        } else if (hasLeastExpensive && !hasMostExpensive) {
                            result = amtA.compareTo(amtB) // Amount ascending
                        } else if (hasMostExpensive && hasLeastExpensive) {
                            // Stable sorting fallback if both contradictory toggles remain active
                            result = amtB.compareTo(amtA)
                        }

                        if (result == 0) {
                            result = idB.compareTo(idA) // Secondary tie-breaker sort using newest IDs
                        }
                        result
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(SurfaceSecondary)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
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
                            Box(
                                modifier = Modifier
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ){
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
                        Row{
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
                        modifier = Modifier
                            .fillMaxWidth(),
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

                // Category Expense Cards / Empty lists
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
        val editingItem = expenseToEdit
        val initialAmountRaw = editingItem?.amount?.replace("₹", "")?.replace(",", "") ?: ""

        AddExpenseBottomSheet(
            sheetState = addExpenseSheetState,
            initialAmount = initialAmountRaw,
            initialReceiver = editingItem?.title ?: "",
            initialCategory = editingItem?.category ?: "",
            initialEmoji = editingItem?.emoji ?: "",
            onDismiss = {
                showAddExpenseSheet = false
                expenseToEdit = null
            },
            onSave = { amount, receiver, category, emoji ->
                val formattedAmount = "₹${formatter.format(amount)}"

                if (editingItem != null) {
                    // Modify existing expense in-place
                    allExpenses = allExpenses.map {
                        if (it.id == editingItem.id) {
                            it.copy(
                                title = receiver.ifBlank { "Unnamed Receiver" },
                                category = category.ifBlank { "Misc" },
                                amount = formattedAmount,
                                emoji = emoji.ifBlank { "💸" },
                                lastUpdatedBy = "Anonymous",
                                lastUpdatedDate = "Just now"
                            )
                        } else {
                            it
                        }
                    }
                } else {
                    // Create and prepend a completely new item
                    val nextUniqueId = ((allExpenses.maxOfOrNull { it.id.toIntOrNull() ?: 0 } ?: 0) + 1).toString()
                    val newItem = ExpenseItem(
                        id = nextUniqueId,
                        title = receiver.ifBlank { "Unnamed Receiver" },
                        category = category.ifBlank { "Misc" },
                        amount = formattedAmount,
                        emoji = emoji.ifBlank { "💸" },
                        lastUpdatedBy = "Anonymous",
                        lastUpdatedDate = "Just now"
                    )
                    allExpenses = listOf(newItem) + allExpenses
                }
                showAddExpenseSheet = false
                expenseToEdit = null
            },
            categories = defaultCategories,
            onAddCategory = { newCategory ->
                if (!defaultCategories.contains(newCategory)) {
                    defaultCategories = defaultCategories + newCategory
                }
            }
        )
    }

    // Dynamic Category Add / Rename Screen Level Sheet Setup
    if (showAddCustomCategorySheet) {
        AddCustomCategoryBottomSheet(
            sheetState = addCustomCategorySheetState,
            initialCategoryName = categoryToRename ?: "",
            heading = if (categoryToRename != null) "Rename category" else "Add custom category",
            onDismiss = {
                showAddCustomCategorySheet = false
                categoryToRename = null
            },
            onAddCategory = { inputName ->
                if (inputName.isBlank()) {
                    Toast.makeText(context, "Please enter a category name first!", Toast.LENGTH_SHORT).show()
                } else {
                    val originalName = categoryToRename
                    if (originalName != null) {
                        // RENAME ACTION FLOW
                        if (originalName != inputName) {
                            // Update our core template lists
                            if (defaultCategories.contains(originalName)) {
                                defaultCategories = defaultCategories.map { if (it == originalName) inputName else it }
                            } else if (!defaultCategories.contains(inputName)) {
                                defaultCategories = defaultCategories + inputName
                            }

                            // Ripple category rename changes across all matching physical expenses
                            allExpenses = allExpenses.map { expense ->
                                if (expense.category == originalName) {
                                    expense.copy(category = inputName)
                                } else {
                                    expense
                                }
                            }

                            // Keep Detail Screen active if we are currently inspecting the renamed category
                            if (selectedCategoryForDetails == originalName) {
                                selectedCategoryForDetails = inputName
                            }
                        }
                    } else {
                        // NEW ADD ACTION FLOW
                        if (!defaultCategories.contains(inputName)) {
                            defaultCategories = defaultCategories + inputName
                        }
                    }
                    showAddCustomCategorySheet = false
                    categoryToRename = null
                }
            }
        )
    }

    // Modal Sheet integration for Delete confirmation flow
    if (expenseToDelete != null) {
        CustomDeleteSheet(
            heading = "Are you sure?",
            subHeading = "The expense amount will be added back to the total budget.",
            confirmButtonText = "Delete Expense",
            onDismiss = {
                expenseToDelete = null
            },
            onConfirmRemove = {
                val currentExpenseId = expenseToDelete?.id
                if (currentExpenseId != null) {
                    allExpenses = allExpenses.filter { it.id != currentExpenseId }
                }
                expenseToDelete = null
            }
        )
    }

    // Modal Sheet integration for Edit Budget Flow
    if (showEditBudgetSheet) {
        EditBudgetBottomSheet(
            initialBudgetValue = budgetValue,
            sheetState = editBudgetSheetState,
            onDismiss = {
                showEditBudgetSheet = false
            },
            onUpdateBudget = { updatedValue ->
                budgetValue = updatedValue
                showEditBudgetSheet = false
            }
        )
    }

    // Custom Menu Bottom Sheet
    if (showMenuBottomSheet) {
        MenuBottomSheet(
            items = listOf(
                MenuSheetActionItem(
                    text = "Edit Budget",
                    icon = painterResource(R.drawable.ic_edit),
                    onClick = {
                        showMenuBottomSheet = false
                        showEditBudgetSheet = true
                    }
                ),
                MenuSheetActionItem(
                    text = "Manage Room Access",
                    icon = painterResource(R.drawable.ic_user_default),
                    onClick = {
                        showMenuBottomSheet = false
                        // Handle Manage Room Access action flow here
                    }
                ),
                MenuSheetActionItem(
                    text = "Manage Categories",
                    icon = painterResource(R.drawable.ic_category),
                    onClick = {
                        showMenuBottomSheet = false
                        currentView = BudgetScreenView.EXPENSE_CATEGORY
                    },
                )
            ),
            onCancelClick = {
                showMenuBottomSheet = false
            }
        )
    }

    // Custom Category-specific Menu Bottom Sheet
    if (showCategoryMenuBottomSheet) {
        MenuBottomSheet(
            items = listOf(
                MenuSheetActionItem(
                    text = "View Expenses",
                    icon = painterResource(R.drawable.ic_file),
                    onClick = {
                        showCategoryMenuBottomSheet = false
                        selectedCategoryForDetails = selectedCategoryForMenu
                        currentView = BudgetScreenView.CATEGORY_DETAIL
                    }
                ),
                MenuSheetActionItem(
                    text = "Rename Category",
                    icon = painterResource(R.drawable.ic_edit),
                    onClick = {
                        showCategoryMenuBottomSheet = false
                        categoryToRename = selectedCategoryForMenu
                        showAddCustomCategorySheet = true
                    }
                ),
                MenuSheetActionItem(
                    text = "Delete Category",
                    icon = painterResource(R.drawable.ic_delete),
                    onClick = {
                        categoryToDeleteConfirm = selectedCategoryForMenu
                        showCategoryMenuBottomSheet = false
                        selectedCategoryForMenu = null
                    },
                    contentColor = MaterialTheme.colorScheme.error
                )
            ),
            onCancelClick = {
                showCategoryMenuBottomSheet = false
                selectedCategoryForMenu = null
            }
        )
    }

    // Modal Sheet integration for Category Delete confirmation flow
    if (categoryToDeleteConfirm != null) {
        CustomDeleteSheet(
            heading = "Are you sure?",
            subHeading = "The category will be deleted permanently.",
            confirmButtonText = "Delete Category",
            onDismiss = {
                categoryToDeleteConfirm = null
            },
            onConfirmRemove = {
                val categoryToDelete = categoryToDeleteConfirm
                if (categoryToDelete != null) {
                    // Filter out both physical category templates & all associated transactions
                    allExpenses = allExpenses.filter { it.category != categoryToDelete }
                    defaultCategories = defaultCategories.filter { it != categoryToDelete }
                }
                categoryToDeleteConfirm = null
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