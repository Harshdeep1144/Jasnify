package com.harshdeep.jasnify.presentation.screens.moments

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.utils.TimeUtils
import java.util.Calendar

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun PhotosGrid(
    moments: List<Moment>,
    subfolders: List<MomentFolder> = emptyList(),
    gridState: LazyGridState = rememberLazyGridState(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    selectedMomentIds: Set<String> = emptySet(),
    onMomentClick: (Moment) -> Unit,
    onMomentLongClick: (Moment) -> Unit,
    onFolderClick: (MomentFolder) -> Unit = {}
) {
    if (moments.isEmpty() && subfolders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No content yet", color = ContentSecondary)
        }
        return
    }

    val groupedMoments = remember(moments) {
        val dayGroups = moments.groupBy { moment ->
            val cal = Calendar.getInstance().apply { timeInMillis = moment.timestamp }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }

        dayGroups.values.associateBy { momentsInDay ->
            val latestMoment = momentsInDay.first()
            val timeHeader = TimeUtils.getTimeAgo(latestMoment.timestamp)
            timeHeader
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Show Subfolders at the top if any
        if (subfolders.isNotEmpty()) {
            item(span = { GridItemSpan(3) }) {
                Text(
                    "Sub-folders",
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(subfolders, key = { "folder_${it.id}" }, span = { GridItemSpan(1) }) { folder ->
                FolderItem(folder, onClick = { onFolderClick(folder) })
            }

            // Divider if moments also exist
            if (moments.isNotEmpty()) {
                item(span = { GridItemSpan(3) }) {
                    Text(
                        "Moments",
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentSecondary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
            }
        }

        groupedMoments.forEach { (header, momentsInDate) ->
            item(span = { GridItemSpan(3) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        header,
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentSecondary
                    )

                    if (selectedMomentIds.isNotEmpty()) {
                        val allSelected = momentsInDate.all { selectedMomentIds.contains(it.id) }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = if (allSelected) SurfaceBrandPrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (allSelected) Color.Transparent else ContentSecondary,
                                    shape = CircleShape
                                )
                                .clickable {
                                    momentsInDate.forEach { moment ->
                                        if (allSelected) {
                                            if (selectedMomentIds.contains(moment.id)) {
                                                onMomentClick(moment)
                                            }
                                        } else {
                                            if (!selectedMomentIds.contains(moment.id)) {
                                                onMomentClick(moment)
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (allSelected) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_check),
                                    contentDescription = "Select All",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            items(momentsInDate, key = { it.id }) { moment ->
                MomentItem(
                    moment = moment,
                    transitionKey = "moment_${moment.id}",
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    isSelected = selectedMomentIds.contains(moment.id),
                    isSelectionMode = selectedMomentIds.isNotEmpty(),
                    onClick = { onMomentClick(moment) },
                    onLongClick = { onMomentLongClick(moment) }
                )
            }
        }
    }
}

@Composable
internal fun FoldersGrid(
    folders: List<MomentFolder>,
    gridState: LazyGridState = rememberLazyGridState(),
    onFolderClick: (MomentFolder) -> Unit
) {
    if (folders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No folders yet", color = ContentSecondary)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(folders, key = { it.id }) { folder ->
            FolderItem(folder, onClick = { onFolderClick(folder) })
        }
    }
}
