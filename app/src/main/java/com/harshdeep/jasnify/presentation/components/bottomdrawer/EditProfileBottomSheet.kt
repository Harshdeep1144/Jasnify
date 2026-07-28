package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.presentation.viewmodels.ProfileUpdateState
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    userName: String,
    userHandle: String,
    profilePic: Any,
    updateState: ProfileUpdateState = ProfileUpdateState.Idle,
    resetUpdateState: () -> Unit = {},
    onUpdateProfile: (String, String, Uri?, Boolean) -> Unit
) {
    var toastData by remember { mutableStateOf(ToastData()) }
    
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is ProfileUpdateState.Success) {
            toastData = ToastData(updateState.message, ToastType.SUCCESS)
            // We don't call resetUpdateState here immediately because parent might need it to close
        } else if (updateState is ProfileUpdateState.Error) {
            toastData = ToastData(updateState.message, ToastType.ERROR)
            resetUpdateState()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
        dragHandle = null,
        sheetGesturesEnabled = true,
    ) {
        val view = LocalView.current
        DisposableEffect(view) {
            var parent = view.parent
            var dialogWindow: android.view.Window? = null
            while (parent != null) {
                if (parent is DialogWindowProvider) {
                    dialogWindow = parent.window
                    break
                }
                parent = parent.parent
            }
            dialogWindow?.let { w ->
                val colorInt = SurfacePrimary.toArgb()
                w.navigationBarColor = colorInt
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    w.isNavigationBarContrastEnforced = false
                }
                val isLightBackground = ColorUtils.calculateLuminance(colorInt) > 0.5
                WindowCompat.getInsetsController(w, view).isAppearanceLightNavigationBars = isLightBackground
            }
            onDispose {}
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = toastData.message != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .zIndex(998f)
            ) {
                CustomToast(
                    message = toastData.message ?: "",
                    type = toastData.type
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(999f)
                    .clip(SquircleShape(CornerExtraLarge, CornerExtraLarge, 0.dp, 0.dp))
                    .background(SurfacePrimary)
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                        .width(56.dp)
                        .height(4.dp)
                        .background(ContentTertiary, shape = SquircleShape(100))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(12.dp, 0.dp, 12.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Edit Profile",
                        style = JasnifyTheme.typography.displayLarge,
                        color = ContentPrimary
                    )
                    TopBarIconButton(
                        backgroundStyle = ButtonBackground.OPAQUE,
                        icon = TopIcon.Predefined.CLOSE,
                        iconSize = 18.dp,
                        onClick = onDismiss
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(600.dp)) {
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
}

@Composable
fun EditProfileContent(
    userName: String,
    userHandle: String,
    profilePic: Any,
    isUpdating: Boolean = false,
    onUpdateProfile: (String, String, Uri?, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(userName) }
    var handle by remember { mutableStateOf(userHandle.removePrefix("@")) }
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
                        model = if (shouldRemovePhoto) R.drawable.ic_user_profile else (selectedImageUri ?: profilePic),
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
                        onClick = { 
                            selectedImageUri = null
                            shouldRemovePhoto = true
                        },
                        text = "Remove",
                        size = ButtonSize.Small,
                        leadingIcon = painterResource(R.drawable.ic_delete),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    CustomTextButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
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
                onClick = { onUpdateProfile(name, handle, selectedImageUri, shouldRemovePhoto) },
                text = if (isUpdating) "Uploading..." else "Update Profile",
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating
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