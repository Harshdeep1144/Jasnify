package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceAccent
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun StepperProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val progress = (currentStep.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
    val stepText = "Step $currentStep of $totalSteps"

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stepText,
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(SquircleShape(CornerExtraSmall, CornerSmoothingDefault)),
            color = ContentBrand,
            trackColor = SurfaceBrandSecondary,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
    }
}



@Preview(showBackground = true)
@Composable
fun StepperProgressBarPreview() {
    StepperProgressBar(
        currentStep = 2,
        totalSteps = 5
    )
}