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
import coil.compose.AsyncImage
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.TextElement
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    data: CardData = CardData(),
    pageOffset: Float = 0f,
    isEditable: Boolean = false,
    showControls: Boolean = false,
    forCapture: Boolean = false,
    isLiked: Boolean = false,
    onLikeClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onUpdate: (CardData) -> Unit = {}
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
    val density = LocalDensity.current.density
    val cardWidthDp = if (canvasSize.width > 0) canvasSize.width / density else 0f

    // Determine corner size based on width thresholds
    val cornerRadius = when {
        cardWidthDp >= 390f -> CornerExtraLarge
        cardWidthDp in 1f..180f -> CornerMedium
        else -> CornerLargeIncrease // Covers <= 280dp as well as fallback between 180dp and 390dp
    }
    val cardShape = SquircleShape(cornerRadius)

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
                        .clip(cardShape)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(0.16f),
                            cardShape
                        )
                } else Modifier
            )
            .background(Color(data.backgroundColorHex.toInt()))
    ) {
        // Background Image
        if (data.backgroundUrl != null) {
            AsyncImage(
                model = data.backgroundUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = data.backgroundRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        if (canvasSize.width > 0 && canvasSize.height > 0) {
            val canvasWidthPx = canvasSize.width.toFloat()
            val canvasWidthDp = canvasWidthPx / density

            val scaleFactor = canvasWidthDp / 284f

            // Separate editable vs uneditable elements to preserve true Y positioning
            val editableElements = data.elements.filter { it.isEditable }.sortedBy { it.yRatio }
            val uneditableElements = data.elements.filter { !it.isEditable }

            // 1. Center Editable Card Elements
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = (20 * scaleFactor).dp, vertical = (40 * scaleFactor).dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                editableElements.forEach { element ->
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

            // 2. Position Uneditable Elements (like "Jasnify" watermark) at absolute yRatio
            uneditableElements.forEach { element ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val yPosDp = (canvasSize.height * element.yRatio / density).dp
                    Box(
                        modifier = Modifier
                            .offset(y = yPosDp)
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        RenderCardTextElement(
                            element = element,
                            scaleFactor = scaleFactor,
                            isEditable = false,
                            onElementUpdate = {}
                        )
                    }
                }
            }

            // 3. Floating Overlay Controls
            if (showControls) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like Button
                    if (onLikeClick != null) {
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .noRippleClickable(onLikeClick),
                            shape = CircleShape,
                            color = Color(0x99000000),
                            shadowElevation = 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_top_bar_heart),
                                    contentDescription = "Like",
                                    tint = if (isLiked) Color.Unspecified else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Share Button
                    if (onShareClick != null) {
                        CustomIconButton(
                            onClick = onShareClick,
                            icon = painterResource(R.drawable.ic_share),
                            size = ButtonSize.Small,
                            contentColor = ContentInvPrimary,
                            containerColor = Color(0x99000000)
                        )
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
        color = Color(element.colorHex.toInt()),
        textAlign = element.textAlign.toComposeTextAlign(),
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
            .padding(vertical = (element.verticalPaddingSp * scaleFactor / 2).dp),
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
