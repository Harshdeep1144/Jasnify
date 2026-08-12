package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.FontStyleType
import com.harshdeep.jasnify.domain.model.InvitationCardData
import com.harshdeep.jasnify.domain.model.TextElement
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape
import kotlin.math.abs

enum class EditorTab(val label: String) {
    THEME("Theme"),
    TEXT("Text"),
    FONT("Font"),
    SIZE("Size"),
    COLOR("Color"),
    FORMAT("Format"),
    ADD_ROW("+ Add Row")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditInvitationDetailsScreen(
    initialData: InvitationCardData = InvitationCardData(),
    onDataChange: (InvitationCardData) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    // ------------------------------------------------------------------------
    // State & History Management (Undo / Redo)
    // ------------------------------------------------------------------------
    val history = remember { mutableStateListOf(initialData) }
    var historyIndex by remember { mutableIntStateOf(0) }
    val currentCard = history.getOrElse(historyIndex) { initialData }

    var selectedElementId by remember { mutableStateOf<String?>(null) }
    var activeTab by remember { mutableStateOf(EditorTab.TEXT) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    // Dynamic Sheet Height & Weight Management
    var bottomSheetWeight by remember { mutableFloatStateOf(0.42f) }
    val minSheetWeight = 0.32f
    val maxSheetWeight = 0.64f
    var parentHeightPx by remember { mutableFloatStateOf(0f) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    // IME Keyboard Visibility
    val isImeVisible = WindowInsets.isImeVisible

    // Helper to commit changes into history
    fun updateCardState(newCard: InvitationCardData) {
        if (newCard == currentCard) return
        while (history.size - 1 > historyIndex) {
            history.removeAt(history.size - 1)
        }
        history.add(newCard)
        historyIndex = history.size - 1
    }

    fun undo() { if (historyIndex > 0) historyIndex-- }
    fun redo() { if (historyIndex < history.size - 1) historyIndex++ }

    fun updateElement(updated: TextElement) {
        val newElements = currentCard.elements.map { if (it.id == updated.id) updated else it }
        updateCardState(currentCard.copy(elements = newElements))
    }

    fun swapElements(id1: String, id2: String) {
        val e1 = currentCard.elements.find { it.id == id1 } ?: return
        val e2 = currentCard.elements.find { it.id == id2 } ?: return

        val updatedElements = currentCard.elements.map {
            when (it.id) {
                id1 -> it.copy(yRatio = e2.yRatio)
                id2 -> it.copy(yRatio = e1.yRatio)
                else -> it
            }
        }
        updateCardState(currentCard.copy(elements = updatedElements))
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val selectedElement = currentCard.elements.find { it.id == selectedElementId }

    // Dynamic vertical shift calculation for Canvas based on selected text element's yRatio
    val targetCanvasOffsetY = remember(isImeVisible, selectedElement) {
        if (isImeVisible && selectedElement != null) {
            val ratio = selectedElement.yRatio.coerceIn(0f, 1f)
            // Increased upward shift (-220dp for top elements, up to -480dp for bottom elements)
            (-220 - (ratio * 260)).dp
        } else {
            0.dp
        }
    }

    val animatedCanvasOffsetY by animateDpAsState(
        targetValue = targetCanvasOffsetY,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "CanvasOffsetAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ContentPrimary)
            .onGloballyPositioned { coordinates ->
                parentHeightPx = coordinates.size.height.toFloat()
            }
    ) {
        // ================================================================
        // 1. TOP BAR (Floats over canvas)
        // ================================================================

        SetStatusBarTheme(useDarkIcons = false)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = TopGradientBrush
                )
                .statusBarsPadding()
                .zIndex(10f)
                .padding(12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(10f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomIconButton(
                    icon = rememberVectorPainter(image = Icons.Rounded.Close),
                    onClick = onBackClick,
                    size = ButtonSize.Small,
                    contentColor = ContentInvPrimary,
                    containerColor = Color(0xE53D3D3D)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CustomIconButton(
                        icon = painterResource(R.drawable.ic_undo),
                        onClick = ::undo,
                        size = ButtonSize.Small,
                        contentColor = if (historyIndex > 0) ContentInvPrimary else ContentSecondary,
                        containerColor = Color(0xE53D3D3D)
                    )
                    Spacer(Modifier.width(8.dp))
                    CustomIconButton(
                        icon = painterResource(R.drawable.ic_redo),
                        onClick = ::redo,
                        size = ButtonSize.Small,
                        contentColor = if (historyIndex < history.size - 1) ContentInvPrimary else ContentSecondary,
                        containerColor = Color(0xE53D3D3D)
                    )
                }

                CustomTextButton(
                    text = "Save",
                    onClick = {
                        onDataChange(currentCard)
                        onBackClick()
                    },
                    leadingIcon = painterResource(R.drawable.ic_check),
                    size = ButtonSize.Small,
                    contentColor = ContentPrimary,
                    containerColor = ContentInvPrimary
                )
            }
        }

        // ================================================================
        // 2. INTERACTIVE CANVAS AREA (Dynamically offset when keyboard is active)
        // ================================================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .zIndex(0f)
                .padding(top = 64.dp, bottom = if (isImeVisible) 0.dp else (parentHeightPx * bottomSheetWeight / density.density).dp)
                .offset(y = animatedCanvasOffsetY),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val cardWidth = minOf(
                    maxWidth,
                    maxHeight * (3f / 4f)
                )

                InteractiveCardCanvas(
                    card = currentCard,
                    selectedElementId = selectedElementId,
                    onSelectElement = { id -> selectedElementId = id },
                    onDoubleTapElement = { id ->
                        selectedElementId = id
                        activeTab = EditorTab.TEXT
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    },
                    onUpdateElement = ::updateElement,
                    onSwapElements = ::swapElements,
                    onDeleteElement = { id ->
                        val remaining = currentCard.elements.filter { it.id != id }
                        updateCardState(currentCard.copy(elements = remaining))
                        if (selectedElementId == id) selectedElementId = null
                    },
                    modifier = Modifier
                        .width(cardWidth)
                        .aspectRatio(3f / 4f)
                        .shadow(16.dp, SquircleShape(CornerLargeIncrease))
                )
            }
        }

        // ================================================================
        // 3. OVERLAPPING BOTTOM SHEET EDITOR
        // ================================================================
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .then(
                    if (isImeVisible) Modifier.wrapContentHeight()
                    else Modifier.fillMaxHeight(bottomSheetWeight)
                )
                .imePadding()
                .zIndex(5f)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isImeVisible) Modifier.wrapContentHeight() else Modifier.fillMaxHeight()),
                shape = RoundedCornerShape(topStart = CornerExtraLarge, topEnd = CornerExtraLarge),
                color = BackgroundSecondary,
                shadowElevation = 24.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // DRAG HANDLE
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectVerticalDragGestures { change, dragAmount ->
                                    change.consume()
                                    if (parentHeightPx > 0f) {
                                        val deltaWeight = -dragAmount / parentHeightPx
                                        bottomSheetWeight = (bottomSheetWeight + deltaWeight)
                                            .coerceIn(minSheetWeight, maxSheetWeight)
                                    }
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.4f))
                        )
                    }

                    // HEADER TITLE + DONE BUTTON
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectVerticalDragGestures { change, dragAmount ->
                                    change.consume()
                                    if (parentHeightPx > 0f) {
                                        val deltaWeight = -dragAmount / parentHeightPx
                                        bottomSheetWeight = (bottomSheetWeight + deltaWeight)
                                            .coerceIn(minSheetWeight, maxSheetWeight)
                                    }
                                }
                            }
                            .padding(start = 24.dp, end = 24.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (activeTab) {
                                EditorTab.THEME -> "Select Theme"
                                EditorTab.TEXT -> "Edit Text"
                                EditorTab.FONT -> "Edit Font"
                                EditorTab.SIZE -> "Edit Size"
                                EditorTab.COLOR -> "Edit Color"
                                EditorTab.FORMAT -> "Edit Format"
                                EditorTab.ADD_ROW -> "Add Row"
                            },
                            style = JasnifyTheme.typography.headingMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )

                        if (activeTab == EditorTab.TEXT && isTextFieldFocused) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = Color.White,
                                onClick = { keyboardController?.hide() },
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.Black, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    // TAB ROW
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(EditorTab.entries) { tab ->
                            val isSelected = activeTab == tab
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .clickable { activeTab = tab }
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab.label,
                                    style = JasnifyTheme.typography.labelLarge,
                                    color = if (isSelected) Color(0xFF005D5D) else Color(0xFF757575),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // TAB CONTENT CONTAINER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (isImeVisible) Modifier.wrapContentHeight() else Modifier.weight(1f))
                            .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (activeTab) {
                            EditorTab.THEME -> ThemeSelectorSection(currentCard.backgroundRes) { updateCardState(currentCard.copy(backgroundRes = it)) }
                            EditorTab.TEXT -> {
                                if (selectedElement != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFE5E5E5))
                                            .padding(16.dp)
                                    ) {
                                        BasicTextField(
                                            value = selectedElement.text,
                                            onValueChange = { updateElement(selectedElement.copy(text = it)) },
                                            textStyle = JasnifyTheme.typography.bodyLarge.copy(color = Color.Black),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .focusRequester(focusRequester)
                                                .onFocusChanged { isTextFieldFocused = it.isFocused },
                                            minLines = 2
                                        )
                                    }
                                } else Text("Tap any text on the card to edit", color = ContentSecondary)
                            }
                            EditorTab.FONT -> {
                                if (selectedElement != null) {
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Text("Font Family", style = JasnifyTheme.typography.labelMedium, color = Color.Gray, fontWeight = FontWeight.Bold)
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            items(FontStyleType.entries) { fontType ->
                                                val isSelected = selectedElement.fontStyle == fontType
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = if (isSelected) Color(0xFF005D5D) else Color(0xFFF5F5F5),
                                                    modifier = Modifier.clickable { updateElement(selectedElement.copy(fontStyle = fontType)) }
                                                ) {
                                                    Text(
                                                        text = fontType.label,
                                                        fontFamily = fontType.fontFamily,
                                                        color = if (isSelected) Color.White else Color.Black,
                                                        style = JasnifyTheme.typography.bodyMedium,
                                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            EditorTab.SIZE -> {
                                if (selectedElement != null) {
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        SliderWithLabel("Font Size", selectedElement.fontSizeSp, { updateElement(selectedElement.copy(fontSizeSp = it)) }, 8f..72f, "px", Icons.Default.FormatSize)
                                        SliderWithLabel("Vertical Padding", selectedElement.verticalPaddingSp, { updateElement(selectedElement.copy(verticalPaddingSp = it)) }, 0f..100f, "px", Icons.Default.Height)
                                        SliderWithLabel("Line Height", selectedElement.lineHeightSp, { updateElement(selectedElement.copy(lineHeightSp = it)) }, 0f..100f, "px", Icons.Default.FormatLineSpacing)
                                        SliderWithLabel("Letter Spacing", selectedElement.letterSpacingSp, { updateElement(selectedElement.copy(letterSpacingSp = it)) }, -2f..10f, "px", Icons.Default.FormatLineSpacing)
                                    }
                                }
                            }
                            EditorTab.COLOR -> {
                                if (selectedElement != null) {
                                    val palette = listOf(0xFF000000L, 0xFFFFFFFFL, 0xFF444444L, 0xFF8E8E8EL, 0xFFA6852FL, 0xFF536E6DL, 0xFFB23B3BL, 0xFF2B5B84L, 0xFF6B4226L)
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        items(palette) { hex ->
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(hex))
                                                    .border(
                                                        width = if (selectedElement.colorHex == hex) 3.dp else 1.dp,
                                                        color = if (selectedElement.colorHex == hex) Color(0xFF005D5D) else Color.LightGray,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { updateElement(selectedElement.copy(colorHex = hex)) }
                                            )
                                        }
                                    }
                                }
                            }
                            EditorTab.FORMAT -> {
                                if (selectedElement != null) {
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            FormatToggleButton(Icons.Default.FormatBold, selectedElement.isBold) { updateElement(selectedElement.copy(isBold = !selectedElement.isBold)) }
                                            FormatToggleButton(Icons.Default.FormatItalic, selectedElement.isItalic) { updateElement(selectedElement.copy(isItalic = !selectedElement.isItalic)) }
                                            FormatToggleButton(Icons.Default.FormatUnderlined, selectedElement.isUnderline) { updateElement(selectedElement.copy(isUnderline = !selectedElement.isUnderline)) }
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            FormatToggleButton(Icons.Default.FormatAlignLeft, selectedElement.textAlign == TextAlign.Left) { updateElement(selectedElement.copy(textAlign = TextAlign.Left)) }
                                            FormatToggleButton(Icons.Default.FormatAlignCenter, selectedElement.textAlign == TextAlign.Center) { updateElement(selectedElement.copy(textAlign = TextAlign.Center)) }
                                            FormatToggleButton(Icons.Default.FormatAlignRight, selectedElement.textAlign == TextAlign.Right) { updateElement(selectedElement.copy(textAlign = TextAlign.Right)) }
                                        }
                                    }
                                }
                            }
                            EditorTab.ADD_ROW -> {
                                Button(
                                    onClick = {
                                        val newElement = TextElement(text = "New Text", yRatio = 0.5f)
                                        updateCardState(currentCard.copy(elements = currentCard.elements + newElement))
                                        selectedElementId = newElement.id
                                        activeTab = EditorTab.TEXT
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005D5D))
                                ) { Text("Add New Text Row") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormatToggleButton(icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF005D5D) else Color(0xFFF5F5F5),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) Color.White else Color.Black)
        }
    }
}

@Composable
fun SliderWithLabel(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                Text(label, color = Color.Gray, style = JasnifyTheme.typography.bodyMedium)
            }
            Text("${value.toInt()} $unit", color = Color.Black, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.LightGray, inactiveTrackColor = Color(0xFFEEEEEE)),
            modifier = Modifier.height(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
    }
}

@Composable
fun InteractiveCardCanvas(
    card: InvitationCardData,
    selectedElementId: String?,
    onSelectElement: (String) -> Unit,
    onDoubleTapElement: (String) -> Unit,
    onUpdateElement: (TextElement) -> Unit,
    onSwapElements: (String, String) -> Unit,
    onDeleteElement: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .onGloballyPositioned { canvasSize = it.size }
            .clip(SquircleShape(CornerLargeIncrease))
            .background(Color(card.backgroundColorHex))
    ) {
        Image(
            painter = painterResource(id = card.backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            card.elements.sortedBy { it.yRatio }.forEach { element ->
                InteractiveTextElementItem(
                    element = element,
                    canvasSize = canvasSize,
                    isSelected = element.id == selectedElementId,
                    otherElements = card.elements.filter { it.id != element.id },
                    onSelect = { onSelectElement(element.id) },
                    onDoubleTap = { onDoubleTapElement(element.id) },
                    onUpdate = onUpdateElement,
                    onSwap = onSwapElements,
                    onDelete = { onDeleteElement(element.id) }
                )
            }
        }
    }
}

@Composable
fun InteractiveTextElementItem(
    element: TextElement,
    canvasSize: IntSize,
    isSelected: Boolean,
    otherElements: List<TextElement>,
    onSelect: () -> Unit,
    onDoubleTap: () -> Unit,
    onUpdate: (TextElement) -> Unit,
    onSwap: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    if (canvasSize.width == 0 || canvasSize.height == 0) return
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    val currentElement by rememberUpdatedState(element)
    val currentOtherElements by rememberUpdatedState(otherElements)
    val currentOnUpdate by rememberUpdatedState(onUpdate)
    val currentOnSwap by rememberUpdatedState(onSwap)

    val canvasWidthPx = canvasSize.width.toFloat()

    // Track local drag offset for visual reordering feedback
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (dragOffsetY / density.density).dp)
            .zIndex(if (isDragging) 10f else 0f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = (canvasWidthPx * element.widthRatio / density.density).dp)
                .wrapContentWidth()
                .pointerInput(element.id) {
                    detectTapGestures(
                        onTap = { onSelect() },
                        onDoubleTap = { onDoubleTap() }
                    )
                }
                .pointerInput(element.id) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            isDragging = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffsetY += dragAmount.y

                            val threshold = 30f * density.density
                            if (abs(dragOffsetY) > threshold) {
                                val direction = if (dragOffsetY > 0) 1 else -1
                                val sorted = (currentOtherElements + currentElement).sortedBy { it.yRatio }
                                val currentIndex = sorted.indexOfFirst { it.id == currentElement.id }
                                val targetIndex = currentIndex + direction

                                if (targetIndex in sorted.indices) {
                                    val target = sorted[targetIndex]
                                    currentOnSwap(currentElement.id, target.id)
                                    dragOffsetY = 0f
                                }
                            }
                        },
                        onDragEnd = {
                            isDragging = false
                            dragOffsetY = 0f
                        },
                        onDragCancel = {
                            isDragging = false
                            dragOffsetY = 0f
                        }
                    )
                }
                .heightIn(min = element.verticalPaddingSp.dp)
                .then(
                    if (isSelected) Modifier
                        .border(2.dp, Color(0xFF6750A4), RoundedCornerShape(8.dp))
                    else Modifier
                ).padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = element.text,
                modifier = Modifier.wrapContentWidth(),
                style = TextStyle(
                    fontFamily = element.fontStyle.fontFamily,
                    fontSize = element.fontSizeSp.sp,
                    fontWeight = if (element.isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (element.isItalic) FontStyle.Italic else FontStyle.Normal,
                    textDecoration = if (element.isUnderline) TextDecoration.Underline else TextDecoration.None,
                    color = Color(element.colorHex),
                    textAlign = element.textAlign,
                    letterSpacing = element.letterSpacingSp.sp,
                    lineHeight = if (element.lineHeightSp > 0) element.lineHeightSp.sp else TextUnit.Unspecified,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    ),
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                )
            )
            if (isSelected) {
                // handles... (Delete, Width, Resize)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 36.dp, y = (-28).dp)
                        .size(24.dp),
                    shape = CircleShape,
                    color = Color(0xFFB3261E),
                    onClick = onDelete,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 24.dp)
                        .height(16.dp)
                        .width(24.dp)
                        .pointerInput(element.id) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val deltaWidthRatio = (dragAmount.x / canvasWidthPx) * 2f
                                currentOnUpdate(currentElement.copy(widthRatio = (currentElement.widthRatio + deltaWidthRatio).coerceIn(0.2f, 1f)))
                            }
                        },
                    shape = CircleShape,
                    color = Color(0xFF6750A4),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Code, contentDescription = "Width", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 36.dp, y = 28.dp)
                        .size(24.dp)
                        .pointerInput(element.id) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val scaleChange = (dragAmount.x + dragAmount.y) * 0.15f
                                currentOnUpdate(currentElement.copy(fontSizeSp = (currentElement.fontSizeSp + scaleChange).coerceIn(8f, 72f)))
                            }
                        },
                    shape = CircleShape,
                    color = Color(0xFF79747E),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.OpenInFull, contentDescription = "Resize", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSelectorSection(currentRes: Int, onSelectTheme: (Int) -> Unit) {
    val themes = listOf(
        Pair(R.drawable.bg_invitation_card_01, "Default"),
        Pair(R.drawable.bg_invitation_card_02, "Red Velvet"),
        Pair(R.drawable.bg_invitation_card_03, "Mint Green"),
        Pair(R.drawable.bg_invitation_card_04, "Midnight Blue"),
        Pair(R.drawable.bg_invitation_card_05, "Golden Rose")
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 8.dp)) {
        items(themes) { (bgRes, name) ->
            val isSelected = currentRes == bgRes
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(100.dp, 140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) Color(0xFF005D5D) else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectTheme(bgRes) }
                ) {
                    Image(painter = painterResource(id = bgRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    if (isSelected) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.1f)), contentAlignment = Alignment.Center) {
                            Surface(shape = CircleShape, color = Color(0xFF005D5D), modifier = Modifier.size(36.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
                Text(text = name, style = JasnifyTheme.typography.labelMedium, color = if (isSelected) Color(0xFF005D5D) else Color.Gray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Preview(
    name = "Edit Invitation Details - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun EditInvitationDetailsScreenPreview() {
    val sampleData = InvitationCardData(
        backgroundRes = R.drawable.bg_invitation_card_01,
        backgroundColorHex = 0xFFFFFFFFL,
        elements = listOf(
            TextElement(
                id = "1",
                text = "SAVE THE DATE",
                fontSizeSp = 18f,
                isBold = true,
                colorHex = 0xFF005D5D,
                yRatio = 0.22f,
                letterSpacingSp = 3f
            ),
            TextElement(
                id = "2",
                text = "Taylor & Travis",
                fontSizeSp = 36f,
                isBold = true,
                fontStyle = FontStyleType.PATTAYA,
                colorHex = 0xFFA6852FL,
                yRatio = 0.42f
            ),
            TextElement(
                id = "3",
                text = "Are getting married",
                fontSizeSp = 14f,
                isItalic = true,
                colorHex = 0xFF444444,
                yRatio = 0.52f
            )
        )
    )

    JasnifyTheme {
        EditInvitationDetailsScreen(
            initialData = sampleData,
            onDataChange = {},
            onBackClick = {}
        )
    }
}