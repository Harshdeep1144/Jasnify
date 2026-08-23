package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.PlanCard
import com.harshdeep.jasnify.presentation.components.cards.ProfileMenuCell
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.theme.TopBrandGradientBrush
import com.harshdeep.jasnify.theme.TopGradientBrushLightTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

private val CellGroupShape = SquircleShape(CornerLarge, CornerSmoothingDefault)

private data class OneShotParticle(
    val iconRes: Int,
    val angleDeg: Double,
    val targetDistance: Float,
    val delayMs: Long
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileRootScreen(
    userName: String,
    userHandle: String,
    profilePic: Any,
    eventCount: Int,
    enquiryCount: Int,
    notificationEnabled: Boolean,
    onEditProfile: () -> Unit,
    onNavigateTo: (ProfileScreen) -> Unit,
    onLogout: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState()
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

    // 16 icons expanding across a wider dispersion radius (up to 155dp)
    val particleList = remember {
        listOf(
            OneShotParticle(R.drawable.ic_food, 12.0, 142f, 0L),
            OneShotParticle(R.drawable.ic_gifts, 34.0, 128f, 60L),
            OneShotParticle(R.drawable.ic_photographers, 58.0, 150f, 30L),
            OneShotParticle(R.drawable.ic_makeup, 82.0, 132f, 90L),
            OneShotParticle(R.drawable.ic_entertainment, 105.0, 148f, 20L),
            OneShotParticle(R.drawable.ic_outfits, 128.0, 135f, 75L),
            OneShotParticle(R.drawable.ic_jewellery, 150.0, 155f, 40L),
            OneShotParticle(R.drawable.ic_mehendi, 172.0, 130f, 100L),
            OneShotParticle(R.drawable.ic_grooming, 195.0, 146f, 15L),
            OneShotParticle(R.drawable.ic_vendor, 218.0, 134f, 70L),
            OneShotParticle(R.drawable.ic_paint, 240.0, 152f, 35L),
            OneShotParticle(R.drawable.ic_book, 262.0, 129f, 95L),
            OneShotParticle(R.drawable.ic_calendar, 285.0, 149f, 25L),
            OneShotParticle(R.drawable.ic_pen, 308.0, 136f, 80L),
            OneShotParticle(R.drawable.ic_category, 330.0, 154f, 45L),
            OneShotParticle(R.drawable.ic_coin_hand, 352.0, 132f, 110L)
        )
    }

    // Individual animatables for each particle
    val animProgressList = remember { List(particleList.size) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        delay(180.milliseconds)
        particleList.forEachIndexed { index, particle ->
            launch {
                delay(particle.delayMs.milliseconds)
                animProgressList[index].animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    val eventSubtitle = remember(eventCount) {
        if (eventCount == 1) "1 Event" else "$eventCount Events"
    }
    val enquirySubtitle = remember(enquiryCount) {
        if (enquiryCount == 1) "1 Enquiry" else "$enquiryCount Enquiries"
    }

    // Calculate fade alpha: starts at 0 and reaches 1 over the first 60dp of scroll
    val density = LocalDensity.current
    val scrollThresholdPx = with(density) { 60.dp.toPx() }
    val overlayAlpha by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0) {
                1f
            } else {
                (lazyListState.firstVisibleItemScrollOffset / scrollThresholdPx).coerceIn(0f, 1f)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState
        ) {
            // 1. User Header
            item(key = "user_header", contentType = "header") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TopBrandGradientBrush)
                        .statusBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp, 28.dp, 28.dp, 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            particleList.forEachIndexed { index, particle ->
                                val progress = animProgressList[index].value

                                if (progress > 0f && progress < 1f) {
                                    val currentDistance = 45f + ((particle.targetDistance - 45f) * progress)
                                    val rad = Math.toRadians(particle.angleDeg)

                                    val horizontalMultiplier = 1.6f
                                    val verticalMultiplier = 0.65f

                                    val offsetX = (currentDistance * cos(rad) * horizontalMultiplier).dp
                                    val offsetY = (currentDistance * sin(rad) * verticalMultiplier).dp

                                    val alpha = when {
                                        progress < 0.22f -> (progress / 0.22f) * 0.95f
                                        progress < 0.65f -> 0.95f
                                        else -> ((1f - progress) / 0.35f) * 0.95f
                                    }.coerceIn(0f, 0.95f)

                                    val scale = 0.6f + (0.5f * progress)

                                    Icon(
                                        painter = painterResource(particle.iconRes),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .offset(x = offsetX, y = offsetY)
                                            .graphicsLayer {
                                                this.alpha = alpha
                                                scaleX = scale
                                                scaleY = scale
                                            }
                                            .size(16.dp),
                                        tint = ContentBrand
                                    )
                                }
                            }

                            // Profile Picture Container
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
                            containerColor = SurfacePrimary,
                            contentColor = ContentPrimary
                        )
                    }
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
                    ProfileMenuCell(
                        title = "Account Settings",
                        subtitle = null,
                        icon = profileIcon,
                        hasBorder = true,
                        onClick = { onNavigateTo(ProfileScreen.AccountSettings) }
                    )

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
                            subtitle = if (notificationEnabled) "On" else "Off",
                            icon = notificationIcon,
                            hasBorder = false,
                            shape = RectangleShape,
                            containerColor = Color.Transparent,
                            onClick = { onNavigateTo(ProfileScreen.Notifications) }
                        )
                    }

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
                            title = "Terms of Use",
                            subtitle = null,
                            icon = termsIcon,
                            hasBorder = false,
                            shape = RectangleShape,
                            containerColor = Color.Transparent,
                            onClick = { onNavigateTo(ProfileScreen.TermsOfUse) }
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

        // Top Gradient Overlay (fades in as user scrolls)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    alpha = overlayAlpha
                }
                .background(TopGradientBrushLightTheme)
        )
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
        ProfileRootScreen(
            userName = "Anand K.",
            userHandle = "@viratanand",
            profilePic = R.drawable.ic_user_profile,
            eventCount = 2,
            enquiryCount = 5,
            notificationEnabled = true,
            onEditProfile = {},
            onNavigateTo = {},
            onLogout = {}
        )
    }
}