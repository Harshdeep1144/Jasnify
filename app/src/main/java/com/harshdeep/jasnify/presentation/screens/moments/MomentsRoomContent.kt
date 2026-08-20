package com.harshdeep.jasnify.presentation.screens.moments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel

@Composable
fun MomentsRoomContent(
    eventId: String,
    onBackClick: () -> Unit,
    roomViewModel: RoomViewModel = hiltViewModel()
) {
    val users by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(eventId) {
        roomViewModel.loadRoomUsers(eventId, "moments")
    }

    RoomScreen(
        allUsers = users,
        currentUserRole = users.find { it.uid == currentUser?.uid }?.role ?: UserRole.VIEWER,
        isSelf = { it.uid == currentUser?.uid },
        onBackClick = onBackClick,
        onMenuClick = { },
        onRoleChange = { user, role -> roomViewModel.updateRole(eventId, "moments", user, role) },
        onRemove = { user -> roomViewModel.removeAccess(eventId, "moments", user.uid) },
        onReport = { },
        onLeave = { roomViewModel.removeAccess(eventId, "moments", currentUser?.uid ?: "") },
        searchResults = searchResults,
        onSearch = { roomViewModel.searchUsers(it) },
        onGrantAccess = { email, role -> roomViewModel.grantAccess(eventId, "moments", email, role) }
    )
}
