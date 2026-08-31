package com.harshdeep.jasnify.presentation.screens.main.tabs.checklist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.domain.model.ChecklistItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.dialogs.ColorPickerWheel
import com.harshdeep.jasnify.presentation.components.others.ChecklistItem
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

private val DefaultChecklistColors = listOf(
    Color(0xFFD0E0D0), // Soft Green
    Color(0xFFC5E2D3), // Mint
    Color(0xFFD8D0E5), // Lavender
    Color(0xFFC5DCF0), // Sky Blue
    Color(0xFFF0D1C0), // Peach
    Color(0xFFC9E8F5), // Light Cyan
    Color(0xFFCFE5D1), // Green
    Color(0xFFF2DFC5), // Warm Orange
    Color(0xFFF0D0DB), // Pink
    Color(0xFFDCD3E9), // Purple
    Color(0xFFC5E6E9), // Teal
    Color(0xFFF1EDC8)  // Yellow
)

private val BaseColorPalette = listOf(
    Color(0xFFC9E8F5),
    Color(0xFFCFE5D1),
    Color(0xFFF2DFC5),
    Color(0xFFF0D0DB),
    Color(0xFFDCD3E9),
    Color(0xFFC5E6E9),
    Color(0xFFF1EDC8),
    Color(0xFFE2CCE5),
    Color(0xFFD2D6E8),
    Color(0xFFC5E8DD),
    Color(0xFFF0CACA),
    Color(0xFFCFE3D4),
    Color(0xFFF2E4B8),
    Color(0xFFC8DCEC),
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ChecklistDetailScreen(
    checklist: Checklist? = null,
    isAddingNew: Boolean = false,
    onBackClick: (Checklist?) -> Unit,
    onDelete: (String) -> Unit = {},
    onTogglePin: (Checklist) -> Unit = {},
    onArchive: (Checklist) -> Unit = {},
    isArchived: Boolean = false,
    isViewer: Boolean = false,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    onOpenColorPicker: (Color) -> Unit = {},
    onOpenMenu: () -> Unit = {},
    currentBgColor: Color = Color.Transparent
) {
    var title by remember { mutableStateOf(checklist?.title ?: "") }

    var items by remember {
        mutableStateOf(
            if (isAddingNew && (checklist?.items == null || checklist.items.isEmpty())) {
                listOf(ChecklistItem(id = UUID.randomUUID().toString()))
            } else {
                checklist?.items ?: emptyList()
            }
        )
    }

    var bgColor by remember(checklist?.id) {
        mutableStateOf(
            if (checklist != null) {
                Color(checklist.bgColorHex)
            } else {
                DefaultChecklistColors.random()
            }
        )
    }

    LaunchedEffect(currentBgColor) {
        if (currentBgColor != Color.Transparent) {
            bgColor = currentBgColor
        }
    }

    var pinned by remember { mutableStateOf(checklist?.pinned ?: false) }
    var archived by remember { mutableStateOf(checklist?.archived ?: isArchived) }

    LaunchedEffect(isArchived) {
        archived = isArchived
    }

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    var isFormattingActive by remember { mutableStateOf(false) }
    var isBoldActive by remember { mutableStateOf(false) }
    var isItalicActive by remember { mutableStateOf(false) }
    var isUnderlineActive by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val firstItemFocusRequester = remember { FocusRequester() }
    var itemToFocusId by remember { mutableStateOf<String?>(null) }

    val cardDateTimeString = remember {
        checklist?.dateTime ?: run {
            val sdf = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
            sdf.format(Date())
        }
    }

    val history = remember { mutableStateListOf<Pair<String, List<ChecklistItem>>>() }
    var historyIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(checklist) {
        if (history.isEmpty()) {
            history.add(Pair(title, items))
            historyIndex = 0
        }
    }

    LaunchedEffect(isAddingNew) {
        if (isAddingNew) {
            delay(250.milliseconds)
            firstItemFocusRequester.requestFocus()
        }
    }

    fun saveToHistory(newTitle: String, newItems: List<ChecklistItem>) {
        if (historyIndex >= 0 && historyIndex < history.size) {
            val current = history[historyIndex]
            if (current.first == newTitle && current.second == newItems) return
        }
        while (history.size > historyIndex + 1) {
            history.removeAt(history.size - 1)
        }
        history.add(Pair(newTitle, newItems.toList()))
        historyIndex = history.size - 1
    }

    fun performUndo() {
        if (historyIndex > 0) {
            historyIndex--
            val snapshot = history[historyIndex]
            title = snapshot.first
            items = snapshot.second
        }
    }

    fun performRedo() {
        if (historyIndex < history.size - 1) {
            historyIndex++
            val snapshot = history[historyIndex]
            title = snapshot.first
            items = snapshot.second
        }
    }

    BackHandler {
        focusManager.clearFocus()
        val cleanedItems = items.filter { it.text.isNotBlank() }
        val result = Checklist(
            id = checklist?.id ?: UUID.randomUUID().toString(),
            title = title,
            dateTime = cardDateTimeString,
            items = cleanedItems,
            bgColorHex = bgColor.toArgb().toLong(),
            pinned = pinned,
            archived = archived,
            lastUpdated = System.currentTimeMillis(),
            createdAt = checklist?.createdAt ?: System.currentTimeMillis()
        )
        onBackClick(result)
    }

    val sharedBoundsModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "bounds-${checklist?.id ?: "new_checklist_bounds"}"),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = BoundsTransform { _, _ ->
                    tween(durationMillis = 350, easing = FastOutSlowInEasing)
                }
            )
        }
    } else {
        Modifier
    }

    val sharedTitleModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = "title-${checklist?.id ?: "new_title_element"}"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    CustomTopBar(
                        onBackClick = {
                            focusManager.clearFocus()
                            val cleanedItems = items.filter { it.text.isNotBlank() }
                            val result = Checklist(
                                id = checklist?.id ?: UUID.randomUUID().toString(),
                                title = title,
                                dateTime = cardDateTimeString,
                                items = cleanedItems,
                                bgColorHex = bgColor.toArgb().toLong(),
                                pinned = pinned,
                                archived = archived,
                                lastUpdated = System.currentTimeMillis(),
                                createdAt = checklist?.createdAt ?: System.currentTimeMillis()
                            )
                            onBackClick(result)
                        },
                        backIcon = TopIcon.CustomPainter(painterResource(R.drawable.ic_check)),
                        secondaryIcon = if (pinned) TopIcon.Predefined.PIN_FILLED else TopIcon.Predefined.PIN,
                        onSecondaryClick = {
                            focusManager.clearFocus()
                            pinned = !pinned
                            checklist?.let { onTogglePin(it) }
                        },
                        onMenuClick = {
                            focusManager.clearFocus()
                            onOpenMenu()
                        },
                        buttonStyle = ButtonBackground.TRANSLUCENT,
                        translucentAlpha = 0.5f
                    )
                }
            },
            containerColor = bgColor,
            modifier = Modifier
                .fillMaxSize()
                .then(sharedBoundsModifier)
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
                    .padding(paddingValues)
                    .imePadding()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    }
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp)
                ) {
                    Text(
                        text = cardDateTimeString,
                        style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = ContentSecondary,
                    )
                    Spacer(Modifier.height(8.dp))

                    BasicTextField(
                        value = title,
                        onValueChange = {
                            if (!isViewer) {
                                title = it
                                saveToHistory(it, items)
                            }
                        },
                        readOnly = isViewer,
                        textStyle = JasnifyTheme.typography.headingXLarge.copy(
                            fontWeight = if (isBoldActive) FontWeight.Bold else FontWeight.Medium,
                            fontStyle = if (isItalicActive) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (isUnderlineActive) TextDecoration.Underline else TextDecoration.None
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(sharedTitleModifier),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    text = "Title",
                                    style = JasnifyTheme.typography.headingXLarge.copy(
                                        fontWeight = if (isBoldActive) FontWeight.Bold else FontWeight.Medium,
                                        fontStyle = if (isItalicActive) FontStyle.Italic else FontStyle.Normal,
                                        textDecoration = if (isUnderlineActive) TextDecoration.Underline else TextDecoration.None
                                    ),
                                    color = ContentSecondary
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(
                            items = items,
                            key = { _, item -> item.id },
                            contentType = { _, _ -> "checklist_item" }
                        ) { index, item ->
                            val focusRequester = if (index == 0) firstItemFocusRequester else remember { FocusRequester() }

                            LaunchedEffect(itemToFocusId) {
                                if (itemToFocusId == item.id) {
                                    focusRequester.requestFocus()
                                    itemToFocusId = null
                                }
                            }

                            val isDragging = draggedItemIndex == index
                            val currentIndex by rememberUpdatedState(index)
                            val density = LocalDensity.current
                            val itemHeightPx = with(density) { 56.dp.toPx() }

                            Box(
                                modifier = Modifier
                                    .animateItem()
                                    .graphicsLayer {
                                        translationY = if (isDragging) dragOffset else 0f
                                        scaleX = if (isDragging) 1.05f else 1f
                                        scaleY = if (isDragging) 1.05f else 1f
                                        alpha = if (isDragging) 0.8f else 1f
                                    }
                                    .zIndex(if (isDragging) 1f else 0f)
                            ) {
                                ChecklistItem(
                                    item = item,
                                    focusRequester = focusRequester,
                                    isViewer = isViewer,
                                    onTextChanged = { newText ->
                                        val updated = items.map { if (it.id == item.id) it.copy(text = newText) else it }
                                        items = updated
                                        saveToHistory(title, updated)
                                    },
                                    onCheckedChange = { isChecked ->
                                        val updated = items.map {
                                            if (it.id == item.id) it.copy(checked = isChecked) else it
                                        }
                                        items = updated
                                        saveToHistory(title, updated)
                                    },
                                    onRemove = {
                                        val updated = items.filter { it.id != item.id }
                                        items = updated
                                        saveToHistory(title, updated)
                                    },
                                    onEnterPressed = {
                                        val newItem = ChecklistItem(id = UUID.randomUUID().toString())
                                        val newItems = items.toMutableList()
                                        newItems.add(index + 1, newItem)
                                        items = newItems
                                        itemToFocusId = newItem.id
                                        saveToHistory(title, newItems)
                                    },
                                    modifier = Modifier.pointerInput(item.id) {
                                        if (!isViewer) {
                                            detectDragGesturesAfterLongPress(
                                                onDragStart = {
                                                    focusManager.clearFocus()
                                                    draggedItemIndex = currentIndex
                                                    dragOffset = 0f
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    dragOffset += dragAmount.y

                                                    val activeIndex = draggedItemIndex
                                                    if (activeIndex != null) {
                                                        val targetIndex = when {
                                                            dragOffset > itemHeightPx -> activeIndex + 1
                                                            dragOffset < -itemHeightPx -> activeIndex - 1
                                                            else -> activeIndex
                                                        }

                                                        if (targetIndex in items.indices && targetIndex != activeIndex) {
                                                            val newList = items.toMutableList()
                                                            val movingItem = newList.removeAt(activeIndex)
                                                            newList.add(targetIndex, movingItem)
                                                            items = newList

                                                            if (targetIndex > activeIndex) {
                                                                dragOffset -= itemHeightPx
                                                            } else {
                                                                dragOffset += itemHeightPx
                                                            }
                                                            draggedItemIndex = targetIndex
                                                        }
                                                    }
                                                },
                                                onDragEnd = {
                                                    draggedItemIndex = null
                                                    dragOffset = 0f
                                                },
                                                onDragCancel = {
                                                    draggedItemIndex = null
                                                    dragOffset = 0f
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        item(key = "add_item_button", contentType = "add_item_button") {
                            if (!isViewer) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val updated = items + ChecklistItem(id = UUID.randomUUID().toString())
                                            items = updated
                                            saveToHistory(title, updated)
                                        }
                                        .padding(start = 32.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = ContentPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Add item",
                                        style = JasnifyTheme.typography.headingMedium,
                                        color = ContentSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                if (!isViewer) {
                    ChecklistDetailToolbar(
                        isFormattingActive = isFormattingActive,
                        onFormattingActiveChange = { isFormattingActive = it },
                        isBoldActive = isBoldActive,
                        onBoldChange = { isBoldActive = it },
                        isItalicActive = isItalicActive,
                        onItalicChange = { isItalicActive = it },
                        isUnderlineActive = isUnderlineActive,
                        onUnderlineChange = { isUnderlineActive = it },
                        canUndo = historyIndex > 0,
                        canRedo = historyIndex < history.size - 1,
                        onUndo = { performUndo() },
                        onRedo = { performRedo() },
                        onColorClick = {
                            focusManager.clearFocus()
                            onOpenColorPicker(bgColor)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChecklistDetailToolbar(
    isFormattingActive: Boolean,
    onFormattingActiveChange: (Boolean) -> Unit,
    isBoldActive: Boolean,
    onBoldChange: (Boolean) -> Unit,
    isItalicActive: Boolean,
    onItalicChange: (Boolean) -> Unit,
    isUnderlineActive: Boolean,
    onUnderlineChange: (Boolean) -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onColorClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isFormattingActive) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_bold)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = { onBoldChange(!isBoldActive) }
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_italic)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = { onItalicChange(!isItalicActive) }
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_underline)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = { onUnderlineChange(!isUnderlineActive) }
                )
            }
            TopBarIconButton(
                icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_cross)),
                backgroundStyle = ButtonBackground.TRANSLUCENT,
                translucentAlpha = 0.5f,
                iconSize = 18.dp,
                onClick = { onFormattingActiveChange(false) }
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_paint)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = onColorClick
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_a_text)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = { onFormattingActiveChange(true) }
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_undo)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    iconColor = if (canUndo) ContentPrimary else ContentTertiary,
                    onClick = onUndo
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_redo)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    iconColor = if (canRedo) ContentPrimary else ContentTertiary,
                    onClick = onRedo
                )
            }
        }
    }
}

private fun areColorsEqual(c1: Color, c2: Color): Boolean {
    val threshold = 0.005f
    return abs(c1.red - c2.red) < threshold &&
            abs(c1.green - c2.green) < threshold &&
            abs(c1.blue - c2.blue) < threshold &&
            abs(c1.alpha - c2.alpha) < threshold
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    initialColor: Color,
    recentColors: List<Color> = emptyList(),
    onColorPreview: (Color) -> Unit,
    onConfirm: (Color) -> Unit,
    onDismiss: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    var selectedColor by remember { mutableStateOf(initialColor) }
    var showColorWheel by remember { mutableStateOf(false) }

    val colors = remember(initialColor, recentColors) {
        val matchesExisting = BaseColorPalette.any { areColorsEqual(it, initialColor) }
        val finalBase = if (matchesExisting) {
            BaseColorPalette
        } else {
            listOf(initialColor) + BaseColorPalette
        }
        (recentColors + finalBase).distinctBy { it.toArgb() }
    }

    if (showColorWheel) {
        Dialog(
            onDismissRequest = { showColorWheel = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .noRippleClickable { showColorWheel = false },
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.noRippleClickable { }) {
                    ColorPickerWheel(
                        initialColor = selectedColor,
                        onColorSelected = { newColor ->
                            selectedColor = newColor
                            onColorPreview(newColor)
                        },
                        onDismiss = { showColorWheel = false }
                    )
                }
            }
        }
    }

    CustomBottomSheet(
        onDismiss = onDismiss,
        onProgress = onProgress,
        containerColor = selectedColor,
        sheetHeight = null,
        showDragHandle = true,
        showCloseButton = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Select Background Color",
                style = JasnifyTheme.typography.headingLarge,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (recentColors.isNotEmpty()) {
                Text(
                    text = "RECENT COLORS",
                    style = JasnifyTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentColors.take(6).forEach { color ->
                        ColorCircle(
                            color = color,
                            isSelected = areColorsEqual(color, selectedColor),
                            onClick = {
                                selectedColor = color
                                onColorPreview(color)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "PALETTE",
                style = JasnifyTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = ContentSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // First row takes 5 colors (to accommodate the color wheel at index 0)
            val paletteOnly = colors.filter { c -> recentColors.none { areColorsEqual(it, c) } }
            val firstRowColors = remember(paletteOnly) { paletteOnly.take(5) }
            val remainingRowsColors = remember(paletteOnly) { paletteOnly.drop(5).chunked(6) }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // First Row: Color Wheel + First 5 Colors
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.sweepGradient(
                                    listOf(
                                        Color.Red,
                                        Color.Yellow,
                                        Color.Green,
                                        Color.Cyan,
                                        Color.Blue,
                                        Color.Magenta,
                                        Color.Red
                                    )
                                )
                            )
                            .noRippleClickable {
                                showColorWheel = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(selectedColor)
                        )
                    }

                    firstRowColors.forEach { color ->
                        ColorCircle(
                            color = color,
                            isSelected = areColorsEqual(color, selectedColor),
                            onClick = {
                                selectedColor = color
                                onColorPreview(color)
                            }
                        )
                    }
                }

                // Subsequent Rows: 6 Colors per row
                remainingRowsColors.forEach { rowColors ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowColors.forEach { color ->
                            ColorCircle(
                                color = color,
                                isSelected = areColorsEqual(color, selectedColor),
                                onClick = {
                                    selectedColor = color
                                    onColorPreview(color)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextButton(
                text = "Done",
                onClick = { onConfirm(selectedColor) },
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                containerColor = ContentPrimary,
                contentColor = ContentInvPrimary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .border(if (isSelected) 2.dp else 0.dp, ContentPrimary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = ContentPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}