package com.harshdeep.jasnify.presentation.components.bottomdrawer

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun AddGuestInfoBottomSheet(
    onDismiss: () -> Unit,
    onAddClick: (Guest) -> Unit,
    onAddNewTypeClick: () -> Unit,
    guestTypes: List<GuestType>,
    initialGuest: Guest? = null,
    onProgress: (Float) -> Unit = {}
) {
    var name by remember(initialGuest) { mutableStateOf(initialGuest?.name ?: "") }
    var selectedType by remember(initialGuest) { mutableStateOf(initialGuest?.type ?: "") }
    var contactNo by remember(initialGuest) { mutableStateOf(initialGuest?.contactNo ?: "") }
    var imageUrl by remember(initialGuest) { mutableStateOf(initialGuest?.imageUrl) }

    CustomBottomSheet(
        heading = if (initialGuest == null) "Add Guest Info" else "Edit Guest Info",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null // Dynamic height
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Upload Avatar Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5EBEA))
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user_profile),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            tint = Color(0xFF635994).copy(alpha = 0.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextButton(
                    onClick = { /* Handle Image Upload */ },
                    text = "Upload",
                    size = ButtonSize.Small,
                    type = ButtonType.Secondary,
                    shapeStyle = ButtonShapeStyle.Round,
                    containerColor = Color(0xFFDEE9E8),
                    contentColor = Color(0xFF005858),
                    leadingIcon = painterResource(id = R.drawable.ic_upload)
                )
            }

            // Name Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Guest Name",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PrimaryInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Enter Guest's Name",
                    shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerLarge, CornerLarge, CornerSmoothingDefault)
                )
            }

            // Guest Type Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Guest Type",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.weight(1f)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(SquircleShape(CornerExtraSmall, CornerLarge, CornerLarge, CornerLarge, CornerSmoothingDefault))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                    SquircleShape(CornerExtraSmall, CornerLarge, CornerLarge, CornerLarge, CornerSmoothingDefault)
                                )
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
                        shapeStyle = ButtonShapeStyle.Round,
                        containerColor = Color(0xFFDEE9E8),
                        contentColor = Color(0xFF005858),
                        leadingIcon = painterResource(id = R.drawable.ic_plus)
                    )
                }
            }

            // Contact Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Contact No. (optional)",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PrimaryInput(
                    value = contactNo,
                    onValueChange = { contactNo = it },
                    placeholder = "Enter Contact No.",
                    keyboardType = KeyboardType.Phone,
                    shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerLarge, CornerLarge, CornerSmoothingDefault)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add Details Button
            CustomTextButton(
                onClick = {
                    onAddClick(
                        Guest(
                            id = initialGuest?.id ?: java.util.UUID.randomUUID().toString(),
                            name = name,
                            type = selectedType,
                            contactNo = contactNo,
                            imageUrl = imageUrl,
                            isInvited = initialGuest?.isInvited ?: false,
                            invitedBy = initialGuest?.invitedBy,
                            invitedAt = initialGuest?.invitedAt,
                            lastUpdatedBy = initialGuest?.lastUpdatedBy,
                            lastUpdatedAt = initialGuest?.lastUpdatedAt
                        )
                    )
                },
                text = if (initialGuest == null) "Add Details" else "Update Details",
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Medium,
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                containerColor = Color(0xFF5D7371), // Dark teal/slate from image
                contentColor = Color.White
            )
        }
    }
}

@Composable
fun AddGuestTypeBottomSheet(
    onDismiss: () -> Unit,
    onAddType: (String) -> Unit,
    onProgress: (Float) -> Unit = {}
) {
    var typeName by remember { mutableStateOf("") }

    CustomBottomSheet(
        heading = "Add a Guest Type",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PrimaryInput(
                value = typeName,
                onValueChange = { typeName = it },
                placeholder = "Close Friend",
                shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerLarge, CornerLarge, CornerSmoothingDefault)
            )

            CustomTextButton(
                onClick = {
                    if (typeName.isNotBlank()) {
                        onAddType(typeName)
                    }
                },
                text = "Add Guest Type",
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Medium,
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                containerColor = Color(0xFF5D7371),
                contentColor = Color.White
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
