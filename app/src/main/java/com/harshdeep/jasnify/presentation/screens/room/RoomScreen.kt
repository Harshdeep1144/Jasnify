package com.harshdeep.jasnify.presentation.screens.room

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.bottomdrawer.RoomAccessBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.RoomProfileBottomSheet
import com.harshdeep.jasnify.presentation.components.cards.UserListItem
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundSecondary
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
    searchResults: List<User> = emptyList(),
    onSearch: (String) -> Unit = {},
    onGrantAccess: (String, UserRole) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    var showProfileBottomSheet by remember { mutableStateOf(false) }
    var showAccessBottomSheet by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    val isAdmin = currentUserRole == UserRole.OWNER

    // Efficiently filter users only when allUsers list or search query changes
    val filteredUsers = remember(allUsers, searchQuery) {
        allUsers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.username.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    menuIcon = if (isAdmin) TopIcon.Predefined.PLUS else TopIcon.Predefined.MENU_VERTICAL,
                    onMenuClick = {
                        if (isAdmin) {
                            showAccessBottomSheet = true
                        } else {
                            onMenuClick()
                        }
                    },
                    backIcon = TopIcon.Predefined.BACK,
                    buttonStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f
                )
            }
        },
        modifier = modifier.fillMaxSize()
            .statusBarsPadding(),
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
            CustomSearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search a user",
                backgroundColor = SurfacePrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // List of Users Container
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerExtraLarge))
                    .background(Color.Transparent)
                    .navigationBarsPadding(),
            ) {
                itemsIndexed(filteredUsers) { index, user ->
                    val isFirst = index == 0
                    val isLast = index == filteredUsers.size - 1

                    // Determine shape based on position in list
                    val itemShape = when {
                        isFirst && isLast -> RoundedCornerShape(CornerExtraLarge)
                        isFirst -> RoundedCornerShape(topStart = CornerExtraLarge, topEnd = CornerExtraLarge)
                        isLast -> RoundedCornerShape(bottomStart = CornerExtraLarge, bottomEnd = CornerExtraLarge)
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
            onSearch = onSearch
        )
    }

    if (showProfileBottomSheet && selectedUser != null) {
        focusManager.clearFocus()
        RoomProfileBottomSheet(
            user = selectedUser!!,
            currentUserRole = currentUserRole,
            isSelf = isSelf(selectedUser!!),
            onDismissRequest = { showProfileBottomSheet = false },
            onRoleChange = { newRole ->
                onRoleChange(selectedUser!!, newRole)
                showProfileBottomSheet = false
            },
            onRemove = {
                onRemove(selectedUser!!)
                showProfileBottomSheet = false
            },
            onReport = {
                onReport(selectedUser!!)
                showProfileBottomSheet = false
            },
            onLeave = {
                onLeave()
                showProfileBottomSheet = false
            }
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RoomScreenPreview() {
//    val sampleUsers = listOf(
//        User("Anand K.", "viratanand", UserRole.OWNER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80"),
//        User("Steve R.", "captainamerica", UserRole.EDITOR, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=150&h=150&q=80"),
//        User("Tony S.", "ironman", UserRole.EDITOR, "https://images.unsplash.com/photo-1531427186611-ecfd6d936c79?auto=format&fit=crop&w=150&h=150&q=80"),
//        User("Bruce B.", "hulk", UserRole.VIEWER, "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&h=150&q=80"),
//        User("Thor O.", "thor", UserRole.EDITOR, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&h=150&q=80"),
//        User("Natasha R.", "blackwidow", UserRole.VIEWER, "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&h=150&q=80"),
//        User("Clint B.", "hawkeye", UserRole.VIEWER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80")
//    )
//
//    JasnifyTheme {
//        RoomScreen(
//            allUsers = sampleUsers,
//            currentUserRole = UserRole.OWNER,
//            isSelf = { it.username == "viratanand" },
//            onBackClick = {},
//            onMenuClick = {},
//            onRoleChange = { _, _ -> },
//            onRemove = {},
//            onReport = {},
//            onLeave = {}
//        )
//    }
//}