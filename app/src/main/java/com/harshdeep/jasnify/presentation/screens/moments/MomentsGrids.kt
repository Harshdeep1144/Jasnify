package com.harshdeep.jasnify.presentation.screens.moments

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.utils.TimeUtils
import java.util.Calendar

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun PhotosGrid(
    moments: List<Moment>,
    gridState: LazyGridState = rememberLazyGridState(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    selectedMomentIds: Set<String> = emptySet(),
    onMomentClick: (Moment) -> Unit,
    onMomentLongClick: (Moment) -> Unit
) {
    if (moments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No moments yet", color = ContentSecondary)
        }
        return
    }

    val groupedMoments = remember(moments) {
        val dayGroups = moments.groupBy { moment ->
            val cal = Calendar.getInstance().apply { timeInMillis = moment.timestamp }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }

        dayGroups.values.associate { momentsInDay ->
            val latestMoment = momentsInDay.first()
            val timeHeader = TimeUtils.getTimeAgo(latestMoment.timestamp)
            timeHeader to momentsInDay
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
        groupedMoments.forEach { (header, momentsInDate) ->
            item(span = { GridItemSpan(3) }) {
                Text(
                    header,
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(momentsInDate, key = { it.id }) { moment ->
                MomentItem(
                    moment = moment,
                    transitionKey = "moment_${moment.id}",
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    isSelected = selectedMomentIds.contains(moment.id),
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
