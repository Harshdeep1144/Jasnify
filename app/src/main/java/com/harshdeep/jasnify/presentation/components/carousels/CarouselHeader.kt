package com.harshdeep.jasnify.presentation.components.carousels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme

enum class SubtitlePosition {
    ABOVE, BELOW
}

@Composable
fun CarouselHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    subtitlePosition: SubtitlePosition = SubtitlePosition.BELOW,
    onSeeAllClick: (() -> Unit)? = null,
    isLoading: Boolean = false
) {
    val facadFluxBold = FontFamily(Font(R.font.facadflux_bold))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            if (subtitlePosition == SubtitlePosition.ABOVE && !subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle.uppercase(),
                    style = JasnifyTheme.typography.labelSmall,
                    color = ContentSecondary,
                    letterSpacing = 2.sp
                )
            }

            Text(
                text = title.uppercase(),
                style = JasnifyTheme.typography.displayLarge.copy(
                    fontFamily = facadFluxBold,
                ),
                fontWeight = FontWeight.SemiBold,
                color = ContentPrimary
            )

            if (subtitlePosition == SubtitlePosition.BELOW && !subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle.uppercase(),
                    style = JasnifyTheme.typography.labelSmall,
                    color = ContentSecondary,
                    letterSpacing = 2.sp
                )
            }
        }

        if (onSeeAllClick != null && !isLoading) {
            CustomIconButton(
                onClick = onSeeAllClick,
                icon = painterResource(R.drawable.ic_arrow_right),
                size = ButtonSize.Small,
                type = ButtonType.Secondary,
                modifier = Modifier.width(60.dp)
            )
        }
    }
}
