package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.Enquiry
import com.harshdeep.jasnify.domain.model.SavedVenue
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.model.VenueReview
import kotlinx.coroutines.flow.Flow

interface VenueRepository {
    // Catalog
    fun getAllVenues(): Flow<List<Venue>>
    fun getVenueById(venueId: String): Flow<Venue?>
    fun getVenueReviews(venueId: String): Flow<List<VenueReview>>
    suspend fun addVenueReview(venueId: String, review: VenueReview)
    suspend fun deleteVenueReview(venueId: String, userId: String)
    
    // Saved Venues
    fun getSavedVenues(eventId: String): Flow<List<SavedVenue>>
    suspend fun saveVenue(savedVenue: SavedVenue, syncToCloud: Boolean)
    suspend fun removeSavedVenue(eventId: String, venueName: String, syncToCloud: Boolean)

    // For Initial Setup/Seeding
    suspend fun seedMockVenues(venues: List<Venue>)
}
