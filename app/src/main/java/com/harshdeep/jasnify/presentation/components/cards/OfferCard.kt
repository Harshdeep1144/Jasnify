package com.harshdeep.jasnify.presentation.components.cards

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

enum class OfferCardType {
    FULL, COMPACT
}

enum class TicketNotchPosition {
    LEFT_RIGHT, TOP_BOTTOM
}

class TicketShape(
    private val cornerRadius: Dp = CornerLargeIncrease,
    private val notchRadius: Dp = 12.dp,
    private val notchCenterPx: Float? = null,
    private val defaultNotchFraction: Float = 0.5f,
    private val notchPosition: TicketNotchPosition = TicketNotchPosition.LEFT_RIGHT
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val cornerPx = with(density) { cornerRadius.toPx() }
        val notchPx = with(density) { notchRadius.toPx() }

        if (notchPosition == TicketNotchPosition.LEFT_RIGHT) {
            val notchY = notchCenterPx ?: (size.height * defaultNotchFraction)

            path.apply {
                reset()
                moveTo(cornerPx, 0f)
                lineTo(size.width - cornerPx, 0f)
                arcTo(
                    rect = Rect(size.width - 2 * cornerPx, 0f, size.width, 2 * cornerPx),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(size.width, notchY - notchPx)
                // Inward right notch
                arcTo(
                    rect = Rect(
                        size.width - notchPx,
                        notchY - notchPx,
                        size.width + notchPx,
                        notchY + notchPx
                    ),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                lineTo(size.width, size.height - cornerPx)
                arcTo(
                    rect = Rect(
                        size.width - 2 * cornerPx,
                        size.height - 2 * cornerPx,
                        size.width,
                        size.height
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(cornerPx, size.height)
                arcTo(
                    rect = Rect(0f, size.height - 2 * cornerPx, 2 * cornerPx, size.height),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(0f, notchY + notchPx)
                // Inward left notch
                arcTo(
                    rect = Rect(
                        -notchPx,
                        notchY - notchPx,
                        notchPx,
                        notchY + notchPx
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                lineTo(0f, cornerPx)
                arcTo(
                    rect = Rect(0f, 0f, 2 * cornerPx, 2 * cornerPx),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                close()
            }
        } else {
            val notchX = notchCenterPx ?: (size.width * defaultNotchFraction)

            path.apply {
                reset()
                moveTo(cornerPx, 0f)
                lineTo(notchX - notchPx, 0f)
                // Inward top notch
                arcTo(
                    rect = Rect(
                        notchX - notchPx,
                        -notchPx,
                        notchX + notchPx,
                        notchPx
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                lineTo(size.width - cornerPx, 0f)
                arcTo(
                    rect = Rect(size.width - 2 * cornerPx, 0f, size.width, 2 * cornerPx),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(size.width, size.height - cornerPx)
                arcTo(
                    rect = Rect(
                        size.width - 2 * cornerPx,
                        size.height - 2 * cornerPx,
                        size.width,
                        size.height
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(notchX + notchPx, size.height)
                // Inward bottom notch
                arcTo(
                    rect = Rect(
                        notchX - notchPx,
                        size.height - notchPx,
                        notchX + notchPx,
                        size.height + notchPx
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                lineTo(cornerPx, size.height)
                arcTo(
                    rect = Rect(0f, size.height - 2 * cornerPx, 2 * cornerPx, size.height),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(0f, cornerPx)
                arcTo(
                    rect = Rect(0f, 0f, 2 * cornerPx, 2 * cornerPx),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                close()
            }
        }

        return Outline.Generic(path)
    }
}

/**
 * Draws an all-sides ambient shadow around any custom [Shape] path.
 */
fun Modifier.allSidesShadow(
    shape: Shape,
    elevation: Dp = 4.dp,
    color: Color = Color.Black.copy(alpha = 0.12f),
): Modifier = this.drawBehind {
    if (elevation <= 0.dp) return@drawBehind

    val shadowRadius = elevation.toPx()

    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = color.toArgb()
        frameworkPaint.maskFilter = BlurMaskFilter(
            shadowRadius,
            BlurMaskFilter.Blur.NORMAL
        )

        val outline = shape.createOutline(size, layoutDirection, this)

        when (outline) {
            is Outline.Generic -> {
                canvas.drawPath(outline.path, paint)
            }
            is Outline.Rounded -> {
                val path = Path().apply { addRoundRect(outline.roundRect) }
                canvas.drawPath(path, paint)
            }
            is Outline.Rectangle -> {
                val path = Path().apply { addRect(outline.rect) }
                canvas.drawPath(path, paint)
            }
        }
    }
}

@Composable
fun OfferCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    type: OfferCardType = OfferCardType.FULL,
    code: String? = null,
    progress: String? = null,
    elevation: Dp = 4.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.12f),
    onViewDetailsClick: () -> Unit = {},
    onCopyCodeClick: (String) -> Unit = {},
    cardBgColor: Color = SurfacePrimary,
    accentColor: Color = ContentBrandDark,
    iconBgColor: Color = SurfaceBrandPrimary,
) {
    var notchCenterPx by remember(type) { mutableFloatStateOf(-1f) }

    val ticketShape = if (type == OfferCardType.COMPACT) {
        TicketShape(
            cornerRadius = CornerLargeIncrease,
            notchRadius = 12.dp,
            notchCenterPx = if (notchCenterPx > 0f) notchCenterPx else null,
            notchPosition = TicketNotchPosition.TOP_BOTTOM
        )
    } else {
        TicketShape(
            cornerRadius = CornerLargeIncrease,
            notchRadius = 12.dp,
            notchCenterPx = if (notchCenterPx > 0f) notchCenterPx else null,
            notchPosition = TicketNotchPosition.LEFT_RIGHT
        )
    }

    Card(
        onClick = onViewDetailsClick,
        modifier = modifier
            .fillMaxWidth()
            .allSidesShadow(
                shape = ticketShape,
                elevation = elevation,
                color = shadowColor
            ),
        shape = ticketShape,
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        if (type == OfferCardType.COMPACT) {
            OfferCardCompactContent(
                title = title,
                description = description,
                progress = progress,
                accentColor = accentColor,
                iconBgColor = iconBgColor,
                onDividerPositioned = { notchCenterPx = it }
            )
        } else {
            OfferCardVerticalContent(
                title = title,
                description = description,
                type = type,
                code = code,
                accentColor = accentColor,
                iconBgColor = iconBgColor,
                onViewDetailsClick = onViewDetailsClick,
                onCopyCodeClick = onCopyCodeClick,
                onDividerPositioned = { notchCenterPx = it }
            )
        }
    }
}

@Composable
private fun OfferCardVerticalContent(
    title: String,
    description: String,
    type: OfferCardType,
    code: String?,
    accentColor: Color,
    iconBgColor: Color,
    onViewDetailsClick: () -> Unit,
    onCopyCodeClick: (String) -> Unit,
    onDividerPositioned: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(SquircleShape(CornerLarge))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalOffer,
                    contentDescription = null,
                    tint = ContentInvPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title.uppercase(),
                    style = JasnifyTheme.typography.labelSmall,
                    color = accentColor,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        TicketDivider(
            modifier = Modifier.onGloballyPositioned { coordinates ->
                val center = coordinates.positionInParent().y + (coordinates.size.height / 2f)
                onDividerPositioned(center)
            }
        )

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomTextButton(
                onClick = onViewDetailsClick,
                text = "View details",
                size = ButtonSize.Small,
                type = ButtonType.Secondary,
                containerColor = SurfaceBrandSecondary,
                contentColor = accentColor
            )

            if (type == OfferCardType.FULL && code != null) {
                CustomTextButton(
                    onClick = { onCopyCodeClick(code) },
                    text = code.uppercase(),
                    size = ButtonSize.Small,
                    type = ButtonType.Secondary,
                    leadingIcon = painterResource(id = R.drawable.ic_copy),
                    containerColor = SurfaceBrandSecondary,
                    contentColor = accentColor
                )
            }
        }
    }
}

@Composable
private fun OfferCardCompactContent(
    title: String,
    description: String,
    progress: String?,
    accentColor: Color,
    iconBgColor: Color,
    onDividerPositioned: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(SquircleShape(CornerLarge))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalOffer,
                    contentDescription = null,
                    tint = ContentInvPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title.uppercase(),
                    style = JasnifyTheme.typography.labelSmall,
                    color = accentColor
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        VerticalTicketDivider(
            modifier = Modifier.onGloballyPositioned { coordinates ->
                val center = coordinates.positionInParent().x + (coordinates.size.width / 2f)
                onDividerPositioned(center)
            }
        )

        Box(
            modifier = Modifier.padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = progress ?: "",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary,
            )
        }
    }
}

@Composable
fun TicketDivider(
    modifier: Modifier = Modifier,
    color: Color = ContentSecondary.copy(0.16f),
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
fun VerticalTicketDivider(
    modifier: Modifier = Modifier,
    color: Color = ContentSecondary.copy(0.16f),
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxHeight().width(1.dp)) {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOfferCards() {
    JasnifyTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OfferCard(
                title = "Offer Title",
                description = "Offer Description",
                code = "JASNIFY12",
                elevation = 6.dp
            )

            OfferCard(
                title = "Offer Title",
                description = "Offer Description",
                elevation = 4.dp
            )

            OfferCard(
                title = "Offer Title",
                description = "Offer Description",
                type = OfferCardType.COMPACT,
                progress = "1/5",
                elevation = 8.dp,
                shadowColor = Color.Black.copy(alpha = 0.16f)
            )
        }
    }
}