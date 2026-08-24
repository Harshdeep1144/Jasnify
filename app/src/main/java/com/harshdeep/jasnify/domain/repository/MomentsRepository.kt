package com.harshdeep.jasnify.domain.repository

import android.net.Uri
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface MomentsRepository {
    fun getFolders(eventId: String, parentId: String = ""): Flow<List<MomentFolder>>
    fun getMoments(eventId: String, folderId: String): Flow<List<Moment>>
    fun getAllMoments(eventId: String): Flow<List<Moment>>
    suspend fun createFolder(eventId: String, name: String, parentId: String = ""): String
    suspend fun uploadMoment(eventId: String, folderId: String, uri: Uri, isVideo: Boolean)
    suspend fun deleteMoment(eventId: String, folderId: String, momentId: String)
    suspend fun deleteFolder(eventId: String, folderId: String)
    suspend fun initializeRoom(eventId: String)
    fun getUserRole(eventId: String): Flow<UserRole>
    fun getSavedMoments(eventId: String): Flow<List<Moment>>
    suspend fun toggleSaveMoment(eventId: String, moment: Moment)
}
