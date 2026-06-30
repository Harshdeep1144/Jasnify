package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import sv.lib.squircleshape.SquircleShape
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale
import kotlinx.coroutines.launch

// ---  DATE PICKER COLUMN ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> DatePickerColumn(
    items: List<T>,
    scrollState: LazyListState,
    itemHeight: Dp,
    visibleItems: Int,
    // Callback to report the item currently centered (real-time)
    onCenteredItemChanged: (T) -> Unit,
    content: @Composable (T) -> Unit
) {
    val halfVisibleItems = visibleItems / 2
    // Ensures smooth snapping behavior
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)

    // List with null spacers for centering the visible items
    val itemsWithSpacers = remember(items) {
        List<T?>(halfVisibleItems) { null } + items + List<T?>(halfVisibleItems) { null }
    }

    // INSTANTLY calculate the index of the item closest to the center using derivedStateOf
    val centeredItemIndex by remember {
        derivedStateOf {
            val layoutInfo = scrollState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isEmpty()) return@derivedStateOf -1

            val viewportCenter = layoutInfo.viewportEndOffset / 2

            val closestItem = layoutInfo.visibleItemsInfo.minByOrNull {
                kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
            }

            (closestItem?.index ?: halfVisibleItems) - halfVisibleItems
        }
    }

    // Determine the actual centered item object
    val centeredItem: T? = remember(centeredItemIndex, items) {
        centeredItemIndex.takeIf { it in items.indices }?.let { items[it] }
    }

    // Report the centered item up instantly
    LaunchedEffect(centeredItem) {
        centeredItem?.let {
            onCenteredItemChanged(it)
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .width(70.dp)
            .height(itemHeight * visibleItems),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(0.dp),
        flingBehavior = flingBehavior // Snap behavior is key for smooth scrolling
    ) {
        itemsIndexed(itemsWithSpacers) { index, item ->
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (item != null) {
                    val isSelected = item == centeredItem

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        val textStyle = if (isSelected) {
                            JasnifyTheme.typography.displayMedium.copy(
                                color = ContentBrandDark,
                            )
                        } else {
                            JasnifyTheme.typography.displaySmall.copy(
                                color = ContentSecondary,
                            )
                        }

                        ProvideTextStyle(value = textStyle) {
                            content(item)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}


// ---  STANDALONE DATE PICKER SLIDER ---
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatePickerSlider(
    selectedDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    yearRange: IntRange = LocalDate.now().year..(LocalDate.now().year + 5),
    itemHeight: Dp = 60.dp,
    visibleItems: Int = 3
) {
    val halfVisibleItems = visibleItems / 2

    val years = remember(yearRange) { yearRange.toList() }
    val months = remember { Month.entries.toList() }

    val daysInMonth by remember(selectedDate.year, selectedDate.month) {
        derivedStateOf { selectedDate.lengthOfMonth() }
    }
    val days by remember(daysInMonth) {
        derivedStateOf { (1..daysInMonth).toList() }
    }

    val yearScrollState = rememberLazyListState()
    val monthScrollState = rememberLazyListState()
    val dayScrollState = rememberLazyListState()

    // track the last date reported to prevent infinite loops/scroll fight when syncing state
    var lastScrollReportedDate by remember { mutableStateOf(selectedDate) }

    // Initial scroll to center based on initial values
    LaunchedEffect(Unit) {
        yearScrollState.scrollToItem(years.indexOf(selectedDate.year) + halfVisibleItems)
        monthScrollState.scrollToItem(months.indexOf(selectedDate.month) + halfVisibleItems)
        val initialDayIndex = days.indexOf(selectedDate.dayOfMonth.coerceAtMost(daysInMonth))
        dayScrollState.scrollToItem(initialDayIndex + halfVisibleItems)
    }

    // Sync scroll states if selectedDate changes from outside (e.g. external reset, calendar sync)
    LaunchedEffect(selectedDate, years, days) {
        if (selectedDate != lastScrollReportedDate) {
            lastScrollReportedDate = selectedDate

            val yearIndex = years.indexOf(selectedDate.year)
            if (yearIndex != -1 && !yearScrollState.isScrollInProgress) {
                yearScrollState.scrollToItem(yearIndex + halfVisibleItems)
            }

            val monthIndex = months.indexOf(selectedDate.month)
            if (monthIndex != -1 && !monthScrollState.isScrollInProgress) {
                monthScrollState.scrollToItem(monthIndex + halfVisibleItems)
            }

            val dayIndex = days.indexOf(selectedDate.dayOfMonth)
            if (dayIndex != -1 && !dayScrollState.isScrollInProgress) {
                dayScrollState.scrollToItem(dayIndex + halfVisibleItems)
            }
        }
    }

    // Helper to safely build dates and push updates back up
    val updateDateValue = remember(selectedDate, onDateChanged) {
        { newYear: Int?, newMonth: Month?, newDay: Int? ->
            try {
                val year = newYear ?: selectedDate.year
                val month = newMonth ?: selectedDate.month
                val day = newDay ?: selectedDate.dayOfMonth

                val maxDay = LocalDate.of(year, month, 1).lengthOfMonth()
                val finalDay = day.coerceAtMost(maxDay)

                val calculatedDate = LocalDate.of(year, month, finalDay)
                if (calculatedDate != selectedDate) {
                    lastScrollReportedDate = calculatedDate
                    onDateChanged(calculatedDate)
                }
            } catch (e: Exception) {
                // Ignore unexpected exceptions
            }
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Previous",
            tint = ContentPrimary,
            modifier = Modifier.size(32.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(itemHeight * visibleItems),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .align(Alignment.Center)
                    .background(
                        color = SurfaceBrandSecondary,
                        shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                    )
            )

            // Date Columns Selector
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Year Column
                DatePickerColumn(
                    items = years,
                    scrollState = yearScrollState,
                    itemHeight = itemHeight,
                    visibleItems = visibleItems,
                    onCenteredItemChanged = { newYear ->
                        updateDateValue(newYear, null, null)
                    }
                ) { year -> Text(text = year.toString()) }

                Spacer(Modifier.width(24.dp))

                // Month Column
                DatePickerColumn(
                    items = months,
                    scrollState = monthScrollState,
                    itemHeight = itemHeight,
                    visibleItems = visibleItems,
                    onCenteredItemChanged = { newMonth ->
                        updateDateValue(null, newMonth, null)
                    }
                ) { month -> Text(month.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())) }

                Spacer(Modifier.width(16.dp))

                // Day Column
                DatePickerColumn(
                    items = days,
                    scrollState = dayScrollState,
                    itemHeight = itemHeight,
                    visibleItems = visibleItems,
                    onCenteredItemChanged = { newDay ->
                        updateDateValue(null, null, newDay)
                    }
                ) { day -> Text(text = day.toString().padStart(2, '0')) }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Next",
            tint = ContentPrimary,
            modifier = Modifier.size(32.dp)
        )
    }
}


// ---  DATE PICKER SHEET ---
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSheet(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate = LocalDate.now()
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedDate by remember { mutableStateOf(initialDate) }

    // Custom Bottom Sheet Container
    CustomBottomSheet(
        heading = "Pick a date",
        sheetState = sheetState,
        onDismiss = onDismiss,
        sheetHeight = 336.dp,
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp, 0.dp)
        ) {
            // Reusable Standalone DatePickerSlider View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                DatePickerSlider(
                    selectedDate = selectedDate,
                    onDateChanged = { newDate ->
                        selectedDate = newDate
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Done button section
            CustomTextButton(
                onClick = {
                    onDateSelected(selectedDate)
                    Toast.makeText(context, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                text = "Done",
                size = ButtonSize.Medium,
                modifier = Modifier.fillMaxWidth(),
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 360, heightDp = 480)
@Composable
fun DatePickerSheetPreview() {
    MaterialTheme {
        val currentDate = LocalDate.now()

        DatePickerSheet(
            onDismiss = { /* Preview stub */ },
            onDateSelected = { /* Preview stub */ },
            initialDate = currentDate
        )
    }
}