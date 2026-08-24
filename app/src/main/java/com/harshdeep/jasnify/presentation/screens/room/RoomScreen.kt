package com.harshdeep.jasnify.presentation.screens.room

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.RoomAccessBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.RoomProfileBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.UserListItem
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.R

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
    searchResults: List<User> = emptyList(),
    onSearch: (String) -> Unit = {},
    onGrantAccess: (String, UserRole) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    SetStatusBarTheme(useDarkIcons = true, statusBarColor = Color.Transparent)

    var searchQuery by remember { mutableStateOf("") }
    var showProfileBottomSheet by remember { mutableStateOf(false) }
    var showAccessBottomSheet by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            showProfileBottomSheet || showAccessBottomSheet
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
                    .padding(12.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
            ) {
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
                onDismissRequest = { showAccessBottomSheet = false },
                onGrantAccess = { email, role ->
                    onGrantAccess(email, role)
                    showAccessBottomSheet = false
                },
                searchResults = searchResults,
                onSearch = onSearch,
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showProfileBottomSheet && selectedUser != null) {
            val targetUser = selectedUser!!
            focusManager.clearFocus()
            RoomProfileBottomSheet(
                user = targetUser,
                currentUserRole = currentUserRole,
                isSelf = isSelf(targetUser),
                onDismissRequest = { showProfileBottomSheet = false },
                onRoleChange = { newRole ->
                    onRoleChange(targetUser, newRole)
                    showProfileBottomSheet = false
                },
                onRemove = {
                    onRemove(targetUser)
                    showProfileBottomSheet = false
                },
                onReport = {
                    onReport(targetUser)
                    showProfileBottomSheet = false
                },
                onLeave = {
                    onLeave()
                    showProfileBottomSheet = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}