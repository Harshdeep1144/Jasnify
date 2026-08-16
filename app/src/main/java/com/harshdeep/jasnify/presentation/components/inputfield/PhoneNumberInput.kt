package com.harshdeep.jasnify.presentation.components.inputfield

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CountryBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SelectableItem
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    // tracks the local phone number part (without code)
    var localPhoneNumber by remember { mutableStateOf("") }
    var showCountryCodeSheet by remember { mutableStateOf(false) }
    var selectedCountryCode by remember {
        mutableStateOf(SelectableItem("+91", "India", "IN"),)
    }

    // Determine if the clear icon should be shown
    val showClearIcon = localPhoneNumber.isNotEmpty()

    // report full number whenever country code or local number changes
    LaunchedEffect(localPhoneNumber, selectedCountryCode) {
        val fullPhoneNumber = selectedCountryCode.code + localPhoneNumber
        onValueChange(fullPhoneNumber)
    }

    // Custom input field
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.25f)
                .clickable { showCountryCodeSheet = true }
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
                text = selectedCountryCode.emoji, // Placeholder for Flag emoji or icon
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(selectedCountryCode.code,
                style = MaterialTheme.typography.bodyLarge,
                color = ContentPrimary
            )

        }

        Spacer(Modifier.width(2.dp))

        OutlinedTextField(
            value = localPhoneNumber,
            onValueChange = { newValue ->
                // Basic filter for numeric input
                localPhoneNumber = newValue.filter { it.isDigit() }
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            placeholder = {
                Text(
                    text = "Enter phone number",
                    color = ContentSecondary
                )
            },
            textStyle = MaterialTheme.typography.labelLarge.copy(
                color = ContentPrimary
            ),
            trailingIcon = {
                if (showClearIcon) {
                    IconButton(onClick = { localPhoneNumber = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear phone number",
                            tint = ContentSecondary
                        )
                    }
                }
            }
        )
    }


    // Country Code Bottom Sheet
    if (showCountryCodeSheet) {
        CountryBottomSheet (
            initialSelection = selectedCountryCode,
            onItemSelected = { selectedItem ->
                // Update the country code. LaunchedEffect handles reporting the full number.
                selectedCountryCode = selectedItem
            },
            onDismiss = { showCountryCodeSheet = false }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PhoneNumberInputPreview() {
    PhoneNumberInput(
        value = "+919876543210",
        onValueChange = {}
    )
}