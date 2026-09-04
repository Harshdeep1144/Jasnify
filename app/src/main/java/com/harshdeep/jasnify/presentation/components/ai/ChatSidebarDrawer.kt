package com.harshdeep.jasnify.presentation.components.ai

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ChatSession
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ChatSidebarDrawer(
    sessions: List<ChatSession>,
    currentChatId: String?,
    onSelectChat: (String) -> Unit,
    onNewChatClick: () -> Unit,
    onDeleteChat: (ChatSession) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    var wasFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            wasFocused = false
            delay(100.milliseconds)
            searchFocusRequester.requestFocus()
        }
    }

    val filteredSessions = remember(sessions, searchQuery) {
        if (searchQuery.isBlank()) sessions
        else sessions.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    val groupedSessions = remember(filteredSessions) {
        val calendar = Calendar.getInstance()
        val nowMillis = System.currentTimeMillis()

        calendar.timeInMillis = nowMillis
        val todayYear = calendar.get(Calendar.YEAR)
        val todayDay = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayYear = calendar.get(Calendar.YEAR)
        val yesterdayDay = calendar.get(Calendar.DAY_OF_YEAR)

        val groups = LinkedHashMap<String, MutableList<ChatSession>>()

        filteredSessions.forEach { session ->
            val sessCal = Calendar.getInstance().apply { timeInMillis = session.timestamp }
            val sessYear = sessCal.get(Calendar.YEAR)
            val sessDay = sessCal.get(Calendar.DAY_OF_YEAR)

            val groupKey = when (sessYear) {
                todayYear if sessDay == todayDay -> "TODAY"
                yesterdayYear if sessDay == yesterdayDay -> "YESTERDAY"
                else -> SimpleDateFormat("d'TH' MMM, yyyy", Locale.US).format(Date(session.timestamp)).uppercase()
            }

            groups.getOrPut(groupKey) { mutableListOf() }.add(session)
        }
        groups
    }

    val aiBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF3E8FA),
            Color(0xFFF8F0FC),
            Color(0xFFFAF4FE)
        )
    )

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(aiBackgroundGradient)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_app),
                    contentDescription = "App Logo",
                    tint = ContentPrimary,
                    modifier = Modifier.height(28.dp)
                )
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = isSearchActive,
                transitionSpec = {
                    if (targetState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "NewChatSearchTransition",
                modifier = Modifier.padding(horizontal = 12.dp)
            ) { active ->
                if (active) {
                    CustomSearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search history...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(searchFocusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    wasFocused = true
                                } else if (wasFocused) {
                                    isSearchActive = false
                                    searchQuery = ""
                                    wasFocused = false
                                }
                            },
                        onActiveChange = {}
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomTextButton(
                            onClick = onNewChatClick,
                            text = "New Chat",
                            leadingIcon = painterResource(R.drawable.ic_plus),
                            type = ButtonType.Secondary,
                            modifier = Modifier.weight(1f)
                        )

                        TopBarIconButton(
                            onClick = { isSearchActive = true },
                            icon = TopIcon.Predefined.SEARCH,
                            size = 56.dp,
                            iconSize = 24.dp,
                            borderColor = ContentPrimary,
                            backgroundStyle = ButtonBackground.OPAQUE
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                groupedSessions.forEach { (dateHeader, sessionList) ->
                    item(key = dateHeader) {
                        Text(
                            text = dateHeader,
                            style = JasnifyTheme.typography.labelMedium,
                            color = ContentSecondary,
                            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                        )
                    }

                    items(sessionList, key = { it.id }) { session ->
                        SwipeToDismissChatSessionItem(
                            session = session,
                            isSelected = session.id == currentChatId,
                            onSelectChat = { onSelectChat(session.id) },
                            onDeleteChat = { onDeleteChat(session) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeToDismissChatSessionItem(
    session: ChatSession,
    isSelected: Boolean,
    onSelectChat: () -> Unit,
    onDeleteChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var itemWidthPx by remember { mutableFloatStateOf(1f) }
    val velocityTracker = remember { VelocityTracker() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerMedium))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFFE53935))
                .padding(start = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete Chat",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = offsetX.value.roundToInt(), y = 0) }
                .background(if (isSelected) SurfaceBrandSecondary else Color.Transparent)
                .pointerInput(session.id) {
                    itemWidthPx = size.width.toFloat()
                    detectHorizontalDragGestures(
                        onDragStart = { velocityTracker.resetTracking() },
                        onDragEnd = {
                            val velocity = velocityTracker.calculateVelocity().x
                            val triggerDistance = itemWidthPx * 0.55f
                            val shouldDismiss = offsetX.value > triggerDistance || velocity > 1200f

                            coroutineScope.launch {
                                if (shouldDismiss) {
                                    offsetX.animateTo(
                                        targetValue = itemWidthPx,
                                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                                    )
                                    onDeleteChat()
                                } else {
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                offsetX.animateTo(0f)
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                            val newOffset = (offsetX.value + dragAmount).coerceIn(0f, itemWidthPx)
                            coroutineScope.launch {
                                offsetX.snapTo(newOffset)
                            }
                            change.consume()
                        }
                    )
                }
                .clickable { onSelectChat() }
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = session.title,
                style = JasnifyTheme.typography.labelXLarge,
                color = if (isSelected) ContentBrandDark else ContentPrimary,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
