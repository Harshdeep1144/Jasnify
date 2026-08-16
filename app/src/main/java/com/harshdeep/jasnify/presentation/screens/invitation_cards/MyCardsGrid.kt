package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.utils.TimeUtils
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MyCardsGrid(
    cards: List<CardData>,
    templates: List<CardData>,
    gridState: LazyGridState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    canEdit: Boolean,
    nestedScrollConnection: NestedScrollConnection,
    onStartEditing: () -> Unit,
    selectedCardIds: Set<String>,
    onToggleSelection: (String) -> Unit,
    onCardClick: (CardData, String) -> Unit,
    onShareClick: (CardData) -> Unit,
) {
    if (cards.isEmpty()) {
        MyCardsEmptyState(
            canEdit = canEdit,
            onStartEditing = onStartEditing
        )
    } else {
        val isSelectionMode = selectedCardIds.isNotEmpty()

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp, 12.dp, 12.dp, 120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            items(cards, key = { it.id }) { card ->
                val isSelected = selectedCardIds.contains(card.id)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.96f else 1f,
                    label = "scale"
                )
                val myCardKey = "my_card_${card.id}"

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    with(sharedTransitionScope) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(280f / 373f)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = myCardKey),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    clipInOverlayDuringTransition = OverlayClip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                                )
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                                .combinedClickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = {
                                        if (isSelectionMode) {
                                            onToggleSelection(card.id)
                                        } else {
                                            onCardClick(card, myCardKey)
                                        }
                                    },
                                    onLongClick = {
                                        onToggleSelection(card.id)
                                    }
                                )
                                .then(
                                    if (isSelected) Modifier.border(2.dp, ContentBrand, SquircleShape(CornerMedium, CornerSmoothingDefault))
                                    else Modifier
                                )
                        ) {
                            CardItem(
                                data = card,
                                showControls = !isSelectionMode,
                                onShareClick = { onShareClick(card) },
                                onLikeClick = null,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (isSelectionMode) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.align(Alignment.TopEnd),
                                        shape = CircleShape,
                                        color = if (isSelected) SurfacePrimary else Color.Black.copy(0.3f)
                                    ) {
                                        CustomChecker(
                                            checked = isSelected,
                                            onCheckedChange = {
                                                onToggleSelection(card.id)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit_pen),
                            contentDescription = null,
                            tint = ContentSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Edited ${TimeUtils.getTimeAgo(card.lastEdited)}",
                            style = JasnifyTheme.typography.labelLarge,
                            color = if (isSelected) ContentBrand else ContentSecondary,
                            maxLines = 1,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}
