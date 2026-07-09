package com.harshdeep.jasnify.presentation.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.chip.CateringItemChip
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.BackgroundBrand
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge

enum class OnboardingState {
    CAROUSEL,
    ENTER_EVENT_ID,
    EVENT_DETAILS
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingType(
    navController: NavController
) {
    // Current screen navigation state
    var currentScreenState by remember { mutableStateOf(OnboardingState.CAROUSEL) }
    var eventIdValue by remember { mutableStateOf("") }

    // State for Custom Toast
    var toastData by remember { mutableStateOf(ToastData()) }

    // LaunchedEffect to dismiss CustomToast automatically
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000L.milliseconds) // Wait for 3 seconds
            toastData = toastData.copy(message = null) // Clear message to dismiss toast
        }
    }

    val pageState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // --- CUSTOM BACK HANDLER ---
    val isBackHandlerEnabled = currentScreenState != OnboardingState.CAROUSEL || pageState.currentPage > 0

    BackHandler(enabled = isBackHandlerEnabled) {
        when (currentScreenState) {
            OnboardingState.CAROUSEL -> {
                if (pageState.currentPage > 0) {
                    coroutineScope.launch {
                        pageState.animateScrollToPage(pageState.currentPage - 1)
                    }
                }
            }
            OnboardingState.ENTER_EVENT_ID -> {
                currentScreenState = OnboardingState.CAROUSEL
            }
            OnboardingState.EVENT_DETAILS -> {
                currentScreenState = OnboardingState.ENTER_EVENT_ID
            }
        }
    }

    // Dynamically change background color of the single Scaffold based on state
    val containerColor = when (currentScreenState) {
        OnboardingState.CAROUSEL -> BackgroundBrand
        OnboardingState.ENTER_EVENT_ID -> BackgroundPrimary
        OnboardingState.EVENT_DETAILS -> BackgroundPrimary
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = containerColor,
            topBar = {
                // Check dynamically if the top bar back button should be visible
                val showTopBar = when (currentScreenState) {
                    OnboardingState.CAROUSEL -> pageState.currentPage > 0
                    OnboardingState.ENTER_EVENT_ID -> true
                    OnboardingState.EVENT_DETAILS -> true
                }

                if (showTopBar) {
                    Column(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .statusBarsPadding()
                    ) {
                        CustomTopBar(
                            onBackClick = {
                                when (currentScreenState) {
                                    OnboardingState.CAROUSEL -> {
                                        if (pageState.currentPage > 0) {
                                            coroutineScope.launch {
                                                pageState.animateScrollToPage(pageState.currentPage - 1)
                                            }
                                        }
                                    }
                                    OnboardingState.ENTER_EVENT_ID -> {
                                        currentScreenState = OnboardingState.CAROUSEL
                                    }
                                    OnboardingState.EVENT_DETAILS -> {
                                        currentScreenState = OnboardingState.ENTER_EVENT_ID
                                    }
                                }
                            },
                            buttonStyle = ButtonBackground.OPAQUE,
                            backIcon = TopIcon.Predefined.BACK_2
                        )
                    }
                } else {
                    Spacer(
                        modifier = Modifier
                            .statusBarsPadding()
                            .height(64.dp)
                    )
                }
            }
        ) { paddingValues ->
            AnimatedContent(
                targetState = currentScreenState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                transitionSpec = {
                    // Check if the state transition is moving forward or backward in the sequence
                    val isForward = targetState.ordinal > initialState.ordinal
                    if (isForward) {
                        (slideInHorizontally(initialOffsetX = { it }) + fadeIn()).togetherWith(
                            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                        )
                    } else {
                        (slideInHorizontally(initialOffsetX = { -it }) + fadeIn()).togetherWith(
                            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                        )
                    }
                },
                label = "OnboardingFlowTransitions"
            ) { state ->
                when (state) {
                    OnboardingState.CAROUSEL -> {
                        CarouselOnboardingScreen(
                            pageState = pageState,
                            coroutineScope = coroutineScope,
                            onNavigateToEventId = {
                                currentScreenState = OnboardingState.ENTER_EVENT_ID
                            },
                            navController = navController
                        )
                    }
                    OnboardingState.ENTER_EVENT_ID -> {
                        EnterEventIdScreen(
                            eventId = eventIdValue,
                            onEventIdChange = { eventIdValue = it },
                            onVerifyClick = {
                                if (eventIdValue.isNotEmpty()) {
                                    currentScreenState = OnboardingState.EVENT_DETAILS
                                } else {
                                    toastData = ToastData("Please enter a valid Event ID", ToastType.ERROR)
                                }
                            }
                        )
                    }
                    OnboardingState.EVENT_DETAILS -> {
                        EventDetailsScreen(
                            eventId = eventIdValue,
                            onEditClick = { currentScreenState = OnboardingState.ENTER_EVENT_ID },
                            onLoginSignupClick = {
                                navController.navigate(Screen.LoginOrSignUp.route)
                            }
                        )
                    }
                }
            }
        }

        // --- CustomToast Display  ---
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
            CustomToast(
                message = toastData.message ?: "",
                type = toastData.type,
                buttonText = null,
                onButtonClick = null,
            )
        }
    }
}

// =================================================================  Carousel Screen =================================================================

@Composable
fun CarouselOnboardingScreen(
    pageState: androidx.compose.foundation.pager.PagerState,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onNavigateToEventId: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            HorizontalPager(
                state = pageState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val deviceFrameRes = when (page) {
                        0 -> R.drawable.device_frame_1
                        1 -> R.drawable.device_frame_2
                        else -> R.drawable.device_frame_3
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clipToBounds(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            painter = painterResource(id = deviceFrameRes),
                            contentDescription = "Mock Device",
                            contentScale = ContentScale.FillWidth,
                            alignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Bottom fade-out overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(1f)
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            BackgroundBrand
                                        )
                                    )
                                )
                        )
                    }

                    // Foreground container
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        when (page) {
                            0 -> SlideOneContent()
                            1 -> SlideTwoContent()
                            2 -> SlideThreeContent()
                        }
                    }
                }
            }
        }

        val (titleText, descText) = when (pageState.currentPage) {
            0 -> Pair(
                "Track Expenses using AI",
                "Set your budget, add expenses, and AI keeps track of all your event spends smartly."
            )
            1 -> Pair(
                "Manage Catering Menu",
                "Add favourite items to the menu, so you don't miss out anything."
            )
            else -> Pair(
                "Explore Vendors & Venues",
                "Connect with reliable vendors who offer top-notch services."
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp, 0.dp, 32.dp, 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titleText,
                style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                color = ContentBrandDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = descText,
                style = JasnifyTheme.typography.bodyLarge,
                color = ContentSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Slide Indicators (Dots)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 3) {
                val isActive = pageState.currentPage == i
                val dotWidth = animateDpAsState(targetValue = if (isActive) 18.dp else 6.dp, label = "dot")
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .width(dotWidth.value)
                        .clip(RoundedCornerShape(100))
                        .background(if (isActive) ContentBrand else ContentTertiary)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (pageState.currentPage < 2) {
                Box(
                    modifier = Modifier.height(174.dp),
                    contentAlignment = Alignment.Center
                ){
                    CustomTextButton(
                        onClick = {
                            coroutineScope.launch {
                                pageState.animateScrollToPage(pageState.currentPage + 1)
                            }
                        },
                        text = "Next",
                        trailingIcon = painterResource(R.drawable.ic_right),
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomTextButton(
                        onClick = {
                            navController.navigate(Screen.LoginOrSignUp.route)
                        },
                        text = "Create a new event",
                        trailingIcon = painterResource(R.drawable.ic_right),
                        size = ButtonSize.Large,
                        modifier = Modifier.fillMaxWidth(),
                        shapeStyle = ButtonShapeStyle.Square
                    )

                    Text(
                        text = "OR",
                        color = ContentSecondary,
                        style = JasnifyTheme.typography.labelMedium,
                        modifier = Modifier.padding(16.dp)
                    )

                    CustomTextButton(
                        onClick = onNavigateToEventId,
                        text = "Have an Event ID?",
                        shapeStyle = ButtonShapeStyle.Square,
                        type = ButtonType.Secondary
                    )
                }
            }
        }
    }
}

// =================================================================  Enter Event ID Screen =================================================================

@Composable
fun EnterEventIdScreen(
    eventId: String,
    onEventIdChange: (String) -> Unit,
    onVerifyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Automatically adjusts for dynamic Gboard keyboard heights
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(
                text = "Event ID",
                style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                color = ContentPrimary
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Use the unique Event ID to join, manage and collaborate on an existing event.",
                style = JasnifyTheme.typography.bodyLarge,
                color = ContentSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryInput(
                value = eventId,
                onValueChange = onEventIdChange,
                placeholder = "Enter the event ID",
                shape = SquircleShape(CornerExtraSmall,CornerLarge,CornerLarge,CornerLarge,CornerSmoothingDefault)
            )
        }
        val context = LocalContext.current

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.BottomCenter
        ){
            CustomTextButton(
                onClick = onVerifyClick,
                text = "Verify & Continue",
                modifier = Modifier.fillMaxWidth()
                    .padding(12.dp),
                shapeStyle = ButtonShapeStyle.Square,
            )
        }
    }
}

// =================================================================  Event Details Screen =================================================================

@Composable
fun EventDetailsScreen(
    eventId: String,
    onEditClick: () -> Unit,
    onLoginSignupClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(96.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.img_onboarding_3),
                contentDescription = "Event Image"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Event Details Headings
        Text(
            text = "Taylor & Travis’s Wedding",
            style = JasnifyTheme.typography.displaySmall.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center,
            color = ContentPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Hosted by Harsh",
            style = JasnifyTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = ContentBrand
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Event ID Chip Pill with Edit Action
        Row(
            modifier = Modifier
                .background(SurfaceSecondary, SquircleShape(100, CornerSmoothingDefault))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                    shape = SquircleShape(100, CornerSmoothingDefault)
                )
                .clip(shape = SquircleShape(100, CornerSmoothingDefault))
                .clickable { onEditClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Event ID : ${eventId.ifEmpty { "OBFEQO2" }}",
                style = JasnifyTheme.typography.labelMedium,
                color = ContentSecondary
            )
            Spacer(Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "Edit Event ID",
                tint = ContentPrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "To join & manage this event, please",
            style = JasnifyTheme.typography.headingMedium.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center,
            color = ContentPrimary,
        )

        Spacer(Modifier.height(16.dp))

        CustomTextButton(
            onClick = onLoginSignupClick,
            text = "Log in or Sign up",
            modifier = Modifier
                .fillMaxWidth(),
            size = ButtonSize.Large,
            shapeStyle = ButtonShapeStyle.Square,
            trailingIcon = painterResource(R.drawable.ic_right)
        )

        Spacer(modifier = Modifier.height(76.dp))

        Box(
            modifier = Modifier.height(160.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ){
            Icon(
                painter = painterResource(R.drawable.ic_app),
                contentDescription = "App Logo",
                tint = ContentTertiary
            )
        }
    }
}

// =================================================================  Slides Content =================================================================

@Composable
fun SlideOneContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Deepest Layer 3 Background Card
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(82.dp)
                .offset(y = 29.6.dp)
                .shadow(20.dp, ambientColor = ContentPrimary, spotColor = ContentPrimary, shape = SquircleShape(CornerLargeIncrease))
                .background(SurfacePrimary, SquircleShape(CornerLargeIncrease))
        )

        // Middle Layer 2 Background Card
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .height(82.dp)
                .offset(y = 15.2.dp)
                .shadow(20.dp, ambientColor = ContentPrimary, spotColor = ContentPrimary, shape = SquircleShape(CornerLargeIncrease))
                .background(SurfacePrimary, SquircleShape(CornerLargeIncrease))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .shadow(20.dp, ambientColor = ContentPrimary, spotColor = ContentPrimary, shape = SquircleShape(CornerLargeIncrease))
                .border(2.dp, ContentBrand.copy(alpha = 0.6f), SquircleShape(CornerLargeIncrease))
                .background(SurfacePrimary, SquircleShape(CornerLargeIncrease))
        ) {
            ExpenseCard(
                title = "R. Sound Studio",
                category = "Equipment Rentals",
                amount = "₹68,000",
                emoji = "🎼",
                showActions = false
            )
        }
    }
}

@Composable
fun SlideTwoContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 54.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .background(SurfacePrimary.copy(alpha = 0.5f), SquircleShape(CornerExtraLarge))
                .fillMaxWidth(0.95f)
                .border(2.dp, Color(0x55737399), SquircleShape(CornerExtraLarge))
                .padding(16.dp)
        ) {
            CateringItemChip(
                label = "Cheese Corn Balls",
                foodType = Dietary.Veg,
                isMultiSelect = false,
            )
            CateringItemChip(
                label = "Chicken Malai Tikka",
                foodType = Dietary.NonVeg,
                isMultiSelect = false,
            )
        }
    }
}

@Composable
fun SlideThreeContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Vendor Bubble 1: Chef
        VendorAvatarWithRating(
            painter = painterResource(id = R.drawable.img_onboarding_2),
            rating = "4.6",
            size = 98.dp,
            modifier = Modifier.offset(x = (-35).dp, y = (-60).dp)
        )

        // Vendor Bubble 2: Photographer
        VendorAvatarWithRating(
            painter = painterResource(id = R.drawable.img_onboarding_3),
            rating = "4.9",
            size = 128.dp,
            modifier = Modifier.offset(x = 115.dp, y = (-30).dp)
        )

        // Vendor Bubble 3: DJ
        VendorAvatarWithRating(
            painter = painterResource(id = R.drawable.img_onboarding_1),
            rating = "4.4",
            size = 108.dp,
            modifier = Modifier.offset(x = (-120).dp, y = 40.dp)
        )

        // Vendor Bubble 4: Cars
        VendorAvatarWithRating(
            painter = painterResource(id = R.drawable.img_onboarding_4),
            rating = "4.8",
            size = 80.dp,
            modifier = Modifier.offset(x = 20.dp, y = 80.dp)
        )
    }
}

// =================================================================  Helper function =================================================================

@Composable
fun VendorAvatarWithRating(
    painter: Painter,
    rating: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val sizeValue = size.value

    val badgeOffset = -(sizeValue * 0.03f).dp
    val badgeShadow = (sizeValue * 0.04f).dp
    val horizontalPadding = (sizeValue * 0.0665f).dp
    val verticalPadding = (sizeValue * 0.0333f).dp
    val badgeSpacing = (sizeValue * 0.0333f).dp
    val starIconSize = (sizeValue * 0.12f).dp
    val ratingFontSize = (sizeValue * 0.11f).sp

    val avatarShadow = (sizeValue * 0.06f).dp
    val avatarBorder = (sizeValue * 0.02f).dp

    Box(
        modifier = modifier.size(size)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(avatarShadow, CircleShape)
                .border(avatarBorder, Color(0x55737380), CircleShape)
                .clip(CircleShape)
                .background(ContentSecondary)
        ) {
            Image(
                painter = painter,
                contentDescription = "Vendor Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = badgeOffset, y = badgeOffset)
                .shadow(badgeShadow, RoundedCornerShape(100))
                .background(Color(0xFF009B0A), RoundedCornerShape(100))
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(badgeSpacing)
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = "Star",
                tint = ContentInvPrimary,
                modifier = Modifier.size(starIconSize)
            )
            Text(
                text = rating,
                color = ContentInvPrimary,
                fontSize = ratingFontSize,
                fontWeight = FontWeight.Medium,
                style = JasnifyTheme.typography.labelSmall.copy(
                    fontSize = ratingFontSize,
                    lineHeight = ratingFontSize * 1.2f
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingTypePreview() {
    val navController = rememberNavController()
    OnboardingType(navController = navController)
}