package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.buttons.CustomRadioButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

@Composable
fun CustomCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isMultiSelect: Boolean = true
) {
    val textColor = animateColorAsState(
        targetValue = if (checked) ContentPrimary else ContentSecondary,
        label = "TextColorAnimation"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(SquircleShape(CornerLarge))
            .background(Color.Transparent)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onCheckedChange(!checked)
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = if (enabled) textColor.value else ContentSecondary,
            style = JasnifyTheme.typography.headingLarge
        )

        if (isMultiSelect) {
            CustomChecker(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        } else {
            CustomRadioButton(
                selected = checked,
                onClick = null, // Nullified to avoid redundant click handling since Row manages it
                enabled = enabled
            )
        }
    }
}




// ---------- Preview ----------------

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun CustomCheckboxPreview() {
    Column(
        modifier = Modifier
            .background(Color(0xFFE2E2E2), shape = RoundedCornerShape(8.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Multi-select / Checkbox States
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CustomCheckbox(
                text = "Multi-select Unchecked",
                checked = false,
                isMultiSelect = true,
                onCheckedChange = {}
            )

            CustomCheckbox(
                text = "Multi-select Checked",
                checked = true,
                isMultiSelect = true,
                onCheckedChange = {}
            )
        }

        // Single-select / Radio States
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CustomCheckbox(
                text = "Single-select Unselected",
                checked = false,
                isMultiSelect = false,
                onCheckedChange = {}
            )

            CustomCheckbox(
                text = "Single-select Selected",
                checked = true,
                isMultiSelect = false,
                onCheckedChange = {}
            )
        }
    }
}