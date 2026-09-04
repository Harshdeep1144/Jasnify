package com.harshdeep.jasnify.presentation.components.ai

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun FormattedAiText(
    text: String,
    modifier: Modifier = Modifier
) {
    val baseStyle = JasnifyTheme.typography.labelLarge

    val annotatedString = remember(text, baseStyle) {
        buildAnnotatedString {
            val pattern = Regex("""\*\*(.*?)\*\*""")
            var lastIndex = 0

            pattern.findAll(text).forEach { matchResult ->
                val range = matchResult.range

                if (range.first > lastIndex) {
                    val normalText = text.substring(lastIndex, range.first)
                    withStyle(
                        style = SpanStyle(
                            fontWeight = baseStyle.fontWeight ?: FontWeight.Normal,
                            color = ContentPrimary
                        )
                    ) {
                        append(normalText)
                    }
                }

                val boldContent = matchResult.groupValues[1]
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E2022)
                    )
                ) {
                    append(boldContent)
                }

                lastIndex = range.last + 1
            }

            if (lastIndex < text.length) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = baseStyle.fontWeight ?: FontWeight.Normal,
                        color = ContentPrimary
                    )
                ) {
                    append(text.substring(lastIndex))
                }
            }
        }
    }

    Text(
        text = annotatedString,
        style = baseStyle,
        modifier = modifier.fillMaxWidth()
    )
}
