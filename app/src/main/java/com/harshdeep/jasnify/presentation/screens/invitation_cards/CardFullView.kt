package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.utils.ShareUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

private val FullCardShape = SquircleShape(CornerMedium, CornerSmoothingDefault)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardFullView(
    card: CardData,
    transitionKey: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    canEdit: Boolean,
    onBackClick: () -> Unit,
    onEditDetailsClick: () -> Unit,
    onLikeToggle: (CardData) -> Unit = {},
    onShareIncrement: (CardData) -> Unit = {}
) {
    SetStatusBarTheme(useDarkIcons = false)

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

    val editPainter = painterResource(R.drawable.ic_edit)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
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

        // Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            CustomTopBar(
                onBackClick = onBackClick,
                backIcon = TopIcon.Predefined.BACK_2,
                textColor = ContentInvPrimary,
                buttonStyle = ButtonBackground.OPAQUE,
                buttonColor = Color(0xE53D3D3D)
            )
        }

        // Card Container with sharedBounds
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 100.dp,
                    bottom = if (canEdit) 140.dp else 40.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            with(sharedTransitionScope) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(280f / 373f)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = transitionKey),
                            animatedVisibilityScope = animatedVisibilityScope,
                            clipInOverlayDuringTransition = OverlayClip(FullCardShape)
                        )
                        .clip(FullCardShape)
                ) {
                    CardItem(
                        data = card,
                        forCapture = false,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Like and Share Pill
            Surface(
                color = Color(0xFF262626),
                shape = CircleShape,
                modifier = Modifier.wrapContentSize()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid }
                    val isLiked = remember(card.likedBy, currentUserId) { 
                        card.likedBy.contains(currentUserId) 
                    }

                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .noRippleClickable(
                                onClick = { onLikeToggle(card) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = if (isLiked) R.drawable.ic_no_border_heart_filled else R.drawable.ic_top_bar_heart),
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Unspecified else ContentInvPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${card.likesCount}",
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentInvPrimary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .noRippleClickable(
                                onClick = { onShareTrigger(card) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = "Share",
                            tint = ContentInvPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${card.sharesCount}",
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentInvPrimary
                        )
                    }
                }
            }
        }

        // Bottom Action Button
        if (canEdit) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .navigationBarsPadding()
            ) {
                CustomTextButton(
                    text = "Edit Details",
                    onClick = onEditDetailsClick,
                    containerColor = ContentInvPrimary,
                    contentColor = ContentPrimary,
                    leadingIcon = editPainter,
                    shapeStyle = ButtonShapeStyle.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    }
}
