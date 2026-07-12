package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentTertiary

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline.copy(0.16f),
    thickness: Float = 2f,
    dashLength: Float = 16f,
    gapLength: Float = 10f
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = thickness,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashLength, gapLength),
                0f
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashedDividerPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        DashedDivider()

        Spacer(modifier = Modifier.height(16.dp))

        DashedDivider(
            color = Color.Gray,
            dashLength = 12f,
            gapLength = 6f
        )
    }
}