package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.PlanCard
import com.harshdeep.jasnify.presentation.components.cards.ProfileMenuCell
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

private val CellGroupShape = SquircleShape(CornerLarge, CornerSmoothingDefault)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileTabContent(
    userName: String,
    userHandle: String,
    profilePic: Any,
    eventCount: Int,
    enquiryCount: Int,
    onEditProfile: () -> Unit,
    onNavigateTo: (ProfileScreen) -> Unit,
    onLogout: () -> Unit
) {
    val editIcon = painterResource(R.drawable.ic_edit)
    val eventsStackIcon = painterResource(R.drawable.ic_events_stack)
    val messageTypingIcon = painterResource(R.drawable.ic_message_typing)
    val profileIcon = painterResource(R.drawable.ic_profile)
    val paintIcon = painterResource(R.drawable.ic_paint)
    val notificationIcon = painterResource(R.drawable.ic_notification)
    val termsIcon = painterResource(R.drawable.ic_terms_and_conditions)
    val privacyIcon = painterResource(R.drawable.ic_privacy_policy)
    val logoutIcon = painterResource(R.drawable.ic_logout)
    val placeholderIcon = painterResource(R.drawable.ic_user_profile)

    val eventSubtitle = remember(eventCount) {
        if (eventCount == 1) "1 Event" else "$eventCount Events"
    }
    val enquirySubtitle = remember(enquiryCount) {
        if (enquiryCount == 1) "1 Enquiry" else "$enquiryCount Enquiries"
    }

    val cellBorderModifier = remember {
        Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Unspecified, // overwritten via MaterialTheme color below
                shape = CellGroupShape
            )
            .clip(CellGroupShape)
            .background(Color.Unspecified)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. User Header
        item(key = "user_header", contentType = "header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp, 28.dp, 28.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(SurfaceSecondary)
                ) {
                    AsyncImage(
                        model = profilePic,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = placeholderIcon
                    )
                }
                Spacer(Modifier.height(12.dp))

                Text(
                    text = userName,
                    style = JasnifyTheme.typography.displaySmall.copy(fontWeight = FontWeight.Medium),
                    color = ContentPrimary
                )
                Text(
                    text = userHandle,
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
                Spacer(Modifier.height(16.dp))

                CustomTextButton(
                    onClick = onEditProfile,
                    text = "Edit Profile",
                    size = ButtonSize.Small,
                    leadingIcon = editIcon,
                    type = ButtonType.Secondary
                )
            }
        }

        // 2. Plan Cards
        item(key = "plan_cards", contentType = "plan_carousel") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlanCard(
                    planName = "Basic Plan",
                    price = "FREE",
                    backgroundColor = Color(0xFFF4E3E2),
                    isCurrentPlan = true,
                    onViewBenefitsClick = {}
                )
                PlanCard(
                    planName = "Pro",
                    price = "$5/month",
                    backgroundColor = Color(0xFFFFDAB9),
                    buttonText = "Upgrade Now",
                    onButtonClick = {},
                    onViewBenefitsClick = {}
                )
                PlanCard(
                    planName = "Ultimate",
                    price = "$20/month",
                    backgroundColor = Color(0xFFD3CDE8),
                    buttonText = "Upgrade Now",
                    onButtonClick = {},
                    onViewBenefitsClick = {}
                )
            }
        }

        // 3. Grid Actions
        item(key = "grid_actions", contentType = "grid_actions") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileGridCell(
                    title = "Manage Events",
                    subtitle = eventSubtitle,
                    icon = eventsStackIcon,
                    onClick = { onNavigateTo(ProfileScreen.ManageEvents) },
                    modifier = Modifier.weight(1f)
                )
                ProfileGridCell(
                    title = "My Enquiries",
                    subtitle = enquirySubtitle,
                    icon = messageTypingIcon,
                    onClick = { onNavigateTo(ProfileScreen.MyEnquiries) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Menu Items
        item(key = "menu_items", contentType = "menu_items") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Account Settings Group
                ProfileMenuCell(
                    title = "Account Settings",
                    subtitle = null,
                    icon = profileIcon,
                    hasBorder = true,
                    onClick = { onNavigateTo(ProfileScreen.AccountSettings) }
                )

                // Appearance & Notifications Group
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = CellGroupShape
                        )
                        .clip(CellGroupShape)
                        .background(SurfacePrimary)
                ) {
                    ProfileMenuCell(
                        title = "Appearance",
                        subtitle = null,
                        icon = paintIcon,
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = { onNavigateTo(ProfileScreen.Appearance) }
                    )
                    ProfileMenuCell(
                        title = "Notifications",
                        subtitle = "On",
                        icon = notificationIcon,
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = { onNavigateTo(ProfileScreen.Notifications) }
                    )
                }

                // Legal Group
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = CellGroupShape
                        )
                        .clip(CellGroupShape)
                        .background(SurfacePrimary)
                ) {
                    ProfileMenuCell(
                        title = "Terms & Conditions",
                        subtitle = null,
                        icon = termsIcon,
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = { onNavigateTo(ProfileScreen.TermsAndConditions) }
                    )
                    ProfileMenuCell(
                        title = "Privacy Policy",
                        subtitle = null,
                        icon = privacyIcon,
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = { onNavigateTo(ProfileScreen.PrivacyPolicy) }
                    )
                }

                // Logout
                ProfileMenuCell(
                    title = "Logout",
                    subtitle = null,
                    icon = logoutIcon,
                    hasBorder = true,
                    onClick = onLogout,
                    contentColor = MaterialTheme.colorScheme.error
                )
            }
        }

        // 5. Footer
        item(key = "footer", contentType = "footer") {
            FooterJansify()
        }
    }
}

@Composable
fun ProfileGridCell(
    title: String,
    subtitle: String? = null,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(CellGroupShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = CellGroupShape
            )
            .clickable { onClick() },
        color = SurfacePrimary,
        shape = CellGroupShape
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = ContentPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ProfileTabPreview() {
    JasnifyTheme {
        ProfileTabContent(
            userName = "Anand K.",
            userHandle = "@viratanand",
            profilePic = R.drawable.ic_user_profile,
            eventCount = 2,
            enquiryCount = 5,
            onEditProfile = {},
            onNavigateTo = {},
            onLogout = {}
        )
    }
}