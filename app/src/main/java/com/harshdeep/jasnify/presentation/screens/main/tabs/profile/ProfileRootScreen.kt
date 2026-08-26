package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.plansheet.PlanType
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.PlanCard
import com.harshdeep.jasnify.presentation.components.cards.ProfileMenuCell
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.theme.TopBrandDarkGradientBrush
import com.harshdeep.jasnify.theme.TopBrandGradientBrush
import com.harshdeep.jasnify.theme.TopGradientBrushLightTheme
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

private val CellGroupShape = SquircleShape(CornerLarge, CornerSmoothingDefault)

@SuppressLint("ConfigurationScreenWidthHeight")
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
    onPlanClick: (PlanType) -> Unit,
    lazyListState: LazyListState = rememberLazyListState()
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val editIcon = painterResource(R.drawable.ic_edit)
    val eventsStackIcon = painterResource(R.drawable.ic_events_stack)
    val messageTypingIcon = painterResource(R.drawable.ic_message_typing)
    val profileIcon = painterResource(R.drawable.ic_profile)
    val paintIcon = painterResource(R.drawable.ic_paint)
    val notificationIcon = painterResource(R.drawable.ic_notification)
    val termsIcon = painterResource(R.drawable.ic_terms_and_conditions)
    val privacyIcon = painterResource(R.drawable.ic_privacy_policy)
    val helpIcon = painterResource(R.drawable.ic_help_feedback)
    val logoutIcon = painterResource(R.drawable.ic_logout)
    val lockIcon = painterResource(R.drawable.ic_lock)
    val placeholderIcon = painterResource(R.drawable.img_profile_placeholder)

    var headerHeightDp by remember { mutableStateOf(configuration.screenHeightDp.dp * 0.5f) }
    var isReadyToPlay by remember { mutableStateOf(false) }

    var toastData by remember { mutableStateOf(ToastData()) }

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000L.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    // Load Lottie composition
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ani_profile_bg_pattern))

    LaunchedEffect(composition) {
        if (composition != null) {
            delay(400L.milliseconds)
            isReadyToPlay = true
        }
    }

    val lottieAnimState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isReadyToPlay,
        restartOnPlay = false,
        iterations = 1,
    )

    // Smooth, relaxed scale-down and scale-up transition
    val avatarScale = remember { Animatable(1f) }
    LaunchedEffect(isReadyToPlay) {
        if (isReadyToPlay) {
            // Gentle ease down
            avatarScale.animateTo(
                targetValue = 0.92f,
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            )
            // Soft expansion back to 1.0f
            avatarScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 450,
                    easing = CubicBezierEasing(0.34f, 1.3f, 0.64f, 1f)
                )
            )
        }
    }

    val gradientAlpha by remember {
        derivedStateOf {
            if (!isReadyToPlay || composition == null) {
                0f
            } else if (lottieAnimState.isAtEnd && lottieAnimState.progress > 0.5f) {
                1f
            } else if (lottieAnimState.progress >= 0.85f) {
                ((lottieAnimState.progress - 0.85f) / 0.15f).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

    val eventSubtitle = remember(eventCount) {
        if (eventCount == 1) "1 Event" else "$eventCount Events"
    }
    val enquirySubtitle = remember(enquiryCount) {
        if (enquiryCount == 1) "1 Enquiry" else "$enquiryCount Enquiries"
    }

    val scrollThresholdPx = with(density) { 160.dp.toPx() }
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
        // Brand Gradient Backdrop
        if (gradientAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeightDp * 1.5f)
                    .graphicsLayer {
                        translationY = -lazyListState.firstVisibleItemScrollOffset.toFloat()
                        alpha = gradientAlpha * (1f - overlayAlpha)
                    }
                    .background(TopBrandDarkGradientBrush)
            )
        }

        // Full-bleed Lottie Animation Overlay
        if (isReadyToPlay && gradientAlpha < 1f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeightDp)
                    .graphicsLayer {
                        translationY = -lazyListState.firstVisibleItemScrollOffset.toFloat()
                        alpha = (1f - gradientAlpha) * (1f - overlayAlpha)
                    }
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { lottieAnimState.progress },
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }

        // Scrollable Foreground Content Layer
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState
        ) {
            // User Header
            item(key = "user_header", contentType = "header") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            val measured = with(density) { coordinates.size.height.toDp() }
                            if (measured > 0.dp) {
                                headerHeightDp = measured
                            }
                        }
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // User Avatar with Smooth Scale
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = avatarScale.value
                                    scaleY = avatarScale.value
                                }
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

                        Spacer(Modifier.height(14.dp))

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
                            contentColor = ContentPrimary,
                            modifier = Modifier.wrapContentHeight()
                        )
                    }
                }
            }

            // Plan Cards
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
                        isCurrentPlan = true,
                        planType = PlanType.BASIC,
                        onViewBenefitsClick = { onPlanClick(PlanType.BASIC) }
                    )
                    PlanCard(
                        planName = "Pro",
                        price = "$5/month",
                        buttonText = "Upgrade Now",
                        planType = PlanType.PRO,
                        onUpgradeNowClick = {
                            toastData = ToastData("Coming Soon!", ToastType.DEFAULT)
                        },
                        onViewBenefitsClick = { onPlanClick(PlanType.PRO) },
                        buttonLeadingIcon = lockIcon
                    )
                    PlanCard(
                        planName = "Ultimate",
                        price = "$20/month",
                        buttonText = "Upgrade Now",
                        planType = PlanType.ULTIMATE,
                        onUpgradeNowClick = {
                            toastData = ToastData("Coming Soon!", ToastType.DEFAULT)
                        },
                        onViewBenefitsClick = { onPlanClick(PlanType.ULTIMATE) },
                        buttonLeadingIcon = lockIcon
                    )
                }
            }

            // Grid Actions
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

            // Menu Items
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
                        ProfileMenuCell(
                            title = "Help & Feedback",
                            subtitle = null,
                            icon = helpIcon,
                            hasBorder = false,
                            shape = RectangleShape,
                            containerColor = Color.Transparent,
                            onClick = { onNavigateTo(ProfileScreen.HelpFeedback) }
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

            // Footer
            item(key = "footer", contentType = "footer") {
                FooterJansify()
            }
        }

        // Pinned Status Bar Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    alpha = overlayAlpha
                }
                .background(TopGradientBrushLightTheme)
        )

        // Pinned Toast Notification (Animated from Top)
        AnimatedVisibility(
            visible = toastData.message != null,
            enter = slideInVertically(initialOffsetY = { -it - 500 }),
            exit = slideOutVertically(targetOffsetY = { -it - 500 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .zIndex(99f)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            CustomToast(message = toastData.message.orEmpty(), type = toastData.type)
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
        ProfileRootScreen(
            userName = "Harsh Deep",
            userHandle = "@harshdeep",
            profilePic = R.drawable.img_profile_placeholder,
            eventCount = 2,
            enquiryCount = 5,
            notificationEnabled = true,
            onEditProfile = {},
            onNavigateTo = {},
            onLogout = {},
            onPlanClick = {}
        )
    }
}