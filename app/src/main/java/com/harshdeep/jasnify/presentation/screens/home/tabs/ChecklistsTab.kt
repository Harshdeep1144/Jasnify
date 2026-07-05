package com.harshdeep.jasnify.presentation.screens.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.Checklist
import com.harshdeep.jasnify.presentation.components.cards.ChecklistCard
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.others.ChecklistItem
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.*
import java.util.*

@Composable
fun ChecklistsTab() {
    var isGridView by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("All") }
    var showMenuSheet by remember { mutableStateOf(false) }
    var selectedChecklist by remember { mutableStateOf<Checklist?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    
    // Manage checklists state
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
//    selectedChecklist != null || isAddingNew
    if (true) {
        ChecklistDetailScreen(
            checklist = selectedChecklist,
            onBackClick = { updatedChecklist ->
                if (updatedChecklist != null) {
                    val index = checklists.indexOfFirst { it.id == updatedChecklist.id }
                    if (index != -1) {
                        checklists[index] = updatedChecklist
                    } else {
                        checklists.add(0, updatedChecklist)
                    }
                }
                selectedChecklist = null
                isAddingNew = false
            },
            onDelete = { id ->
                checklists.removeAll { it.id == id }
                selectedChecklist = null
                isAddingNew = false
            },
            onTogglePin = { id ->
                val index = checklists.indexOfFirst { it.id == id }
                if (index != -1) {
                    checklists[index] = checklists[index].copy(isPinned = !checklists[index].isPinned)
                }
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
                    .padding(paddingValues)
            ) {
                // Filter Chips
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
                    "Recent First" -> checklists.sortedByDescending { it.dateTime } // Simplified sorting
                    "Oldest First" -> checklists.sortedBy { it.dateTime }
                    else -> checklists.sortedByDescending { it.isPinned }
                }

                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(sortedChecklists, key = { it.id }) { checklist ->
                            ChecklistCard(
                                checklist = checklist,
                                onClick = { selectedChecklist = checklist }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(sortedChecklists, key = { it.id }) { checklist ->
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
                    onClick = { showMenuSheet = false }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistDetailScreen(
    checklist: Checklist? = null,
    onBackClick: (Checklist?) -> Unit,
    onDelete: (String) -> Unit = {},
    onTogglePin: (String) -> Unit = {}
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

    // Drag and drop state
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

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
                    secondaryIcon = TopIcon.Predefined.PIN,
                    onSecondaryClick = {
                        isPinned = !isPinned
                        checklist?.id?.let { onTogglePin(it) }
                    },
                    onMenuClick = {},
                    buttonStyle = ButtonBackground.OPAQUE,
                )
            }
        },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                onValueChange = { title = it },
                textStyle = JasnifyTheme.typography.headingXLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            "Title",
                            style = JasnifyTheme.typography.headingXLarge.copy(fontWeight = FontWeight.Medium),
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
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
                                items = items.map { if (it.id == item.id) it.copy(text = newText) else it }
                            },
                            onCheckedChange = { isChecked ->
                                items = items.map { if (it.id == item.id) it.copy(isChecked = isChecked) else it }
                            },
                            onRemove = {
                                items = items.filter { it.id != item.id }
                            },
                            modifier = Modifier.pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { draggedItemIndex = index },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount.y
                                        
                                        // Simple reordering logic
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
                                items = items + ChecklistItem(id = UUID.randomUUID().toString())
                            }
                            .padding(start = 32.dp)
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
            
            // Bottom bar icons for color picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { showColorPicker = true }) {
                    Icon(painter = painterResource(id = R.drawable.ic_ai), contentDescription = "Change color", tint = ContentPrimary)
                }
            }
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
        containerColor = SurfacePrimary,
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
