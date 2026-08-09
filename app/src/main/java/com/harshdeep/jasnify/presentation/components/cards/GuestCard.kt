package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

enum class GuestCardType {
    DEFAULT,
    SELECTABLE,
    INVITE_ACTION
}

@Composable
fun GuestCard(
    name: String,
    label: String,
    modifier: Modifier = Modifier,
    type: GuestCardType = GuestCardType.DEFAULT,
    imageUrl: String? = null,
    isSelected: Boolean = false,
    isInvited: Boolean = false,
    showActions: Boolean = false,
    lastUpdatedBy: String? = null,
    lastUpdatedAt: String? = null,
    labelColor: Color = Color(0xFF635994),
    cardShape: SquircleShape = SquircleShape(CornerLarge, CornerSmoothingDefault),
    onCardClick: () -> Unit = {},
    onSelectToggle: (Boolean) -> Unit = {},
    onInviteClick: () -> Unit = {},
    onViewDetailsClick: () -> Unit = {}
) {
    val animationDuration = 150

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCardClick
            )
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = EaseInOut
                )
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = SurfaceSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (type == GuestCardType.SELECTABLE) {
                    CustomChecker(
                        checked = isSelected,
                        onCheckedChange = onSelectToggle,
                        activeColor = ContentPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Avatar Container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ContentSecondary)
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                            error = painterResource(id = R.drawable.ic_profile_placeholder)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_placeholder),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                // Name and Label
                Column(
                    modifier = Modifier.weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = name,
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentPrimary,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee()
                    )
                    Text(
                        text = label,
                        style = JasnifyTheme.typography.labelLarge,
                        color = labelColor
                    )
                }

                if (type == GuestCardType.INVITE_ACTION && !showActions) {
                    if (!isInvited) {
                        CustomTextButton(
                            onClick = onInviteClick,
                            text = "Mark Invited",
                            size = ButtonSize.Small,
                            type = ButtonType.Primary,
                            shapeStyle = ButtonShapeStyle.Round,
                            containerColor = ContentPrimary,
                            contentColor = ContentInvPrimary
                        )
                    } else {
                        Surface(
                            modifier = Modifier
                                .width(56.dp)
                                .height(40.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onInviteClick
                                ),
                            shape = RoundedCornerShape(100),
                            color = BackgroundPrimary,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_check),
                                    contentDescription = "Invited",
                                    modifier = Modifier.size(20.dp),
                                    tint = ContentPrimary
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showActions,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = EaseInOut
                    )
                ) + expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = EaseInOut
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = EaseInOut
                    )
                ) + shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = EaseInOut
                    )
                )
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextButton(
                        onClick = onViewDetailsClick,
                        text = "View Details",
                        modifier = Modifier.fillMaxWidth(),
                        size = ButtonSize.Small,
                        shapeStyle = ButtonShapeStyle.Square,
                        containerColor = ContentInvPrimary,
                        contentColor = ContentPrimary
                    )

                    if (lastUpdatedBy != null || lastUpdatedAt != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (lastUpdatedBy != null) {
                                    Text(
                                        text = "Last updated by ",
                                        style = JasnifyTheme.typography.bodyMedium,
                                        color = ContentSecondary,
                                        fontWeight = FontWeight.Light
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = lastUpdatedBy,
                                        style = JasnifyTheme.typography.bodyMedium,
                                        color = ContentSecondary
                                    )
                                } else {
                                    Text(
                                        text = "Last updated",
                                        style = JasnifyTheme.typography.bodyMedium,
                                        color = ContentSecondary,
                                        fontWeight = FontWeight.Light
                                    )
                                }
                            }
                            if (lastUpdatedAt != null) {
                                Text(
                                    text = lastUpdatedAt,
                                    style = JasnifyTheme.typography.bodyMedium,
                                    color = ContentSecondary,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGuestCards() {
    var showActions by remember { mutableStateOf(false) }

    JasnifyTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(BackgroundPrimary),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GuestCard(
                name = "Akriti R.",
                label = "Close Friend",
                type = GuestCardType.SELECTABLE,
                isSelected = true,
                modifier = Modifier.fillMaxWidth()
            )

            GuestCard(
                name = "Akriti R.",
                label = "Close Friend",
                type = GuestCardType.INVITE_ACTION,
                isInvited = false,
                modifier = Modifier.fillMaxWidth()
            )

            GuestCard(
                name = "Akriti R.",
                label = "Close Friend",
                type = GuestCardType.DEFAULT,
                showActions = showActions,
                lastUpdatedBy = "Anand K.",
                lastUpdatedAt = "Aug 24, 2025, 01:04pm",
                onCardClick = { showActions = !showActions },
                onViewDetailsClick = { /* Open Bottom Sheet */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}