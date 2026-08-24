package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.harshdeep.jasnify.data.local.CateringDao
import com.harshdeep.jasnify.data.local.CateringItemEntity
import com.harshdeep.jasnify.data.local.CateringMetadataEntity
import com.harshdeep.jasnify.data.remote.api.DishImageApi
import com.harshdeep.jasnify.data.utils.CateringDefaults
import com.harshdeep.jasnify.domain.repository.CateringRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.harshdeep.jasnify.BuildConfig

@Singleton
class CateringRepositoryImpl @Inject constructor(
    private val dishImageApi: DishImageApi,
    private val cateringDao: CateringDao,
    private val firestore: FirebaseFirestore
) : CateringRepository {

    private val externalScope = CoroutineScope(Dispatchers.IO)

    override fun getCateringItems(eventId: String): Flow<List<CateringItemEntity>> {
        fetchItemsFromFirestore(eventId)
        return cateringDao.getCateringItemsForEvent(eventId)
    }

    private fun fetchItemsFromFirestore(eventId: String) {
        if (eventId.isEmpty()) return

        // 1. Listen for items
        firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Catering")
            .collection("items")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                snapshot?.documents?.forEach { doc ->
                    val item = doc.toObject(CateringItemEntity::class.java)
                    if (item != null) {
                        externalScope.launch {
                            cateringDao.insertItem(item.copy(isSynced = true))
                        }
                    }
                }
            }

        // 2. Listen for metadata (isSeeded)
        firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Catering")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                if (snapshot.getBoolean("isSeeded") == true) {
                    externalScope.launch {
                        cateringDao.insertMetadata(CateringMetadataEntity(eventId, true))
                    }
                }
            }
    }

    override suspend fun addItem(item: CateringItemEntity) {
        cateringDao.insertItem(item)
        syncItemToCloud(item)
    }

    override suspend fun addItems(items: List<CateringItemEntity>) {
        cateringDao.insertItems(items)
        items.forEach { syncItemToCloud(it) }
    }

    override suspend fun deleteItem(itemId: String, eventId: String) {
        cateringDao.deleteItemById(itemId)
        externalScope.launch {
            try {
                firestore.collection("events")
                    .document(eventId)
                    .collection("rooms")
                    .document("Catering")
                    .collection("items")
                    .document(itemId)
                    .delete()
                    .await()
            } catch (e: Exception) {}
        }
    }

    private fun syncItemToCloud(item: CateringItemEntity) {
        externalScope.launch {
            try {
                firestore.collection("events")
                    .document(item.eventId)
                    .collection("rooms")
                    .document("Catering")
                    .collection("items")
                    .document(item.id)
                    .set(item)
                    .await()
                cateringDao.insertItem(item.copy(isSynced = true))
            } catch (e: Exception) {}
        }
    }

    override suspend fun seedDefaultItems(eventType: String, eventId: String) {
        // 1. Check local metadata first (Instant)
        if (cateringDao.isEventSeeded(eventId) == true) return

        // 2. Check remote metadata (Backup)
        try {
            val doc = firestore.collection("events")
                .document(eventId)
                .collection("rooms")
                .document("Catering")
                .get()
                .await()

            if (doc.exists() && doc.getBoolean("isSeeded") == true) {
                // Update local metadata so we don't check remote again
                cateringDao.insertMetadata(CateringMetadataEntity(eventId, true))
                return
            }
        } catch (e: Exception) {}

        // 3. Mark as seeded locally immediately to prevent race conditions
        cateringDao.insertMetadata(CateringMetadataEntity(eventId, true))

        // 4. Mark as seeded in Firestore
        externalScope.launch {
            try {
                firestore.collection("events")
                    .document(eventId)
                    .collection("rooms")
                    .document("Catering")
                    .set(mapOf("isSeeded" to true), SetOptions.merge())
                    .await()
            } catch (e: Exception) {}
        }

        // 5. Retrieve appropriate template bundle and map to entities
        val normalizedType = eventType.lowercase()
        val templateItems = when {
            normalizedType.contains("wedding") ||
                    normalizedType.contains("engagement") ||
                    normalizedType.contains("anniversary") -> CateringDefaults.weddingTemplates

            normalizedType.contains("birthday") ||
                    normalizedType.contains("family") ||
                    normalizedType.contains("reunion") ||
                    normalizedType.contains("farewell") -> CateringDefaults.casualPartyTemplates

            normalizedType.contains("seminar") ||
                    normalizedType.contains("exhibition") ||
                    normalizedType.contains("meetup") ||
                    normalizedType.contains("corporate") -> CateringDefaults.corporateTemplates

            normalizedType.contains("concert") ||
                    normalizedType.contains("cultural") ||
                    normalizedType.contains("festival") -> CateringDefaults.festivalTemplates

            else -> CateringDefaults.generalTemplates
        }

        val defaultItems = templateItems.map { it.toEntity(eventId) }
        addItems(defaultItems)
    }

    override suspend fun searchDishImages(query: String): Result<List<String>> {
        return try {
            val cleanQuery = query
                .replace(" dish food", "", ignoreCase = true)
                .replace(" dish", "", ignoreCase = true)
                .replace(" food", "", ignoreCase = true)
                .replace("Indian", "", ignoreCase = true)
                .replace("Bread", "", ignoreCase = true)
                .replace("Continental", "", ignoreCase = true)
                .replace("Mughlai", "", ignoreCase = true)
                .replace("with Ice Cream", "", ignoreCase = true)
                .replace("with Rabri", "", ignoreCase = true)
                .replace("Kullhad", "", ignoreCase = true)
                .trim()

            android.util.Log.d("CateringRepo", "Wikipedia Search: $cleanQuery")
            
            val response = dishImageApi.searchDishImages("$cleanQuery dish")
            val pages = response.query?.pages?.values ?: emptyList()
            var urls = pages.mapNotNull { it.original?.source ?: it.thumbnail?.source }
                .filter { it.isNotBlank() }

            if (urls.isEmpty()) {
                android.util.Log.d("CateringRepo", "Wikipedia failed for $cleanQuery. Using Fallback.")
                
                val mainWord = cleanQuery.split(" ").lastOrNull() ?: "food"
                
                // Use a mix of LoremFlickr and Pixabay-like static placeholders for high reliability
                urls = listOf(
                    "https://loremflickr.com/400/400/food,$mainWord",
                    "https://loremflickr.com/400/400/culinary,$mainWord",
                    "https://loremflickr.com/400/400/dish,$mainWord",
                    "https://loremflickr.com/400/400/meal,$mainWord",
                    "https://loremflickr.com/400/400/food,delicious"
                )
            }

            android.util.Log.d("CateringRepo", "Final URL list: $urls")
            Result.success(urls)
        } catch (e: Exception) {
            android.util.Log.e("CateringRepo", "Critical Search Error: ${e.message}")
            val errorUrls = listOf("https://loremflickr.com/400/400/food")
            Result.success(errorUrls)
        }
    }

}