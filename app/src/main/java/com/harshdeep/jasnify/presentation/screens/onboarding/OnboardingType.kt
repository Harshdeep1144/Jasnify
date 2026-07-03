package com.harshdeep.jasnify.presentation.screens.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.chip.CateringItemChip
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape

@Composable
fun OnboardingType(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val pageState = rememberPagerState(pageCount = { 3 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBrand) // Fits the beige brand color from your layout system
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Top Header Area (Contains conditional back button) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pageState.currentPage > 0) {
                CustomIconButton(
                    onClick = {
                        coroutineScope.launch {
                            pageState.animateScrollToPage(pageState.currentPage - 1)
                        }
                    },
                    icon = rememberVectorPainter(Icons.Default.ArrowBack),
                    size = ButtonSize.Small,
                    type = ButtonType.Tertiary,
                    shapeStyle = ButtonShapeStyle.Round,
                    containerColor = SurfacePrimary, // Off-white/white circular button base
                    contentColor = ContentPrimary
                )
            }
        }

        // --- Main Presentation Pager ---
        HorizontalPager(
            state = pageState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Customized dynamic Illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (page) {
                        0 -> OnboardingIllustrationPage0()
                        1 -> OnboardingIllustrationPage1()
                        2 -> OnboardingIllustrationPage2()
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Heading matching screenshots' colors
                Text(
                    text = when (page) {
                        0 -> "Track Expenses using AI"
                        1 -> "Manage Catering Menu"
                        else -> "Explore Vendors & Venues"
                    },
                    style = JasnifyTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F4D4A) // Elegant dark teal/forest green shade
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Explanatory Body text
                Text(
                    text = when (page) {
                        0 -> "Set your budget, add expenses, and AI keeps track of all your event spends smartly."
                        1 -> "Add favourite items to the menu, so you don't miss out anything."
                        else -> "Connect with reliable vendors who offer top-notch services."
                    },
                    style = JasnifyTheme.typography.bodyLarge,
                    color = ContentSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // --- Active Page Progress Dots (First active expands to pill) ---
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            repeat(3) { index ->
                val isActive = pageState.currentPage == index
                val widthAnimated by animateDpAsState(
                    targetValue = if (isActive) 24.dp else 8.dp,
                    label = "IndicatorWidth"
                )
                val dotColor = if (isActive) Color(0xFF38665B) else Color(0xFFC0BEB4)

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = widthAnimated, height = 8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Dynamic Navigation & CTA Controls ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (pageState.currentPage < 2) {
                // "Next" Pill Button
                CustomTextButton(
                    onClick = {
                        coroutineScope.launch {
                            pageState.animateScrollToPage(pageState.currentPage + 1)
                        }
                    },
                    text = "Next",
                    size = ButtonSize.Medium,
                    type = ButtonType.Primary,
                    shapeStyle = ButtonShapeStyle.Round,
                    trailingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowForward),
                    containerColor = Color(0xFF4C7368), // Dark teal/green color as in screenshots
                    contentColor = Color.White
                )
            } else {
                // Final onboarding CTA layout
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomTextButton(
                        onClick = {
                            navController.navigate(Screen.LoginOrSignUp.route)
                        },
                        text = "Create a new event",
                        size = ButtonSize.Medium,
                        type = ButtonType.Primary,
                        shapeStyle = ButtonShapeStyle.Square, // Premium squircle look
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color(0xFF4C7368),
                        contentColor = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "OR",
                        style = JasnifyTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ContentSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextButton(
                        onClick = {
                            navController.navigate(Screen.LoginOrSignUp.route)
                        },
                        text = "Have an Event ID?",
                        size = ButtonSize.Small,
                        type = ButtonType.Secondary,
                        shapeStyle = ButtonShapeStyle.Round,
                        containerColor = Color(0xFFD3E5DE), // Light blue-teal tint
                        contentColor = Color(0xFF2B4D43) // Matching dark teal text
                    )
                }
            }
        }
    }
}

// --- Stylized Reusable Phone Mockup Frame ---
@Composable
private fun PhoneMockupShell(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .height(300.dp)
            .background(Color.Transparent)
            .border(width = 3.dp, color = Color(0xFFD4D2C9), shape = RoundedCornerShape(32.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Stylized screen interior canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .background(Color(0xFFE8E6DF), shape = RoundedCornerShape(26.dp))
        ) {
            // Front camera dot mockup
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .size(8.dp)
                    .background(Color(0xFFBCB9B0), shape = CircleShape)
            )

            // Faux system details inside phone structure
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(Color(0xFFDFDDD4), RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(12.dp).background(Color(0xFFDFDDD4), RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(12.dp).background(Color(0xFFDFDDD4), RoundedCornerShape(4.dp)))
            }

            // Overlay actual components smoothly
            content()
        }
    }
}

// --- Page 1 Spends Illustration (Deck Stack) ---
@Composable
private fun OnboardingIllustrationPage0() {
    PhoneMockupShell {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.94f)
                .offset(y = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Simulated visual card shadows / layers underneath (mimicking stack layout)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(60.dp)
                    .offset(y = 16.dp)
                    .graphicsLayer(alpha = 0.4f, shadowElevation = 2f)
                    .background(SurfacePrimary, shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .height(68.dp)
                    .offset(y = 8.dp)
                    .graphicsLayer(alpha = 0.7f, shadowElevation = 4f)
                    .background(SurfacePrimary, shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
            )

            // Foreground ExpenseCard
            ExpenseCard(
                title = "R. Sound Studio",
                category = "Equipment Rentals",
                amount = "₹68,000",
                emoji = "🎼",
                showActions = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(shadowElevation = 8f)
            )
        }
    }
}

// --- Page 2 Catering Menu Illustration ---
@Composable
private fun OnboardingIllustrationPage1() {
    PhoneMockupShell {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.95f)
                .offset(y = 12.dp)
                .graphicsLayer(shadowElevation = 8f, shape = SquircleShape(CornerLarge, CornerSmoothingDefault), clip = true)
                .background(SurfacePrimary)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
                .padding(12.dp)
        ) {
            Column {
                CateringItemChip(
                    label = "Cheese Corn Balls",
                    foodType = Dietary.Veg,
                    isMultiSelect = true,
                    checked = true,
                    onCheckedChange = {}
                )
                Spacer(modifier = Modifier.height(4.dp))
                CateringItemChip(
                    label = "Chicken Malai Tikka",
                    foodType = Dietary.NonVeg,
                    isMultiSelect = true,
                    checked = true,
                    onCheckedChange = {}
                )
            }
        }
    }
}

// --- Page 3 Scattered Floating Vendor Cards ---
@Composable
private fun OnboardingIllustrationPage2() {
    PhoneMockupShell {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Chef profile (Top Center-Left)
            FloatingVendorAvatar(
                emoji = "🍳",
                rating = "4.6",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = (-35).dp, y = 30.dp)
            )

            // Photographer profile (Center Right)
            FloatingVendorAvatar(
                emoji = "📸",
                rating = "4.9",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-10).dp, y = (-25).dp)
            )

            // DJ profile (Center Left)
            FloatingVendorAvatar(
                emoji = "🎧",
                rating = "4.4",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 10.dp, y = 25.dp)
            )

            // Luxury Cars profile (Bottom Center-Right)
            FloatingVendorAvatar(
                emoji = "🚗",
                rating = "4.8",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = 25.dp, y = (-35).dp)
            )
        }
    }
}

// --- Staggered Vendor Avatar Card Helper ---
@Composable
private fun FloatingVendorAvatar(
    emoji: String,
    rating: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .graphicsLayer(shadowElevation = 8f, shape = CircleShape)
    ) {
        // Base Circle Container
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape = CircleShape)
                .border(1.dp, Color(0xFFE3E1D7), CircleShape)
        ) {
            Text(text = emoji, fontSize = 28.sp)
        }

        // Mini Green Rating Capsule
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 2.dp)
                .background(Color(0xFF00AA44), shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "★",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = rating,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Outfit
            )
        }
    }
}

// --- Live Layout Preview System ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingTypePreview() {
    val navController = rememberNavController()
    OnboardingType(navController = navController)
}