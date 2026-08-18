package com.harshdeep.jasnify.presentation.screens.main.tabs.checklist

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.ChecklistCard
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme

private val FilterOptions = listOf("All", "Recent First", "Oldest First")

@Composable
fun ChecklistArchivesScreen(
    archivedChecklists: List<Checklist>,
    onBackClick: () -> Unit,
    onChecklistClick: (Checklist) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val focusManager = LocalFocusManager.current

    val filteredChecklists = remember(searchQuery, selectedFilter, archivedChecklists) {
        val query = searchQuery.trim()
        val searched = if (query.isEmpty()) {
            archivedChecklists
        } else {
            archivedChecklists.filter { checklist ->
                checklist.title.contains(query, ignoreCase = true) ||
                        checklist.items.any { item -> item.text.contains(query, ignoreCase = true) }
            }
        }

        when (selectedFilter) {
            "Recent First" -> searched.sortedByDescending { it.lastUpdated }
            "Oldest First" -> searched.sortedBy { it.lastUpdated }
            else -> searched.sortedByDescending { it.createdAt }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(BackgroundPrimary)
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Archives",
                    onBackClick = {
                        focusManager.clearFocus()
                        onBackClick()
                    },
                    backIcon = TopIcon.Predefined.BACK,
                    buttonStyle = ButtonBackground.OPAQUE,
                    isLargeTitle = true
                )
            }
        },
        containerColor = BackgroundPrimary,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            Spacer(Modifier.height(12.dp))

            CustomSearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                onActiveChange = { }
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterOptions.forEach { filterOption ->
                    FilterChip(
                        label = filterOption,
                        isSelected = selectedFilter == filterOption,
                        onClick = {
                            focusManager.clearFocus()
                            selectedFilter = filterOption
                        },
                        hasStroke = true,
                        shapeStyle = ChipShapeStyle.Round
                    )
                }
            }

            if (filteredChecklists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_receipt),
                            contentDescription = "Nothing Archived Yet",
                            tint = ContentTertiary,
                            modifier = Modifier.size(84.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No matching archives found" else "Nothing Archived Yet",
                            style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                            color = ContentTertiary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                ) {
                    items(
                        items = filteredChecklists,
                        key = { it.id },
                        contentType = { "archived_checklist_card" }
                    ) { checklist ->
                        ChecklistCard(
                            checklist = checklist,
                            onClick = {
                                focusManager.clearFocus()
                                onChecklistClick(checklist)
                            }
                        )
                    }
                }
            }
        }
    }
}