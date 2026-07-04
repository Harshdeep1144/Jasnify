package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun OrDivider(
    modifier: Modifier = Modifier,
    text: String? = null,
    divider: Boolean? = null,
    dividerGap: Dp = 16.dp
) {
    val showDivider = divider ?: true // default = show divider
    val textValue = text ?: "OR"

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(top = dividerGap, bottom = dividerGap)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (showDivider) {
                Divider(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(0.9.dp),
                    color = MaterialTheme.colorScheme.outline.copy(0.16f)
                )
            }

            Text(
                modifier = Modifier.padding(8.dp),
                text = textValue,
                color = ContentTertiary,
                style = JasnifyTheme.typography.bodyLarge
            )

            if (showDivider) {
                Divider(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(0.9.dp),
                    color = MaterialTheme.colorScheme.outline.copy(0.16f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrDividerPreview() {
    androidx.compose.foundation.layout.Column {
        OrDivider()

        OrDivider(text = "AND")

        OrDivider(divider = false)
    }
}
