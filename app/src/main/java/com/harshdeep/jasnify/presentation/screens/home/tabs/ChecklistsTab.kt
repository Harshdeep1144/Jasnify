package com.harshdeep.jasnify.presentation.screens.home.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomDeleteSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.Checklist
import com.harshdeep.jasnify.presentation.components.cards.ChecklistCard
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastType
import kotlinx.coroutines.delay
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.ChecklistItem
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.*
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ChecklistsTab() {
    var isGridView by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("All") }
    var showMenuSheet by remember { mutableStateOf(false) }
    var selectedChecklist by remember { mutableStateOf<Checklist?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var showArchives by remember { mutableStateOf(false) }
    var navigatedFromArchives by remember { mutableStateOf(false) }
    var showDiscardToast by remember { mutableStateOf(false) }

    LaunchedEffect(showDiscardToast) {
        if (showDiscardToast) {
            delay(2000.milliseconds)
            showDiscardToast = false
        }
    }

    // System Back Button Handling
    BackHandler(enabled = selectedChecklist != null || isAddingNew || showArchives) {
        if (selectedChecklist != null || isAddingNew) {
            selectedChecklist = null
            isAddingNew = false
            // If we came from archives, go back to archives
            if (navigatedFromArchives) {
                showArchives = true
                navigatedFromArchives = false
            }
        } else if (showArchives) {
            showArchives = false
        }
    }

    val checklists = remember {
        mutableStateListOf(
            Checklist(
                id = "1",
                title = "Shopping for Bride",
                dateTime = "Today, 09:30 PM",
                items = listOf(
                    ChecklistItem(text = "Purchase Outfits"),
                    ChecklistItem(text = "Make Appointment for Makeup"),
                    ChecklistItem(text = "Book Jewellery")
                ),
                bgColor = SoftMint,
                isPinned = true
            ),
            Checklist(
                id = "2",
                title = "Catering Arrangement",
                dateTime = "Today, 10:28 AM",
                items = listOf(
                    ChecklistItem(text = "Confirm Menu Selection"),
                    ChecklistItem(text = "Finalize Guest List"),
                    ChecklistItem(text = "Arrange Table Settings")
                ),
                bgColor = PaleLavender
            ),
            Checklist(
                id = "3",
                title = "Saturday To-Dos",
                dateTime = "Yesterday, 04:50 PM",
                items = listOf(
                    ChecklistItem(text = "Pick up floral arrangements"),
                    ChecklistItem(text = "Confirm limousine service booking"),
                    ChecklistItem(text = "Finalize seating chart presentation")
                ),
                bgColor = SoftPeach
            )
        )
    }

    val archivedChecklists = remember {
        mutableStateListOf(
            Checklist(
                id = "archived_1",
                title = "Audio-Visual Setup",
                dateTime = "Yesterday, 9:00 AM",
                items = listOf(
                    ChecklistItem(text = "Test Equipment"),
                    ChecklistItem(text = "Confirm Speaker Arrangements"),
                    ChecklistItem(text = "Check Lighting Levels")
                ),
                bgColor = PaleLavender
            ),
            Checklist(
                id = "archived_2",
                title = "Guest Transportation",
                dateTime = "Yesterday, 11:00 AM",
                items = listOf(
                    ChecklistItem(text = "Book Shuttle Services"),
                    ChecklistItem(text = "Verify Arrival Times"),
                    ChecklistItem(text = "Coordinate with Drivers")
                ),
                bgColor = SoftPeach
            )
        )
    }

    if (selectedChecklist != null || isAddingNew) {
        ChecklistDetailScreen(
            checklist = selectedChecklist,
            onBackClick = { updatedChecklist ->
                if (updatedChecklist != null) {
                    val isEmpty = updatedChecklist.title.isBlank() &&
                            updatedChecklist.items.all { it.text.isBlank() }

                    if (isEmpty && isAddingNew) {
                        showDiscardToast = true
                    } else if (updatedChecklist.title.isNotBlank() || updatedChecklist.items.any { it.text.isNotBlank() }) {
                        // Check if it's in normal list
                        val index = checklists.indexOfFirst { it.id == updatedChecklist.id }
                        if (index != -1) {
                            checklists[index] = updatedChecklist
                        } else {
                            // Check if it's in archived list
                            val archIndex = archivedChecklists.indexOfFirst { it.id == updatedChecklist.id }
                            if (archIndex != -1) {
                                archivedChecklists[archIndex] = updatedChecklist
                            } else {
                                checklists.add(0, updatedChecklist)
                            }
                        }
                    }
                }
                selectedChecklist = null
                isAddingNew = false
                if (navigatedFromArchives) {
                    showArchives = true
                    navigatedFromArchives = false
                }
            },
            onDelete = { id ->
                checklists.removeAll { it.id == id }
                archivedChecklists.removeAll { it.id == id }
                selectedChecklist = null
                isAddingNew = false
                if (navigatedFromArchives) {
                    showArchives = true
                    navigatedFromArchives = false
                }
            },
            onTogglePin = { id ->
                val index = checklists.indexOfFirst { it.id == id }
                if (index != -1) {
                    checklists[index] = checklists[index].copy(isPinned = !checklists[index].isPinned)
                }
            },
            onArchive = { id ->
                val index = checklists.indexOfFirst { it.id == id }
                if (index != -1) {
                    val item = checklists.removeAt(index)
                    archivedChecklists.add(0, item.copy(isPinned = false))
                } else {
                    val archIndex = archivedChecklists.indexOfFirst { it.id == id }
                    if (archIndex != -1) {
                        val item = archivedChecklists.removeAt(archIndex)
                        checklists.add(0, item)
                    }
                }
                selectedChecklist = null
                isAddingNew = false
                if (navigatedFromArchives) {
                    showArchives = true
                    navigatedFromArchives = false
                }
            },
            isArchived = archivedChecklists.any { it.id == selectedChecklist?.id }
        )
    } else if (showArchives) {
        ChecklistArchivesScreen(
            archivedChecklists = archivedChecklists,
            onBackClick = { showArchives = false },
            onChecklistClick = { checklist ->
                selectedChecklist = checklist
                showArchives = false
                navigatedFromArchives = true
            }
        )
    } else {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier.background(BackgroundPrimary)
                        .fillMaxWidth()
                        .statusBarsPadding()
                ){
                    CustomTopBar(
                        title = "Checklist",
                        titleIcon = TopIcon.Predefined.CHECKLIST,
                        isLeftAligned = true,
                        isLargeTitle = true,
                        secondaryIcon = TopIcon.Predefined.SEARCH,
                        onSecondaryClick = {},
                        onMenuClick = { showMenuSheet = true },
                        buttonStyle = ButtonBackground.OPAQUE
                    )
                }
            },
            floatingActionButton = {
                CustomIconButton(
                    onClick = { isAddingNew = true },
                    icon = painterResource(R.drawable.ic_plus),
                    size = ButtonSize.Large,
                    modifier = Modifier.shadow(16.dp, CircleShape)
                )
            },
            containerColor = BackgroundPrimary
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        label = "All",
                        isSelected = selectedFilter == "All",
                        onClick = { selectedFilter = "All" },
                        hasStroke = true,
                        shapeStyle = ChipShapeStyle.Round
                    )
                    FilterChip(
                        label = "Recent First",
                        isSelected = selectedFilter == "Recent First",
                        onClick = { selectedFilter = "Recent First" },
                        hasStroke = true,
                        shapeStyle = ChipShapeStyle.Round
                    )
                    FilterChip(
                        label = "Oldest First",
                        isSelected = selectedFilter == "Oldest First",
                        onClick = { selectedFilter = "Oldest First" },
                        hasStroke = true,
                        shapeStyle = ChipShapeStyle.Round
                    )
                }

                val sortedChecklists = when (selectedFilter) {
                    "Recent First" -> checklists.sortedWith(compareByDescending<Checklist> { it.isPinned }.thenByDescending { it.dateTime })
                    "Oldest First" -> checklists.sortedWith(compareByDescending<Checklist> { it.isPinned }.thenBy { it.dateTime })
                    else -> checklists.sortedByDescending { it.isPinned }
                }

                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        modifier = Modifier.fillMaxSize()
                            .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                    ) {
                        items(items = sortedChecklists, key = { it.id }) { checklist ->
                            ChecklistCard(
                                checklist = checklist,
                                onClick = { selectedChecklist = checklist }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                            .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                    ) {
                        items(items = sortedChecklists, key = { it.id }) { checklist ->
                            ChecklistCard(
                                checklist = checklist,
                                onClick = { selectedChecklist = checklist }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMenuSheet) {
        MenuBottomSheet(
            items = listOf(
                MenuSheetActionItem(
                    text = if (isGridView) "List View" else "Grid View",
                    icon = if (isGridView) painterResource(R.drawable.ic_list) else painterResource(R.drawable.ic_grid),
                    onClick = {
                        isGridView = !isGridView
                        showMenuSheet = false
                    }
                ),
                MenuSheetActionItem(
                    text = "View Archives",
                    icon = painterResource(R.drawable.ic_box),
                    onClick = {
                        showArchives = true
                        showMenuSheet = false
                    }
                ),
                MenuSheetActionItem(
                    text = "Manage Room Access",
                    icon = painterResource(R.drawable.ic_user_default),
                    onClick = { showMenuSheet = false }
                ),
                MenuSheetActionItem(
                    text = "Help & Feedback",
                    icon = painterResource(R.drawable.ic_help_feedback),
                    onClick = { showMenuSheet = false }
                )
            ),
            onCancelClick = { showMenuSheet = false }
        )
    }

    // --- CustomToast Display positioned at the bottom of the screen ---
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = showDiscardToast,
            enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight + 500 }),
            exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight + 500 }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 92.dp) // Positioned above the bottom bar
                .zIndex(100f)
        ) {
            CustomToast(
                message = "Empty list discarded",
                type = ToastType.DEFAULT
            )
        }
    }
}

// ========================================== DETAIL SCREEN ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistDetailScreen(
    checklist: Checklist? = null,
    onBackClick: (Checklist?) -> Unit,
    onDelete: (String) -> Unit = {},
    onTogglePin: (String) -> Unit = {},
    onArchive: (String) -> Unit = {},
    isArchived: Boolean = false
) {
    var title by remember { mutableStateOf(checklist?.title ?: "") }
    var items by remember {
        mutableStateOf(
            checklist?.items ?: emptyList()
        )
    }
    var bgColor by remember { mutableStateOf(checklist?.bgColor ?: SoftMint) }
    var isPinned by remember { mutableStateOf(checklist?.isPinned ?: false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // Text Formatting states
    var isFormattingActive by remember { mutableStateOf(false) }
    var isBoldActive by remember { mutableStateOf(false) }
    var isItalicActive by remember { mutableStateOf(false) }
    var isUnderlineActive by remember { mutableStateOf(false) }

    // Undo/Redo State History
    val history = remember { mutableStateListOf<Pair<String, List<ChecklistItem>>>() }
    var historyIndex by remember { mutableIntStateOf(-1) }

    // Initialize snapshot
    LaunchedEffect(checklist) {
        if (history.isEmpty()) {
            history.add(Pair(title, items))
            historyIndex = 0
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

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(Color.Transparent)
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    onBackClick = {
                        val result = Checklist(
                            id = checklist?.id ?: UUID.randomUUID().toString(),
                            title = title,
                            dateTime = checklist?.dateTime ?: "Today, 09:30 PM",
                            items = items,
                            bgColor = bgColor,
                            isPinned = isPinned
                        )
                        onBackClick(result)
                    },
                    backIcon = TopIcon.CustomPainter(painterResource(R.drawable.ic_check)),
                    secondaryIcon =  if(isPinned) TopIcon.Predefined.PIN_FILLED else TopIcon.Predefined.PIN,
                    onSecondaryClick = {
                        isPinned = !isPinned
                        checklist?.id?.let { onTogglePin(it) }
                    },
                    onMenuClick = { showMenu = true },
                    buttonStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f
                )
            }
        },
        containerColor = bgColor
    ) { paddingValues ->
        val imeBottomPadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
        val adjustedBottomPadding = (imeBottomPadding - 100.dp).coerceAtLeast(0.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = adjustedBottomPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = checklist?.dateTime ?: "Today, 09:30 PM",
                    style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = ContentSecondary,
                )
                Spacer(Modifier.height(8.dp))

                BasicTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        saveToHistory(it, items)
                    },
                    textStyle = JasnifyTheme.typography.headingXLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                "Title",
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
                    itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                        val focusRequester = remember { FocusRequester() }
                        val isDragging = draggedItemIndex == index

                        Box(
                            modifier = Modifier
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
                                onTextChanged = { newText ->
                                    val updated = items.map { if (it.id == item.id) it.copy(text = newText) else it }
                                    items = updated
                                    saveToHistory(title, updated)
                                },
                                onCheckedChange = { isChecked ->
                                    val updated = items.map { if (it.id == item.id) it.copy(isChecked = isChecked) else it }
                                    items = updated
                                    saveToHistory(title, updated)
                                },
                                onRemove = {
                                    val updated = items.filter { it.id != item.id }
                                    items = updated
                                    saveToHistory(title, updated)
                                },
                                modifier = Modifier.pointerInput(Unit) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { draggedItemIndex = index },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffset += dragAmount.y

                                            val targetIndex = (index + (dragOffset / 60f).toInt()).coerceIn(0, items.size - 1)
                                            if (targetIndex != index && draggedItemIndex != null) {
                                                val newList = items.toMutableList()
                                                val movingItem = newList.removeAt(index)
                                                newList.add(targetIndex, movingItem)
                                                items = newList
                                                draggedItemIndex = targetIndex
                                                dragOffset = 0f
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
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val updated = items + ChecklistItem(id = UUID.randomUUID().toString())
                                    items = updated
                                    saveToHistory(title, updated)
                                }
                                .padding(start = 32.dp, 8.dp, 8.dp, 8.dp)
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
                onColorClick = { showColorPicker = true }
            )
        }
    }

    if (showColorPicker) {
        ColorPickerBottomSheet(
            selectedColor = bgColor,
            onColorSelected = {
                bgColor = it
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }

    if (showMenu) {
        MenuBottomSheet(
            items = listOf(
                MenuSheetActionItem(
                    text = if (isArchived) "Unarchive" else "Archive",
                    icon = painterResource(R.drawable.ic_box),
                    onClick = {
                        showMenu = false
                        checklist?.id?.let { onArchive(it) }
                    }
                ),
                MenuSheetActionItem(
                    text = "Delete",
                    icon = painterResource(R.drawable.ic_delete),
                    contentColor = Color.Red,
                    onClick = {
                        showMenu = false
                        showDeleteConfirmation = true
                    }
                )
            ),
            onCancelClick = { showMenu = false }
        )
    }

    if (showDeleteConfirmation) {
        CustomDeleteSheet(
            heading = "Are you sure?",
            subHeading = "The checklist will be deleted permanently.",
            confirmButtonText = "Delete Checklist",
            onDismiss = { showDeleteConfirmation = false },
            onConfirmRemove = {
                showDeleteConfirmation = false
                checklist?.id?.let { onDelete(it) }
            }
        )
    }
}

// ========================================== ARCHIVES SCREEN ==========================================

@Composable
fun ChecklistArchivesScreen(
    archivedChecklists: List<Checklist>,
    onBackClick: () -> Unit,
    onChecklistClick: (Checklist) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredChecklists = remember(searchQuery, selectedFilter, archivedChecklists) {
        archivedChecklists.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.items.any { item -> item.text.contains(searchQuery, ignoreCase = true) }
        }.let { list ->
            when (selectedFilter) {
                "Recent First" -> list.sortedByDescending { it.dateTime }
                "Oldest First" -> list.sortedBy { it.dateTime }
                else -> list
            }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(BackgroundPrimary)
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Archives",
                    onBackClick = onBackClick,
                    backIcon = TopIcon.Predefined.BACK,
                    buttonStyle = ButtonBackground.OPAQUE,
                    isLargeTitle = true
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Spacer(Modifier.height(12.dp))

            // Search Bar
            CustomSearchBar(
                value = searchQuery,
                onValueChange = {searchQuery = it },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp),
                onActiveChange = { }
            )

            Spacer(Modifier.height(12.dp))

            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "All",
                    isSelected = selectedFilter == "All",
                    onClick = { selectedFilter = "All" },
                    hasStroke = true,
                    shapeStyle = ChipShapeStyle.Round
                )
                FilterChip(
                    label = "Recent First",
                    isSelected = selectedFilter == "Recent First",
                    onClick = { selectedFilter = "Recent First" },
                    hasStroke = true,
                    shapeStyle = ChipShapeStyle.Round
                )
                FilterChip(
                    label = "Oldest First",
                    isSelected = selectedFilter == "Oldest First",
                    onClick = { selectedFilter = "Oldest First" },
                    hasStroke = true,
                    shapeStyle = ChipShapeStyle.Round
                )
            }

            // Archived List
            LazyColumn(
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp)
            ) {
                items(items = filteredChecklists, key = { it.id }) { checklist ->
                    ChecklistCard(
                        checklist = checklist,
                        onClick = { onChecklistClick(checklist) }
                    )
                }
            }
        }
    }
}


// ========================================== HELPER COMPONENTS ==========================================


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
            // STATE 2: TEXT FORMATTING STATE
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
            // STATE 1: DEFAULT STATE
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
                    iconColor = if(canUndo) ContentPrimary else ContentTertiary,
                    onClick = onUndo
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_redo)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    iconColor = if(canRedo) ContentPrimary else ContentTertiary,
                    onClick = onRedo
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = listOf(
        NeutralWhite, SoftMint, PaleLavender, LightSkyBlue, SoftPeach,
        Color(0xFFD7E3E3), Color(0xFFF2EFEA), Color(0xFFDFE5F3), Color(0xFFDAE8D8),
        Color(0xFFF4E3E2), Color(0xFFE9EDF7), Color(0xFFE6E3F2)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = selectedColor,
        shape = RoundedCornerShape(topStart = CornerExtraLarge, topEnd = CornerExtraLarge),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(56.dp)
                    .height(4.dp)
                    .background(ContentTertiary, shape = RoundedCornerShape(100))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Select Background Color",
                style = JasnifyTheme.typography.headingLarge,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.take(6).forEach { color ->
                        ColorCircle(
                            color = color,
                            isSelected = color == selectedColor,
                            onClick = { onColorSelected(color) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.drop(6).take(6).forEach { color ->
                        ColorCircle(
                            color = color,
                            isSelected = color == selectedColor,
                            onClick = { onColorSelected(color) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextButton(
                text = "Done",
                onClick = onDismiss,
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

@Preview(showBackground = true)
@Composable
fun ChecklistsTabPreview() {
    JasnifyTheme {
        ChecklistsTab()
    }
}