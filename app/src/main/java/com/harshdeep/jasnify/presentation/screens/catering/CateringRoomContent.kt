package com.harshdeep.jasnify.presentation.screens.catering

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.BackgroundSecondary

@Composable
fun CateringRoomContent(
    eventId: String,
    roomViewModel: RoomViewModel,
    currentUserRole: UserRole,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onRemove: (User) -> Unit,
    onLeave: () -> Unit,
    onShowToast: (ToastData) -> Unit
) {
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUserUid = remember(auth.currentUser) { auth.currentUser?.uid.orEmpty() }

    val displayUsers = remember(roomUsers, currentUserUid, currentUserRole, auth.currentUser) {
        val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
        if (currentUserInRoom == null && currentUserUid.isNotEmpty()) {
            val self = User(
                uid = currentUserUid,
                name = auth.currentUser?.displayName ?: "Me",
                email = auth.currentUser?.email.orEmpty(),
                role = currentUserRole,
                username = auth.currentUser?.email?.substringBefore("@") ?: "me"
            )
            (listOf(self) + roomUsers).distinctBy { it.uid }
        } else {
            roomUsers
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSecondary)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        RoomScreen(
            allUsers = displayUsers,
            currentUserRole = currentUserRole,
            isSelf = { it.uid == currentUserUid },
            onBackClick = {
                onBackClick()
                focusManager.clearFocus()
            },
            onMenuClick = {
                onMenuClick()
                focusManager.clearFocus()
            },
            onRoleChange = { targetUser, newRole ->
                roomViewModel.updateRole(eventId, "Catering", targetUser, newRole)
            },
            onRemove = onRemove,
            onReport = { targetUser ->
                onShowToast(ToastData("${targetUser.name} reported", ToastType.DEFAULT))
            },
            onLeave = onLeave,
            searchResults = searchResults,
            onSearch = { roomViewModel.searchUsers(it) },
            onGrantAccess = { email, role ->
                roomViewModel.grantAccess(eventId, "Catering", email, role)
                onShowToast(ToastData("Access granted to $email", ToastType.SUCCESS))
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}