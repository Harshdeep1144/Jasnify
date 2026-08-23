package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.cards.ProfileMenuCell
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary

@Composable
fun NotificationsScreen(
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val isEnabled by profileViewModel.isNotificationsEnabled.collectAsState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Notifications",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ProfileMenuCell(
                title = "Push Notifications",
                subtitle = if (isEnabled) "Receive updates and alerts" else "Notifications are currently off",
                icon = painterResource(R.drawable.ic_notification),
                hasBorder = true,
                showArrow = false,
                trailingContent = {
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { profileViewModel.toggleNotifications(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ContentPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f),
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }
            )
        }
    }
}
