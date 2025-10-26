package com.harshdeep.jasnify.presentation.components.inputfield

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CurrencyBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SelectableItem
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlin.math.roundToInt
import java.text.NumberFormat
import java.util.Locale
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfaceBrand
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

const val MIN_AMOUNT_FLOAT = 100_000f
const val MAX_AMOUNT_FLOAT = 10_000_000f
const val INTERVAL_FLOAT = 500_000f
val SLIDER_STEPS = ((MAX_AMOUNT_FLOAT - MIN_AMOUNT_FLOAT) / INTERVAL_FLOAT - 1).roundToInt()

// Helper function to format the number (e.g., 100000 -> 100,000)
fun formatAmount(amount: Float, locale: Locale = Locale.US): String {
    val formatter = NumberFormat.getNumberInstance(locale)
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetInput(
    value: String, // The full currency string (e.g., "INR100000")
    onValueChange: (String) -> Unit
) {
    // tracks the local currency value part (without code/symbol)
    var localCurrencyValue by remember { mutableStateOf("") }
    var showCurrencyCodeSheet by remember { mutableStateOf(false) }
    var selectedCurrency by remember {
        // Code is the Symbol, Name is the ISO code
        mutableStateOf(SelectableItem("INR", "Indian Rupee", "IN"))
    }

    var sliderValue by remember { mutableStateOf(MIN_AMOUNT_FLOAT) }

    // Flag to prevent the slider from overwriting user manual input immediately
    var isManualInput by remember { mutableStateOf(false) }


    // --- Effect to report the combined value ---
    LaunchedEffect(localCurrencyValue, selectedCurrency) {
        val fullCurrencyValue = selectedCurrency.code + localCurrencyValue
        onValueChange(fullCurrencyValue)
    }

    // --- Effect to sync external value change to local state ---
    LaunchedEffect(value) {
        val numericValue = value.removePrefix(selectedCurrency.code)

        if (!isManualInput && numericValue.isNotEmpty()) {
            localCurrencyValue = numericValue

            // Try to update slider position based on the input field value
            numericValue.toFloatOrNull()?.let { fValue ->
                if (fValue >= MIN_AMOUNT_FLOAT && fValue <= MAX_AMOUNT_FLOAT) {
                    sliderValue = fValue
                }
            }
        }
        isManualInput = false
    }

    val showClearIcon = localCurrencyValue.isNotEmpty()

    // Determine the label format based on currency
    val minLabelText: String
    val maxLabelText: String

    if (selectedCurrency.code == "INR") {
        minLabelText = "1 Lakh"
        maxLabelText = "1 Cr"
    } else {
        // Use standard large numbers for non-INR currencies
        minLabelText = selectedCurrency.code + "\t" + formatAmount(MIN_AMOUNT_FLOAT)
        maxLabelText = selectedCurrency.code + "\t" + formatAmount(MAX_AMOUNT_FLOAT)
    }

    // --- UI Layout ---
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(shape = SquircleShape(CornerLarge, CornerSmoothingDefault)),
        ) {
            // Currency Selection Area (Symbol + ISO Code )
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f)
                    .clickable { showCurrencyCodeSheet = true }
                    .background(SurfaceSecondary,
                        shape = SquircleShape(CornerLarge, CornerExtraSmall, CornerLarge,CornerExtraSmall, CornerSmoothingDefault)
                    )
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                        shape = SquircleShape(CornerLarge, CornerExtraSmall, CornerLarge,CornerExtraSmall, CornerSmoothingDefault)
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = selectedCurrency.emoji,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(selectedCurrency.code,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ContentPrimary
                )

            }

            Spacer(Modifier.width(2.dp))

            // Currency Value Input Field
            OutlinedTextField(
                value = localCurrencyValue,
                onValueChange = { newValue ->
                    isManualInput = true

                    val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                        .let {
                            if (it.count { char -> char == '.' } > 1) {
                                it.substringBeforeLast('.')
                            } else {
                                it
                            }
                        }

                    localCurrencyValue = filteredValue

                    filteredValue.toFloatOrNull()?.let { fValue ->
                        when {
                            fValue <= MIN_AMOUNT_FLOAT -> sliderValue = MIN_AMOUNT_FLOAT
                            fValue >= MAX_AMOUNT_FLOAT -> sliderValue = MAX_AMOUNT_FLOAT
                            else -> sliderValue = fValue
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f)
                    .background(SurfaceSecondary,
                        shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerExtraSmall,CornerLarge, CornerSmoothingDefault)

                    )
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                        shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerExtraSmall,CornerLarge, CornerSmoothingDefault)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = {
                    Text(text = "Enter amount", color = ContentSecondary)
                },
                textStyle = MaterialTheme.typography.labelLarge.copy(color = ContentPrimary),
                trailingIcon = {
                    if (showClearIcon) {
                        IconButton(
                            onClick = {
                                localCurrencyValue = ""
                                sliderValue = MIN_AMOUNT_FLOAT
                                isManualInput = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear amount",
                                tint = ContentSecondary
                            )
                        }
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "This figure will be only used to manage your budget.",
                style = JasnifyTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  Budget Slider
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {

            Slider(
                value = sliderValue,
                onValueChange = { newSliderValue ->
                    // Snap the new value to the nearest interval step
                    val snappedValue = ((newSliderValue / INTERVAL_FLOAT).roundToInt() * INTERVAL_FLOAT)
                        .coerceIn(MIN_AMOUNT_FLOAT, MAX_AMOUNT_FLOAT)

                    sliderValue = snappedValue
                    val roundedValue = snappedValue.roundToInt().toString()
                    localCurrencyValue = roundedValue
                    isManualInput = true
                },
                valueRange = MIN_AMOUNT_FLOAT..MAX_AMOUNT_FLOAT,
                steps = SLIDER_STEPS,
                colors = SliderDefaults.colors(
                    activeTrackColor = SurfaceBrand,
                    inactiveTrackColor = SurfaceSecondary,
                    thumbColor = ContentBrand,
                    activeTickColor = SurfacePrimary,
                    inactiveTickColor = ContentSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            // Min/Max Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = minLabelText,
                    style = MaterialTheme.typography.labelSmall,
                    color = ContentSecondary
                )
                Text(
                    text = maxLabelText,
                    style = MaterialTheme.typography.labelSmall,
                    color = ContentSecondary
                )
            }
        }
    }


    // Currency Code Bottom Sheet
    if (showCurrencyCodeSheet) {
        CurrencyBottomSheet(
            initialSelection = selectedCurrency,
            onItemSelected = { selectedItem ->
                selectedCurrency = selectedItem
            },
            onDismiss = { showCurrencyCodeSheet = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyInputPreview() {
    BudgetInput(
        value = "₹1000000",
        onValueChange = {}
    )
}