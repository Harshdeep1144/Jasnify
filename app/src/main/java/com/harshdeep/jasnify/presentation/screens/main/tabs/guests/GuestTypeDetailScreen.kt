package com.harshdeep.jasnify.presentation.screens.main.tabs.guests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.cards.GuestCard
import com.harshdeep.jasnify.presentation.components.cards.GuestCardType
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

@Composable
fun GuestTypeDetailScreen(
    isViewer: Boolean = false,
    typeName: String,
    guests: List<Guest>,
    onBackClick: () -> Unit,
    getGuestTypeColor: (String) -> Color,
    onViewDetails: (Guest) -> Unit,
    onEditTypeClick: () -> Unit,
    onInviteToggle: (String) -> Unit
) {
    var activeFilter by remember { mutableStateOf("Everyone") }
    var expandedGuestId by remember { mutableStateOf<String?>(null) }

    val filteredGuests = remember(guests, activeFilter) {
        when (activeFilter) {
            "Yet to invite" -> guests.filter { !it.invited }
            "Already invited" -> guests.filter { it.invited }
            else -> guests
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.statusBarsPadding()
            ) {
                CustomTopBar(
                    onBackClick = onBackClick
                )
            }
        },
        containerColor = BackgroundPrimary,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = typeName,
                    style = JasnifyTheme.typography.displayLarge,
                    fontWeight = FontWeight.Medium,
                    color = ContentPrimary
                )

                if (!isViewer) {
                    Spacer(modifier = Modifier.width(8.dp))

                    CustomIconButton(
                        icon = painterResource(R.drawable.ic_edit),
                        size = ButtonSize.Small,
                        contentColor = ContentPrimary,
                        containerColor = ContentInvPrimary,
                        onClick = onEditTypeClick
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        label = "Everyone",
                        isSelected = activeFilter == "Everyone",
                        onClick = { activeFilter = "Everyone" },
                        shapeStyle = ChipShapeStyle.Round,
                        hasStroke = true
                    )
                }
                item {
                    FilterChip(
                        label = "Yet to invite",
                        isSelected = activeFilter == "Yet to invite",
                        onClick = { activeFilter = "Yet to invite" },
                        shapeStyle = ChipShapeStyle.Round,
                        hasStroke = true
                    )
                }
                item {
                    FilterChip(
                        label = "Already invited",
                        isSelected = activeFilter == "Already invited",
                        onClick = { activeFilter = "Already invited" },
                        shapeStyle = ChipShapeStyle.Round,
                        hasStroke = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 12.dp, top = 0.dp, bottom = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (filteredGuests.isEmpty()) {
                    item {
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
                            }
                        }
                    }
                } else {
                    itemsIndexed(filteredGuests, key = { _, guest -> guest.id }) { index, guest ->
                        val isExpanded = expandedGuestId == guest.id

                        val topRadius = if (index == 0) CornerLargeIncrease else CornerExtraSmall
                        val bottomRadius = if (index == filteredGuests.lastIndex) CornerLargeIncrease else CornerExtraSmall
                        val itemShape = SquircleShape(
                            topStart = topRadius,
                            topEnd = topRadius,
                            bottomStart = bottomRadius,
                            bottomEnd = bottomRadius,
                            cornerSmoothing = CornerSmoothingDefault
                        )

                        GuestCard(
                            name = guest.name,
                            label = guest.type,
                            imageUrl = guest.imageUrl,
                            isInvited = guest.invited,
                            invitedBy = guest.invitedBy,
                            invitedAt = guest.invitedAt,
                            type = if (isViewer) GuestCardType.DEFAULT else GuestCardType.INVITE_ACTION,
                            labelColor = getGuestTypeColor(guest.type),
                            showActions = isExpanded,
                            onCardClick = {
                                expandedGuestId = if (isExpanded) null else guest.id
                            },
                            onViewDetailsClick = { onViewDetails(guest) },
                            onInviteClick = { onInviteToggle(guest.id) },
                            cardShape = itemShape
                        )
                    }
                }
            }
        }
    }
}