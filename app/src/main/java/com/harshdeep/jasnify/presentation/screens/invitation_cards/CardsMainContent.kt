package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.utils.ShareUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private val ZeroInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardsMainContent(
    selectedTab: CardsTab,
    onTabSelected: (CardsTab) -> Unit,
    myCards: List<CardData>,
    likedCards: List<CardData>,
    jasnifyCards: List<CardData>,
    activeEvent: Event?,
    selectedCardIds: Set<String>,
    exploreLazyListState: LazyListState,
    myCardsGridState: LazyGridState,
    explorePagerState: PagerState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    canEdit: Boolean,
    isCardAdmin: Boolean,
    nestedScrollConnection: NestedScrollConnection,
    onToggleCardSelection: (String) -> Unit,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onCardClick: (CardData, String) -> Unit,
    onLikeToggle: (CardData) -> Unit,
    onShareIncrement: (CardData) -> Unit,
    onEditDetailsClick: (CardData) -> Unit,
    onAddNewClick: () -> Unit,
    onPublishSelectedToJasnify: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    var cardToCapture by remember { mutableStateOf<CardData?>(null) }

    val onShareTrigger: (CardData, Boolean) -> Unit = remember(context, graphicsLayer) {
        { data: CardData, whatsappOnly: Boolean ->
            coroutineScope.launch {
                cardToCapture = data
                delay(100.milliseconds)
                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                ShareUtils.shareImage(context, bitmap, whatsappOnly = whatsappOnly)
                cardToCapture = null
            }
        }
    }

    val isSelectionMode = selectedCardIds.isNotEmpty()

    val deletePainter = painterResource(R.drawable.ic_delete)
    val heartPainter = painterResource(R.drawable.ic_top_bar_heart)

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
                Column(
                    modifier = Modifier.statusBarsPadding()
                ) {
                    CustomTopBar(
                        title = if (isSelectionMode) "${selectedCardIds.size} Selected" else "Cards",
                        isLargeTitle = !isSelectionMode,
                        onBackClick = onBackClick,
                        onMenuClick = onMenuClick,
                        menuIcon = when {
                            isSelectionMode -> TopIcon.CustomPainter(deletePainter)
                            selectedTab == CardsTab.EXPLORE -> TopIcon.Predefined.MENU_VERTICAL
                            else -> TopIcon.CustomPainter(heartPainter)
                        }
                    )
                }
            },
            containerColor = BackgroundPrimary,
            contentWindowInsets = ZeroInsets
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        if (targetState == CardsTab.MY_CARDS) {
                            (slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300))) togetherWith
                                    (slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> -fullWidth },
                                        animationSpec = tween(300)
                                    ) + fadeOut(animationSpec = tween(300)))
                        } else {
                            (slideInHorizontally(
                                initialOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300))) togetherWith
                                    (slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> fullWidth },
                                        animationSpec = tween(300)
                                    ) + fadeOut(animationSpec = tween(300)))
                        }
                    },
                    label = "CardsTabContentHorizontalSlide"
                ) { tab ->
                    when (tab) {
                        CardsTab.EXPLORE -> {
                            ExploreTabContent(
                                likedCards = likedCards,
                                jasnifyCards = jasnifyCards,
                                lazyListState = exploreLazyListState,
                                pagerState = explorePagerState,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sharedTransitionScope = sharedTransitionScope,
                                canEdit = canEdit,
                                nestedScrollConnection = nestedScrollConnection,
                                onCardClick = onCardClick,
                                onLikeToggle = onLikeToggle,
                                onShareTrigger = { card ->
                                    onShareIncrement(card)
                                    onShareTrigger(card, false)
                                },
                                onEditDetailsClick = onEditDetailsClick,
                                onWhatsappShare = { card ->
                                    onShareIncrement(card)
                                    onShareTrigger(card, true)
                                }
                            )
                        }
                        CardsTab.MY_CARDS -> {
                            MyCardsGrid(
                                cards = myCards,
                                gridState = myCardsGridState,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sharedTransitionScope = sharedTransitionScope,
                                canEdit = canEdit,
                                nestedScrollConnection = nestedScrollConnection,
                                onStartEditing = { onTabSelected(CardsTab.EXPLORE) },
                                selectedCardIds = selectedCardIds,
                                onToggleSelection = onToggleCardSelection,
                                onCardClick = onCardClick,
                                onShareClick = { card -> onShareTrigger(card, false) }
                            )
                        }
                    }
                }
            }
        }


        // ============================================== Card Admin Options =================================================


        // Admin Multi-Select Bottom Bar in My Edits Tab
        AnimatedVisibility(
            visible = isSelectionMode && isCardAdmin && selectedTab == CardsTab.MY_CARDS,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
                .zIndex(20f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = BottomGradientBrush)
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .pill360Shadow(
                            ambientColor = Color.Black.copy(alpha = 0.10f),
                            ambientBlur = 12.dp,
                            ambientSpread = 2.dp,
                            spotColor = Color.Black.copy(alpha = 0.15f),
                            spotBlur = 18.dp,
                            spotOffsetY = 4.dp
                        ),
                    color = SurfacePrimary,
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextButton(
                            onClick = onPublishSelectedToJasnify,
                            text = "Publish to Jasnify",
                            type = ButtonType.Primary,
                            shapeStyle = ButtonShapeStyle.Round,
                            leadingIcon = painterResource(R.drawable.ic_plus),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // FAB for Admin to Add New Jasnify Card
        AnimatedVisibility(
            visible = (selectedTab == CardsTab.MY_CARDS) && isCardAdmin && !isSelectionMode,
            enter = androidx.compose.animation.scaleIn() + fadeIn(),
            exit = androidx.compose.animation.scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 132.dp, end = 24.dp)
                .zIndex(15f)
        ) {
            CustomIconButton(
                icon = painterResource(R.drawable.ic_plus),
                onClick = onAddNewClick,
                containerColor = com.harshdeep.jasnify.theme.ContentBrand,
                contentColor = com.harshdeep.jasnify.theme.SurfacePrimary,
                size = ButtonSize.Large
            )
        }
    }
}