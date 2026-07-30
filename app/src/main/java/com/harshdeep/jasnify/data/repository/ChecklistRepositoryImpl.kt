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

    override fun getAllChecklists(eventId: String): Flow<List<Checklist>> {
        Log.d("ChecklistRepo", "getAllChecklists called for eventId: $eventId")
        // Trigger background fetch from Firestore whenever this is called
        fetchChecklistsFromFirestore(eventId)
        
        return dao.getAllChecklists(eventId).map { entities ->
            Log.d("ChecklistRepo", "Emitting ${entities.size} checklists from Room for eventId: $eventId")
            entities.map { it.toChecklist() }
        }
    }

    override fun getArchivedChecklists(eventId: String): Flow<List<Checklist>> {
        return dao.getArchivedChecklists(eventId).map { entities ->
            entities.map { it.toChecklist() }
        }
    }

    private fun fetchChecklistsFromFirestore(eventId: String) {
        val userId = auth.currentUser?.uid ?: return
        Log.d("ChecklistRepo", "Fetching from Firestore for user: $userId, eventId: $eventId")
        
        if (eventId.isEmpty()) return

        val query = firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Checklist")
            .collection("items")

        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("ChecklistRepo", "Error listening to checklists", e)
                return@addSnapshotListener
            }
            
            Log.d("ChecklistRepo", "Firestore snapshot received with ${snapshot?.size()} documents")
            snapshot?.documents?.forEach { doc ->
                try {
                    val checklist = doc.toObject(Checklist::class.java)
                    if (checklist != null) {
                        Log.d("ChecklistRepo", "Found checklist in Firestore: ${checklist.title} (ID: ${checklist.id}, eventId: ${checklist.eventId})")
                        scope.launch {
                            val existing = dao.getChecklistById(checklist.id)
                            val entityToInsert = if (existing != null) {
                                checklist.toChecklistEntity().copy(
                                    pinned = existing.pinned,
                                    archived = existing.archived,
                                    createdAt = if (existing.createdAt != 0L) existing.createdAt else (if (checklist.createdAt != 0L) checklist.createdAt else System.currentTimeMillis())
                                )
                            } else {
                                checklist.toChecklistEntity().let { 
                                    if (it.createdAt == 0L) it.copy(createdAt = System.currentTimeMillis()) else it
                                }
                            }
                            dao.insertChecklist(entityToInsert)
                        }
                    }
                } catch (ex: Exception) {
                    Log.e("ChecklistRepo", "Error parsing checklist document: ${ex.message}")
                }
            }
        }
        
        // Migrate legacy checklists if needed (Optional, keeping simple for now)
    }

    override suspend fun saveChecklist(checklist: Checklist) {
        withContext(NonCancellable) {
            val currentUserId = auth.currentUser?.uid ?: ""
            val checklistToSave = if (checklist.ownerId.isEmpty()) {
                checklist.copy(ownerId = currentUserId)
            } else {
                checklist
            }

            val eventId = checklistToSave.eventId
            Log.d("ChecklistRepo", "Attempting to save checklist: ${checklistToSave.title} (ID: ${checklistToSave.id}) with eventId: $eventId")
            
            if (eventId.isNullOrEmpty()) {
                Log.e("ChecklistRepo", "ABORTING SAVE: Cannot save checklist without eventId")
                return@withContext
            }

            Log.d("ChecklistRepo", "Proceeding to save to Room and Firestore...")

            // 1. Update Room Instantly
            try {
                dao.insertChecklist(checklistToSave.toChecklistEntity())
                Log.d("ChecklistRepo", "Saved to Room successfully")
            } catch (e: Exception) {
                Log.e("ChecklistRepo", "Error saving to Room: ${e.message}")
            }

            // 2. Sync to Firestore
            if (currentUserId.isNotEmpty()) {
                scope.launch {
                    try {
                        firestore.collection("events")
                            .document(eventId)
                            .collection("rooms")
                            .document("Checklist")
                            .collection("items")
                            .document(checklistToSave.id)
                            .set(checklistToSave)
                            .await()
                        Log.d("ChecklistRepo", "Synced to Firestore successfully")
                    } catch (e: Exception) {
                        Log.e("ChecklistRepo", "Error syncing to Firestore: ${e.message}")
                    }
                }
            }
        }
    }

    override suspend fun saveChecklistLocally(checklist: Checklist) {
        withContext(Dispatchers.IO) {
            try {
                dao.insertChecklist(checklist.toChecklistEntity())
                Log.d("ChecklistRepo", "Saved to Room locally: ${checklist.title}")
            } catch (e: Exception) {
                Log.e("ChecklistRepo", "Error saving locally to Room: ${e.message}")
            }
        }
    }

    override suspend fun deleteChecklist(id: String) {
        val checklist = dao.getChecklistById(id)
        dao.deleteChecklistById(id)
        
        val eventId = checklist?.eventId
        if (eventId.isNullOrEmpty()) return

        scope.launch {
            try {
                firestore.collection("events")
                    .document(eventId)
                    .collection("rooms")
                    .document("Checklist")
                    .collection("items")
                    .document(id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("ChecklistRepo", "Error deleting from Firestore: ${e.message}")
            }
        }
    }

    override suspend fun syncWithFirestore() {
        // Implement full sync if needed
    }
}
