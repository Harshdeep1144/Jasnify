package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.CardData
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getCardData(eventId: String): Flow<CardData?>
    suspend fun saveCardData(eventId: String, data: CardData)
}
