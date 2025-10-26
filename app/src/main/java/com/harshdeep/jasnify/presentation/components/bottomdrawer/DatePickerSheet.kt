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
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceAccent
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

    //  Determine the actual centered item object
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












// ---  DATE PICKER SHEET ---
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
    val halfVisibleItems = visibleItems / 2

    val currentYear = initialDate.year
    val years = remember { (currentYear..currentYear + 5).toList() }
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

    // Function to scroll to center (using original list indices)
    fun LazyListState.scrollToItemCenter(index: Int) {
        coroutineScope.launch {
            val target = index + halfVisibleItems
            animateScrollToItem(target)
        }
    }

    // Initial scroll to center
    LaunchedEffect(Unit) {
        yearScrollState.scrollToItemCenter(years.indexOf(initialDate.year))
        monthScrollState.scrollToItemCenter(months.indexOf(initialDate.month))
        // Ensure initial day is within bounds for the initial month/year
        val initialDayIndex = days.indexOf(initialDate.dayOfMonth.coerceAtMost(daysInMonth))
        dayScrollState.scrollToItemCenter(initialDayIndex)
    }

    // New centralized function to update the date safely
    fun updateSelectedDate(
        newYear: Int? = null,
        newMonth: Month? = null,
        newDay: Int? = null
    ) {
        selectedDate = try {
            val year = newYear ?: selectedDate.year
            val month = newMonth ?: selectedDate.month
            val day = newDay ?: selectedDate.dayOfMonth

            val maxDay = LocalDate.of(year, month, 1).lengthOfMonth()
            val finalDay = day.coerceAtMost(maxDay)

            LocalDate.of(year, month, finalDay)
        } catch (e: Exception) {
            selectedDate
        }
    }

    // --- UI ---
    CustomBottomSheet(
        heading = "Pick a date",
        sheetState = sheetState,
        onDismiss = onDismiss,
        sheetHeight = 336.dp,
        sheetGesturesEnabled = false
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
                            // Year Column
                            DatePickerColumn(
                                items = years,
                                scrollState = yearScrollState,
                                itemHeight = itemHeight,
                                visibleItems = visibleItems,
                                onCenteredItemChanged = { newYear ->
                                    updateSelectedDate(newYear = newYear)
                                }
                            ) { year -> Text(text = year.toString()) }

                            Spacer(Modifier.width(24.dp))

                            // Month Column
                            DatePickerColumn(
                                items = months,
                                scrollState = monthScrollState,
                                itemHeight = itemHeight,
                                visibleItems = visibleItems,
                                // Instantaneous update on centered item change
                                onCenteredItemChanged = { newMonth ->
                                    updateSelectedDate(newMonth = newMonth)
                                }
                            ) { month -> Text(month.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())) }

                            Spacer(Modifier.width(16.dp))

                            DatePickerColumn(
                                items = days,
                                scrollState = dayScrollState,
                                itemHeight = itemHeight,
                                visibleItems = visibleItems,
                                // Instantaneous update on centered item change
                                onCenteredItemChanged = { newDay ->
                                    updateSelectedDate(newDay = newDay)
                                }
                            ) { day -> Text(text = day.toString().padStart(2, '0')) }
                        }
                    }

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
