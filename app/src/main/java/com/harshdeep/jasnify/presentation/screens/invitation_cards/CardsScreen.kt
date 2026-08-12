package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.InvitationCardData
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.InvitationCardItem
import com.harshdeep.jasnify.presentation.components.carousels.InvitationCardCarousel
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTabStyle
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.utils.ShareUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

enum class InvitationCardsView {
    MAIN,
    EDIT_DETAILS
}

enum class InvitationCardsTab {
    EXPLORE,
    SAVED
}

@Composable
fun CardsScreen(
    onBackClick: () -> Unit = {},
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    var currentView by remember { mutableStateOf(InvitationCardsView.MAIN) }

    // Unified state for invitation card data using domain model
    var cardData by remember { mutableStateOf(InvitationCardData()) }

    // Sync state with active event once loaded by mapping values into the card's TextElement list
    LaunchedEffect(activeEvent) {
        activeEvent?.let { event ->
            val date = event.date?.let { Date(it) } ?: Date()
            val day = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
            val dayOfMonth = SimpleDateFormat("dd", Locale.getDefault()).format(date)
            val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase()
            val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
            val formattedDateString = "$day • $dayOfMonth $month • $year"

            // Update specific TextElement items inside the card data
            val updatedElements = cardData.elements.mapIndexed { index, element ->
                when (index) {
                    1 -> element.copy(text = event.name.ifBlank { element.text }) // Event Name / Title
                    3 -> element.copy(text = formattedDateString)               // Event Date
                    else -> element
                }
            }

            cardData = cardData.copy(elements = updatedElements)
        }
    }

    AnimatedContent(
        targetState = currentView,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "InvitationCardsViewContent"
    ) { view ->
        when (view) {
            InvitationCardsView.MAIN -> {
                InvitationCardsMainContent(
                    cardData = cardData,
                    onBackClick = onBackClick,
                    onEditDetailsClick = { currentView = InvitationCardsView.EDIT_DETAILS }
                )
            }
            InvitationCardsView.EDIT_DETAILS -> {
                EditInvitationDetailsScreen(
                    initialData = cardData,
                    onDataChange = { cardData = it },
                    onBackClick = { currentView = InvitationCardsView.MAIN }
                )
            }
        }
    }
}

@Composable
fun InvitationCardsMainContent(
    cardData: InvitationCardData,
    onBackClick: () -> Unit,
    onEditDetailsClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    var selectedTab by remember { mutableStateOf(InvitationCardsTab.EXPLORE) }

    val backgrounds = listOf(
        R.drawable.bg_invitation_card_01,
        R.drawable.bg_invitation_card_02,
        R.drawable.bg_invitation_card_03,
        R.drawable.bg_invitation_card_04,
        R.drawable.bg_invitation_card_05
    )

    val pagerState = rememberPagerState(
        initialPage = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % backgrounds.size),
        pageCount = { Int.MAX_VALUE }
    )

    var cardToCapture by remember { mutableStateOf<InvitationCardData?>(null) }
    var likedCardRes by remember { mutableStateOf(setOf<Int>()) }

    val onLikeToggle = { resId: Int ->
        likedCardRes = if (likedCardRes.contains(resId)) {
            likedCardRes - resId
        } else {
            likedCardRes + resId
        }
    }

    val onShareTrigger = { data: InvitationCardData, whatsappOnly: Boolean ->
        coroutineScope.launch {
            cardToCapture = data
            delay(100.milliseconds)
            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
            ShareUtils.shareImage(context, bitmap, whatsappOnly = whatsappOnly)
            cardToCapture = null
        }
        Unit
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
            val currentCarouselCard = cardData.copy(
                backgroundRes = backgrounds[pagerState.currentPage % backgrounds.size]
            )
            InvitationCardItem(
                data = cardToCapture ?: currentCarouselCard,
                forCapture = true,
                modifier = Modifier.fillMaxSize()
            )
        }

        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier.statusBarsPadding()
                ) {
                    CustomTopBar(
                        title = "Cards",
                        isLargeTitle = true,
                        onBackClick = onBackClick,
                        onMenuClick = { },
                        menuIcon = TopIcon.Predefined.MENU_VERTICAL
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
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "InvitationCardsTabContent"
                ) { tab ->
                    when (tab) {
                        InvitationCardsTab.EXPLORE -> {
                            ExploreCardsContent(
                                cardData = cardData,
                                backgrounds = backgrounds,
                                likedCardRes = likedCardRes,
                                onLikeToggle = onLikeToggle,
                                pagerState = pagerState,
                                onEditDetailsClick = onEditDetailsClick,
                                onShareTrigger = onShareTrigger
                            )
                        }
                        InvitationCardsTab.SAVED -> {
                            SavedCardsContent(
                                cardData = cardData,
                                backgrounds = backgrounds,
                                likedCardRes = likedCardRes,
                                onLikeToggle = onLikeToggle,
                                onShareClick = { onShareTrigger(it, false) }
                            )
                        }
                    }
                }

            }
        }

        // Floating Tab anchored to the bottom center
        BottomTab(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            items = listOf(
                TabItem(
                    label = "Explore",
                    value = InvitationCardsTab.EXPLORE,
                    icon = painterResource(R.drawable.ic_notes)
                ),
                TabItem(
                    label = "Saved",
                    value = InvitationCardsTab.SAVED,
                    icon = painterResource(R.drawable.ic_heart)
                )
            ),
            selectedValue = selectedTab,
            onItemSelected = { selectedTab = it },
            style = BottomTabStyle.FLOATING
        )
    }
}

@Composable
fun ExploreCardsContent(
    cardData: InvitationCardData,
    backgrounds: List<Int>,
    likedCardRes: Set<Int>,
    onLikeToggle: (Int) -> Unit,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onEditDetailsClick: () -> Unit,
    onShareTrigger: (InvitationCardData, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InvitationCardCarousel(
                    cardData = cardData,
                    cardWidth = 280.dp,
                    cardHeight = 373.dp,
                    pagerState = pagerState,
                    isLiked = { likedCardRes.contains(it) },
                    onLikeClick = { card -> onLikeToggle(card.backgroundRes) },
                    onShareClick = { card -> onShareTrigger(card, false) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CustomIconButton(
                        icon = painterResource(id = R.drawable.ic_edit),
                        onClick = onEditDetailsClick,
                        type = ButtonType.Secondary
                    )
                    CustomTextButton(
                        text = "Share Card",
                        onClick = {
                            val currentCard = cardData.copy(
                                backgroundRes = backgrounds[pagerState.currentPage % backgrounds.size]
                            )
                            onShareTrigger(currentCard, false)
                        },
                        modifier = Modifier.weight(1f),
                        containerColor = ContentPrimary,
                        contentColor = ContentInvPrimary,
                        trailingIcon = painterResource(id = R.drawable.ic_share)
                    )
                    CustomIconButton(
                        icon = painterResource(id = R.drawable.ic_whatsapp),
                        onClick = {
                            val currentCard = cardData.copy(
                                backgroundRes = backgrounds[pagerState.currentPage % backgrounds.size]
                            )
                            onShareTrigger(currentCard, true)
                        },
                        containerColor = Color(0xFF1BA911),
                        contentColor = ContentInvPrimary
                    )
                }
            }
        }

        item {
            DashedDivider()
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Explore Cards",
                    style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(backgrounds) { bgRes ->
            val card = cardData.copy(backgroundRes = bgRes)
            InvitationCardItem(
                data = card,
                showControls = true,
                isLiked = likedCardRes.contains(bgRes),
                onLikeClick = { onLikeToggle(bgRes) },
                onShareClick = { onShareTrigger(card, false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .aspectRatio(280f / 373f)
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Extra space for floating tab
        }
    }
}

@Composable
fun SavedCardsContent(
    cardData: InvitationCardData,
    backgrounds: List<Int>,
    likedCardRes: Set<Int>,
    onLikeToggle: (Int) -> Unit,
    onShareClick: (InvitationCardData) -> Unit
) {
    val likedBackgrounds = backgrounds.filter { likedCardRes.contains(it) }

    if (likedBackgrounds.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No saved cards yet",
                style = JasnifyTheme.typography.bodyLarge,
                color = ContentSecondary
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(likedBackgrounds) { bgRes ->
                val card = cardData.copy(backgroundRes = bgRes)
                InvitationCardItem(
                    data = card,
                    showControls = true,
                    isLiked = true,
                    onLikeClick = { onLikeToggle(bgRes) },
                    onShareClick = { onShareClick(card) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(280f / 373f)
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
