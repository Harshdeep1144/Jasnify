package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

/**
 * Generic wheel picker column for selecting dates (Years, Months, Days).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> DatePickerColumn(
    items: List<T>,
    scrollState: LazyListState,
    itemHeight: Dp,
    visibleItems: Int,
    reportUpdates: Boolean = true,
    onCenteredItemChanged: (T) -> Unit,
    content: @Composable (T) -> Unit
) {
    val halfVisibleItems = visibleItems / 2
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)

    // Add empty spacers at top and bottom to center wheel items
    val itemsWithSpacers = remember(items) {
        List<T?>(halfVisibleItems) { null } + items + List<T?>(halfVisibleItems) { null }
    }

    // Instantly calculate the index of the item closest to the middle viewport offset
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

    val centeredItem: T? = remember(centeredItemIndex, items) {
        centeredItemIndex.takeIf { it in items.indices }?.let { items[it] }
    }

    // Report selection update when centered item changes
    LaunchedEffect(centeredItem, reportUpdates) {
        if (reportUpdates && centeredItem != null) {
            onCenteredItemChanged(centeredItem)
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .width(70.dp)
            .height(itemHeight * visibleItems),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(0.dp),
        flingBehavior = flingBehavior
    ) {
        itemsIndexed(itemsWithSpacers) { _, item ->
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
                                color = ContentBrandDark
                            )
                        } else {
                            JasnifyTheme.typography.displaySmall.copy(
                                color = ContentSecondary
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

/**
 * Standalone triple-column slider for date selection (Year, Month, Day).
 */
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
    val years = remember(yearRange) { yearRange.toList() }
    val months = remember { Month.entries.toList() }

    val daysInMonth by remember(selectedDate.year, selectedDate.month) {
        derivedStateOf { selectedDate.lengthOfMonth() }
    }
    val days by remember(daysInMonth) {
        derivedStateOf { (1..daysInMonth).toList() }
    }

    val initialYearIndex = remember { years.indexOf(selectedDate.year).coerceAtLeast(0) }
    val initialMonthIndex = remember { months.indexOf(selectedDate.month).coerceAtLeast(0) }
    val initialDayIndex = remember { (selectedDate.dayOfMonth - 1).coerceIn(0, daysInMonth - 1) }

    val yearScrollState = rememberLazyListState(initialFirstVisibleItemIndex = initialYearIndex)
    val monthScrollState = rememberLazyListState(initialFirstVisibleItemIndex = initialMonthIndex)
    val dayScrollState = rememberLazyListState(initialFirstVisibleItemIndex = initialDayIndex)

    var isProgrammaticScroll by remember { mutableStateOf(false) }
    var lastScrollReportedDate by remember { mutableStateOf(selectedDate) }

    // Synchronize scroll states if selectedDate updates from external caller
    LaunchedEffect(selectedDate, years, days) {
        if (selectedDate != lastScrollReportedDate) {
            isProgrammaticScroll = true
            lastScrollReportedDate = selectedDate

            val yearIndex = years.indexOf(selectedDate.year)
            if (yearIndex != -1 && !yearScrollState.isScrollInProgress) {
                yearScrollState.scrollToItem(yearIndex)
            }

            val monthIndex = months.indexOf(selectedDate.month)
            if (monthIndex != -1 && !monthScrollState.isScrollInProgress) {
                monthScrollState.scrollToItem(monthIndex)
            }

            val dayIndex = days.indexOf(selectedDate.dayOfMonth)
            if (dayIndex != -1 && !dayScrollState.isScrollInProgress) {
                dayScrollState.scrollToItem(dayIndex)
            }

            isProgrammaticScroll = false
        }
    }

    // Coerce day index if month changes to one with fewer days (e.g., Jan 31 -> Feb)
    LaunchedEffect(days) {
        val currentDayIndex = dayScrollState.firstVisibleItemIndex
        val maxDayIndex = days.size - 1
        if (currentDayIndex > maxDayIndex) {
            isProgrammaticScroll = true
            dayScrollState.scrollToItem(maxDayIndex)
            isProgrammaticScroll = false
        }
    }

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
            } catch (_: Exception) {
                // Ignore transient invalid dates during scroll
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
                    reportUpdates = !isProgrammaticScroll,
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
                    reportUpdates = !isProgrammaticScroll,
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
                    reportUpdates = !isProgrammaticScroll,
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

/**
 * DatePickerSheet modal component powered by fluid physics-based CustomBottomSheet container.
 */
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("FrequentlyChangingValue")
@Composable
fun DatePickerSheet(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate = LocalDate.now(),
    isVisible: Boolean = true,
    onProgress: ((Float) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(initialDate) }

    CustomBottomSheet(
        heading = "Pick a date",
        onDismiss = onDismiss,
        isVisible = isVisible,
        sheetHeight = 336.dp,
        sheetGesturesEnabled = false,
        showDragHandle = false,
        onProgress = onProgress
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                shapeStyle = ButtonShapeStyle.Square
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 360, heightDp = 520)
@Composable
fun DatePickerSheetPreview() {
    JasnifyTheme {
        Surface {
            val currentDate = remember { LocalDate.now() }
            var isSheetVisible by remember { mutableStateOf(true) }

            Box(modifier = Modifier.fillMaxSize()) {
                DatePickerSheet(
                    isVisible = isSheetVisible,
                    onDismiss = { isSheetVisible = false },
                    onDateSelected = { /* Handle selected date */ },
                    initialDate = currentDate
                )
            }
        }
    }
}