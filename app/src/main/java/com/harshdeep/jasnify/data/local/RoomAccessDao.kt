package com.harshdeep.jasnify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoomAccessDao {
    @Query("SELECT * FROM room_access WHERE eventId = :eventId AND roomType = :roomType AND uid = :uid")
    suspend fun getAccess(eventId: String, roomType: String, uid: String): RoomAccessEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccess(access: RoomAccessEntity)

    @Query("DELETE FROM room_access")
    suspend fun clearAllAccess()
}
