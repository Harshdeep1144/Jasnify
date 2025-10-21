package com.harshdeep.jasnify.presentation.util

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

@Composable
fun SetStatusBarTheme(
    useDarkIcons: Boolean,
    statusBarColor: Color = Color.Transparent
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    LaunchedEffect(useDarkIcons, statusBarColor) {
        activity?.enableEdgeToEdge(
            statusBarStyle = if (useDarkIcons) {
                SystemBarStyle.light(statusBarColor.toArgb(), Color.White.toArgb())
            } else {
                SystemBarStyle.dark(statusBarColor.toArgb())
            }
        )
    }
}
