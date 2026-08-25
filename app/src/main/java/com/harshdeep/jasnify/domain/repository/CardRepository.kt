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
    suspend fun saveGlobalCardTheme(theme: CardTheme)
    suspend fun updateCardThemeName(eventId: String, themeId: String, newName: String)
    fun getJasnifyCards(): Flow<List<CardData>>
    fun getGlobalCardThemes(): Flow<List<CardTheme>>
    suspend fun saveJasnifyCard(data: CardData)
    suspend fun saveJasnifyCards(cards: List<CardData>)
    suspend fun incrementCardShare(cardId: String, isJasnifyCard: Boolean)
    suspend fun toggleJasnifyCardLike(cardId: String, userId: String, shouldLike: Boolean)
    fun checkIsCardsAdmin(uid: String): Flow<Map<String, Any>?>
}