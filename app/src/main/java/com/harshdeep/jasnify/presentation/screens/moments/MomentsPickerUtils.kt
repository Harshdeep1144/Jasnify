package com.harshdeep.jasnify.presentation.screens.moments

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

internal fun pickMultiFromGooglePhotos(
    context: Context,
    onLaunchPickerIntent: (Intent) -> Unit,
    onFallback: () -> Unit
) {
    val googlePhotosPackage = "com.google.android.apps.photos"
    val pm = context.packageManager
    val isAppInstalled = try {
        pm.getPackageInfo(googlePhotosPackage, 0)
        true
    } catch (_: Exception) {
        false
    }

    if (isAppInstalled) {
        try {
            val pickIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                setPackage(googlePhotosPackage)
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            onLaunchPickerIntent(pickIntent)
            return
        } catch (_: Exception) {
            try {
                val actionPickIntent = Intent(Intent.ACTION_PICK).apply {
                    setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*,video/*")
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    setPackage(googlePhotosPackage)
                }
                onLaunchPickerIntent(actionPickIntent)
                return
            } catch (_: Exception) {
                // Fallback
            }
        }
    }

    try {
        val webIntent = Intent(Intent.ACTION_VIEW, "https://photos.google.com/".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(webIntent)
    } catch (_: Exception) {
        onFallback()
    }
}

internal fun openICloudPhotos(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, "https://www.icloud.com/photos".toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        android.util.Log.e("MomentsScreen", "Failed to open iCloud web: ${e.message}")
    }
}
