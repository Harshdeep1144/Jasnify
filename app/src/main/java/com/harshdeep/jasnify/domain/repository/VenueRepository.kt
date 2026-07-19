package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.SavedVenue
import kotlinx.coroutines.flow.Flow

interface VenueRepository {
    fun getSavedVenues(eventId: String): Flow<List<SavedVenue>>
    suspend fun saveVenue(savedVenue: SavedVenue, syncToCloud: Boolean)
    suspend fun removeSavedVenue(eventId: String, venueName: String, syncToCloud: Boolean)
}
