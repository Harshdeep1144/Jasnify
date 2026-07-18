package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun RoomAccessGuardian(
    hasAccess: Boolean?,
    roomName: String,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit
) {
    when (hasAccess) {
        true -> content()
        false -> {
            Scaffold(
                containerColor = BackgroundPrimary,
                topBar = {
                    Column(modifier = Modifier.statusBarsPadding()) {
                        CustomTopBar(
                            onBackClick = onBackClick,
                            backIcon = TopIcon.Predefined.BACK,
                            buttonStyle = ButtonBackground.OPAQUE
                        )
                    }
                },
                bottomBar = {
                    Column(modifier = Modifier.navigationBarsPadding()) {
                        CustomTextButton(
                            text = "Back to Home",
                            onClick = onBackClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            shapeStyle = ButtonShapeStyle.Square
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ill_block_access),
                            contentDescription = "Access Denied",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(100.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Oops, you don't have access to $roomName!",
                            style = JasnifyTheme.typography.displayMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomAccessGuardianPreview() {
    JasnifyTheme {
        RoomAccessGuardian(
            hasAccess = false,
            roomName = "Lounge",
            onBackClick = {},
            content = {}
        )
    }
}
