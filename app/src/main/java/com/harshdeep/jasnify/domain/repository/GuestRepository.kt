package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.Guest
import kotlinx.coroutines.flow.Flow

interface GuestRepository {
    fun getGuests(eventId: String): Flow<List<Guest>>
    suspend fun addGuest(eventId: String, guest: Guest)
    suspend fun updateGuest(eventId: String, guest: Guest)
    suspend fun deleteGuest(eventId: String, guestId: String)
    suspend fun deleteMultipleGuests(eventId: String, guestIds: List<String>)
}
