package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Enquiry
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.cards.EnquiryCard
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme

enum class EnquiryFilter(val title: String) {
    ALL("All"),
    VENUES("Venues"),
    VENDORS("Vendors")
}

@Composable
fun MyEnquiriesScreen(
    enquiryViewModel: EnquiryViewModel,
    userId: String,
    onBack: () -> Unit,
    onEnquiryClick: (Enquiry) -> Unit
) {
    val enquiries by enquiryViewModel.getEnquiriesForUser(userId).collectAsState(initial = emptyList())

    MyEnquiriesContent(
        enquiries = enquiries,
        userId = userId,
        onBack = onBack,
        onEnquiryClick = onEnquiryClick
    )
}

@Composable
fun MyEnquiriesContent(
    enquiries: List<Enquiry>,
    userId: String,
    onBack: () -> Unit,
    onEnquiryClick: (Enquiry) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(EnquiryFilter.ALL) }

    val filteredEnquiries = remember(enquiries, selectedFilter) {
        when (selectedFilter) {
            EnquiryFilter.ALL -> enquiries
            EnquiryFilter.VENUES -> enquiries.filter {
                it.itemType.equals("Venue", ignoreCase = true)
            }
            EnquiryFilter.VENDORS -> enquiries.filter {
                it.itemType.equals("Vendor", ignoreCase = true)
            }
        }
    }

    val emptyStateMessage = when (selectedFilter) {
        EnquiryFilter.ALL -> "Your vendors and venues\nenquiries will appear here"
        EnquiryFilter.VENUES -> "Your venue enquiries\nwill appear here"
        EnquiryFilter.VENDORS -> "Your vendor enquiries\nwill appear here"
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
            ) {
                CustomTopBar(
                    title = "My Enquiries",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )

                IosSegmentedControl(
                    options = EnquiryFilter.entries,
                    selectedOption = selectedFilter,
                    onOptionSelected = { selectedFilter = it },
                    labelProvider = { it.title },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { padding ->
        if (filteredEnquiries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(bottom = 96.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_message),
                        contentDescription = null,
                        tint = ContentTertiary,
                        modifier = Modifier.size(84.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = emptyStateMessage,
                        style = JasnifyTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = ContentTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEnquiries, key = { it.id }) { enquiry ->
                    EnquiryCard(
                        enquiry = enquiry,
                        onClick = { onEnquiryClick(enquiry) },
                        currentUserId = userId
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun MyEnquiriesEmptyPreview() {
    JasnifyTheme {
        MyEnquiriesContent(
            enquiries = emptyList(),
            userId = "user_123",
            onBack = {},
            onEnquiryClick = {}
        )
    }
}