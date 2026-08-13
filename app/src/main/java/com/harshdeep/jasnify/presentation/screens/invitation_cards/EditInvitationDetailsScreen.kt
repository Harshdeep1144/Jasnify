package com.harshdeep.jasnify.presentation.screens.invitation_cards

import android.R.attr.scaleX
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatAlignLeft
import androidx.compose.material.icons.automirrored.rounded.FormatAlignRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.FormatAlignCenter
import androidx.compose.material.icons.rounded.FormatAlignJustify
import androidx.compose.material.icons.rounded.OpenInFull
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
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
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.sliders.CustomSliderCard
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.utils.dashedBorder
import com.harshdeep.jasnify.presentation.utils.drawScrollbar
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.math.abs
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds

enum class EditorTab(val label: String) {
    TEXT("Text"),
    THEME("Theme"),
    FONT("Font"),
    SIZE("Size"),
    COLOR("Color")
}

data class CardThemeItem(
    val name: String,
    val resId: Int
)

// Helper to normalize 24-bit hex colors (e.g. 0x005D5D) to 32-bit ARGB (0xFF005D5D) on initial load
private fun TextElement.normalizeAlpha(): TextElement {
    val hex = this.colorHex
    return if ((hex and 0xFF000000L) == 0L && hex > 0L) {
        this.copy(colorHex = 0xFF000000L or hex)
    } else this
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
    val normalizedInitialData = remember(initialData) {
        initialData.copy(elements = initialData.elements.map { it.normalizeAlpha() })
    }
    val history = remember { mutableStateListOf(normalizedInitialData) }
    var historyIndex by remember { mutableIntStateOf(0) }
    val currentCard = history.getOrElse(historyIndex) { normalizedInitialData }

    var selectedElementId by remember { mutableStateOf<String?>(null) }
    var activeTab by remember { mutableStateOf(EditorTab.TEXT) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    // Dynamic Sheet Height & Weight Management
    var bottomSheetWeight by remember { mutableFloatStateOf(0.42f) }
    val minSheetWeight = 0.32f
    val maxSheetWeight = 0.64f
    var parentHeightPx by remember { mutableFloatStateOf(0f) }

    val colorRowLazyListState = rememberLazyListState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    val tabIndicatorShape = remember(density) {
        GenericShape { size, _ ->
            val radius = with(density) { 24.dp.toPx() }
            val baseCurveSize = with(density) { 32.dp.toPx() }

            moveTo(-baseCurveSize, size.height)
            cubicTo(
                -baseCurveSize / 2f, size.height,
                0f, size.height,
                0f, size.height - baseCurveSize
            )
            lineTo(0f, radius)
            arcTo(Rect(0f, 0f, radius * 2f, radius * 2f), 180f, 90f, false)
            lineTo(size.width - radius, 0f)
            arcTo(Rect(size.width - radius * 2f, 0f, size.width, radius * 2f), 270f, 90f, false)
            lineTo(size.width, size.height - baseCurveSize)
            cubicTo(
                size.width, size.height,
                size.width + baseCurveSize / 2f, size.height,
                size.width + baseCurveSize, size.height
            )
            close()
        }
    }

    val isImeVisible = WindowInsets.isImeVisible

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

    val targetCanvasOffsetY = remember(isImeVisible, selectedElement) {
        if (isImeVisible && selectedElement != null) {
            val ratio = selectedElement.yRatio.coerceIn(0f, 1f)
            (-120 - (ratio * 180)).dp
        } else {
            0.dp
        }
    }

    val bottomPadding = if (isImeVisible) 0.dp else (parentHeightPx * bottomSheetWeight / density.density).dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ContentPrimary)
            .onGloballyPositioned { coordinates ->
                parentHeightPx = coordinates.size.height.toFloat()
            }
    ) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .zIndex(0f)
                .padding(top = 64.dp, bottom = bottomPadding)
                .offset(y = targetCanvasOffsetY),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 8.dp),
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 0.dp, 16.dp, 12.dp)
                            .height(40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (activeTab) {
                                EditorTab.TEXT -> "Edit Text"
                                EditorTab.THEME -> "Select Theme"
                                EditorTab.FONT -> "Edit Font"
                                EditorTab.SIZE -> "Edit Size"
                                EditorTab.COLOR -> "Select Text Color"
                            },
                            style = JasnifyTheme.typography.headingXLarge,
                            color = ContentPrimary,
                            fontWeight = FontWeight.Medium
                        )

                        if (activeTab == EditorTab.TEXT) {
                            Spacer(Modifier.width(12.dp))
                            CustomIconButton(
                                onClick = {
                                    if (isImeVisible) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    } else {
                                        val newElement = TextElement(text = "New Text", yRatio = 0.5f).normalizeAlpha()
                                        updateCardState(currentCard.copy(elements = currentCard.elements + newElement))
                                        selectedElementId = newElement.id
                                        activeTab = EditorTab.TEXT
                                    }
                                },
                                icon = if (isImeVisible) painterResource(R.drawable.ic_check) else painterResource(R.drawable.ic_plus),
                                contentColor = ContentPrimary,
                                containerColor = SurfacePrimary,
                                size = ButtonSize.Small,
                                modifier = Modifier
                                    .width(56.dp)
                                    .height(40.dp)
                            )
                        }
                    }

                    val tabScrollState = rememberScrollState()
                    var hasPlayedTabLaunchAnimation by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        snapshotFlow { tabScrollState.maxValue }
                            .collect { maxScroll ->
                                if (maxScroll > 0 && !hasPlayedTabLaunchAnimation) {
                                    hasPlayedTabLaunchAnimation = true
                                    delay(200.milliseconds)

                                    tabScrollState.animateScrollTo(
                                        value = maxScroll,
                                        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
                                    )
                                    delay(150.milliseconds)

                                    tabScrollState.animateScrollTo(
                                        value = 0,
                                        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
                                    )
                                }
                            }
                    }

                    val tabPositions = remember { mutableStateListOf<Float>() }
                    val tabWidths = remember { mutableStateListOf<Float>() }
                    var containerCords by remember { mutableStateOf<androidx.compose.ui.layout.LayoutCoordinates?>(null) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tabScrollState)
                            .onGloballyPositioned { containerCords = it }
                    ) {
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

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (isImeVisible) Modifier.wrapContentHeight() else Modifier.weight(1f))
                            .background(SurfacePrimary)
                            .then(
                                if (activeTab != EditorTab.SIZE && activeTab != EditorTab.COLOR) Modifier.verticalScroll(rememberScrollState())
                                else Modifier
                            )
                    ) {
                        when (activeTab) {
                            EditorTab.TEXT -> {
                                if (selectedElement != null) {
                                    key(selectedElement.id) {
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
                                    key(selectedElement.id) {
                                        ProfessionalFontSelector(
                                            selectedElement = selectedElement,
                                            bottomSheetWeight = bottomSheetWeight,
                                            onUpdateElement = ::updateElement
                                        )
                                    }
                                } else {
                                    NoSelectionPlaceholder()
                                }
                            }
                            EditorTab.SIZE -> {
                                if (selectedElement != null) {
                                    key(selectedElement.id) {
                                        val initialElement = remember(selectedElement.id, normalizedInitialData) {
                                            normalizedInitialData.elements.find { it.id == selectedElement.id }
                                        }

                                        val isLineHeightAuto = selectedElement.lineHeightSp <= 0f
                                        val displayLineHeightValue = if (isLineHeightAuto) {
                                            (selectedElement.fontSizeSp * 1.2f).coerceIn(0f, 100f)
                                        } else {
                                            selectedElement.lineHeightSp
                                        }

                                        data class SliderConfigData(
                                            val label: String,
                                            val value: Float,
                                            val icon: androidx.compose.ui.graphics.vector.ImageVector,
                                            val isAuto: Boolean,
                                            val onValueChange: (Float) -> Unit,
                                            val onReset: () -> Unit
                                        )

                                        val sliders = listOf(
                                            SliderConfigData(
                                                label = "Font Size",
                                                value = selectedElement.fontSizeSp,
                                                icon = Icons.Default.FormatSize,
                                                isAuto = false,
                                                onValueChange = { v ->
                                                    updateElement(selectedElement.copy(fontSizeSp = v))
                                                },
                                                onReset = {
                                                    val savedFontSize = initialElement?.fontSizeSp ?: 24f
                                                    updateElement(selectedElement.copy(fontSizeSp = savedFontSize))
                                                }
                                            ),
                                            SliderConfigData(
                                                label = "Line Height",
                                                value = displayLineHeightValue,
                                                icon = Icons.Default.FormatLineSpacing,
                                                isAuto = isLineHeightAuto,
                                                onValueChange = { v ->
                                                    updateElement(selectedElement.copy(lineHeightSp = v))
                                                },
                                                onReset = {
                                                    val savedLineHeight = initialElement?.lineHeightSp ?: 0f
                                                    updateElement(selectedElement.copy(lineHeightSp = savedLineHeight))
                                                }
                                            ),
                                            SliderConfigData(
                                                label = "Letter Spacing",
                                                value = selectedElement.letterSpacingSp,
                                                icon = Icons.Default.TextFields,
                                                isAuto = false,
                                                onValueChange = { v ->
                                                    updateElement(selectedElement.copy(letterSpacingSp = v))
                                                },
                                                onReset = {
                                                    val savedLetterSpacing = initialElement?.letterSpacingSp ?: 0f
                                                    updateElement(selectedElement.copy(letterSpacingSp = savedLetterSpacing))
                                                }
                                            ),
                                            SliderConfigData(
                                                label = "Vertical Padding",
                                                value = selectedElement.verticalPaddingSp,
                                                icon = Icons.Default.Height,
                                                isAuto = false,
                                                onValueChange = { v ->
                                                    updateElement(selectedElement.copy(verticalPaddingSp = v))
                                                },
                                                onReset = {
                                                    val savedVerticalPadding = initialElement?.verticalPaddingSp ?: 0f
                                                    updateElement(selectedElement.copy(verticalPaddingSp = savedVerticalPadding))
                                                }
                                            )
                                        )

                                        val sizeScrollState = rememberScrollState()

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .padding(16.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight()
                                                    .border(
                                                        width = 1.dp,
                                                        color = MaterialTheme.colorScheme.outline.copy(0.16f),
                                                        shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
                                                    )
                                                    .clip(SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
                                                    .background(SurfaceSecondary)
                                                    .drawScrollbar(sizeScrollState)
                                                    .verticalScroll(sizeScrollState)
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .wrapContentHeight()
                                                ) {
                                                    sliders.forEachIndexed { index, config ->
                                                        CustomSliderCard(
                                                            label = config.label,
                                                            value = config.value,
                                                            onValueChange = config.onValueChange,
                                                            valueRange = 0f..100f,
                                                            unit = "px",
                                                            icon = config.icon,
                                                            isAuto = config.isAuto,
                                                            onReset = config.onReset,
                                                            shape = SquircleShape(0.dp)
                                                        )

                                                        if (index < sliders.lastIndex) {
                                                            HorizontalDivider(
                                                                color = MaterialTheme.colorScheme.outline.copy(0.12f),
                                                                thickness = 1.dp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    NoSelectionPlaceholder()
                                }
                            }
                            EditorTab.COLOR -> {
                                if (selectedElement != null) {
                                    val currentSelectedElement by rememberUpdatedState(selectedElement)
                                    val initialElement = remember(selectedElement.id, normalizedInitialData) {
                                        normalizedInitialData.elements.find { it.id == selectedElement.id }
                                    }

                                    val palette = remember {
                                        listOf(
                                            0xFF8A5A00L, 0xFFFFFFFFL, 0xFFE5E5E5L, 0xFF9E9E9EL, 0xFF8C3B2BL,
                                            0xFF000000L, 0xFF005D5DL, 0xFF1B5E20L, 0xFF01579BL, 0xFF311B92L,
                                            0xFFFFB300L, 0xFFFFC107L, 0xFFFFD54FL, 0xFFFF8F00L, 0xFFE65100L,
                                            0xFFF4511EL, 0xFFD84315L, 0xFFB71C1CL, 0xFFC62828L, 0xFFAD1457L,
                                            0xFFD81B60L, 0xFF6A1B9AL, 0xFF4527A0L, 0xFF283593L, 0xFF1565C0L,
                                            0xFF0277BDL, 0xFF00838FL, 0xFF00695CL, 0xFF2E7D32L, 0xFF558B2FL,
                                            0xFF7CB342L, 0xFF827717L, 0xFFAFB42BL, 0xFF795548L, 0xFF6D4C41L,
                                            0xFF455A64L, 0xFF37474FL, 0xFF78909CL, 0xFFBDBDBDL, 0xFF424242L
                                        )
                                    }

                                    LaunchedEffect(selectedElement.id) {
                                        val activeRgb = currentSelectedElement.colorHex and 0x00FFFFFFL
                                        val matchIndex = palette.indexOfFirst { (it and 0x00FFFFFFL) == activeRgb }
                                        if (matchIndex >= 0) {
                                            colorRowLazyListState.animateScrollToItem(matchIndex + 2)
                                        }
                                    }

                                    val currentAlpha = (currentSelectedElement.colorHex shr 24) and 0xFFL
                                    val currentOpacity = (currentAlpha.toFloat() / 255f) * 100f

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        LazyRow(
                                            state = colorRowLazyListState,
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .clip(CircleShape)
                                                        .noRippleClickable { /* Eyedropper action */ },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Colorize,
                                                        contentDescription = "Eyedropper",
                                                        tint = ContentPrimary,
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                }
                                            }

                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            brush = androidx.compose.ui.graphics.Brush.sweepGradient(
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
                                                        .noRippleClickable { /* Color wheel dialog */ },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(SurfacePrimary)
                                                    )
                                                }
                                            }

                                            items(palette) { hex ->
                                                val isSelected = (currentSelectedElement.colorHex and 0x00FFFFFFL) == (hex and 0x00FFFFFFL)
                                                val swatchColor = Color(hex)

                                                Box(
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .clip(CircleShape)
                                                        .background(swatchColor)
                                                        .border(1.dp, Color(0x26000000), CircleShape)
                                                        .clickable {
                                                            val latestTarget = currentCard.elements.find { it.id == currentSelectedElement.id } ?: currentSelectedElement
                                                            val activeAlpha = (latestTarget.colorHex shr 24) and 0xFFL
                                                            val swatchRgb = hex and 0x00FFFFFFL
                                                            val updatedColorHex = (activeAlpha shl 24) or swatchRgb
                                                            updateElement(latestTarget.copy(colorHex = updatedColorHex))
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isSelected) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.ic_check),
                                                            contentDescription = "Selected",
                                                            tint = if ((hex and 0x00FFFFFFL) == 0xFFFFFFL || (hex and 0x00FFFFFFL) == 0xE5E5E5L) Color.Black else Color.White,
                                                            modifier = Modifier.size(32.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                                .border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colorScheme.outline.copy(0.16f),
                                                    shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
                                                )
                                                .clip(SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
                                                .background(SurfaceSecondary)
                                        ) {
                                            CustomSliderCard(
                                                label = "Opacity",
                                                value = currentOpacity,
                                                onValueChange = { newOpacity ->
                                                    val latestTarget = currentCard.elements.find { it.id == currentSelectedElement.id } ?: currentSelectedElement
                                                    val alphaByte = round((newOpacity / 100f) * 255f).toLong().coerceIn(0L, 255L)
                                                    val currentRgb = latestTarget.colorHex and 0x00FFFFFFL
                                                    val updatedColorHex = (alphaByte shl 24) or currentRgb
                                                    updateElement(latestTarget.copy(colorHex = updatedColorHex))
                                                },
                                                valueRange = 0f..100f,
                                                unit = "%",
                                                icon = Icons.Default.WbSunny,
                                                onReset = {
                                                    val latestTarget = currentCard.elements.find { it.id == currentSelectedElement.id } ?: currentSelectedElement
                                                    val currentRgb = latestTarget.colorHex and 0x00FFFFFFL
                                                    val savedColorHex = initialElement?.colorHex ?: (0xFF000000L or currentRgb)
                                                    val savedAlpha = (savedColorHex shr 24) and 0xFFL
                                                    val updatedColorHex = (savedAlpha shl 24) or currentRgb
                                                    updateElement(latestTarget.copy(colorHex = updatedColorHex))
                                                },
                                                shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
                                            )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfessionalFontSelector(
    selectedElement: TextElement,
    bottomSheetWeight: Float,
    onUpdateElement: (TextElement) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val fontTypes = remember { FontStyleType.entries }
    val initialFontIndex = remember(selectedElement.id) {
        fontTypes.indexOf(selectedElement.fontStyle).coerceAtLeast(0)
    }

    val fontLazyListState = rememberLazyListState(initialFirstVisibleItemIndex = initialFontIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = fontLazyListState)

    var containerWidthPx by remember { mutableFloatStateOf(0f) }

    // Dynamically calculate horizontal padding so items can snap directly to the center of the container
    val horizontalPaddingDp = remember(containerWidthPx) {
        if (containerWidthPx > 0f) {
            with(density) { ((containerWidthPx / 2f) - 40.dp.toPx()).coerceAtLeast(16.dp.toPx()).toDp() }
        } else {
            120.dp
        }
    }

    val centerItemIndex by remember {
        derivedStateOf {
            val layoutInfo = fontLazyListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) initialFontIndex
            else {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                visibleItems.minByOrNull { item ->
                    val itemCenter = item.offset + (item.size / 2)
                    abs(itemCenter - viewportCenter)
                }?.index ?: initialFontIndex
            }
        }
    }

    val currentSelectedElement by rememberUpdatedState(selectedElement)
    val currentUpdateElement by rememberUpdatedState(onUpdateElement)

    LaunchedEffect(centerItemIndex) {
        val currentFont = fontTypes.getOrNull(centerItemIndex)
        if (currentFont != null && currentSelectedElement.fontStyle != currentFont) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            currentUpdateElement(currentSelectedElement.copy(fontStyle = currentFont))
        }
    }

    LaunchedEffect(selectedElement.fontStyle) {
        val targetIndex = fontTypes.indexOf(selectedElement.fontStyle)
        if (targetIndex >= 0 && centerItemIndex != targetIndex && !fontLazyListState.isScrollInProgress) {
            fontLazyListState.animateScrollToItem(targetIndex)
        }
    }

    val isSheetScaleIncreased = bottomSheetWeight > 0.48f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    containerWidthPx = coordinates.size.width.toFloat()
                },
            contentAlignment = Alignment.Center
        ) {
            LazyRow(
                state = fontLazyListState,
                flingBehavior = snapFlingBehavior,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = horizontalPaddingDp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(fontTypes) { index, fontType ->
                    val isCentered = centerItemIndex == index

                    val fontBgColor by animateColorAsState(
                        targetValue = if (isCentered) SurfaceBrandSecondary else SurfaceSecondary,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "FontBgColorAnimation"
                    )

                    val fontBorderColor by animateColorAsState(
                        targetValue = if (isCentered) ContentBrandDark else Color.Transparent,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "FontBorderColorAnimation"
                    )

                    val fontTextColor by animateColorAsState(
                        targetValue = if (isCentered) ContentBrandDark else ContentSecondary,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "FontTextColorAnimation"
                    )

                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .wrapContentWidth()
                            .noRippleClickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                currentUpdateElement(selectedElement.copy(fontStyle = fontType))
                                coroutineScope.launch {
                                    fontLazyListState.animateScrollToItem(index)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .align(Alignment.Center),
                            color = fontBgColor,
                            shape = RoundedCornerShape(100),
                            border = BorderStroke(2.dp, fontBorderColor)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(horizontal = 24.dp)
                            ) {
                                Text(
                                    text = fontType.label,
                                    fontFamily = fontType.fontFamily,
                                    color = fontTextColor,
                                    style = JasnifyTheme.typography.displaySmall,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(0.16f),
                    shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
                )
                .clip(SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
                .background(SurfacePrimary)
        ) {
            val alignmentOptions = remember {
                listOf(
                    TextAlign.Left to Icons.AutoMirrored.Rounded.FormatAlignLeft,
                    TextAlign.Center to Icons.Rounded.FormatAlignCenter,
                    TextAlign.Right to Icons.AutoMirrored.Rounded.FormatAlignRight,
                    TextAlign.Justify to Icons.Rounded.FormatAlignJustify
                )
            }
            val currentAlignIndex = remember(selectedElement.textAlign) {
                alignmentOptions.indexOfFirst { it.first == selectedElement.textAlign }.coerceAtLeast(0)
            }

            val alignPositions = remember { mutableStateListOf<Float>() }
            val alignWidths = remember { mutableStateListOf<Float>() }
            var alignContainerCords by remember { mutableStateOf<androidx.compose.ui.layout.LayoutCoordinates?>(null) }

            @Composable
            fun AlignmentSelectorPill() {
                Box(
                    modifier = Modifier
                        .clip(shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .background(color = SurfaceSecondary, shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .padding(4.dp)
                        .onGloballyPositioned { alignContainerCords = it }
                ) {
                    if (alignPositions.size == alignmentOptions.size) {
                        val indicatorOffset by animateFloatAsState(
                            targetValue = alignPositions[currentAlignIndex],
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "AlignIndicatorOffset"
                        )
                        val indicatorWidth by animateFloatAsState(
                            targetValue = alignWidths[currentAlignIndex],
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "AlignIndicatorWidth"
                        )

                        Box(
                            modifier = Modifier
                                .offset(x = indicatorOffset.dp)
                                .width(indicatorWidth.dp)
                                .height(48.dp)
                                .background(
                                    color = SurfaceBrandPrimary,
                                    shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                                )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        alignmentOptions.forEachIndexed { index, (align, icon) ->
                            val isSelected = selectedElement.textAlign == align
                            val animateIconColor by animateColorAsState(
                                targetValue = if (isSelected) ContentInvPrimary else ContentSecondary,
                                animationSpec = tween(durationMillis = 200),
                                label = "IconColorAnimation"
                            )

                            Box(
                                modifier = Modifier
                                    .onGloballyPositioned { cords ->
                                        if (alignPositions.size <= index) {
                                            alignPositions.add(0f)
                                            alignWidths.add(0f)
                                        }
                                        alignContainerCords?.let { parent ->
                                            val pos = parent.localPositionOf(cords, androidx.compose.ui.geometry.Offset.Zero).x
                                            alignPositions[index] = (pos / density.density)
                                            alignWidths[index] = (cords.size.width / density.density)
                                        }
                                    }
                                    .size(48.dp)
                                    .noRippleClickable {
                                        onUpdateElement(selectedElement.copy(textAlign = align))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = animateIconColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (isSheetScaleIncreased) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_bold),
                            isSelected = selectedElement.isBold
                        ) {
                            onUpdateElement(selectedElement.copy(isBold = !selectedElement.isBold))
                        }
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_italic),
                            isSelected = selectedElement.isItalic
                        ) {
                            onUpdateElement(selectedElement.copy(isItalic = !selectedElement.isItalic))
                        }
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_underline),
                            isSelected = selectedElement.isUnderline
                        ) {
                            onUpdateElement(selectedElement.copy(isUnderline = !selectedElement.isUnderline))
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(0.16f),
                        thickness = 1.dp
                    )

                    AlignmentSelectorPill()
                }
            } else {
                val formatContainerScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(formatContainerScrollState)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_bold),
                            isSelected = selectedElement.isBold
                        ) {
                            onUpdateElement(selectedElement.copy(isBold = !selectedElement.isBold))
                        }
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_italic),
                            isSelected = selectedElement.isItalic
                        ) {
                            onUpdateElement(selectedElement.copy(isItalic = !selectedElement.isItalic))
                        }
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_underline),
                            isSelected = selectedElement.isUnderline
                        ) {
                            onUpdateElement(selectedElement.copy(isUnderline = !selectedElement.isUnderline))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(0.16f))
                    )

                    AlignmentSelectorPill()
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
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_click_hand),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = ContentTertiary
        )
        Text(
            text = "Tap on any text from the card",
            style = JasnifyTheme.typography.displayMedium,
            color = ContentTertiary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FormatToggleButton(
    icon: Painter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) SurfaceBrandPrimary else SurfaceSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "FormatBgColor"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) ContentInvPrimary else ContentSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "FormatIconColor"
    )

    CustomIconButton(
        onClick = onClick,
        icon = icon,
        containerColor = backgroundColor,
        contentColor = iconColor,
        shapeStyle = ButtonShapeStyle.Square
    )
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(16.dp, SquircleShape(CornerLargeIncrease))
                .clip(SquircleShape(CornerLargeIncrease))
                .background(Color(card.backgroundColorHex))
                .pointerInput(Unit) {
                    detectTapGestures { onSelectElement("") }
                }
        ) {
            Image(
                painter = painterResource(id = card.backgroundRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = (40 * scaleFactor).dp)
                .pointerInput(Unit) {
                    detectTapGestures { onSelectElement("") }
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
                        .border(2.dp, Color(0xFF6750A4), SquircleShape(CornerExtraSmall))
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
                    lineHeight = if (element.lineHeightSp > 0f) (element.lineHeightSp * scaleFactor).sp else TextUnit.Unspecified,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    ),
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                )
            )
            if (isSelected) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (36 * scaleFactor).dp, y = ((-28) * scaleFactor).dp)
                        .size((24 * scaleFactor).dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error,
                    onClick = onDelete,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Delete",
                            tint = ContentInvPrimary,
                            modifier = Modifier.size((18 * scaleFactor).dp)
                        )
                    }
                }

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
                        Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = "Width",
                            tint = ContentInvPrimary,
                            modifier = Modifier.size((16 * scaleFactor).dp)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (36 * scaleFactor).dp, y = (28 * scaleFactor).dp)
                        .size((24 * scaleFactor).dp)
                        .pointerInput(element.id) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val scaleChange = (dragAmount.x + dragAmount.y) * 0.15f
                                currentOnUpdate(currentElement.copy(fontSizeSp = (currentElement.fontSizeSp + (scaleChange / scaleFactor)).coerceIn(8f, 72f)))
                            }
                        },
                    shape = CircleShape,
                    color = ContentSecondary,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.OpenInFull,
                            contentDescription = "Mirror",
                            tint = ContentInvPrimary,
                            modifier = Modifier
                                .size((16 * scaleFactor).dp)
                                .graphicsLayer {
                                    scaleX = -1f
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSelectorSection(currentRes: Int, onSelectTheme: (Int) -> Unit) {
    val themes = remember {
        listOf(
            CardThemeItem("Classic Elegance", R.drawable.bg_invitation_card_01),
            CardThemeItem("Floral Romance", R.drawable.bg_invitation_card_02),
            CardThemeItem("Golden Glamour", R.drawable.bg_invitation_card_03),
            CardThemeItem("Modern Minimalist", R.drawable.bg_invitation_card_04),
            CardThemeItem("Vintage Botanical", R.drawable.bg_invitation_card_05)
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 120.dp, height = 160.dp)
                        .clip(shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .dashedBorder(
                            color = ContentSecondary,
                            shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
                            strokeWidth = 2.dp,
                            dashLength = 6.dp,
                            gapLength = 4.dp
                        )
                        .background(Color.Transparent)
                        .clickable { /* Upload handler */ },
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
                Text(
                    text = "Custom",
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        items(themes) { theme ->
            val isSelected = currentRes == theme.resId
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        .clickable { onSelectTheme(theme.resId) }
                ) {
                    Image(
                        painter = painterResource(id = theme.resId),
                        contentDescription = theme.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
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
                Text(
                    text = theme.name,
                    style = JasnifyTheme.typography.labelMedium,
                    color = if (isSelected) ContentBrandDark else ContentSecondary,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
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
                colorHex = 0xFF005D5DL,
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
                colorHex = 0xFF444444L,
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