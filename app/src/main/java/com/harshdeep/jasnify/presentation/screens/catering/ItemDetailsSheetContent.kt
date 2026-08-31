package com.harshdeep.jasnify.presentation.screens.catering

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.InfoTooltip
import com.harshdeep.jasnify.presentation.components.states.shimmerBrush
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

private val CardTranslucentGradientBrush = androidx.compose.ui.graphics.Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.25f)
    )
)

@Composable
fun ItemDetailsSheetContent(
    item: MenuItem,
    canEdit: Boolean = true,
    onFetchImages: suspend (String) -> List<String>,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onRecentActivityClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val itemCategoryStyle = remember(item.type) { getCategoryStyle(item.type) }

    var imageUrls by remember(item.id, item.name, item.cuisine) { mutableStateOf<List<String>>(emptyList()) }
    var isImagesLoading by remember(item.id, item.name, item.cuisine) { mutableStateOf(true) }

    // State to manage tooltip visibility
    var showTooltip by remember { mutableStateOf(false) }

    LaunchedEffect(item.id, item.name, item.cuisine) {
        isImagesLoading = true
        val query = "${item.name} ${item.cuisine}"
        val results = onFetchImages(query)
        imageUrls = results
        isImagesLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = CardSquircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = CardTranslucentGradientBrush, shape = CardSquircleShape)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header: Search Preview label and Info Icon with Tooltip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_inspirations),
                                contentDescription = "Search",
                                tint = ContentSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Search Preview",
                                style = JasnifyTheme.typography.labelMedium,
                                color = ContentSecondary,
                            )
                        }

                        // Info Icon Anchor + Popup Tooltip
                        Box {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_info),
                                contentDescription = "Info",
                                tint = ContentSecondary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .noRippleClickable {
                                        showTooltip = true
                                    }
                            )
                            InfoTooltip(
                                visible = showTooltip,
                                tooltipText = "We show relevant results based on your saved info.",
                                onDismiss = { showTooltip = false }
                            )
                        }
                    }

                    // Image Carousel / Web link
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        if (isImagesLoading) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                items(3) {
                                    Box(
                                        modifier = Modifier
                                            .size(160.dp)
                                            .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                                            .background(shimmerBrush())
                                    )
                                }
                            }
                        } else if (imageUrls.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(136.dp)
                                    .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                                    .background(SurfaceSecondary)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.16f), SquircleShape(CornerLarge, CornerSmoothingDefault))
                                    .clickable {
                                        val googleSearchQuery = "${item.name} ${item.type} ${item.cuisine}"
                                        val queryUri =
                                            "https://www.google.com/search?q=${Uri.encode(googleSearchQuery)}&tbm=isch".toUri()
                                        context.startActivity(Intent(Intent.ACTION_VIEW, queryUri))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_google),
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Tap to search images on Google",
                                        style = JasnifyTheme.typography.labelLarge,
                                        color = ContentBrandDark
                                    )
                                }
                            }
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(imageUrls) { url ->
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(url)
                                            .addHeader("User-Agent", "JasnifyEventApp/1.0 (contact@jasnify.app)")
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = item.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(160.dp)
                                            .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                                            .background(SurfaceSecondary)
                                    )
                                }

                                item {
                                    Box(
                                        modifier = Modifier
                                            .size(160.dp)
                                            .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                                            .background(SurfaceBrandSecondary)
                                            .border(1.dp, ContentBrandDark, SquircleShape(CornerLarge, CornerSmoothingDefault))
                                            .clickable {
                                                val googleSearchQuery = "${item.name} ${item.type} ${item.cuisine}"
                                                val queryUri =
                                                    "https://www.google.com/search?q=${Uri.encode(googleSearchQuery)}&tbm=isch".toUri()
                                                context.startActivity(Intent(Intent.ACTION_VIEW, queryUri))
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_google),
                                                contentDescription = "Google",
                                                tint = Color.Unspecified,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Text(
                                                text = "google.com ↗",
                                                color = ContentBrandDark,
                                                style = JasnifyTheme.typography.labelLarge.copy(
                                                    textDecoration = TextDecoration.Underline
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ){
                        val isVeg = item.dietary == Dietary.Veg
                        val drawableRes = if (isVeg) R.drawable.ic_veg else R.drawable.ic_non_veg

                        Image(
                            painter = painterResource(id = drawableRes),
                            contentDescription = if (isVeg) "Vegetarian" else "Non-Vegetarian",
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = item.name,
                            style = JasnifyTheme.typography.headingLarge,
                            color = itemCategoryStyle.headerTextColor,
                            fontWeight = FontWeight.Medium
                        )

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
                        )

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CUISINE",
                                    style = JasnifyTheme.typography.labelSmall,
                                    color = ContentSecondary,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.cuisine,
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentPrimary
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TYPE",
                                    style = JasnifyTheme.typography.labelSmall,
                                    color = ContentSecondary,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.type,
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentPrimary
                                )
                            }
                        }

                        DashedDivider()
                        Spacer(Modifier.height(4.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRecentActivityClick() },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = "Recent Activity",
                                    tint = ContentTertiary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Recent Activity",
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentSecondary
                                )
                            }

                            Icon(
                                painter = painterResource(id = R.drawable.ic_right_chevron),
                                contentDescription = "Go",
                                tint = ContentPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        if (canEdit) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomIconButton(
                    onClick = {
                        focusManager.clearFocus()
                        onDeleteClick()
                    },
                    icon = painterResource(R.drawable.ic_delete),
                    containerColor = SurfacePrimary,
                    contentColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.width(84.dp)
                )

                CustomTextButton(
                    onClick = {
                        focusManager.clearFocus()
                        onEditClick()
                    },
                    text = "Edit Details",
                    leadingIcon = painterResource(R.drawable.ic_edit),
                    containerColor = ContentPrimary,
                    contentColor = ContentInvPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
