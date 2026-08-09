package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun ContactPickerBottomSheet(
    contacts: List<Contact>,
    onDismiss: () -> Unit,
    onAddManuallyClick: () -> Unit,
    onContactsSelected: (List<Contact>, Boolean) -> Unit,
    onProgress: (Float) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedContacts = remember { mutableStateListOf<Contact>() }
    var includePhoneNo by remember { mutableStateOf(true) }

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) {
            contacts
        } else {
            contacts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.phoneNumber.contains(searchQuery)
            }
        }
    }

    CustomBottomSheet(
        heading = "Add Guests",
        onDismiss = onDismiss,
        onProgress = onProgress,
        showCloseButton = true,
        sheetGesturesEnabled = false,
        showDragHandle = false,
        sheetHeight = 620.dp
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

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(
                    items = filteredContacts,
                    key = { _, contact -> contact.id + contact.phoneNumber }
                ) { index, contact ->
                    val isSelected = selectedContacts.any { it.phoneNumber == contact.phoneNumber }

                    val topRadius = if (index == 0) CornerLargeIncrease else CornerExtraSmall
                    val bottomRadius = if (index == filteredContacts.lastIndex) CornerLargeIncrease else CornerExtraSmall
                    val itemShape = SquircleShape(
                        topStart = topRadius,
                        topEnd = topRadius,
                        bottomStart = bottomRadius,
                        bottomEnd = bottomRadius,
                        cornerSmoothing = CornerSmoothingDefault
                    )

                    ContactItem(
                        contact = contact,
                        isSelected = isSelected,
                        shape = itemShape,
                        onToggle = {
                            if (isSelected) {
                                selectedContacts.removeAll { it.phoneNumber == contact.phoneNumber }
                            } else {
                                selectedContacts.add(contact)
                            }
                        }
                    )
                }
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(SquircleShape(radius = CornerLarge, cornerSmoothing = CornerSmoothingDefault))
                    .background(SurfaceSecondary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
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
                        onContactsSelected(selectedContacts.toList(), includePhoneNo)
                        onDismiss()
                    },
                    text = "Import Selected (${selectedContacts.size})",
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
            onDismiss = {},
            onAddManuallyClick = {},
            onContactsSelected = { _, _ -> },
            onProgress = {}
        )
    }
}