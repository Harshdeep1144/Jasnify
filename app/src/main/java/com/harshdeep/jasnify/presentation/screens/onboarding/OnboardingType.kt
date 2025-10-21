package com.harshdeep.jasnify.presentation.screens.onboarding

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.BackgroundBrand
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape

@Composable
fun OnboardingType(navController: NavController) {
    Column (
        modifier = Modifier.fillMaxSize()
            .background(BackgroundBrand)
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val images = listOf(
            R.drawable.carousel_img1,
            R.drawable.carousel_img2,
            R.drawable.carousel_img3,
            R.drawable.carousel_img4
        )
        val pageState = rememberPagerState (pageCount = { images.size })

        // Auto-slide
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000)
                val nextPage = (pageState.currentPage + 1) % images.size
                pageState.animateScrollToPage(nextPage)
            }
        }

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .clip(SquircleShape(CornerLarge, CornerSmoothingDefault)                )
        ) {
            HorizontalPager(
                state = pageState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = "carousel image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dots indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            ) {
                val currentPage = pageState.currentPage
                images.forEachIndexed { index, _ ->
                    val color = if (index == currentPage) Color.Black
                    
                    else Color.Gray
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(SquircleShape(100, CornerSmoothingDefault))
                            .background(color)
                    )
                    if (index != images.lastIndex) {
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }
            }


        }

        Spacer(Modifier.height(16.dp))

        CustomTextButton(
            onClick = {
                navController.navigate(Screen.LoginOrSignUp.route)
            },
            text = "Create a new event",
            size = ButtonSize.Large,
            type = ButtonType.Primary,
            shapeStyle = ButtonShapeStyle.Square,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = painterResource(R.drawable.ic_right)
        )

        OrDivider(divider = false)

        CustomTextButton(
            onClick = {
                navController.navigate(Screen.LoginOrSignUp.route)
            },
            text = "Have a code?",
            size = ButtonSize.Large,
            type = ButtonType.Primary,
            shapeStyle = ButtonShapeStyle.Square,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier.height(100.dp)
                .width(120.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(R.drawable.ic_app),
                contentDescription = "App Logo"
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
