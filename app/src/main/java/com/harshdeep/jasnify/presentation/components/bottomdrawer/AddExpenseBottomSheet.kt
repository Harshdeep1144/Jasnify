package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
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
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange

/**
 * A custom visual transformation that formats raw numeric input into standard Indian style
 * grouping (e.g., 68000 -> 68,000) while keeping offset calculations correct for the keyboard cursor.
 */
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
    onSave: (amount: Long, receiver: String, category: String, emoji: String) -> Unit,
    categories: List<String>,
    onAddCategory: (String) -> Unit,
    initialAmount: String = "",
    initialReceiver: String = "",
    initialCategory: String = "",
    initialEmoji: String = ""
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Set up amount as TextFieldValue with selection pointing to the end of the initial amount
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

    var dynamicCategories by remember(categories) { mutableStateOf(categories) }

    // Managing the sub-sheet's independent state internally
    var showCustomCategorySheet by remember { mutableStateOf(false) }
    val customCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val headingTitle = if (initialReceiver.isNotEmpty()) "Edit expense" else "Add an expense"

    CustomBottomSheet(
        heading = headingTitle,
        sheetState = sheetState,
        onDismiss = onDismiss,
        sheetHeight = 543.dp,
        sheetGesturesEnabled = true
    ) {
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
            onCustomCategoryClick = {
                showCustomCategorySheet = true
            },
            onDismiss = onDismiss,
            onSave = { amt, rec, cat ->
                if (amountTextFieldValue.text.isBlank() || receiverName.isBlank() || selectedCategory.isBlank()) {
                    Toast.makeText(context, "Please enter Amount, Paid to, and select a Category!", Toast.LENGTH_SHORT).show()
                } else {
                    onSave(amt, rec, cat, selectedEmoji)
                }
            }
        )
    }

    if (showCustomCategorySheet) {
        AddCustomCategoryBottomSheet(
            sheetState = customCategorySheetState,
            onDismiss = {
                coroutineScope.launch {
                    customCategorySheetState.hide()
                }.invokeOnCompletion {
                    if (!customCategorySheetState.isVisible) {
                        showCustomCategorySheet = false
                    }
                }
            },
            onAddCategory = { newCategory ->
                if (newCategory.isBlank()) {
                    Toast.makeText(context, "Please enter a category name first!", Toast.LENGTH_SHORT).show()
                } else {
                    onAddCategory(newCategory)
                    dynamicCategories = dynamicCategories + newCategory
                    selectedCategory = newCategory
                    coroutineScope.launch {
                        customCategorySheetState.hide()
                    }.invokeOnCompletion {
                        if (!customCategorySheetState.isVisible) {
                            showCustomCategorySheet = false
                        }
                    }
                }
            }
        )
    }
}

/**
 * Reusable Custom Category Bottom Sheet configured to support both Adding & Renaming categories.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomCategoryBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddCategory: (String) -> Unit,
    initialCategoryName: String = "",
    heading: String = "Add custom category"
) {
    CustomBottomSheet(
        heading = heading,
        sheetState = sheetState,
        onDismiss = onDismiss,
        sheetHeight = 161.dp,
        sheetGesturesEnabled = true
    ) {
        AddCustomCategorySheetContent(
            onDismiss = onDismiss,
            onAddCategory = onAddCategory,
            initialCategoryName = initialCategoryName
        )
    }
}

// ----------- Helper Functions and Content of the above bottom sheets ---------------------

fun getEmojiFromString(text: String): String? {
    if (text.isEmpty()) return null
    val length = text.length
    if (length >= 2 && Character.isHighSurrogate(text[length - 2]) && Character.isLowSurrogate(text[length - 1])) {
        return text.substring(length - 2)
    }
    return text.takeLast(1)
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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
                                if (amountTextFieldValue.text.isEmpty()) {
                                    Text(
                                        text = "0",
                                        style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                                        color = ContentPrimary
                                    )
                                } else {
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
                    OutlinedTextField(
                        value = receiverName,
                        onValueChange = onReceiverChange,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                SurfaceSecondary,
                                shape = SquircleShape(CornerLarge, CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerSmoothingDefault)
                            )
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                                shape = SquircleShape(CornerLarge, CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerSmoothingDefault)
                            ),
                        shape = SquircleShape(CornerLarge, CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerSmoothingDefault),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Enter Receiver's Name",
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentSecondary
                            )
                        },
                        textStyle = JasnifyTheme.typography.labelXLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent
                        ),
                        trailingIcon = {
                            if (receiverName.isNotEmpty()) {
                                IconButton(onClick = { onReceiverChange("") }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_circle_cross),
                                        contentDescription = "Clear receiver",
                                        tint = ContentPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    )

                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .fillMaxHeight()
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
                .padding(12.dp, 12.dp, 12.dp, 0.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
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

            CustomTextButton(
                onClick = onDismiss,
                text = "Cancel",
                size = ButtonSize.Medium,
                type = ButtonType.Tertiary,
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
    // Utilize TextFieldValue state wrapper to manually enforce end-of-word selection ranges
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
        delay(50) // Small delay ensures focus and soft-keyboard interactions bind flawlessly
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(161.dp)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Styled directly using OutlinedTextField to safely inject robust TextFieldValue mappings
        OutlinedTextField(
            value = categoryInput,
            onValueChange = { categoryInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .focusRequester(focusRequester)
                .background(
                    SurfaceSecondary,
                    shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                )
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                    shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                ),
            shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Enter a category of your choice",
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentSecondary
                )
            },
            textStyle = JasnifyTheme.typography.labelXLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent
            )
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
        onCustomCategoryClick = {},
        onDismiss = {},
        onSave = { _, _, _ -> }
    )
}