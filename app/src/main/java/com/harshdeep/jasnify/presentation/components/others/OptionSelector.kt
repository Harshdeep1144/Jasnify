package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // Added for state management
import androidx.compose.runtime.remember // Added for state management
import androidx.compose.runtime.setValue // Added for state delegation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceAccent
import sv.lib.squircleshape.SquircleShape

@Composable
fun OptionSelector(
    label: String,
    bodyText: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Animate background color change for smooth transitions between selected/unselected states
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            SurfaceAccent
        } else {
            BackgroundPrimary
        }, label = "backgroundColor"
    )

    // 2. Animate content color (text, radio button) for contrast
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            ContentPrimary
        } else {
            ContentPrimary
        }, label = "contentColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(min (a = 78.dp, b = 100.dp))
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
        color = backgroundColor,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = ContentBrand,
                    unselectedColor = ContentSecondary
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = JasnifyTheme.typography.headingLarge,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = bodyText,
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary
                )
            }
        }
    }
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun OptionSelectorPreview() {
    MaterialTheme { // Use a Material 3 theme for accurate visualization
        // List of options to display
        val options = listOf(
            Pair("Basic Plan", "The ideal choice for single users or small projects."),
            Pair("Standard Plan", "Body Text for the default option."),
            Pair("Premium Plan", "Includes all features and priority support.")
        )

        // State to track which option is selected (default to the second option, index 1, to match the original image)
        var selectedOptionIndex by remember { mutableStateOf(1) }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Loop through the options to create the interactive radio group
            options.forEachIndexed { index, option ->
                OptionSelector(
                    label = option.first,
                    bodyText = option.second,
                    isSelected = index == selectedOptionIndex, // Check if this option is currently selected
                    onClick = {
                        selectedOptionIndex = index // Update the state when clicked
                    }
                )
            }
        }
    }
}
