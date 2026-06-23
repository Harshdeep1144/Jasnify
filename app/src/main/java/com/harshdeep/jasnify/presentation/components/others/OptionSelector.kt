package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.CustomRadioButton
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceAccent
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@Composable
fun OptionSelector(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bodyText: String? = null
) {
    val backgroundColor = if (isSelected) SurfaceBrandSecondary else SurfacePrimary
    val contentColor = if (isSelected) ContentBrandDark else ContentSecondary
    val bodyTextColor = if (isSelected) ContentPrimary else ContentSecondary

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight() // Changed from fixed height to wrapContentHeight
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
            CustomRadioButton(
                selected = isSelected,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = JasnifyTheme.typography.headingLarge,
                    color = contentColor
                )

                // When bodyText is null, neither the Spacer nor the Text compile into the UI tree
                if (!bodyText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = bodyText,
                        style = JasnifyTheme.typography.bodyMedium,
                        color = bodyTextColor
                    )
                }
            }
        }
    }
}



// --- Preview ---

@Preview(showBackground = true)
@Composable
fun OptionSelectorPreview() {
    MaterialTheme {
        val options = listOf(
            Pair("Basic Plan", "The ideal choice for single users or small projects."),
            Pair("Standard Plan", null), // Testing the null body text case
            Pair("Premium Plan", "Includes all features and priority support.")
        )

        var selectedOptionIndex by remember { mutableStateOf(1) }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            options.forEachIndexed { index, option ->
                OptionSelector(
                    label = option.first,
                    bodyText = option.second,
                    isSelected = index == selectedOptionIndex,
                    onClick = {
                        selectedOptionIndex = index
                    }
                )
            }
        }
    }
}