package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.buttons.*
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomAccessBottomSheet(
    onDismissRequest: () -> Unit,
    onGrantAccess: (String, UserRole) -> Unit,
    searchResults: List<User>,
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedRole by remember { mutableStateOf(UserRole.VIEWER) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    CustomBottomSheet(
        heading = "Share Room Access",
        sheetState = sheetState,
        onDismiss = onDismissRequest,
        sheetHeight = 550.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            CustomSearchBar(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    selectedUser = null
                    onSearch(it)
                },
                placeholder = "Email or username",
                backgroundColor = SurfaceSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            // Dynamic results area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                if (searchQuery.isNotEmpty() && selectedUser == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(vertical = 8.dp)
                    ) {
                        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                            items(searchResults) { user ->
                                UserSearchItem(
                                    user = user,
                                    onClick = {
                                        selectedUser = user
                                        searchQuery = user.email
                                    }
                                )
                            }
                        }
                        
                        if (searchResults.isEmpty() && searchQuery.contains("@")) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceSecondary.copy(alpha = 0.5f))
                                    .clickable {
                                        selectedUser = User(
                                            name = searchQuery.substringBefore("@"),
                                            email = searchQuery,
                                            username = searchQuery.substringBefore("@"),
                                            role = UserRole.VIEWER
                                        )
                                    }
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.PersonAddAlt,
                                        contentDescription = null,
                                        tint = ContentBrandDark
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = "Invite '$searchQuery'",
                                        color = ContentPrimary,
                                        style = JasnifyTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                } else if (selectedUser != null) {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        UserSearchItem(
                            user = selectedUser!!,
                            onClick = { selectedUser = null },
                            modifier = Modifier.background(SurfaceBrandSecondary, RoundedCornerShape(12.dp))
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "Viewer Access",
                    isSelected = selectedRole == UserRole.VIEWER,
                    onClick = { selectedRole = UserRole.VIEWER },
                    leadingIcon = Icons.Outlined.Visibility,
                    shapeStyle = ChipShapeStyle.Round,
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    label = "Editor Access",
                    isSelected = selectedRole == UserRole.EDITOR,
                    onClick = { selectedRole = UserRole.EDITOR },
                    leadingIcon = Icons.Outlined.Edit,
                    shapeStyle = ChipShapeStyle.Round,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextButton(
                text = "Grant Access",
                onClick = {
                    val email = selectedUser?.email ?: searchQuery
                    if (email.isNotEmpty()) {
                        onGrantAccess(email, selectedRole)
                        onDismissRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                size = ButtonSize.Large,
                containerColor = Color(0xFF5B7876) // Matching the dark teal color in image
            )
        }
    }
}

@Composable
fun UserSearchItem(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceSecondary),
            contentAlignment = Alignment.Center
        ) {
            if (user.profilePictureUrl != null) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = user.name.take(1).uppercase(),
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))


        Column {
            Text(
                text = user.name,
                style = JasnifyTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = ContentPrimary
            )
            Text(
                text = "@${user.username}",
                style = JasnifyTheme.typography.bodyMedium,
                color = ContentSecondary
            )
        }
    }
}
