package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.*

@Composable
fun CustomChecker(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val color by animateColorAsState(
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
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            if (checked) {
                drawCircle(
                    color = color,
                    radius = size.minDimension / 2,
                    style = Fill
                )

                val path = Path().apply {
                    moveTo(size.width * 0.28f, size.height * 0.52f)
                    lineTo(size.width * 0.44f, size.height * 0.68f)
                    lineTo(size.width * 0.72f, size.height * 0.36f)
                }
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            } else {
                drawCircle(
                    color = color,
                    radius = size.minDimension / 2,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

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