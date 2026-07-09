package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.Neutral100
import com.harshdeep.jasnify.theme.Neutral200
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTimeLineInfoSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfacePrimary,
        scrimColor = Color.Black.copy(alpha = 0.8f),
        dragHandle = null,
        shape = SquircleShape(topStart = CornerExtraLarge, topEnd = CornerExtraLarge)
    ) {
        EventTimeLineInfoSheetContent(
            onDismiss = onDismiss,
            modifier = modifier
        )
    }
}

@Composable
fun EventTimeLineInfoSheetContent(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(SurfacePrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Background Pattern
            Image(
                painter = painterResource(R.drawable.bg_pattern_overlay_event_timeline),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.8f
            )
            // Drag Handle
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = 8.dp)
                    .width(56.dp)
                    .height(4.dp)
                    .background(ContentTertiary, shape = RoundedCornerShape(100))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    // 3D Illustration
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_event_timeline),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )
                    Text(
                        text = "Event Timeline",
                        style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                        color = ContentPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Plan and organize events that happen across multiple days, like Mehendi Ceremony, Haldi & Sangeet, etc in Indian Weddings.",
                        style = JasnifyTheme.typography.bodyLarge,
                        color = ContentSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                // Feature List
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoFeatureItem(
                        iconRes = R.drawable.img_p1_event_timeline,
                        title = "Add multiple days & activities",
                        description = "Each sub-event can be added with its own date & name."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoFeatureItem(
                        iconRes = R.drawable.img_p2_event_timeline,
                        title = "Assign vendors separately",
                        description = "You can assign separate vendors for each sub-event by saving them separately."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoFeatureItem(
                        iconRes = R.drawable.img_p3_event_timeline,
                        title = "Manage separate catering",
                        description = "You can add & manage separate catering menus for each sub-event."
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CustomTextButton(
                    onClick = onDismiss,
                    text = "Got it!",
                    modifier = Modifier.fillMaxWidth(),
                    shapeStyle = ButtonShapeStyle.Square
                )
            }
        }
    }
}

@Composable
private fun InfoFeatureItem(
    iconRes: Int,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceSecondary, SquircleShape(CornerLargeIncrease))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(ContentInvPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = JasnifyTheme.typography.headingMedium.copy(fontWeight = FontWeight.Medium),
                color = ContentBrandDark
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                style = JasnifyTheme.typography.bodyMedium,
                color = ContentSecondary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EventTimeLineInfoSheetPreview() {
    JasnifyTheme {
        Box(modifier = Modifier.background(Color.Gray)) {
            EventTimeLineInfoSheetContent(onDismiss = {})
        }
    }
}
