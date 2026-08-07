package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

enum class GuestCardType {
    SELECTABLE,
    INVITE_ACTION,
    VIEW_DETAILS
}

@Composable
fun GuestCard(
    name: String,
    label: String,
    modifier: Modifier = Modifier,
    type: GuestCardType = GuestCardType.SELECTABLE,
    imageUrl: String? = null,
    isSelected: Boolean = false,
    isInvited: Boolean = false,
    isExpanded: Boolean = false,
    lastUpdatedBy: String? = null,
    lastUpdatedAt: String? = null,
    labelColor: Color = Color(0xFF635994),
    onCardClick: () -> Unit = {},
    onSelectToggle: (Boolean) -> Unit = {},
    onInviteClick: () -> Unit = {},
    onViewDetailsClick: () -> Unit = {}
) {
    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth(),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
        colors = CardDefaults.cardColors(containerColor = SurfaceSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (type == GuestCardType.SELECTABLE) {
                    CustomChecker(
                        checked = isSelected,
                        onCheckedChange = onSelectToggle,
                        activeColor = Color(0xFF005858)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.ic_user_profile),
                            error = painterResource(id = R.drawable.ic_user_profile)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_user_profile),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and Label
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = name,
                        style = JasnifyTheme.typography.labelXLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = ContentPrimary
                    )
                    Text(
                        text = label,
                        style = JasnifyTheme.typography.labelLarge,
                        color = labelColor
                    )
                }

                if (type == GuestCardType.INVITE_ACTION) {
                    if (!isInvited) {
                        CustomTextButton(
                            onClick = onInviteClick,
                            text = "Mark as invited",
                            size = ButtonSize.Small,
                            type = ButtonType.Primary,
                            shapeStyle = ButtonShapeStyle.Round,
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    } else {
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onInviteClick() },
                            shape = CircleShape,
                            color = BackgroundPrimary,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color.LightGray.copy(alpha = 0.5f)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Invited",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            if (type == GuestCardType.VIEW_DETAILS) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            onClick = onViewDetailsClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = SquircleShape(16.dp, CornerSmoothingDefault),
                            color = BackgroundPrimary
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "View Details",
                                    style = JasnifyTheme.typography.labelXLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = ContentPrimary
                                )
                            }
                        }

                        if (lastUpdatedBy != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Last updated by $lastUpdatedBy",
                                value = lastUpdatedAt ?: ""
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = JasnifyTheme.typography.labelLarge,
            color = ContentSecondary.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = JasnifyTheme.typography.labelLarge,
            color = ContentSecondary.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGuestCards() {
    var expanded by remember { mutableStateOf(false) }

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
                type = GuestCardType.VIEW_DETAILS,
                isExpanded = expanded,
                lastUpdatedBy = "Anand K.",
                lastUpdatedAt = "Aug 24, 2025, 01:04pm",
                onCardClick = { expanded = !expanded },
                onViewDetailsClick = { /* Open Bottom Sheet */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
