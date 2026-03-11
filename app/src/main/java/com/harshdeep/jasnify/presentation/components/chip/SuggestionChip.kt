package com.harshdeep.jasnify.presentation.components.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ChipColors
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfaceAccent
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape
import androidx.compose.material3.SuggestionChipDefaults

@Composable
fun CustomSuggestionChip(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(text) },
        modifier = modifier,
        enabled = enabled,
        shape = SquircleShape(CornerMedium, CornerSmoothingDefault),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = SurfaceAccent,
            labelColor = ContentBrand,
            disabledContainerColor = SurfaceSecondary,
            disabledLabelColor = ContentSecondary
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) ContentBrand else Color.Transparent
        )
    )
}


@Preview(showBackground = true)
@Composable
fun SuggestionChipPreview() {
    Column (
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        var isEnabled by remember { mutableStateOf(true) }

        CustomSuggestionChip(
            text = "Label",
            enabled = isEnabled,
            onClick = { isEnabled = !isEnabled }
        )
        CustomSuggestionChip(
            text = "Label",
            enabled = true,
            onClick = {  }
        )
    }

}
