package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.sliders.CustomSliderCard
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.utils.drawScrollbar
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape
import kotlin.math.abs

enum class EditorTab(val label: String) {
    TEXT("Text"),
    THEME("Theme"),
    FONT("Font"),
    SIZE("Size"),
    COLOR("Color"),
    FORMAT("Format")
}

@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
)

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

    val tabIndicatorShape = remember(density) {
        GenericShape { size, _ ->
            val radius = with(density) { 24.dp.toPx() }
            val baseCurveSize = with(density) { 16.dp.toPx() }

            moveTo(-baseCurveSize, size.height)
            // Left Concave Curve
            cubicTo(
                -baseCurveSize / 2f, size.height,
                0f, size.height,
                0f, size.height - baseCurveSize
            )
            lineTo(0f, radius)
            // Top Left
            arcTo(Rect(0f, 0f, radius * 2f, radius * 2f), 180f, 90f, false)
            lineTo(size.width - radius, 0f)
            // Top Right
            arcTo(Rect(size.width - radius * 2f, 0f, size.width, radius * 2f), 270f, 90f, false)
            lineTo(size.width, size.height - baseCurveSize)
            // Right Concave Curve
            cubicTo(
                size.width, size.height,
                size.width + baseCurveSize / 2f, size.height,
                size.width + baseCurveSize, size.height
            )
            close()
        }
    }

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
            (-120 - (ratio * 180)).dp // Adjusted for smoother transition
        } else {
            0.dp
        }
    }

    val animatedCanvasOffsetY by animateDpAsState(
        targetValue = targetCanvasOffsetY,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "CanvasOffsetAnimation"
    )

    val animatedBottomPadding by animateDpAsState(
        targetValue = if (isImeVisible) 0.dp else (parentHeightPx * bottomSheetWeight / density.density).dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "BottomPaddingAnimation"
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
                .background(brush = TopGradientBrush)
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
                .padding(top = 64.dp, bottom = animatedBottomPadding)
                .offset(y = animatedCanvasOffsetY),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 8.dp), // Reduced vertical padding to match better
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
                shape = SquircleShape(topStart = CornerExtraLarge, topEnd = CornerExtraLarge),
                color = SurfaceSecondary,
                shadowElevation = 40.dp
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
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(ContentTertiary)
                        )
                    }

                    // HEADER TITLE + ADD BUTTON
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 0.dp, 16.dp, 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = when (activeTab) {
                                EditorTab.TEXT -> "Edit Text"
                                EditorTab.THEME -> "Select Theme"
                                EditorTab.FONT -> "Edit Font"
                                EditorTab.SIZE -> "Edit Size"
                                EditorTab.COLOR -> "Select Text Color"
                                EditorTab.FORMAT -> "Edit Text Format"
                            },
                            style = JasnifyTheme.typography.headingXLarge,
                            color = ContentPrimary,
                            lineHeight = 40.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (activeTab == EditorTab.TEXT) {
                            Spacer(Modifier.width(12.dp))
                            CustomIconButton(
                                onClick = {
                                    val newElement = TextElement(text = "New Text", yRatio = 0.5f)
                                    updateCardState(currentCard.copy(elements = currentCard.elements + newElement))
                                    selectedElementId = newElement.id
                                    activeTab = EditorTab.TEXT
                                },
                                icon = painterResource(R.drawable.ic_plus),
                                contentColor = ContentPrimary,
                                containerColor = SurfacePrimary,
                                size = ButtonSize.Small,
                                modifier = Modifier.width(56.dp)
                                    .height(40.dp)
                                    .align(Alignment.Top)
                            )
                        }
                    }

                    // TAB ROW
                    val tabScrollState = rememberScrollState()
                    val tabPositions = remember { mutableStateListOf<Float>() }
                    val tabWidths = remember { mutableStateListOf<Float>() }
                    var containerCords by remember { mutableStateOf<androidx.compose.ui.layout.LayoutCoordinates?>(null) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tabScrollState)
                            .onGloballyPositioned { containerCords = it }
                    ) {
                        // Animated Sliding Indicator
                        if (tabPositions.size == EditorTab.entries.size) {
                            val targetIndex = EditorTab.entries.indexOf(activeTab)
                            val indicatorOffset by animateFloatAsState(
                                targetValue = tabPositions[targetIndex],
                                animationSpec = spring(stiffness = Spring.StiffnessLow),
                                label = "TabIndicatorOffset"
                            )
                            val indicatorWidth by animateFloatAsState(
                                targetValue = tabWidths[targetIndex],
                                animationSpec = spring(stiffness = Spring.StiffnessLow),
                                label = "TabIndicatorWidth"
                            )

                            Box(
                                modifier = Modifier
                                    .offset(x = indicatorOffset.dp)
                                    .width(indicatorWidth.dp)
                                    .height(48.dp)
                                    .background(SurfacePrimary, tabIndicatorShape)
                            )
                        }

                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            EditorTab.entries.forEachIndexed { index, tab ->
                                val isSelected = activeTab == tab
                                Box(
                                    modifier = Modifier
                                        .onGloballyPositioned { cords ->
                                            if (tabPositions.size <= index) {
                                                tabPositions.add(0f)
                                                tabWidths.add(0f)
                                            }
                                            containerCords?.let { parent ->
                                                val pos = parent.localPositionOf(cords, androidx.compose.ui.geometry.Offset.Zero).x
                                                tabPositions[index] = (pos / density.density)
                                                tabWidths[index] = (cords.size.width / density.density)
                                            }
                                        }
                                        .noRippleClickable { activeTab = tab }
                                        .padding(horizontal = 24.dp, vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tab.label,
                                        style = JasnifyTheme.typography.labelXLarge,
                                        color = if (isSelected) ContentBrandDark else ContentSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // TAB CONTENT CONTAINER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (isImeVisible) Modifier.wrapContentHeight() else Modifier.weight(1f))
                            .background(SurfacePrimary)
                            .then(
                                if (activeTab != EditorTab.SIZE) Modifier.verticalScroll(rememberScrollState())
                                else Modifier
                            )
                    ) {
                        when (activeTab) {
                            EditorTab.TEXT -> {
                                if (selectedElement != null) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        PrimaryInput(
                                            value = selectedElement.text,
                                            placeholder = "New Text",
                                            onValueChange = {
                                                updateElement(selectedElement.copy(text = it))
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .focusRequester(focusRequester)
                                                .onFocusChanged { isTextFieldFocused = it.isFocused }
                                        )
                                    }
                                } else {
                                    NoSelectionPlaceholder()
                                }
                            }
                            EditorTab.THEME -> {
                                Box(modifier = Modifier.padding(vertical = 16.dp)) {
                                    ThemeSelectorSection(currentCard.backgroundRes) { updateCardState(currentCard.copy(backgroundRes = it)) }
                                }
                            }
                            EditorTab.FONT -> {
                                if (selectedElement != null) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = "Font Family",
                                            style = JasnifyTheme.typography.labelMedium,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
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
                                } else {
                                    NoSelectionPlaceholder()
                                }
                            }
                            EditorTab.SIZE -> {
                                if (selectedElement != null) {
                                    val sliders = listOf(
                                        Triple("Font Size", selectedElement.fontSizeSp, Icons.Default.FormatSize) to { v: Float -> updateElement(selectedElement.copy(fontSizeSp = v)) },
                                        Triple("Line Height", selectedElement.lineHeightSp, Icons.Default.FormatLineSpacing) to { v: Float -> updateElement(selectedElement.copy(lineHeightSp = v)) },
                                        Triple("Vertical Padding", selectedElement.verticalPaddingSp, Icons.Default.Height) to { v: Float -> updateElement(selectedElement.copy(verticalPaddingSp = v)) }
                                    )

                                    val sizeScrollState = rememberScrollState()

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .drawScrollbar(sizeScrollState)
                                                .verticalScroll(sizeScrollState)
                                                .padding(horizontal = 16.dp),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            sliders.forEachIndexed { index, (data, onValueChange) ->
                                                val (label, value, icon) = data

                                                val itemShape = when (index) {
                                                    0 -> SquircleShape(topStart = CornerLargeIncrease, topEnd = CornerLargeIncrease, bottomStart = CornerExtraSmall, bottomEnd = CornerExtraSmall)
                                                    sliders.lastIndex -> SquircleShape(topStart = CornerExtraSmall, topEnd = CornerExtraSmall, bottomStart = CornerLargeIncrease, bottomEnd = CornerLargeIncrease)
                                                    else -> SquircleShape(CornerExtraSmall)
                                                }

                                                CustomSliderCard(
                                                    label = label,
                                                    value = value,
                                                    onValueChange = onValueChange,
                                                    valueRange = 8f..100f,
                                                    unit = "px",
                                                    icon = icon,
                                                    shape = itemShape
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    NoSelectionPlaceholder()
                                }
                            }
                            EditorTab.COLOR -> {
                                if (selectedElement != null) {
                                    val palette = listOf(0xFF000000L, 0xFFFFFFFFL, 0xFFE0E0E0L, 0xFF9E9E9EL, 0xFFA6852FL, 0xFF3E2723L, 0xFFB71C1CL, 0xFF1B5E20L, 0xFF004D40L, 0xFF01579BL, 0xFF311B92L, 0xFF880E4FL, 0xFF4E342EL, 0xFF212121L)
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        // Color Picker Icon
                                        Box(
                                            modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFF5F5F5)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Colorize, contentDescription = null, modifier = Modifier.size(24.dp))
                                        }

                                        // Color Wheel Icon (Placeholder)
                                        Box(
                                            modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFF5F5F5)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.Gray)
                                        }

                                        palette.forEach { hex ->
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(hex))
                                                    .border(
                                                        width = if (selectedElement.colorHex == hex) 3.dp else 1.dp,
                                                        color = if (selectedElement.colorHex == hex) Color(0xFF005D5D) else Color.LightGray.copy(alpha = 0.3f),
                                                        shape = CircleShape
                                                    )
                                                    .clickable { updateElement(selectedElement.copy(colorHex = hex)) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (selectedElement.colorHex == hex) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = if (hex == 0xFFFFFFFFL) Color.Black else Color.White)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    NoSelectionPlaceholder()
                                }
                            }
                            EditorTab.FORMAT -> {
                                if (selectedElement != null) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(Color(0xFFF5F5F5))
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(24.dp)
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            FormatToggleButton(Icons.Default.FormatBold, selectedElement.isBold) { updateElement(selectedElement.copy(isBold = !selectedElement.isBold)) }
                                            FormatToggleButton(Icons.Default.FormatItalic, selectedElement.isItalic) { updateElement(selectedElement.copy(isItalic = !selectedElement.isItalic)) }
                                            FormatToggleButton(Icons.Default.FormatUnderlined, selectedElement.isUnderline) { updateElement(selectedElement.copy(isUnderline = !selectedElement.isUnderline)) }
                                        }

                                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFFE0E0E0).copy(alpha = 0.5f))
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            AlignmentToggleButton(Icons.Default.FormatAlignLeft, selectedElement.textAlign == TextAlign.Left) { updateElement(selectedElement.copy(textAlign = TextAlign.Left)) }
                                            AlignmentToggleButton(Icons.Default.FormatAlignCenter, selectedElement.textAlign == TextAlign.Center) { updateElement(selectedElement.copy(textAlign = TextAlign.Center)) }
                                            AlignmentToggleButton(Icons.Default.FormatAlignRight, selectedElement.textAlign == TextAlign.Right) { updateElement(selectedElement.copy(textAlign = TextAlign.Right)) }
                                            AlignmentToggleButton(Icons.Default.FormatAlignJustify, selectedElement.textAlign == TextAlign.Justify) { updateElement(selectedElement.copy(textAlign = TextAlign.Justify)) }
                                        }
                                    }
                                } else {
                                    NoSelectionPlaceholder()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoSelectionPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_click_hand),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Text(
            text = "Tap on any text from the card",
            style = JasnifyTheme.typography.bodyLarge,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FormatToggleButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(64.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFF5D7474) else Color(0xFFE5E5E5),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) Color.White else Color(0xFF757575), modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun AlignmentToggleButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF5D7474) else Color.Transparent,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) Color.White else Color(0xFF757575), modifier = Modifier.size(24.dp))
        }
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
    ) {
        val density = LocalDensity.current.density
        val canvasWidthPx = canvasSize.width.toFloat()
        val canvasWidthDp = canvasWidthPx / density
        val scaleFactor = if (canvasWidthDp > 0) canvasWidthDp / 284f else 1f

        // 1. Clipped background card layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(16.dp, SquircleShape(CornerLargeIncrease))
                .clip(SquircleShape(CornerLargeIncrease))
                .background(Color(card.backgroundColorHex))
                .pointerInput(Unit) {
                    detectTapGestures { onSelectElement("") } // Deselect when tapping background
                }
        ) {
            Image(
                painter = painterResource(id = card.backgroundRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        // 2. Interactive layer (Unclipped so control handles can overflow outside the card)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = (40 * scaleFactor).dp)
                .pointerInput(Unit) {
                    detectTapGestures { onSelectElement("") } // Deselect when tapping between rows
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            card.elements.sortedBy { it.yRatio }.forEach { element ->
                InteractiveTextElementItem(
                    element = element,
                    canvasSize = canvasSize,
                    scaleFactor = scaleFactor,
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
    scaleFactor: Float,
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
                .then(
                    if (isSelected) Modifier
                        .border(2.dp, Color(0xFF6750A4), RoundedCornerShape(8.dp))
                    else Modifier
                )
                .padding(vertical = (element.verticalPaddingSp * scaleFactor / 2).dp)
                .padding(horizontal = (12 * scaleFactor).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = element.text,
                modifier = Modifier.wrapContentWidth(),
                style = TextStyle(
                    fontFamily = element.fontStyle.fontFamily,
                    fontSize = (element.fontSizeSp * scaleFactor).sp,
                    fontWeight = if (element.isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (element.isItalic) FontStyle.Italic else FontStyle.Normal,
                    textDecoration = if (element.isUnderline) TextDecoration.Underline else TextDecoration.None,
                    color = Color(element.colorHex),
                    textAlign = element.textAlign,
                    letterSpacing = (element.letterSpacingSp * scaleFactor).sp,
                    lineHeight = if (element.lineHeightSp > 0) (element.lineHeightSp * scaleFactor).sp else TextUnit.Unspecified,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    ),
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                )
            )
            if (isSelected) {
                // Delete Handle
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (36 * scaleFactor).dp, y = ((-28) * scaleFactor).dp)
                        .size((24 * scaleFactor).dp),
                    shape = CircleShape,
                    color = Color(0xFFB3261E),
                    onClick = onDelete,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size((18 * scaleFactor).dp))
                    }
                }

                // Width Drag Handle
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (24 * scaleFactor).dp)
                        .height((16 * scaleFactor).dp)
                        .width((24 * scaleFactor).dp)
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
                        Icon(Icons.Default.Code, contentDescription = "Width", tint = Color.White, modifier = Modifier.size((16 * scaleFactor).dp))
                    }
                }

                // Resize Drag Handle
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (36 * scaleFactor).dp, y = (28 * scaleFactor).dp)
                        .size((24 * scaleFactor).dp)
                        .pointerInput(element.id) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val scaleChange = (dragAmount.x + dragAmount.y) * 0.15f
                                // We update the raw SP value, which then gets scaled by scaleFactor for display
                                currentOnUpdate(currentElement.copy(fontSizeSp = (currentElement.fontSizeSp + (scaleChange / scaleFactor)).coerceIn(8f, 72f)))
                            }
                        },
                    shape = CircleShape,
                    color = Color(0xFF79747E),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.OpenInFull, contentDescription = "Resize", tint = Color.White, modifier = Modifier.size((16 * scaleFactor).dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSelectorSection(currentRes: Int, onSelectTheme: (Int) -> Unit) {
    val themes = listOf(
        R.drawable.bg_invitation_card_01,
        R.drawable.bg_invitation_card_02,
        R.drawable.bg_invitation_card_03,
        R.drawable.bg_invitation_card_04,
        R.drawable.bg_invitation_card_05
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 120.dp, height = 160.dp)
                        .clip(shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .border(width = 1.dp, ContentSecondary, shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .background(Color.Transparent)
                        .clickable { /* Handle Upload */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_upload),
                            contentDescription = null,
                            tint = ContentSecondary
                        )
                        Text(
                            text = "Upload\nImage",
                            textAlign = TextAlign.Center,
                            color = ContentSecondary,
                            style = JasnifyTheme.typography.labelXLarge,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }

        items(themes) { bgRes ->
            val isSelected = currentRes == bgRes
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 120.dp, height = 160.dp)
                        .clip(shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .border(
                            width = if (isSelected) 4.dp else 0.dp,
                            color = if (isSelected) ContentBrandDark else Color.Transparent,
                            shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                        )
                        .clickable { onSelectTheme(bgRes) }
                ) {
                    Image(
                        painter = painterResource(id = bgRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ContentBrandDark,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = null,
                                        tint = ContentInvPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
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
