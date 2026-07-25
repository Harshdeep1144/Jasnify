package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.Enquiry
import com.harshdeep.jasnify.domain.model.MerchantUser
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.repository.EnquiryRepository
import com.harshdeep.jasnify.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnquiryViewModel @Inject constructor(
    private val enquiryRepository: EnquiryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun getEnquiriesForUser(userId: String): Flow<List<Enquiry>> {
        return enquiryRepository.getEnquiriesForUser(userId)
    }

    fun getEnquiriesForMerchant(merchantId: String): Flow<List<Enquiry>> {
        return enquiryRepository.getEnquiriesForMerchant(merchantId)
    }

    fun getMerchantProfile(merchantId: String): Flow<MerchantUser?> {
        return userRepository.getMerchantProfileFlow(merchantId)
    }

    fun getChatMessages(userId: String, merchantId: String, itemId: String): Flow<List<ChatMessage>> {
        val enquiryId = "${userId}_${merchantId}_${itemId}"
        return enquiryRepository.getEnquiryById(enquiryId).map { it?.messages ?: emptyList() }
    }

    fun sendMessage(userId: String, merchantId: String, itemId: String, itemName: String, itemType: String = "Venue", text: String) {
        val enquiryId = "${userId}_${merchantId}_${itemId}"
        val message = ChatMessage(
            text = text,
            senderId = userId,
            timestamp = System.currentTimeMillis()
        )
        
        viewModelScope.launch {
            val enquiry = enquiryRepository.getEnquiryOnce(enquiryId)
            if (enquiry == null) {
                // First message: Create enquiry with this message
                val userProfile = userRepository.getUserProfile(userId)
                
                val newEnquiry = Enquiry(
                    id = enquiryId,
                    userId = userId,
                    userName = userProfile?.name ?: "User",
                    userProfileUrl = userProfile?.profilePictureUrl,
                    merchantId = merchantId,
                    venueId = itemId, 
                    venueName = itemName,
                    itemType = itemType,
                    lastMessage = text,
                    timestamp = System.currentTimeMillis(),
                    messages = listOf(message)
                )
                enquiryRepository.createEnquiry(newEnquiry)
            } else {
                // Subsequent messages: Append to list
                enquiryRepository.sendMessage(enquiryId, message)
            }
        }
    }

    fun markMessagesAsSeen(userId: String, merchantId: String, itemId: String) {
        val enquiryId = "${userId}_${merchantId}_${itemId}"
        viewModelScope.launch {
            enquiryRepository.updateMessageStatus(enquiryId, userId, MessageStatus.SEEN)
        }
    }

    fun markMessagesAsDelivered(userId: String, merchantId: String, itemId: String) {
        val enquiryId = "${userId}_${merchantId}_${itemId}"
        viewModelScope.launch {
            enquiryRepository.markAsDelivered(enquiryId, userId)
        }
    }
}
