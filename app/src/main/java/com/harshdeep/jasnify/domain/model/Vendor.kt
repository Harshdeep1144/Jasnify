package com.harshdeep.jasnify.domain.model

import java.util.UUID

enum class VendorStatus {
    DRAFT,
    PENDING,
    PUBLISHED,
    REJECTED,
    HIDDEN
}

data class Vendor(
    val id: String = UUID.randomUUID().toString(),
    val merchantId: String = "",
    val name: String = "",
    val category: String = "",
    val categoryIconRes: String? = null,
    val status: VendorStatus = VendorStatus.DRAFT,
    val city: String = "City",
    val locality: String = "Locality",
    val location: String = "",
    val rating: Double = 0.0,
    val totalReviews: String = "0",
    val priceStartsFrom: String = "₹0",
    val priceUnit: String = "per day",
    val images: List<String> = emptyList(),
    val enquiriesLastMonth: Int = 0,
    val favorite: Boolean = false,
    val aboutText: String? = null,
    val mediaItems: List<VendorMediaItem> = emptyList(),
    val pricingItems: List<VendorPricingItem> = emptyList(),
    val highlightItems: List<VendorHighlightItem> = emptyList(),
    val galleryCategories: List<VendorGalleryCategory> = emptyList(),
    val reviewsData: VendorReviewsData? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class VendorMediaItem(
    val url: String = "",
    val video: Boolean = false,
    val videoDuration: String? = null
)

data class VendorPricingItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val price: String = "",
    val unit: String = "",
    val iconRes: String? = null,
    val labelText: String = "Price Point Offer"
)

data class VendorHighlightItem(
    val id: String = UUID.randomUUID().toString(),
    val label: String = "",
    val value: String = "",
    val iconRes: String? = null
)

data class VendorGalleryCategory(
    val categoryName: String = "",
    val lastUpdated: String? = null,
    val mediaItems: List<VendorMediaItem> = emptyList()
)

data class VendorReviewsData(
    val ratingBreakdown: List<VendorRatingBreakdown> = emptyList(),
    val reviews: List<VendorReview> = emptyList(),
    val totalRatingsCount: String = "0",
    val distribution: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
    val subMetrics: List<VendorRatingBreakdown> = emptyList()
)

data class VendorRatingBreakdown(
    val score: String = "0.0",
    val label: String = ""
)

data class VendorReview(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String? = null,
    val rating: Double = 0.0,
    val relativeTime: String = "",
    val reviewText: String = "",
    val isVerified: Boolean = false,
    val attachedImages: List<String> = emptyList(),
    val likedOptions: List<String> = emptyList(),
    val merchantReply: VendorMerchantReply? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class VendorMerchantReply(
    val merchantName: String = "",
    val merchantAvatarUrl: String? = null,
    val relativeTime: String = "",
    val replyText: String = "",
    val isVerified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
