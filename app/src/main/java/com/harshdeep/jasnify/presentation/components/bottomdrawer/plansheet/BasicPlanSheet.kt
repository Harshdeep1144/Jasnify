package com.harshdeep.jasnify.presentation.components.bottomdrawer.plansheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun BasicPlanSheet(
    onDismiss: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    val benefits = listOf(
        "Manage 1 active event",
        "Standard venue & vendor catalog",
        "Core budget tracking tools",
        "Standard checklists",
        "Personal planning space"
    )

    CustomBottomSheet(
        heading = "Basic Plan",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        containerColor = Color(0xFFF4E3E2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "FREE",
                style = JasnifyTheme.typography.displaySmall,
                color = ContentPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Core features to get your event planning started.",
                style = JasnifyTheme.typography.bodyLarge,
                color = ContentSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "What's included:",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            benefits.forEach { benefit ->
                PlanBenefitItem(text = benefit)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            CustomTextButton(
                onClick = { onDismiss() },
                text = "Current Plan",
                enabled = false,
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlanBenefitItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_tick),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF006363)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = JasnifyTheme.typography.bodyMedium,
            color = ContentPrimary
        )
    }
}
