package com.harshdeep.jasnify.presentation.screens.venues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.SurfaceSecondary

@Composable
fun VenueRoomContent(
    eventId: String,
    roomViewModel: RoomViewModel,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onRemove: (User) -> Unit,
    onLeave: () -> Unit,
    onShowToast: (ToastData) -> Unit
) {
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = remember(auth.currentUser) { auth.currentUser }
    val currentUserUid = remember(currentUser) { currentUser?.uid.orEmpty() }

    val currentUserRole = remember(roomUsers, currentUserUid) {
        roomUsers.find { it.uid == currentUserUid }?.role ?: UserRole.VIEWER
    }

    val displayUsers = remember(roomUsers, currentUser, currentUserUid, currentUserRole) {
        val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
        if (currentUserInRoom == null && currentUserUid.isNotEmpty()) {
            val self = User(
                uid = currentUserUid,
                name = currentUser?.displayName ?: "Me",
                email = currentUser?.email.orEmpty(),
                role = currentUserRole,
                username = currentUser?.email?.substringBefore("@") ?: "me"
            )
            (listOf(self) + roomUsers).distinctBy { it.uid }
        } else {
            roomUsers
        }
    }

    androidx.compose.runtime.LaunchedEffect(eventId) {
        if (eventId.isNotBlank()) {
            roomViewModel.loadRoomPicture(eventId, "Venue")
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
                roomViewModel.updateRole(eventId, "Venue", targetUser, newRole)
            },
            onRemove = onRemove,
            onReport = { targetUser ->
                onShowToast(ToastData("${targetUser.name} reported", ToastType.DEFAULT))
            },
            onLeave = onLeave,
            searchResults = searchResults,
            onSearch = { roomViewModel.searchUsers(it) },
            onGrantAccess = { email, role ->
                roomViewModel.grantAccess(eventId, "Venue", email, role)
                onShowToast(ToastData("Access granted to $email", ToastType.SUCCESS))
            },
            roomPictureUrl = roomViewModel.roomPictureUrl.collectAsStateWithLifecycle().value,
            onUploadRoomPicture = { uri ->
                roomViewModel.uploadRoomPicture(
                    eventId = eventId,
                    roomType = "Venue",
                    uri = uri,
                    onSuccess = { onShowToast(ToastData("Room profile picture updated", ToastType.SUCCESS)) },
                    onError = { err -> onShowToast(ToastData(err, ToastType.ERROR)) }
                )
            },
            onDeleteRoomPicture = {
                roomViewModel.deleteRoomPicture(
                    eventId = eventId,
                    roomType = "Venue",
                    onSuccess = { onShowToast(ToastData("Room profile picture deleted", ToastType.SUCCESS)) },
                    onError = { err -> onShowToast(ToastData(err, ToastType.ERROR)) }
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}