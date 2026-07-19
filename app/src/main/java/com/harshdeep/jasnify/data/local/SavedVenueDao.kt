package com.harshdeep.jasnify.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedVenueDao {
    @Query("SELECT * FROM saved_venues WHERE eventId = :eventId ORDER BY timestamp DESC")
    fun getSavedVenues(eventId: String): Flow<List<SavedVenueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedVenue(venue: SavedVenueEntity)

    @Query("DELETE FROM saved_venues WHERE venueName = :venueName AND eventId = :eventId")
    suspend fun deleteSavedVenue(venueName: String, eventId: String)
}
