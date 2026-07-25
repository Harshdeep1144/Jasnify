package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.Enquiry
import com.harshdeep.jasnify.domain.model.MessageStatus
import kotlinx.coroutines.flow.Flow

interface EnquiryRepository {
    suspend fun createEnquiry(enquiry: Enquiry)
    fun getEnquiriesForUser(userId: String): Flow<List<Enquiry>>
    fun getEnquiriesForMerchant(merchantId: String): Flow<List<Enquiry>>
    fun getEnquiryById(enquiryId: String): Flow<Enquiry?>
    suspend fun sendMessage(enquiryId: String, message: ChatMessage)
    suspend fun updateMessageStatus(enquiryId: String, userId: String, status: MessageStatus)
}
