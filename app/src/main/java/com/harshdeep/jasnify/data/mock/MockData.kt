package com.harshdeep.jasnify.data.mock

import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.*

object MockData {
    val sampleVenues1 = listOf(
        Venue(
            name = "Hotel Imperial Inn",
            location = "2nd Floor, Style Baazar, Park Street Road, Sampatchak, Patna, Bihar - 800007, Patna, Bihar 800007",
            rating = 4.4,
            totalReviews = "1k",
            services = listOf("AC Rooms", "Catering", "Parking", "AC Rooms", "AC Rooms", "AC Rooms", "AC Rooms", "AC Rooms", "AC Rooms"),
            priceStartsFrom = "₹2,999",
            images = listOf(
                "https://picsum.photos/800/400?random=1",
                "https://picsum.photos/800/400?random=2",
                "https://picsum.photos/800/400?random=3"
            )
        ),
        Venue(
            name = "Raj Palace Banquet",
            location = "Danapur, Patna",
            rating = 4.0,
            totalReviews = "800",
            services = listOf("Decor", "AC", "Power Backup"),
            priceStartsFrom = "₹2,749",
            images = listOf(
                "https://picsum.photos/800/400?random=4",
                "https://picsum.photos/800/400?random=5",
                "https://picsum.photos/800/400?random=6"
            ),
            enquiriesLastMonth = 32
        ),
        Venue(
            name = "Royal Garden Lawn",
            location = "Bailey Road, Patna",
            rating = 4.5,
            totalReviews = "950",
            services = listOf("Lawn", "Lighting", "Catering"),
            priceStartsFrom = "₹3,999",
            images = listOf(
                "https://picsum.photos/800/400?random=7",
                "https://picsum.photos/800/400?random=8",
                "https://picsum.photos/800/400?random=9"
            )
        ),
        Venue(
            name = "Grand Celebration Hall",
            location = "Kankarbagh, Patna",
            rating = 4.3,
            totalReviews = "720",
            services = listOf("Stage", "Decoration", "Parking"),
            priceStartsFrom = "₹2,499",
            images = listOf(
                "https://picsum.photos/800/400?random=10",
                "https://picsum.photos/800/400?random=11",
                "https://picsum.photos/800/400?random=12"
            )
        ),
        Venue(
            name = "Maharaja Banquet",
            location = "Ashiana Nagar, Patna",
            rating = 4.2,
            totalReviews = "530",
            services = listOf("DJ", "Lighting", "Catering"),
            priceStartsFrom = "₹2,799",
            images = listOf(
                "https://picsum.photos/800/400?random=13",
                "https://picsum.photos/800/400?random=14",
                "https://picsum.photos/800/400?random=15"
            ),
            enquiriesLastMonth = 90
        ),
        Venue(
            name = "Lotus Convention Center",
            location = "Patliputra Colony, Patna",
            rating = 4.6,
            totalReviews = "1.2k",
            services = listOf("AC Hall", "Decoration", "Parking"),
            priceStartsFrom = "₹3,799",
            images = listOf(
                "https://picsum.photos/800/400?random=16",
                "https://picsum.photos/800/400?random=17",
                "https://picsum.photos/800/400?random=18"
            )
        ),
        Venue(
            name = "Golden Leaf Banquet",
            location = "Saguna More, Patna",
            rating = 4.1,
            totalReviews = "610",
            services = listOf("Stage", "AC Hall", "Decor"),
            priceStartsFrom = "₹2,199",
            images = listOf(
                "https://picsum.photos/800/400?random=19",
                "https://picsum.photos/800/400?random=20",
                "https://picsum.photos/800/400?random=21"
            )
        ),
        Venue(
            name = "Royal Heritage Lawn",
            location = "Bihta, Patna",
            rating = 4.4,
            totalReviews = "770",
            services = listOf("Outdoor Lawn", "Lighting", "Parking"),
            priceStartsFrom = "₹3,499",
            images = listOf(
                "https://picsum.photos/800/400?random=22",
                "https://picsum.photos/800/400?random=23",
                "https://picsum.photos/800/400?random=24"
            )
        )
    )

    val sampleVenues2 = listOf(
        Venue(
            name = "Shahi Garden",
            location = "Phulwari Sharif, Patna",
            rating = 4.1,
            totalReviews = "610",
            services = listOf("Lawn", "Decoration", "Parking"),
            priceStartsFrom = "₹2,199",
            images = listOf(
                "https://picsum.photos/800/400?random=25",
                "https://picsum.photos/800/400?random=26",
                "https://picsum.photos/800/400?random=27"
            )
        ),
        Venue(
            name = "Celebration Banquet",
            location = "Patliputra Colony, Patna",
            rating = 4.3,
            totalReviews = "700",
            services = listOf("AC Hall", "DJ", "Catering"),
            priceStartsFrom = "₹3,299",
            images = listOf(
                "https://picsum.photos/800/400?random=28",
                "https://picsum.photos/800/400?random=29",
                "https://picsum.photos/800/400?random=30"
            )
        ),
        Venue(
            name = "Green Valley Resort",
            location = "Danapur, Patna",
            rating = 4.5,
            totalReviews = "850",
            services = listOf("Resort Stay", "Pool", "Catering"),
            priceStartsFrom = "₹4,999",
            images = listOf(
                "https://picsum.photos/800/400?random=31",
                "https://picsum.photos/800/400?random=32",
                "https://picsum.photos/800/400?random=33"
            )
        ),
        Venue(
            name = "Royal Palace Hall",
            location = "Boring Road, Patna",
            rating = 4.0,
            totalReviews = "540",
            services = listOf("Stage", "Lighting", "Parking"),
            priceStartsFrom = "₹2,499",
            images = listOf(
                "https://picsum.photos/800/400?random=34",
                "https://picsum.photos/800/400?random=35",
                "https://picsum.photos/800/400?random=36"
            )
        ),
        Venue(
            name = "Grand Lotus Banquet",
            location = "Kankarbagh, Patna",
            rating = 4.2,
            totalReviews = "620",
            services = listOf("Decoration", "DJ", "AC Hall"),
            priceStartsFrom = "₹2,899",
            images = listOf(
                "https://picsum.photos/800/400?random=37",
                "https://picsum.photos/800/400?random=38",
                "https://picsum.photos/800/400?random=39"
            )
        ),
        Venue(
            name = "Silver Oak Lawn",
            location = "Saguna More, Patna",
            rating = 4.3,
            totalReviews = "710",
            services = listOf("Outdoor Lawn", "Lighting", "Catering"),
            priceStartsFrom = "₹3,199",
            images = listOf(
                "https://picsum.photos/800/400?random=40",
                "https://picsum.photos/800/400?random=41",
                "https://picsum.photos/800/400?random=42"
            )
        ),
        Venue(
            name = "Emerald Garden",
            location = "Ashiana Nagar, Patna",
            rating = 4.4,
            totalReviews = "760",
            services = listOf("Garden Venue", "Decor", "Parking"),
            priceStartsFrom = "₹3,499",
            images = listOf(
                "https://picsum.photos/800/400?random=43",
                "https://picsum.photos/800/400?random=44",
                "https://picsum.photos/800/400?random=45"
            )
        ),
        Venue(
            name = "Grand Palace Banquet",
            location = "Patna City, Patna",
            rating = 4.1,
            totalReviews = "500",
            services = listOf("AC Hall", "Decoration", "Stage"),
            priceStartsFrom = "₹2,599",
            images = listOf(
                "https://picsum.photos/800/400?random=46",
                "https://picsum.photos/800/400?random=47",
                "https://picsum.photos/800/400?random=48"
            )
        )
    )

    // Generator function that constructs robust mock detail screens for any given card.
    fun getDetailsForVenue(venue: Venue, similar: List<Venue>): Venue {
        val detailImages = venue.images.toMutableList()
        // Ensure we have at least 4 unique gallery image placeholders for the 4-grid layout
        while (detailImages.size < 4) {
            detailImages.add("https://picsum.photos/800/600?random=${(100..999).random()}")
        }

        return venue.copy(
            mediaItems = listOf(
                VenueMediaItem(url = detailImages[0], isVideo = false),
                VenueMediaItem(url = detailImages[1], isVideo = true, videoDuration = "0:45"),
                VenueMediaItem(url = detailImages[2], isVideo = false),
                VenueMediaItem(url = detailImages[3], isVideo = false)
            ),
            pricingItems = listOf(
                VenuePricingItem(
                    title = "Veg Package Plate",
                    price = venue.priceStartsFrom,
                    unit = "Per Plate",
                    iconRes = R.drawable.ic_gallery, // Fallback drawable identifier
                    labelText = "Standard Entry Package"
                ),
                VenuePricingItem(
                    title = "Non-Veg Package Plate",
                    price = "₹" + ((venue.priceStartsFrom.replace("₹", "").replace(",", "").toIntOrNull() ?: 2000) + 400).toString(),
                    unit = "Per Plate",
                    iconRes = R.drawable.ic_gallery,
                    labelText = "Premium Culinary Tier"
                ),
                VenuePricingItem(
                    title = "Hall Rental Only",
                    price = "₹45,000",
                    unit = "Per Day",
                    iconRes = R.drawable.ic_gallery,
                    labelText = "Excludes Food / Decor Service"
                )
            ),
            highlightItems = listOf(
                VenueHighlightItem(
                    label = "Capacity",
                    value = "250 to 1200 Guests",
                    iconRes = R.drawable.ic_gallery
                ),
                VenueHighlightItem(
                    label = "Space Status",
                    value = "Indoor Hall & Outdoor Lawn Available",
                    iconRes = R.drawable.ic_gallery
                ),
                VenueHighlightItem(
                    label = "Parking Space",
                    value = "Valet Parking for up to 150 Vehicles",
                    iconRes = R.drawable.ic_gallery
                )
            ),
            aboutText = "${venue.name} located around ${venue.location} is an exquisite venue suited for premium wedding receptions, engagement ceremonies, parties, and upscale corporate conferences. Our customizable services ensure that your special day matches your dreams.",
            galleryCategories = listOf(
                VenueGalleryCategory(
                    categoryName = "All Photos",
                    imageUrls = detailImages
                ),
                VenueGalleryCategory(
                    categoryName = "Decor",
                    imageUrls = listOf(
                        "https://picsum.photos/800/400?random=101",
                        "https://picsum.photos/800/400?random=102",
                        "https://picsum.photos/800/400?random=103",
                        "https://picsum.photos/800/400?random=104"
                    )
                ),
                VenueGalleryCategory(
                    categoryName = "Seating Layout",
                    imageUrls = listOf(
                        "https://picsum.photos/800/400?random=105",
                        "https://picsum.photos/800/400?random=106",
                        "https://picsum.photos/800/400?random=107",
                        "https://picsum.photos/800/400?random=108"
                    )
                ),
                VenueGalleryCategory(
                    categoryName = "Food & Catering",
                    imageUrls = listOf(
                        "https://picsum.photos/800/400?random=109",
                        "https://picsum.photos/800/400?random=110",
                        "https://picsum.photos/800/400?random=111",
                        "https://picsum.photos/800/400?random=112"
                    )
                )
            ),
            reviewsData = VenueReviewsData(
                ratingBreakdown = listOf(
                    VenueRatingBreakdown(score = "4.8", label = "Food Quality"),
                    VenueRatingBreakdown(score = "4.5", label = "Staff Behavior"),
                    VenueRatingBreakdown(score = "4.2", label = "Location Space")
                ),
                reviews = listOf(
                    VenueReview(
                        userName = "Amit Kumar",
                        userAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100",
                        rating = 4.5,
                        relativeTime = "2 weeks ago",
                        reviewText = "Highly satisfied with the services. The food was absolutely stellar, and managing the event flow with the staff coordinators was exceptionally smooth."
                    ),
                    VenueReview(
                        userName = "Priya Singh",
                        userAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100",
                        rating = 4.0,
                        relativeTime = "1 month ago",
                        reviewText = "Beautiful interior arrangement and lightning facilities. The air conditioning was working well even during extreme summer peak days."
                    )
                )
            )
            // similarVenues logic can be added if needed in a more production-like way
        )
    }

    // Lazy mapped details for quick retrieval
    val venueDetailsMap: Map<String, Venue> by lazy {
        val fullList = sampleVenues1 + sampleVenues2
        fullList.associate { venue ->
            venue.name to getDetailsForVenue(venue, fullList)
        }
    }
}
