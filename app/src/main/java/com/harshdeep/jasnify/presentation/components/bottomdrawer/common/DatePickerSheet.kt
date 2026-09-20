package com.harshdeep.jasnify.presentation.components.bottomdrawer.common

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
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import sv.lib.squircleshape.SquircleShape
import java.time.LocalDate
import java.time.Month
import java.util.Locale
import java.time.format.TextStyle as JavaTextStyle

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
    val haptic = LocalHapticFeedback.current
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

    val currentReportUpdates by rememberUpdatedState(reportUpdates)
    val currentOnCenteredItemChanged by rememberUpdatedState(onCenteredItemChanged)

    // Listen to changes in the centered index via snapshotFlow
    LaunchedEffect(scrollState, items) {
        snapshotFlow { centeredItemIndex }
            .distinctUntilChanged()
            .drop(1) // Ignore initial composition setup
            .collect { index ->
                if (index in items.indices) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    if (currentReportUpdates) {
                        currentOnCenteredItemChanged(items[index])
                    }
                }
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
    minDate: LocalDate = LocalDate.now(),
    maxYear: Int = 2040,
    yearRange: IntRange = minDate.year..maxOf(minDate.year, maxYear),
    itemHeight: Dp = 60.dp,
    visibleItems: Int = 3
) {
    val effectiveSelectedDate = remember(selectedDate, minDate) {
        if (selectedDate.isBefore(minDate)) minDate else selectedDate
    }

    val years = remember(yearRange) { yearRange.toList() }

    val availableMonths = remember(effectiveSelectedDate.year, minDate) {
        if (effectiveSelectedDate.year == minDate.year) {
            Month.entries.filter { it.value >= minDate.monthValue }
        } else {
            Month.entries.toList()
        }
    }

    val daysInMonth = remember(effectiveSelectedDate.year, effectiveSelectedDate.month) {
        effectiveSelectedDate.lengthOfMonth()
    }

    val availableDays = remember(effectiveSelectedDate.year, effectiveSelectedDate.month, minDate, daysInMonth) {
        val startDay = if (effectiveSelectedDate.year == minDate.year && effectiveSelectedDate.month == minDate.month) {
            minDate.dayOfMonth
        } else {
            1
        }
        (startDay..daysInMonth).toList()
    }

    val initialYearIndex = remember { years.indexOf(effectiveSelectedDate.year).coerceAtLeast(0) }
    val initialMonthIndex = remember { availableMonths.indexOf(effectiveSelectedDate.month).coerceAtLeast(0) }
    val initialDayIndex = remember { availableDays.indexOf(effectiveSelectedDate.dayOfMonth).coerceAtLeast(0) }

    val yearScrollState = rememberLazyListState(initialFirstVisibleItemIndex = initialYearIndex)
    val monthScrollState = rememberLazyListState(initialFirstVisibleItemIndex = initialMonthIndex)
    val dayScrollState = rememberLazyListState(initialFirstVisibleItemIndex = initialDayIndex)

    var isProgrammaticScroll by remember { mutableStateOf(false) }
    var lastScrollReportedDate by remember { mutableStateOf(effectiveSelectedDate) }

    // Synchronize scroll states if selectedDate updates from external caller
    LaunchedEffect(effectiveSelectedDate, years, availableMonths, availableDays) {
        if (effectiveSelectedDate != lastScrollReportedDate) {
            isProgrammaticScroll = true
            lastScrollReportedDate = effectiveSelectedDate

            val yearIndex = years.indexOf(effectiveSelectedDate.year)
            if (yearIndex != -1 && !yearScrollState.isScrollInProgress) {
                yearScrollState.scrollToItem(yearIndex)
            }

            val monthIndex = availableMonths.indexOf(effectiveSelectedDate.month)
            if (monthIndex != -1 && !monthScrollState.isScrollInProgress) {
                monthScrollState.scrollToItem(monthIndex)
            }

            val dayIndex = availableDays.indexOf(effectiveSelectedDate.dayOfMonth)
            if (dayIndex != -1 && !dayScrollState.isScrollInProgress) {
                dayScrollState.scrollToItem(dayIndex)
            }

            isProgrammaticScroll = false
        }
    }

    // Coerce month index if available months list shrinks
    LaunchedEffect(availableMonths) {
        val currentMonthIndex = monthScrollState.firstVisibleItemIndex
        val maxMonthIndex = (availableMonths.size - 1).coerceAtLeast(0)
        if (currentMonthIndex > maxMonthIndex) {
            isProgrammaticScroll = true
            monthScrollState.scrollToItem(maxMonthIndex)
            isProgrammaticScroll = false
        }
    }

    // Coerce day index if available days list shrinks
    LaunchedEffect(availableDays) {
        val currentDayIndex = dayScrollState.firstVisibleItemIndex
        val maxDayIndex = (availableDays.size - 1).coerceAtLeast(0)
        if (currentDayIndex > maxDayIndex) {
            isProgrammaticScroll = true
            dayScrollState.scrollToItem(maxDayIndex)
            isProgrammaticScroll = false
        }
    }

    val updateDateValue = remember(effectiveSelectedDate, minDate, availableMonths, availableDays, onDateChanged) {
        { newYear: Int?, newMonth: Month?, newDay: Int? ->
            try {
                val targetYear = newYear ?: effectiveSelectedDate.year

                val validMonths = if (targetYear == minDate.year) {
                    Month.entries.filter { it.value >= minDate.monthValue }
                } else {
                    Month.entries.toList()
                }
                val requestedMonth = newMonth ?: effectiveSelectedDate.month
                val targetMonth = if (requestedMonth in validMonths) requestedMonth else validMonths.first()

                val maxDay = LocalDate.of(targetYear, targetMonth, 1).lengthOfMonth()
                val startDay = if (targetYear == minDate.year && targetMonth == minDate.month) minDate.dayOfMonth else 1
                val requestedDay = newDay ?: effectiveSelectedDate.dayOfMonth
                val targetDay = requestedDay.coerceIn(startDay, maxDay)

                var calculatedDate = LocalDate.of(targetYear, targetMonth, targetDay)
                if (calculatedDate.isBefore(minDate)) {
                    calculatedDate = minDate
                }

                if (calculatedDate != effectiveSelectedDate) {
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
                    items = availableMonths,
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
                    items = availableDays,
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
    minDate: LocalDate = LocalDate.now(),
    maxYear: Int = 2040,
    isVisible: Boolean = true,
    onProgress: ((Float) -> Unit)? = null
) {
    val context = LocalContext.current
    val effectiveInitialDate = remember(initialDate, minDate) {
        if (initialDate.isBefore(minDate)) minDate else initialDate
    }
    var selectedDate by remember(effectiveInitialDate) { mutableStateOf(effectiveInitialDate) }

    CustomBottomSheet(
        heading = "Pick a date",
        onDismiss = onDismiss,
        isVisible = isVisible,
        sheetHeight = 336.dp,
        sheetGesturesEnabled = false,
        showDragHandle = true,
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
                    minDate = minDate,
                    maxYear = maxYear,
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