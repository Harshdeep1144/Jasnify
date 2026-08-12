package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import com.harshdeep.jasnify.R
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.harshdeep.jasnify.domain.model.InvitationCardData
import com.harshdeep.jasnify.domain.model.TextElement
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun InvitationCardItem(
    modifier: Modifier = Modifier,
    data: InvitationCardData = InvitationCardData(),
    pageOffset: Float = 0f,
    isEditable: Boolean = false,
    showControls: Boolean = false,
    forCapture: Boolean = false,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onUpdate: (InvitationCardData) -> Unit = {}
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

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .onGloballyPositioned { canvasSize = it.size }
            .then(
                if (!forCapture) {
                    Modifier
                        .clip(SquircleShape(CornerLargeIncrease))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(0.16f),
                            SquircleShape(CornerLargeIncrease)
                        )
                } else Modifier
            )
            .background(Color(data.backgroundColorHex))
    ) {
        // Background Image
        Image(
            painter = painterResource(id = data.backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (canvasSize.width > 0 && canvasSize.height > 0) {
            val density = LocalDensity.current.density
            val canvasWidthPx = canvasSize.width.toFloat()
            val canvasHeightPx = canvasSize.height.toFloat()
            
            // Convert pixels to DP for scale calculation
            val canvasWidthDp = canvasWidthPx / density

            // Baseline width for scaling text relative to canvas size
            // We use 280dp as the standard reference width
            val scaleFactor = canvasWidthDp / 284f

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = (40 * scaleFactor).dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                data.elements.sortedBy { it.yRatio }.forEach { element ->
                    RenderCardTextElement(
                        element = element,
                        scaleFactor = scaleFactor,
                        isEditable = isEditable,
                        onElementUpdate = { updatedElement ->
                            val updatedElements = data.elements.map {
                                if (it.id == updatedElement.id) updatedElement else it
                            }
                            onUpdate(data.copy(elements = updatedElements))
                        }
                    )
                }
            }

            if (showControls) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding((16 * scaleFactor).dp),
                    horizontalArrangement = Arrangement.spacedBy((8 * scaleFactor).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like Button
                    Surface(
                        modifier = Modifier
                            .size((36 * scaleFactor).dp)
                            .clickable { onLikeClick() },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart),
                                contentDescription = "Like",
                                tint = if (isLiked) Color.Red else Color.White,
                                modifier = Modifier.size((18 * scaleFactor).dp)
                            )
                        }
                    }

                    // Share Button
                    Surface(
                        modifier = Modifier
                            .size((36 * scaleFactor).dp)
                            .clickable { onShareClick() },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_share),
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size((18 * scaleFactor).dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderCardTextElement(
    element: TextElement,
    scaleFactor: Float,
    isEditable: Boolean,
    onElementUpdate: (TextElement) -> Unit
) {
    val density = LocalDensity.current.density

    val textStyle = TextStyle(
        fontFamily = element.fontStyle.fontFamily,
        fontSize = (element.fontSizeSp * scaleFactor).sp,
        fontWeight = if (element.isBold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (element.isItalic) FontStyle.Italic else FontStyle.Normal,
        textDecoration = if (element.isUnderline) TextDecoration.Underline else TextDecoration.None,
        color = Color(element.colorHex),
        textAlign = element.textAlign,
        letterSpacing = (element.letterSpacingSp * scaleFactor).sp,
        lineHeight = if (element.lineHeightSp > 0) (element.lineHeightSp * scaleFactor).sp else TextUnit.Unspecified,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        ),
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = (element.verticalPaddingSp * scaleFactor / 2).dp)
            .padding(horizontal = (12 * scaleFactor).dp),
        contentAlignment = Alignment.Center
    ) {
        if (isEditable) {
            BasicTextField(
                value = element.text,
                onValueChange = { onElementUpdate(element.copy(text = it)) },
                textStyle = textStyle,
                modifier = Modifier.widthIn(min = 20.dp),
                cursorBrush = SolidColor(textStyle.color),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (element.text.isEmpty()) {
                            Text(
                                text = "Type...",
                                style = textStyle.copy(color = textStyle.color.copy(alpha = 0.3f))
                            )
                        }
                        innerTextField()
                    }
                }
            )
        } else {
            Text(
                text = element.text,
                style = textStyle
            )
        }
    }
}
