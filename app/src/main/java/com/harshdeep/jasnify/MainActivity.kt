package com.harshdeep.jasnify

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.harshdeep.jasnify.presentation.navigation.AppNavigation
import com.harshdeep.jasnify.theme.JasnifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge(
            // icons color light
//           statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
            // icons color dark
          statusBarStyle = SystemBarStyle.Companion.light(Color.Companion.Transparent.toArgb(), Color.Companion.White.toArgb())
        )
        setContent {
            JasnifyTheme {
                AppNavigation()
            }
        }
    }
}