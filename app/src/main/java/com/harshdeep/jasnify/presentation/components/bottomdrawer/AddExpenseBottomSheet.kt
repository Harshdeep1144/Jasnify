package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
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
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.text.NumberFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

private val IndianLocale = Locale("en", "IN")
private val AmountContainerShape = SquircleShape(CornerLarge, CornerSmoothingDefault)
private val ReceiverInputShape = SquircleShape(CornerLarge, CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerSmoothingDefault)
private val EmojiButtonShape = SquircleShape(CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerLarge, CornerSmoothingDefault)

class ThousandsSeparatorVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formatted = try {
            val parsed = originalText.toLong()
            NumberFormat.getNumberInstance(IndianLocale).format(parsed)
        } catch (_: Exception) {
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
    onDismiss: () -> Unit,
    onSave: (amount: Long, receiver: String, category: String, emoji: String, phoneNumber: String, note: String) -> Unit,
    categories: List<String>,
    onAddCategory: (String) -> Unit,
    initialAmount: String = "",
    initialReceiver: String = "",
    initialCategory: String = "",
    initialEmoji: String = "",
    initialPhoneNumber: String = "",
    initialNote: String = "",
    onProgress: ((Float) -> Unit)? = null
) {
    var toastData by remember { mutableStateOf(ToastData()) }
    var activeToastData by remember { mutableStateOf<ToastData?>(null) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            if (toastData.type == ToastType.ERROR) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (toastData.message?.contains("Please", ignoreCase = true) == true ||
                    toastData.message?.contains("enter", ignoreCase = true) == true ||
                    toastData.message?.contains("select", ignoreCase = true) == true
                ) {
                    delay(80.milliseconds)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            activeToastData = toastData
            delay(2000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    val amountTextFieldValueState = remember(initialAmount) {
        mutableStateOf(
            TextFieldValue(
                text = initialAmount,
                selection = TextRange(initialAmount.length)
            )
        )
    }
    var amountTextFieldValue by amountTextFieldValueState

    var receiverName by remember(initialReceiver) { mutableStateOf(initialReceiver) }
    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory) }
    var selectedEmoji by remember(initialEmoji) { mutableStateOf(initialEmoji) }
    var phoneNumber by remember(initialPhoneNumber) { mutableStateOf(initialPhoneNumber) }
    var note by remember(initialNote) { mutableStateOf(initialNote) }

    var dynamicCategories by remember(categories) { mutableStateOf(categories) }
    var showCustomCategoryUI by remember { mutableStateOf(false) }

    val headingTitle = remember(showCustomCategoryUI, initialReceiver) {
        if (showCustomCategoryUI) "Add custom category" else if (initialReceiver.isNotEmpty()) "Edit expense" else "Add an expense"
    }

    val visualTransformation = remember { ThousandsSeparatorVisualTransformation() }

    var currentSheetHeight by remember { mutableStateOf<androidx.compose.ui.unit.Dp?>(if (showCustomCategoryUI) 161.dp else 560.dp) }

    LaunchedEffect(showCustomCategoryUI) {
        currentSheetHeight = if (showCustomCategoryUI) 161.dp else 560.dp
    }

    val animatedSheetHeight by animateDpAsState(
        targetValue = currentSheetHeight ?: 1000.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "SheetHeightAnimation"
    )

    CustomBottomSheet(
        heading = headingTitle,
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = animatedSheetHeight,
        showDragHandle = true,
        sheetGesturesEnabled = false,
        showCloseButton = true,
        hasToast = toastData.message != null,
        toast = {
            AnimatedVisibility(
                visible = toastData.message != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                activeToastData?.let { data ->
                    CustomToast(
                        message = data.message ?: "",
                        type = data.type
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (showCustomCategoryUI) {
                AddCustomCategorySheetContent(
                    onDismiss = { showCustomCategoryUI = false },
                    onAddCategory = { newCategory ->
                        if (newCategory.isBlank()) {
                            toastData = ToastData("Please enter an expense category!", ToastType.ERROR)
                        } else {
                            onAddCategory(newCategory)
                            dynamicCategories = dynamicCategories + newCategory
                            selectedCategory = newCategory
                            showCustomCategoryUI = false
                        }
                    }
                )
            } else {
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
                    onCustomCategoryClick = { showCustomCategoryUI = true },
                    onDismiss = onDismiss,
                    onSave = { amt, rec, cat ->
                        if (amountTextFieldValue.text.isBlank()) {
                            toastData = ToastData("Please enter the expense amount!", ToastType.ERROR)
                        } else if (receiverName.isBlank()) {
                            toastData = ToastData("Please enter receiver name!", ToastType.ERROR)
                        } else if (selectedCategory.isBlank()) {
                            toastData = ToastData("Please select an expense category!", ToastType.ERROR)
                        } else {
                            val finalEmoji = selectedEmoji.ifBlank { "💸" }
                            onSave(amt, rec, cat, finalEmoji, phoneNumber, note)
                        }
                    },
                    visualTransformation = visualTransformation,
                    onExpandRequest = { currentSheetHeight = null }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomCategoryBottomSheet(
    onDismiss: () -> Unit,
    onAddCategory: (String) -> Unit,
    initialCategoryName: String = "",
    heading: String = "Add custom category",
    onProgress: ((Float) -> Unit)? = null
) {
    CustomBottomSheet(
        heading = heading,
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = 161.dp,
        showDragHandle = true,
        showCloseButton = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                AddCustomCategorySheetContent(
                    onDismiss = onDismiss,
                    onAddCategory = onAddCategory,
                    initialCategoryName = initialCategoryName
                )
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
    visualTransformation: VisualTransformation,
    modifier: Modifier = Modifier,
    onExpandRequest: () -> Unit = {}
) {
    val emojiFocusRequester = remember { FocusRequester() }
    val amountFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        kotlinx.coroutines.yield()
        amountFocusRequester.requestFocus()
        keyboardController?.show()
    }

    val textStyle = JasnifyTheme.typography.displayLarge.copy(
        fontWeight = FontWeight.Medium,
        color = ContentPrimary,
        textAlign = TextAlign.Start
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .nestedScroll(remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                            if (available.y < 0) {
                                onExpandRequest()
                            }
                            return Offset.Zero
                        }
                    }
                })
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
                            AmountContainerShape
                        )
                        .background(
                            color = SurfaceSecondary,
                            shape = AmountContainerShape
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
                                    NumberFormat.getNumberInstance(IndianLocale).format(parsed)
                                } catch (_: Exception) {
                                    amountTextFieldValue.text
                                }
                            }
                        }

                        val textWidthDp = remember(displayAmountText, textStyle, density) {
                            val textLayoutResult = textMeasurer.measure(
                                text = displayAmountText,
                                style = textStyle
                            )
                            with(density) { textLayoutResult.size.width.toDp() }
                        }

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
                            visualTransformation = visualTransformation,
                            modifier = Modifier
                                .width(textWidthDp + 6.dp)
                                .focusRequester(amountFocusRequester),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (amountTextFieldValue.text.isEmpty()) {
                                        Text(
                                            text = "0",
                                            style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                                            color = ContentPrimary.copy(alpha = 0.5f)
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
                        shape = ReceiverInputShape
                    )

                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .fillMaxHeight()
                            .clip(shape = EmojiButtonShape)
                            .background(
                                SurfaceSecondary,
                                shape = EmojiButtonShape
                            )
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                                shape = EmojiButtonShape
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
        delay(50.milliseconds)
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
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
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
        onSave = { _, _, _ -> },
        visualTransformation = ThousandsSeparatorVisualTransformation()
    )
}