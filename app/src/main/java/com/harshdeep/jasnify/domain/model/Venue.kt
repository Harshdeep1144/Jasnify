package com.harshdeep.jasnify.domain.model

import androidx.compose.runtime.Immutable
import com.google.firebase.firestore.PropertyName
import java.util.UUID

enum class VenueStatus {
    DRAFT,
    PENDING,
    PUBLISHED,
    REJECTED,
    HIDDEN
}

@Immutable
data class Venue(
    val id: String = UUID.randomUUID().toString(),
    val merchantId: String = "",
    val name: String = "",
    val status: VenueStatus = VenueStatus.DRAFT,
    val city: String = "City",
    val locality: String = "Locality",
    val location: String = "",
    val phoneNumber: String? = null,
    val type: String? = null,
    val rating: Double = 0.0,
    val totalReviews: String = "0",
    val priceStartsFrom: String = "₹0",
    val images: List<String> = emptyList(),
    val enquiriesLastMonth: Int = 0,
    val favorite: Boolean = false,
    val aboutText: String? = null,
    val mediaItems: List<VenueMediaItem> = emptyList(),
    val pricingItems: List<VenuePricingItem> = emptyList(),
    val highlightItems: List<VenueHighlightItem> = emptyList(),
    val galleryCategories: List<VenueGalleryCategory> = emptyList(),
    val reviewsData: VenueReviewsData? = null,
    val offers: List<Offer> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

@Immutable
data class VenueMediaItem(
    val url: String = "",
    val video: Boolean = false,
    val videoDuration: String? = null
)

@Immutable
data class VenuePricingItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "",
    val price: String = "",
    val unit: String = "",
    val iconRes: String? = null,
    val labelText: String = "Price Point Offer"
)

@Immutable
data class VenueHighlightItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String = "",
    val value: String = "",
    val iconRes: String? = null
)

@Immutable
data class VenueGalleryCategory(
    val categoryName: String = "",
    val lastUpdated: String? = null,
    val mediaItems: List<VenueMediaItem> = emptyList()
)

@Immutable
data class VenueReviewsData(
    val ratingBreakdown: List<VenueRatingBreakdown> = emptyList(),
    val reviews: List<VenueReview> = emptyList(),
    val totalRatingsCount: String = "0",
    val distribution: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
    val subMetrics: List<VenueRatingBreakdown> = emptyList()
)

@Immutable
data class VenueRatingBreakdown(
    val score: String = "0.0",
    val label: String = ""
)

@Immutable
data class VenueReview(
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
    val merchantReply: VenueMerchantReply? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Immutable
data class VenueMerchantReply(
    val merchantName: String = "",
    val merchantAvatarUrl: String? = null,
    val relativeTime: String = "",
    val replyText: String = "",
    val isVerified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
