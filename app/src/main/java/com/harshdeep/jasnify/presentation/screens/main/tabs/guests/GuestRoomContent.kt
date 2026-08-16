package com.harshdeep.jasnify.presentation.screens.main.tabs.guests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.SurfaceSecondary

@Composable
fun GuestRoomContent(
    eventId: String,
    roomViewModel: RoomViewModel,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onRemove: (User) -> Unit,
    onLeave: () -> Unit,
    onShowToast: (ToastData) -> Unit
) {
    val roomUsers by roomViewModel.roomUsers.collectAsState()
    val searchResults by roomViewModel.searchResults.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""

    val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
    val currentUserRole = currentUserInRoom?.role ?: UserRole.VIEWER

    val displayUsers = remember(roomUsers, currentUserUid) {
        if (currentUserInRoom == null && currentUserUid.isNotEmpty()) {
            val self = User(
                uid = currentUserUid,
                name = auth.currentUser?.displayName ?: "Me",
                email = auth.currentUser?.email ?: "",
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
            .background(SurfaceSecondary)
    ) {
        RoomScreen(
            allUsers = displayUsers,
            currentUserRole = currentUserRole,
            isSelf = { it.uid == currentUserUid },
            onBackClick = onBackClick,
            onMenuClick = onMenuClick,
            onRoleChange = { targetUser, newRole ->
                roomViewModel.updateRole(eventId, "Guest", targetUser, newRole)
            },
            onRemove = onRemove,
            onReport = { targetUser ->
                onShowToast(ToastData("${targetUser.name} reported", ToastType.DEFAULT))
            },
            onLeave = onLeave,
            searchResults = searchResults,
            onSearch = { roomViewModel.searchUsers(it) },
            onGrantAccess = { email, role ->
                roomViewModel.grantAccess(eventId, "Guest", email, role)
                onShowToast(ToastData("Access granted to $email", ToastType.SUCCESS))
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
