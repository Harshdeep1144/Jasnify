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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CurrencyBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SelectableItem
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetInput(
    value: String, // The full currency string (e.g., "INR100000")
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null // Explicit focus parameter
) {
    var showCurrencyCodeSheet by remember { mutableStateOf(false) }
    var selectedCurrency by remember {
        // Code is the Symbol, Name is the ISO code
        mutableStateOf(SelectableItem("INR", "Indian Rupee", "IN"))
    }

    // Parse current currency prefix from value
    val currentPrefix = remember(value) {
        value.takeWhile { !it.isDigit() && it != '.' }
    }

    // Sync selectedCurrency if prefix changes externally
    if (currentPrefix.isNotEmpty() && currentPrefix != selectedCurrency.code) {
        selectedCurrency = SelectableItem(currentPrefix, selectedCurrency.name, selectedCurrency.emoji)
    }

    // Derive the local currency numeric value part directly from the passed source of truth
    val numericValue = remember(value, selectedCurrency.code) {
        value.removePrefix(selectedCurrency.code)
    }

    val showClearIcon = numericValue.isNotEmpty()

    // Apply the parent-supplied layout modifiers ONLY to the outermost Column
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(shape = SquircleShape(CornerLarge, CornerSmoothingDefault)),
        ) {
            // Currency Selection Area (Symbol + ISO Code)
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f)
                    .clickable { showCurrencyCodeSheet = true }
                    .background(
                        SurfaceSecondary,
                        shape = SquircleShape(CornerLarge, CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerSmoothingDefault)
                    )
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                        shape = SquircleShape(CornerLarge, CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerSmoothingDefault)
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = selectedCurrency.emoji,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = selectedCurrency.code,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ContentPrimary
                )
            }

            Spacer(Modifier.width(2.dp))

            // Set up the OutlinedTextField modifier chain, appending the focus requester if provided
            val textFieldModifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(1f)
                .background(
                    SurfaceSecondary,
                    shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerLarge, CornerSmoothingDefault)
                )
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                    shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerLarge, CornerSmoothingDefault)
                )
                .let { baseModifier ->
                    if (focusRequester != null) baseModifier.focusRequester(focusRequester) else baseModifier
                }

            OutlinedTextField(
                value = numericValue,
                onValueChange = { newValue ->
                    val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                        .let {
                            if (it.count { char -> char == '.' } > 1) {
                                it.substringBeforeLast('.')
                            } else {
                                it
                            }
                        }

                    // Enforce a maximum limit of 12 digits (up to 99,999 Crores) to prevent layout & arithmetic overflow crashes
                    if (filteredValue.length <= 12) {
                        onValueChange(selectedCurrency.code + filteredValue)
                    }
                },
                modifier = textFieldModifier,
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
                                onValueChange(selectedCurrency.code)
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
    }

    // Currency Code Bottom Sheet
    if (showCurrencyCodeSheet) {
        CurrencyBottomSheet(
            initialSelection = selectedCurrency,
            onItemSelected = { selectedItem ->
                selectedCurrency = selectedItem
                onValueChange(selectedItem.code + numericValue)
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