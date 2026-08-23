package com.harshdeep.jasnify.presentation.components.states

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

private val ChecklistCardShape = SquircleShape(CornerLarge, CornerSmoothingDefault)

@Composable
fun ChecklistLoadingState(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush()
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundPrimary,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomTopBar(
                        title = "Checklist",
                        titleIcon = painterResource(R.drawable.ill_checklists),
                        menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                        isLeftAligned = true,
                        isLargeTitle = true,
                        secondaryIcon = TopIcon.Predefined.SEARCH,
                        onSecondaryClick = {},
                        onMenuClick = {},
                        buttonStyle = ButtonBackground.OPAQUE
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips Skeleton Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 54.dp, height = 36.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 36.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .size(width = 104.dp, height = 36.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(brush)
                )
            }

            // 2-Column Checklist Cards Grid Skeleton
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 12.dp
                    )
            ) {
                items(6) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(154.dp)
                            .clip(ChecklistCardShape)
                            .background(SurfacePrimary)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                shape = ChecklistCardShape
                            )
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Section: Title and Pin Placeholder
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(brush)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(brush)
                                )
                            }

                            // Middle Section: Checklist Items Placeholders
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(brush)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(brush)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(brush)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.55f)
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(brush)
                                    )
                                }
                            }

                            // Bottom Section: Date / Meta Indicator
                            Box(
                                modifier = Modifier
                                    .size(width = 64.dp, height = 10.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(brush)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Checklist Loading State Preview")
@Composable
private fun ChecklistLoadingStatePreview() {
    JasnifyTheme {
        ChecklistLoadingState()
    }
}