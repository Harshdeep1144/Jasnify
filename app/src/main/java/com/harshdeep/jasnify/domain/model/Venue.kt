package com.harshdeep.jasnify.domain.model

import java.util.UUID

data class Venue(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val location: String = "",
    val type: String? = null,
    val rating: Double = 0.0,
    val totalReviews: String = "0",
    val services: List<String> = emptyList(),
    val priceStartsFrom: String = "₹0",
    val images: List<String> = emptyList(),
    val enquiriesLastMonth: Int = 0,
    val isFavorite: Boolean = false,
    val aboutText: String? = null,
    val mediaItems: List<VenueMediaItem> = emptyList(),
    val pricingItems: List<VenuePricingItem> = emptyList(),
    val highlightItems: List<VenueHighlightItem> = emptyList(),
    val galleryCategories: List<VenueGalleryCategory> = emptyList(),
    val reviewsData: VenueReviewsData? = null,
    val similarVenues: List<Venue>? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class VenueMediaItem(
    val url: String = "",
    val isVideo: Boolean = false,
    val videoDuration: String? = null
)

data class VenuePricingItem(
    val title: String = "",
    val price: String = "",
    val unit: String = "",
    val iconRes: Int? = null,
    val labelText: String = "Price Point Offer"
)

data class VenueHighlightItem(
    val label: String = "",
    val value: String = "",
    val iconRes: Int? = null
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
