package com.harshdeep.jasnify.presentation.screens.room

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel

@Composable
fun RoomAccessScreen(
    eventId: String,
    roomType: String,
    onBackClick: () -> Unit,
    roomViewModel: RoomViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUserUid = remember(auth.currentUser) { auth.currentUser?.uid.orEmpty() }

    val currentUserInRoom = remember(roomUsers, currentUserUid) {
        roomUsers.find { it.uid == currentUserUid }
    }

    val isOwner = remember(activeEvent, currentUserUid) {
        activeEvent?.ownerId == currentUserUid
    }
    
    val currentUserRole = remember(isOwner, currentUserInRoom) {
        when {
            isOwner -> UserRole.OWNER
            currentUserInRoom != null -> currentUserInRoom.role
            else -> UserRole.VIEWER
        }
    }

    LaunchedEffect(eventId, roomType) {
        roomViewModel.loadRoomUsers(eventId, roomType)
        eventViewModel.fetchUserEvents() // To get activeEvent for owner check
    }

    RoomScreen(
        allUsers = roomUsers,
        currentUserRole = currentUserRole,
        isSelf = { it.uid == currentUserUid },
        onBackClick = onBackClick,
        onMenuClick = { /* Not used in this context or handle if needed */ },
        onRoleChange = { user, newRole ->
            roomViewModel.updateRole(eventId, roomType, user, newRole)
        },
        onRemove = { user ->
            roomViewModel.removeAccess(eventId, roomType, user.uid)
        },
        onReport = { /* Handle report */ },
        onLeave = {
            roomViewModel.removeAccess(eventId, roomType, currentUserUid)
            onBackClick()
        },
        searchResults = searchResults,
        onSearch = { query -> roomViewModel.searchUsers(query) },
        onGrantAccess = { email, role ->
            roomViewModel.grantAccess(eventId, roomType, email, role)
        }
    )
}
