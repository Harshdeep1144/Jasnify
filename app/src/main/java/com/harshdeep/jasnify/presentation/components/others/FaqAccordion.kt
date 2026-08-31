package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun FaqAccordion(
    question: String,
    answer: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "rotation")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
            .background(SurfaceSecondary)
            .noRippleClickable { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = question,
                style = JasnifyTheme.typography.headingLarge,
                color = ContentPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = ContentPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationState)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = answer,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentPrimary
                )
            }
        }
    }
}
