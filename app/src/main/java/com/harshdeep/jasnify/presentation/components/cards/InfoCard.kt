package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

enum class InfoCardNature{
    Neutral,
    Positive,
    Negative
}

data class InfoCardColors(
    val backgroundColor: Color,
    val borderColor: Color
)

fun getButtonColors(nature: InfoCardNature): InfoCardColors {
    return when (nature) {
        InfoCardNature.Neutral -> InfoCardColors(
            Color(0xFFDAECFB),
            Color(0xFF1474B8)
        )
        InfoCardNature.Negative -> InfoCardColors(
            Color(0xFFF9DEDC),
            Color(0xFFB3261E)
        )
        InfoCardNature.Positive -> InfoCardColors(
            Color(0xFFD4F2D9),
            Color(0xFF37B24C)
        )
    }
}


@Composable
fun InfoCard(
    message: String,
    nature: InfoCardNature,
    modifier: Modifier = Modifier
) {

    val currentColors = getButtonColors(nature = nature)

    Box(
        modifier = modifier
            .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
            .border(
                width = 1.dp,
                color = currentColors.borderColor,
                shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
            )
            .background(currentColors.backgroundColor)
    ){
        Row(
            modifier = Modifier
                .height(52.dp)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = currentColors.borderColor
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = message,
                style = JasnifyTheme.typography.labelLarge,
                fontWeight = FontWeight.Light,
                color = currentColors.borderColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoCard(
            message = "Write the message here.",
            nature = InfoCardNature.Neutral
        )
        InfoCard(
            message = "Write the message here.",
            nature = InfoCardNature.Positive
        )
        InfoCard(
            message = "Write the message here.",
            nature = InfoCardNature.Negative
        )
    }
}