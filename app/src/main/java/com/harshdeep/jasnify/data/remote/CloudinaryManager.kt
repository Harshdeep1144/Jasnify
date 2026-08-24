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
        return uploadFromSource(uri, "jasnify/users/$userId", "profile_pic", "image")
    }

    suspend fun uploadProfilePictureFromUrl(url: String, userId: String): String {
        return uploadFromSource(url, "jasnify/users/$userId", "profile_pic", "image")
    }

    suspend fun uploadVenueReviewImage(uri: Uri, venueId: String): String {
        return uploadFromSource(uri, "jasnify/venues/$venueId/reviews", null, "image")
    }

    suspend fun uploadVendorReviewImage(uri: Uri, vendorId: String): String {
        return uploadFromSource(uri, "jasnify/vendors/$vendorId/reviews", null, "image")
    }

    suspend fun uploadGuestProfilePicture(uri: Uri, eventId: String, guestId: String): String {
        return uploadFromSource(uri, "jasnify/guests/$eventId", guestId, "image")
    }

    suspend fun uploadCardThemeImage(uri: Uri, eventId: String): String {
        return uploadFromSource(uri, "jasnify/cards/$eventId/themes", null, "image")
    }

    suspend fun uploadMoment(uri: Uri, eventId: String, folderPath: String, isVideo: Boolean): String {
        val subFolder = if (isVideo) "videos" else "images"
        val resourceType = if (isVideo) "video" else "image"
        // Sanitize folder path: remove leading/trailing slashes and double slashes
        val sanitizedPath = folderPath.trim('/').replace("//", "/")
        return uploadFromSource(uri, "jasnify/moments/$eventId/$sanitizedPath/$subFolder", null, resourceType)
    }

    private suspend fun uploadFromSource(source: Any, folder: String, publicId: String?, resourceType: String = "auto"): String {
        return suspendCancellableCoroutine { continuation ->
            val uploadRequest = when (source) {
                is Uri -> MediaManager.get().upload(source)
                is String -> MediaManager.get().upload(source)
                else -> throw IllegalArgumentException("Unsupported upload source type")
            }
            
            uploadRequest.option("folder", folder)
            uploadRequest.option("resource_type", resourceType)
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
                val resourceType = when {
                    url.contains("/video/") -> "video"
                    url.contains("/raw/") -> "raw"
                    else -> "image"
                }
                
                if (publicId != null) {
                    android.util.Log.d("CloudinaryManager", "Deleting resource: $publicId ($resourceType)")
                    
                    // Standard Uploader API destroy call (Supported on Android)
                    val result = MediaManager.get().cloudinary.uploader().destroy(
                        publicId, 
                        mapOf(
                            "resource_type" to resourceType,
                            "invalidate" to true
                        )
                    )
                    
                    android.util.Log.d("CloudinaryManager", "Delete result for $publicId: $result")
                }
            } catch (e: Exception) {
                android.util.Log.e("CloudinaryManager", "Error deleting from Cloudinary: ${e.message}")
            }
        }
    }

    private fun extractPublicId(url: String): String? {
        val uploadIndex = url.indexOf("/upload/")
        if (uploadIndex == -1) return null

        var publicIdPath = url.substring(uploadIndex + 8)
        
        // Remove version if present (e.g., v123456789/)
        val firstSlash = publicIdPath.indexOf("/")
        if (firstSlash != -1) {
            val potentialVersion = publicIdPath.substring(0, firstSlash)
            if (potentialVersion.startsWith("v") && potentialVersion.substring(1).all { it.isDigit() }) {
                publicIdPath = publicIdPath.substring(firstSlash + 1)
            }
        }

        val lastDot = publicIdPath.lastIndexOf(".")
        val publicId = if (lastDot != -1) publicIdPath.substring(0, lastDot) else publicIdPath
        
        return Uri.decode(publicId)
    }
}
