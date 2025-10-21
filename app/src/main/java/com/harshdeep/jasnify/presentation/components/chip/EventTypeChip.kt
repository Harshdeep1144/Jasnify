package com.harshdeep.jasnify.presentation.components.chip

import com.harshdeep.jasnify.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.widget.TextViewCompat
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceAccent
import sv.lib.squircleshape.SquircleShape


@Composable
fun EventTypeChip(
    modifier: Modifier = Modifier,
    iconPainter: Painter,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) {
        SurfaceAccent
    } else {
        BackgroundSecondary
    }

    val borderColor = if (isSelected) {
        ContentBrand
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        ContentPrimary
    } else {
        ContentSecondary
    }

    // Define the size of the checkmark badge
    val badgeSize = 24.dp
    val badgeOffset = 5.dp

    Box(
        modifier = modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Card(
            modifier = Modifier
                .widthIn(min = 100.dp)
                .height(128.dp)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                ),
            shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
            colors = CardDefaults.cardColors(containerColor = containerColor),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .wrapContentWidth()
                    .height(IntrinsicSize.Max), // Ensures the column fills the card height
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = iconPainter,
                        contentDescription = "$label Icon",
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = label,
                    color = contentColor,
                    style = JasnifyTheme.typography.labelLarge,
                    maxLines = 1,
                )
            }
        }

        // Selected Checkmark (Badge)
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = badgeOffset, y = -badgeOffset) // Apply offset for a better visual
                    .size(badgeSize)
                    .clip(shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
                    .background(ContentBrand)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = ContentInvPrimary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EventTypeChipPreview() {
    // Using a simple Android resource for the preview icon
    val WeddingIconPlaceholder = painterResource(id = R.drawable.carousel_img4)

    var isSelectedWedding by remember { mutableStateOf(false) }
    var isSelectedBirthday by remember { mutableStateOf(true) }
    var isSelectedLong by remember { mutableStateOf(false) }


    JasnifyTheme {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Default State", style = MaterialTheme.typography.titleMedium)
            EventTypeChip(
                iconPainter = WeddingIconPlaceholder,
                label = "Short Label",
                isSelected = isSelectedWedding,
                onClick = { isSelectedWedding = !isSelectedWedding }
            )

            Text("Selected State", style = MaterialTheme.typography.titleMedium)
            EventTypeChip(
                iconPainter = WeddingIconPlaceholder,
                label = "Birthday Party Test",
                isSelected = isSelectedBirthday,
                onClick = { isSelectedBirthday = !isSelectedBirthday }
            )

            Text("Long Label State", style = MaterialTheme.typography.titleMedium)
            EventTypeChip(
                iconPainter = WeddingIconPlaceholder,
                label = "Long Label With Auto-Expansion Test",
                isSelected = isSelectedLong,
                onClick = { isSelectedLong = !isSelectedLong }
            )
        }
    }
}
