package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge

data class BottomSheetActionItem(
    val text: String,
    val icon: Painter,
    val onClick: () -> Unit
)

@Composable
fun MenuBottomSheet(
    items: List<BottomSheetActionItem>,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SurfaceSecondary,
                shape = SquircleShape(CornerExtraLarge, CornerExtraLarge)
            )
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag Handle Pill at the top
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(4.dp)
                .background(
                    color = ContentTertiary,
                    shape = SquircleShape(100)
                ).padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))

        // Stacked Action Menu Item Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth()
                .padding(12.dp)
        ) {
            val totalItems = items.size
            items.forEachIndexed { index, item ->
                // Calculate custom rounded corners on only two sides (first and last)
                val roundedShape = when {
                    totalItems == 1 -> RoundedCornerShape(CornerLarge)  // Only have one button
                    index == 0 -> RoundedCornerShape(CornerLarge, CornerLarge, CornerExtraSmall, CornerExtraSmall)
                    index == totalItems - 1 -> RoundedCornerShape(CornerExtraSmall, CornerExtraSmall, CornerLarge, CornerLarge)
                    else -> RoundedCornerShape(CornerExtraSmall) // Middle buttons
                }

                CustomTextButton(
                    onClick = item.onClick,
                    text = item.text,
                    leadingIcon = item.icon,
                    type = ButtonType.Tertiary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = SurfacePrimary, shape = roundedShape)
                        .clip(roundedShape)
                )
            }
        }

        Box(
            modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 12.dp)
        ){
            CustomTextButton(
                onClick = onCancelClick,
                text = "Cancel",
                type = ButtonType.Tertiary,
                contentColor = ContentSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = SurfacePrimary, shape = SquircleShape(CornerLarge))
                    .clip(SquircleShape(CornerLarge))
            )
        }
    }
}



// ---  Previews ---

@Preview(showBackground = true)
@Composable
private fun VenueMenuBottomSheetPreview() {
    val items = listOf(
        BottomSheetActionItem("Change Location", painterResource(R.drawable.ic_coin_hand), {}),
        BottomSheetActionItem("Manage Room Access", painterResource(R.drawable.ic_ai), {})
    )

    MenuBottomSheet(
        items = items,
        onCancelClick = {}
    )
}
