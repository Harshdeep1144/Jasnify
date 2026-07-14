package com.harshdeep.jasnify.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CateringDao {
    @Query("SELECT * FROM catering_items ORDER BY type ASC")
    fun getAllCateringItems(): Flow<List<CateringItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CateringItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CateringItemEntity>)

    @Delete
    suspend fun deleteItem(item: CateringItemEntity)

    @Query("DELETE FROM catering_items WHERE id = :id")
    suspend fun deleteItemById(id: String)
    
    @Query("SELECT COUNT(*) FROM catering_items")
    suspend fun getItemCount(): Int
}
