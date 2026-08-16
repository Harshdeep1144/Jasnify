package com.harshdeep.jasnify.presentation.screens.main.tabs.guests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun GuestEmptyState(
    hasContactPermission: Boolean,
    onAddGuestClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
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
                    leadingIcon = if (hasContactPermission) painterResource(id = R.drawable.ic_plus) else null,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (hasContactPermission) "Select and Import multiple \n contacts at once." else "We need contacts \n access to show contacts.",
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentTertiary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}