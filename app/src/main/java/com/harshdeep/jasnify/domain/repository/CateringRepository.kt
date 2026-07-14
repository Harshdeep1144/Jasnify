package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.data.local.CateringItemEntity
import kotlinx.coroutines.flow.Flow

interface CateringRepository {
    fun getAllCateringItems(): Flow<List<CateringItemEntity>>
    suspend fun addItem(item: CateringItemEntity)
    suspend fun addItems(items: List<CateringItemEntity>)
    suspend fun deleteItem(itemId: String)
    suspend fun seedDefaultItems(eventType: String)
}
