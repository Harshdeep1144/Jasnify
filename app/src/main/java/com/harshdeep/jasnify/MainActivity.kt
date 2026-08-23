package com.harshdeep.jasnify

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.harshdeep.jasnify.data.local.prefs.PreferenceManager
import com.harshdeep.jasnify.presentation.navigation.AppNavigation
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.theme.JasnifyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Sync FCM if toggle is ON (which it is by default)
            if (preferenceManager.isNotificationsEnabled()) {
                FirebaseMessaging.getInstance().subscribeToTopic("all")
            }
        } else {
            // Permission denied.
            FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.incrementSessionCount()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge(
            // icons color light
            // statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
            // icons color dark
          statusBarStyle = SystemBarStyle.Companion.light(Color.Companion.Transparent.toArgb(), Color.Companion.White.toArgb())
        )
        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val auth = FirebaseAuth.getInstance()
            
            // Keep user's lastActive status updated
            LaunchedEffect(auth.currentUser?.uid) {
                auth.currentUser?.uid?.let { uid ->
                    authViewModel.updateLastActive(uid, isMerchant = false)
                }
            }

            JasnifyTheme {
                AppNavigation()
            }
        }
    }

    fun triggerNotificationPermissionCheck() {
        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
            
            val auth = FirebaseAuth.getInstance()
            val isLoggedIn = auth.currentUser != null

            if (isGranted) {
                // If permission granted, ensure FCM is synced with preference
                if (preferenceManager.isNotificationsEnabled()) {
                    FirebaseMessaging.getInstance().subscribeToTopic("all")
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
                }
                return
            }

            // Only show the popup if user is logged in (Home screen requirement)
            if (!isLoggedIn) return

            val currentSession = preferenceManager.getSessionCount()
            val lastRequestSession = preferenceManager.getLastPermissionRequestSession()
            
            // Requirement 1: 1st time at login
            val isFirstLoginAsk = !preferenceManager.hasFirstLoginPermissionAsked()
            
            // Requirement 2: Every 3rd session
            val isThirdSessionAsk = currentSession - lastRequestSession >= 3
            
            if (isFirstLoginAsk || isThirdSessionAsk) {
                preferenceManager.setLastPermissionRequestSession(currentSession)
                if (isFirstLoginAsk) {
                    preferenceManager.setHasFirstLoginPermissionAsked(true)
                }
                
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Pre-Tiramisu: Always sync FCM based on toggle
            if (preferenceManager.isNotificationsEnabled()) {
                FirebaseMessaging.getInstance().subscribeToTopic("all")
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
            }
        }
    }
}
