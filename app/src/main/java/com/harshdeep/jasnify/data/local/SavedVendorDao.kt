package com.harshdeep.jasnify.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedVendorDao {
    @Query("SELECT * FROM saved_vendors WHERE eventId = :eventId ORDER BY timestamp DESC")
    fun getSavedVendors(eventId: String): Flow<List<SavedVendorEntity>>

    @Query("SELECT * FROM saved_vendors WHERE eventId = :eventId AND category = :category ORDER BY timestamp DESC")
    fun getSavedVendorsByCategory(eventId: String, category: String): Flow<List<SavedVendorEntity>>

    @Upsert
    suspend fun insertSavedVendor(vendor: SavedVendorEntity)

    @Query("DELETE FROM saved_vendors WHERE vendorName = :vendorName AND eventId = :eventId AND category = :category")
    suspend fun deleteSavedVendor(vendorName: String, eventId: String, category: String)

    @Query("DELETE FROM saved_vendors WHERE eventId = :eventId")
    suspend fun clearSavedVendors(eventId: String)
}
