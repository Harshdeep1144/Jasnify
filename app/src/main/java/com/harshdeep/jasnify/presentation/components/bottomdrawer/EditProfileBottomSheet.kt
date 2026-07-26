package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    userName: String,
    userHandle: String,
    profilePic: Any,
    onUpdateProfile: (String, String) -> Unit
) {
    CustomBottomSheet(
        heading = "Edit Profile",
        sheetState = sheetState,
        onDismiss = onDismiss,
        sheetHeight = 600.dp
    ) {
        EditProfileContent(
            userName = userName,
            userHandle = userHandle,
            profilePic = profilePic,
            onUpdateProfile = onUpdateProfile
        )
    }
}

@Composable
fun EditProfileContent(
    userName: String,
    userHandle: String,
    profilePic: Any,
    onUpdateProfile: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(userName) }
    var handle by remember { mutableStateOf(userHandle.removePrefix("@")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Section
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(SurfaceSecondary)
                ) {
                    AsyncImage(
                        model = profilePic,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_user_profile)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons for Profile Picture
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextButton(
                        onClick = { /* Remove Profile Pic */ },
                        text = "Remove",
                        size = ButtonSize.Small,
                        leadingIcon = painterResource(R.drawable.ic_delete),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    CustomTextButton(
                        onClick = { /* Upload Profile Pic */ },
                        text = "Upload",
                        size = ButtonSize.Small,
                        leadingIcon = painterResource(R.drawable.ic_upload),
                        type = ButtonType.Secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Name Input Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Name",
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PrimaryInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Enter your name"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Username Input Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Username",
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PrimaryInput(
                    value = handle,
                    onValueChange = { handle = it },
                    placeholder = "Enter username"
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = ContentSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Username can be changed only once in a month.",
                        style = JasnifyTheme.typography.bodyMedium,
                        color = ContentSecondary
                    )
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            CustomTextButton(
                onClick = { onUpdateProfile(name, handle) },
                text = "Update Profile",
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EditProfileBottomSheetPreview() {
    JasnifyTheme {
        EditProfileContent(
            userName = "Anand K.",
            userHandle = "@viratanand",
            profilePic = R.drawable.ic_user_profile,
            onUpdateProfile = { _, _ -> }
        )
    }
}