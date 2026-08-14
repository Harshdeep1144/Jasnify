package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.components.carousels.CardCarousel
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTabStyle
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.CardViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.utils.ShareUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

enum class CardsView {
    MAIN,
    EDIT_DETAILS,
    FULL_VIEW,
    LIKED_CARDS,
    ROOM
}

enum class CardsTab {
    EXPLORE,
    MY_CARDS
}

@Composable
fun CardsScreen(
    onBackClick: () -> Unit = {},
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    cardViewModel: CardViewModel = hiltViewModel()
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val myCards by cardViewModel.myCards.collectAsStateWithLifecycle()
    val likedCards by cardViewModel.likedCards.collectAsStateWithLifecycle()

    var currentView by remember { mutableStateOf(CardsView.MAIN) }
    var selectedCard by remember { mutableStateOf<CardData?>(null) }
    var editingCard by remember { mutableStateOf<CardData?>(null) }
    var selectedTab by remember { mutableStateOf(CardsTab.EXPLORE) }

    var showMenuSheet by remember { mutableStateOf(false) }
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var toastData by remember { mutableStateOf(ToastData()) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    LaunchedEffect(activeEventId) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (activeEventId != null) {
            roomViewModel.verifyAccess(activeEventId!!, "Cards", uid)
            roomViewModel.loadRoomUsers(activeEventId!!, "Cards")
            cardViewModel.setEventId(activeEventId!!)
        } else {
            roomViewModel.setAccessState(true)
        }
    }

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    BackHandler {
        when {
            showMenuSheet -> showMenuSheet = false
            showRoomMenuBottomSheet -> showRoomMenuBottomSheet = false
            currentView == CardsView.LIKED_CARDS -> currentView = CardsView.MAIN
            currentView == CardsView.FULL_VIEW -> currentView = CardsView.MAIN
            currentView == CardsView.EDIT_DETAILS -> currentView = CardsView.MAIN
            currentView != CardsView.MAIN -> currentView = CardsView.MAIN
            else -> onBackClick()
        }
    }

    val isAnySheetVisible = showMenuSheet || showRoomMenuBottomSheet || userToRemove != null || showLeaveConfirmation
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        RoomAccessGuardian(
            hasAccess = hasAccess,
            roomName = "Cards",
            onBackClick = onBackClick
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundPrimary)
            ) {
                AnimatedContent(
                    targetState = currentView,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "CardsViewContent"
                ) { view ->
                    when (view) {
                        CardsView.MAIN -> {
                            CardsMainContent(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it },
                                myCards = myCards,
                                likedCards = likedCards,
                                activeEvent = activeEvent,
                                onBackClick = onBackClick,
                                onMenuClick = { 
                                    if (selectedTab == CardsTab.EXPLORE) showMenuSheet = true 
                                    else currentView = CardsView.LIKED_CARDS 
                                },
                                onCardClick = { card ->
                                    selectedCard = card
                                    currentView = CardsView.FULL_VIEW
                                },
                                onLikeToggle = { card -> cardViewModel.toggleLikedCard(card) },
                                onEditDetailsClick = { card ->
                                    editingCard = card
                                    currentView = CardsView.EDIT_DETAILS
                                }
                            )
                        }
                        CardsView.FULL_VIEW -> {
                            selectedCard?.let { card ->
                                val isCardLiked = likedCards.any { it.backgroundRes == card.backgroundRes }
                                CardFullView(
                                    card = card,
                                    isLiked = isCardLiked,
                                    onBackClick = { currentView = CardsView.MAIN },
                                    onEditDetailsClick = {
                                        editingCard = card
                                        currentView = CardsView.EDIT_DETAILS
                                    }
                                )
                            }
                        }
                        CardsView.LIKED_CARDS -> {
                            LikedCardsContent(
                                cards = likedCards,
                                onBackClick = { currentView = CardsView.MAIN },
                                onCardClick = { card ->
                                    selectedCard = card
                                    currentView = CardsView.FULL_VIEW
                                },
                                onLikeToggle = { card -> cardViewModel.toggleLikedCard(card) }
                            )
                        }
                        CardsView.EDIT_DETAILS -> {
                            editingCard?.let { card ->
                                EditCardDetailsScreen(
                                    initialData = card,
                                    onDataChange = { updated ->
                                        val cardToSave = if (updated.id.startsWith("template_")) {
                                            updated.copy(id = UUID.randomUUID().toString())
                                        } else {
                                            updated
                                        }
                                        cardViewModel.saveMyCard(cardToSave)
                                        selectedCard = cardToSave
                                    },
                                    onBackClick = { currentView = CardsView.MAIN }
                                )
                            }
                        }
                        CardsView.ROOM -> {
                            activeEvent?.let { event ->
                                CardRoomContent(
                                    eventId = event.id,
                                    roomViewModel = roomViewModel,
                                    onBackClick = { currentView = CardsView.MAIN },
                                    onMenuClick = { showRoomMenuBottomSheet = true },
                                    onRemove = { userToRemove = it },
                                    onLeave = { showLeaveConfirmation = true },
                                    onShowToast = { toastData = it }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Toasts and Overlays
        AnimatedVisibility(
            visible = toastData.message != null && !isAnySheetVisible,
            enter = slideInVertically(initialOffsetY = { -it - 500 }),
            exit = slideOutVertically(targetOffsetY = { -it - 500 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .zIndex(100f)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            CustomToast(message = toastData.message ?: "", type = toastData.type)
        }

        if (showMenuSheet) {
            MenuBottomSheet(
                items = listOf(
                    listOf(
                        MenuSheetActionItem(
                            text = "Manage Room Access",
                            icon = painterResource(R.drawable.ic_user_default),
                            onClick = {
                                showMenuSheet = false
                                currentView = CardsView.ROOM
                            }
                        )
                    )
                ),
                onCancelClick = { showMenuSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showRoomMenuBottomSheet) {
            MenuBottomSheet(
                items = listOf(
                    listOf(
                        MenuSheetActionItem(
                            text = "Leave Room",
                            icon = painterResource(R.drawable.ic_logout),
                            contentColor = MaterialTheme.colorScheme.error,
                            onClick = {
                                showRoomMenuBottomSheet = false
                                showLeaveConfirmation = true
                            }
                        )
                    )
                ),
                onCancelClick = { showRoomMenuBottomSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        userToRemove?.let {
            ConfirmationBottomSheet(
                heading = "Remove ${it.name}?",
                subHeading = "They will not be able to access this room anymore.",
                confirmButtonText = "Remove",
                onDismiss = { userToRemove = null },
                onConfirm = {
                    if (activeEventId != null) {
                        roomViewModel.removeAccess(activeEventId!!, "Cards", it.uid)
                        toastData = ToastData("${it.name} removed", ToastType.SUCCESS)
                    }
                    userToRemove = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showLeaveConfirmation) {
            ConfirmationBottomSheet(
                heading = "Leave Room?",
                subHeading = "You will lose access to this room.",
                confirmButtonText = "Leave",
                onDismiss = { showLeaveConfirmation = false },
                onConfirm = {
                    activeEventId?.let { eventId ->
                        roomViewModel.removeAccess(eventId, "Cards", FirebaseAuth.getInstance().currentUser?.uid ?: "")
                    }
                    toastData = ToastData("You left the room", ToastType.DEFAULT)
                    currentView = CardsView.MAIN
                    showLeaveConfirmation = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}

@Composable
fun CardsMainContent(
    selectedTab: CardsTab,
    onTabSelected: (CardsTab) -> Unit,
    myCards: List<CardData>,
    likedCards: List<CardData>,
    activeEvent: com.harshdeep.jasnify.domain.model.Event?,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onCardClick: (CardData) -> Unit,
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
            val baseCard = CardData(id = "template_$index", backgroundRes = resId)
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
                        title = "Cards",
                        isLargeTitle = true,
                        onBackClick = onBackClick,
                        onMenuClick = onMenuClick,
                        menuIcon = if (selectedTab == CardsTab.EXPLORE) TopIcon.Predefined.MENU_HORIZONTAL else TopIcon.CustomPainter(painterResource(R.drawable.ic_heart))
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
                    label = "CardsTabContent"
                ) { tab ->
                    when (tab) {
                        CardsTab.EXPLORE -> {
                            ExploreTabContent(
                                templates = templates,
                                likedCards = likedCards,
                                onCardClick = onCardClick,
                                onLikeToggle = onLikeToggle,
                                onShareTrigger = { onShareTrigger(it, false) },
                                onEditDetailsClick = onEditDetailsClick,
                                onWhatsappShare = { onShareTrigger(it, true) }
                            )
                        }
                        CardsTab.MY_CARDS -> {
                            MyCardsGrid(
                                cards = myCards,
                                onCardClick = onCardClick,
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
                    value = CardsTab.EXPLORE,
                    icon = painterResource(R.drawable.ic_file)
                ),
                TabItem(
                    label = "My Cards",
                    value = CardsTab.MY_CARDS,
                    icon = painterResource(R.drawable.ic_heart)
                )
            ),
            selectedValue = selectedTab,
            onItemSelected = onTabSelected,
            style = BottomTabStyle.FLOATING
        )
    }
}

@Composable
fun ExploreTabContent(
    templates: List<CardData>,
    likedCards: List<CardData>,
    onCardClick: (CardData) -> Unit,
    onLikeToggle: (CardData) -> Unit,
    onShareTrigger: (CardData) -> Unit,
    onEditDetailsClick: (CardData) -> Unit,
    onWhatsappShare: (CardData) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % templates.size),
        pageCount = { Int.MAX_VALUE }
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CardCarousel(
                    cardData = templates[pagerState.currentPage % templates.size],
                    cardWidth = 280.dp,
                    cardHeight = 373.dp,
                    pagerState = pagerState,
                    isLiked = { resId -> likedCards.any { it.backgroundRes == resId } },
                    onLikeClick = onLikeToggle,
                    onShareClick = onShareTrigger,
                    onCardClick = onCardClick
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
                        onClick = { onEditDetailsClick(templates[pagerState.currentPage % templates.size]) },
                        containerColor = SurfaceSecondary,
                        contentColor = ContentPrimary
                    )
                    CustomTextButton(
                        text = "Share Card",
                        onClick = { onShareTrigger(templates[pagerState.currentPage % templates.size]) },
                        modifier = Modifier.weight(1f),
                        containerColor = ContentPrimary,
                        contentColor = ContentInvPrimary,
                        trailingIcon = painterResource(id = R.drawable.ic_share)
                    )
                    CustomIconButton(
                        icon = painterResource(id = R.drawable.ic_whatsapp),
                        onClick = { onWhatsappShare(templates[pagerState.currentPage % templates.size]) },
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
                    text = "Trending Templates",
                    style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Grid within LazyColumn
        val chunkedTemplates = templates.chunked(2)
        items(chunkedTemplates) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { template ->
                    val isLiked = likedCards.any { it.backgroundRes == template.backgroundRes }
                    Box(modifier = Modifier.weight(1f).aspectRatio(280f / 373f)) {
                        CardItem(
                            data = template,
                            showControls = true,
                            isLiked = isLiked,
                            onLikeClick = { onLikeToggle(template) },
                            onShareClick = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onCardClick(template) }
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyCardsGrid(
    cards: List<CardData>,
    onCardClick: (CardData) -> Unit,
    onShareClick: (CardData) -> Unit,
) {
    var selectedCardIdForDelete by remember { mutableStateOf<String?>(null) }

    if (cards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No cards created yet.", style = JasnifyTheme.typography.bodyLarge, color = ContentSecondary)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp, 12.dp, 12.dp, 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(cards) { card ->
                val isSelectedForDelete = selectedCardIdForDelete == card.id
                Box(modifier = Modifier
                    .aspectRatio(280f / 373f)
                    .combinedClickable(
                        onClick = {
                            if (isSelectedForDelete) selectedCardIdForDelete = null
                            else onCardClick(card)
                        },
                        onLongClick = {
                            selectedCardIdForDelete = if (isSelectedForDelete) null else card.id
                        }
                    )
                ) {
                    CardItem(
                        data = card,
                        showControls = true,
                        onShareClick = if (isSelectedForDelete) null else { { onShareClick(card) } },
                        onLikeClick = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun LikedCardsContent(
    cards: List<CardData>,
    onBackClick: () -> Unit,
    onCardClick: (CardData) -> Unit,
    onLikeToggle: (CardData) -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Saved Templates",
                    isLargeTitle = true,
                    onBackClick = onBackClick
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { innerPadding ->
        if (cards.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(text = "No liked cards yet.", style = JasnifyTheme.typography.bodyLarge, color = ContentSecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                items(cards) { card ->
                    Box(modifier = Modifier.aspectRatio(280f / 373f)) {
                        CardItem(
                            data = card,
                            showControls = true,
                            isLiked = true,
                            onLikeClick = { onLikeToggle(card) },
                            onShareClick = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onCardClick(card) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardFullView(
    card: CardData,
    isLiked: Boolean,
    onBackClick: () -> Unit,
    onEditDetailsClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    var cardToCapture by remember { mutableStateOf<CardData?>(null) }

    val onShareTrigger = { data: CardData ->
        coroutineScope.launch {
            cardToCapture = data
            delay(100.milliseconds)
            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
            ShareUtils.shareImage(context, bitmap, whatsappOnly = false)
            cardToCapture = null
        }
        Unit
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBackClick,
                shape = CircleShape,
                color = SurfaceSecondary,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = ContentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Surface(
                onClick = { onShareTrigger(card) },
                shape = CircleShape,
                color = SurfaceSecondary,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_share),
                        contentDescription = "Share",
                        tint = ContentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 120.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            CardItem(
                data = card,
                modifier = Modifier.fillMaxHeight().aspectRatio(280f/373f)
            )
        }

        // Bottom Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp, start = 24.dp, end = 24.dp)
        ) {
            CustomTextButton(
                text = "Edit Details",
                onClick = onEditDetailsClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                containerColor = ContentPrimary,
                contentColor = ContentInvPrimary,
                leadingIcon = painterResource(R.drawable.ic_edit),
                shapeStyle = ButtonShapeStyle.Round
            )
        }
    }
}

@Composable
fun CardRoomContent(
    eventId: String,
    roomViewModel: RoomViewModel,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onRemove: (User) -> Unit,
    onLeave: () -> Unit,
    onShowToast: (ToastData) -> Unit
) {
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val currentUserRole = roomUsers.find { it.uid == currentUser?.uid }?.role ?: UserRole.VIEWER

    val displayUsers = remember(roomUsers, currentUser) {
        if (currentUser == null) return@remember roomUsers

        val self = User(
            uid = currentUser.uid,
            name = currentUser.displayName ?: "Me",
            email = currentUser.email ?: "",
            role = roomUsers.find { it.uid == currentUser.uid }?.role ?: currentUserRole,
            username = currentUser.email?.substringBefore("@") ?: "me"
        )

        val baseList = if (roomUsers.any { it.uid == currentUser.uid }) {
            roomUsers.map { if (it.uid == currentUser.uid) self.copy(role = it.role) else it }
        } else {
            listOf(self) + roomUsers
        }
        baseList.distinctBy { it.uid }
    }

    RoomScreen(
        allUsers = displayUsers,
        currentUserRole = currentUserRole,
        isSelf = { it.uid == currentUser?.uid },
        onBackClick = onBackClick,
        onMenuClick = onMenuClick,
        onRoleChange = { user, newRole ->
            roomViewModel.updateRole(eventId, "Cards", user, newRole)
        },
        onRemove = onRemove,
        onReport = { user -> onShowToast(ToastData("${user.name} reported", ToastType.DEFAULT)) },
        onLeave = onLeave,
        searchResults = searchResults,
        onSearch = { query -> roomViewModel.searchUsers(query) },
        onGrantAccess = { email, role ->
            roomViewModel.grantAccess(eventId, "Cards", email, role)
            onShowToast(ToastData("Access granted to $email", ToastType.SUCCESS))
        }
    )
}
