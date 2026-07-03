package com.harshdeep.jasnify.presentation.screens.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import kotlinx.coroutines.launch

import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.chip.CateringItemChip
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.BackgroundBrand
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@Composable
fun OnboardingType(
    navController: NavController
) {
    val pageState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (pageState.currentPage > 0) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .statusBarsPadding()
                ) {
                    CustomTopBar(
                        onBackClick = {
                            coroutineScope.launch {
                                pageState.animateScrollToPage(pageState.currentPage - 1)
                            }
                        },
                        buttonStyle = ButtonBackground.OPAQUE,
                        backIcon = TopIcon.Predefined.BACK_2
                    )
                }
            } else {
                Spacer(modifier = Modifier.statusBarsPadding().height(64.dp))
            }
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundBrand
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                        modifier = Modifier
                            .fillMaxWidth(),
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
                            modifier = Modifier
                                .fillMaxWidth(),
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
                            onClick = {
                                navController.navigate(Screen.LoginOrSignUp.route)
                            },
                            text = "Have an Event ID?",
                            shapeStyle = ButtonShapeStyle.Square,
                            type = ButtonType.Secondary
                        )

                    }
                }
            }
        }
    }
}


// ----------------------------------------------------------------  Slides Content ----------------------------------------------------------


@Composable
fun SlideOneContent() {
    Box(
        modifier = Modifier.fillMaxSize()
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
        modifier = Modifier.fillMaxSize()
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


//--------------------------------- Helper function ------------------------------------------

@Composable
fun VendorAvatarWithRating(
    painter: Painter,
    rating: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    // Treat 100.dp as the reference baseline
    val sizeValue = size.value

    // Proportional dimensions based on the baseline
    val badgeOffset = -(sizeValue * 0.03f).dp           // 100dp size -> -3dp offset
    val badgeShadow = (sizeValue * 0.04f).dp            // 100dp size -> 4dp shadow
    val horizontalPadding = (sizeValue * 0.0665f).dp    // 100dp size -> 6.65dp padding
    val verticalPadding = (sizeValue * 0.0333f).dp      // 100dp size -> 3.33dp padding
    val badgeSpacing = (sizeValue * 0.0333f).dp         // 100dp size -> 3.33dp item spacing
    val starIconSize = (sizeValue * 0.12f).dp           // 100dp size -> 12dp star size
    val ratingFontSize = (sizeValue * 0.11f).sp         // 100dp size -> 11sp text size

    // Proportional modifications for Avatar decoration boundaries
    val avatarShadow = (sizeValue * 0.06f).dp
    val avatarBorder = (sizeValue * 0.02f).dp

    Box(
        modifier = modifier.size(size)
    ) {
        // High fidelity circular cropped real vendor photo
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

        // Dynamically scaled ratings badge
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