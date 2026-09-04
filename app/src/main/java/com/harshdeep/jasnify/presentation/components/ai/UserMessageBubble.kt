package com.harshdeep.jasnify.presentation.components.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.domain.model.AiMessage
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerSmall
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

@Composable
fun UserMessageBubble(
    message: AiMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .clip(
                    shape = SquircleShape(
                        topStart = CornerExtraLarge,
                        topEnd = CornerExtraLarge,
                        bottomStart = CornerExtraLarge,
                        bottomEnd = CornerSmall,
                        cornerSmoothing = CornerSmoothingDefault
                    )
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFA033FF), Color(0xFF7000FF))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .widthIn(max = 280.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = JasnifyTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            )
        }
    }
}
