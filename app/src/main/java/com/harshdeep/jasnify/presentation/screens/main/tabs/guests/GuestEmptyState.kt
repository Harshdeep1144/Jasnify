package com.harshdeep.jasnify.presentation.screens.main.tabs.guests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun GuestEmptyState(
    hasContactPermission: Boolean,
    onAddGuestClick: () -> Unit,
    onAddManuallyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = null,
                    tint = ContentTertiary,
                    modifier = Modifier.size(84.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No guests to invite",
                    style = JasnifyTheme.typography.displayMedium.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ContentTertiary
                )
                Spacer(modifier = Modifier.height(24.dp))

                CustomTextButton(
                    onClick = onAddGuestClick,
                    text = if (hasContactPermission) "Add Guests" else "Allow Contacts Access",
                    containerColor = if(!hasContactPermission) ContentPrimary else null,
                    leadingIcon = if (hasContactPermission) painterResource(id = R.drawable.ic_plus) else null,
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (hasContactPermission) {
                        "Select and Import multiple \n contacts at once."
                    } else {
                        "We need contacts \n access to show contacts."
                    },
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentTertiary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(16.dp))

                if(!hasContactPermission){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "or",
                            style = JasnifyTheme.typography.labelMedium,
                            color = ContentTertiary
                        )
                        Text(
                            text = "Add Guests Manually",
                            style = JasnifyTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                textDecoration = TextDecoration.Underline
                            ),
                            color = ContentBrandDark,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onAddManuallyClick
                            )
                        )
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun GuestEmptyStatePreview() {
    JasnifyTheme {
        GuestEmptyState(
            hasContactPermission = true,
            onAddGuestClick = {},
            onAddManuallyClick = {}
        )
    }
}