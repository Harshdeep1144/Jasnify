package com.harshdeep.jasnify.presentation.screens.budget

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.domain.model.CategorySummaryData
import com.harshdeep.jasnify.presentation.components.buttons.AskAiButton
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.CategoryCard
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.screens.others.AiChatScreen
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ExpenseCategoryContent(
    categorySearchQuery: String,
    onCategorySearchQueryChange: (String) -> Unit,
    filteredCategorySummary: List<CategorySummaryData>,
    isViewer: Boolean,
    onBackClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCategoryMenuClick: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    eventId: String? = null
) {
    val focusManager = LocalFocusManager.current
    var showAiChat by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    var isBottomBarVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember(listState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val canScroll = listState.canScrollForward || listState.canScrollBackward

                if (!canScroll) {
                    isBottomBarVisible = true
                    return Offset.Zero
                }

                if (delta > 0) {
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) {
                    if (scrollAccumulator > 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                }

                if (scrollAccumulator > 150f && !isBottomBarVisible) {
                    isBottomBarVisible = true
                    scrollAccumulator = 0f
                } else if (scrollAccumulator < -150f && isBottomBarVisible) {
                    isBottomBarVisible = false
                    scrollAccumulator = 0f
                }

                return Offset.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceSecondary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceSecondary)
                    .statusBarsPadding()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    }
            ) {
                CustomTopBar(
                    title = "Expense Category",
                    onBackClick = onBackClick,
                    buttonStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CustomSearchBar(
                        value = categorySearchQuery,
                        placeholder = "Search",
                        onValueChange = onCategorySearchQueryChange,
                        backgroundColor = SurfacePrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(
                            items = filteredCategorySummary,
                            key = { it.name },
                            contentType = { "category_item" }
                        ) { categoryItem ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCategoryClick(categoryItem.name) }
                            ) {
                                CategoryCard(
                                    title = categoryItem.name,
                                    amount = categoryItem.amountFormatted,
                                    emojis = categoryItem.emojis,
                                    totalItemCount = categoryItem.totalCount,
                                    onMenuClick = { onCategoryMenuClick(categoryItem.name) },
                                    showMenu = !isViewer
                                )
                            }
                        }

                        item(key = "bottom_spacer", contentType = "spacer") {
                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = isBottomBarVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 260)
                    ) + fadeIn(animationSpec = tween(durationMillis = 260)),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 260)
                    ) + fadeOut(animationSpec = tween(durationMillis = 260)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(10f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(brush = BottomGradientBrush)
                            .navigationBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
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
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!isViewer) {
                                    CustomTextButton(
                                        onClick = onAddCategoryClick,
                                        text = "Add New Category",
                                        type = ButtonType.Primary,
                                        shapeStyle = ButtonShapeStyle.Round,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val showAskAiButton by remember {
            derivedStateOf {
                !showAiChat && filteredCategorySummary.isNotEmpty() && isBottomBarVisible
            }
        }

        if (showAskAiButton) {
            AskAiButton(
                onClick = { showAiChat = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 240.dp)
                    .zIndex(150f)
            )
        }

        AnimatedVisibility(
            visible = showAiChat,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.zIndex(200f)
        ) {
            AiChatScreen(
                eventId = eventId,
                initialContext = "Expense Categories:\n" + filteredCategorySummary.joinToString("\n") { "- ${it.name}: ${it.amountFormatted}" },
                shouldStartNewSession = true,
                onBackClick = { showAiChat = false }
            )
        }
    }
}
