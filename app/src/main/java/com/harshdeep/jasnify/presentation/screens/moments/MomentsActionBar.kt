package com.harshdeep.jasnify.presentation.screens.moments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun MomentsActionBar(
    onShareClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (() -> Unit)? = null,
    onFavoriteClick: (() -> Unit)? = null,
    isFavorite: Boolean = false,
    containerColor: Color = Color.White,
    contentColor: Color = ContentPrimary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .pill360Shadow(
                ambientColor = Color.Black.copy(alpha = 0.10f),
                ambientBlur = 12.dp,
                ambientSpread = 2.dp,
                spotColor = Color.Black.copy(alpha = 0.15f),
                spotBlur = 18.dp,
                spotOffsetY = 4.dp
            )
            .background(containerColor, CircleShape)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionBarItem(
            iconRes = R.drawable.ic_share_card,
            label = "Share",
            onClick = onShareClick,
            contentColor = contentColor,
            modifier = Modifier.weight(1f)
        )
        if (onDeleteClick != null) {
            ActionBarItem(
                iconRes = R.drawable.ic_delete,
                label = "Delete",
                onClick = onDeleteClick,
                contentColor = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
        if (onFavoriteClick != null) {
            ActionBarItem(
                iconRes = if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart,
                label = if (isFavorite) "Saved" else "Favorites",
                onClick = onFavoriteClick,
                contentColor = if (isFavorite) Color(0xFFE53935) else contentColor,
                iconTint = if (isFavorite) Color.Unspecified else contentColor,
                modifier = Modifier.weight(1f)
            )
        }
        ActionBarItem(
            iconRes = R.drawable.ic_download,
            label = "Download",
            onClick = onDownloadClick,
            contentColor = contentColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionBarItem(
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
    contentColor: Color,
    modifier: Modifier = Modifier,
    iconTint: Color = contentColor
) {
    Column(
        modifier = modifier
            .noRippleClickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            style = JasnifyTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = contentColor
        )
    }
}
