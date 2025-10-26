package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.Pattaya
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.ContentInvPrimary

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    alpha: Float
) {
    val containerColor = BackgroundSecondary.copy(alpha = alpha)
    val contentColor = if (alpha < 0.5f) ContentInvPrimary else ContentPrimary.copy(alpha = 1f)
    val buttonBackground = if (alpha > 0.5f) ButtonBackground.OPAQUE else ButtonBackground.TRANSLUCENT

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(containerColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "Jasnify",
            style = JasnifyTheme.typography.displayLarge,
            fontFamily = Pattaya,
            color = ContentPrimary,
        )

        TopBarIconButton(
            icon = TopIcon.Predefined.MENU_HORIZONTAL,
            onClick = { },
            backgroundStyle = buttonBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeTopBarPreview() {
    JasnifyTheme {
        Column(Modifier.background(Color.DarkGray)) {
            HomeTopBar(alpha = 0f)
            HomeTopBar(alpha = 1f)
        }
    }
}