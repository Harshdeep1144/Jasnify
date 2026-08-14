package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.CardData
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getMyCards(eventId: String): Flow<List<CardData>>
    suspend fun saveMyCard(eventId: String, data: CardData)
    suspend fun deleteMyCard(eventId: String, cardId: String)

    fun getLikedCards(eventId: String): Flow<List<CardData>>
    suspend fun toggleLikedCard(eventId: String, data: CardData)
}
