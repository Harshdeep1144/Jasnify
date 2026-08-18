package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary

enum class IconPlacement {
    Left,
    Top
}

data class MenuSheetActionItem(
    val text: String,
    val icon: Painter,
    val containerColor: Color = SurfacePrimary,
    val contentColor: Color = ContentPrimary,
    val iconPlacement: IconPlacement = IconPlacement.Left,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBottomSheet(
    items: List<List<MenuSheetActionItem>>,
    onCancelClick: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    CustomBottomSheet(
        onDismiss = onCancelClick,
        onProgress = onProgress,
        sheetHeight = null,
        containerColor = SurfaceSecondary,
        showDragHandle = true,
        showCloseButton = false,
    ) {
        MenuBottomSheetContent(
            items = items,
            onCancelClick = onCancelClick
        )
    }
}

@Composable
fun MenuBottomSheetContent(
    items: List<List<MenuSheetActionItem>>,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            val totalRows = items.size
            items.forEachIndexed { rowIndex, rowItems ->
                val totalCols = rowItems.size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    rowItems.forEachIndexed { colIndex, item ->
                        val roundedShape = remember(rowIndex, totalRows, colIndex, totalCols) {
                            calculateItemShape(
                                rowIndex = rowIndex,
                                totalRows = totalRows,
                                colIndex = colIndex,
                                totalCols = totalCols
                            )
                        }

                        val isSingleCol = totalCols <= 1
                        val itemModifier = Modifier
                            .then(if (isSingleCol) Modifier.fillMaxWidth() else Modifier.weight(1f))
                            .background(color = item.containerColor, shape = roundedShape)
                            .clip(shape = roundedShape)
                            .clickable(onClick = item.onClick)
                            .padding(16.dp)

                        if (item.iconPlacement == IconPlacement.Top) {
                            Column(
                                modifier = itemModifier,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = item.icon,
                                    contentDescription = null,
                                    tint = item.contentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.text,
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = item.contentColor
                                )
                            }
                        } else {
                            Row(
                                modifier = itemModifier,
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = item.icon,
                                    contentDescription = null,
                                    tint = item.contentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.text,
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = item.contentColor
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 12.dp)
        ) {
            CustomTextButton(
                onClick = onCancelClick,
                text = "Cancel",
                type = ButtonType.Tertiary,
                shapeStyle = ButtonShapeStyle.Square,
                contentColor = ContentSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun calculateItemShape(
    rowIndex: Int,
    totalRows: Int,
    colIndex: Int,
    totalCols: Int
): RoundedCornerShape {
    val topLeft = if (rowIndex == 0 && colIndex == 0) CornerLarge else CornerExtraSmall
    val topRight = if (rowIndex == 0 && colIndex == totalCols - 1) CornerLarge else CornerExtraSmall
    val bottomLeft = if (rowIndex == totalRows - 1 && colIndex == 0) CornerLarge else CornerExtraSmall
    val bottomRight = if (rowIndex == totalRows - 1 && colIndex == totalCols - 1) CornerLarge else CornerExtraSmall

    return RoundedCornerShape(
        topStart = topLeft,
        topEnd = topRight,
        bottomStart = bottomLeft,
        bottomEnd = bottomRight
    )
}

// ----------------------------------------------- Preview ------------------------------------------------

@Preview(showBackground = true)
@Composable
fun MenuBottomSheetPreview() {
    val mockItems = listOf(
        listOf(
            MenuSheetActionItem(
                text = "List View",
                icon = painterResource(id = R.drawable.ic_list),
                iconPlacement = IconPlacement.Top,
                onClick = { }
            ),
            MenuSheetActionItem(
                text = "View Archives",
                icon = painterResource(id = R.drawable.ic_box),
                iconPlacement = IconPlacement.Top,
                onClick = { }
            )
        ),
        listOf(
            MenuSheetActionItem(
                text = "Manage Room Access",
                icon = painterResource(id = R.drawable.ic_user_default),
                iconPlacement = IconPlacement.Left,
                onClick = { }
            )
        ),
        listOf(
            MenuSheetActionItem(
                text = "Help & Feedback",
                icon = painterResource(id = R.drawable.ic_info),
                iconPlacement = IconPlacement.Left,
                onClick = { }
            )
        )
    )

    MenuBottomSheetContent(
        items = mockItems,
        onCancelClick = { }
    )
}