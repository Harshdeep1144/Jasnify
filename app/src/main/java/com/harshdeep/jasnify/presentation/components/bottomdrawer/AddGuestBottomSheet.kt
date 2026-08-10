package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.domain.model.GuestType
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape
import java.util.UUID

@Composable
fun AddGuestInfoBottomSheet(
    onDismiss: () -> Unit,
    onAddClick: (Guest) -> Unit,
    onAddNewTypeClick: () -> Unit,
    guestTypes: List<GuestType>,
    initialGuest: Guest? = null,
    onUploadPhoto: (Uri, String, (String) -> Unit) -> Unit = { _, _, _ -> },
    onRemovePhoto: (String) -> Unit = {},
    onProgress: (Float) -> Unit = {},
    onError: (String) -> Unit = {},
    hasToast: Boolean = false,
    toast: @Composable () -> Unit = {}
) {
    val guestId = remember(initialGuest) { initialGuest?.id ?: UUID.randomUUID().toString() }
    var name by remember(initialGuest) { mutableStateOf(initialGuest?.name ?: "") }
    var selectedType by remember(initialGuest) { mutableStateOf(initialGuest?.type ?: "") }
    var contactNo by remember(initialGuest) { mutableStateOf(initialGuest?.contactNo ?: "") }
    var imageUrl by remember(initialGuest) { mutableStateOf(initialGuest?.imageUrl) }
    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var prevSize by remember { mutableIntStateOf(guestTypes.size) }
    var isUploading by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            localImageUri = it
        }
    }

    // Automatically select the newly added guest type
    LaunchedEffect(guestTypes.size) {
        if (guestTypes.size > prevSize) {
            selectedType = guestTypes.last().name
        }
        prevSize = guestTypes.size
    }

    CustomBottomSheet(
        heading = if (initialGuest == null) "Add Guest Details" else "Edit Guest Details",
        onDismiss = onDismiss,
        onProgress = onProgress,
        showDragHandle = false,
        sheetHeight = null,
        hasToast = hasToast,
        toast = toast
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Upload Avatar Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(16.dp))
                // Avatar
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(ContentSecondary),
                    contentAlignment = Alignment.Center
                ){
                    val displayImage = localImageUri ?: imageUrl
                    if (displayImage != null) {
                        AsyncImage(
                            model = displayImage,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                            error = painterResource(id = R.drawable.ic_profile_placeholder)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_placeholder),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (isUploading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ContentPrimary.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = ContentInvPrimary,
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row {
                    if (localImageUri != null || imageUrl != null) {
                        CustomTextButton(
                            onClick = {
                                localImageUri = null
                                imageUrl?.let {
                                    onRemovePhoto(it)
                                    imageUrl = null
                                }
                            },
                            text = "Remove",
                            size = ButtonSize.Small,
                            type = ButtonType.Secondary,
                            shapeStyle = ButtonShapeStyle.Round,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error,
                            leadingIcon = painterResource(id = R.drawable.ic_delete),
                            enabled = !isUploading
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    
                    CustomTextButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        text = if (localImageUri == null && imageUrl == null) "Upload" else "Change",
                        size = ButtonSize.Small,
                        type = ButtonType.Secondary,
                        shapeStyle = ButtonShapeStyle.Round,
                        leadingIcon = painterResource(id = R.drawable.ic_upload),
                        enabled = !isUploading
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Name Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Guest Name",
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PrimaryInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Enter Guest's Name",
                )
            }

            Spacer(Modifier.height(12.dp))

            // Guest Type Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Guest Type",
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.weight(1f)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), SquircleShape(CornerLarge, CornerSmoothingDefault))
                                .clickable { expanded = true },
                            color = SurfaceSecondary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedType.ifEmpty { "Select Guest Type" },
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = if (selectedType.isEmpty()) ContentSecondary else ContentPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = ContentSecondary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Increased width for better visibility
                                .background(BackgroundPrimary)
                        ) {
                            guestTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = type.name,
                                            style = JasnifyTheme.typography.labelXLarge,
                                            color = ContentPrimary
                                        ) 
                                    },
                                    onClick = {
                                        selectedType = type.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    CustomTextButton(
                        onClick = onAddNewTypeClick,
                        text = "New",
                        size = ButtonSize.Medium,
                        type = ButtonType.Secondary,
                        shapeStyle = ButtonShapeStyle.Square,
                        leadingIcon = painterResource(id = R.drawable.ic_plus)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Contact Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Contact No. (optional)",
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PrimaryInput(
                    value = contactNo,
                    onValueChange = { contactNo = it },
                    placeholder = "Enter Contact No.",
                    keyboardType = KeyboardType.Phone,
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

        Column{
            CustomTextButton(
                onClick = {
                    if (name.isBlank()) {
                        onError("Please enter Guest's Name!")
                    } else if (selectedType.isBlank()) {
                        onError("Please select a suitable Guest Type!")
                    } else {
                        val handleFinalAction = { finalImageUrl: String? ->
                            onAddClick(
                                Guest(
                                    id = guestId,
                                    name = name,
                                    type = selectedType,
                                    contactNo = contactNo,
                                    imageUrl = finalImageUrl,
                                    invited = initialGuest?.invited ?: false,
                                    invitedBy = initialGuest?.invitedBy,
                                    invitedAt = initialGuest?.invitedAt,
                                    updatedBy = initialGuest?.updatedBy,
                                    updatedAt = initialGuest?.updatedAt,
                                    addedBy = initialGuest?.addedBy,
                                    addedAt = initialGuest?.addedAt
                                )
                            )
                        }

                        if (localImageUri != null) {
                            isUploading = true
                            onUploadPhoto(localImageUri!!, guestId) { uploadedUrl ->
                                isUploading = false
                                handleFinalAction(uploadedUrl)
                            }
                        } else {
                            handleFinalAction(imageUrl)
                        }
                    }
                },
                text = if (initialGuest == null) "Add Details" else "Update Details",
                modifier = Modifier.fillMaxWidth()
                    .padding(12.dp),
                shapeStyle = ButtonShapeStyle.Square,
                enabled = !isUploading
            )
        }
    }
}


@Composable
fun AddGuestTypeBottomSheet(
    onDismiss: () -> Unit,
    onAddType: (String) -> Unit,
    initialType: String? = null,
    onProgress: (Float) -> Unit = {},
    hasToast: Boolean = false,
    toast: @Composable () -> Unit = {}
) {
    var typeName by remember(initialType) { mutableStateOf(initialType ?: "") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    CustomBottomSheet(
        heading = if (initialType == null) "Add a Guest Type" else "Edit Guest Type",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        hasToast = hasToast,
        toast = toast
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryInput(
                value = typeName,
                onValueChange = { typeName = it },
                placeholder = "Close Friend",
                modifier = Modifier.focusRequester(focusRequester)
            )

            CustomTextButton(
                onClick = {
                    if (typeName.isNotBlank()) {
                        onAddType(typeName)
                    }
                },
                text = if (initialType == null) "Add Guest Type" else "Update Guest Type",
                modifier = Modifier.fillMaxWidth(),
                shapeStyle = ButtonShapeStyle.Square,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddGuestSheets() {
    JasnifyTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AddGuestInfoBottomSheet(
                onDismiss = {},
                onAddClick = {},
                onAddNewTypeClick = {},
                guestTypes = emptyList()
            )
            
            AddGuestTypeBottomSheet(
                onDismiss = {},
                onAddType = {}
            )
        }
    }
}
