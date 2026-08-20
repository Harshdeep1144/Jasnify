package com.harshdeep.jasnify.domain.repository

import android.net.Uri
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import kotlinx.coroutines.flow.Flow

interface MomentsRepository {
    fun getFolders(eventId: String): Flow<List<MomentFolder>>
    fun getMoments(eventId: String, folderId: String): Flow<List<Moment>>
    fun getAllMoments(eventId: String): Flow<List<Moment>>
    suspend fun createFolder(eventId: String, name: String): String
    suspend fun uploadMoment(eventId: String, folderId: String, uri: Uri, isVideo: Boolean)
    suspend fun initializeRoom(eventId: String)
}
