package com.harshdeep.jasnify.presentation.screens.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.catch
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.presentation.components.cards.EnquiryCard
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.*

@Composable
fun ProfileTab(
    mainNavController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    venueViewModel: VenueViewModel = hiltViewModel(),
    enquiryViewModel: EnquiryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""
    
    val enquiries by remember(currentUserUid) {
        enquiryViewModel.getEnquiriesForUser(currentUserUid)
            .catch { emit(emptyList()) }
    }.collectAsState(initial = emptyList())

    // Mark incoming messages as delivered when they appear in the inbox
    LaunchedEffect(enquiries) {
        enquiries.forEach { enquiry ->
            val hasUndelivered = enquiry.messages.any { 
                it.senderId != currentUserUid && it.status == MessageStatus.SENT 
            }
            if (hasUndelivered) {
                enquiryViewModel.markMessagesAsDelivered(currentUserUid, enquiry.merchantId, enquiry.venueId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(16.dp),
    ) {
        Text(
            text = "Profile",
            style = JasnifyTheme.typography.displayLarge,
            color = ContentPrimary
        )
        
        Spacer(Modifier.height(24.dp))
        
        Text(
            text = "My Enquiries",
            style = JasnifyTheme.typography.headingLarge,
            fontWeight = FontWeight.Medium,
            color = ContentPrimary
        )
        
        Spacer(Modifier.height(12.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(enquiries) { enquiry ->
                EnquiryCard(
                    enquiry = enquiry,
                    currentUserId = currentUserUid,
                    onClick = {
                        mainNavController.navigate("chat_screen/${enquiry.merchantId}/${enquiry.venueId}")
                    }
                )
            }
            
            if (enquiries.isEmpty()) {
                item {
                    Text(
                        text = "No enquiries yet.",
                        style = JasnifyTheme.typography.labelLarge,
                        color = ContentSecondary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        
        Button(
            onClick = {
                authViewModel.logout(context)
                eventViewModel.clearActiveEvent()
                mainNavController.navigate(Screen.LoginOrSignUp.route) {
                    popUpTo(Screen.MainAppGraph.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
