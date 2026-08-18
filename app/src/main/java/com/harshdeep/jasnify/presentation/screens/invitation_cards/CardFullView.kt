package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
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
    onEditDetailsClick: () -> Unit
) {
    SetStatusBarTheme(useDarkIcons = false)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    var cardToCapture by remember { mutableStateOf<CardData?>(null) }

    val onShareTrigger: (CardData) -> Unit = remember(context, graphicsLayer) {
        { data: CardData ->
            coroutineScope.launch {
                cardToCapture = data
                delay(100.milliseconds)
                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                ShareUtils.shareImage(context, bitmap, whatsappOnly = false)
                cardToCapture = null
            }
        }
    }

    val sharePainter = painterResource(R.drawable.ic_share)
    val editPainter = painterResource(R.drawable.ic_edit)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ContentPrimary)
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
                onMenuClick = { onShareTrigger(card) },
                backIcon = TopIcon.Predefined.BACK_2,
                menuIcon = TopIcon.CustomPainter(sharePainter),
                textColor = ContentInvPrimary,
                buttonStyle = ButtonBackground.TRANSLUCENT
            )
        }

        // Card Container with sharedBounds
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 80.dp,
                    bottom = if (canEdit) 120.dp else 32.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            contentAlignment = Alignment.Center
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
        }

        // Bottom Action Button
        if (canEdit) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
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
                        .navigationBarsPadding(),
                )
            }
        }
    }
}