package com.harshdeep.jasnify.presentation.components.bottomdrawer.event

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.models.eventTypes
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

private val EnterIdInputShape = SquircleShape(
    topStart = CornerExtraSmall,
    topEnd = CornerLarge,
    bottomStart = CornerLarge,
    bottomEnd = CornerLarge,
    cornerSmoothing = CornerSmoothingDefault
)

private val EventIconContainerShape = SquircleShape(CornerLargeIncrease)
private val EventIdBadgeShape = SquircleShape(100, CornerSmoothingDefault)

/**
 * Bottom sheet for choosing between creating a new event or joining with an ID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinOrCreateBottomSheet(
    onDismiss: () -> Unit,
    onCreateNewEvent: () -> Unit,
    onJoinWithId: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    CustomBottomSheet(
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = false,
        showCloseButton = false,
        closeButtonBackgroundStyle = ButtonBackground.TRANSLUCENT
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CustomTextButton(
                onClick = onCreateNewEvent,
                text = "Create a New Event",
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth()
            )

            OrDivider(text = "OR", divider = false)

            CustomTextButton(
                onClick = onJoinWithId,
                text = "Join with Event ID",
                type = ButtonType.Secondary,
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

enum class JoinEventSheetState {
    ENTER_ID,
    EVENT_DETAILS
}

/**
 * Bottom sheet for joining an event using an ID, including verification step.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinEventBottomSheet(
    onDismiss: () -> Unit,
    currentState: JoinEventSheetState,
    eventId: String,
    onEventIdChange: (String) -> Unit,
    verifiedEvent: Event?,
    isVerifying: Boolean,
    onVerify: () -> Unit,
    onJoin: () -> Unit,
    onEditId: () -> Unit,
    toastData: ToastData = ToastData(),
    onProgress: ((Float) -> Unit)? = null
) {
    var activeToastData by remember { mutableStateOf<ToastData?>(null) }

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            activeToastData = toastData
        }
    }

    CustomBottomSheet(
        heading = if (currentState == JoinEventSheetState.ENTER_ID) "Event ID" else "",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = true,
        showCloseButton = true,
        hasToast = toastData.message != null,
        headerBackgroundImage = {
            if (currentState == JoinEventSheetState.EVENT_DETAILS) {
                Image(
                    painter = painterResource(id = R.drawable.bg_pattern_overlay_events_doodle),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop,
                )
            }
        },
        toast = {
            AnimatedVisibility(
                visible = toastData.message != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(12.dp)
            ) {
                activeToastData?.let { data ->
                    CustomToast(
                        message = data.message ?: "",
                        type = data.type
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = currentState,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "JoinEventStateTransition"
            ) { state ->
                when (state) {
                    JoinEventSheetState.ENTER_ID -> {
                        EnterIdSheetContent(
                            eventId = eventId,
                            onEventIdChange = onEventIdChange,
                            onVerify = onVerify,
                            isVerifying = isVerifying
                        )
                    }
                    JoinEventSheetState.EVENT_DETAILS -> {
                        EventDetailsSheetContent(
                            event = verifiedEvent,
                            onJoin = onJoin,
                            onEditId = onEditId
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnterIdSheetContent(
    eventId: String,
    onEventIdChange: (String) -> Unit,
    onVerify: () -> Unit,
    isVerifying: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Use the unique Event ID to join, manage and collaborate on an existing event.",
            style = JasnifyTheme.typography.bodyLarge,
            color = ContentSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        PrimaryInput(
            value = eventId,
            onValueChange = onEventIdChange,
            placeholder = "Enter the event ID",
            shape = EnterIdInputShape,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        CustomTextButton(
            onClick = onVerify,
            text = if (isVerifying) "Verifying..." else "Verify & Continue",
            type = ButtonType.Primary,
            shapeStyle = ButtonShapeStyle.Square,
            enabled = !isVerifying,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun EventDetailsSheetContent(
    event: Event?,
    onJoin: () -> Unit,
    onEditId: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        val eventTypeIcon = remember(event?.typeId) {
            eventTypes.find { it.id == event?.typeId }?.iconResId
                ?: R.drawable.ill_event_type_others
        }

        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(shape = EventIconContainerShape)
                .background(SurfaceSecondary),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(eventTypeIcon),
                contentDescription = "Event Type Icon",
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = event?.name ?: "",
            style = JasnifyTheme.typography.displaySmall.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center,
            color = ContentPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Hosted by ${event?.ownerName ?: ""}",
            style = JasnifyTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = ContentBrand
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Event ID Badge
        Row(
            modifier = Modifier
                .background(SurfaceSecondary, EventIdBadgeShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                    shape = EventIdBadgeShape
                )
                .clip(shape = EventIdBadgeShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onEditId() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Event ID : ${event?.id?.take(8)?.uppercase() ?: ""}",
                style = JasnifyTheme.typography.labelMedium,
                color = ContentSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "Edit ID",
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        CustomTextButton(
            onClick = onJoin,
            text = "Join this Event",
            type = ButtonType.Primary,
            shapeStyle = ButtonShapeStyle.Square,
            modifier = Modifier.fillMaxWidth()
        )
    }
}