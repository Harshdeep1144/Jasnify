package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.local.CateringDao
import com.harshdeep.jasnify.data.local.CateringItemEntity
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

    override fun getAllCateringItems(): Flow<List<CateringItemEntity>> = cateringDao.getAllCateringItems()

    override suspend fun addItem(item: CateringItemEntity) {
        cateringDao.insertItem(item)
        syncItemToCloud(item)
    }

    override suspend fun addItems(items: List<CateringItemEntity>) {
        cateringDao.insertItems(items)
        items.forEach { syncItemToCloud(it) }
    }

    override suspend fun deleteItem(itemId: String) {
        cateringDao.deleteItemById(itemId)
        externalScope.launch {
            try {
                firestore.collection("catering")
                    .document("default_room")
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
                firestore.collection("catering")
                    .document("default_room")
                    .collection("items")
                    .document(item.id)
                    .set(item)
                    .await()
                cateringDao.insertItem(item.copy(isSynced = true))
            } catch (e: Exception) {}
        }
    }

    override suspend fun seedDefaultItems(eventType: String) {
        val count = cateringDao.getItemCount()
        if (count > 0) return

        val normalizedType = eventType.lowercase()
        val defaultItems = when {
            // Grand Festive Events: Wedding, Engagement, Anniversary
            normalizedType.contains("wedding") ||
                    normalizedType.contains("engagement") ||
                    normalizedType.contains("anniversary") -> listOf(
                // --- STARTERS ---
                CateringItemEntity(UUID.randomUUID().toString(), "Chicken Malai Tikka", Dietary.NonVeg, "Starters", "Mughlai"),
                CateringItemEntity(UUID.randomUUID().toString(), "Crispy Chilli Potato", Dietary.Veg, "Starters", "Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), "Mutton Seekh Kebab", Dietary.NonVeg, "Starters", "Mughlai"),
                CateringItemEntity(UUID.randomUUID().toString(), "Amritsari Fish Fry", Dietary.NonVeg, "Starters", "Punjabi"),
                CateringItemEntity(UUID.randomUUID().toString(), "Hara Bhara Kebab", Dietary.Veg, "Starters", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Chicken 65", Dietary.NonVeg, "Starters", "South Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Cheese Corn Balls", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Garlic Butter Prawns", Dietary.NonVeg, "Starters", "Continental"),

                // --- BEVERAGES ---
                CateringItemEntity(UUID.randomUUID().toString(), "Mango Lassi", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Masala Lemonade", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Virgin Mojito", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Cold Coffee with Ice Cream", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Iced Peach Tea", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Blue Lagoon Mocktail", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Sweet & Salt Lime Soda", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Thandai", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Assorted Soft Drinks", Dietary.Veg, "Beverages", "Global"),

                // --- MAIN COURSE ---
                CateringItemEntity(UUID.randomUUID().toString(), "Rajma Rice Bowl", Dietary.Veg, "Main Course", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Dal Makhani", Dietary.Veg, "Main Course", "Punjabi"),
                CateringItemEntity(UUID.randomUUID().toString(), "Butter Chicken", Dietary.NonVeg, "Main Course", "Punjabi"),
                CateringItemEntity(UUID.randomUUID().toString(), "Mutton Rogan Josh", Dietary.NonVeg, "Main Course", "Kashmiri"),
                CateringItemEntity(UUID.randomUUID().toString(), "Paneer Lababdar", Dietary.Veg, "Main Course", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Hyderabadi Chicken Biryani", Dietary.NonVeg, "Main Course", "Hyderabadi"),
                CateringItemEntity(UUID.randomUUID().toString(), "Veg Dum Biryani", Dietary.Veg, "Main Course", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Chicken Tikka Masala", Dietary.NonVeg, "Main Course", "Mughlai"),
                CateringItemEntity(UUID.randomUUID().toString(), "Chicken Hakka Noodles", Dietary.NonVeg, "Main Course", "Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), "Fish Curry", Dietary.NonVeg, "Main Course", "Coastal"),
                CateringItemEntity(UUID.randomUUID().toString(), "Chilli Paneer", Dietary.Veg, "Main Course", "Indo-Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), "Veg Manchurian", Dietary.Veg, "Main Course", "Indo-Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), "Chicken Manchurian", Dietary.NonVeg, "Main Course", "Indo-Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), "Tandoori Roti", Dietary.Veg, "Main Course", "Indian Bread"),
                CateringItemEntity(UUID.randomUUID().toString(), "Butter Naan", Dietary.Veg, "Main Course", "Indian Bread"),
                CateringItemEntity(UUID.randomUUID().toString(), "Paneer Stuffed Naan", Dietary.Veg, "Main Course", "Indian Bread"),

                // --- DESSERTS ---
                CateringItemEntity(UUID.randomUUID().toString(), "Classic Cheesecake", Dietary.Veg, "Desserts", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Moong Dal Halwa", Dietary.Veg, "Desserts", "Rajasthani"),
                CateringItemEntity(UUID.randomUUID().toString(), "Warm Chocolate Brownie", Dietary.Veg, "Desserts", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Kesari Phirni", Dietary.Veg, "Desserts", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Tiramisu Cups", Dietary.Veg, "Desserts", "Italian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Fresh Fruit Cream", Dietary.Veg, "Desserts", "Global"),
                CateringItemEntity(UUID.randomUUID().toString(), "Shahi Tukda", Dietary.Veg, "Desserts", "Awadhi"),
                CateringItemEntity(UUID.randomUUID().toString(), "Vanilla Bean Ice Cream", Dietary.Veg, "Desserts", "Global")
            )

            // Casual & Social Events: Birthday Party, Family Gathering, Reunion, Farewell
            normalizedType.contains("birthday") ||
                    normalizedType.contains("family") ||
                    normalizedType.contains("reunion") ||
                    normalizedType.contains("farewell") -> listOf(
                CateringItemEntity(UUID.randomUUID().toString(), "Mini Burgers", Dietary.NonVeg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "French Fries", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Cheese Corn Balls", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Chilli Paneer", Dietary.Veg, "Main Course", "Indo-Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), "Chicken Hakka Noodles", Dietary.NonVeg, "Main Course", "Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), "Virgin Mojito", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Cold Coffee with Ice Cream", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Warm Chocolate Brownie", Dietary.Veg, "Desserts", "Continental")
            )

            // Formal/Corporate Events: Seminar, Exhibition, Meetup
            normalizedType.contains("seminar") ||
                    normalizedType.contains("exhibition") ||
                    normalizedType.contains("meetup") -> listOf(
                CateringItemEntity(UUID.randomUUID().toString(), "Hara Bhara Kebab", Dietary.Veg, "Starters", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Assorted Sandwiches", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Masala Lemonade", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Iced Peach Tea", Dietary.Veg, "Beverages", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Dal Makhani", Dietary.Veg, "Main Course", "Punjabi"),
                CateringItemEntity(UUID.randomUUID().toString(), "Paneer Lababdar", Dietary.Veg, "Main Course", "North Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Butter Naan", Dietary.Veg, "Main Course", "Indian Bread"),
                CateringItemEntity(UUID.randomUUID().toString(), "Fresh Fruit Cream", Dietary.Veg, "Desserts", "Global")
            )

            // Public/Entertainment Events: Concert, Cultural Event
            normalizedType.contains("concert") ||
                    normalizedType.contains("cultural") -> listOf(
                CateringItemEntity(UUID.randomUUID().toString(), "Crispy Chilli Potato", Dietary.Veg, "Starters", "Chinese"),
                CateringItemEntity(UUID.randomUUID().toString(), "Chicken 65", Dietary.NonVeg, "Starters", "South Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Assorted Soft Drinks", Dietary.Veg, "Beverages", "Global"),
                CateringItemEntity(UUID.randomUUID().toString(), "Sweet & Salt Lime Soda", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Veg Dum Biryani", Dietary.Veg, "Main Course", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Hyderabadi Chicken Biryani", Dietary.NonVeg, "Main Course", "Hyderabadi")
            )

            // Fallback: Others
            else -> listOf(
                CateringItemEntity(UUID.randomUUID().toString(), "Masala Lemonade", Dietary.Veg, "Beverages", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Cheese Corn Balls", Dietary.Veg, "Starters", "Continental"),
                CateringItemEntity(UUID.randomUUID().toString(), "Veg Dum Biryani", Dietary.Veg, "Main Course", "Indian"),
                CateringItemEntity(UUID.randomUUID().toString(), "Vanilla Bean Ice Cream", Dietary.Veg, "Desserts", "Global")
            )
        }
        addItems(defaultItems)
    }
}
