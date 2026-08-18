package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun ImportContactsBanner(
    onAllowAccessClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = SurfaceBrandSecondary, shape = SquircleShape(CornerLarge))
            .border(width = 1.dp, color = ContentBrandDark, shape = SquircleShape(CornerLarge))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = null,
                    tint = ContentBrandDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Import multiple contacts at once",
                    style = JasnifyTheme.typography.headingMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = ContentBrandDark
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "It will help you manage your guests smartly.",
                style = JasnifyTheme.typography.labelMedium,
                color = ContentBrandDark,
                modifier = Modifier.padding(start = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    onClick = onDismissClick,
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_cross)),
                    iconSize = 18.dp,
                    backgroundStyle = ButtonBackground.TRANSPARENT
                )
                Spacer(modifier = Modifier.width(4.dp))

                CustomTextButton(
                    onClick = onAllowAccessClick,
                    text = "Allow Access",
                    containerColor = ContentBrandDark,
                    contentColor = ContentInvPrimary,
                    shapeStyle = ButtonShapeStyle.Square,
                    size = ButtonSize.Small
                )
            }
        }
    }
}