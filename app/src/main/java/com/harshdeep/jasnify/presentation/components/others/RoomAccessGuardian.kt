package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.sections.FullCardLoading
import com.harshdeep.jasnify.presentation.components.sections.shimmerBrush
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmall
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

@Composable
fun RoomAccessGuardian(
    hasAccess: Boolean?,
    roomName: String,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit
) {
    when (hasAccess) {
        true -> content()
        false -> {
            Scaffold(
                containerColor = BackgroundPrimary,
                topBar = {
                    Column(modifier = Modifier.statusBarsPadding()) {
                        CustomTopBar(
                            onBackClick = onBackClick,
                            backIcon = TopIcon.Predefined.BACK,
                            buttonStyle = ButtonBackground.OPAQUE
                        )
                    }
                },
                bottomBar = {
                    Column(modifier = Modifier.navigationBarsPadding()) {
                        CustomTextButton(
                            text = "Back to Home",
                            onClick = onBackClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            shapeStyle = ButtonShapeStyle.Square
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ill_block_access),
                            contentDescription = "Access Denied",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(100.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Oops, you don't have access to $roomName!",
                            style = JasnifyTheme.typography.displayMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
        null -> {
            RoomAccessLoading(roomName = roomName, onBackClick = onBackClick)
        }
    }
}

@Composable
fun RoomAccessLoading(
    roomName: String,
    onBackClick: () -> Unit
) {
    val brush = shimmerBrush()
    
    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = roomName,
                    onBackClick = onBackClick,
                    backIcon = TopIcon.Predefined.BACK,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            item {
                // Search Bar Shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .background(brush)
                )
            }
            
            when (roomName) {
                "Venue", "Vendors" -> {
                    items(3) {
                        FullCardLoading(shimmerBrush = brush)
                        Spacer(Modifier.height(12.dp))
                    }
                }
                "Catering" -> {
                    items(3) {
                        SkeletonMenuCategoryCard(brush = brush)
                    }
                }
                "Budget" -> {
                    item {
                        // Budget Summary Card Shimmer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(SquircleShape(CornerExtraLarge, CornerSmoothingDefault))
                                .background(brush)
                        )
                    }
                    items(5) {
                        // Expense Card Shimmer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                                .background(brush)
                        )
                    }
                }
                else -> {
                    items(8) {
                        // Generic List/Checklist Shimmer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                                .background(brush)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SkeletonMenuCategoryCard(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = SquircleShape(CornerExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = ContentTertiary.copy(alpha = 0.1f)
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(28.dp)
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(CornerLarge))
                    .background(brush)
            )
            DashedDivider(
                color = MaterialTheme.colorScheme.outline.copy(0.16f),
                dashLength = 20f,
                gapLength = 6f
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(CornerSmall))
                            .background(brush)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomAccessGuardianPreview() {
    JasnifyTheme {
        RoomAccessGuardian(
            hasAccess = false,
            roomName = "Lounge",
            onBackClick = {},
            content = {}
        )
    }
}
