package com.harshdeep.jasnify.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.local.ChecklistDao
import com.harshdeep.jasnify.data.mapper.toChecklist
import com.harshdeep.jasnify.data.mapper.toChecklistEntity
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.domain.repository.ChecklistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class ChecklistRepositoryImpl @Inject constructor(
    private val dao: ChecklistDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ChecklistRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun getAllChecklists(): Flow<List<Checklist>> {
        return dao.getAllChecklists().map { entities ->
            entities.map { it.toChecklist() }
        }
    }

    override fun getArchivedChecklists(): Flow<List<Checklist>> {
        return dao.getArchivedChecklists().map { entities ->
            entities.map { it.toChecklist() }
        }
    }

    override suspend fun saveChecklist(checklist: Checklist) {
        withContext(NonCancellable) {
            // Add ownerId if missing and user is logged in
            val currentUserId = auth.currentUser?.uid ?: ""
            val checklistToSave = if (checklist.ownerId.isEmpty()) {
                checklist.copy(ownerId = currentUserId)
            } else {
                checklist
            }

            // 1. Update Room Instantly
            try {
                dao.insertChecklist(checklistToSave.toChecklistEntity())
                Log.d("ChecklistRepo", "Successfully saved checklist ${checklistToSave.id} to Room")
            } catch (e: Exception) {
                Log.e("ChecklistRepo", "Error saving to Room: ${e.message}")
            }

            // 2. Trigger Firestore Sync in background
            if (currentUserId.isNotEmpty()) {
                scope.launch {
                    try {
                        firestore.collection("checklists")
                            .document(checklistToSave.id)
                            .set(checklistToSave)
                            .await()
                        Log.d("ChecklistRepo", "Successfully synced checklist ${checklistToSave.id} to Firestore")
                    } catch (e: Exception) {
                        Log.e("ChecklistRepo", "Error syncing to Firestore: ${e.message}", e)
                    }
                }
            } else {
                Log.w("ChecklistRepo", "No user logged in, skipping Firestore sync")
            }
        }
    }

    override suspend fun deleteChecklist(id: String) {
        dao.deleteChecklistById(id)
        
        scope.launch {
            try {
                firestore.collection("checklists")
                    .document(id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("ChecklistRepo", "Error deleting from Firestore: ${e.message}", e)
            }
        }
    }

    override suspend fun syncWithFirestore() {
        // Implement full sync logic if needed
    }
}
