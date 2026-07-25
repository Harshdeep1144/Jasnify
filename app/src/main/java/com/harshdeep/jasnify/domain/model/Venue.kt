package com.harshdeep.jasnify.domain.model

import com.google.firebase.firestore.PropertyName
import java.util.UUID

enum class VenueStatus {
    DRAFT,
    PENDING,
    PUBLISHED,
    REJECTED,
    HIDDEN
}

data class Venue(
    val id: String = UUID.randomUUID().toString(),
    val merchantId: String = "",
    val name: String = "",
    val status: VenueStatus = VenueStatus.DRAFT,
    val city: String = "City",
    val locality: String = "Locality",
    val location: String = "",
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
    val timestamp: Long = System.currentTimeMillis()
)

data class VenueMediaItem(
    val url: String = "",
    val video: Boolean = false,
    val videoDuration: String? = null
)

data class VenuePricingItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "",
    val price: String = "",
    val unit: String = "",
    val iconRes: String? = null,
    val labelText: String = "Price Point Offer"
)

data class VenueHighlightItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String = "",
    val value: String = "",
    val iconRes: String? = null
)

data class VenueGalleryCategory(
    val categoryName: String = "",
    val mediaItems: List<VenueMediaItem> = emptyList()
)

data class VenueReviewsData(
    val ratingBreakdown: List<VenueRatingBreakdown> = emptyList(),
    val reviews: List<VenueReview> = emptyList(),
    val totalRatingsCount: String = "0",
    val distribution: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
    val subMetrics: List<VenueRatingBreakdown> = emptyList()
)

data class VenueRatingBreakdown(
    val score: String = "0.0",
    val label: String = ""
)
