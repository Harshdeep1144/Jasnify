package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Contact
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.SearchBarType
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

private val SingleContactItemShape = SquircleShape(
    radius = CornerLargeIncrease,
    cornerSmoothing = CornerSmoothingDefault
)

private val TopContactItemShape = SquircleShape(
    topStart = CornerLargeIncrease,
    topEnd = CornerLargeIncrease,
    bottomStart = CornerExtraSmall,
    bottomEnd = CornerExtraSmall,
    cornerSmoothing = CornerSmoothingDefault
)

private val MiddleContactItemShape = SquircleShape(
    radius = CornerExtraSmall,
    cornerSmoothing = CornerSmoothingDefault
)

private val BottomContactItemShape = SquircleShape(
    topStart = CornerExtraSmall,
    topEnd = CornerExtraSmall,
    bottomStart = CornerLargeIncrease,
    bottomEnd = CornerLargeIncrease,
    cornerSmoothing = CornerSmoothingDefault
)

private val FooterCardShape = SquircleShape(
    radius = CornerLarge,
    cornerSmoothing = CornerSmoothingDefault
)

@Composable
fun ContactPickerBottomSheet(
    contacts: List<Contact>,
    hasPermission: Boolean,
    onPermissionRequest: () -> Unit,
    onDismiss: () -> Unit,
    onAddManuallyClick: () -> Unit,
    onContactsSelected: (List<Contact>, Boolean) -> Unit,
    existingGuestIdentifiers: Set<String> = emptySet(),
    onProgress: (Float) -> Unit = {},
    hasToast: Boolean = false,
    toast: @Composable () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedContacts = remember { mutableStateListOf<Contact>() }
    val selectedPhoneNumbers by remember {
        derivedStateOf { selectedContacts.map { it.phoneNumber }.toSet() }
    }

    var includePhoneNo by remember { mutableStateOf(true) }
    var currentSheetHeight by remember { mutableStateOf<Dp?>(620.dp) }

    val animatedSheetHeight by animateDpAsState(
        targetValue = currentSheetHeight ?: 1000.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "SheetHeightAnimation"
    )

    val mockContacts = remember {
        listOf(
            Contact("m1", "Kavita N.", "+91 98765 43210", null),
            Contact("m2", "Akriti R.", "+91 98765 43210", null),
            Contact("m3", "Tarun C.", "+91 98765 43210", null),
            Contact("m4", "Lina A.", "+91 98765 43210", null),
            Contact("m5", "Sameer N.", "+91 98765 43210", null),
            Contact("m6", "Rahul M.", "+91 98765 43210", null)
        )
    }

    val displayContacts = if (hasPermission) {
        remember(contacts, searchQuery, existingGuestIdentifiers) {
            val trimmedQuery = searchQuery.trim()
            val base = if (trimmedQuery.isEmpty()) {
                contacts
            } else {
                contacts.filter {
                    it.name.contains(trimmedQuery, ignoreCase = true) ||
                            it.phoneNumber.contains(trimmedQuery)
                }
            }

            if (existingGuestIdentifiers.isEmpty()) {
                base
            } else {
                base.filter { contact ->
                    val identifier = contact.name.lowercase() + contact.phoneNumber
                    !existingGuestIdentifiers.contains(identifier)
                }
            }
        }
    } else {
        mockContacts
    }

    CustomBottomSheet(
        heading = "Add Guests",
        onDismiss = onDismiss,
        onProgress = onProgress,
        showCloseButton = true,
        sheetGesturesEnabled = false,
        showDragHandle = true,
        sheetHeight = animatedSheetHeight,
        hasToast = hasToast,
        toast = toast
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextButton(
                    onClick = onAddManuallyClick,
                    text = "Add Manually",
                    leadingIcon = painterResource(id = R.drawable.ic_plus),
                    modifier = Modifier.weight(1f),
                    type = ButtonType.Secondary,
                    shapeStyle = ButtonShapeStyle.Round
                )

                CustomSearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    type = SearchBarType.COMPACT,
                    placeholder = "Search contacts..."
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .nestedScroll(remember {
                            object : NestedScrollConnection {
                                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                                    if (available.y < 0 && currentSheetHeight != null) {
                                        currentSheetHeight = null
                                    }
                                    return Offset.Zero
                                }
                            }
                        }),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(
                        items = displayContacts,
                        key = { _, contact -> contact.id + contact.phoneNumber },
                        contentType = { _, _ -> "contact_item" }
                    ) { index, contact ->
                        val isSelected = selectedPhoneNumbers.contains(contact.phoneNumber)

                        val itemShape = when {
                            displayContacts.size == 1 -> SingleContactItemShape
                            index == 0 -> TopContactItemShape
                            index == displayContacts.lastIndex -> BottomContactItemShape
                            else -> MiddleContactItemShape
                        }

                        ContactItem(
                            contact = contact,
                            isSelected = isSelected && hasPermission,
                            shape = itemShape,
                            isBlurred = !hasPermission,
                            onToggle = {
                                if (hasPermission) {
                                    if (isSelected) {
                                        selectedContacts.removeAll { it.phoneNumber == contact.phoneNumber }
                                    } else {
                                        selectedContacts.add(contact)
                                    }
                                } else {
                                    onPermissionRequest()
                                }
                            }
                        )
                    }
                }

                if (!hasPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomTextButton(
                            onClick = onPermissionRequest,
                            text = "View Your Contacts",
                            leadingIcon = painterResource(id = R.drawable.ic_eye_closed),
                            type = ButtonType.Primary,
                            shapeStyle = ButtonShapeStyle.Round
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(FooterCardShape)
                    .background(SurfaceSecondary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(FooterCardShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            includePhoneNo = !includePhoneNo
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_phone),
                            contentDescription = null,
                            tint = ContentSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Include Contact No.",
                            style = JasnifyTheme.typography.headingSmall,
                            color = ContentSecondary,
                        )
                    }

                    Switch(
                        checked = includePhoneNo,
                        onCheckedChange = { includePhoneNo = it },
                        modifier = Modifier
                            .height(24.dp)
                            .scale(0.8f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ContentInvPrimary,
                            checkedTrackColor = ContentBrand,
                            uncheckedThumbColor = ContentInvPrimary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )
                }

                CustomTextButton(
                    onClick = {
                        if (!hasPermission) {
                            onPermissionRequest()
                        } else if (selectedContacts.isEmpty()) {
                            onContactsSelected(emptyList(), includePhoneNo)
                        } else {
                            onContactsSelected(selectedContacts.toList(), includePhoneNo)
                            onDismiss()
                        }
                    },
                    text = if (hasPermission) "Import Selected (${selectedContacts.size})" else "Import Contacts",
                    modifier = Modifier.fillMaxWidth(),
                    type = ButtonType.Primary,
                    shapeStyle = ButtonShapeStyle.Square
                )
            }
        }
    }
}

@Composable
private fun ContactItem(
    contact: Contact,
    isSelected: Boolean,
    shape: Shape,
    isBlurred: Boolean = false,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceSecondary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onToggle()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .then(if (isBlurred) Modifier.blur(8.dp) else Modifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomChecker(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                activeColor = ContentPrimary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BackgroundSecondary)
            ) {
                if (!contact.photoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = contact.photoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile_placeholder),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentPrimary,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = contact.phoneNumber,
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewContactPickerBottomSheet() {
    val mockContacts = listOf(
        Contact(
            id = "1",
            name = "Kavita N.",
            phoneNumber = "+91 98765 43210",
            photoUri = null
        ),
        Contact(
            id = "2",
            name = "Akriti R.",
            phoneNumber = "+91 98765 43210",
            photoUri = null
        ),
        Contact(
            id = "3",
            name = "Tarun C.",
            phoneNumber = "+91 98765 43210",
            photoUri = null
        ),
        Contact(
            id = "4",
            name = "Lina A.",
            phoneNumber = "+91 98765 43210",
            photoUri = null
        ),
        Contact(
            id = "5",
            name = "Sameer N.",
            phoneNumber = "+91 98765 43210",
            photoUri = null
        ),
        Contact(
            id = "6",
            name = "Rahul M.",
            phoneNumber = "+91 98765 43210",
            photoUri = null
        )
    )

    JasnifyTheme {
        ContactPickerBottomSheet(
            contacts = mockContacts,
            hasPermission = true,
            onPermissionRequest = {},
            onDismiss = {},
            onAddManuallyClick = {},
            onContactsSelected = { _, _ -> },
            onProgress = {}
        )
    }
}