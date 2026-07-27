package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.UserEvent
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun ManageEventCard(
    userEvent: UserEvent,
    isActive: Boolean,
    isAdmin: Boolean,
    onEventClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onEventClick,
        color = if (isActive) SurfaceSecondary else SurfacePrimary,
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userEvent.eventName,
                        style = JasnifyTheme.typography.labelXLarge.copy(fontWeight = FontWeight.Bold),
                        color = ContentPrimary
                    )
                    Text(
                        text = if (isAdmin) "Admin / Owner" else "Member",
                        style = JasnifyTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isActive) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = "Active Event",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    TopBarIconButton(
                        icon = TopIcon.Predefined.MENU_VERTICAL,
                        onClick = onMenuClick
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Room Access:",
                    style = JasnifyTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ContentSecondary
                )
                userEvent.roomRoles.forEach { (room, role) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = room,
                            style = JasnifyTheme.typography.labelSmall,
                            color = ContentSecondary
                        )
                        Text(
                            text = role.name,
                            style = JasnifyTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = if (role == UserRole.OWNER) MaterialTheme.colorScheme.primary else ContentPrimary
                        )
                    }
                }
            }

            if (!isActive) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextButton(
                        onClick = onEventClick,
                        text = "Switch to",
                        size = ButtonSize.Small,
                        type = ButtonType.Secondary
                    )
                }
            }
        }
    }
}
