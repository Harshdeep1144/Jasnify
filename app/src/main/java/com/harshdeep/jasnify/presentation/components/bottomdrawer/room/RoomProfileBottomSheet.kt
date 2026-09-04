package com.harshdeep.jasnify.presentation.components.bottomdrawer.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.OptionSelector
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary

private val ProfileCardShape = RoundedCornerShape(CornerExtraLarge)
private val SingleActionShape = RoundedCornerShape(CornerLarge)
private val TopActionShape = RoundedCornerShape(
    topStart = CornerLarge,
    topEnd = CornerLarge,
    bottomStart = CornerExtraSmall,
    bottomEnd = CornerExtraSmall
)
private val MiddleActionShape = RoundedCornerShape(CornerExtraSmall)
private val BottomActionShape = RoundedCornerShape(
    topStart = CornerExtraSmall,
    topEnd = CornerExtraSmall,
    bottomStart = CornerLarge,
    bottomEnd = CornerLarge
)

private data class ActionItem(
    val text: String,
    val textColor: Color,
    val iconResId: Int,
    val onClick: () -> Unit
)

private data class AccessBannerData(
    val backgroundColor: Color,
    val textColor: Color,
    val bannerText: String,
    val iconResId: Int? = null,
    val subText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomProfileBottomSheet(
    user: User,
    currentUserRole: UserRole,
    isSelf: Boolean,
    onDismissRequest: () -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onRemove: () -> Unit,
    onReport: () -> Unit,
    onLeave: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    CustomBottomSheet(
        onDismiss = onDismissRequest,
        onProgress = onProgress,
        sheetHeight = null,
        containerColor = SurfaceSecondary,
        showDragHandle = true,
        showCloseButton = false,
    ) {
        RoomProfileContent(
            user = user,
            currentUserRole = currentUserRole,
            isSelf = isSelf,
            onClose = onDismissRequest,
            onRoleChange = onRoleChange,
            onRemove = onRemove,
            onReport = onReport,
            onLeave = onLeave
        )
    }
}

@Composable
fun RoomProfileContent(
    user: User,
    currentUserRole: UserRole,
    isSelf: Boolean,
    onClose: () -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onRemove: () -> Unit,
    onReport: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceSecondary)
            .navigationBarsPadding()
    ) {
        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Profile",
                style = JasnifyTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary
            )
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cross),
                    contentDescription = "Close",
                    tint = ContentPrimary
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .clip(ProfileCardShape)
                .background(SurfacePrimary)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Frame
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
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
                        painter = painterResource(R.drawable.ic_user_profile),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // User Identifiers
            Text(
                text = user.name,
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentPrimary
            )
            Text(
                text = "@${user.username}",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Access Level Section
            AccessLevelSection(
                user = user,
                currentUserRole = currentUserRole,
                isSelf = isSelf,
                onRoleChange = onRoleChange
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Actions Section (Leave, Remove, Report)
        ActionsSection(
            user = user,
            currentUserRole = currentUserRole,
            isSelf = isSelf,
            onRemove = onRemove,
            onReport = onReport,
            onLeave = onLeave
        )
    }
}

@Composable
fun AccessLevelSection(
    user: User,
    currentUserRole: UserRole,
    isSelf: Boolean,
    onRoleChange: (UserRole) -> Unit
) {
    val canManageRoles = currentUserRole == UserRole.OWNER && !isSelf
    var selectedRole by remember(user.uid, user.role) { mutableStateOf(user.role) }

    if (canManageRoles) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            DashedDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OptionSelector(
                label = "Editor",
                bodyText = "can edit details of the room",
                isSelected = selectedRole == UserRole.EDITOR,
                onClick = {
                    if (selectedRole != UserRole.EDITOR) {
                        selectedRole = UserRole.EDITOR
                        onRoleChange(UserRole.EDITOR)
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            OptionSelector(
                label = "Viewer",
                bodyText = "can only view the latest details",
                isSelected = selectedRole == UserRole.VIEWER,
                onClick = {
                    if (selectedRole != UserRole.VIEWER) {
                        selectedRole = UserRole.VIEWER
                        onRoleChange(UserRole.VIEWER)
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    } else {
        val firstName = remember(user.name) { user.name.substringBefore(' ') }

        val data = remember(user.role, isSelf, firstName) {
            when (user.role) {
                UserRole.OWNER -> {
                    val text = if (isSelf) "You’re the Owner" else "Owner"
                    val note = if (isSelf) "You can't transfer ownership to someone else." else null
                    AccessBannerData(
                        backgroundColor = Color(0xFFF7D985),
                        textColor = Color(0xFF654C05),
                        bannerText = text,
                        iconResId = R.drawable.ic_shield,
                        subText = note
                    )
                }
                UserRole.EDITOR -> {
                    val text = if (isSelf) "You have Editor Access" else "$firstName has Editor Access"
                    AccessBannerData(SurfaceBrandSecondary, ContentBrand, text)
                }
                else -> {
                    val text = if (isSelf) "You have Viewer Access" else "$firstName has Viewer Access"
                    AccessBannerData(SurfaceSecondary, ContentSecondary, text)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(data.backgroundColor)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (data.iconResId != null) {
                    Icon(
                        painter = painterResource(id = data.iconResId),
                        contentDescription = null,
                        tint = data.textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = data.bannerText,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = data.textColor
                )
            }

            if (data.subText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = null,
                        tint = ContentSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = data.subText,
                        style = JasnifyTheme.typography.bodyMedium,
                        color = ContentSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ActionsSection(
    user: User,
    currentUserRole: UserRole,
    isSelf: Boolean,
    onRemove: () -> Unit,
    onReport: () -> Unit,
    onLeave: () -> Unit
) {
    val showRemove = !isSelf && (
            (currentUserRole == UserRole.OWNER && user.role != UserRole.OWNER) ||
                    (currentUserRole == UserRole.EDITOR && user.role == UserRole.VIEWER)
            )

    val showLeave = isSelf && user.role != UserRole.OWNER
    val showReport = !isSelf

    val errorColor = MaterialTheme.colorScheme.error
    val firstName = remember(user.name) { user.name.substringBefore(' ') }

    val actionItems = remember(showRemove, showLeave, showReport, errorColor, firstName) {
        buildList {
            if (showRemove) {
                add(
                    ActionItem(
                        text = "Remove from Room",
                        textColor = errorColor,
                        iconResId = R.drawable.ic_minus,
                        onClick = onRemove
                    )
                )
            }
            if (showLeave) {
                add(
                    ActionItem(
                        text = "Leave Room",
                        textColor = errorColor,
                        iconResId = R.drawable.ic_logout,
                        onClick = onLeave
                    )
                )
            }
            if (showReport) {
                add(
                    ActionItem(
                        text = "Report $firstName",
                        textColor = errorColor,
                        iconResId = R.drawable.ic_thumbs_down,
                        onClick = onReport
                    )
                )
            }
        }
    }

    if (actionItems.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            val totalItems = actionItems.size
            actionItems.forEachIndexed { index, item ->
                val roundedShape = when {
                    totalItems == 1 -> SingleActionShape
                    index == 0 -> TopActionShape
                    index == totalItems - 1 -> BottomActionShape
                    else -> MiddleActionShape
                }

                CustomTextButton(
                    onClick = item.onClick,
                    text = item.text,
                    leadingIcon = painterResource(id = item.iconResId),
                    type = ButtonType.Tertiary,
                    contentColor = item.textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = SurfacePrimary, shape = roundedShape)
                        .clip(roundedShape)
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

// ================================================= Preview ======================================================

@Preview(showBackground = true)
@Composable
fun RoomProfileCardPreview_OwnerViewingViewer() {
    JasnifyTheme {
        RoomProfileContent(
            user = User(
                uid = "1",
                name = "Natasha R.",
                email = "natasha@jasnify.com",
                username = "blackwidow",
                role = UserRole.VIEWER
            ),
            currentUserRole = UserRole.OWNER,
            isSelf = false,
            onClose = {},
            onRoleChange = {},
            onRemove = {},
            onReport = {},
            onLeave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomProfileCardPreview_OwnerViewingSelf() {
    JasnifyTheme {
        RoomProfileContent(
            user = User(
                uid = "2",
                name = "Anand K.",
                email = "anand@jasnify.com",
                username = "viratanand",
                role = UserRole.OWNER
            ),
            currentUserRole = UserRole.OWNER,
            isSelf = true,
            onClose = {},
            onRoleChange = {},
            onRemove = {},
            onReport = {},
            onLeave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomProfileCardPreview_ViewerViewingOwner() {
    JasnifyTheme {
        RoomProfileContent(
            user = User(
                uid = "2",
                name = "Anand K.",
                email = "anand@jasnify.com",
                username = "viratanand",
                role = UserRole.OWNER
            ),
            currentUserRole = UserRole.VIEWER,
            isSelf = false,
            onClose = {},
            onRoleChange = {},
            onRemove = {},
            onReport = {},
            onLeave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomProfileCardPreview_ViewerViewingSelf() {
    JasnifyTheme {
        RoomProfileContent(
            user = User(
                uid = "1",
                name = "Natasha R.",
                email = "natasha@jasnify.com",
                username = "blackwidow",
                role = UserRole.VIEWER
            ),
            currentUserRole = UserRole.VIEWER,
            isSelf = true,
            onClose = {},
            onRoleChange = {},
            onRemove = {},
            onReport = {},
            onLeave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomProfileCardPreview_EditorViewingViewer() {
    JasnifyTheme {
        RoomProfileContent(
            user = User(
                uid = "1",
                name = "Natasha R.",
                email = "natasha@jasnify.com",
                username = "blackwidow",
                role = UserRole.VIEWER
            ),
            currentUserRole = UserRole.EDITOR,
            isSelf = false,
            onClose = {},
            onRoleChange = {},
            onRemove = {},
            onReport = {},
            onLeave = {}
        )
    }
}