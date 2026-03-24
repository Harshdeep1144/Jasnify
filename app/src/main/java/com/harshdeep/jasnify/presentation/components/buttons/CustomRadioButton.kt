package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.*

@Composable
fun CustomRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val dotRadius by animateDpAsState(
        targetValue = if (selected) 6.dp else 0.dp,
        animationSpec = tween(durationMillis = 150),
        label = "DotRadius"
    )

    val color by animateColorAsState(
        targetValue = when {
            !enabled -> ContentTertiary
            selected -> ContentBrandDark
            else -> ContentSecondary
        },
        label = "RadioColor"
    )

    val selectableModifier = if (onClick != null) {
        Modifier.selectable(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        )
    } else Modifier

    Box(
        modifier = modifier
            .then(selectableModifier)
            .size(24.dp)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val strokeWidth = 2.dp.toPx()

            drawCircle(
                color = color,
                radius = size.minDimension / 2,
                style = Stroke(width = strokeWidth)
            )

            if (selected) {
                drawCircle(
                    color = color,
                    radius = dotRadius.toPx(),
                    style = Fill
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomRadioButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Enabled States
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CustomRadioButton(selected = true, onClick = {}, enabled = true)
            CustomRadioButton(selected = false, onClick = {}, enabled = true)
        }
        // Disabled States
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CustomRadioButton(selected = true, onClick = {}, enabled = false)
            CustomRadioButton(selected = false, onClick = {}, enabled = false)
        }
    }
}