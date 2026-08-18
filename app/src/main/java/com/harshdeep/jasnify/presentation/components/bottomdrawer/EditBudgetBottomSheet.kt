package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.inputfield.BudgetInput
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.yield
import java.math.BigDecimal

private val QuickAddOptions = listOf(
    5000L to "+ ₹5,000",
    10000L to "+ ₹10,000",
    50000L to "+ ₹50,000",
    200000L to "+ ₹2,00,000"
)

private val WhitespaceRegex = Regex("\\s+")

private val Units = arrayOf(
    "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
    "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
)

private val Tens = arrayOf(
    "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetBottomSheet(
    initialBudgetValue: String,
    isBudgetNotSet: Boolean = false,
    onDismiss: () -> Unit,
    onUpdateBudget: (String) -> Unit,
    modifier: Modifier = Modifier,
    onProgress: ((Float) -> Unit)? = null
) {
    var budgetValue by remember { mutableStateOf(initialBudgetValue) }
    var showCurrencyUI by remember { mutableStateOf(false) }

    val currencyCode = remember(budgetValue) {
        budgetValue.takeWhile { !it.isDigit() && it != '.' }
    }
    val numericString = remember(budgetValue, currencyCode) {
        budgetValue.substring(currencyCode.length)
    }
    val numericValue = remember(numericString) {
        numericString.toDoubleOrNull() ?: 0.0
    }

    val budgetInWords = remember(numericValue, currencyCode) {
        if (numericValue > 0.0) {
            convertToIndianCurrencyInWords(numericValue, currencyCode)
        } else {
            val cleanCode = when (currencyCode.trim()) {
                "₹", "INR", "" -> "Rupees"
                "$", "USD" -> "USD"
                "¥", "JPY" -> "JPY"
                "€", "EUR" -> "EUR"
                "£", "GBP" -> "GBP"
                else -> currencyCode.trim()
            }
            "Zero $cleanCode Only"
        }
    }

    val dynamicSheetHeight = remember(budgetInWords) {
        val baseHeight = 226
        val charsPerLine = 45
        if (budgetInWords.length > charsPerLine) {
            val extraLines = (budgetInWords.length - charsPerLine) / charsPerLine + 1
            (baseHeight + (extraLines * 18)).coerceAtMost(300).dp
        } else {
            baseHeight.dp
        }
    }

    val currentSheetHeight = if (showCurrencyUI) 512.dp else dynamicSheetHeight
    val currentHeading = if (showCurrencyUI) "Select currency" else if (isBudgetNotSet) "Add Budget" else "Edit Budget"

    CustomBottomSheet(
        heading = currentHeading,
        onDismiss = {
            if (showCurrencyUI) {
                showCurrencyUI = false
            } else {
                onDismiss()
            }
        },
        onProgress = onProgress,
        sheetHeight = currentSheetHeight
    ) {
        if (showCurrencyUI) {
            val currentCurrency = remember(currencyCode) {
                getCurrencyCodes().find { it.code == currencyCode } ?: SelectableItem(currencyCode, "", "")
            }
            CurrencySheetContent(
                initialSelection = currentCurrency,
                onItemSelected = { selectedItem ->
                    budgetValue = selectedItem.code + numericString
                    showCurrencyUI = false
                }
            )
        } else {
            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current

            LaunchedEffect(Unit) {
                yield()
                focusRequester.requestFocus()
                keyboardController?.show()
            }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BudgetInput(
                        value = budgetValue,
                        onValueChange = { newValue ->
                            budgetValue = newValue
                        },
                        onCurrencyClick = {
                            showCurrencyUI = true
                        },
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .focusRequester(focusRequester)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "In words : ",
                            style = JasnifyTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Light,
                                color = ContentSecondary,
                            ),
                            modifier = Modifier.wrapContentSize()
                        )
                        Text(
                            text = budgetInWords,
                            style = JasnifyTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                color = ContentPrimary,
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuickAddOptions.forEach { (amountToAdd, labelText) ->
                            FilterChip(
                                label = labelText,
                                isSelected = false,
                                shapeStyle = ChipShapeStyle.Round,
                                size = ChipSize.Small,
                                hasStroke = true,
                                onClick = {
                                    val updatedValue = (numericValue + amountToAdd).coerceAtMost(999999999999.0)
                                    val plainString = BigDecimal.valueOf(updatedValue).toPlainString()
                                    val cleanString = if (plainString.endsWith(".0")) plainString.substringBefore(".0") else plainString
                                    budgetValue = currencyCode + cleanString
                                },
                                modifier = Modifier.background(Color.Transparent)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                CustomTextButton(
                    onClick = { onUpdateBudget(budgetValue) },
                    text = if (isBudgetNotSet) "Add Budget" else "Update Budget",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    type = ButtonType.Primary,
                    shapeStyle = ButtonShapeStyle.Square,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = ContentInvPrimary
                )
            }
        }
    }
}

private fun convertToIndianCurrencyInWords(amount: Double, currencyCode: String): String {
    if (amount.isNaN() || amount.isInfinite() || amount < 0.0) return "Zero Only"
    if (amount > 999999999999.0) return "Amount Too Large"

    val num = amount.toLong()
    if (num == 0L) return "Zero Only"

    val result = StringBuilder()
    val crores = num / 10000000L
    var rem = num % 10000000L

    val lakhs = rem / 100000L
    rem %= 100000L

    val finalThousands = rem / 1000L
    val finalRemAfterThousands = rem % 1000L
    val finalHundreds = finalRemAfterThousands / 100L
    val finalRemainingUnits = finalRemAfterThousands % 100L

    if (crores > 0) {
        result.append(convertThousandsAndBelow(crores)).append(" Crore ")
    }
    if (lakhs > 0) {
        result.append(convertLessThanThousand(lakhs)).append(" Lakh ")
    }
    if (finalThousands > 0) {
        result.append(convertLessThanThousand(finalThousands)).append(" Thousand ")
    }
    if (finalHundreds > 0) {
        result.append(convertLessThanThousand(finalHundreds)).append(" Hundred ")
    }
    if (finalRemainingUnits > 0) {
        if (result.isNotEmpty()) result.append("and ")
        result.append(convertLessThanThousand(finalRemainingUnits))
    }

    val words = result.toString().trim().replace(WhitespaceRegex, " ")
    if (words.isEmpty()) return ""

    val capitalizedWords = words.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    val cleanCode = when (currencyCode.trim()) {
        "₹", "INR" -> "Rupee"
        "$", "USD" -> "USD"
        "¥", "JPY" -> "JPY"
        "€", "EUR" -> "EUR"
        "£", "GBP" -> "GBP"
        else -> currencyCode.trim().ifEmpty { "Rupee" }
    }

    return if (cleanCode == "Rupee") {
        "$capitalizedWords Rupee Only"
    } else {
        "$capitalizedWords $cleanCode Only"
    }
}

private fun convertThousandsAndBelow(number: Long): String {
    val result = StringBuilder()
    val thousands = number / 1000L
    val rem = number % 1000L
    if (thousands > 0) {
        result.append(convertLessThanThousand(thousands)).append(" Thousand ")
    }
    if (rem > 0) {
        result.append(convertLessThanThousand(rem))
    }
    return result.toString().trim()
}

private fun convertLessThanThousand(number: Long): String {
    var n = number
    val result = StringBuilder()
    if (n >= 100) {
        val hundredIndex = (n / 100).toInt()
        if (hundredIndex in Units.indices) {
            result.append(Units[hundredIndex]).append(" Hundred ")
        }
        n %= 100
    }
    if (n >= 20) {
        val tensIndex = (n / 10).toInt()
        if (tensIndex in Tens.indices) {
            result.append(Tens[tensIndex]).append(" ")
        }
        n %= 10
    }
    if (n > 0) {
        val unitIndex = n.toInt()
        if (unitIndex in Units.indices) {
            result.append(Units[unitIndex]).append(" ")
        }
    }
    return result.toString().trim()
}

@Preview(showBackground = true, name = "Edit Budget Sheet Light Preview")
@Composable
private fun EditBudgetBottomSheetPreview() {
    JasnifyTheme {
        EditBudgetBottomSheet(
            initialBudgetValue = "INR10000000",
            onDismiss = {},
            onUpdateBudget = {}
        )
    }
}
