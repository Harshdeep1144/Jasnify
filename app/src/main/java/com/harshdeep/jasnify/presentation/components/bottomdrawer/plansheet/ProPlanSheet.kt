package com.harshdeep.jasnify.presentation.components.bottomdrawer.plansheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

private val DefaultProBenefits = listOf(
    "Advanced AI recommendations",
    "Collaborative planning (Room Chat Access)",
    "Add expense directly through Receipt",
    "Exportable expense reports"
)

@Composable
fun ProPlanSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onProgress: ((Float) -> Unit)? = null,
    onUpgradeClick: () -> Unit = {},
    isUpgradeEnabled: Boolean = false,
    benefits: List<String> = remember { DefaultProBenefits }
) {
    CustomBottomSheet(
        heading = "Pro Plan",
        onDismiss = onDismiss,
        onProgress = onProgress,
        showDragHandle = false,
        sheetHeight = null,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "$5/month",
                style = JasnifyTheme.typography.displaySmall,
                color = ContentPrimary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Perfect for collaborative planning and smarter insights.",
                style = JasnifyTheme.typography.bodyLarge,
                color = ContentPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "What's included:",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentPrimary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            benefits.forEach { benefit ->
                PlanBenefitItem(text = benefit)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            CustomTextButton(
                onClick = onUpgradeClick,
                text = "Upgrade to Pro Plan",
                enabled = isUpgradeEnabled,
                shapeStyle = ButtonShapeStyle.Square,
                leadingIcon = painterResource(R.drawable.ic_lock),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PlanBenefitItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_tick),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = JasnifyTheme.typography.bodyMedium,
            color = ContentPrimary
        )
    }
}