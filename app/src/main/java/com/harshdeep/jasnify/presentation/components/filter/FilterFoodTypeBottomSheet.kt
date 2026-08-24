package com.harshdeep.jasnify.presentation.components.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.screens.catering.getCategoryStyle
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

private val FacadFluxBold = FontFamily(Font(R.font.facadflux_bold))

@Immutable
data class FoodTypeOption(
    val name: String,
    val count: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterFoodTypeBottomSheet(
    options: List<FoodTypeOption>,
    initialSelectedOptions: Set<String>,
    onDismiss: () -> Unit,
    onApply: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    isMultiSelect: Boolean = true,
    onProgress: ((Float) -> Unit)? = null
) {
    var tempSelectedOptions by remember(initialSelectedOptions) {
        mutableStateOf(initialSelectedOptions)
    }

    CustomBottomSheet(
        heading = "Select Food Type",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = false,
        showCloseButton = true
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(
                    items = options,
                    key = { it.name },
                    contentType = { "FoodTypeOptionCard" }
                ) { option ->
                    val isSelected = option.name in tempSelectedOptions
                    FoodTypeOptionCard(
                        option = option,
                        isSelected = isSelected,
                        onClick = {
                            tempSelectedOptions = if (isMultiSelect) {
                                if (isSelected) tempSelectedOptions - option.name else tempSelectedOptions + option.name
                            } else {
                                if (isSelected) emptySet() else setOf(option.name)
                            }
                        }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                color = SurfacePrimary
            ) {
                CustomTextButton(
                    onClick = { onApply(tempSelectedOptions) },
                    text = "Apply",
                    type = ButtonType.Primary,
                    shapeStyle = ButtonShapeStyle.Square,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun FoodTypeOptionCard(
    option: FoodTypeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryStyle = remember(option.name) { getCategoryStyle(option.name) }
    val itemShape = remember { SquircleShape(CornerLargeIncrease, CornerSmoothingDefault) }
    val cardBackground = if (isSelected) SurfaceBrandSecondary else Color.Transparent
    val painter = painterResource(id = categoryStyle.illustrationRes)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(itemShape)
            .background(cardBackground)
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomChecker(
                checked = isSelected,
                onCheckedChange = { onClick() }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.name.uppercase(),
                    fontFamily = FacadFluxBold,
                    fontWeight = FontWeight.Bold,
                    color = categoryStyle.headerTextColor,
                    style = JasnifyTheme.typography.displayLarge
                )
                Text(
                    text = "${option.count} ITEMS",
                    color = categoryStyle.subtitleTextColor,
                    letterSpacing = 2.sp,
                    style = JasnifyTheme.typography.labelSmall
                )
            }

            Image(
                painter = painter,
                contentDescription = option.name,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}