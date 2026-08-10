package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.InvitationCardData
import com.harshdeep.jasnify.domain.model.TextElement
import com.harshdeep.jasnify.domain.model.FontStyleType
import com.harshdeep.jasnify.domain.model.defaultElements
import com.harshdeep.jasnify.presentation.components.cards.InvitationCardItem
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditInvitationDetailsScreen(
    initialData: InvitationCardData = InvitationCardData(),
    onDataChange: (InvitationCardData) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var editingData by remember { mutableStateOf(initialData) }
    var selectedElementId by remember { mutableStateOf<String?>(null) }

    val selectedElement = editingData.elements.find { it.id == selectedElementId }

    // Helper to update individual element
    fun updateElement(updated: TextElement) {
        editingData = editingData.copy(
            elements = editingData.elements.map { if (it.id == updated.id) updated else it }
        )
    }

    // Helper to add new text element
    fun addNewElement(text: String, sizeSp: Float, isBold: Boolean = false, fontStyle: FontStyleType = FontStyleType.DEFAULT) {
        val newElem = TextElement(
            text = text,
            xRatio = 0.5f,
            yRatio = 0.5f,
            fontSizeSp = sizeSp,
            isBold = isBold,
            fontStyle = fontStyle,
            zIndex = (editingData.elements.maxOfOrNull { it.zIndex } ?: 0) + 1
        )
        editingData = editingData.copy(elements = editingData.elements + newElem)
        selectedElementId = newElem.id
    }

    // Helper to move element up/down (swap logic)
    fun moveElement(elementId: String, isUp: Boolean) {
        val sorted = editingData.elements.sortedBy { it.yRatio }
        val currentIndex = sorted.indexOfFirst { it.id == elementId }
        if (currentIndex == -1) return

        val targetIndex = if (isUp) currentIndex - 1 else currentIndex + 1
        if (targetIndex in sorted.indices) {
            val current = sorted[currentIndex]
            val target = sorted[targetIndex]

            val updatedElements = editingData.elements.map {
                when (it.id) {
                    current.id -> it.copy(yRatio = target.yRatio)
                    target.id -> it.copy(yRatio = current.yRatio)
                    else -> it
                }
            }
            editingData = editingData.copy(elements = updatedElements)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Design Studio", style = JasnifyTheme.typography.headingMedium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            onDataChange(editingData)
                            onBackClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF536E6D)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Design", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundPrimary)
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Interactive Canvas Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        selectedElementId = null // Deselect on background tap
                    },
                contentAlignment = Alignment.Center
            ) {
                InteractiveCardCanvas(
                    card = editingData,
                    selectedElementId = selectedElementId,
                    onSelectElement = { selectedElementId = it },
                    onUpdateElement = ::updateElement,
                    onMoveElement = ::moveElement,
                    onDeleteElement = { id ->
                        editingData = editingData.copy(elements = editingData.elements.filter { it.id != id })
                        if (selectedElementId == id) selectedElementId = null
                    },
                    modifier = Modifier
                        .fillMaxHeight(0.9f)
                        .aspectRatio(280f / 373f)
                        .shadow(16.dp, RoundedCornerShape(12.dp))
                )
            }

            // Bottom Tool Palette / Inspector
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfacePrimary,
                shadowElevation = 16.dp
            ) {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    if (selectedElement != null) {
                        // Element Inspector Controls
                        ElementInspectorSheet(
                            element = selectedElement,
                            onUpdate = ::updateElement,
                            onDelete = {
                                editingData = editingData.copy(elements = editingData.elements.filter { it.id != selectedElement.id })
                                selectedElementId = null
                            }
                        )
                    } else {
                        // Global Card Controls & Add Buttons
                        GlobalCardSheet(
                            card = editingData,
                            onUpdateCard = { editingData = it },
                            onAddHeading = { addNewElement("HEADING TEXT", 24f, isBold = true, FontStyleType.PATTAYA) },
                            onAddSubheading = { addNewElement("SUBHEADING", 14f, isBold = true) },
                            onAddBody = { addNewElement("Add body text details here", 10f) },
                            onResetLayout = {
                                editingData = editingData.copy(elements = defaultElements())
                            }
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// INTERACTIVE CARD CANVAS & GESTURE SYSTEM
// ============================================================================

@Composable
fun InteractiveCardCanvas(
    card: InvitationCardData,
    selectedElementId: String?,
    onSelectElement: (String) -> Unit,
    onUpdateElement: (TextElement) -> Unit,
    onMoveElement: (String, Boolean) -> Unit,
    onDeleteElement: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .onGloballyPositioned { canvasSize = it.size }
            .clip(SquircleShape(CornerLargeIncrease))
            .background(Color(card.backgroundColorHex))
            .border(1.dp, Color.Black.copy(0.1f), SquircleShape(CornerLargeIncrease))
    ) {
        // Card Background Image
        Image(
            painter = painterResource(id = card.backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Render sorted elements by Z-Index
        card.elements.sortedBy { it.zIndex }.forEach { element ->
            val isSelected = element.id == selectedElementId

            InteractiveTextItem(
                element = element,
                canvasSize = canvasSize,
                isSelected = isSelected,
                onSelect = { onSelectElement(element.id) },
                onUpdate = onUpdateElement,
                onMove = { isUp -> onMoveElement(element.id, isUp) },
                onDelete = { onDeleteElement(element.id) }
            )
        }
    }
}

@Composable
fun InteractiveTextItem(
    element: TextElement,
    canvasSize: IntSize,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onUpdate: (TextElement) -> Unit,
    onMove: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    if (canvasSize.width == 0 || canvasSize.height == 0) return

    val density = androidx.compose.ui.platform.LocalDensity.current.density
    val canvasWidthPx = canvasSize.width.toFloat()
    val canvasHeightPx = canvasSize.height.toFloat()

    // Force horizontal center position for the container
    val posX = 0.5f * canvasWidthPx
    val posY = element.yRatio * canvasHeightPx

    Box(
        modifier = Modifier
            .fillMaxWidth() // Fill width to allow aligning arrows to the card's right side
            .offset(y = (posY / density).dp)
            .graphicsLayer {
                translationY = -size.height / 2f
            }
    ) {
        // The Text Content (Centered horizontally)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    rotationZ = element.rotationDegrees
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onSelect() }
                .then(
                    if (isSelected) {
                        Modifier
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    } else {
                        Modifier.padding(8.dp)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = element.text,
                style = TextStyle(
                    fontFamily = element.fontStyle.fontFamily,
                    fontSize = element.fontSizeSp.sp,
                    fontWeight = if (element.isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (element.isItalic) FontStyle.Italic else FontStyle.Normal,
                    color = Color(element.colorHex),
                    textAlign = element.textAlign,
                    letterSpacing = element.letterSpacingSp.sp,
                    lineHeight = if (element.lineHeightSp > 0) element.lineHeightSp.sp else TextUnit.Unspecified
                )
            )

            // Delete Button top-right of selection
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 12.dp, y = (-12).dp)
                        .size(22.dp)
                        .background(Color.Red, CircleShape)
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Vertical Swap Controls (Fixed to center right of the card row)
        if (isSelected) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .background(SurfacePrimary.copy(alpha = 0.9f), CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), CircleShape)
            ) {
                IconButton(
                    onClick = { onMove(true) }, // Move Up (Swap with element above)
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", modifier = Modifier.size(24.dp))
                }
                IconButton(
                    onClick = { onMove(false) }, // Move Down (Swap with element below)
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

// ============================================================================
// INSPECTOR PANELS & EDITING CONTROLS
// ============================================================================

enum class EditorToolTab { TEXT, FONT, SIZE, COLOR, ALIGN }

@Composable
fun ElementInspectorSheet(
    element: TextElement,
    onUpdate: (TextElement) -> Unit,
    onDelete: () -> Unit
) {
    var activeTab by remember { mutableStateOf(EditorToolTab.TEXT) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header Info Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Edit Text Element",
                style = JasnifyTheme.typography.headingSmall,
                color = ContentPrimary
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }

        // Tool Selector Tabs
        ScrollableTabRow(
            selectedTabIndex = activeTab.ordinal,
            edgePadding = 0.dp,
            divider = {},
            containerColor = Color.Transparent
        ) {
            EditorToolTab.values().forEach { tab ->
                Tab(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    text = { Text(tab.name, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        when (activeTab) {
            EditorToolTab.TEXT -> {
                OutlinedTextField(
                    value = element.text,
                    onValueChange = { onUpdate(element.copy(text = it)) },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )
            }
            EditorToolTab.FONT -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FontStyleType.values()) { fontStyle ->
                        FilterChip(
                            selected = element.fontStyle == fontStyle,
                            onClick = { onUpdate(element.copy(fontStyle = fontStyle)) },
                            label = {
                                Text(
                                    text = fontStyle.label,
                                    fontFamily = fontStyle.fontFamily
                                )
                            }
                        )
                    }
                }
            }
            EditorToolTab.SIZE -> {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Font Size: ${element.fontSizeSp.toInt()} sp", modifier = Modifier.weight(1f))
                        IconButton(onClick = { onUpdate(element.copy(fontSizeSp = (element.fontSizeSp - 1).coerceAtLeast(6f))) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        IconButton(onClick = { onUpdate(element.copy(fontSizeSp = element.fontSizeSp + 1)) }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                    Slider(
                        value = element.fontSizeSp,
                        onValueChange = { onUpdate(element.copy(fontSizeSp = it)) },
                        valueRange = 8f..72f
                    )

                    Text("Letter Spacing: ${String.format("%.1f", element.letterSpacingSp)}")
                    Slider(
                        value = element.letterSpacingSp,
                        onValueChange = { onUpdate(element.copy(letterSpacingSp = it)) },
                        valueRange = -1f..5f
                    )

                    Text("Line Height: ${if (element.lineHeightSp > 0) String.format("%.1f", element.lineHeightSp) else "Default"}")
                    Slider(
                        value = element.lineHeightSp,
                        onValueChange = { onUpdate(element.copy(lineHeightSp = it)) },
                        valueRange = 0f..100f
                    )
                }
            }
            EditorToolTab.COLOR -> {
                val colors = listOf(
                    0xFF000000L, 0xFFFFFFFFL, 0xFF444444L, 0xFF8E8E8EL,
                    0xFFA6852FL, 0xFF536E6DL, 0xFFB23B3BL, 0xFF2B5B84L, 0xFF6B4226L
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(colors) { colorHex ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (element.colorHex == colorHex) 3.dp else 1.dp,
                                    color = if (element.colorHex == colorHex) MaterialTheme.colorScheme.primary else Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable { onUpdate(element.copy(colorHex = colorHex)) }
                        )
                    }
                }
            }
            EditorToolTab.ALIGN -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { onUpdate(element.copy(isBold = !element.isBold)) }) {
                        Text("B", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (element.isBold) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                    IconButton(onClick = { onUpdate(element.copy(isItalic = !element.isItalic)) }) {
                        Text("I", fontStyle = FontStyle.Italic, fontSize = 18.sp, color = if (element.isItalic) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                    IconButton(onClick = { onUpdate(element.copy(textAlign = TextAlign.Left)) }) {
                        Icon(Icons.Default.FormatAlignLeft, contentDescription = "Left", tint = if (element.textAlign == TextAlign.Left) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                    IconButton(onClick = { onUpdate(element.copy(textAlign = TextAlign.Center)) }) {
                        Icon(Icons.Default.FormatAlignCenter, contentDescription = "Center", tint = if (element.textAlign == TextAlign.Center) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                    IconButton(onClick = { onUpdate(element.copy(textAlign = TextAlign.Right)) }) {
                        Icon(Icons.Default.FormatAlignRight, contentDescription = "Right", tint = if (element.textAlign == TextAlign.Right) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalCardSheet(
    card: InvitationCardData,
    onUpdateCard: (InvitationCardData) -> Unit,
    onAddHeading: () -> Unit,
    onAddSubheading: () -> Unit,
    onAddBody: () -> Unit,
    onResetLayout: () -> Unit
) {
    val backgrounds = listOf(
        R.drawable.bg_invitation_card_01,
        R.drawable.bg_invitation_card_02,
        R.drawable.bg_invitation_card_03,
        R.drawable.bg_invitation_card_04,
        R.drawable.bg_invitation_card_05
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Choose Background:",
            style = JasnifyTheme.typography.labelMedium,
            color = ContentSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(backgrounds) { bgRes ->
                Box(
                    modifier = Modifier
                        .size(60.dp, 80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (card.backgroundRes == bgRes) 2.dp else 1.dp,
                            color = if (card.backgroundRes == bgRes) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onUpdateCard(card.copy(backgroundRes = bgRes)) }
                ) {
                    Image(
                        painter = painterResource(id = bgRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Add new elements:",
            style = JasnifyTheme.typography.labelMedium,
            color = ContentSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onAddHeading,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Header", fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = onAddSubheading,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Subheader", fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = onAddBody,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Body Text", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onResetLayout,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Reset to Template", color = Color.Gray, fontSize = 12.sp)
        }
    }
}
