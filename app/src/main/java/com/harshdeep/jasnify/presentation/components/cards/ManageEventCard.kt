package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.UserEvent
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
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
    var isExpanded by remember { mutableStateOf(false) }

    // --- ANIMATION STATES ---
    var animTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(isActive) {
        animTriggered = isActive
    }

    // Badge Slide Down Animation (Starts hidden behind main card and slides down to 0.dp)
    val badgeOffsetY by animateDpAsState(
        targetValue = if (isActive && animTriggered) 0.dp else (-26).dp,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "CurrentEventBadgeSlideAnimation"
    )

    // Badge Fade In Animation
    val badgeAlpha by animateFloatAsState(
        targetValue = if (isActive && animTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "CurrentEventBadgeFadeAnimation"
    )

    val cardShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)

    Box(modifier = modifier.fillMaxWidth()) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = badgeOffsetY) // Slides down from behind the card
                    .alpha(badgeAlpha)        // Fades in simultaneously
                    .background(
                        color = SurfaceBrandPrimary,
                        shape = cardShape
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "CURRENT EVENT",
                    color = ContentInvPrimary,
                    style = JasnifyTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                )
            }
        }

        // 2. Main Card Surface (placed ON TOP of badge, covering top portion of badge)
        Surface(
            onClick = { if (!isActive) onEventClick() },
            enabled = !isActive,
            color = SurfacePrimary,
            shape = cardShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isActive) 26.dp else 0.dp) // Exposes bottom badge when active
                .border(width = if (isActive) 2.dp else 1.dp, color = if (isActive) ContentBrand else MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), cardShape)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header: Title and Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = userEvent.eventName,
                        style = JasnifyTheme.typography.displaySmall.copy(fontWeight = FontWeight.Medium),
                        color = ContentPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    TopBarIconButton(
                        icon = TopIcon.Predefined.MENU_VERTICAL,
                        onClick = onMenuClick
                    )
                }

                // Expandable Section
                Surface(
                    color = SurfaceSecondary,
                    shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .noRippleClickable {
                                isExpanded = !isExpanded
                            }
                            .padding(12.dp)
                    ) {
                        // Expandable Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isAdmin) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_shield),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = ContentPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Owner",
                                        style = JasnifyTheme.typography.labelLarge,
                                        color = ContentPrimary
                                    )
                                } else {
                                    Text(
                                        text = "Room Access",
                                        style = JasnifyTheme.typography.labelLarge,
                                        color = ContentPrimary
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = ContentPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                if (isAdmin) {
                                    Surface(
                                        color = SurfacePrimary,
                                        shape = SquircleShape(CornerMedium, CornerSmoothingDefault),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "You have full Access",
                                            modifier = Modifier
                                                .padding(12.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            style = JasnifyTheme.typography.labelLarge,
                                            color = ContentPrimary
                                        )
                                    }
                                } else {
                                    userEvent.roomRoles.forEach { (room, role) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = room,
                                                style = JasnifyTheme.typography.labelLarge,
                                                color = ContentSecondary
                                            )
                                            Text(
                                                text = role.name.lowercase().replaceFirstChar { it.uppercase() },
                                                style = JasnifyTheme.typography.labelLarge,
                                                color = ContentSecondary
                                            )
                                        }
                                        if (room != userEvent.roomRoles.keys.last()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------- Previews ----------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFFEBEBEB)
@Composable
fun ManageEventCardPreview() {
    val sampleEvent = UserEvent(
        eventId = "1",
        eventName = "Taylor & Travis’s Wedding",
        roomRoles = mapOf(
            "Budget Tracker" to UserRole.EDITOR,
            "Catering Menu" to UserRole.VIEWER,
            "Venue" to UserRole.EDITOR,
            "Guests & Cards" to UserRole.EDITOR,
            "Moments" to UserRole.VIEWER
        )
    )

    JasnifyTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Current Active Event - Non-Admin (Room Access)
            ManageEventCard(
                userEvent = sampleEvent,
                isActive = true,
                isAdmin = false,
                onEventClick = {},
                onMenuClick = {}
            )

            // 2. Current Active Event - Admin (Owner)
            ManageEventCard(
                userEvent = sampleEvent,
                isActive = true,
                isAdmin = true,
                onEventClick = {},
                onMenuClick = {}
            )

            // 3. Inactive Event - Non-Admin
            ManageEventCard(
                userEvent = sampleEvent,
                isActive = false,
                isAdmin = false,
                onEventClick = {},
                onMenuClick = {}
            )

            // 4. Inactive Event - Admin (Owner)
            ManageEventCard(
                userEvent = sampleEvent,
                isActive = false,
                isAdmin = true,
                onEventClick = {},
                onMenuClick = {}
            )
        }
    }
}