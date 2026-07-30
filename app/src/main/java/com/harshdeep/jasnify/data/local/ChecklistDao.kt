package com.harshdeep.jasnify.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklists WHERE archived = 0 AND (eventId = :eventId OR eventId IS NULL) ORDER BY pinned DESC, createdAt DESC")
    fun getAllChecklists(eventId: String): Flow<List<ChecklistEntity>>

    @Query("SELECT * FROM checklists WHERE archived = 1 AND (eventId = :eventId OR eventId IS NULL) ORDER BY createdAt DESC")
    fun getArchivedChecklists(eventId: String): Flow<List<ChecklistEntity>>

    @Query("SELECT * FROM checklists WHERE id = :id")
    suspend fun getChecklistById(id: String): ChecklistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(checklist: ChecklistEntity)

    @Delete
    suspend fun deleteChecklist(checklist: ChecklistEntity)

    @Query("DELETE FROM checklists WHERE id = :id")
    suspend fun deleteChecklistById(id: String)
}
