package com.harshdeep.jasnify.presentation.screens.moments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel

@Composable
fun MomentsRoomContent(
    eventId: String,
    onBackClick: () -> Unit,
    roomViewModel: RoomViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = remember(auth.currentUser) { auth.currentUser }
    val currentUserUid = remember(currentUser) { currentUser?.uid.orEmpty() }

    val isOwner = remember(activeEvent, currentUserUid) {
        activeEvent?.ownerId == currentUserUid && currentUserUid.isNotEmpty()
    }
    val currentUserInRoom = remember(roomUsers, currentUserUid) {
        roomUsers.find { it.uid == currentUserUid }
    }
    val currentUserRole = remember(isOwner, currentUserInRoom) {
        when {
            isOwner -> UserRole.OWNER
            currentUserInRoom != null -> currentUserInRoom.role
            else -> UserRole.VIEWER
        }
    }

    val displayUsers = remember(roomUsers, currentUser, currentUserUid, currentUserRole) {
        val inRoom = roomUsers.find { it.uid == currentUserUid }
        if (inRoom == null && currentUserUid.isNotEmpty()) {
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

    LaunchedEffect(eventId) {
        if (eventId.isNotBlank()) {
            roomViewModel.loadRoomUsers(eventId, "Moments")
            roomViewModel.loadRoomPicture(eventId, "Moments")
        }
    }

    RoomScreen(
        allUsers = displayUsers,
        currentUserRole = currentUserRole,
        isSelf = { it.uid == currentUserUid },
        roomTitle = "Moments Room",
        onBackClick = onBackClick,
        onMenuClick = { },
        onRoleChange = { user, role -> roomViewModel.updateRole(eventId, "Moments", user, role) },
        onRemove = { user -> roomViewModel.removeAccess(eventId, "Moments", user.uid) },
        onReport = { },
        onLeave = { roomViewModel.removeAccess(eventId, "Moments", currentUserUid) },
        searchResults = searchResults,
        onSearch = { roomViewModel.searchUsers(it) },
        onGrantAccess = { email, role -> roomViewModel.grantAccess(eventId, "Moments", email, role) },
        roomPictureUrl = roomViewModel.roomPictureUrl.collectAsStateWithLifecycle().value,
        onUploadRoomPicture = { uri ->
            roomViewModel.uploadRoomPicture(
                eventId = eventId,
                roomType = "Moments",
                uri = uri
            )
        },
        onDeleteRoomPicture = {
            roomViewModel.deleteRoomPicture(
                eventId = eventId,
                roomType = "Moments"
            )
        }
    )
}
