package com.harshdeep.jasnify.presentation.components.bottomdrawer.room

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

private val EmailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

private val UserSearchItemShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)

private fun isValidEmail(email: String): Boolean = email.matches(EmailRegex)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomAccessBottomSheet(
    onDismissRequest: () -> Unit,
    onGrantAccess: (String, UserRole) -> Unit,
    searchResults: List<User>,
    onSearch: (String) -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedRole by remember { mutableStateOf(UserRole.VIEWER) }

    val hasValidEmail = isValidEmail(searchQuery)
    val isShowingResults = searchQuery.isNotEmpty() && searchResults.isNotEmpty() && selectedUser == null
    val isShowingInvite = searchQuery.isNotEmpty() && searchResults.isEmpty() && hasValidEmail && selectedUser == null
    val isUserSelected = selectedUser != null

    val targetHeight = when {
        isShowingResults -> {
            val dynamicHeight = 220.dp + (searchResults.size * 72).dp
            dynamicHeight.coerceIn(290.dp, 460.dp)
        }
        isShowingInvite || isUserSelected -> 290.dp
        else -> 220.dp
    }

    val animatedSheetHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = 400f
        ),
        label = "BottomSheetHeight"
    )

    CustomBottomSheet(
        heading = "Share Room Access",
        onDismiss = onDismissRequest,
        sheetHeight = animatedSheetHeight,
        onProgress = onProgress
    ) {
        RoomAccessBottomSheetContent(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchQuery = query
                selectedUser = null
                onSearch(query)
            },
            selectedUser = selectedUser,
            onSelectedUserChange = { user ->
                selectedUser = user
                if (user != null) {
                    searchQuery = user.email
                }
            },
            selectedRole = selectedRole,
            onSelectedRoleChange = { selectedRole = it },
            onDismissRequest = onDismissRequest,
            onGrantAccess = onGrantAccess,
            searchResults = searchResults
        )
    }
}

@Composable
fun RoomAccessBottomSheetContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedUser: User?,
    onSelectedUserChange: (User?) -> Unit,
    selectedRole: UserRole,
    onSelectedRoleChange: (UserRole) -> Unit,
    onDismissRequest: () -> Unit,
    onGrantAccess: (String, UserRole) -> Unit,
    searchResults: List<User>
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(150.milliseconds)
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .navigationBarsPadding()
    ) {
        PrimaryInput(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Email or username",
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
                .animateContentSize()
        ) {
            if (searchQuery.isNotEmpty() && selectedUser == null) {
                if (searchResults.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = searchResults,
                            key = { it.uid.ifEmpty { it.email } },
                            contentType = { "user_search_item" }
                        ) { user ->
                            UserSearchItem(
                                user = user,
                                onClick = { onSelectedUserChange(user) }
                            )
                        }
                    }
                } else if (isValidEmail(searchQuery)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(UserSearchItemShape)
                            .background(SurfaceSecondary.copy(alpha = 0.5f))
                            .clickable {
                                onSelectedUserChange(
                                    User(
                                        name = searchQuery.substringBefore("@"),
                                        email = searchQuery,
                                        username = "",
                                        role = UserRole.VIEWER
                                    )
                                )
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_user_profile),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Invite '$searchQuery'",
                                color = ContentPrimary,
                                style = JasnifyTheme.typography.labelXLarge
                            )
                        }
                    }
                }
            } else if (selectedUser != null) {
                UserSearchItem(
                    user = selectedUser,
                    onClick = { onSelectedUserChange(null) },
                    modifier = Modifier.background(
                        SurfaceBrandSecondary,
                        UserSearchItemShape
                    )
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "Viewer Access",
                    isSelected = selectedRole == UserRole.VIEWER,
                    onClick = { onSelectedRoleChange(UserRole.VIEWER) },
                    leadingIcon = ImageVector.vectorResource(id = R.drawable.ic_eye_open),
                    shapeStyle = ChipShapeStyle.Round,
                    hasStroke = true
                )
                FilterChip(
                    label = "Editor Access",
                    isSelected = selectedRole == UserRole.EDITOR,
                    onClick = { onSelectedRoleChange(UserRole.EDITOR) },
                    leadingIcon = ImageVector.vectorResource(id = R.drawable.ic_edit_pen),
                    shapeStyle = ChipShapeStyle.Round,
                    hasStroke = true
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(12.dp))
            CustomTextButton(
                text = "Grant Access",
                onClick = {
                    val email = selectedUser?.email ?: searchQuery
                    if (email.isNotEmpty()) {
                        onGrantAccess(email, selectedRole)
                        onDismissRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shapeStyle = ButtonShapeStyle.Square,
            )
        }
    }
}

@Composable
fun UserSearchItem(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(UserSearchItemShape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceSecondary),
            contentAlignment = Alignment.Center
        ) {
            if (user.profilePictureUrl != null) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_user_profile),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentPrimary,
            )
            Text(
                text = "@${user.username}",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomAccessBottomSheetPreview() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedRole by remember { mutableStateOf(UserRole.VIEWER) }

    val sampleUsers = listOf(
        User("Anand K.", "viratanand", "anand@example.com", "", UserRole.OWNER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80"),
        User("Steve R.", "captainamerica", "steve@example.com", "", UserRole.EDITOR, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=150&h=150&q=80"),
    )

    Box(
        modifier = Modifier
            .height(420.dp)
            .fillMaxWidth()
    ) {
        RoomAccessBottomSheetContent(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            selectedUser = selectedUser,
            onSelectedUserChange = { selectedUser = it },
            selectedRole = selectedRole,
            onSelectedRoleChange = { selectedRole = it },
            onDismissRequest = {},
            onGrantAccess = { _, _ -> },
            searchResults = sampleUsers
        )
    }
}