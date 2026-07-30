package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTimeLineInfoSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onProgress: ((Float) -> Unit)? = null
) {
    CustomBottomSheet(
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = false,
        showCloseButton = false,
        headerBackgroundImage = {
            Image(
                painter = painterResource(id = R.drawable.bg_pattern_overlay_events_doodle),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.8f
            )
        }
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Illustration
        Image(
            painter = painterResource(id = R.drawable.img_hero_event_timeline),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

        // Feature List
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoFeatureItem(
                iconRes = R.drawable.img_p1_event_timeline,
                title = "Add multiple days & activities",
                description = "Each sub-event can be added with its own date & name."
            )
            InfoFeatureItem(
                iconRes = R.drawable.img_p2_event_timeline,
                title = "Assign vendors separately",
                description = "You can assign separate vendors for each sub-event by saving them separately."
            )
            InfoFeatureItem(
                iconRes = R.drawable.img_p3_event_timeline,
                title = "Manage separate catering",
                description = "You can add & manage separate catering menus for each sub-event."
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextButton(
            onClick = onDismiss,
            text = "Got it!",
            modifier = Modifier.fillMaxWidth(),
            shapeStyle = ButtonShapeStyle.Square
        )
    }
}

@Composable
private fun InfoFeatureItem(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val shape = SquircleShape(CornerLargeIncrease)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), shape)
            .background(SurfaceSecondary, shape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = JasnifyTheme.typography.headingMedium.copy(fontWeight = FontWeight.Medium),
                color = ContentBrandDark
            )
            Spacer(modifier = Modifier.height(4.dp))
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
private fun EventTimeLineInfoSheetPreview() {
    JasnifyTheme {
        Box(modifier = Modifier.background(Color.Gray)) {
            EventTimeLineInfoSheetContent(onDismiss = {})
        }
    }
}