package com.harshdeep.jasnify

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder
import com.cloudinary.android.MediaManager
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JasnifyApplication: Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(this)

        // Subscribe to global notifications topic
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("JasnifyApp", "Subscribed to 'all' topic")
                }
            }

        // Initialize Firebase App Check for local development
        if (BuildConfig.DEBUG) {
            // For local development on your own machine
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            // Automatically attests legitimate users via Google Play
            Firebase.appCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
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

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
                add(coil.decode.SvgDecoder.Factory())
            }
            .build()
    }
}
