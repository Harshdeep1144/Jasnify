package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.buttons.*
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomAccessBottomSheet(
    onDismissRequest: () -> Unit,
    onGrantAccess: (String, UserRole) -> Unit,
    searchResults: List<User>,
    onSearch: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    CustomBottomSheet(
        heading = "Share Room Access",
        sheetState = sheetState,
        onDismiss = onDismissRequest,
        sheetHeight = 550.dp
    ) {
        RoomAccessBottomSheetContent(
            onDismissRequest = onDismissRequest,
            onGrantAccess = onGrantAccess,
            searchResults = searchResults,
            onSearch = onSearch
        )
    }
}


@Composable
fun RoomAccessBottomSheetContent(
    onDismissRequest: () -> Unit,
    onGrantAccess: (String, UserRole) -> Unit,
    searchResults: List<User>,
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedRole by remember { mutableStateOf(UserRole.VIEWER) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        PrimaryInput(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                selectedUser = null
                onSearch(it)
            },
            placeholder = "Email or username",
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
                        modifier = Modifier.background(SurfaceBrandSecondary, SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
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
                hasStroke = true
            )
            FilterChip(
                label = "Editor Access",
                isSelected = selectedRole == UserRole.EDITOR,
                onClick = { selectedRole = UserRole.EDITOR },
                leadingIcon = Icons.Outlined.Edit,
                shapeStyle = ChipShapeStyle.Round,
                hasStroke = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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
            shapeStyle = ButtonShapeStyle.Square,
        )
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
            .clip(SquircleShape(CornerLargeIncrease))
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
                Image(
                    painter = painterResource(id = R.drawable.ic_user_profile),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))

        // User Info (Name and @username)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.name,
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentPrimary,
            )
            Text(
                text = "@${user.username}",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RoomAccessBottomSheetPreview() {
    Column {
        val sampleUsers = listOf(
            User("Anand K.", "viratanand", "", "", UserRole.OWNER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80"),
            User("Steve R.", "captainamerica", "", "", UserRole.EDITOR, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=150&h=150&q=80"),
        )

        RoomAccessBottomSheetContent(
            onDismissRequest = {},
            onSearch = {},
            onGrantAccess = { _, _ ->

            },
            searchResults = sampleUsers
        )

    }
}