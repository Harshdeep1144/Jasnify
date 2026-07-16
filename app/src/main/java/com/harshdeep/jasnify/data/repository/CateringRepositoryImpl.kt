package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.local.CateringDao
import com.harshdeep.jasnify.data.local.CateringItemEntity
import com.harshdeep.jasnify.data.local.CateringMetadataEntity
import com.harshdeep.jasnify.domain.repository.CateringRepository
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class CateringRepositoryImpl @Inject constructor(
    private val cateringDao: CateringDao,
    private val firestore: FirebaseFirestore
) : CateringRepository {

    private val externalScope = CoroutineScope(Dispatchers.IO)

    override fun getCateringItems(eventId: String): Flow<List<CateringItemEntity>> = 
        cateringDao.getCateringItemsForEvent(eventId)

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
                    .set(mapOf("isSeeded" to true), com.google.firebase.firestore.SetOptions.merge())
                    .await()
            } catch (e: Exception) {}
        }

        val normalizedType = eventType.lowercase()
        val defaultItems = when {
            // Grand Festive Events: Wedding, Engagement, Anniversary
            normalizedType.contains("wedding") ||
            normalizedType.contains("engagement") ||
            normalizedType.contains("anniversary") -> listOf(
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Chicken Malai Tikka", Dietary.NonVeg, "Starters", "Mughlai"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Crispy Chilli Potato", Dietary.Veg, "Starters", "Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Mutton Seekh Kebab", Dietary.NonVeg, "Starters", "Mughlai"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Amritsari Fish Fry", Dietary.NonVeg, "Starters", "Punjabi"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Hara Bhara Kebab", Dietary.Veg, "Starters", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Chicken 65", Dietary.NonVeg, "Starters", "South Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Cheese Corn Balls", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Garlic Butter Prawns", Dietary.NonVeg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Mango Lassi", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Masala Lemonade", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Virgin Mojito", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Cold Coffee with Ice Cream", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Iced Peach Tea", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Blue Lagoon Mocktail", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Sweet & Salt Lime Soda", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Thandai", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Assorted Soft Drinks", Dietary.Veg, "Beverages", "Global"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Rajma Rice Bowl", Dietary.Veg, "Main Course", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Dal Makhani", Dietary.Veg, "Main Course", "Punjabi"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Butter Chicken", Dietary.NonVeg, "Main Course", "Punjabi"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Mutton Rogan Josh", Dietary.NonVeg, "Main Course", "Kashmiri"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Paneer Lababdar", Dietary.Veg, "Main Course", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Hyderabadi Chicken Biryani", Dietary.NonVeg, "Main Course", "Hyderabadi"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Veg Dum Biryani", Dietary.Veg, "Main Course", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Chicken Tikka Masala", Dietary.NonVeg, "Main Course", "Mughlai"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Chicken Hakka Noodles", Dietary.NonVeg, "Main Course", "Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Fish Curry", Dietary.NonVeg, "Main Course", "Coastal"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Chilli Paneer", Dietary.Veg, "Main Course", "Indo-Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Veg Manchurian", Dietary.Veg, "Main Course", "Indo-Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Chicken Manchurian", Dietary.NonVeg, "Main Course", "Indo-Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Tandoori Roti", Dietary.Veg, "Main Course", "Indian Bread"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Butter Naan", Dietary.Veg, "Main Course", "Indian Bread"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Paneer Stuffed Naan", Dietary.Veg, "Main Course", "Indian Bread"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Classic Cheesecake", Dietary.Veg, "Desserts", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Moong Dal Halwa", Dietary.Veg, "Desserts", "Rajasthani"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Warm Chocolate Brownie", Dietary.Veg, "Desserts", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Kesari Phirni", Dietary.Veg, "Desserts", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Tiramisu Cups", Dietary.Veg, "Desserts", "Italian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Fresh Fruit Cream", Dietary.Veg, "Desserts", "Global"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Shahi Tukda", Dietary.Veg, "Desserts", "Awadhi"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Vanilla Bean Ice Cream", Dietary.Veg, "Desserts", "Global")
            )

            normalizedType.contains("birthday") ||
            normalizedType.contains("family") ||
            normalizedType.contains("reunion") ||
            normalizedType.contains("farewell") -> listOf(
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Mini Burgers", Dietary.NonVeg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "French Fries", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Cheese Corn Balls", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Chilli Paneer", Dietary.Veg, "Main Course", "Indo-Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Chicken Hakka Noodles", Dietary.NonVeg, "Main Course", "Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Virgin Mojito", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Cold Coffee with Ice Cream", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Warm Chocolate Brownie", Dietary.Veg, "Desserts", "Continental")
            )

            normalizedType.contains("seminar") ||
            normalizedType.contains("exhibition") ||
            normalizedType.contains("meetup") -> listOf(
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Hara Bhara Kebab", Dietary.Veg, "Starters", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Assorted Sandwiches", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Masala Lemonade", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Iced Peach Tea", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Dal Makhani", Dietary.Veg, "Main Course", "Punjabi"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Paneer Lababdar", Dietary.Veg, "Main Course", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Butter Naan", Dietary.Veg, "Main Course", "Indian Bread"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Fresh Fruit Cream", Dietary.Veg, "Desserts", "Global")
            )

            normalizedType.contains("concert") ||
            normalizedType.contains("cultural") -> listOf(
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Crispy Chilli Potato", Dietary.Veg, "Starters", "Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Chicken 65", Dietary.NonVeg, "Starters", "South Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Assorted Soft Drinks", Dietary.Veg, "Beverages", "Global"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Sweet & Salt Lime Soda", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Veg Dum Biryani", Dietary.Veg, "Main Course", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Hyderabadi Chicken Biryani", Dietary.NonVeg, "Main Course", "Hyderabadi")
            )

            else -> listOf(
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Masala Lemonade", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Cheese Corn Balls", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Veg Dum Biryani", Dietary.Veg, "Main Course", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), eventId, "Vanilla Bean Ice Cream", Dietary.Veg, "Desserts", "Global")
            )
        }
        addItems(defaultItems)
    }
}
