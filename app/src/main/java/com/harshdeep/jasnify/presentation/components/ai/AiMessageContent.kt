package com.harshdeep.jasnify.presentation.components.ai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.local.BudgetEntity
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.domain.model.AiMessage
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.presentation.components.cards.BudgetTrackerCard
import com.harshdeep.jasnify.presentation.components.cards.ChecklistCard
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.cards.GuestCard
import com.harshdeep.jasnify.presentation.components.carousels.VendorCarousel
import com.harshdeep.jasnify.presentation.components.carousels.VenueCarousel
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AiMessageContent(
    message: AiMessage,
    isStreaming: Boolean = false,
    allVenues: List<Venue> = emptyList(),
    allVendors: List<Vendor> = emptyList(),
    guests: List<Guest> = emptyList(),
    expenses: List<ExpenseEntity> = emptyList(),
    checklists: List<Checklist> = emptyList(),
    budgetSettings: BudgetEntity? = null,
    onFeedbackClick: (Int) -> Unit = {},
    onVenueClick: (Venue) -> Unit = {},
    onVendorClick: (Vendor) -> Unit = {},
    onGuestClick: (Guest) -> Unit = {},
    onExpenseClick: (ExpenseEntity) -> Unit = {},
    onChecklistClick: (Checklist) -> Unit = {},
    onSeeAllVenues: () -> Unit = {},
    onSeeAllVendors: () -> Unit = {},
    onSeeAllGuests: () -> Unit = {},
    onSeeAllBudget: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (message.text.isNotBlank()) {
            FormattedAiText(text = message.text)
        } else if (isStreaming) {
            GeneratingIndicator()
        }

        if (message.showBudgetSummary && budgetSettings != null) {
            val totalBudget = budgetSettings.totalBudget ?: 0.0
            val spent = expenses.sumOf { it.amount }
            val remaining = (totalBudget - spent).coerceAtLeast(0.0)
            val progress = if (totalBudget > 0) (remaining / totalBudget).toFloat().coerceIn(0f, 1f) else 0f

            Spacer(modifier = Modifier.height(12.dp))
            BudgetTrackerCard(
                heading = "Budget Status",
                progress = progress,
                amountText = "₹${remaining.toLong()}",
                labelText = "left",
                onClick = onSeeAllBudget
            )
        }

        if (message.venueIds.isNotEmpty()) {
            val matchedVenues = allVenues.filter { message.venueIds.contains(it.id) }
            if (matchedVenues.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                VenueCarousel(
                    title = "Suggested Venues",
                    venues = matchedVenues,
                    onVenueClick = onVenueClick,
                    onFavoriteToggle = {},
                    onSeeAllClick = onSeeAllVenues,
                    onOfferClick = {},
                    cardSize = CompactCardSize.MEDIUM
                )
            }
        }

        if (message.vendorIds.isNotEmpty()) {
            val matchedVendors = allVendors.filter { message.vendorIds.contains(it.id) }
            if (matchedVendors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                VendorCarousel(
                    title = "Suggested Vendors",
                    vendors = matchedVendors,
                    onVendorClick = onVendorClick,
                    onFavoriteToggle = {},
                    onSeeAllClick = onSeeAllVendors,
                    onOfferClick = {},
                    cardSize = CompactCardSize.MEDIUM
                )
            }
        }

        if (message.guestIds.isNotEmpty()) {
            val matchedGuests = guests.filter { message.guestIds.contains(it.id) }.take(5)
            matchedGuests.forEach { guest ->
                Spacer(modifier = Modifier.height(8.dp))
                GuestCard(
                    name = guest.name,
                    label = guest.type,
                    isInvited = guest.invited,
                    onInviteClick = {},
                    onCardClick = { onGuestClick(guest) }
                )
            }

            if (guests.size > matchedGuests.size) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "See all guests (${guests.size}) →",
                    style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = ContentBrandDark,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onSeeAllGuests() }
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                )
            }
        }

        if (message.expenseIds.isNotEmpty()) {
            val matchedExpenses = expenses.filter { message.expenseIds.contains(it.id) }
            matchedExpenses.forEach { expense ->
                Spacer(modifier = Modifier.height(12.dp))
                ExpenseCard(
                    title = expense.title,
                    category = expense.category,
                    amount = "₹${expense.amount}",
                    emoji = expense.emoji,
                    lastUpdatedBy = expense.lastUpdatedBy,
                    lastUpdatedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expense.lastUpdatedDate)),
                    onDeleteClick = {},
                    onModifyClick = { onExpenseClick(expense) }
                )
            }
        }

        if (message.checklistIds.isNotEmpty()) {
            val matchedChecklists = checklists.filter { message.checklistIds.contains(it.id) }
            matchedChecklists.forEach { checklist ->
                Spacer(modifier = Modifier.height(12.dp))
                ChecklistCard(
                    checklist = checklist,
                    onClick = { onChecklistClick(checklist) }
                )
            }
        }

        if (message.text.isNotBlank() && !isStreaming) {
            Spacer(modifier = Modifier.height(12.dp))

            val isLiked = message.feedback == 1
            val isDisliked = message.feedback == -1

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onFeedbackClick(1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = if (isLiked) painterResource(R.drawable.ic_thumbs_up_filled) else painterResource(R.drawable.ic_thumbs_up),
                        contentDescription = "Helpful",
                        tint = ContentSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { onFeedbackClick(-1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = if (isDisliked) painterResource(R.drawable.ic_thumbs_down_filled) else painterResource(R.drawable.ic_thumbs_down),
                        contentDescription = "Unhelpful",
                        tint = ContentSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
