package com.harshdeep.jasnify.data.remote

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class CloudinaryManager @Inject constructor() {
    suspend fun uploadProfilePicture(uri: Uri, userId: String): String {
        return uploadFromSource(uri, userId)
    }

    suspend fun uploadProfilePictureFromUrl(url: String, userId: String): String {
        return uploadFromSource(url, userId)
    }

    private suspend fun uploadFromSource(source: Any, userId: String): String {
        return suspendCancellableCoroutine { continuation ->
            val uploadRequest = when (source) {
                is Uri -> MediaManager.get().upload(source)
                is String -> MediaManager.get().upload(source)
                else -> throw IllegalArgumentException("Unsupported upload source type")
            }
            
            uploadRequest
                .option("folder", "jasnify/users/$userId")
                .option("public_id", "profile_pic")
                .option("overwrite", true)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String ?: ""
                        continuation.resume(url)
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resumeWithException(Exception(error.description))
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch()
        }
    }

    suspend fun deleteProfilePicture(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                val publicId = "jasnify/users/$userId/profile_pic"
                MediaManager.get().cloudinary.uploader().destroy(publicId, emptyMap<String, Any>())
            } catch (e: Exception) {
                e.printStackTrace()
                // We don't want to throw error if deletion fails as user might not have a pic yet
            }
        }
    }
}
