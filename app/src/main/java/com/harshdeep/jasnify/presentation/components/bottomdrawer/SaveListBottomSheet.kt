package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.inputfield.TimeLineInput
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.viewmodels.SubEventItem
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SaveListBottomSheet(
    timelineEvents: List<TimelineEvent>,
    isMySavedListChecked: Boolean,
    onMySavedListToggled: (Boolean) -> Unit,
    selectedEventId: String?,
    onEventSelected: (String?) -> Unit,
    onAddNewEvent: (SubEventItem) -> Unit,
    isViewer: Boolean,
    onDismiss: () -> Unit,
    onDone: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    var draftNewEvent by remember { mutableStateOf<SubEventItem?>(null) }
    var showInfoSheet by remember { mutableStateOf(false) }

    CustomBottomSheet(
        heading = "Manage Saved List",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = 560.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfacePrimary)
        ) {
            Spacer(Modifier.height(12.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(SquircleShape(CornerLargeIncrease))
                            .background(SurfaceSecondary)
                            .clickable { onMySavedListToggled(!isMySavedListChecked) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "My Saved List",
                            style = JasnifyTheme.typography.labelXLarge,
                            color = ContentPrimary
                        )

                        CustomChecker(
                            checked = isMySavedListChecked,
                            onCheckedChange = null
                        )
                    }
                }

                item {
                    OrDivider(
                        text = "OR",
                        dividerGap = 24.dp,
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Save for an event",
                                style = JasnifyTheme.typography.headingLarge,
                                fontWeight = FontWeight.Medium,
                                color = ContentPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(R.drawable.ic_info),
                                contentDescription = "Info panel",
                                tint = ContentPrimary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        showInfoSheet = true
                                    }
                            )
                        }

                        if (!isViewer) {
                            Row(
                                modifier = Modifier.clickable {
                                    draftNewEvent = SubEventItem(
                                        dateString = "",
                                        name = "",
                                        isExisting = false,
                                        isEditing = true
                                    )
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_plus),
                                    contentDescription = null,
                                    tint = ContentBrandDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "New Event",
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentBrandDark
                                )
                            }
                        }
                    }
                }

                if (draftNewEvent != null) {
                    item {
                        TimeLineInput(
                            item = draftNewEvent!!,
                            onUpdate = { updatedItem ->
                                if (!updatedItem.isEditing) {
                                    if (updatedItem.isExisting) {
                                        onAddNewEvent(updatedItem)
                                    }
                                    draftNewEvent = null
                                } else {
                                    draftNewEvent = updatedItem
                                }
                            },
                            onDelete = {
                                draftNewEvent = null
                            },
                            backgroundColor = SurfaceSecondary,
                            hasBorder = true
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                itemsIndexed(timelineEvents) { index, eventItem ->
                    val isSelected = selectedEventId == eventItem.id

                    val cardShape = when {
                        timelineEvents.size == 1 -> RoundedCornerShape(CornerLargeIncrease)
                        index == 0 -> SquircleShape(CornerLargeIncrease, CornerLargeIncrease, CornerExtraSmall, CornerExtraSmall)
                        index == timelineEvents.lastIndex -> SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLargeIncrease, CornerLargeIncrease)
                        else -> RoundedCornerShape(CornerExtraSmall)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(cardShape)
                            .background(SurfaceSecondary)
                            .clickable { onEventSelected(if (isSelected) null else eventItem.id) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = eventItem.date,
                                style = JasnifyTheme.typography.labelLarge,
                                color = ContentSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = eventItem.event,
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary
                            )
                        }

                        CustomChecker(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                    }

                    if (index < timelineEvents.lastIndex) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfacePrimary,
                tonalElevation = 0.dp
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(12.dp)
                    ) {
                        CustomTextButton(
                            onClick = onDone,
                            text = "Done",
                            shapeStyle = ButtonShapeStyle.Square,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    if (showInfoSheet) {
        EventTimeLineInfoSheet(
            onDismiss = { showInfoSheet = false },
            onProgress = onProgress
        )
    }
}
