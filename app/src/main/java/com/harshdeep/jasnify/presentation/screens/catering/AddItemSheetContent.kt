package com.harshdeep.jasnify.presentation.screens.catering

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.components.chip.FoodChip
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

@Composable
fun AddItemSheetContent(
    itemName: String,
    onItemNameChange: (String) -> Unit,
    cuisine: String,
    onCuisineClick: () -> Unit,
    type: String,
    onTypeClick: () -> Unit,
    dietary: Dietary,
    onDietaryChange: (Dietary) -> Unit,
    onSubmitClick: () -> Unit,
    isEditMode: Boolean = false
) {
    val isNameEntered = itemName.isNotBlank()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val inputSquircleShape = remember { SquircleShape(CornerLarge) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 300))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(scrollState)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ITEM",
                    style = JasnifyTheme.typography.labelSmall,
                    color = ContentSecondary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                PrimaryInput(
                    value = itemName,
                    onValueChange = onItemNameChange,
                    placeholder = "Type or Search a dish",
                    trailingIcon = painterResource(id = R.drawable.ic_ai),
                    textStyle = JasnifyTheme.typography.labelXLarge.copy(color = ContentPrimary)
                )
            }

            AnimatedVisibility(
                visible = isNameEntered,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 300))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "CUISINE",
                            style = JasnifyTheme.typography.labelSmall,
                            color = ContentSecondary,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            PrimaryInput(
                                value = "",
                                onValueChange = {},
                                placeholder = cuisine,
                                trailingIcon = painterResource(R.drawable.ic_edit),
                                trailingIconEnabled = true,
                                textStyle = JasnifyTheme.typography.labelXLarge.copy(color = ContentPrimary)
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(inputSquircleShape)
                                    .clickable {
                                        focusManager.clearFocus()
                                        onCuisineClick()
                                    }
                            )
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "TYPE",
                            style = JasnifyTheme.typography.labelSmall,
                            color = ContentSecondary,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            PrimaryInput(
                                value = "",
                                onValueChange = {},
                                placeholder = type,
                                trailingIcon = painterResource(R.drawable.ic_edit),
                                textStyle = JasnifyTheme.typography.labelXLarge.copy(color = ContentPrimary),
                                trailingIconEnabled = true
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(inputSquircleShape)
                                    .clickable {
                                        focusManager.clearFocus()
                                        onTypeClick()
                                    }
                            )
                        }
                    }
                }
            }

            DashedDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                dashLength = 12f,
                gapLength = 6f
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FoodChip(
                    foodType = Dietary.Veg,
                    isSelected = dietary == Dietary.Veg,
                    shapeStyle = ChipShapeStyle.Square,
                    size = ChipSize.Large,
                    onClick = {
                        focusManager.clearFocus()
                        onDietaryChange(Dietary.Veg)
                    }
                )
                FoodChip(
                    foodType = Dietary.NonVeg,
                    isSelected = dietary == Dietary.NonVeg,
                    shapeStyle = ChipShapeStyle.Square,
                    size = ChipSize.Large,
                    onClick = {
                        focusManager.clearFocus()
                        onDietaryChange(Dietary.NonVeg)
                    }
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            CustomTextButton(
                onClick = {
                    focusManager.clearFocus()
                    onSubmitClick()
                },
                text = if (isEditMode) "Save Changes" else "Add to Menu",
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                size = ButtonSize.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
