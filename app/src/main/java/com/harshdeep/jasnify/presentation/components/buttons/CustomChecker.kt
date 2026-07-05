package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.R

@Composable
fun CustomChecker(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Animate the active container or outline color dynamically
    val animatedColor by animateColorAsState(
        targetValue = when {
            !enabled -> ContentTertiary
            checked -> ContentBrandDark
            else -> ContentSecondary
        },
        label = "CheckerColor"
    )

    val toggleableModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange,
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        )
    } else Modifier

    Box(
        modifier = modifier
            .then(toggleableModifier)
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
            .border(
                width = if (checked) 0.dp else 1.5.dp,
                color = if (checked) Color.Transparent else animatedColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(R.drawable.ic_tick),
                contentDescription = "Selected",
                tint = if (enabled) animatedColor else ContentSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


// --------------------------------------------- Preview ------------------------------------

@Preview(showBackground = true)
@Composable
private fun CustomCheckerPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Enabled States
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CustomChecker(checked = true, onCheckedChange = {}, enabled = true)
            CustomChecker(checked = false, onCheckedChange = {}, enabled = true)
        }
        // Disabled States
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CustomChecker(checked = true, onCheckedChange = {}, enabled = false)
            CustomChecker(checked = false, onCheckedChange = {}, enabled = false)
        }
    }
}