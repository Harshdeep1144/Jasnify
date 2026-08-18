package com.harshdeep.jasnify.presentation.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.SurfaceSecondary

@Composable
fun BudgetRoomContent(
    eventId: String,
    roomUsers: List<User>,
    currentUserUid: String,
    currentUserRole: UserRole,
    searchResults: List<User>,
    roomViewModel: RoomViewModel,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onRemoveClick: (User) -> Unit,
    onLeaveClick: () -> Unit,
    onToastShow: (ToastData) -> Unit
) {
    val displayUsers = remember(roomUsers, currentUserUid, currentUserRole) {
        if (roomUsers.none { it.uid == currentUserUid } && currentUserUid.isNotEmpty()) {
            val self = User(
                uid = currentUserUid,
                name = "Me",
                email = "",
                role = currentUserRole,
                username = "me"
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
                roomViewModel.updateRole(eventId, "Budget", targetUser, newRole)
            },
            onRemove = onRemoveClick,
            onReport = { targetUser ->
                onToastShow(ToastData("${targetUser.name} reported", ToastType.DEFAULT))
            },
            onLeave = onLeaveClick,
            searchResults = searchResults,
            onSearch = { roomViewModel.searchUsers(it) },
            onGrantAccess = { email, role ->
                roomViewModel.grantAccess(eventId, "Budget", email, role)
                onToastShow(ToastData("Access granted to $email", ToastType.SUCCESS))
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}