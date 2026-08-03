package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.harshdeep.jasnify.presentation.components.states.FullCardLoading
import com.harshdeep.jasnify.presentation.components.states.SkeletonMenuCategoryCard
import com.harshdeep.jasnify.presentation.components.states.shimmerBrush
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

/**
 * A wrapper component that handles room access verification.
 * It shows the room content optimistically while verifying access, or an access denied screen if rejected.
 */
@Composable
fun RoomAccessGuardian(
    hasAccess: Boolean?,
    roomName: String,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit
) {
    when (hasAccess) {
        true, null -> content() // Allow optimistic rendering while access is verified
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
    }
}

/**
 * A dedicated loading state for room access verification.
 * Intended to be used internally by screens when data is fetching.
 */
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

@Preview(showBackground = true)
@Composable
private fun RoomAccessGuardianPreview() {
    JasnifyTheme {
        RoomAccessGuardian(
            hasAccess = false,
            roomName = "Budget",
            onBackClick = {},
            content = {}
        )
    }
}
