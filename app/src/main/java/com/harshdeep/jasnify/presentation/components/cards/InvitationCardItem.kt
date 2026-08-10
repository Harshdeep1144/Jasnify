package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.InvitationCard
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun InvitationCardItem(
    modifier: Modifier = Modifier,
    data: InvitationCard = InvitationCard(),
    pageOffset: Float = 0f,
    isEditable: Boolean = false,
    onUpdate: (InvitationCard) -> Unit = {}
) {
    val scale = lerp(
        start = 0.9f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(0f, 1f)
    )
    
    val alpha = lerp(
        start = 0.5f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(0f, 1f)
    )

    // Base reference size for proportional scaling
    val refWidth = 280f
    
    BoxWithConstraints(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clip(SquircleShape(CornerLargeIncrease))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.16f), SquircleShape(CornerLargeIncrease))
            .background(SurfacePrimary)
    ) {
        val width = maxWidth.value
        
        // Scale factor based on width ratio
        val scaleFactor = width / refWidth
        
        val verticalPadding = 48.dp * scaleFactor
        val horizontalPadding = 32.dp * scaleFactor

        Image(
            painter = painterResource(id = data.backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1.2f))
            
            EditableText(
                value = data.primaryHeader,
                onValueChange = { onUpdate(data.copy(primaryHeader = it)) },
                isEditable = isEditable,
                style = JasnifyTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (1.1f * scaleFactor).sp,
                    fontSize = (10 * scaleFactor).sp,
                    color = Color(data.contentColorHex),
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height((8 * scaleFactor).dp))
            
            EditableText(
                value = data.names,
                onValueChange = { onUpdate(data.copy(names = it)) },
                isEditable = isEditable,
                style = TextStyle(
                    fontFamily = Pattaya,
                    fontSize = (38 * scaleFactor).sp,
                    color = Color(data.nameColorHex),
                    textAlign = TextAlign.Center,
                    lineHeight = (38 * 1.1 * scaleFactor).sp
                )
            )
            
            Spacer(modifier = Modifier.height((8 * scaleFactor).dp))
            
            EditableText(
                value = data.description,
                onValueChange = { onUpdate(data.copy(description = it)) },
                isEditable = isEditable,
                style = JasnifyTheme.typography.labelXSmall.copy(
                    lineHeight = (9 * 1.5 * scaleFactor).sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (0.5 * scaleFactor).sp,
                    fontSize = (9 * scaleFactor).sp,
                    color = Color(data.secondaryContentColorHex),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = (4 * scaleFactor).dp)
            )
            
            Spacer(modifier = Modifier.height((20 * scaleFactor).dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = data.day,
                    style = JasnifyTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium, 
                        fontSize = (10 * scaleFactor).sp
                    ),
                    color = Color(data.contentColorHex)
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = (10 * scaleFactor).dp)
                        .width((1 * scaleFactor).dp)
                        .height((24 * scaleFactor).dp)
                        .background(Color.LightGray.copy(alpha = 0.6f))
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = data.date,
                        style = JasnifyTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold, 
                            fontSize = (24 * scaleFactor).sp
                        ),
                        color = Color(data.contentColorHex)
                    )
                    Text(
                        text = data.month,
                        style = JasnifyTheme.typography.labelXSmall.copy(
                            fontWeight = FontWeight.Bold, 
                            fontSize = (8 * scaleFactor).sp
                        ),
                        color = Color(data.contentColorHex)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = (10 * scaleFactor).dp)
                        .width((1 * scaleFactor).dp)
                        .height((24 * scaleFactor).dp)
                        .background(Color.LightGray.copy(alpha = 0.6f))
                )
                Text(
                    text = data.year,
                    style = JasnifyTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium, 
                        fontSize = (10 * scaleFactor).sp
                    ),
                    color = Color(data.contentColorHex)
                )
            }
            
            Spacer(modifier = Modifier.height((20 * scaleFactor).dp))
            
            EditableText(
                value = data.subHeader,
                onValueChange = { onUpdate(data.copy(subHeader = it)) },
                isEditable = isEditable,
                style = JasnifyTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (0.5 * scaleFactor).sp,
                    fontSize = (10 * scaleFactor).sp,
                    color = Color(data.contentColorHex),
                    textAlign = TextAlign.Center
                )
            )
            
            EditableText(
                value = data.timeAndVenue,
                onValueChange = { onUpdate(data.copy(timeAndVenue = it)) },
                isEditable = isEditable,
                style = JasnifyTheme.typography.labelXSmall.copy(
                    fontWeight = FontWeight.Medium, 
                    fontSize = (9 * scaleFactor).sp,
                    color = Color(data.secondaryContentColorHex),
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height((20 * scaleFactor).dp))
            
            EditableText(
                value = data.rsvpDeadline,
                onValueChange = { onUpdate(data.copy(rsvpDeadline = it)) },
                isEditable = isEditable,
                style = JasnifyTheme.typography.labelXSmall.copy(
                    fontWeight = FontWeight.Medium, 
                    fontSize = (8 * scaleFactor).sp,
                    color = Color(data.secondaryContentColorHex),
                    textAlign = TextAlign.Center
                )
            )
            
            EditableText(
                value = data.rsvpContact,
                onValueChange = { onUpdate(data.copy(rsvpContact = it)) },
                isEditable = isEditable,
                style = JasnifyTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (0.2 * scaleFactor).sp,
                    fontSize = (10 * scaleFactor).sp,
                    color = Color(data.contentColorHex),
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Jasnify",
                fontFamily = Pattaya,
                fontSize = (16 * scaleFactor).sp,
                color = ContentTertiary.copy(alpha = 0.5f)
            )
        }

        // Heart Button
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding((16 * scaleFactor).dp)
                .size((36 * scaleFactor).dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.5f),
            shadowElevation = 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_heart),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size((18 * scaleFactor).dp)
                )
            }
        }
    }
}

@Composable
private fun EditableText(
    value: String,
    onValueChange: (String) -> Unit,
    isEditable: Boolean,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    if (isEditable) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = style,
            modifier = modifier.fillMaxWidth(),
            cursorBrush = SolidColor(style.color),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (value.isEmpty()) {
                        Text(
                            text = "Type here...",
                            style = style.copy(color = style.color.copy(alpha = 0.3f))
                        )
                    }
                    innerTextField()
                }
            }
        )
    } else {
        Text(
            text = value,
            style = style,
            modifier = modifier
        )
    }
}
