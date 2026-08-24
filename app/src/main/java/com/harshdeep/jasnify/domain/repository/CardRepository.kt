package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.CardRoomData
import com.harshdeep.jasnify.domain.model.CardTheme
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getMyCards(eventId: String): Flow<List<CardData>>
    suspend fun saveMyCard(eventId: String, data: CardData)
    suspend fun deleteMyCard(eventId: String, cardId: String)

    fun getLikedCards(eventId: String): Flow<List<CardData>>
    suspend fun toggleLikedCard(eventId: String, data: CardData)

    fun getCardRoomData(eventId: String): Flow<CardRoomData?>
    suspend fun initializeCardRoom(eventId: String, defaultThemes: List<CardTheme>)
    suspend fun saveCardTheme(eventId: String, theme: CardTheme)
    suspend fun updateCardThemeName(eventId: String, themeId: String, newName: String)

    // Jasnify Global Cards
    fun getJasnifyCards(): Flow<List<CardData>>
    suspend fun saveJasnifyCard(data: CardData)
    suspend fun incrementCardShare(cardId: String, isJasnifyCard: Boolean)
    suspend fun updateCardLikes(cardId: String, isJasnifyCard: Boolean, newLikesCount: Int)
}
