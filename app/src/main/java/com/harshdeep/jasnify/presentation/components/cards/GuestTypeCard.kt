package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun GuestTypeCard(
    label: String,
    guestCount: Int,
    modifier: Modifier = Modifier,
    showChecker: Boolean = true,
    isSelected: Boolean = false,
    imageUrls: List<String> = emptyList(),
    onToggle: (Boolean) -> Unit = {},
    onClick: () -> Unit = { onToggle(!isSelected) }
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showChecker) {
                CustomChecker(
                    checked = isSelected,
                    onCheckedChange = onToggle,
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentPrimary
                )
                Text(
                    text = "$guestCount Guests",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
            }

            if (imageUrls.isNotEmpty()) {
                AvatarStack(imageUrls = imageUrls)
            }
        }
    }
}

@Composable
fun AvatarStack(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    overlap: Dp = 20.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(-overlap)
    ) {
        imageUrls.take(3).forEach { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(2.dp, SurfacePrimary, CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_user_profile),
                error = painterResource(id = R.drawable.ic_user_profile)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGuestTypeCards() {
    JasnifyTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(BackgroundPrimary),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GuestTypeCard(
                label = "Close Friend",
                guestCount = 14,
                showChecker = true,
                isSelected = false
            )
            GuestTypeCard(
                label = "Close Friend",
                guestCount = 14,
                showChecker = true,
                isSelected = true
            )
            GuestTypeCard(
                label = "Close Friend",
                guestCount = 14,
                showChecker = false
            )

            val images = listOf(
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e"
            )

            GuestTypeCard(
                label = "Close Friend",
                guestCount = 14,
                imageUrls = images
            )
        }
    }
}
