package com.harshdeep.jasnify.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RoomAccessDao {
    @Query("SELECT * FROM room_access WHERE eventId = :eventId AND roomType = :roomType AND uid = :uid")
    suspend fun getAccess(eventId: String, roomType: String, uid: String): RoomAccessEntity?

    @Upsert
    suspend fun insertAccess(access: RoomAccessEntity)

    @Query("DELETE FROM room_access")
    suspend fun clearAllAccess()
}
