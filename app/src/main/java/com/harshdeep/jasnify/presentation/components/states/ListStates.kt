package com.harshdeep.jasnify.presentation.components.states

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme

/**
 * A standard empty state display intended for use within a LazyColumn/LazyRow.
 */
@Composable
fun LazyItemScope.EmptyState(
    message: String = "No results found",
    iconRes: Int = R.drawable.ic_receipt
) {
    Box(
        modifier = Modifier
            .fillParentMaxHeight(0.7f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = message,
                tint = ContentTertiary,
                modifier = Modifier.size(84.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                color = ContentTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * A specialized empty state for "Saved" lists.
 */
@Composable
fun LazyItemScope.EmptySavedState() {
    EmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
}

/**
 * A suggestion item shown during active search.
 */
@Composable
fun SearchSuggestionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_location_marker),
            contentDescription = null,
            tint = ContentSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentPrimary
            )
            Text(
                text = subtitle,
                style = JasnifyTheme.typography.labelMedium,
                color = ContentSecondary
            )
        }
    }
}
