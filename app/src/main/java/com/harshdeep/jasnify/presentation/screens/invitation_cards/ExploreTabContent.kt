package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.components.carousels.CardCarousel
import com.harshdeep.jasnify.presentation.components.explore.ExploreTrendingCards
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExploreTabContent(
    templates: List<CardData>,
    likedCards: List<CardData>,
    lazyListState: LazyListState,
    pagerState: PagerState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    canEdit: Boolean,
    nestedScrollConnection: NestedScrollConnection,
    onCardClick: (CardData, String) -> Unit,
    onLikeToggle: (CardData) -> Unit,
    onShareTrigger: (CardData) -> Unit,
    onEditDetailsClick: (CardData) -> Unit,
    onWhatsappShare: (CardData) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val trendingOffset by remember {
            derivedStateOf {
                val layoutInfo = lazyListState.layoutInfo
                val trendingItem = layoutInfo.visibleItemsInfo.find { it.index == 1 }
                trendingItem?.offset
            }
        }

        // Ambient radial glow behind trending header
        trendingOffset?.let { offsetPx ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .offset(y = with(density) { offsetPx.toDp() } - 70.dp)
                    .drawWithCache {
                        val brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0.00f to Color(0xFFFFC09A).copy(alpha = 0.25f),
                                0.30f to Color(0xFFFFC09A).copy(alpha = 0.40f),
                                0.55f to Color(0xFFFFC09A).copy(alpha = 0.18f),
                                0.75f to BackgroundPrimary.copy(alpha = 0.08f),
                                1.00f to BackgroundPrimary.copy(alpha = 0.05f),
                            ),
                            radius = 360.dp.toPx()
                        )
                        onDrawBehind {
                            scale(
                                scaleX = 1.5f,
                                scaleY = 0.7f,
                                pivot = center
                            ) {
                                drawCircle(
                                    brush = brush,
                                    radius = 360.dp.toPx(),
                                    center = center
                                )
                            }
                        }
                    }
            )
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = "carousel_section") {
                Column(
                    modifier = Modifier.padding(vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val currentCarouselCard = templates[pagerState.currentPage % templates.size]
                    val carouselKey = "carousel_${currentCarouselCard.id}"

                    with(sharedTransitionScope) {
                        Box(
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(key = carouselKey),
                                animatedVisibilityScope = animatedVisibilityScope,
                                zIndexInOverlay = 1f,
                                clipInOverlayDuringTransition = OverlayClip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                            )
                        ) {
                            CardCarousel(
                                cardData = currentCarouselCard,
                                pagerState = pagerState,
                                isLiked = { resId -> likedCards.any { it.backgroundRes == resId } },
                                onLikeClick = onLikeToggle,
                                onCardClick = { card -> onCardClick(card, carouselKey) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val currentTemplate = templates[pagerState.currentPage % templates.size]

                        if (canEdit) {
                            CustomIconButton(
                                icon = painterResource(id = R.drawable.ic_edit),
                                onClick = { onEditDetailsClick(currentTemplate) },
                                type = ButtonType.Secondary
                            )
                        }

                        CustomTextButton(
                            text = "Share Card",
                            onClick = { onShareTrigger(currentTemplate) },
                            containerColor = ContentPrimary,
                            contentColor = ContentInvPrimary,
                            trailingIcon = painterResource(id = R.drawable.ic_share),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        CustomIconButton(
                            icon = painterResource(id = R.drawable.ic_whatsapp),
                            onClick = { onWhatsappShare(currentTemplate) },
                            containerColor = Color(0xFF1BA911),
                            contentColor = ContentInvPrimary
                        )
                    }
                }
            }

            item(key = "trending_header") {
                ExploreTrendingCards()
            }

            // Enclosed Template Grid Container
            item(key = "template_grid") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x334D2A15),
                                        Color.Transparent
                                    ),
                                    startY = 0.dp.toPx(),
                                    endY = 0f
                                ),
                                topLeft = Offset(0f, -4.dp.toPx()),
                                size = Size(
                                    width = size.width,
                                    height = 20.dp.toPx()
                                )
                            )
                        }
                        .clip(
                            SquircleShape(
                                CornerLargeIncrease,
                                CornerLargeIncrease,
                                0.dp,
                                0.dp,
                                CornerSmoothingDefault
                            )
                        )
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to SurfacePrimary.copy(alpha = 0.50f),
                                    0.40f to SurfacePrimary.copy(alpha = 0.70f),
                                    0.80f to SurfacePrimary.copy(alpha = 0.90f),
                                    1.0f to SurfacePrimary
                                )
                            )
                        )
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val chunkedTemplates = templates.chunked(2)
                        chunkedTemplates.forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { template ->
                                    val isLiked = likedCards.any { it.backgroundRes == template.backgroundRes }
                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isPressed by interactionSource.collectIsPressedAsState()
                                    val scale by animateFloatAsState(
                                        targetValue = if (isPressed) 0.96f else 1f,
                                        label = "scale"
                                    )
                                    val trendingKey = "trending_${template.id}"

                                    with(sharedTransitionScope) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(280f / 373f)
                                                .sharedBounds(
                                                    sharedContentState = rememberSharedContentState(key = trendingKey),
                                                    animatedVisibilityScope = animatedVisibilityScope,
                                                    zIndexInOverlay = 1f,
                                                    clipInOverlayDuringTransition = OverlayClip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                                                )
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .graphicsLayer {
                                                        scaleX = scale
                                                        scaleY = scale
                                                    }
                                                    .clip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                                                    .clickable(
                                                        interactionSource = interactionSource,
                                                        indication = null
                                                    ) { onCardClick(template, trendingKey) }
                                            ) {
                                                CardItem(
                                                    data = template,
                                                    showControls = true,
                                                    isLiked = isLiked,
                                                    onLikeClick = { onLikeToggle(template) },
                                                    onShareClick = null,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            item(key = "footer") {
                Spacer(modifier = Modifier.height(16.dp))
                FooterJansify()
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, backgroundColor = 0xFFFAF7F2, widthDp = 390, heightDp = 844)
@Composable
fun ExploreTabContentPreview() {
    JasnifyTheme {
        val sampleTemplates = remember {
            listOf(
                CardData(
                    id = "template_0",
                    backgroundRes = R.drawable.bg_invitation_card_01,
                    bgName = "Classic Elegance"
                ),
                CardData(
                    id = "template_1",
                    backgroundRes = R.drawable.bg_invitation_card_02,
                    bgName = "Floral Romance"
                ),
                CardData(
                    id = "template_2",
                    backgroundRes = R.drawable.bg_invitation_card_03,
                    bgName = "Golden Glamour"
                ),
                CardData(
                    id = "template_3",
                    backgroundRes = R.drawable.bg_invitation_card_04,
                    bgName = "Modern Minimalist"
                )
            )
        }

        val exploreLazyListState = rememberLazyListState()
        val explorePagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { sampleTemplates.size }
        )

        val dummyNestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
            }
        }

        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                ExploreTabContent(
                    templates = sampleTemplates,
                    likedCards = emptyList(),
                    lazyListState = exploreLazyListState,
                    pagerState = explorePagerState,
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    canEdit = true,
                    nestedScrollConnection = dummyNestedScrollConnection,
                    onCardClick = { _, _ -> },
                    onLikeToggle = {},
                    onShareTrigger = {},
                    onEditDetailsClick = {},
                    onWhatsappShare = {}
                )
            }
        }
    }
}
