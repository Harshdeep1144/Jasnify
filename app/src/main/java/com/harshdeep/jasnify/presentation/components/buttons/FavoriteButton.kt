package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import sv.lib.squircleshape.SquircleShape

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    IconButton(
        onClick = onFavoriteToggle,
        modifier = modifier
            .size(size)
            .background(
                color = ContentSecondary.copy(alpha = 0.5f),
                shape = SquircleShape(CornerMedium, CornerSmoothingDefault)
            ).padding(0.dp)
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
            tint = if (isFavorite) Color.Red else ContentPrimary,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}




@Preview(showBackground = true)
@Composable
fun FavoriteButtonPreview() {
    FavoriteButton(
        isFavorite = true,
        onFavoriteToggle = {}
    )
}
