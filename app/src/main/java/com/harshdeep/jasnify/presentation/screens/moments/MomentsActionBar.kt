package com.harshdeep.jasnify.presentation.screens.moments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun MomentsActionBar(
    onShareClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFF1E1E1E),
    contentColor: Color = Color.White
) {
    Row(
        modifier = modifier
            .height(80.dp)
            .background(containerColor, CircleShape)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionBarItem(
            iconRes = R.drawable.ic_share_card,
            label = "Share",
            onClick = onShareClick,
            contentColor = contentColor,
            modifier = Modifier.weight(1f)
        )
        ActionBarItem(
            iconRes = R.drawable.ic_heart,
            label = "Favorites",
            onClick = onFavoriteClick,
            contentColor = contentColor,
            modifier = Modifier.weight(1f)
        )
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = JasnifyTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = contentColor.copy(alpha = 0.8f)
        )
    }
}
