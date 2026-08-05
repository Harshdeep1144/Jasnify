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
        return uploadFromSource(uri, "jasnify/users/$userId", "profile_pic")
    }

    suspend fun uploadProfilePictureFromUrl(url: String, userId: String): String {
        return uploadFromSource(url, "jasnify/users/$userId", "profile_pic")
    }

    suspend fun uploadVenueReviewImage(uri: Uri, venueId: String): String {
        return uploadFromSource(uri, "jasnify/venues/$venueId/reviews", null)
    }

    suspend fun uploadVendorReviewImage(uri: Uri, vendorId: String): String {
        return uploadFromSource(uri, "jasnify/vendors/$vendorId/reviews", null)
    }

    private suspend fun uploadFromSource(source: Any, folder: String, publicId: String?): String {
        return suspendCancellableCoroutine { continuation ->
            val uploadRequest = when (source) {
                is Uri -> MediaManager.get().upload(source)
                is String -> MediaManager.get().upload(source)
                else -> throw IllegalArgumentException("Unsupported upload source type")
            }
            
            uploadRequest.option("folder", folder)
            if (publicId != null) {
                uploadRequest.option("public_id", publicId)
                uploadRequest.option("overwrite", true)
            }
            
            uploadRequest
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
            }
        }
    }

    suspend fun deleteImageByUrl(url: String) {
        withContext(Dispatchers.IO) {
            try {
                val publicId = extractPublicId(url)
                if (publicId != null) {
                    MediaManager.get().cloudinary.uploader().destroy(publicId, emptyMap<String, Any>())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun extractPublicId(url: String): String? {
        val uploadIndex = url.indexOf("/upload/")
        if (uploadIndex == -1) return null

        val subStr = url.substring(uploadIndex + 8)
        val firstSlash = subStr.indexOf("/")
        if (firstSlash == -1) return null

        val afterVersion = subStr.substring(firstSlash + 1)
        val lastDot = afterVersion.lastIndexOf(".")
        return if (lastDot != -1) afterVersion.substring(0, lastDot) else afterVersion
    }
}
