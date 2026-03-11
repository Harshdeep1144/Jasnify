package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun FooterJansify(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(250.dp)
                .background(color = Color.Transparent)
                .align(Alignment.BottomCenter)
        ) {
            Image(
                painter = painterResource(R.drawable.bg_footer_pattern),
                contentDescription = "footer background",
                alignment = Alignment.Center,
                colorFilter = ColorFilter.tint(Color(0x1A557373).copy(alpha = 0.9f)),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
        }

        Column (
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(R.drawable.ic_app),
                contentDescription = "App Logo",
                colorFilter = ColorFilter.tint(color = Color(0xFF9BAAAA))
            )
            Text(
                text = "Your smart way \n" +
                        "\t\t to celebrate.",
                style = JasnifyTheme.typography.displayMedium,
                color = Color(0xFF9BAAAA)
            )
            Text(
                text = "Designed with \uD83E\uDD0D in India",
                style = JasnifyTheme.typography.headingSmall,
                color = Color(0xFF9BAAAA)
            )
        }

    }
}




@Preview(showBackground = true)
@Composable
fun FooterJasnifyPreview() {
    FooterJansify()
}