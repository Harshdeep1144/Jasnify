package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceAccent
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DatePickerSheet(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate = LocalDate.now()
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedDate by remember { mutableStateOf(initialDate) }

    val coroutineScope = rememberCoroutineScope()

    val itemHeight = 60.dp
    val visibleItems = 3
    // This defines the number of spacer items needed for centering
    val halfVisibleItems = visibleItems / 2 // This is 1

    val currentYear = initialDate.year
    // The range of years the user can pick
    val years = remember { (currentYear..currentYear + 5).toList() }
    val months = remember { Month.entries.toList() }
    val daysInMonth = remember(selectedDate.year, selectedDate.month) {
        selectedDate.lengthOfMonth()
    }
    val days = remember(daysInMonth) { (1..daysInMonth).toList() }

    val yearScrollState = rememberLazyListState()
    val monthScrollState = rememberLazyListState()
    val dayScrollState = rememberLazyListState()

    fun LazyListState.scrollToItemCenter(index: Int) {
        coroutineScope.launch {
            // Target index in the list *with* spacers
            val target = index + halfVisibleItems
            animateScrollToItem(target)
        }
    }

    fun LazyListState.getCenteredItemIndex(): Int {
        val viewportCenter = layoutInfo.viewportEndOffset / 2
        val closest = layoutInfo.visibleItemsInfo.minByOrNull {
            kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
        }
        return (closest?.index ?: halfVisibleItems) - halfVisibleItems
    }

    // Initial scroll to center (using original list indices)
    LaunchedEffect(Unit) {
        yearScrollState.scrollToItemCenter(years.indexOf(initialDate.year))
        monthScrollState.scrollToItemCenter(months.indexOf(initialDate.month))
        dayScrollState.scrollToItemCenter(days.indexOf(initialDate.dayOfMonth))
    }

    // Update selected date when year scrolling stops
    LaunchedEffect(yearScrollState.isScrollInProgress) {
        if (!yearScrollState.isScrollInProgress) {
            val newYearIndex = yearScrollState.getCenteredItemIndex().coerceIn(0, years.size - 1)
            val newYear = years[newYearIndex]
            if (selectedDate.year != newYear) {
                selectedDate = LocalDate.of(
                    newYear,
                    selectedDate.month,
                    selectedDate.dayOfMonth.coerceAtMost(
                        LocalDate.of(newYear, selectedDate.month, 1).lengthOfMonth()
                    )
                )
            }
        }
    }

    // Update selected date when month scrolling stops
    LaunchedEffect(monthScrollState.isScrollInProgress) {
        if (!monthScrollState.isScrollInProgress) {
            val newMonthIndex = monthScrollState.getCenteredItemIndex().coerceIn(0, months.size - 1)
            val newMonth = months[newMonthIndex]
            if (selectedDate.month != newMonth) {
                selectedDate = LocalDate.of(
                    selectedDate.year,
                    newMonth,
                    selectedDate.dayOfMonth.coerceAtMost(
                        LocalDate.of(selectedDate.year, newMonth, 1).lengthOfMonth()
                    )
                )
            }
        }
    }

    // Update selected date when day scrolling stops
    LaunchedEffect(dayScrollState.isScrollInProgress) {
        if (!dayScrollState.isScrollInProgress) {
            val maxDayIndex = days.size - 1
            val newDayIndex = dayScrollState.getCenteredItemIndex().coerceIn(0, maxDayIndex)
            val newDay = days[newDayIndex]
            if (selectedDate.dayOfMonth != newDay) {
                selectedDate = LocalDate.of(selectedDate.year, selectedDate.month, newDay)
            }
        }
    }

    // --- UI ---
    CustomBottomSheet(
        heading = "Pick a date",
        sheetState = sheetState,
        onDismiss = onDismiss,
        sheetHeight = 336.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(12.dp, 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    // Left arrow
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Previous",
                        tint = ContentPrimary,
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(itemHeight * visibleItems),
                        contentAlignment = Alignment.Center
                    ) {
                        // Center highlight background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight)
                                .align(Alignment.Center)
                                .background(
                                    color = SurfaceAccent,
                                    shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                                )
                        )

                        // Date columns
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DatePickerColumn(
                                items = years,
                                scrollState = yearScrollState,
                                selectedItem = selectedDate.year,
                                itemHeight = itemHeight,
                                visibleItems = visibleItems,
                                onScrollToIndex = { yearScrollState.scrollToItemCenter(it) }
                            ) { year -> Text(text = year.toString()) }

                            DatePickerColumn(
                                items = months,
                                scrollState = monthScrollState,
                                selectedItem = selectedDate.month,
                                itemHeight = itemHeight,
                                visibleItems = visibleItems,
                                onScrollToIndex = { monthScrollState.scrollToItemCenter(it) }
                            ) { month -> Text(month.getDisplayName(TextStyle.SHORT, Locale.getDefault())) }

                            DatePickerColumn(
                                items = days,
                                scrollState = dayScrollState,
                                selectedItem = selectedDate.dayOfMonth,
                                itemHeight = itemHeight,
                                visibleItems = visibleItems,
                                onScrollToIndex = { dayScrollState.scrollToItemCenter(it) }
                            ) { day -> Text(text = day.toString().padStart(2, '0')) }
                        }
                    }

                    // Right arrow
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Next",
                        tint = ContentPrimary,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
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
                modifier = Modifier
                    .fillMaxWidth(),
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> DatePickerColumn(
    items: List<T>,
    scrollState: LazyListState,
    selectedItem: T,
    itemHeight: Dp,
    visibleItems: Int,
    onScrollToIndex: (Int) -> Unit,
    content: @Composable (T) -> Unit
) {
    val halfVisibleItems = visibleItems / 2 // 1 in this case
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)
    val itemsWithSpacers = remember(items) {
        List<T?>(halfVisibleItems) { null } + items + List<T?>(halfVisibleItems) { null }
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
        itemsIndexed(itemsWithSpacers) { index, item ->
            val actualIndex = index - halfVisibleItems

            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (item != null) {
                    val isSelected = item == selectedItem

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clickable { onScrollToIndex(actualIndex) },
                        contentAlignment = Alignment.Center
                    ) {
                        val textStyle = if (isSelected) {
                            JasnifyTheme.typography.displayMedium.copy(
                                color = ContentPrimary,
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
