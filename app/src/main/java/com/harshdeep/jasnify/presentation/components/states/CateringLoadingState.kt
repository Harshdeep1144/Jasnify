package com.harshdeep.jasnify.presentation.components.states

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun CateringLoadingState(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(4) {
            SkeletonMenuCategoryCard(brush = brush)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CateringLoadingStatePreview() {
    JasnifyTheme {
        CateringLoadingState()
    }
}
