package com.harshdeep.jasnify.presentation.components.bottomdrawer.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.viewmodels.ProfileUpdateState
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    onDismiss: () -> Unit,
    userName: String,
    userHandle: String,
    profilePic: Any,
    updateState: ProfileUpdateState = ProfileUpdateState.Idle,
    resetUpdateState: () -> Unit = {},
    onUpdateProfile: (String, String, Uri?, Boolean) -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    var toastData by remember { mutableStateOf(ToastData()) }
    var activeToastData by remember { mutableStateOf<ToastData?>(null) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            if (toastData.type == ToastType.ERROR) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (toastData.message?.contains("Please", ignoreCase = true) == true ||
                    toastData.message?.contains("empty", ignoreCase = true) == true ||
                    toastData.message?.contains("Username", ignoreCase = true) == true
                ) {
                    delay(80.milliseconds)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            activeToastData = toastData
            delay(2000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is ProfileUpdateState.Success) {
            toastData = ToastData("Profile Updated!", ToastType.SUCCESS)
        } else if (updateState is ProfileUpdateState.Error) {
            toastData = ToastData(updateState.message, ToastType.ERROR)
            resetUpdateState()
        }
    }

    CustomBottomSheet(
        heading = "Edit Profile",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = false,
        showCloseButton = true,
        hasToast = toastData.message != null,
        toast = {
            AnimatedVisibility(
                visible = toastData.message != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(12.dp)
            ) {
                activeToastData?.let { data ->
                    CustomToast(
                        message = data.message ?: "",
                        type = data.type
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
            ) {
                EditProfileContent(
                    userName = userName,
                    userHandle = userHandle,
                    profilePic = profilePic,
                    isUpdating = updateState is ProfileUpdateState.Loading,
                    onUpdateProfile = { n, h, uri, remove ->
                        if (n.isBlank()) {
                            toastData = ToastData("Name cannot be empty", ToastType.ERROR)
                        } else if (h.isBlank()) {
                            toastData = ToastData("Username cannot be empty", ToastType.ERROR)
                        } else {
                            onUpdateProfile(n, h, uri, remove)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EditProfileContent(
    userName: String,
    userHandle: String,
    profilePic: Any,
    isUpdating: Boolean = false,
    onUpdateProfile: (String, String, Uri?, Boolean) -> Unit
) {
    var name by remember(userName) { mutableStateOf(userName) }
    var handle by remember(userHandle) { mutableStateOf(userHandle.removePrefix("@")) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var shouldRemovePhoto by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            shouldRemovePhoto = false
        }
    }

    val (displayImage, hasImageToRemove) = remember(selectedImageUri, shouldRemovePhoto, profilePic) {
        val resolvedImage = when {
            shouldRemovePhoto -> R.drawable.ic_user_profile
            selectedImageUri != null -> selectedImageUri
            else -> profilePic
        }
        val canRemove = (selectedImageUri != null || (profilePic != R.drawable.ic_user_profile && profilePic != "")) && !shouldRemovePhoto
        Pair(resolvedImage, canRemove)
    }

    val hasChanges = remember(name, handle, selectedImageUri, shouldRemovePhoto, userName, userHandle) {
        name != userName || handle != userHandle.removePrefix("@") || selectedImageUri != null || shouldRemovePhoto
    }

    Column(
        modifier = Modifier.fillMaxSize()
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
                        .background(SurfaceSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = displayImage,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_user_profile),
                        error = painterResource(R.drawable.ic_user_profile)
                    )

                    if (isUpdating) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = ContentInvPrimary,
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons for Profile Picture
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasImageToRemove) {
                        CustomTextButton(
                            onClick = {
                                selectedImageUri = null
                                shouldRemovePhoto = true
                            },
                            text = "Remove",
                            size = ButtonSize.Small,
                            leadingIcon = painterResource(R.drawable.ic_delete),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error,
                            enabled = !isUpdating
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    CustomTextButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        text = if (displayImage == R.drawable.ic_user_profile) "Upload" else "Change",
                        size = ButtonSize.Small,
                        leadingIcon = painterResource(R.drawable.ic_upload),
                        type = ButtonType.Secondary,
                        enabled = !isUpdating
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
                    placeholder = "Enter your name",
                    readOnly = isUpdating
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
                    placeholder = "Enter username",
                    readOnly = isUpdating
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
                onClick = { onUpdateProfile(name, handle, selectedImageUri, shouldRemovePhoto) },
                text = "Update Profile",
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier.fillMaxWidth(),
                isLoading = isUpdating,
                enabled = hasChanges && !isUpdating
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
            isUpdating = false,
            onUpdateProfile = { _, _, _, _ -> }
        )
    }
}