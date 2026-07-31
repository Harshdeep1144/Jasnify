package com.harshdeep.jasnify.presentation.components.chip

import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun VendorTypeChip(
    label: String,
    icon: Int,
    modifier: Modifier = Modifier,
    subLabel: String? = null,
    isLarge: Boolean = true,
    onClick: () -> Unit = {}
) {
    val shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)

    if (isLarge) {
        Column(
            modifier = modifier
                .clip(shape)
                .widthIn(min = 120.dp)
                .background(SurfaceSecondary)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.16f), shape)
                .clickable { onClick() }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.height(60.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = JasnifyTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.basicMarquee(
                    iterations = Int.MAX_VALUE,
                )
            )
        }
    } else {
        Row(
            modifier = modifier
                .clip(shape)
                .widthIn(min = 162.dp)
                .background(SurfaceSecondary)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.16f), shape)
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = JasnifyTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = ContentPrimary
                )
                if (subLabel != null) {
                    Text(
                        text = subLabel,
                        style = JasnifyTheme.typography.labelSmall,
                        color = ContentBrandDark,
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun VendorTypeChipLargePreview() {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        VendorTypeChip(
            label = "Gifts",
            icon = R.drawable.ill_vendor_food,
            isLarge = true
        )
        VendorTypeChip(
            label = "Gifts",
            subLabel = "Explore Now",
            icon = R.drawable.ill_vendor_gifts,
            isLarge = false
        )
    }
}

