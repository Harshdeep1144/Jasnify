package com.harshdeep.jasnify.presentation.screens.invitation_cards

import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatAlignLeft
import androidx.compose.material.icons.automirrored.rounded.FormatAlignRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.FormatAlignCenter
import androidx.compose.material.icons.rounded.FormatAlignJustify
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalIndirectPointerApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.CardPaddingMode
import com.harshdeep.jasnify.domain.model.CardRoomData
import com.harshdeep.jasnify.domain.model.CardTextAlign
import com.harshdeep.jasnify.domain.model.FontStyleType
import com.harshdeep.jasnify.domain.model.TextElement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomSuccessBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.components.dialogs.ColorPickerWheel
import com.harshdeep.jasnify.presentation.components.dialogs.ConfirmationDialog
import com.harshdeep.jasnify.presentation.components.dialogs.EyeDropperOverlay
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.sliders.CustomSliderCard
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.utils.dashedBorder
import com.harshdeep.jasnify.presentation.utils.drawScrollbar
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.theme.TopGradientBrush
import com.harshdeep.jasnify.utils.ShareUtils
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
    COLOR("Color"),
    SIZE("Size")
}

data class CardThemeItem(
    val id: String,
    val name: String,
    val resId: Int = 0,
    val url: String? = null,
    val isDefault: Boolean = false
)

private val DefaultThemeItems = listOf(
    CardThemeItem(id = "default_1", name = "Classic Elegance", resId = R.drawable.bg_invitation_card_01, isDefault = true),
    CardThemeItem(id = "default_2", name = "Floral Romance", resId = R.drawable.bg_invitation_card_02, isDefault = true),
    CardThemeItem(id = "default_3", name = "Golden Glamour", resId = R.drawable.bg_invitation_card_03, isDefault = true),
    CardThemeItem(id = "default_4", name = "Modern Minimalist", resId = R.drawable.bg_invitation_card_04, isDefault = true),
    CardThemeItem(id = "default_5", name = "Vintage Botanical", resId = R.drawable.bg_invitation_card_05, isDefault = true)
)

private val ColorPaletteHexes = listOf(
    0xFF8A5A00L, 0xFFFFFFFFL, 0xFFE5E5E5L, 0xFF9E9E9EL, 0xFF8C3B2BL,
    0xFF000000L, 0xFF005D5DL, 0xFF1B5E20L, 0xFF01579BL, 0xFF311B92L,
    0xFFFFB300L, 0xFFFFC107L, 0xFFFFD54FL, 0xFFFF8F00L, 0xFFE65100L,
    0xFFF4511EL, 0xFFD84315L, 0xFFB71C1CL, 0xFFC62828L, 0xFFAD1457L,
    0xFFD81B60L, 0xFF6A1B9AL, 0xFF4527A0L, 0xFF283593L, 0xFF1565C0L,
    0xFF0277BDL, 0xFF00838FL, 0xFF00695CL, 0xFF2E7D32L, 0xFF558B2FL,
    0xFF7CB342L, 0xFF827717L, 0xFFAFB42BL, 0xFF795548L, 0xFF6D4C41L,
    0xFF455A64L, 0xFF37474FL, 0xFF78909CL, 0xFFBDBDBDL, 0xFF424242L
)

private val AlignmentOptionsList: List<Pair<CardTextAlign, ImageVector>> = listOf(
    CardTextAlign.LEFT to Icons.AutoMirrored.Rounded.FormatAlignLeft,
    CardTextAlign.CENTER to Icons.Rounded.FormatAlignCenter,
    CardTextAlign.RIGHT to Icons.AutoMirrored.Rounded.FormatAlignRight,
    CardTextAlign.JUSTIFY to Icons.Rounded.FormatAlignJustify
)

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
fun EditCardDetailsScreen(
    initialData: CardData = CardData(),
    allCards: List<CardData> = emptyList(),
    cardRoomData: CardRoomData? = null,
    onDataChange: (CardData) -> Unit = {},
    onBackClick: () -> Unit = {},
    onUploadImage: (Uri, (String) -> Unit, (String) -> Unit) -> Unit = { _, _, _ -> },
    onUpdateThemeName: (String, String) -> Unit = { _, _ -> }
) {
    val normalizedInitialData = remember(initialData.id) {
        initialData.copy(elements = initialData.elements.map { it.normalizeAlpha() })
    }
    val history = remember { mutableStateListOf(normalizedInitialData) }
    var historyIndex by remember { mutableIntStateOf(0) }
    val currentCard = history.getOrElse(historyIndex) { normalizedInitialData }

    var savedCardState by remember(normalizedInitialData) { mutableStateOf(normalizedInitialData) }

    var selectedElementId by remember { mutableStateOf<String?>(null) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(EditorTab.TEXT) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    var showColorPicker by remember { mutableStateOf(false) }
    var showEyeDropper by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    var isUploading by remember { mutableStateOf(false) }
    var showSuccessSheet by remember { mutableStateOf(false) }
    var showDownloadSuccessSheet by remember { mutableStateOf(false) }
    var toastData by remember { mutableStateOf<ToastData?>(null) }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    var cardToDownload by remember { mutableStateOf<CardData?>(null) }

    val hasUnsavedChanges by remember(currentCard, savedCardState, pendingImageUri) {
        derivedStateOf {
            currentCard != savedCardState || pendingImageUri != null
        }
    }

    BackHandler(enabled = hasUnsavedChanges) {
        showDiscardDialog = true
    }

    fun handleExit() {
        if (hasUnsavedChanges) {
            showDiscardDialog = true
        } else {
            onBackClick()
        }
    }

    var sheetMotionProgress by remember { mutableFloatStateOf(1.0f) }
    val isAnySheetVisible = showSuccessSheet || showDownloadSuccessSheet

    val targetScale = if (isAnySheetVisible) 0.92f + (0.08f * sheetMotionProgress) else 1.0f

    val backdropScaleState = animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadiusState = animateDpAsState(
        targetValue = if (isAnySheetVisible) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    val graphicsLayer = rememberGraphicsLayer()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.isImeVisible

    fun updateCardState(newCard: CardData) {
        val latest = history.getOrNull(historyIndex) ?: normalizedInitialData
        if (newCard == latest) return
        while (history.size - 1 > historyIndex) {
            history.removeAt(history.size - 1)
        }
        history.add(newCard)
        historyIndex = history.size - 1
    }

    fun undo() { if (historyIndex > 0) historyIndex-- }
    fun redo() { if (historyIndex < history.size - 1) historyIndex++ }

    fun updateElement(elementId: String, transform: (TextElement) -> TextElement) {
        val latest = history.getOrNull(historyIndex) ?: normalizedInitialData
        val existing = latest.elements.find { it.id == elementId && it.isEditable } ?: return
        val updated = transform(existing)
        if (updated == existing) return
        val newElements = latest.elements.map { if (it.id == elementId) updated else it }
        updateCardState(latest.copy(elements = newElements))
    }

    fun swapElements(id1: String, id2: String) {
        val e1 = currentCard.elements.find { it.id == id1 } ?: return
        val e2 = currentCard.elements.find { it.id == id2 } ?: return

        if (!e1.isEditable || !e2.isEditable) return

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

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            pendingImageUri = it
            updateCardState(currentCard.copy(backgroundUrl = it.toString(), backgroundRes = 0))
        }
    }

    LaunchedEffect(toastData) {
        if (toastData != null) {
            delay(3000.milliseconds)
            toastData = null
        }
    }

    var bottomSheetWeight by remember { mutableFloatStateOf(0.42f) }
    val minSheetWeight = 0.32f
    val maxSheetWeight = 0.64f
    var parentHeightPx by remember { mutableFloatStateOf(0f) }

    val colorRowLazyListState = rememberLazyListState()

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

    val selectedElement = currentCard.elements.find { it.id == selectedElementId && it.isEditable }

    var topBarBottomPx by remember { mutableFloatStateOf(0f) }
    var sheetTopPx by remember { mutableFloatStateOf(0f) }
    var selectedElementCenterYPx by remember { mutableFloatStateOf(0f) }
    var currentCanvasOffsetYPx by remember { mutableFloatStateOf(0f) }

    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val targetCanvasOffsetY = remember(
        isImeVisible,
        selectedElementId,
        selectedElementCenterYPx,
        topBarBottomPx,
        sheetTopPx
    ) {
        if (isImeVisible && selectedElement != null && sheetTopPx > topBarBottomPx && selectedElementCenterYPx > 0f) {
            val targetCenterY = (topBarBottomPx + sheetTopPx) / 2f
            val unoffsetElementCenterY = selectedElementCenterYPx - currentCanvasOffsetYPx
            val requiredOffsetYPx = targetCenterY - unoffsetElementCenterY
            (requiredOffsetYPx / density.density).dp
        } else {
            0.dp
        }
    }

    val animatedCanvasOffsetY by animateDpAsState(
        targetValue = targetCanvasOffsetY,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "CanvasOffsetYAnimation"
    )

    SideEffect {
        currentCanvasOffsetYPx = with(density) { animatedCanvasOffsetY.toPx() }
    }

    val bottomPadding = if (isImeVisible) 0.dp else (parentHeightPx * bottomSheetWeight / density.density).dp

    if (showDiscardDialog) {
        ConfirmationDialog(
            onDismissRequest = { showDiscardDialog = false },
            onConfirm = {
                showDiscardDialog = false
                onBackClick()
            },
            title = "Discard changes?",
            description = "All the changes you made will be lost.",
            confirmButtonText = "Discard",
            dismissButtonText = "Cancel",
            isDestructive = false
        )
    }

    if (showColorPicker && selectedElement != null) {
        Dialog(
            onDismissRequest = { showColorPicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .noRippleClickable { showColorPicker = false },
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.noRippleClickable { }) {
                    ColorPickerWheel(
                        initialColor = Color(selectedElement.colorHex.toInt()),
                        onColorSelected = { newColor ->
                            updateElement(selectedElement.id) { target ->
                                val currentAlpha = (target.colorHex shr 24) and 0xFFL
                                val alphaToUse = if (currentAlpha == 0L) 0xFFL else currentAlpha
                                val newRgb = newColor.toArgb().toLong() and 0x00FFFFFFL
                                target.copy(colorHex = (alphaToUse shl 24) or newRgb)
                            }
                        },
                        onDismiss = { showColorPicker = false }
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = backdropScaleState.value
                    scaleY = backdropScaleState.value
                    val radius = backdropCornerRadiusState.value
                    clip = isAnySheetVisible || radius > 0.dp
                    shape = RoundedCornerShape(radius.coerceAtLeast(0.dp))
                }
                .background(ContentPrimary)
                .onGloballyPositioned { coordinates ->
                    parentHeightPx = coordinates.size.height.toFloat()
                }
                .noRippleClickable { focusManager.clearFocus() }
        ) {
            SetStatusBarTheme(useDarkIcons = false)

            Box(
                modifier = Modifier
                    .size(280.dp, 373.dp)
                    .offset(x = (-2000).dp)
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                    }
            ) {
                cardToDownload?.let {
                    CardItem(
                        data = it,
                        forCapture = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = TopGradientBrush)
                    .statusBarsPadding()
                    .zIndex(10f)
                    .padding(12.dp)
                    .onGloballyPositioned { coordinates ->
                        topBarBottomPx = coordinates.positionInRoot().y + coordinates.size.height.toFloat()
                    },
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
                        onClick = ::handleExit,
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CustomTextButton(
                            text = if (isUploading) "Uploading..." else "Save",
                            onClick = {
                                if (isUploading) return@CustomTextButton

                                val uriToUpload = pendingImageUri
                                val isUsingCustomImage = currentCard.backgroundRes == 0 &&
                                        currentCard.backgroundUrl != null &&
                                        currentCard.backgroundUrl == uriToUpload?.toString()

                                if (uriToUpload != null && isUsingCustomImage) {
                                    isUploading = true
                                    onUploadImage(uriToUpload, { url ->
                                        isUploading = false
                                        pendingImageUri = null
                                        val finalCard = currentCard.copy(backgroundUrl = url)
                                        onDataChange(finalCard)
                                        savedCardState = finalCard
                                        showSuccessSheet = true
                                    }, { error ->
                                        isUploading = false
                                        toastData = ToastData(error, ToastType.ERROR)
                                    })
                                } else {
                                    onDataChange(currentCard)
                                    savedCardState = currentCard
                                    showSuccessSheet = true
                                }
                            },
                            leadingIcon = if (isUploading) null else painterResource(R.drawable.ic_check),
                            size = ButtonSize.Small,
                            contentColor = ContentPrimary,
                            containerColor = ContentInvPrimary,
                            enabled = !isUploading
                        )
                        Spacer(Modifier.width(8.dp))
                        Box {
                            CustomIconButton(
                                icon = rememberVectorPainter(image = Icons.Default.MoreVert),
                                onClick = { isMenuExpanded = !isMenuExpanded },
                                size = ButtonSize.Small,
                                contentColor = ContentInvPrimary,
                                containerColor = Color(0xE53D3D3D)
                            )

                            if (isMenuExpanded) {
                                Popup(
                                    onDismissRequest = { isMenuExpanded = false },
                                    offset = IntOffset(0, with(LocalDensity.current) { 48.dp.roundToPx() }),
                                    properties = PopupProperties(focusable = true),
                                    alignment = Alignment.TopEnd
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .height(64.dp)
                                            .shadow(8.dp, SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
                                            .clip(SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
                                            .background(Color(0xFF2C2C2C)),
                                        color = Color(0xFF2C2C2C)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .clip(SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
                                                .clickable {
                                                    isMenuExpanded = false
                                                    updateCardState(normalizedInitialData)
                                                    selectedElementId = null
                                                    zoomScale = 1f
                                                    panOffset = Offset.Zero
                                                }
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Sync,
                                                contentDescription = null,
                                                tint = ContentInvPrimary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = "Reset to Defaults",
                                                style = JasnifyTheme.typography.labelXLarge,
                                                color = ContentInvPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .zIndex(0f)
                    .padding(top = 64.dp, bottom = bottomPadding)
                    .offset(y = animatedCanvasOffsetY),
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
                        onSelectElement = { id ->
                            selectedElementId = id
                        },
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
                        onUpdateCardBgName = { updateCardState(currentCard.copy(bgName = it)) },
                        onSelectedElementCenterYChanged = { y -> selectedElementCenterYPx = y },
                        zoomScale = zoomScale,
                        modifier = Modifier
                            .width(cardWidth)
                            .aspectRatio(3f / 4f)
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Main)
                                    val isDownConsumed = down.isConsumed

                                    do {
                                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                                        val pressedPointers = event.changes.filter { it.pressed }

                                        if (pressedPointers.size >= 2) {
                                            val zoomChange = event.calculateZoom()
                                            val panChange = event.calculatePan()

                                            if (zoomChange != 1f || panChange != Offset.Zero) {
                                                zoomScale = (zoomScale * zoomChange).coerceIn(1f, 4f)
                                                if (zoomScale > 1f) {
                                                    panOffset += panChange
                                                } else {
                                                    panOffset = Offset.Zero
                                                }
                                                event.changes.forEach { it.consume() }
                                            }
                                        } else if (pressedPointers.size == 1 && zoomScale > 1f && !isDownConsumed) {
                                            val change = event.changes.firstOrNull { it.pressed }
                                            if (change != null && !change.isConsumed) {
                                                val panChange = event.calculatePan()
                                                if (panChange != Offset.Zero) {
                                                    panOffset += panChange
                                                    change.consume()
                                                }
                                            }
                                        }
                                    } while (currentEvent.changes.any { it.pressed })
                                }
                            }
                            .graphicsLayer {
                                scaleX = zoomScale
                                scaleY = zoomScale
                                translationX = panOffset.x
                                translationY = panOffset.y
                            }
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
                        .then(if (isImeVisible) Modifier.wrapContentHeight() else Modifier.fillMaxHeight())
                        .onGloballyPositioned { coordinates ->
                            sheetTopPx = coordinates.positionInRoot().y
                        },
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
                                    EditorTab.COLOR -> "Select Text Color"
                                    EditorTab.SIZE -> "Edit Size"
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
                        var containerCords by remember { mutableStateOf<LayoutCoordinates?>(null) }

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
                                                    val pos = parent.localPositionOf(cords, Offset.Zero).x
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
                                                    onValueChange = { newText ->
                                                        updateElement(selectedElement.id) { it.copy(text = newText) }
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
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = 16.dp)
                                            .noRippleClickable { focusManager.clearFocus() }
                                    ) {
                                        ThemeSelectorSection(
                                            currentCard = currentCard,
                                            cardRoomData = cardRoomData,
                                            isUploading = isUploading,
                                            onSelectTheme = { resId, url, _ ->
                                                val updated = currentCard.copy(
                                                    backgroundRes = resId,
                                                    backgroundUrl = url,
                                                    bgName = if (currentCard.bgName.isBlank()) "" else currentCard.bgName
                                                )
                                                updateCardState(updated)
                                            },
                                            onUpdateThemeName = onUpdateThemeName,
                                            onUpdateCardBgName = { newName ->
                                                val updated = currentCard.copy(bgName = newName)
                                                updateCardState(updated)
                                            },
                                            onUploadClick = { imageLauncher.launch("image/*") }
                                        )
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
                                EditorTab.COLOR -> {
                                    if (selectedElement != null) {
                                        key(selectedElement.id) {
                                            val initialElement = remember(selectedElement.id, normalizedInitialData) {
                                                normalizedInitialData.elements.find { it.id == selectedElement.id }
                                            }

                                            LaunchedEffect(selectedElement.id) {
                                                val activeRgb = selectedElement.colorHex and 0x00FFFFFFL
                                                val matchIndex = ColorPaletteHexes.indexOfFirst { (it and 0x00FFFFFFL) == activeRgb }
                                                if (matchIndex >= 0) {
                                                    colorRowLazyListState.animateScrollToItem(matchIndex + 2)
                                                }
                                            }

                                            val currentAlpha = (selectedElement.colorHex shr 24) and 0xFFL
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
                                                    item(key = "eyedropper") {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(56.dp)
                                                                .clip(CircleShape)
                                                                .noRippleClickable {
                                                                    showEyeDropper = true
                                                                },
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(
                                                                painter = painterResource(R.drawable.ic_color_picker),
                                                                contentDescription = "Eyedropper",
                                                                tint = ContentPrimary,
                                                                modifier = Modifier.size(32.dp)
                                                            )
                                                        }
                                                    }

                                                    item(key = "wheel_picker") {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(56.dp)
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
                                                                    showColorPicker = true
                                                                },
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

                                                    items(
                                                        items = ColorPaletteHexes,
                                                        key = { it }
                                                    ) { hex ->
                                                        val isSelected = (selectedElement.colorHex and 0x00FFFFFFL) == (hex and 0x00FFFFFFL)
                                                        val swatchColor = Color(hex.toInt())

                                                        Box(
                                                            modifier = Modifier
                                                                .size(56.dp)
                                                                .clip(CircleShape)
                                                                .background(swatchColor)
                                                                .border(1.dp, Color(0x26000000), CircleShape)
                                                                .clickable {
                                                                    val swatchRgb = hex and 0x00FFFFFFL
                                                                    updateElement(selectedElement.id) { target ->
                                                                        val activeAlpha = (target.colorHex shr 24) and 0xFFL
                                                                        val updatedColorHex = (activeAlpha shl 24) or swatchRgb
                                                                        target.copy(colorHex = updatedColorHex)
                                                                    }
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
                                                    val initialOpacity = remember(initialElement) {
                                                        ((initialElement?.colorHex?.shr(24)?.and(0xFFL)?.toFloat() ?: 255f) / 255f * 100f)
                                                    }

                                                    CustomSliderCard(
                                                        label = "Opacity",
                                                        value = currentOpacity,
                                                        onValueChange = { newOpacity ->
                                                            val alphaByte = round((newOpacity / 100f) * 255f).toLong().coerceIn(0L, 255L)
                                                            updateElement(selectedElement.id) { target ->
                                                                val currentRgb = target.colorHex and 0x00FFFFFFL
                                                                val updatedColorHex = (alphaByte shl 24) or currentRgb
                                                                target.copy(colorHex = updatedColorHex)
                                                            }
                                                        },
                                                        valueRange = 0f..100f,
                                                        unit = "%",
                                                        icon = painterResource(R.drawable.ic_opacity),
                                                        onReset = {
                                                            val initialHex = initialElement?.colorHex ?: 0xFF000000L
                                                            val savedAlpha = (initialHex shr 24) and 0xFFL
                                                            updateElement(selectedElement.id) { target ->
                                                                val currentRgb = target.colorHex and 0x00FFFFFFL
                                                                val updatedColorHex = (savedAlpha shl 24) or currentRgb
                                                                target.copy(colorHex = updatedColorHex)
                                                            }
                                                        },
                                                        showReset = abs(currentOpacity - initialOpacity) > 0.1f,
                                                        shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
                                                    )
                                                }
                                            }
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
                                                val icon: Painter,
                                                val isAuto: Boolean,
                                                val showReset: Boolean,
                                                val onValueChange: (Float) -> Unit,
                                                val onReset: () -> Unit
                                            )

                                            val sliders = listOf(
                                                SliderConfigData(
                                                    label = "Font Size",
                                                    value = selectedElement.fontSizeSp,
                                                    icon = painterResource(R.drawable.ic_font_size),
                                                    isAuto = false,
                                                    showReset = selectedElement.fontSizeSp != (initialElement?.fontSizeSp ?: 24f),
                                                    onValueChange = { v ->
                                                        updateElement(selectedElement.id) { it.copy(fontSizeSp = v) }
                                                    },
                                                    onReset = {
                                                        val savedFontSize = initialElement?.fontSizeSp ?: 24f
                                                        updateElement(selectedElement.id) { it.copy(fontSizeSp = savedFontSize) }
                                                    }
                                                ),
                                                SliderConfigData(
                                                    label = "Line Height",
                                                    value = displayLineHeightValue,
                                                    icon = painterResource(R.drawable.ic_line_height),
                                                    isAuto = isLineHeightAuto,
                                                    showReset = selectedElement.lineHeightSp != (initialElement?.lineHeightSp ?: 0f),
                                                    onValueChange = { v ->
                                                        updateElement(selectedElement.id) { it.copy(lineHeightSp = v) }
                                                    },
                                                    onReset = {
                                                        val savedLineHeight = initialElement?.lineHeightSp ?: 0f
                                                        updateElement(selectedElement.id) { it.copy(lineHeightSp = savedLineHeight) }
                                                    }
                                                ),
                                                SliderConfigData(
                                                    label = "Vertical Padding",
                                                    value = selectedElement.verticalPaddingSp,
                                                    icon = painterResource(R.drawable.ic_vertical_spacing),
                                                    isAuto = false,
                                                    showReset = selectedElement.verticalPaddingSp != (initialElement?.verticalPaddingSp ?: 0f) ||
                                                            selectedElement.paddingMode != (initialElement?.paddingMode ?: CardPaddingMode.BOTH),
                                                    onValueChange = { v ->
                                                        updateElement(selectedElement.id) { it.copy(verticalPaddingSp = v) }
                                                    },
                                                    onReset = {
                                                        val savedVerticalPadding = initialElement?.verticalPaddingSp ?: 0f
                                                        val savedPaddingMode = initialElement?.paddingMode ?: CardPaddingMode.BOTH
                                                        updateElement(selectedElement.id) {
                                                            it.copy(
                                                                verticalPaddingSp = savedVerticalPadding,
                                                                paddingMode = savedPaddingMode
                                                            )
                                                        }
                                                    }
                                                ),
                                                SliderConfigData(
                                                    label = "Letter Spacing",
                                                    value = selectedElement.letterSpacingSp,
                                                    icon = painterResource(R.drawable.ic_letter_spacing),
                                                    isAuto = false,
                                                    showReset = selectedElement.letterSpacingSp != (initialElement?.letterSpacingSp ?: 0f),
                                                    onValueChange = { v ->
                                                        updateElement(selectedElement.id) { it.copy(letterSpacingSp = v) }
                                                    },
                                                    onReset = {
                                                        val savedLetterSpacing = initialElement?.letterSpacingSp ?: 0f
                                                        updateElement(selectedElement.id) { it.copy(letterSpacingSp = savedLetterSpacing) }
                                                    }
                                                ),
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
                                                            Column {
                                                                CustomSliderCard(
                                                                    label = config.label,
                                                                    value = config.value,
                                                                    onValueChange = config.onValueChange,
                                                                    valueRange = 0f..100f,
                                                                    unit = "px",
                                                                    icon = config.icon,
                                                                    isAuto = config.isAuto,
                                                                    onReset = config.onReset,
                                                                    showReset = config.showReset,
                                                                    shape = SquircleShape(0.dp)
                                                                )

                                                                if (config.label == "Vertical Padding") {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                                                            .padding(bottom = 8.dp)
                                                                    ) {
                                                                        IosSegmentedControl(
                                                                            options = CardPaddingMode.entries,
                                                                            selectedOption = selectedElement.paddingMode,
                                                                            onOptionSelected = { mode ->
                                                                                updateElement(selectedElement.id) { it.copy(paddingMode = mode) }
                                                                            },
                                                                            labelProvider = { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
                                                                            modifier = Modifier.fillMaxWidth()
                                                                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.12f), RoundedCornerShape(100))
                                                                        )
                                                                    }
                                                                }
                                                            }

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
                            }
                        }
                    }
                }
            }
        }

        if (showEyeDropper && selectedElement != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(100f)
            ) {
                EyeDropperOverlay(
                    onColorPicked = { pickedColor ->
                        updateElement(selectedElement.id) { target ->
                            val alpha = (target.colorHex shr 24) and 0xFFL
                            val alphaToUse = if (alpha == 0L) 0xFFL else alpha
                            val newRgb = pickedColor.toArgb().toLong() and 0x00FFFFFFL
                            target.copy(colorHex = (alphaToUse shl 24) or newRgb)
                        }
                        showEyeDropper = false
                    },
                )
            }
        }

        AnimatedVisibility(
            visible = toastData != null,
            enter = slideInVertically(initialOffsetY = { -it - 500 }),
            exit = slideOutVertically(targetOffsetY = { -it - 500 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .zIndex(200f)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            toastData?.let {
                CustomToast(message = it.message ?: "", type = it.type)
            }
        }

        if (showSuccessSheet) {
            CustomBottomSheet(
                isVisible = true,
                onDismiss = { showSuccessSheet = false },
                showCloseButton = false,
                showDragHandle = false,
                sheetHeight = 480.dp,
                onProgress = { sheetMotionProgress = it }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_tick),
                                contentDescription = "Success checkmark",
                                modifier = Modifier.size(64.dp),
                                tint = ContentBrandDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Saved Successfully!",
                            style = JasnifyTheme.typography.displaySmall,
                            textAlign = TextAlign.Center,
                            color = ContentBrandDark
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextButton(
                            text = "Download",
                            onClick = {
                                showSuccessSheet = false
                                coroutineScope.launch {
                                    cardToDownload = currentCard
                                    delay(100.milliseconds)
                                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                    val success = ShareUtils.downloadImage(context, bitmap)
                                    cardToDownload = null
                                    if (success) {
                                        showDownloadSuccessSheet = true
                                    } else {
                                        toastData = ToastData("Failed to download", ToastType.ERROR)
                                    }
                                }
                            },
                            leadingIcon = painterResource(R.drawable.ic_download),
                            type = ButtonType.Secondary,
                            shapeStyle = ButtonShapeStyle.Square,
                            modifier = Modifier.weight(1f)
                        )

                        CustomTextButton(
                            text = "Done",
                            onClick = {
                                showSuccessSheet = false
                                onBackClick()
                            },
                            modifier = Modifier.weight(1f),
                            shapeStyle = ButtonShapeStyle.Square
                        )
                    }
                }
            }
        }

        if (showDownloadSuccessSheet) {
            CustomSuccessBottomSheet(
                message = "Image Downloaded!",
                onDismiss = { showDownloadSuccessSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfessionalFontSelector(
    selectedElement: TextElement,
    bottomSheetWeight: Float,
    onUpdateElement: (String, (TextElement) -> TextElement) -> Unit
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

    val currentElementId by rememberUpdatedState(selectedElement.id)

    LaunchedEffect(centerItemIndex) {
        if (fontLazyListState.isScrollInProgress) {
            val currentFont = fontTypes.getOrNull(centerItemIndex)
            if (currentFont != null && selectedElement.fontStyle != currentFont) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onUpdateElement(currentElementId) { it.copy(fontStyle = currentFont) }
            }
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
                itemsIndexed(
                    items = fontTypes,
                    key = { _, fontType -> fontType.name }
                ) { index, fontType ->
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
                                onUpdateElement(selectedElement.id) { it.copy(fontStyle = fontType) }
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
            val currentAlignIndex = remember(selectedElement.textAlign) {
                AlignmentOptionsList.indexOfFirst { it.first == selectedElement.textAlign }.coerceAtLeast(0)
            }

            val alignPositions = remember { mutableStateListOf<Float>() }
            val alignWidths = remember { mutableStateListOf<Float>() }
            var alignContainerCords by remember { mutableStateOf<LayoutCoordinates?>(null) }

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
                            onUpdateElement(selectedElement.id) { it.copy(isBold = !it.isBold) }
                        }
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_italic),
                            isSelected = selectedElement.isItalic
                        ) {
                            onUpdateElement(selectedElement.id) { it.copy(isItalic = !it.isItalic) }
                        }
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_underline),
                            isSelected = selectedElement.isUnderline
                        ) {
                            onUpdateElement(selectedElement.id) { it.copy(isUnderline = !it.isUnderline) }
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(0.16f),
                        thickness = 1.dp
                    )

                    AlignmentSelectorBar(
                        selectedTextAlign = selectedElement.textAlign,
                        currentAlignIndex = currentAlignIndex,
                        alignPositions = alignPositions,
                        alignWidths = alignWidths,
                        alignContainerCords = alignContainerCords,
                        onCordsChange = { alignContainerCords = it },
                        density = density,
                        onAlignSelected = { align ->
                            onUpdateElement(selectedElement.id) { it.copy(textAlign = align) }
                        }
                    )
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
                            onUpdateElement(selectedElement.id) { it.copy(isBold = !it.isBold) }
                        }
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_italic),
                            isSelected = selectedElement.isItalic
                        ) {
                            onUpdateElement(selectedElement.id) { it.copy(isItalic = !it.isItalic) }
                        }
                        FormatToggleButton(
                            icon = painterResource(R.drawable.ic_underline),
                            isSelected = selectedElement.isUnderline
                        ) {
                            onUpdateElement(selectedElement.id) { it.copy(isUnderline = !it.isUnderline) }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(0.16f))
                    )

                    AlignmentSelectorBar(
                        selectedTextAlign = selectedElement.textAlign,
                        currentAlignIndex = currentAlignIndex,
                        alignPositions = alignPositions,
                        alignWidths = alignWidths,
                        alignContainerCords = alignContainerCords,
                        onCordsChange = { alignContainerCords = it },
                        density = density,
                        onAlignSelected = { align ->
                            onUpdateElement(selectedElement.id) { it.copy(textAlign = align) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlignmentSelectorBar(
    selectedTextAlign: CardTextAlign,
    currentAlignIndex: Int,
    alignPositions: MutableList<Float>,
    alignWidths: MutableList<Float>,
    alignContainerCords: LayoutCoordinates?,
    onCordsChange: (LayoutCoordinates) -> Unit,
    density: androidx.compose.ui.unit.Density,
    onAlignSelected: (CardTextAlign) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
            .background(color = SurfaceSecondary, shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
            .padding(4.dp)
            .onGloballyPositioned { onCordsChange(it) }
    ) {
        if (alignPositions.size == AlignmentOptionsList.size) {
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
            AlignmentOptionsList.forEachIndexed { index, (align, icon) ->
                val isSelected = selectedTextAlign == align
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
                                val pos = parent.localPositionOf(cords, Offset.Zero).x
                                alignPositions[index] = (pos / density.density)
                                alignWidths[index] = (cords.size.width / density.density)
                            }
                        }
                        .size(48.dp)
                        .noRippleClickable { onAlignSelected(align) },
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
    card: CardData,
    selectedElementId: String?,
    onSelectElement: (String) -> Unit,
    onDoubleTapElement: (String) -> Unit,
    onUpdateElement: (String, (TextElement) -> TextElement) -> Unit,
    onSwapElements: (String, String) -> Unit,
    onDeleteElement: (String) -> Unit,
    onUpdateCardBgName: (String) -> Unit = {},
    onSelectedElementCenterYChanged: (Float) -> Unit = {},
    zoomScale: Float = 1f,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
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
                .background(Color(card.backgroundColorHex.toInt()))
                .pointerInput(Unit) {
                    detectTapGestures {
                        onSelectElement("")
                        focusManager.clearFocus()
                    }
                }
        ) {
            if (card.backgroundUrl != null) {
                AsyncImage(
                    model = card.backgroundUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = card.backgroundRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        val editableElements = remember(card.elements) {
            card.elements.filter { it.isEditable }.sortedBy { it.yRatio }
        }
        val uneditableElements = remember(card.elements) {
            card.elements.filter { !it.isEditable }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = (20 * scaleFactor).dp, vertical = (40 * scaleFactor).dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        onSelectElement("")
                        focusManager.clearFocus()
                    }
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            editableElements.forEach { element ->
                key(element.id) {
                    InteractiveTextElementItem(
                        element = element,
                        canvasSize = canvasSize,
                        scaleFactor = scaleFactor,
                        isSelected = element.id == selectedElementId && element.isEditable,
                        otherElements = editableElements.filter { it.id != element.id },
                        onSelect = {
                            if (element.isEditable) onSelectElement(element.id)
                            else onSelectElement("")
                        },
                        onDoubleTap = {
                            if (element.isEditable) onDoubleTapElement(element.id)
                        },
                        onUpdate = onUpdateElement,
                        onSwap = onSwapElements,
                        onDelete = {
                            if (element.isEditable) onDeleteElement(element.id)
                        },
                        onSelectedElementCenterYChanged = onSelectedElementCenterYChanged,
                        zoomScale = zoomScale
                    )
                }
            }
        }

        uneditableElements.forEach { element ->
            if (canvasSize.height > 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val yPosDp = (canvasSize.height * element.yRatio / density).dp
                    Box(
                        modifier = Modifier
                            .offset(y = yPosDp)
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        InteractiveTextElementItem(
                            element = element,
                            canvasSize = canvasSize,
                            scaleFactor = scaleFactor,
                            isSelected = false,
                            otherElements = emptyList(),
                            onSelect = { onSelectElement("") },
                            onDoubleTap = {},
                            onUpdate = { _, _ -> },
                            onSwap = { _, _ -> },
                            onDelete = {},
                            onSelectedElementCenterYChanged = {},
                            zoomScale = zoomScale
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalIndirectPointerApi::class)
@Composable
fun InteractiveTextElementItem(
    element: TextElement,
    canvasSize: IntSize,
    scaleFactor: Float,
    isSelected: Boolean,
    otherElements: List<TextElement>,
    onSelect: () -> Unit,
    onDoubleTap: () -> Unit,
    onUpdate: (String, (TextElement) -> TextElement) -> Unit,
    onSwap: (String, String) -> Unit,
    onDelete: () -> Unit,
    onSelectedElementCenterYChanged: (Float) -> Unit = {},
    zoomScale: Float = 1f
) {
    if (canvasSize.width == 0 || canvasSize.height == 0) return
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    val currentElementId by rememberUpdatedState(element.id)
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
                .widthIn(max = (canvasWidthPx * element.widthRatio / density.density).dp + 120.dp)
                .wrapContentWidth()
                .then(
                    if (element.isEditable) {
                        Modifier
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
                                            val sorted = (currentOtherElements + element).sortedBy { it.yRatio }
                                            val currentIndex = sorted.indexOfFirst { it.id == element.id }
                                            val targetIndex = currentIndex + direction

                                            if (targetIndex in sorted.indices) {
                                                val target = sorted[targetIndex]
                                                if (target.isEditable) {
                                                    currentOnSwap(element.id, target.id)
                                                    dragOffsetY = 0f
                                                }
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
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            val topPadding = when (element.paddingMode) {
                CardPaddingMode.BOTH,
                CardPaddingMode.TOP -> (element.verticalPaddingSp * scaleFactor / 2).dp
                else -> 0.dp
            }
            val bottomPadding = when (element.paddingMode) {
                CardPaddingMode.BOTH,
                CardPaddingMode.BOTTOM -> (element.verticalPaddingSp * scaleFactor / 2).dp
                else -> 0.dp
            }

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = topPadding, bottom = bottomPadding)
                    .onGloballyPositioned { coordinates ->
                        if (isSelected) {
                            val yCenter = coordinates.positionInRoot().y + (coordinates.size.height / 2f)
                            onSelectedElementCenterYChanged(yCenter)
                        }
                    }
                    .then(
                        if (isSelected && element.isEditable) Modifier
                            .border(2.dp, Color(0xFF6750A4), SquircleShape(CornerExtraSmall))
                        else Modifier
                    ),
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
                        color = Color(element.colorHex.toInt()),
                        textAlign = element.textAlign.toComposeTextAlign(),
                        letterSpacing = (element.letterSpacingSp * scaleFactor).sp,
                        lineHeight = if (element.lineHeightSp > 0f) (element.lineHeightSp * scaleFactor).sp else TextUnit.Unspecified,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        ),
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    )
                )
            }

            if (isSelected && element.isEditable) {
                Box(
                    modifier = Modifier.matchParentSize()
                ) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                        val zoomFactor = zoomScale.coerceAtLeast(1f)

                        val reducedHandleSize = (22.dp / zoomFactor).coerceIn(14.dp, 24.dp)
                        val reducedIconSize = (14.dp / zoomFactor).coerceIn(9.dp, 15.dp)

                        val widthHandleWidth = (26.dp / zoomFactor).coerceIn(16.dp, 28.dp)
                        val widthHandleHeight = (18.dp / zoomFactor).coerceIn(12.dp, 20.dp)
                        val widthIconSize = (16.dp / zoomFactor).coerceIn(10.dp, 17.dp)

                        val cornerOffset = reducedHandleSize

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = cornerOffset, y = -cornerOffset)
                                .requiredSize(reducedHandleSize)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error)
                                .pointerInput(element.id) {
                                    awaitEachGesture {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        down.consume()
                                        val up = waitForUpOrCancellation()
                                        if (up != null) {
                                            up.consume()
                                            onDelete()
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Delete",
                                tint = ContentInvPrimary,
                                modifier = Modifier.requiredSize(reducedIconSize)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(x = (6.dp / zoomFactor).coerceIn(4.dp, 8.dp))
                                .requiredSize(width = widthHandleWidth, height = widthHandleHeight)
                                .clip(CircleShape)
                                .background(Color(0xFF6750A4))
                                .pointerInput(element.id, zoomScale) {
                                    awaitEachGesture {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        down.consume()
                                        val pointerId = down.id

                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                            if (!change.pressed) break

                                            val dragAmount = change.positionChange()
                                            if (dragAmount != Offset.Zero) {
                                                change.consume()
                                                val adjustedDragX = dragAmount.x / zoomScale
                                                val deltaWidthRatio = (adjustedDragX / canvasWidthPx) * 2f
                                                currentOnUpdate(currentElementId) { target ->
                                                    target.copy(widthRatio = (target.widthRatio + deltaWidthRatio).coerceIn(0.2f, 1f))
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Code,
                                contentDescription = "Width",
                                tint = ContentInvPrimary,
                                modifier = Modifier.requiredSize(widthIconSize)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = cornerOffset, y = cornerOffset)
                                .requiredSize(reducedHandleSize)
                                .clip(CircleShape)
                                .background(ContentSecondary)
                                .pointerInput(element.id, zoomScale) {
                                    awaitEachGesture {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        down.consume()
                                        val pointerId = down.id

                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                            if (!change.pressed) break

                                            val dragAmount = change.positionChange()
                                            if (dragAmount != Offset.Zero) {
                                                change.consume()
                                                val adjustedDragX = dragAmount.x / zoomScale
                                                val adjustedDragY = dragAmount.y / zoomScale
                                                val scaleChange = (adjustedDragX + adjustedDragY) * 0.15f
                                                currentOnUpdate(currentElementId) { target ->
                                                    target.copy(fontSizeSp = (target.fontSizeSp + (scaleChange / scaleFactor)).coerceIn(8f, 72f))
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.OpenInFull,
                                contentDescription = "Resize",
                                tint = ContentInvPrimary,
                                modifier = Modifier
                                    .requiredSize(reducedIconSize)
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
}

@Composable
fun ThemeSelectorSection(
    currentCard: CardData,
    cardRoomData: CardRoomData?,
    isUploading: Boolean,
    onSelectTheme: (Int, String?, String) -> Unit,
    onUpdateThemeName: (String, String) -> Unit,
    onUpdateCardBgName: (String) -> Unit,
    onUploadClick: () -> Unit
) {
    val themes = remember(cardRoomData) {
        val uploaded = cardRoomData?.themes?.filter { !it.isDefault }?.map {
            CardThemeItem(
                id = it.id,
                name = it.name,
                resId = it.resId,
                url = it.url,
                isDefault = it.isDefault
            )
        } ?: emptyList()

        uploaded + DefaultThemeItems
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item(key = "custom_upload") {
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
                        .clickable(enabled = !isUploading) { onUploadClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = ContentSecondary,
                            strokeWidth = 3.dp
                        )
                    } else {
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
                Text(
                    text = "Custom",
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        items(
            items = themes,
            key = { it.id }
        ) { theme ->
            val isSelected = if (theme.isDefault) {
                currentCard.backgroundRes == theme.resId
            } else {
                currentCard.backgroundRes == 0 && currentCard.backgroundUrl == theme.url
            }

            ThemeItem(
                theme = theme,
                isSelected = isSelected,
                cardBgName = currentCard.bgName,
                onSelect = { onSelectTheme(theme.resId, theme.url, theme.name) },
                onUpdateName = { newName ->
                    if (!theme.isDefault) {
                        onUpdateThemeName(theme.id, newName)
                        if (isSelected) {
                            onUpdateCardBgName(newName)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ThemeItem(
    theme: CardThemeItem,
    isSelected: Boolean,
    cardBgName: String,
    onSelect: () -> Unit,
    onUpdateName: (String) -> Unit
) {
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
                .clickable { onSelect() }
        ) {
            if (theme.url != null) {
                AsyncImage(
                    model = theme.url,
                    contentDescription = theme.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = theme.resId),
                    contentDescription = theme.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
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

        if (isSelected && !theme.isDefault) {
            BasicTextField(
                value = cardBgName,
                onValueChange = onUpdateName,
                textStyle = JasnifyTheme.typography.labelMedium.copy(
                    color = ContentBrandDark,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.width(120.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (cardBgName.isBlank()) {
                            Text(
                                text = "Untitled",
                                style = JasnifyTheme.typography.labelMedium,
                                color = ContentBrandDark.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                        innerTextField()
                    }
                }
            )
        } else {
            Text(
                text = theme.name.ifBlank { "Untitled" },
                style = JasnifyTheme.typography.labelMedium,
                color = if (isSelected) ContentBrandDark else ContentSecondary,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Preview(name = "Edit Card Details - Light Mode", showBackground = true, showSystemUi = true)
@Composable
fun EditCardDetailsScreenPreview() {
    JasnifyTheme {
        EditCardDetailsScreen(
            initialData = CardData(),
            onDataChange = {},
            onBackClick = {}
        )
    }
}