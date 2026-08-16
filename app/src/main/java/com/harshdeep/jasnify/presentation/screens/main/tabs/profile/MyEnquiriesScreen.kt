package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.Enquiry
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.cards.EnquiryCard
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary

@Composable
fun MyEnquiriesScreen(
    enquiryViewModel: EnquiryViewModel,
    userId: String,
    onBack: () -> Unit,
    onEnquiryClick: (Enquiry) -> Unit
) {
    val enquiries by enquiryViewModel.getEnquiriesForUser(userId).collectAsState(emptyList())

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "My Enquiries",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(enquiries) { enquiry ->
                EnquiryCard(
                    enquiry = enquiry,
                    onClick = { onEnquiryClick(enquiry) },
                    currentUserId = userId
                )
            }
        }
    }
}
