package com.harshdeep.jasnify.data.mock

import com.harshdeep.jasnify.domain.model.*

object MockData {

    val sampleOffers = listOf(
        Offer(
            title = "Festive Wedding Discount",
            description = "Flat 20% off on all top-rated venue bookings for grand celebrations.",
            code = "JASNIFY20",
            termsAndConditions = listOf(
                "Valid on a minimum venue booking value of ₹50,000.",
                "Applicable for events scheduled during peak wedding season.",
                "Cannot be merged with other merchant-specific discounts.",
                "Subject to venue availability at the time of advance confirmation."
            )
        ),
        Offer(
            title = "Early Bird Photography",
            description = "Get 15% discount on full-day cinematography when booking 90+ days in advance.",
            code = "EARLY15",
            termsAndConditions = listOf(
                "Event date must be at least 90 days from the booking date.",
                "Applicable only on complete photo & video package tiers.",
                "20% advance deposit is mandatory to lock the promotional rate."
            )
        ),
    )

    val sampleVenues1 = listOf(
        Venue(
            merchantId = "merchant_123",
            name = "Hotel Imperial Inn",
            city = "Patna",
            locality = "Sampatchak",
            location = "2nd Floor, Style Baazar, Park Street Road, Sampatchak, Patna, Bihar - 800007",
            rating = 4.4,
            totalReviews = "1k",
            priceStartsFrom = "₹2,999",
            images = listOf(
                "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800"
            ),
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_456",
            name = "Raj Palace Banquet",
            city = "Patna",
            locality = "Danapur",
            location = "Main Road, Danapur Cantonment, Near Danapur Station, Patna, Bihar 801503",
            rating = 4.0,
            totalReviews = "800",
            priceStartsFrom = "₹2,749",
            images = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=800"
            ),
            enquiriesLastMonth = 32,
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_789",
            name = "Royal Garden Lawn",
            city = "Patna",
            locality = "Bailey Road",
            location = "Near Saguna More, Bailey Road, Danapur, Patna, Bihar 801503",
            rating = 4.5,
            totalReviews = "950",
            priceStartsFrom = "₹3,999",
            images = listOf(
                "https://images.unsplash.com/photo-1520854221256-17451cc331bf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=800"
            ),
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_101",
            name = "Grand Celebration Hall",
            city = "Patna",
            locality = "Kankarbagh",
            location = "Lohia Nagar, Kankarbagh Main Road, Opposite PC Colony, Patna, Bihar 800020",
            rating = 4.3,
            totalReviews = "720",
            priceStartsFrom = "₹2,499",
            images = listOf(
                "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800"
            )
        ),
        Venue(
            merchantId = "merchant_102",
            name = "Maharaja Banquet",
            city = "Patna",
            locality = "Ashiana Nagar",
            location = "Ashiana-Digha Road, Near Passport Office, Ashiana Nagar, Patna, Bihar 800025",
            rating = 4.2,
            totalReviews = "530",
            priceStartsFrom = "₹2,799",
            images = listOf(
                "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1532712938310-34cb3982ef74?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1515934751635-c81c6bc9a2d8?auto=format&fit=crop&w=800"
            ),
            enquiriesLastMonth = 90,
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_103",
            name = "Lotus Convention Center",
            city = "Patna",
            locality = "Patliputra Colony",
            location = "Industrial Estate, Patliputra Colony, Near Kurji More, Patna, Bihar 800013",
            rating = 4.6,
            totalReviews = "1.2k",
            priceStartsFrom = "₹3,799",
            images = listOf(
                "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1522158634458-a5dc36a238a8?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1520854221256-17451cc331bf?auto=format&fit=crop&w=800"
            ),
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_104",
            name = "Golden Leaf Banquet",
            city = "Patna",
            locality = "Saguna More",
            location = "R.K. Puram, Saguna More, Danapur-Khagaul Road, Patna, Bihar 801503",
            rating = 4.1,
            totalReviews = "610",
            priceStartsFrom = "₹2,199",
            images = listOf(
                "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800"
            )
        ),
        Venue(
            merchantId = "merchant_105",
            name = "Royal Heritage Lawn",
            city = "Patna",
            locality = "Bihta",
            location = "Near IIT Patna, Bihta-Aurangabad Road, Bihta, Patna, Bihar 801103",
            rating = 4.4,
            totalReviews = "770",
            priceStartsFrom = "₹3,499",
            images = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=800"
            )
        )
    )

    val sampleVenues2 = listOf(
        Venue(
            merchantId = "merchant_201",
            name = "Shahi Garden",
            city = "Patna",
            locality = "Phulwari Sharif",
            location = "Anisabad-Phulwari Road, Near AIIMS Patna, Phulwari Sharif, Patna, Bihar 801505",
            rating = 4.1,
            totalReviews = "610",
            priceStartsFrom = "₹2,199",
            images = listOf(
                "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=800"
            )
        ),
        Venue(
            merchantId = "merchant_202",
            name = "Celebration Banquet",
            city = "Patna",
            locality = "Patliputra Colony",
            location = "P&M Mall Road, Patliputra Colony, Patna, Bihar 800013",
            rating = 4.3,
            totalReviews = "700",
            priceStartsFrom = "₹3,299",
            images = listOf(
                "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1532712938310-34cb3982ef74?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1515934751635-c81c6bc9a2d8?auto=format&fit=crop&w=800"
            ),
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_203",
            name = "Green Valley Resort",
            city = "Patna",
            locality = "Danapur",
            location = "Khagaul-Danapur Road, Near DRM Office, Danapur, Patna, Bihar 801503",
            rating = 4.5,
            totalReviews = "850",
            priceStartsFrom = "₹4,999",
            images = listOf(
                "https://images.unsplash.com/photo-1520854221256-17451cc331bf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800"
            ),
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_204",
            name = "Royal Palace Hall",
            city = "Patna",
            locality = "Boring Road",
            location = "Sri Krishna Puri, Near Boring Road Crossing, Patna, Bihar 800001",
            rating = 4.0,
            totalReviews = "540",
            priceStartsFrom = "₹2,499",
            images = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=800"
            ),
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_205",
            name = "Grand Lotus Banquet",
            city = "Patna",
            locality = "Kankarbagh",
            location = "Hanuman Nagar, Kankarbagh Road, Near Kendriya Vidyalaya, Patna, Bihar 800020",
            rating = 4.2,
            totalReviews = "620",
            priceStartsFrom = "₹2,899",
            images = listOf(
                "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=800"
            )
        ),
        Venue(
            merchantId = "merchant_206",
            name = "Silver Oak Lawn",
            city = "Patna",
            locality = "Saguna More",
            location = "Danapur-Khagaul Road, Near Saguna More, Patna, Bihar 801503",
            rating = 4.3,
            totalReviews = "710",
            priceStartsFrom = "₹3,199",
            images = listOf(
                "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800"
            ),
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_207",
            name = "Emerald Garden",
            city = "Patna",
            locality = "Ashiana Nagar",
            location = "Magistrate Colony Road, Ashiana Nagar, Patna, Bihar 800025",
            rating = 4.4,
            totalReviews = "760",
            priceStartsFrom = "₹3,499",
            images = listOf(
                "https://images.unsplash.com/photo-1520854221256-17451cc331bf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800"
            ),
            offers = sampleOffers
        ),
        Venue(
            merchantId = "merchant_208",
            name = "Grand Palace Banquet",
            city = "Patna",
            locality = "Patna City",
            location = "Ashok Rajpath, Near Takhat Sri Patna Sahib, Patna City, Patna, Bihar 800008",
            rating = 4.1,
            totalReviews = "500",
            priceStartsFrom = "₹2,599",
            images = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=800"
            )
        )
    )

    val sampleGrooming = listOf(
        Vendor(
            name = "Classic Grooming Lounge",
            locality = "Boring Road",
            city = "Patna",
            rating = 4.6,
            priceStartsFrom = "₹12,000",
            images = listOf(
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 15,
            offers = sampleOffers
        ),
        Vendor(
            name = "The Gentleman's Club",
            locality = "Connaught Place",
            city = "Delhi NCR",
            rating = 4.9,
            priceStartsFrom = "₹25,000",
            images = listOf(
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 48
        ),
        Vendor(
            name = "Royal Cut Grooming & Spa",
            locality = "Khan Market",
            city = "Delhi NCR",
            rating = 4.8,
            priceStartsFrom = "₹18,000",
            images = listOf(
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 32
        ),
        Vendor(
            name = "Dapper Kings Studio",
            locality = "Frazer Town",
            city = "Bengaluru",
            rating = 4.7,
            priceStartsFrom = "₹15,000",
            images = listOf(
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 24,
            offers = sampleOffers
        ),
        Vendor(
            name = "Imperial Grooming Parlour",
            locality = "Linking Road",
            city = "Mumbai",
            rating = 4.8,
            priceStartsFrom = "₹22,000",
            images = listOf(
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 41
        ),
        Vendor(
            name = "Signature Men's Spa",
            locality = "C-Scheme",
            city = "Jaipur",
            rating = 4.9,
            priceStartsFrom = "₹16,000",
            images = listOf(
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 28,
            offers = sampleOffers
        ),
        Vendor(
            name = "Urban Grooming Station",
            locality = "Gomti Nagar",
            city = "Lucknow",
            rating = 4.6,
            priceStartsFrom = "₹10,000",
            images = listOf(
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 19
        ),
        Vendor(
            name = "Vogue Groom Lounge",
            locality = "Rajouri Garden",
            city = "Delhi NCR",
            rating = 4.7,
            priceStartsFrom = "₹14,000",
            images = listOf(
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 22,
            offers = sampleOffers
        )
    )

    val sampleMakeupArtists = listOf(
        Vendor(
            name = "MUA by Sanwlee",
            locality = "New Delhi",
            city = "Delhi NCR",
            rating = 4.8,
            priceStartsFrom = "₹65,000",
            images = listOf(
                "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 24,
            offers = sampleOffers
        ),
        Vendor(
            name = "Tanya's L'Oreal Studio",
            locality = "Ghaziabad",
            city = "Delhi NCR",
            rating = 4.7,
            priceStartsFrom = "₹60,000",
            images = listOf(
                "https://images.unsplash.com/photo-1512496011951-aacf7080f56e?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1516975080664-ed2fc6a32937?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 15
        ),
        Vendor(
            name = "Glam by Gauri",
            locality = "South Delhi",
            city = "Delhi NCR",
            rating = 4.9,
            priceStartsFrom = "₹85,000",
            images = listOf(
                "https://images.unsplash.com/photo-1457974182554-a04bb41d9a2f?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1526045612212-70caf35c11bc?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 42
        ),
        Vendor(
            name = "Preeti's Makeover",
            locality = "Noida Sector 18",
            city = "Delhi NCR",
            rating = 4.6,
            priceStartsFrom = "₹45,000",
            images = listOf(
                "https://images.unsplash.com/photo-1503910397258-41d3e21aa51b?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1496440737103-cd596325d314?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 12
        ),
        Vendor(
            name = "Meenakshi Dutt Makeovers",
            locality = "Punjabi Bagh",
            city = "Delhi NCR",
            rating = 4.9,
            priceStartsFrom = "₹95,000",
            images = listOf(
                "https://images.unsplash.com/photo-1516975080664-ed2fc6a32937?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 68
        ),
        Vendor(
            name = "House of Beauty by Parul",
            locality = "Boring Road",
            city = "Patna",
            rating = 4.7,
            priceStartsFrom = "₹25,000",
            images = listOf(
                "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1512496011951-aacf7080f56e?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 31,
            offers = sampleOffers
        ),
        Vendor(
            name = "Artistry by Kriti",
            locality = "Indiranagar",
            city = "Bengaluru",
            rating = 4.8,
            priceStartsFrom = "₹55,000",
            images = listOf(
                "https://images.unsplash.com/photo-1526045612212-70caf35c11bc?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1503910397258-41d3e21aa51b?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 29
        ),
        Vendor(
            name = "Velvet Touch Glam Studio",
            locality = "Bandra West",
            city = "Mumbai",
            rating = 4.8,
            priceStartsFrom = "₹75,000",
            images = listOf(
                "https://images.unsplash.com/photo-1457974182554-a04bb41d9a2f?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1496440737103-cd596325d314?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 40
        )
    )

    val samplePhotographers = listOf(
        Vendor(
            name = "Royal Starlight Photography",
            locality = "Sector 62",
            city = "Noida",
            rating = 4.8,
            priceStartsFrom = "₹45,000",
            images = listOf(
                "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 18
        ),
        Vendor(
            name = "Candid Moments Studio",
            locality = "DLF Phase 4",
            city = "Gurugram",
            rating = 4.6,
            priceStartsFrom = "₹55,000",
            images = listOf(
                "https://images.unsplash.com/photo-1537633552985-df8429e8048b?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 12,
            offers = sampleOffers
        ),
        Vendor(
            name = "The Wedding Story",
            locality = "Vasant Kunj",
            city = "Delhi NCR",
            rating = 4.9,
            priceStartsFrom = "₹1,20,000",
            images = listOf(
                "https://images.unsplash.com/photo-1515934751635-c81c6bc9a2d8?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1520854221256-17451cc331bf?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 55,
            offers = sampleOffers
        ),
        Vendor(
            name = "Visual Vibes Media",
            locality = "Rohini",
            city = "Delhi NCR",
            rating = 4.5,
            priceStartsFrom = "₹35,000",
            images = listOf(
                "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1470633534180-264d8523c9b9?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 8
        ),
        Vendor(
            name = "Frames & Memories Patna",
            locality = "Patliputra Colony",
            city = "Patna",
            rating = 4.7,
            priceStartsFrom = "₹30,000",
            images = listOf(
                "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 27,
            offers = sampleOffers
        ),
        Vendor(
            name = "Lights & Shadows Films",
            locality = "Juhu",
            city = "Mumbai",
            rating = 4.9,
            priceStartsFrom = "₹1,50,000",
            images = listOf(
                "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1537633552985-df8429e8048b?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 62
        ),
        Vendor(
            name = "Eternia Lens Studios",
            locality = "Koramangala",
            city = "Bengaluru",
            rating = 4.8,
            priceStartsFrom = "₹65,000",
            images = listOf(
                "https://images.unsplash.com/photo-1520854221256-17451cc331bf?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 36,
            offers = sampleOffers
        ),
        Vendor(
            name = "Epic Clicks & Wedding Reels",
            locality = "C-Scheme",
            city = "Jaipur",
            rating = 4.7,
            priceStartsFrom = "₹48,000",
            images = listOf(
                "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1515934751635-c81c6bc9a2d8?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 22
        )
    )

    val sampleMehendiArtists = listOf(
        Vendor(
            name = "Deepak Mehendi Art",
            locality = "Dwarka",
            city = "Delhi NCR",
            rating = 4.9,
            priceStartsFrom = "₹15,000",
            images = listOf(
                "https://images.unsplash.com/photo-1590240974880-928929e072b2?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1590240974733-4f91d51c72f7?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 30,
            offers = sampleOffers
        ),
        Vendor(
            name = "Sona Mehendi Designs",
            locality = "Karol Bagh",
            city = "Delhi NCR",
            rating = 4.5,
            priceStartsFrom = "₹12,000",
            images = listOf(
                "https://images.unsplash.com/photo-1590240974733-4f91d51c72f7?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1590240974880-928929e072b2?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 10
        ),
        Vendor(
            name = "Anjali's Henna Studio",
            locality = "Lajpat Nagar",
            city = "Delhi NCR",
            rating = 4.7,
            priceStartsFrom = "₹20,000",
            images = listOf(
                "https://images.unsplash.com/photo-1590240974880-928929e072b2?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1590240974733-4f91d51c72f7?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 25,
            offers = sampleOffers
        ),
        Vendor(
            name = "Rajasthani Mehendi Hub",
            locality = "Chandni Chowk",
            city = "Delhi NCR",
            rating = 4.8,
            priceStartsFrom = "₹18,000",
            images = listOf(
                "https://images.unsplash.com/photo-1590240974733-4f91d51c72f7?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1590240974880-928929e072b2?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 45
        ),
        Vendor(
            name = "Prem Mehendi Creation",
            locality = "Kankarbagh",
            city = "Patna",
            rating = 4.6,
            priceStartsFrom = "₹8,000",
            images = listOf(
                "https://images.unsplash.com/photo-1590240974880-928929e072b2?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1590240974733-4f91d51c72f7?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 18,
            offers = sampleOffers
        ),
        Vendor(
            name = "Kundan Mehendi Artist",
            locality = "Andheri West",
            city = "Mumbai",
            rating = 4.8,
            priceStartsFrom = "₹22,000",
            images = listOf(
                "https://images.unsplash.com/photo-1590240974733-4f91d51c72f7?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1590240974880-928929e072b2?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 34,
            offers = sampleOffers
        ),
        Vendor(
            name = "Royal Marwar Henna",
            locality = "Raja Park",
            city = "Jaipur",
            rating = 4.9,
            priceStartsFrom = "₹16,000",
            images = listOf(
                "https://images.unsplash.com/photo-1590240974880-928929e072b2?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1590240974733-4f91d51c72f7?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 28
        ),
        Vendor(
            name = "Crafts & Curves Henna",
            locality = "Jayanagar",
            city = "Bengaluru",
            rating = 4.7,
            priceStartsFrom = "₹14,000",
            images = listOf(
                "https://images.unsplash.com/photo-1590240974733-4f91d51c72f7?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1590240974880-928929e072b2?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 19,
            offers = sampleOffers
        )
    )

    val sampleJewellery = listOf(
        Vendor(
            name = "Elegant Heritage Jewellery",
            locality = "Ashok Rajpath",
            city = "Patna",
            rating = 4.7,
            priceStartsFrom = "₹50,000",
            images = listOf(
                "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 26,
            offers = sampleOffers
        ),
        Vendor(
            name = "Kundan & Polki Crafts",
            locality = "Chandni Chowk",
            city = "Delhi NCR",
            rating = 4.9,
            priceStartsFrom = "₹1,50,000",
            images = listOf(
                "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 61,
            offers = sampleOffers
        ),
        Vendor(
            name = "Emerald & Ruby Fine Jewels",
            locality = "South Ext 1",
            city = "Delhi NCR",
            rating = 4.8,
            priceStartsFrom = "₹2,00,000",
            images = listOf(
                "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 43
        ),
        Vendor(
            name = "Temple Gold & Gems",
            locality = "Malleswaram",
            city = "Bengaluru",
            rating = 4.9,
            priceStartsFrom = "₹1,20,000",
            images = listOf(
                "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 38,
            offers = sampleOffers
        ),
        Vendor(
            name = "Zaveri Heritage Jewelers",
            locality = "Zaveri Bazaar",
            city = "Mumbai",
            rating = 4.9,
            priceStartsFrom = "₹1,80,000",
            images = listOf(
                "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 75
        ),
        Vendor(
            name = "Royal Rajputana Jewellers",
            locality = "MI Road",
            city = "Jaipur",
            rating = 4.8,
            priceStartsFrom = "₹90,000",
            images = listOf(
                "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 51,
            offers = sampleOffers
        ),
        Vendor(
            name = "Diamond Sparkle Lounge",
            locality = "Greater Kailash",
            city = "Delhi NCR",
            rating = 4.7,
            priceStartsFrom = "₹1,10,000",
            images = listOf(
                "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 34
        ),
        Vendor(
            name = "Nawabi Pearls & Heritage",
            locality = "Chowk",
            city = "Lucknow",
            rating = 4.8,
            priceStartsFrom = "₹70,000",
            images = listOf(
                "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 29,
            offers = sampleOffers
        )
    )

    val sampleOutfits = listOf(
        Vendor(
            name = "Crimson Threads Couture",
            locality = "Shahpur Jat",
            city = "Delhi NCR",
            rating = 4.8,
            priceStartsFrom = "₹85,000",
            images = listOf(
                "https://images.unsplash.com/photo-1594552072238-b8a33785b261?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 41,
            offers = sampleOffers
        ),
        Vendor(
            name = "Rajkumari Bridal & Groom Wear",
            locality = "Chandni Chowk",
            city = "Delhi NCR",
            rating = 4.7,
            priceStartsFrom = "₹45,000",
            images = listOf(
                "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 58
        ),
        Vendor(
            name = "Shringar Ethnic Heritage",
            locality = "Fraser Road",
            city = "Patna",
            rating = 4.5,
            priceStartsFrom = "₹25,000",
            images = listOf(
                "https://images.unsplash.com/photo-1594552072238-b8a33785b261?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 22,
            offers = sampleOffers
        ),
        Vendor(
            name = "Royal Silks & Sherwanis",
            locality = "Commercial Street",
            city = "Bengaluru",
            rating = 4.8,
            priceStartsFrom = "₹60,000",
            images = listOf(
                "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 33,
            offers = sampleOffers
        ),
        Vendor(
            name = "Zardozi Bridal & Tuxedos",
            locality = "South Extension",
            city = "Delhi NCR",
            rating = 4.9,
            priceStartsFrom = "₹1,20,000",
            images = listOf(
                "https://images.unsplash.com/photo-1594552072238-b8a33785b261?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 72
        ),
        Vendor(
            name = "Sheetal Designer Lounge",
            locality = "Juhu",
            city = "Mumbai",
            rating = 4.8,
            priceStartsFrom = "₹1,10,000",
            images = listOf(
                "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 49,
            offers = sampleOffers
        ),
        Vendor(
            name = "Heritage Lehengas & Royal Suits",
            locality = "Johari Bazaar",
            city = "Jaipur",
            rating = 4.9,
            priceStartsFrom = "₹50,000",
            images = listOf(
                "https://images.unsplash.com/photo-1594552072238-b8a33785b261?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 39
        ),
        Vendor(
            name = "Threadwork Atelier",
            locality = "Hazratganj",
            city = "Lucknow",
            rating = 4.7,
            priceStartsFrom = "₹40,000",
            images = listOf(
                "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 26,
            offers = sampleOffers
        )
    )

    val sampleEntertainment = listOf(
        Vendor(
            name = "Rhythm & Beats Live Band",
            locality = "Boring Road",
            city = "Patna",
            rating = 4.8,
            priceStartsFrom = "₹15,000",
            images = listOf(
                "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 19
        ),
        Vendor(
            name = "DJ Soundwave & Visuals",
            locality = "DLF Cyber City",
            city = "Gurugram",
            rating = 4.9,
            priceStartsFrom = "₹45,000",
            images = listOf(
                "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 57,
            offers = sampleOffers
        ),
        Vendor(
            name = "Royal Brass Band & Dhol",
            locality = "Old Delhi",
            city = "Delhi NCR",
            rating = 4.7,
            priceStartsFrom = "₹25,000",
            images = listOf(
                "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 33,
            offers = sampleOffers
        ),
        Vendor(
            name = "Symphony Acoustic Ensemble",
            locality = "Koramangala",
            city = "Bengaluru",
            rating = 4.8,
            priceStartsFrom = "₹35,000",
            images = listOf(
                "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 28
        ),
        Vendor(
            name = "Bollywood Groove Dancers",
            locality = "Andheri East",
            city = "Mumbai",
            rating = 4.9,
            priceStartsFrom = "₹60,000",
            images = listOf(
                "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 64,
            offers = sampleOffers
        ),
        Vendor(
            name = "Sufi & Folk Fusion Ensemble",
            locality = "C-Scheme",
            city = "Jaipur",
            rating = 4.8,
            priceStartsFrom = "₹40,000",
            images = listOf(
                "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 31
        ),
        Vendor(
            name = "Beats of Punjab Dhol Tasha",
            locality = "Sector 35",
            city = "Chandigarh",
            rating = 4.7,
            priceStartsFrom = "₹20,000",
            images = listOf(
                "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 22
        ),
        Vendor(
            name = "Harmony Wedding Orchestra",
            locality = "Hazratganj",
            city = "Lucknow",
            rating = 4.6,
            priceStartsFrom = "₹30,000",
            images = listOf(
                "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 17
        )
    )

    val sampleFood = listOf(
        Vendor(
            name = "Royal Feast Caterers",
            locality = "Kankarbagh",
            city = "Patna",
            rating = 4.7,
            priceStartsFrom = "₹650",
            priceUnit = "per plate",
            images = listOf(
                "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 44,
            offers = sampleOffers
        ),
        Vendor(
            name = "Gourmet Celebrations",
            locality = "Vasant Vihar",
            city = "Delhi NCR",
            rating = 4.9,
            priceStartsFrom = "₹1,800",
            priceUnit = "per plate",
            images = listOf(
                "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 83,
            offers = sampleOffers
        ),
        Vendor(
            name = "Spice & Saffron Catering",
            locality = "Indiranagar",
            city = "Bengaluru",
            rating = 4.8,
            priceStartsFrom = "₹1,200",
            priceUnit = "per plate",
            images = listOf(
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 31
        ),
        Vendor(
            name = "Grand Rasoi Hospitality",
            locality = "Danapur",
            city = "Patna",
            rating = 4.4,
            priceStartsFrom = "₹500",
            priceUnit = "per plate",
            images = listOf(
                "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 19,
            offers = sampleOffers
        ),
        Vendor(
            name = "Culinary Krafts Studio",
            locality = "Powai",
            city = "Mumbai",
            rating = 4.8,
            priceStartsFrom = "₹1,500",
            priceUnit = "per plate",
            images = listOf(
                "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 52
        ),
        Vendor(
            name = "Nawabi Flavors Catering",
            locality = "Hazratganj",
            city = "Lucknow",
            rating = 4.9,
            priceStartsFrom = "₹1,100",
            priceUnit = "per plate",
            images = listOf(
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 60
        ),
        Vendor(
            name = "Shahi Thali Services",
            locality = "Raja Park",
            city = "Jaipur",
            rating = 4.6,
            priceStartsFrom = "₹800",
            priceUnit = "per plate",
            images = listOf(
                "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 27
        ),
        Vendor(
            name = "Golden Spoon Banquet Food",
            locality = "Sector 18",
            city = "Noida",
            rating = 4.7,
            priceStartsFrom = "₹950",
            priceUnit = "per plate",
            images = listOf(
                "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 37
        )
    )

    val sampleGifts = listOf(
        Vendor(
            name = "Unique Gift Hamper Studio",
            locality = "Saguna More",
            city = "Patna",
            rating = 4.3,
            priceStartsFrom = "₹200",
            images = listOf(
                "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 14,
            offers = sampleOffers
        ),
        Vendor(
            name = "Sweet Moments Custom Trays",
            locality = "Hauz Khas",
            city = "Delhi NCR",
            rating = 4.8,
            priceStartsFrom = "₹500",
            images = listOf(
                "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 42
        ),
        Vendor(
            name = "Artisan Wood & Brass Favors",
            locality = "Sadatganj",
            city = "Lucknow",
            rating = 4.7,
            priceStartsFrom = "₹350",
            images = listOf(
                "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 25
        ),
        Vendor(
            name = "Royal Mithai & Box Crafts",
            locality = "Chandni Chowk",
            city = "Delhi NCR",
            rating = 4.9,
            priceStartsFrom = "₹600",
            images = listOf(
                "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 59
        ),
        Vendor(
            name = "Botanic Bliss Eco Favors",
            locality = "Indiranagar",
            city = "Bengaluru",
            rating = 4.8,
            priceStartsFrom = "₹300",
            images = listOf(
                "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 38
        ),
        Vendor(
            name = "Velvet Box Gifting Lounge",
            locality = "Bandra East",
            city = "Mumbai",
            rating = 4.8,
            priceStartsFrom = "₹800",
            images = listOf(
                "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 46
        ),
        Vendor(
            name = "Heritage Craft Favors",
            locality = "Johari Bazaar",
            city = "Jaipur",
            rating = 4.7,
            priceStartsFrom = "₹400",
            images = listOf(
                "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&w=800"
            ),
            favorite = true,
            enquiriesLastMonth = 31
        ),
        Vendor(
            name = "Golden Knot Invitations & Favors",
            locality = "Sector 62",
            city = "Noida",
            rating = 4.6,
            priceStartsFrom = "₹450",
            images = listOf(
                "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&w=800",
                "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800"
            ),
            favorite = false,
            enquiriesLastMonth = 23
        )
    )

    val sampleVendors = (
            sampleGrooming.map { it.copy(category = "Grooming") } +
                    sampleMakeupArtists.map { it.copy(category = "Makeup") } +
                    samplePhotographers.map { it.copy(category = "Photography") } +
                    sampleMehendiArtists.map { it.copy(category = "Mehendi") } +
                    sampleJewellery.map { it.copy(category = "Jewellery") } +
                    sampleOutfits.map { it.copy(category = "Outfits") } +
                    sampleEntertainment.map { it.copy(category = "Entertainment") } +
                    sampleFood.map { it.copy(category = "Food") } +
                    sampleGifts.map { it.copy(category = "Gifts") }
            ).map { getDetailsForVendor(it) }

    fun getDetailsForVendor(vendor: Vendor): Vendor {
        val detailImages = vendor.images.toMutableList()
        while (detailImages.size < 6) {
            detailImages.add("https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800")
        }

        val parsedBasePrice = parsePrice(vendor.priceStartsFrom)
        val premiumPrice = if (parsedBasePrice > 0) "₹" + (parsedBasePrice * 1.6).toInt() else "₹50,000"

        return vendor.copy(
            id = vendor.id.ifBlank { "vendor_${vendor.name.replace(" ", "_").lowercase()}" },
            phoneNumber = "+919876543210",
            aboutText = "${vendor.name} is a premier ${vendor.category} professional based in ${vendor.locality}, ${vendor.city}. With years of experience and an unwavering commitment to quality, they specialize in grand celebrations, weddings, and high-profile social events.",
            mediaItems = detailImages.map { url -> VendorMediaItem(url = url, video = false) },
            offers = listOf(
                Offer(
                    title = "Weekday Celebration",
                    description = "Flat 20% off on hall rental or per-plate rates.",
                    code = "WEEKDAY20",
                    termsAndConditions = listOf(
                        "Valid for events booked Monday to Thursday.",
                        "Minimum 100 plates or hall booking needed for availing this discount.",
                        "Excludes weekdays on major public holidays or auspicious wedding dates.",
                        "If a weekday booking is postponed or rescheduled to a weekend slot (Fri-Sun), the discount becomes void and standard weekend rates will apply.",
                        "The offer is only applicable if the full booking deposit (25%) is cleared at the time of reservation."
                    )
                ),
                Offer(
                    title = "Early Bird Discount",
                    description = "Get 10% discount when booking 6+ months in advance.",
                    code = "EARLY10",
                    termsAndConditions = listOf(
                        "Applicable on total booking value.",
                        "Must book at least 180 days before the event date."
                    )
                )
            ),
            pricingItems = listOf(
                VendorPricingItem(
                    title = "Standard Service Package",
                    price = vendor.priceStartsFrom,
                    unit = vendor.priceUnit.ifEmpty { "Starting" },
                    iconRes = "ic_star"
                ),
                VendorPricingItem(
                    title = "Premium Luxury Tier",
                    price = premiumPrice,
                    unit = if (vendor.priceUnit.isNotEmpty()) vendor.priceUnit else "Full Event",
                    iconRes = "ic_star_2"
                )
            ),
            highlightItems = listOf(
                VendorHighlightItem(label = "Experience", value = "6+ Years", iconRes = "ic_user_default"),
                VendorHighlightItem(label = "Travels to Venue", value = "Yes, Pan-India", iconRes = "ic_car"),
                VendorHighlightItem(label = "Advance Deposit", value = "25% to Book", iconRes = "ic_receipt"),
                VendorHighlightItem(label = "Customization", value = "Tailored Packages", iconRes = "ic_star")
            ),
            galleryCategories = listOf(
                VendorGalleryCategory(
                    categoryName = "Featured Work",
                    mediaItems = detailImages.map { VendorMediaItem(url = it) }
                ),
                VendorGalleryCategory(
                    categoryName = "Recent Ceremonies",
                    mediaItems = listOf(
                        VendorMediaItem("https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=800"),
                        VendorMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800")
                    )
                )
            ),
            reviewsData = VendorReviewsData(
                totalRatingsCount = (80..650).random().toString(),
                distribution = listOf(0.82f, 0.11f, 0.04f, 0.02f, 0.01f),
                ratingBreakdown = listOf(
                    VendorRatingBreakdown(score = "4.9", label = "Professionalism"),
                    VendorRatingBreakdown(score = "4.8", label = "Quality of Work"),
                    VendorRatingBreakdown(score = "4.7", label = "Punctuality")
                ),
                reviews = listOf(
                    VendorReview(
                        id = java.util.UUID.randomUUID().toString(),
                        userName = "Ananya Sharma",
                        rating = 5.0,
                        relativeTime = "2 days ago",
                        reviewText = "Absolute perfection! ${vendor.name} made our event so stress-free and spectacular.",
                        isVerified = true
                    ),
                    VendorReview(
                        id = java.util.UUID.randomUUID().toString(),
                        userName = "Rohan Verma",
                        rating = 4.8,
                        relativeTime = "1 week ago",
                        reviewText = "Very prompt response, highly courteous staff, and incredible attention to detail.",
                        isVerified = true
                    )
                )
            )
        )
    }

    fun getDetailsForVenue(venue: Venue): Venue {
        val detailImages = venue.images.toMutableList()
        while (detailImages.size < 8) {
            detailImages.add("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800")
        }

        return venue.copy(
            type = if (venue.name.contains("Lawn")) "Lawn / Farmhouse" else "Banquet Hall",
            phoneNumber = "+919876543210",
            aboutText = "${venue.name} located in ${venue.location} is an exquisite venue suited for premium wedding receptions, engagement ceremonies, parties, and upscale corporate conferences. Our customizable services ensure that your special day matches your dreams perfectly.",
            enquiriesLastMonth = if (venue.enquiriesLastMonth == 0) (20..150).random() else venue.enquiriesLastMonth,
            offers = listOf(
                Offer(
                    title = "Anniversary Special",
                    description = "10% off on decorations and event setups.",
                    code = "ANNIV10",
                    termsAndConditions = listOf(
                        "Valid for anniversary events only.",
                        "Minimum booking of ₹50,000 required."
                    )
                )
            ),
            mediaItems = listOf(
                VenueMediaItem(url = detailImages[0], video = false),
                VenueMediaItem(url = "https://www.w3schools.com/html/mov_bbb.mp4", video = true, videoDuration = "0:10"),
                VenueMediaItem(url = detailImages[1], video = false),
                VenueMediaItem(url = detailImages[2], video = false),
                VenueMediaItem(url = detailImages[3], video = false)
            ),
            pricingItems = listOf(
                VenuePricingItem(
                    title = "Veg Package",
                    price = venue.priceStartsFrom,
                    unit = "Per Plate",
                    iconRes = "ic_veg",
                    labelText = "Standard Entry Package"
                ),
                VenuePricingItem(
                    title = "Non-Veg Package",
                    price = "₹" + (parsePrice(venue.priceStartsFrom) + 400).toString(),
                    unit = "Per Plate",
                    iconRes = "ic_non_veg",
                    labelText = "Premium Culinary Tier"
                ),
                VenuePricingItem(
                    title = "Hall Rental",
                    price = "₹45,000",
                    unit = "Per Day",
                    iconRes = "ic_door",
                    labelText = "Excludes Food / Decor"
                )
            ),
            highlightItems = listOf(
                VenueHighlightItem(label = "Capacity", value = "250 to 1200 Guests", iconRes = "ic_user_default"),
                VenueHighlightItem(label = "Space Status", value = "Indoor & Outdoor Available", iconRes = "ic_star_2"),
                VenueHighlightItem(label = "Parking", value = "Valet for 150+ Vehicles", iconRes = "ic_car"),
                VenueHighlightItem(label = "Catering", value = "In-house & Outside Allowed", iconRes = "ic_food"),
                VenueHighlightItem(label = "Music", value = "DJ & Sound System", iconRes = "ic_music")
            ),
            galleryCategories = listOf(
                VenueGalleryCategory(
                    categoryName = "All Photos",
                    mediaItems = detailImages.mapIndexed { index, url ->
                        if (index == 1) VenueMediaItem(url = "https://www.w3schools.com/html/mov_bbb.mp4", video = true, videoDuration = "0:15")
                        else VenueMediaItem(url = url)
                    }
                ),
                VenueGalleryCategory(
                    categoryName = "Decor",
                    mediaItems = listOf(
                        VenueMediaItem("https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800"),
                        VenueMediaItem("https://images.unsplash.com/photo-1520854221256-17451cc331bf?auto=format&fit=crop&w=800")
                    )
                ),
                VenueGalleryCategory(
                    categoryName = "Food & Catering",
                    mediaItems = listOf(
                        VenueMediaItem("https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=800"),
                        VenueMediaItem("https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=800")
                    )
                )
            ),
            reviewsData = VenueReviewsData(
                totalRatingsCount = "1,248",
                distribution = listOf(0.7f, 0.2f, 0.05f, 0.03f, 0.02f),
                ratingBreakdown = listOf(
                    VenueRatingBreakdown(score = "4.8", label = "Food Quality"),
                    VenueRatingBreakdown(score = "4.5", label = "Staff Behavior"),
                    VenueRatingBreakdown(score = "4.2", label = "Cleanliness")
                ),
                subMetrics = listOf(
                    VenueRatingBreakdown(score = "4.7", label = "Value for Money"),
                    VenueRatingBreakdown(score = "4.6", label = "Ambiance")
                ),
                reviews = listOf(
                    VenueReview(
                        id = java.util.UUID.randomUUID().toString(),
                        userName = "Amit Kumar",
                        userAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100",
                        rating = 4.5,
                        relativeTime = "2 weeks ago",
                        reviewText = "Highly satisfied with the services. The food was stellar and managing event flow with staff was smooth.",
                        isVerified = true
                    ),
                    VenueReview(
                        id = java.util.UUID.randomUUID().toString(),
                        userName = "Priya Singh",
                        userAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100",
                        rating = 4.0,
                        relativeTime = "1 month ago",
                        reviewText = "Beautiful interior arrangement and lighting facilities.",
                        isVerified = true,
                        merchantReply = VenueMerchantReply(
                            merchantName = "Venue Manager",
                            replyText = "Thank you Priya! We are glad you enjoyed your event.",
                            relativeTime = "3 weeks ago"
                        )
                    )
                )
            )
        )
    }

    private fun parsePrice(priceString: String): Int {
        val clean = priceString
            .replace("₹", "")
            .replace(",", "")
            .replace(" ", "")
            .trim()
        return clean.toIntOrNull() ?: 0
    }

    val venueDetailsMap: Map<String, Venue> by lazy {
        val fullList = sampleVenues1 + sampleVenues2
        fullList.associate { venue ->
            venue.name to getDetailsForVenue(venue)
        }
    }

    suspend fun seedToFirestore(repository: com.harshdeep.jasnify.domain.repository.VenueRepository) {
        val allDetails = venueDetailsMap.values.toList()
        repository.seedMockVenues(allDetails)
    }
}