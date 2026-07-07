package com.harshdeep.jasnify.presentation.screens.room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.bottomdrawer.RoomProfileBottomSheet
import com.harshdeep.jasnify.presentation.components.cards.UserListItem
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.Neutral100
import com.harshdeep.jasnify.theme.SurfacePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    // Sample data to match the image
    val allUsers = remember {
        listOf(
            User("Anand K.", "viratanand", UserRole.OWNER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80"),
            User("Steve R.", "captainamerica", UserRole.EDITOR, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=150&h=150&q=80"),
            User("Tony S.", "ironman", UserRole.EDITOR, "https://images.unsplash.com/photo-1531427186611-ecfd6d936c79?auto=format&fit=crop&w=150&h=150&q=80"),
            User("Bruce B.", "hulk", UserRole.VIEWER, "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&h=150&q=80"),
            User("Thor O.", "thor", UserRole.EDITOR, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&h=150&q=80"),
            User("Natasha R.", "blackwidow", UserRole.VIEWER, "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&h=150&q=80"),
            User("Clint B.", "hawkeye", UserRole.VIEWER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80")
        )
    }

    val filteredUsers = allUsers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.username.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Manage Room Access",
                onBackClick = { navController.popBackStack() },
                buttonStyle = ButtonBackground.OPAQUE,
                menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                onMenuClick = { /* More options */ },
                backIcon = TopIcon.Predefined.BACK
            )
        },
        containerColor = BackgroundSecondary,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            CustomSearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search a user"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // List of Users Container
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerExtraLarge))
                    .background(SurfacePrimary),
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
                            showBottomSheet = true
                        }
                    )

                    if (!isLast) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Neutral100
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet && selectedUser != null) {
        RoomProfileBottomSheet(
            user = selectedUser!!,
            currentUserRole = UserRole.OWNER,
            isSelf = false,
            onDismissRequest = { showBottomSheet = false },
            onRoleChange = { /* Handle role change */ },
            onRemove = { /* Handle remove */ },
            onReport = { /* Handle report */ },
            onLeave = { /* Handle leave */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomScreenPreview() {
    JasnifyTheme {
        RoomScreen(navController = rememberNavController())
    }
}
