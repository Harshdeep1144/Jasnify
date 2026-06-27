package com.harshdeep.jasnify.presentation.screens.budget

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddExpenseBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomDeleteSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.EditBudgetBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
import com.harshdeep.jasnify.presentation.components.others.CustomPieChart
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.PieChartSlice
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import java.text.NumberFormat
import java.util.Locale


// Enumeration to manage state transitions within the budget flows
enum class BudgetScreenView {
    BUDGET_TRACKER,
    EXPENSE_SUMMARY
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onBackClick: () -> Unit,
) {
    // Top-level layout view state switcher
    var currentView by remember { mutableStateOf(BudgetScreenView.BUDGET_TRACKER) }

    var searchQuery by remember { mutableStateOf("") }
    var expandedCardId by remember { mutableStateOf<String?>("0") }
    var isSearchBarFocused by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }

    var showAddExpenseSheet by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<ExpenseItem?>(null) }
    val addExpenseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var expenseToDelete by remember { mutableStateOf<ExpenseItem?>(null) }
    val deleteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Dynamic Edit Budget Sheet state integrations
    var showEditBudgetSheet by remember { mutableStateOf(false) }
    var budgetValue by remember { mutableStateOf("INR10000000") } // Default Budget to 1 Crore (10,000,000)
    val editBudgetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sortOptions = remember {
        listOf(
            "Newest First",
            "Oldest First",
            "Highest Amount",
            "Lowest Amount"
        )
    }

    val filterOptions = remember {
        listOf(
            "Vendors",
            "Catering",
            "Beauty",
            "Stationery",
            "Apparel",
            "Beverages",
            "Transport",
            "Equipment Rentals"
        )
    }

    var selectedSortOption by remember { mutableStateOf("Newest First") }
    var selectedFilterOptions by remember { mutableStateOf(emptySet<String>()) }

    var allExpenses by remember {
        mutableStateOf(
            listOf(
                ExpenseItem(
                    id = "1",
                    title = "The Divine Frames",
                    category = "Vendors",
                    amount = "₹26,10,660",
                    emoji = "📸",
                    lastUpdatedBy = "Anand K.",
                    lastUpdatedDate = "Aug 24, 2025, 01:04pm"
                ),
                ExpenseItem(
                    id = "2",
                    title = "Varun Catering",
                    category = "Catering",
                    amount = "₹12,45,000",
                    emoji = "🍔"
                ),
                ExpenseItem(
                    id = "3",
                    title = "GenX Entertainment",
                    category = "Vendors",
                    amount = "₹45,000",
                    emoji = "🥂"
                ),
                ExpenseItem(
                    id = "6",
                    title = "Shine & Glow Makeup Studio",
                    category = "Beauty",
                    amount = "₹15,000",
                    emoji = "💄"
                ),
                ExpenseItem(
                    id = "7",
                    title = "Elite Invitations & Prints",
                    category = "Stationery",
                    amount = "₹12,500",
                    emoji = "✉️"
                ),
                ExpenseItem(
                    id = "8",
                    title = "Classic Gowns & Tuxedos",
                    category = "Apparel",
                    amount = "₹85,000",
                    emoji = "👔"
                ),
                ExpenseItem(
                    id = "9",
                    title = "Signature Mocktail Bar",
                    category = "Beverages",
                    amount = "₹18,000",
                    emoji = "🍸"
                ),
                ExpenseItem(
                    id = "10",
                    title = "Prasad Travels",
                    category = "Transport",
                    amount = "₹2,52,600",
                    emoji = "🚗",
                    lastUpdatedBy = "Meera J.",
                    lastUpdatedDate = "Aug 26, 2025, 04:30pm"
                ),
                ExpenseItem(
                    id = "11",
                    title = "Vikas Equipment Rentals",
                    category = "Equipment Rentals",
                    amount = "₹4,79,990",
                    emoji = "⚙️",
                    lastUpdatedBy = "Harsh Deep",
                    lastUpdatedDate = "Aug 27, 2025, 11:15am"
                )
            )
        )
    }

    var defaultCategories by remember {
        mutableStateOf(
            listOf(
                "Venue", "Catering", "Gifts", "Staff & Crew",
                "Costumes", "Vendors", "Transportation",
                "Entertainment", "Equipment Rentals"
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

    // Convert dynamic total expenses into a clean adaptive display value (Cr, L, K, or raw units)
    val centerTextPrimaryValue = remember(totalSpent) {
        when {
            totalSpent >= 10000000.0 -> { // >= 1 Crore (100 Lakhs)
                val spentInCrores = totalSpent / 10000000.0
                "₹ ${String.format(Locale.ENGLISH, "%.1f", spentInCrores)} Cr"
            }
            totalSpent >= 100000.0 -> { // >= 1 Lakh (100 Thousand)
                val spentInLakhs = totalSpent / 100000.0
                "₹ ${String.format(Locale.ENGLISH, "%.1f", spentInLakhs)} L"
            }
            totalSpent >= 1000.0 -> { // >= 1 Thousand
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

    // Design Tokens & Color mappings matching your high-fidelity screenshot
    val categoryColors = remember {
        mapOf(
            "Vendors" to Color(0xFF1D5590),
            "Catering" to Color(0xFFFF1E56),
            "Equipment Rentals" to Color(0xFFE56B8F),
            "Transport" to Color(0xFF0FAD48),
            "Transportation" to Color(0xFF0FAD48),
            "Beauty" to Color(0xFF2FA4C4),
            "Stationery" to Color(0xFF8D16FF),
            "Apparel" to Color(0xFFFFB020),
            "Beverages" to Color(0xFF00C9A7)
        )
    }

    // Resolving dynamic category colors exclusively from the categoryColors map
    val getCategoryColor = remember(categoryColors) {
        { categoryName: String ->
            categoryColors[categoryName] ?: run {
                val colorValues = categoryColors.values.toList()
                val index = kotlin.math.abs(categoryName.hashCode()) % colorValues.size
                colorValues[index]
            }
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
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .background(SurfacePrimary)
                    .statusBarsPadding()
            ) {
                if (currentView == BudgetScreenView.BUDGET_TRACKER) {
                    CustomTopBar(
                        title = "Budget Tracker",
                        onBackClick = { onBackClick() },
                        onMenuClick = { },
                        isLargeTitle = true
                    )
                } else {
                    CustomTopBar(
                        title = "Expense Summary",
                        onBackClick = { currentView = BudgetScreenView.EXPENSE_SUMMARY },
                    )
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
                    .background(SurfaceSecondary),
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

        // Expanded Expense Summary View
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
                                    text = "Remaining Funds",
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
                                text = formattedRemaining,
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

                    // Action CTAs (AI Overview & Add Expense Row matches perfect alignment)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextButton(
                            onClick = { },
                            text = "AI Overview",
                            leadingIcon = painterResource(R.drawable.ic_ai),
                            shapeStyle = ButtonShapeStyle.Square,
                            type = ButtonType.Secondary,
                            modifier = Modifier.weight(1f)
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
    }

    if (showBottomSheet) {
        SortFilterBottomSheet(
            sheetState = sheetState,
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
                                lastUpdatedBy = "Harsh Deep",
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
                        lastUpdatedBy = "Harsh Deep",
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

    // Modal Sheet integration for Delete confirmation flow
    if (expenseToDelete != null) {
        CustomDeleteSheet(
            heading = "Are you sure?",
            subHeading = "The expense amount will be added back to the total budget.",
            sheetState = deleteSheetState,
            confirmButtonText = "Delete Expense",
            onDismiss = {
                coroutineScope.launch {
                    deleteSheetState.hide()
                }.invokeOnCompletion {
                    expenseToDelete = null
                }
            },
            onConfirmRemove = {
                val currentExpenseId = expenseToDelete?.id
                if (currentExpenseId != null) {
                    allExpenses = allExpenses.filter { it.id != currentExpenseId }
                }
                coroutineScope.launch {
                    deleteSheetState.hide()
                }.invokeOnCompletion {
                    expenseToDelete = null
                }
            }
        )
    }

    // Modal Sheet integration for Edit Budget Flow
    if (showEditBudgetSheet) {
        EditBudgetBottomSheet(
            initialBudgetValue = budgetValue,
            sheetState = editBudgetSheetState,
            onDismiss = {
                coroutineScope.launch {
                    editBudgetSheetState.hide()
                }.invokeOnCompletion {
                    showEditBudgetSheet = false
                }
            },
            onUpdateBudget = { updatedValue ->
                budgetValue = updatedValue
                coroutineScope.launch {
                    editBudgetSheetState.hide()
                }.invokeOnCompletion {
                    showEditBudgetSheet = false
                }
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