package com.harshdeep.jasnify.presentation.screens.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun CardsScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            Surface(
                color = BackgroundPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TopBarIconButton(
                        icon = TopIcon.Predefined.BACK,
                        onClick = onBackClick,
                        backgroundStyle = ButtonBackground.OPAQUE
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Cards",
                        style = JasnifyTheme.typography.headingLarge,
                        color = ContentPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.padding(22.dp))
                }
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Cards Feature coming soon", style = JasnifyTheme.typography.displaySmall)
        }
    }
}
