package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.utils.ShareUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardsMainContent(
    selectedTab: CardsTab,
    onTabSelected: (CardsTab) -> Unit,
    myCards: List<CardData>,
    likedCards: List<CardData>,
    activeEvent: com.harshdeep.jasnify.domain.model.Event?,
    selectedCardIds: Set<String>,
    exploreLazyListState: LazyListState,
    myCardsGridState: LazyGridState,
    explorePagerState: PagerState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    canEdit: Boolean,
    nestedScrollConnection: NestedScrollConnection,
    onToggleCardSelection: (String) -> Unit,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onCardClick: (CardData, String) -> Unit,
    onLikeToggle: (CardData) -> Unit,
    onEditDetailsClick: (CardData) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    val templateBackgrounds = listOf(
        R.drawable.bg_invitation_card_01,
        R.drawable.bg_invitation_card_02,
        R.drawable.bg_invitation_card_03,
        R.drawable.bg_invitation_card_04,
        R.drawable.bg_invitation_card_05
    )

    val templates = remember(activeEvent, templateBackgrounds) {
        templateBackgrounds.mapIndexed { index, resId ->
            val baseCard = CardData(id = "template_$index", backgroundRes = resId, bgName = "")
            activeEvent?.let { event ->
                val date = event.date?.let { Date(it) } ?: Date()
                val day = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
                val dayOfMonth = SimpleDateFormat("dd", Locale.getDefault()).format(date)
                val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase()
                val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
                val formattedDateString = "$day • $dayOfMonth $month • $year"

                val updatedElements = baseCard.elements.mapIndexed { eIndex, element ->
                    when (eIndex) {
                        1 -> element.copy(text = event.name.ifBlank { element.text })
                        3 -> element.copy(text = formattedDateString)
                        else -> element
                    }
                }
                baseCard.copy(elements = updatedElements)
            } ?: baseCard
        }
    }

    var cardToCapture by remember { mutableStateOf<CardData?>(null) }

    val onShareTrigger = { data: CardData, whatsappOnly: Boolean ->
        coroutineScope.launch {
            cardToCapture = data
            delay(100.milliseconds)
            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
            ShareUtils.shareImage(context, bitmap, whatsappOnly = whatsappOnly)
            cardToCapture = null
        }
        Unit
    }

    val isSelectionMode = selectedCardIds.isNotEmpty()

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
                            isSelectionMode -> TopIcon.CustomPainter(painterResource(R.drawable.ic_delete))
                            selectedTab == CardsTab.EXPLORE -> TopIcon.Predefined.MENU_VERTICAL
                            else -> TopIcon.CustomPainter(painterResource(R.drawable.ic_top_bar_heart))
                        }
                    )
                }
            },
            containerColor = BackgroundPrimary,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Horizontal Slide Animation between Tabs
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
                                templates = templates,
                                likedCards = likedCards,
                                lazyListState = exploreLazyListState,
                                pagerState = explorePagerState,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sharedTransitionScope = sharedTransitionScope,
                                canEdit = canEdit,
                                nestedScrollConnection = nestedScrollConnection,
                                onCardClick = onCardClick,
                                onLikeToggle = onLikeToggle,
                                onShareTrigger = { card -> onShareTrigger(card, false) },
                                onEditDetailsClick = onEditDetailsClick,
                                onWhatsappShare = { card -> onShareTrigger(card, true) }
                            )
                        }
                        CardsTab.MY_CARDS -> {
                            MyCardsGrid(
                                cards = myCards,
                                templates = templates,
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
    }
}
