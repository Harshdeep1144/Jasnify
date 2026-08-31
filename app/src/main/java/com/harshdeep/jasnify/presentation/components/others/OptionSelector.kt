package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.CustomRadioButton
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun OptionSelector(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bodyText: String? = null,
    trailingText: String? = null
) {
    val backgroundColor = if (isSelected) SurfaceBrandSecondary else Color.Transparent
    val contentColor = if (isSelected) ContentBrandDark else ContentSecondary
    val bodyTextColor = if (isSelected) ContentBrandDark else ContentSecondary

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
        color = backgroundColor,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomRadioButton(
                selected = isSelected,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = JasnifyTheme.typography.headingLarge,
                    color = contentColor
                )

                if (!bodyText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = bodyText,
                        style = JasnifyTheme.typography.bodyMedium,
                        color = bodyTextColor
                    )
                }
            }

            if (!trailingText.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = trailingText,
                    style = JasnifyTheme.typography.headingLarge,
                    color = contentColor
                )
            }
        }
    }
}