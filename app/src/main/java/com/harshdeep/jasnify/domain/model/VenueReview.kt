package com.harshdeep.jasnify.domain.model

data class VenueReview(
    val id: String = "",
    val userName: String = "",
    val userAvatarUrl: String? = null,
    val rating: Double = 0.0,
    val relativeTime: String = "",
    val reviewText: String = "",
    val isVerified: Boolean = false,
    val attachedImages: List<String> = emptyList(),
    val merchantReply: VenueMerchantReply? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class VenueMerchantReply(
    val merchantName: String = "",
    val merchantAvatarUrl: String? = null,
    val relativeTime: String = "",
    val replyText: String = "",
    val isVerified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
