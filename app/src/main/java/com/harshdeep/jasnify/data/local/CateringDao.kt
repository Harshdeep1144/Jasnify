package com.harshdeep.jasnify.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CateringDao {
    @Query("SELECT * FROM catering_items WHERE eventId = :eventId ORDER BY type ASC")
    fun getCateringItemsForEvent(eventId: String): Flow<List<CateringItemEntity>>

    @Upsert
    suspend fun insertItem(item: CateringItemEntity)

    @Upsert
    suspend fun insertItems(items: List<CateringItemEntity>)

    @Delete
    suspend fun deleteItem(item: CateringItemEntity)

    @Query("DELETE FROM catering_items WHERE id = :id")
    suspend fun deleteItemById(id: String)
    
    @Query("SELECT COUNT(*) FROM catering_items WHERE eventId = :eventId")
    suspend fun getItemCountForEvent(eventId: String): Int

    // Metadata for seeding tracking
    @Query("SELECT isSeeded FROM catering_metadata WHERE eventId = :eventId")
    suspend fun isEventSeeded(eventId: String): Boolean?

    @Upsert
    suspend fun insertMetadata(metadata: CateringMetadataEntity)
}
