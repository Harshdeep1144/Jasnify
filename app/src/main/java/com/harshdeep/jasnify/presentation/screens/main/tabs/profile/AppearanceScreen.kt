package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AppThemeOption
import com.harshdeep.jasnify.presentation.components.bottomdrawer.NavBarStyleOption
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.cards.ProfileMenuCell
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@Composable
fun AppearanceScreen(
    currentTheme: AppThemeOption,
    currentNavBarStyle: NavBarStyleOption,
    onBack: () -> Unit,
    onChangeTheme: () -> Unit,
    onChangeNavBarStyle: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Appearance",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        SquircleShape(CornerLarge, CornerSmoothingDefault)
                    )
                    .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                    .background(SurfacePrimary)
            ) {
                ProfileMenuCell(
                    title = "Theme",
                    subtitle = currentTheme.label,
                    icon = painterResource(R.drawable.ic_paint),
                    hasBorder = false,
                    shape = RectangleShape,
                    containerColor = Color.Transparent,
                    onClick = onChangeTheme
                )
                ProfileMenuCell(
                    title = "Nav Bar Style",
                    subtitle = currentNavBarStyle.label,
                    icon = painterResource(R.drawable.ic_home),
                    hasBorder = false,
                    shape = RectangleShape,
                    containerColor = Color.Transparent,
                    onClick = onChangeNavBarStyle
                )
            }
        }
    }
}
