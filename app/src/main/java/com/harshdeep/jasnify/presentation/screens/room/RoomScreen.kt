package com.harshdeep.jasnify.presentation.screens.room
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.room.RoomAccessBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.room.RoomProfileBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.UserListItem
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    allUsers: List<User>,
    currentUserRole: UserRole,
    isSelf: (User) -> Boolean,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onRoleChange: (User, UserRole) -> Unit,
    onRemove: (User) -> Unit,
    onReport: (User) -> Unit,
    onLeave: () -> Unit,
    roomTitle: String = "",
    searchResults: List<User> = emptyList(),
    onSearch: (String) -> Unit = {},
    onGrantAccess: (String, UserRole) -> Unit = { _, _ -> },
    roomPictureUrl: String? = null,
    onUploadRoomPicture: (Uri) -> Unit = {},
    onDeleteRoomPicture: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUploadRoomPicture(uri)
        }
    }

    SetStatusBarTheme(useDarkIcons = true, statusBarColor = Color.Transparent)

    var searchQuery by remember { mutableStateOf("") }
    var showProfileBottomSheet by remember { mutableStateOf(false) }
    var showAccessBottomSheet by remember { mutableStateOf(false) }
    var showRoomPictureBottomSheet by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            showProfileBottomSheet || showAccessBottomSheet || showRoomPictureBottomSheet
        }
    }

    val targetScale = if (isAnyBottomSheetOpen) {
        0.92f + (0.08f * sheetMotionProgress)
    } else {
        1.0f
    }

    val backdropScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadius by animateDpAsState(
        targetValue = if (isAnyBottomSheetOpen) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    val isAdmin = currentUserRole == UserRole.OWNER

    val filteredUsers = remember(allUsers, searchQuery) {
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            allUsers
        } else {
            allUsers.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.username.contains(query, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusManager.clearFocus()
                        }
                ) {
                    CustomTopBar(
                        title = "Manage Room Access",
                        onBackClick = onBackClick,
                        onMenuClick = if(!isAdmin) onMenuClick else null,
                        backIcon = TopIcon.Predefined.BACK,
                        buttonStyle = ButtonBackground.TRANSLUCENT,
                        translucentAlpha = 0.5f
                    )
                }
            },
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = backdropScale
                    scaleY = backdropScale
                    clip = isAnyBottomSheetOpen || backdropCornerRadius > 0.dp
                    shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                },
            containerColor = BackgroundSecondary,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            color = SurfacePrimary,
                            tonalElevation = 2.dp
                        ) {
                            if (!roomPictureUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = roomPictureUrl,
                                    contentDescription = "Room Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.img_profile_placeholder),
                                    contentDescription = "Default Room Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        val canEditRoomPicture = currentUserRole == UserRole.OWNER || currentUserRole == UserRole.EDITOR

                        if (canEditRoomPicture) {
                            CustomIconButton(
                                onClick = {
                                    showRoomPictureBottomSheet = true
                                },
                                icon = painterResource(R.drawable.ic_edit_pen),
                                size = ButtonSize.Small,
                                contentColor = ContentInvPrimary,
                                containerColor = Color.Black.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                if (roomTitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = roomTitle,
                        style = JasnifyTheme.typography.headingXLarge,
                        color = ContentPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomSearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search a user",
                        backgroundColor = SurfacePrimary,
                        modifier = Modifier.weight(1f)
                    )

                    if (isAdmin) {
                        CustomIconButton(
                            onClick = {
                                focusManager.clearFocus()
                                showAccessBottomSheet = true
                            },
                            icon = painterResource(R.drawable.ic_plus),
                            type = ButtonType.Tertiary,
                            enabled = true,
                            containerColor = SurfacePrimary,
                            modifier = Modifier.width(84.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CornerExtraLarge))
                        .background(Color.Transparent)
                        .navigationBarsPadding(),
                ) {
                    itemsIndexed(
                        items = filteredUsers,
                        key = { _, user -> user.uid },
                        contentType = { _, _ -> "user_list_item" }
                    ) { index, user ->
                        val isFirst = index == 0
                        val isLast = index == filteredUsers.size - 1

                        val itemShape = when {
                            isFirst && isLast -> RoundedCornerShape(CornerExtraLarge)
                            isFirst -> RoundedCornerShape(
                                topStart = CornerExtraLarge,
                                topEnd = CornerExtraLarge
                            )
                            isLast -> RoundedCornerShape(
                                bottomStart = CornerExtraLarge,
                                bottomEnd = CornerExtraLarge
                            )
                            else -> RectangleShape
                        }

                        UserListItem(
                            user = user,
                            shape = itemShape,
                            onClick = {
                                selectedUser = user
                                showProfileBottomSheet = true
                            },
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
        }

        if (showAccessBottomSheet) {
            focusManager.clearFocus()
            RoomAccessBottomSheet(
                searchResults = searchResults,
                onSearch = onSearch,
                onGrantAccess = { email, role ->
                    onGrantAccess(email, role)
                },
                onDismissRequest = {
                    showAccessBottomSheet = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showProfileBottomSheet && selectedUser != null) {
            focusManager.clearFocus()
            val activeUser = remember(selectedUser, allUsers) {
                allUsers.find { it.uid == selectedUser?.uid } ?: selectedUser!!
            }
            RoomProfileBottomSheet(
                user = activeUser,
                isSelf = isSelf(activeUser),
                currentUserRole = currentUserRole,
                onDismissRequest = {
                    showProfileBottomSheet = false
                    selectedUser = null
                },
                onRoleChange = { newRole ->
                    onRoleChange(activeUser, newRole)
                },
                onRemove = {
                    showProfileBottomSheet = false
                    onRemove(activeUser)
                    selectedUser = null
                },
                onReport = {
                    showProfileBottomSheet = false
                    onReport(activeUser)
                    selectedUser = null
                },
                onLeave = {
                    showProfileBottomSheet = false
                    onLeave()
                    selectedUser = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showRoomPictureBottomSheet) {
            focusManager.clearFocus()

            val uploadIcon = painterResource(R.drawable.ic_upload)
            val deleteIcon = painterResource(R.drawable.ic_delete)
            val deleteColor = MaterialTheme.colorScheme.error

            val roomPictureMenuItems = remember(roomPictureUrl, uploadIcon, deleteIcon, deleteColor) {
                val list = mutableListOf<List<MenuSheetActionItem>>()

                list.add(
                    listOf(
                        MenuSheetActionItem(
                            text = "Choose from Gallery",
                            icon = uploadIcon,
                            iconPlacement = IconPlacement.Left,
                            onClick = {
                                showRoomPictureBottomSheet = false
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                    )
                )

                if (!roomPictureUrl.isNullOrBlank()) {
                    list.add(
                        listOf(
                            MenuSheetActionItem(
                                text = "Delete Picture",
                                icon = deleteIcon,
                                contentColor = deleteColor,
                                iconPlacement = IconPlacement.Left,
                                onClick = {
                                    showRoomPictureBottomSheet = false
                                    onDeleteRoomPicture()
                                }
                            )
                        )
                    )
                }

                list
            }

            MenuBottomSheet(
                items = roomPictureMenuItems,
                onCancelClick = { showRoomPictureBottomSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}

// ============================================= Preview ======================================


private val previewUsers = listOf(
    User(
        uid = "1",
        name = "Harsh Deep",
        username = "harshdeep",
        email = "harsh@example.com",
        role = UserRole.OWNER,
        profilePictureUrl = null
    ),
    User(
        uid = "2",
        name = "Jane Doe",
        username = "janedoe",
        email = "jane@example.com",
        role = UserRole.EDITOR,
        profilePictureUrl = null
    ),
    User(
        uid = "3",
        name = "Alex Smith",
        username = "alexsmith",
        email = "alex@example.com",
        role = UserRole.VIEWER,
        profilePictureUrl = null
    )
)

@Preview(name = "Room Screen - Member View", showBackground = true, showSystemUi = true)
@Composable
fun RoomScreenMemberPreview() {
    JasnifyTheme {
        RoomScreen(
            allUsers = previewUsers,
            currentUserRole = UserRole.VIEWER,
            roomTitle = "Budget Room",
            isSelf = { it.uid == "3" },
            onBackClick = {},
            onMenuClick = {},
            onRoleChange = { _, _ -> },
            onRemove = {},
            onReport = {},
            onLeave = {},
            roomPictureUrl = "https://example.com/avatar.jpg",
            searchResults = emptyList()
        )
    }
}