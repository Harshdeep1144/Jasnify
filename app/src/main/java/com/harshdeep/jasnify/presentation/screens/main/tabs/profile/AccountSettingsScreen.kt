package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.cards.ProfileMenuCell
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@Composable
fun AccountSettingsScreen(
    email: String,
    isGoogleUser: Boolean,
    lastChangedText: String,
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Account Settings",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    title = "Email ID",
                    subtitle = email,
                    icon = painterResource(R.drawable.ic_mail),
                    hasBorder = false,
                    showArrow = false,
                    shape = RectangleShape,
                    containerColor = Color.Transparent
                )
                if (!isGoogleUser) {
                    ProfileMenuCell(
                        title = "Password",
                        subtitle = lastChangedText,
                        icon = painterResource(R.drawable.ic_key),
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = onChangePassword
                    )
                }
            }

            ProfileMenuCell(
                title = "Delete Account",
                subtitle = null,
                icon = painterResource(R.drawable.ic_delete),
                containerColor = SurfacePrimary,
                contentColor = MaterialTheme.colorScheme.error,
                onClick = onDeleteAccount
            )
        }
    }
}
