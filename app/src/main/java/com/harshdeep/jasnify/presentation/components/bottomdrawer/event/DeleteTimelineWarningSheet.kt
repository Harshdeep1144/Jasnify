package com.harshdeep.jasnify.presentation.components.bottomdrawer.event

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.InfoCard
import com.harshdeep.jasnify.presentation.components.cards.InfoCardNature
import com.harshdeep.jasnify.presentation.components.others.InfoTooltip
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteTimelineWarningSheet(
    onDismiss: () -> Unit,
    venueCount: Int,
    vendorCount: Int,
    onReviewVenues: () -> Unit,
    onReviewVendors: () -> Unit,
    modifier: Modifier = Modifier,
    onProgress: ((Float) -> Unit)? = null
) {
    CustomBottomSheet(
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = true,
        showCloseButton = false
    ) {
        DeleteTimelineWarningContent(
            onDismiss = onDismiss,
            venueCount = venueCount,
            vendorCount = vendorCount,
            onReviewVenues = onReviewVenues,
            onReviewVendors = onReviewVendors,
        )
    }
}

@Composable
fun DeleteTimelineWarningContent(
    onDismiss: () -> Unit,
    venueCount: Int,
    vendorCount: Int,
    onReviewVenues: () -> Unit,
    onReviewVendors: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(SurfacePrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TopBarIconButton(
                backgroundStyle = ButtonBackground.OPAQUE,
                icon = TopIcon.Predefined.CLOSE,
                iconSize = 18.dp,
                onClick = onDismiss,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 0.dp, 12.dp, 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_alert_triangle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Attention Required!",
                style = JasnifyTheme.typography.displayLarge,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "We found that you already have some plans on this timeline.",
                style = JasnifyTheme.typography.bodyLarge,
                color = ContentSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Warning Info Box
            InfoCard(
                message = "To delete the timeline, you must modify or remove the following associated plans.",
                nature = InfoCardNature.Negative
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (venueCount > 0) {
                    val venueTooltip = if (venueCount == 1) {
                        "Review & remove the 1 venue saved from the timeline."
                    } else {
                        "Review & remove all $venueCount venues saved from the timeline."
                    }

                    ReviewCard(
                        count = venueCount,
                        label = if (venueCount == 1) "Venue\nSaved" else "Venues\nSaved",
                        iconRes = R.drawable.ic_home,
                        tooltipText = venueTooltip,
                        onReview = onReviewVenues,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (vendorCount > 0) {
                    val vendorTooltip = if (vendorCount == 1) {
                        "Review & remove the 1 vendor saved from the timeline."
                    } else {
                        "Review & remove all $vendorCount vendors saved from the timeline."
                    }

                    ReviewCard(
                        count = vendorCount,
                        label = if (vendorCount == 1) "Vendor\nSaved" else "Vendors\nSaved",
                        iconRes = R.drawable.ic_vendor,
                        tooltipText = vendorTooltip,
                        onReview = onReviewVendors,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ReviewCard(
    count: Int,
    label: String,
    iconRes: Int,
    tooltipText: String,
    onReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTooltip by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(SquircleShape(CornerLargeIncrease))
            .background(SurfaceSecondary)
            .border(2.dp, MaterialTheme.colorScheme.error, SquircleShape(CornerLargeIncrease))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = String.format("%02d", count),
                    style = JasnifyTheme.typography.displayMedium,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 24.sp,
                    color = ContentPrimary
                )
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = ContentPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "Info",
                    tint = ContentPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showTooltip = true
                        }
                )

                InfoTooltip(
                    visible = showTooltip,
                    tooltipText = tooltipText,
                    onDismiss = { showTooltip = false }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = JasnifyTheme.typography.labelLarge,
            color = ContentPrimary,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextButton(
            onClick = onReview,
            text = "Review Now",
            trailingIcon = painterResource(R.drawable.ic_right),
            containerColor = MaterialTheme.colorScheme.error,
            size = ButtonSize.Small,
            shapeStyle = ButtonShapeStyle.Square,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DeleteTimelineWarningSheetPreview() {
    JasnifyTheme {
        DeleteTimelineWarningContent(
            onDismiss = {},
            venueCount = 1,
            vendorCount = 7,
            onReviewVenues = {},
            onReviewVendors = {}
        )
    }
}