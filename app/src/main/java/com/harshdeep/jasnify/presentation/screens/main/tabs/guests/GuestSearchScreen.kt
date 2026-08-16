package com.harshdeep.jasnify.presentation.screens.main.tabs.guests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.GuestCard
import com.harshdeep.jasnify.presentation.components.cards.GuestCardType
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

@Composable
fun GuestSearchScreen(
    isViewer: Boolean = false,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    recentSearches: List<String>,
    onClearRecent: () -> Unit,
    onRemoveRecent: (String) -> Unit,
    searchResults: List<Guest>,
    onGuestClick: (Guest) -> Unit,
    onAddGuestClick: () -> Unit,
    getGuestTypeColor: (String) -> Color,
    onInviteToggle: (String) -> Unit
) {
    var wasFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
    ) {
        CustomSearchBar(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Search Guests",
            onActiveChange = { active ->
                if (active) {
                    wasFocused = true
                } else if (wasFocused) {
                    onBackClick()
                    wasFocused = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp, 12.dp, 0.dp)
                .focusRequester(focusRequester)
        )

        if (searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clock_forward),
                            contentDescription = null,
                            tint = ContentPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recent Searches",
                            style = JasnifyTheme.typography.headingMedium,
                            fontWeight = FontWeight.Medium,
                            color = ContentPrimary
                        )
                    }
                    Text(
                        text = "Clear all",
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentBrandDark,
                        modifier = Modifier.clickable { onClearRecent() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentSearches.forEach { search ->
                        FilterChip(
                            label = search,
                            trailingIcon = Icons.Default.Close,
                            hasStroke = true,
                            shapeStyle = ChipShapeStyle.Round,
                            onTrailingIconClick = {
                                onRemoveRecent(search)
                            },
                            onClick = {
                                onSearchQueryChange(search)
                            }
                        )
                    }
                }
            }
        } else {
            if (searchResults.isEmpty()) {
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
                            text = "Add New Guests",
                            leadingIcon = painterResource(id = R.drawable.ic_plus),
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(searchResults, key = { _, guest -> guest.id }) { index, guest ->
                        val topRadius = if (index == 0) CornerLargeIncrease else CornerExtraSmall
                        val bottomRadius = if (index == searchResults.lastIndex) CornerLargeIncrease else CornerExtraSmall
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
                            onCardClick = {
                                onGuestClick(guest)
                            },
                            onInviteClick = { onInviteToggle(guest.id) },
                            cardShape = itemShape,
                        )
                    }
                }
            }
        }
    }
}