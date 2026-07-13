package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.Checklist
import kotlinx.coroutines.flow.Flow

interface ChecklistRepository {
    fun getAllChecklists(): Flow<List<Checklist>>
    fun getArchivedChecklists(): Flow<List<Checklist>>
    suspend fun saveChecklist(checklist: Checklist)
    suspend fun deleteChecklist(id: String)
    suspend fun syncWithFirestore()
}
