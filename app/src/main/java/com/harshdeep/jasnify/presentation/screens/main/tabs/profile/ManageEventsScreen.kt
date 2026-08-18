package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserEvent
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.ManageEventCard
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary

@Composable
fun ManageEventsScreen(
    userProfile: User?,
    eventViewModel: EventViewModel,
    mainNavController: NavHostController,
    ownedEvents: List<Event>,
    onBack: () -> Unit,
    onEventClick: (String) -> Unit,
    onJoinOrCreateClick: () -> Unit,
    onShowMenu: (UserEvent) -> Unit
) {
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val isUserEventsLoading by eventViewModel.isUserEventsLoading.collectAsStateWithLifecycle()
    val joinedEvents = userProfile?.joinedEvents ?: emptyList()

    // Merge owned events for old accounts that don't have joinedEvents populated
    val allUserEvents = remember(ownedEvents, joinedEvents) {
        val ownedAsUserEvents = ownedEvents.map { event ->
            UserEvent(
                eventId = event.id,
                eventName = event.name,
                adminId = event.ownerId,
                roomRoles = mapOf(
                    "Budget" to UserRole.OWNER,
                    "Catering" to UserRole.OWNER,
                    "Checklist" to UserRole.OWNER,
                    "Vendors" to UserRole.OWNER,
                    "Venue" to UserRole.OWNER
                )
            )
        }
        // Deduplicate: Prioritize joinedEvents as they have more granular role/screen info if shared
        (ownedAsUserEvents + joinedEvents).distinctBy { it.eventId }
    }

    // Redirect to event creation if no events found and loading is complete
    LaunchedEffect(allUserEvents, isUserEventsLoading, userProfile) {
        if (!isUserEventsLoading && userProfile != null && allUserEvents.isEmpty()) {
            mainNavController.navigate(Screen.OnboardingType.route)
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Manage Events",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundPrimary
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (allUserEvents.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = ContentSecondary)
                    Text(
                        text = "Redirecting to event creation...",
                        color = ContentSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        top = 12.dp,
                        end = 12.dp,
                        bottom = 96.dp // Extra space so items aren't occluded by the floating button
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allUserEvents) { userEvent ->
                        ManageEventCard(
                            userEvent = userEvent,
                            isActive = userEvent.eventId == activeEventId,
                            isAdmin = userProfile?.uid == userEvent.adminId,
                            onEventClick = { onEventClick(userEvent.eventId) },
                            onMenuClick = {
                                onShowMenu(userEvent)
                            }
                        )
                    }
                }
            }

            // Floating action container at the bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(brush = BottomGradientBrush)
                    .navigationBarsPadding()
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(62.dp)
                        .pill360Shadow(
                            ambientColor = Color.Black.copy(alpha = 0.10f),
                            ambientBlur = 12.dp,
                            ambientSpread = 2.dp,
                            spotColor = Color.Black.copy(alpha = 0.15f),
                            spotBlur = 18.dp,
                            spotOffsetY = 4.dp
                        ),
                    color = SurfacePrimary,
                    shape = CircleShape
                ) {
                    CustomTextButton(
                        onClick = onJoinOrCreateClick,
                        text = "Join or Create Event",
                        type = ButtonType.Primary,
                        shapeStyle = ButtonShapeStyle.Round,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}