package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole

@Composable
fun UserListItem(
    user: User,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(CornerLargeIncrease),
    backgroundColor: Color = SurfacePrimary,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(16.dp, 16.dp, 8.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Picture with img_user_default Fallback
        if (user.profilePictureUrl != null) {
            SubcomposeAsyncImage(
                model = user.profilePictureUrl,
                contentDescription = "Profile picture of ${user.name}",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user_profile),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                },
                error = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user_profile),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                }
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_user_profile),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // User Info (Name and @username)
        Column(
            modifier = Modifier.weight(1f)
        ) {
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

        // Role Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            val roleText = when (user.role) {
                UserRole.OWNER -> "Owner"
                UserRole.EDITOR -> "Editor"
                UserRole.VIEWER -> "Viewer"
            }

            val isOwner = user.role == UserRole.OWNER
            val roleColor = if (isOwner) ContentBrandDark else ContentPrimary

            if (isOwner) {
                Icon(
                    imageVector = Icons.Outlined.VerifiedUser,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = roleColor
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = roleText,
                style = JasnifyTheme.typography.labelXLarge,
                color = roleColor,
            )

            if (!isOwner) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = ContentPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListCardPreview() {
    JasnifyTheme {
        val users = listOf(
            User(
                name = "Harsh Deep",
                username = "harshdeep",
                role = UserRole.OWNER,
                profilePictureUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&h=150&q=80"
            ),
            User(
                name = "Jane Doe",
                username = "janedoe",
                role = UserRole.EDITOR,
                profilePictureUrl = null
            ),
            User(
                name = "Virat Anand",
                username = "viratanand",
                role = UserRole.VIEWER,
                profilePictureUrl = null
            )
        )

        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            users.forEach { user ->
                UserListItem(user = user)
            }
        }
    }
}