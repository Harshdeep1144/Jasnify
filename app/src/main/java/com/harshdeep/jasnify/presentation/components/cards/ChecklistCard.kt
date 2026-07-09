package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.others.ChecklistItem
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker

data class Checklist(
    val id: String,
    val title: String,
    val dateTime: String,
    val items: List<ChecklistItem>,
    val bgColor: Color,
    val isPinned: Boolean = false
)

@Composable
fun ChecklistCard(
    checklist: Checklist,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault),
        colors = CardDefaults.cardColors(
            containerColor = checklist.bgColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(0.08f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = checklist.dateTime,
                    style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = ContentSecondary
                )
                Spacer(Modifier.width(4.dp))

                if (checklist.isPinned) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pin),
                        contentDescription = "Pinned",
                        modifier = Modifier.size(18.dp),
                        tint = ContentSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val displayTitle = checklist.title.ifBlank { "Title" }
            val titleColor = if (checklist.title.isBlank()) ContentSecondary else ContentPrimary

            Text(
                text = displayTitle,
                style = JasnifyTheme.typography.headingXLarge,
                fontWeight = FontWeight.Medium,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            checklist.items.sortedBy { it.isChecked }.take(3).forEach { item ->
                ChecklistCardItem(item)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun ChecklistCardItem(item: ChecklistItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        CustomChecker(
            checked = item.isChecked,
            enabled = false,
            onCheckedChange = null,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = item.text,
            style = JasnifyTheme.typography.headingSmall.merge(
                TextStyle(
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                )
            ),
            color = if (item.isChecked) ContentSecondary else ContentPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ----------------------------------------  Preview ---------------------------------------

@Preview(showBackground = true)
@Composable
private fun ChecklistCardPreview() {
    val sampleChecklist = Checklist(
        id = "1",
        title = "Shopping for Bride",
        dateTime = "Today, 09:30 PM",
        items = listOf(
            ChecklistItem(text = "Purchase Outfits", isChecked = true),
            ChecklistItem(text = "Make Appointment for Makeup"),
            ChecklistItem(text = "Book Jewellery"),
            ChecklistItem(text = "Arrange Transportation"),
            ChecklistItem(text = "Finalize Guest List"),
            ChecklistItem(text = "Send Invitations")
        ),
        bgColor = SoftMint,
        isPinned = true
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Grid View", style = JasnifyTheme.typography.labelSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ChecklistCard(
                checklist = sampleChecklist,
                modifier = Modifier.weight(1f)
            )
            // Empty title preview test
            ChecklistCard(
                checklist = sampleChecklist.copy(title = "", bgColor = PaleLavender, isPinned = false),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("List View", style = JasnifyTheme.typography.labelSmall)
        ChecklistCard(
            checklist = sampleChecklist.copy(title = "Saturday To-Dos", bgColor = SoftPeach, isPinned = false),
        )
    }
}