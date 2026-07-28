package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import java.text.NumberFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class ThousandsSeparatorVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formatted = try {
            val parsed = originalText.toLong()
            NumberFormat.getNumberInstance(Locale("en", "IN")).format(parsed)
        } catch (e: Exception) {
            originalText
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                var originalIdx = 0
                var transformedIdx = 0
                while (originalIdx < offset && transformedIdx < formatted.length) {
                    if (formatted[transformedIdx] == ',') {
                        transformedIdx++
                    } else {
                        originalIdx++
                        transformedIdx++
                    }
                }
                return transformedIdx
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val clampedOffset = offset.coerceAtMost(formatted.length)
                var originalCount = 0
                for (i in 0 until clampedOffset) {
                    if (formatted[i] != ',') {
                        originalCount++
                    }
                }
                return originalCount.coerceAtMost(originalText.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (amount: Long, receiver: String, category: String, emoji: String, phoneNumber: String, note: String) -> Unit,
    categories: List<String>,
    onAddCategory: (String) -> Unit,
    initialAmount: String = "",
    initialReceiver: String = "",
    initialCategory: String = "",
    initialEmoji: String = "",
    initialPhoneNumber: String = "",
    initialNote: String = ""
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var toastData by remember { mutableStateOf(ToastData()) }
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    var amountTextFieldValue by remember(initialAmount) {
        mutableStateOf(
            TextFieldValue(
                text = initialAmount,
                selection = TextRange(initialAmount.length)
            )
        )
    }
    var receiverName by remember(initialReceiver) { mutableStateOf(initialReceiver) }
    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory) }
    var selectedEmoji by remember(initialEmoji) { mutableStateOf(initialEmoji) }
    var phoneNumber by remember(initialPhoneNumber) { mutableStateOf(initialPhoneNumber) }
    var note by remember(initialNote) { mutableStateOf(initialNote) }

    var dynamicCategories by remember(categories) { mutableStateOf(categories) }

    var showCustomCategorySheet by remember { mutableStateOf(false) }
    val customCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val headingTitle = if (initialReceiver.isNotEmpty()) "Edit expense" else "Add an expense"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
        dragHandle = null,
        sheetGesturesEnabled = true,
    ) {
        val view = LocalView.current
        DisposableEffect(view) {
            var parent = view.parent
            var dialogWindow: android.view.Window? = null
            while (parent != null) {
                if (parent is DialogWindowProvider) {
                    dialogWindow = parent.window
                    break
                }
                parent = parent.parent
            }
            dialogWindow?.let { w ->
                val colorInt = SurfacePrimary.toArgb()
                w.navigationBarColor = colorInt
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    w.isNavigationBarContrastEnforced = false
                }
                val isLightBackground = ColorUtils.calculateLuminance(colorInt) > 0.5
                WindowCompat.getInsetsController(w, view).isAppearanceLightNavigationBars = isLightBackground
            }
            onDispose {}
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = toastData.message != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .zIndex(998f)
            ) {
                CustomToast(
                    message = toastData.message ?: "",
                    type = toastData.type
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(999f)
                    .clip(SquircleShape(CornerExtraLarge, CornerExtraLarge, 0.dp, 0.dp))
                    .background(SurfacePrimary)
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                        .width(56.dp)
                        .height(4.dp)
                        .background(ContentTertiary, shape = SquircleShape(100))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(12.dp, 0.dp, 12.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = headingTitle,
                        style = JasnifyTheme.typography.displayLarge,
                        color = ContentPrimary
                    )
                    TopBarIconButton(
                        backgroundStyle = ButtonBackground.OPAQUE,
                        icon = TopIcon.Predefined.CLOSE,
                        iconSize = 18.dp,
                        onClick = onDismiss
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(600.dp)) {
                    AddExpenseSheetContent(
                        amountTextFieldValue = amountTextFieldValue,
                        onAmountChange = { amountTextFieldValue = it },
                        receiverName = receiverName,
                        onReceiverChange = { receiverName = it },
                        selectedCategory = selectedCategory,
                        onCategorySelect = { selectedCategory = it },
                        dynamicCategories = dynamicCategories,
                        selectedEmoji = selectedEmoji,
                        onEmojiChange = { selectedEmoji = it },
                        phoneNumber = phoneNumber,
                        onPhoneNumberChange = { phoneNumber = it },
                        note = note,
                        onNoteChange = { note = it },
                        onCustomCategoryClick = { showCustomCategorySheet = true },
                        onDismiss = onDismiss,
                        onSave = { amt, rec, cat ->
                            if (amountTextFieldValue.text.isBlank()) {
                                toastData = ToastData("Please enter the expense!", ToastType.ERROR)
                            }else if (receiverName.isBlank()) {
                                toastData = ToastData("Please enter receiver name!", ToastType.ERROR)
                            }else if (selectedCategory.isBlank()) {
                                toastData = ToastData("Please select an expense category!", ToastType.ERROR)
                            } else {
                                // Default fallback to 💸 emoji if left blank by user
                                val finalEmoji = selectedEmoji.ifBlank { "💸" }
                                onSave(amt, rec, cat, finalEmoji, phoneNumber, note)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showCustomCategorySheet) {
        AddCustomCategoryBottomSheet(
            sheetState = customCategorySheetState,
            onDismiss = {
                coroutineScope.launch { customCategorySheetState.hide() }.invokeOnCompletion {
                    showCustomCategorySheet = false
                }
            },
            onAddCategory = { newCategory ->
                if (newCategory.isBlank()) {
                    toastData = ToastData("Please enter an expense category!", ToastType.ERROR)
                } else {
                    onAddCategory(newCategory)
                    dynamicCategories = dynamicCategories + newCategory
                    selectedCategory = newCategory
                    coroutineScope.launch { customCategorySheetState.hide() }.invokeOnCompletion {
                        showCustomCategorySheet = false
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomCategoryBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddCategory: (String) -> Unit,
    initialCategoryName: String = "",
    heading: String = "Add custom category"
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
        dragHandle = null,
        sheetGesturesEnabled = true,
    ) {
        val view = LocalView.current
        DisposableEffect(view) {
            var parent = view.parent
            var dialogWindow: android.view.Window? = null
            while (parent != null) {
                if (parent is DialogWindowProvider) {
                    dialogWindow = parent.window
                    break
                }
                parent = parent.parent
            }
            dialogWindow?.let { w ->
                val colorInt = SurfacePrimary.toArgb()
                w.navigationBarColor = colorInt
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    w.isNavigationBarContrastEnforced = false
                }
                val isLightBackground = ColorUtils.calculateLuminance(colorInt) > 0.5
                WindowCompat.getInsetsController(w, view).isAppearanceLightNavigationBars = isLightBackground
            }
            onDispose {}
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(999f)
                    .clip(SquircleShape(CornerExtraLarge, CornerExtraLarge, 0.dp, 0.dp))
                    .background(SurfacePrimary)
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                        .width(56.dp)
                        .height(4.dp)
                        .background(ContentTertiary, shape = SquircleShape(100))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(12.dp, 0.dp, 12.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = heading,
                        style = JasnifyTheme.typography.displayLarge,
                        color = ContentPrimary
                    )
                    TopBarIconButton(
                        backgroundStyle = ButtonBackground.OPAQUE,
                        icon = TopIcon.Predefined.CLOSE,
                        iconSize = 18.dp,
                        onClick = onDismiss
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(161.dp)) {
                    AddCustomCategorySheetContent(
                        onDismiss = onDismiss,
                        onAddCategory = onAddCategory,
                        initialCategoryName = initialCategoryName
                    )
                }
            }
        }
    }
}

fun getEmojiFromString(text: String): String? {
    if (text.isEmpty()) return null
    val length = text.length
    if (length >= 2 && Character.isHighSurrogate(text[length - 2]) && Character.isLowSurrogate(text[length - 1])) {
        return text.substring(length - 2)
    }
    return text.takeLast(1)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseSheetContent(
    amountTextFieldValue: TextFieldValue,
    onAmountChange: (TextFieldValue) -> Unit,
    receiverName: String,
    onReceiverChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    dynamicCategories: List<String>,
    selectedEmoji: String,
    onEmojiChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    onCustomCategoryClick: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (amount: Long, receiver: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val emojiFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                            SquircleShape(CornerLarge, CornerSmoothingDefault)
                        )
                        .background(
                            color = SurfaceSecondary,
                            shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "₹",
                            style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                            color = ContentPrimary
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        val displayAmountText = remember(amountTextFieldValue.text) {
                            if (amountTextFieldValue.text.isEmpty()) {
                                "0"
                            } else {
                                try {
                                    val parsed = amountTextFieldValue.text.toLong()
                                    NumberFormat.getNumberInstance(Locale("en", "IN")).format(parsed)
                                } catch (e: Exception) {
                                    amountTextFieldValue.text
                                }
                            }
                        }

                        val textStyle = JasnifyTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = ContentPrimary,
                            textAlign = TextAlign.Start
                        )

                        val textMeasurer = rememberTextMeasurer()
                        val textLayoutResult = textMeasurer.measure(
                            text = displayAmountText,
                            style = textStyle
                        )
                        val textWidthDp = with(LocalDensity.current) { textLayoutResult.size.width.toDp() }

                        BasicTextField(
                            value = amountTextFieldValue,
                            onValueChange = { newValue ->
                                if (newValue.text.all { it.isDigit() } && newValue.text.length <= 9) {
                                    onAmountChange(newValue)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = textStyle,
                            cursorBrush = SolidColor(ContentPrimary),
                            singleLine = true,
                            visualTransformation = ThousandsSeparatorVisualTransformation(),
                            modifier = Modifier
                                .width(textWidthDp + 6.dp),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (amountTextFieldValue.text.isEmpty()) {
                                        Text(
                                            text = "0",
                                            style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                                            color = ContentPrimary.copy(alpha = 0.5f) // Set as hint
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                Text(
                    text = "The amount will be subtracted from the total budget.",
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentTertiary,
                    textAlign = TextAlign.Center
                )
            }

            DashedDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), dashLength = 12f, gapLength = 6f)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_coin_hand),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = ContentPrimary
                    )
                    Text(
                        text = "Paid to",
                        style = JasnifyTheme.typography.headingLarge,
                        fontWeight = FontWeight.Medium,
                        color = ContentPrimary
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    PrimaryInput(
                        value = receiverName,
                        onValueChange = onReceiverChange,
                        modifier = Modifier.weight(1f),
                        placeholder = "Enter Receiver's Name",
                        shape = SquircleShape(CornerLarge, CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerSmoothingDefault)
                    )

                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .fillMaxHeight()
                            .clip(shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerLarge, CornerSmoothingDefault))
                            .background(
                                SurfaceSecondary,
                                shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerLarge, CornerSmoothingDefault)
                            )
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                                shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerLarge, CornerSmoothingDefault)
                            )
                            .clickable {
                                emojiFocusRequester.requestFocus()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedEmoji.isEmpty()) {
                            Icon(
                                painter = painterResource(R.drawable.ic_emoji_plus),
                                contentDescription = "Add Contact or Emoji",
                                modifier = Modifier.size(24.dp),
                                tint = ContentSecondary
                            )
                        } else {
                            Text(
                                text = selectedEmoji,
                                fontSize = 24.sp
                            )
                        }

                        BasicTextField(
                            value = "",
                            onValueChange = { newValue ->
                                if (newValue.isNotEmpty()) {
                                    val emoji = getEmojiFromString(newValue)
                                    if (emoji != null) {
                                        onEmojiChange(emoji)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(1.dp)
                                .focusRequester(emojiFocusRequester),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            textStyle = TextStyle(color = Color.Transparent),
                            cursorBrush = SolidColor(Color.Transparent)
                        )
                    }
                }
            }

            DashedDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), dashLength = 12f, gapLength = 6f)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_category),
                            contentDescription = "Category",
                            modifier = Modifier.size(24.dp),
                            tint = ContentPrimary
                        )
                        Text(
                            text = "Select Category",
                            style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                            color = ContentPrimary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .clickable { onCustomCategoryClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_plus),
                            contentDescription = "Add Custom",
                            tint = ContentBrandDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Custom",
                            style = JasnifyTheme.typography.labelXLarge,
                            color = ContentBrandDark
                        )
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dynamicCategories.forEach { category ->
                        val isSelected = selectedCategory == category

                        FilterChip(
                            label = category,
                            isSelected = isSelected,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small,
                            hasStroke = true,
                            trailingIcon = if (isSelected) Icons.Default.Close else null,
                            onTrailingIconClick = {
                                if (isSelected) onCategorySelect("")
                            },
                            onClick = {
                                onCategorySelect(category)
                            }
                        )
                    }
                }
            }

            DashedDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), dashLength = 12f, gapLength = 6f)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_phone),
                        contentDescription = "Phone icon",
                        modifier = Modifier.size(24.dp),
                        tint = ContentPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Phone Number",
                            style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                            color = ContentPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(optional)",
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentSecondary
                        )
                    }
                }

                PrimaryInput(
                    value = phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    placeholder = "Enter receiver's phone number",
                    keyboardType = KeyboardType.Phone
                )
            }

            DashedDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), dashLength = 12f, gapLength = 6f)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notes),
                        contentDescription = "Note icon",
                        modifier = Modifier.size(24.dp),
                        tint = ContentPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Note",
                            style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                            color = ContentPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(optional)",
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentSecondary
                        )
                    }
                }

                PrimaryInput(
                    value = note,
                    onValueChange = onNoteChange,
                    placeholder = "Write something to remember...",
                    keyboardType = KeyboardType.Text,
                    singleLine = false,
                    modifier = Modifier.heightIn(min = 56.dp, max = 112.dp)
                )
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfacePrimary)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextButton(
                onClick = {
                    val finalAmount = amountTextFieldValue.text.toLongOrNull() ?: 0L
                    onSave(finalAmount, receiverName, selectedCategory)
                },
                text = "Save Details",
                size = ButtonSize.Medium,
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun AddCustomCategorySheetContent(
    onDismiss: () -> Unit,
    onAddCategory: (String) -> Unit,
    initialCategoryName: String = ""
) {
    var categoryInput by remember(initialCategoryName) {
        mutableStateOf(
            TextFieldValue(
                text = initialCategoryName,
                selection = TextRange(initialCategoryName.length)
            )
        )
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(focusRequester) {
        delay(50)
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(161.dp)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PrimaryInput(
            value = categoryInput.text,
            onValueChange = { categoryInput = categoryInput.copy(text = it) },
            placeholder = "Enter a category of your choice",
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .focusRequester(focusRequester)
        )

        DashedDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
            dashLength = 12f,
            gapLength = 6f
        )

        CustomTextButton(
            onClick = {
                onAddCategory(categoryInput.text.trim())
            },
            text = "Save",
            size = ButtonSize.Medium,
            type = ButtonType.Primary,
            shapeStyle = ButtonShapeStyle.Square,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, name = "Add Expense - Sheet Content Preview")
@Composable
fun AddExpenseSheetContentPreview() {
    val categories = remember {
        listOf(
            "Venue", "Catering", "Gifts", "Staff & Crew",
            "Costumes", "Vendors", "Transportation",
            "Entertainment", "Equipment Rentals"
        )
    }

    var selectedCategory by remember { mutableStateOf("") }

    AddExpenseSheetContent(
        amountTextFieldValue = TextFieldValue("45000"),
        onAmountChange = {},
        receiverName = "GenX Entertainment",
        onReceiverChange = {},
        selectedCategory = selectedCategory,
        onCategorySelect = { selectedCategory = it },
        dynamicCategories = categories,
        selectedEmoji = "💍",
        onEmojiChange = {},
        phoneNumber = "",
        onPhoneNumberChange = {},
        note = "",
        onNoteChange = {},
        onCustomCategoryClick = {},
        onDismiss = {},
        onSave = { _, _, _ -> }
    )
}