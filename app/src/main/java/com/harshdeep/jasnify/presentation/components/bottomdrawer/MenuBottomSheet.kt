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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.contextmenu.modifier.filterTextContextMenuComponents
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.JasnifyTheme

data class MenuSheetActionItem(
    val text: String,
    val icon: Painter,
    val containerColor: Color = SurfacePrimary,
    val contentColor: Color = ContentPrimary,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBottomSheet(
    items: List<MenuSheetActionItem>,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onCancelClick,
        sheetState = sheetState,
        containerColor = SurfaceSecondary,
        shape = SquircleShape(CornerExtraLarge, CornerExtraLarge),
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(56.dp)
                    .height(4.dp)
                    .background(ContentTertiary, shape = RoundedCornerShape(100))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stacked Action Menu Item Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                val totalItems = items.size
                items.forEachIndexed { index, item ->
                    val roundedShape = when {
                        totalItems == 1 -> RoundedCornerShape(CornerLarge)
                        index == 0 -> RoundedCornerShape(CornerLarge, CornerLarge, CornerExtraSmall, CornerExtraSmall)
                        index == totalItems - 1 -> RoundedCornerShape(CornerExtraSmall, CornerExtraSmall, CornerLarge, CornerLarge)
                        else -> RoundedCornerShape(CornerExtraSmall)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(color = item.containerColor, shape = roundedShape)
                            .clip(shape = roundedShape)
                            .clickable { item.onClick() }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ){
                        Icon(
                            painter = item.icon,
                            contentDescription = null
                        )
                        Spacer(modifier.width(8.dp))
                        Text(
                            text = item.text,
                            style = JasnifyTheme.typography.labelXLarge,
                            color = item.contentColor
                        )
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
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MenuBottomSheetPreview() {
    val mockItems = listOf(
        MenuSheetActionItem(
            text = "Add an item",
            icon = painterResource(id = R.drawable.ic_plus),
            onClick = {  }
        ),
        MenuSheetActionItem(
            text = "Manage Room Access",
            icon = painterResource(id = R.drawable.ic_user_default),
            onClick = {  }
        )
    )

    MenuBottomSheet(
        items = mockItems,
        onCancelClick = {  }
    )
}