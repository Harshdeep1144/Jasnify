package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.utils.ShareUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

private val LikedCardShape = SquircleShape(CornerMedium, CornerSmoothingDefault)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LikedCardsContent(
    cards: List<CardData>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onBackClick: () -> Unit,
    onCardClick: (CardData, String) -> Unit,
    onLikeToggle: (CardData) -> Unit,
    onShareIncrement: (CardData) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    var cardToCapture by remember { mutableStateOf<CardData?>(null) }

    val onShareTrigger: (CardData) -> Unit = remember(context, graphicsLayer, onShareIncrement) {
        { data: CardData ->
            onShareIncrement(data)
            coroutineScope.launch {
                cardToCapture = data
                delay(100.milliseconds)
                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                ShareUtils.shareImage(context, bitmap, whatsappOnly = false)
                cardToCapture = null
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Hidden Capture Area
        Box(
            modifier = Modifier
                .size(280.dp, 373.dp)
                .offset(x = (-2000).dp)
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                }
        ) {
            cardToCapture?.let {
                CardItem(
                    data = it,
                    forCapture = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Scaffold(
            topBar = {
                Column(modifier = Modifier.statusBarsPadding()) {
                    CustomTopBar(
                        title = "Saved",
                        isLargeTitle = true,
                        onBackClick = onBackClick
                    )
                }
            },
            containerColor = BackgroundPrimary
        ) { innerPadding ->
            if (cards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Saved cards yet.",
                        style = JasnifyTheme.typography.bodyLarge,
                        color = ContentSecondary
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(
                        items = cards,
                        key = { it.id },
                        contentType = { "liked_card_item" }
                    ) { card ->
                        Column {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale by animateFloatAsState(
                                targetValue = if (isPressed) 0.96f else 1f,
                                label = "scale"
                            )
                            val likedKey = "liked_card_${card.id}"

                            with(sharedTransitionScope) {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(280f / 373f)
                                        .sharedBounds(
                                            sharedContentState = rememberSharedContentState(key = likedKey),
                                            animatedVisibilityScope = animatedVisibilityScope,
                                            clipInOverlayDuringTransition = OverlayClip(LikedCardShape)
                                        )
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                        }
                                        .clip(LikedCardShape)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) { onCardClick(card, likedKey) }
                                ) {
                                    CardItem(
                                        data = card,
                                        showControls = false,
                                        isLiked = true,
                                        onLikeClick = { onLikeToggle(card) },
                                        onShareClick = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid }
                            
                            var localIsLiked by remember(card.id, card.likedBy, currentUserId) {
                                mutableStateOf(true) 
                            }
                            var localLikesCount by remember(card.id, card.likesCount) {
                                mutableIntStateOf(card.likesCount)
                            }
                            var localSharesCount by remember(card.id, card.sharesCount) {
                                mutableIntStateOf(card.sharesCount)
                            }

                            CardInteractionRow(
                                likesCount = localLikesCount,
                                sharesCount = localSharesCount,
                                isLiked = localIsLiked,
                                onLikeClick = { 
                                    localIsLiked = !localIsLiked
                                    if (localIsLiked) localLikesCount++ else localLikesCount--
                                    onLikeToggle(card) 
                                },
                                onShareClick = { 
                                    localSharesCount++
                                    onShareTrigger(card) 
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardInteractionRow(
    likesCount: Int,
    sharesCount: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onLikeClick
            )
        ) {
            Icon(
                painter = painterResource(id = if (isLiked) R.drawable.ic_no_border_heart_filled else R.drawable.ic_top_bar_heart),
                contentDescription = "Like",
                tint = if (isLiked) Color.Unspecified else ContentPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$likesCount",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentPrimary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onShareClick
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "Share",
                tint = ContentPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$sharesCount",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentPrimary
            )
        }
    }
}
