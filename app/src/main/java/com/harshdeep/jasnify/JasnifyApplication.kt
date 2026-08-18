package com.harshdeep.jasnify

import android.app.Application
import com.cloudinary.android.MediaManager
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JasnifyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase App Check for local development
        if (BuildConfig.DEBUG) {
            Firebase.initialize(this)
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance(),
            )
        }
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
