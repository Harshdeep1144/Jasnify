package com.harshdeep.jasnify.presentation.components.chip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceAccent
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun FamousCityChip(
    modifier: Modifier = Modifier,
    cityName: String,
    cityImage: Painter,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    // Dynamic styling based on selection state
    val containerColor = if (isSelected) SurfaceBrandSecondary else SurfaceSecondary

    val borderColor = if (isSelected) {
        ContentBrand
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
            .background(containerColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
            )
            .clickable(
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = cityImage,
                contentDescription = "$cityName Image",
                modifier = Modifier
                    .width(80.dp)
                    .height(60.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cityName,
                style = JasnifyTheme.typography.labelLarge,
                color = ContentPrimary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FamousCityChipPreview() {
    // State management for preview purposes
    var selectedCityId by remember { mutableStateOf(1) }

    JasnifyTheme {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FamousCityChip(
                    cityName = "Hajipur",
                    cityImage = painterResource(id = R.drawable.ic_city_del),
                    isSelected = selectedCityId == 1,
                    onClick = { selectedCityId = 1 }
                )

                FamousCityChip(
                    cityName = "Patna",
                    cityImage = painterResource(id = R.drawable.ic_city_del),
                    isSelected = selectedCityId == 2,
                    onClick = { selectedCityId = 2 }
                )
            }
        }
    }
}