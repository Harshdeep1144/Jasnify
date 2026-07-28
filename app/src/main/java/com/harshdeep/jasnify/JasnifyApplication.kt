package com.harshdeep.jasnify

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JasnifyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Cloudinary
        val config: Map<String, Any> = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET,
        )
        try {
            MediaManager.init(this, config)
        } catch (_: Exception) {
            // Already initialized
        }
    }
}
