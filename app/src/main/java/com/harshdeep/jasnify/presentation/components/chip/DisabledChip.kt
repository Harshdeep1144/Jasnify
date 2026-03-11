package com.harshdeep.jasnify.presentation.components.chip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun DisabledChip(
    label: String,
    modifier: Modifier = Modifier,
    shapeStyle: ChipShapeStyle = ChipShapeStyle.Square
) {
    val shape: Shape = when (shapeStyle) {
        ChipShapeStyle.Square -> SquircleShape(CornerMedium, CornerSmoothingDefault)
        ChipShapeStyle.Round -> CircleShape
    }

    // Static styling for disabled state
    val containerColor = SurfaceSecondary
    val contentColor = ContentTertiary
    val style = JasnifyTheme.typography.labelLarge

    // Surface without onClick makes it non-clickable
    Surface(
        modifier = modifier.height(36.dp),
        shape = shape,
        color = containerColor,
        contentColor = contentColor
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = style,
                color = ContentSecondary
            )
        }
    }
}

// --------- Previews ----------

@Preview(showBackground = true, name = "DisabledChip Variations")
@Composable
private fun DisabledChipPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Disabled Chip (Square)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        DisabledChip(label = "Services")

        Spacer(Modifier.height(8.dp))

        Text("Disabled Chip (Round)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        DisabledChip(label = "Decorations", shapeStyle = ChipShapeStyle.Round)
    }
}