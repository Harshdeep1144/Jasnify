package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.CardTheme
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
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
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.CardViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.utils.ShareUtils
import com.harshdeep.jasnify.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
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

@OptIn(ExperimentalSharedTransitionApi::class)
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
    val cardRoomData by cardViewModel.cardRoomData.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()

    val currentUser = remember { FirebaseAuth.getInstance().currentUser }
    val canEdit = remember(roomUsers, currentUser) {
        val userRole = roomUsers.find { it.uid == currentUser?.uid }?.role ?: UserRole.VIEWER
        userRole != UserRole.VIEWER
    }

    var currentView by remember { mutableStateOf(CardsView.MAIN) }
    var selectedCard by remember { mutableStateOf<CardData?>(null) }
    var activeTransitionKey by remember { mutableStateOf<String?>(null) }
    var editingCard by remember { mutableStateOf<CardData?>(null) }

    // Remembered tab state across screen transitions
    var selectedTab by remember { mutableStateOf(CardsTab.EXPLORE) }

    // Persistent scroll & pager states across navigation
    val exploreLazyListState = rememberLazyListState()
    val myCardsGridState = rememberLazyGridState()
    val explorePagerState = rememberPagerState(
        initialPage = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % 5),
        pageCount = { Int.MAX_VALUE }
    )

    var showMenuSheet by remember { mutableStateOf(false) }
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var toastData by remember { mutableStateOf(ToastData()) }
    var sheetMotionProgress by remember { mutableFloatStateOf(1.0f) }

    var selectedCardIds by remember { mutableStateOf(setOf<String>()) }

    val isAnySheetVisible = showMenuSheet || showRoomMenuBottomSheet || userToRemove != null || showLeaveConfirmation || showDeleteConfirmation

    val targetScale = if (isAnySheetVisible) 0.92f + (0.08f * sheetMotionProgress) else 1.0f

    val backdropScaleState = animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadiusState = animateDpAsState(
        targetValue = if (isAnySheetVisible) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    var isBottomTabVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember(selectedTab, exploreLazyListState, myCardsGridState, isAnySheetVisible) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (currentView != CardsView.MAIN || isAnySheetVisible || selectedCardIds.isNotEmpty()) return Offset.Zero

                val delta = available.y
                val canScroll = if (selectedTab == CardsTab.EXPLORE) exploreLazyListState.canScrollForward || exploreLazyListState.canScrollBackward
                else myCardsGridState.canScrollForward || myCardsGridState.canScrollBackward

                if (!canScroll) {
                    isBottomTabVisible = true
                    return Offset.Zero
                }

                if (delta > 0) { // Scrolling up (showing)
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) { // Scrolling down (hiding)
                    if (scrollAccumulator > 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                }

                if (scrollAccumulator > 150f && !isBottomTabVisible) {
                    isBottomTabVisible = true
                    scrollAccumulator = 0f
                } else if (scrollAccumulator < -150f && isBottomTabVisible) {
                    isBottomTabVisible = false
                    scrollAccumulator = 0f
                }

                return Offset.Zero
            }
        }
    }

    LaunchedEffect(activeEventId, cardRoomData) {
        if (activeEventId != null && cardRoomData == null) {
            val defaultThemes = listOf(
                CardTheme(id = "default_1", name = "Classic Elegance", resId = R.drawable.bg_invitation_card_01, isDefault = true),
                CardTheme(id = "default_2", name = "Floral Romance", resId = R.drawable.bg_invitation_card_02, isDefault = true),
                CardTheme(id = "default_3", name = "Golden Glamour", resId = R.drawable.bg_invitation_card_03, isDefault = true),
                CardTheme(id = "default_4", name = "Modern Minimalist", resId = R.drawable.bg_invitation_card_04, isDefault = true),
                CardTheme(id = "default_5", name = "Vintage Botanical", resId = R.drawable.bg_invitation_card_05, isDefault = true)
            )
            cardViewModel.initializeRoom(defaultThemes)
        }
    }

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
            delay(2000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    BackHandler {
        when {
            showDeleteConfirmation -> showDeleteConfirmation = false
            showMenuSheet -> showMenuSheet = false
            showRoomMenuBottomSheet -> showRoomMenuBottomSheet = false
            userToRemove != null -> userToRemove = null
            showLeaveConfirmation -> showLeaveConfirmation = false
            selectedCardIds.isNotEmpty() -> selectedCardIds = emptySet()
            currentView == CardsView.LIKED_CARDS -> currentView = CardsView.MAIN
            currentView == CardsView.FULL_VIEW -> currentView = CardsView.MAIN
            currentView == CardsView.EDIT_DETAILS -> currentView = CardsView.MAIN
            currentView != CardsView.MAIN -> currentView = CardsView.MAIN
            else -> onBackClick()
        }
    }

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
                    .graphicsLayer {
                        scaleX = backdropScaleState.value
                        scaleY = backdropScaleState.value
                        val radius = backdropCornerRadiusState.value
                        clip = isAnySheetVisible || radius > 0.dp
                        shape = RoundedCornerShape(radius.coerceAtLeast(0.dp))
                    }
                    .background(BackgroundPrimary)
            ) {
                SharedTransitionLayout {
                    AnimatedContent(
                        targetState = currentView,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                        },
                        label = "CardsViewContent"
                    ) { view ->
                        when (view) {
                            CardsView.MAIN -> {
                                CardsMainContent(
                                    selectedTab = selectedTab,
                                    onTabSelected = {
                                        selectedTab = it
                                        selectedCardIds = emptySet()
                                    },
                                    myCards = myCards,
                                    likedCards = likedCards,
                                    activeEvent = activeEvent,
                                    selectedCardIds = selectedCardIds,
                                    exploreLazyListState = exploreLazyListState,
                                    myCardsGridState = myCardsGridState,
                                    explorePagerState = explorePagerState,
                                    animatedVisibilityScope = this@AnimatedContent,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    canEdit = canEdit,
                                    nestedScrollConnection = nestedScrollConnection,
                                    onToggleCardSelection = { id ->
                                        selectedCardIds = if (selectedCardIds.contains(id)) {
                                            selectedCardIds - id
                                        } else {
                                            selectedCardIds + id
                                        }
                                    },
                                    onBackClick = {
                                        if (selectedCardIds.isNotEmpty()) {
                                            selectedCardIds = emptySet()
                                        } else {
                                            onBackClick()
                                        }
                                    },
                                    onMenuClick = {
                                        if (selectedCardIds.isNotEmpty()) {
                                            showDeleteConfirmation = true
                                        } else if (selectedTab == CardsTab.EXPLORE) {
                                            showMenuSheet = true
                                        } else {
                                            currentView = CardsView.LIKED_CARDS
                                        }
                                    },
                                    onCardClick = { card, transitionKey ->
                                        selectedCard = card
                                        activeTransitionKey = transitionKey
                                        currentView = CardsView.FULL_VIEW
                                    },
                                    onLikeToggle = { card -> cardViewModel.toggleLikedCard(card) },
                                    onEditDetailsClick = { card ->
                                        editingCard = if (card.id.startsWith("template_")) {
                                            card.copy(id = UUID.randomUUID().toString())
                                        } else {
                                            card
                                        }
                                        currentView = CardsView.EDIT_DETAILS
                                    }
                                )
                            }
                            CardsView.FULL_VIEW -> {
                                selectedCard?.let { card ->
                                    CardFullView(
                                        card = card,
                                        transitionKey = activeTransitionKey ?: "card_${card.id}",
                                        animatedVisibilityScope = this@AnimatedContent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        canEdit = canEdit,
                                        onBackClick = { currentView = CardsView.MAIN },
                                        onEditDetailsClick = {
                                            editingCard = if (card.id.startsWith("template_")) {
                                                card.copy(id = UUID.randomUUID().toString())
                                            } else {
                                                card
                                            }
                                            currentView = CardsView.EDIT_DETAILS
                                        }
                                    )
                                }
                            }
                            CardsView.LIKED_CARDS -> {
                                LikedCardsContent(
                                    cards = likedCards,
                                    animatedVisibilityScope = this@AnimatedContent,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    onBackClick = { currentView = CardsView.MAIN },
                                    onCardClick = { card, transitionKey ->
                                        selectedCard = card
                                        activeTransitionKey = transitionKey
                                        currentView = CardsView.FULL_VIEW
                                    },
                                    onLikeToggle = { card -> cardViewModel.toggleLikedCard(card) }
                                )
                            }
                            CardsView.EDIT_DETAILS -> {
                                editingCard?.let { card ->
                                    EditCardDetailsScreen(
                                        initialData = card,
                                        allCards = myCards,
                                        cardRoomData = cardRoomData,
                                        onDataChange = { updated ->
                                            cardViewModel.saveMyCard(updated)
                                            selectedCard = updated
                                            editingCard = updated
                                        },
                                        onBackClick = { currentView = CardsView.MAIN },
                                        onUploadImage = { uri, onSuccess, onError ->
                                            cardViewModel.uploadThemeImage(uri, onSuccess, onError)
                                        },
                                        onUpdateThemeName = { id, name ->
                                            cardViewModel.updateThemeName(id, name)
                                        }
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

                // Animated Bottom Tab: Hidden when in FULL_VIEW or Selection Mode or Scrolling
                AnimatedVisibility(
                    visible = currentView == CardsView.MAIN && selectedCardIds.isEmpty() && isBottomTabVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 260)
                    ) + fadeIn(animationSpec = tween(durationMillis = 260)),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 260)
                    ) + fadeOut(animationSpec = tween(durationMillis = 260)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(10f)
                ) {
                    BottomTab(
                        items = listOf(
                            TabItem(
                                label = "Explore",
                                value = CardsTab.EXPLORE,
                                icon = painterResource(R.drawable.ic_share_card)
                            ),
                            TabItem(
                                label = "My Edits",
                                value = CardsTab.MY_CARDS,
                                icon = painterResource(R.drawable.ic_edit)
                            )
                        ),
                        selectedValue = selectedTab,
                        onItemSelected = { selectedTab = it },
                        style = BottomTabStyle.FLOATING
                    )
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
                .padding(12.dp)
        ) {
            CustomToast(message = toastData.message ?: "", type = toastData.type)
        }

        if (showMenuSheet) {
            MenuBottomSheet(
                items = listOf(
                    listOf(
                        MenuSheetActionItem(
                            text = "Saved Cards",
                            icon = painterResource(R.drawable.ic_top_bar_heart),
                            onClick = {
                                showMenuSheet = false
                                currentView = CardsView.LIKED_CARDS
                            }
                        )
                    ),
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

        if (showDeleteConfirmation) {
            val count = selectedCardIds.size
            ConfirmationBottomSheet(
                heading = if (count == 1) "Delete Card?" else "Delete $count Cards?",
                subHeading = "This action cannot be undone.",
                confirmButtonText = "Delete",
                onDismiss = { showDeleteConfirmation = false },
                onConfirm = {
                    selectedCardIds.forEach { id ->
                        cardViewModel.deleteMyCard(id)
                    }
                    val deletedCount = selectedCardIds.size
                    selectedCardIds = emptySet()
                    toastData = ToastData(
                        message = if (deletedCount == 1) "Card deleted" else "$deletedCount cards deleted",
                        type = ToastType.ERROR
                    )
                    showDeleteConfirmation = false
                },
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
                        toastData = ToastData("${it.name} removed", ToastType.ERROR)
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
                            else -> TopIcon.CustomPainter(painterResource(R.drawable.ic_top_bar_heart)) // Consider updating this icon to a 'saved' bookmark icon if available
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
                                onShareTrigger = { onShareTrigger(it, false) },
                                onEditDetailsClick = onEditDetailsClick,
                                onWhatsappShare = { onShareTrigger(it, true) }
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
                                onShareClick = { onShareTrigger(it, false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

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
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val currentCarouselCard = templates[pagerState.currentPage % templates.size]
                val carouselKey = "carousel_${currentCarouselCard.id}"

                with(sharedTransitionScope) {
                    Box(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = carouselKey),
                            animatedVisibilityScope = animatedVisibilityScope,
                            clipInOverlayDuringTransition = OverlayClip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                        )
                    ) {
                        CardCarousel(
                            cardData = currentCarouselCard,
                            pagerState = pagerState,
                            isLiked = { resId -> likedCards.any { it.backgroundRes == resId } },
                            onLikeClick = onLikeToggle,
                            onShareClick = onShareTrigger,
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

        item {
            DashedDivider()
            Spacer(Modifier.height(24.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trend_up),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Trending Templates",
                    style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                    color = ContentPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        val chunkedTemplates = templates.chunked(2)
        items(chunkedTemplates) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                    clipInOverlayDuringTransition = OverlayClip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                                )
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
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            FooterJansify()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MyCardsGrid(
    cards: List<CardData>,
    templates: List<CardData>,
    gridState: LazyGridState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    canEdit: Boolean,
    nestedScrollConnection: NestedScrollConnection,
    onStartEditing: () -> Unit,
    selectedCardIds: Set<String>,
    onToggleSelection: (String) -> Unit,
    onCardClick: (CardData, String) -> Unit,
    onShareClick: (CardData) -> Unit,
) {
    if (cards.isEmpty()) {
        val pagerState = rememberPagerState(
            initialPage = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % templates.size),
            pageCount = { Int.MAX_VALUE }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CardCarousel(
                cardData = templates[pagerState.currentPage % templates.size],
                showControls = false,
                pagerState = pagerState,
                onCardClick = { if (canEdit) onStartEditing() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (canEdit) {
                CustomTextButton(
                    text = "Start Editing",
                    onClick = onStartEditing,
                    modifier = Modifier
                        .width(200.dp)
                        .height(56.dp),
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    shapeStyle = ButtonShapeStyle.Round
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Make your edits right on your\nfavorite template.",
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            } else {
                Text(
                    text = "No cards edited yet.",
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    } else {
        val isSelectionMode = selectedCardIds.isNotEmpty()

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp, 12.dp, 12.dp, 120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            items(cards, key = { it.id }) { card ->
                val isSelected = selectedCardIds.contains(card.id)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.96f else 1f,
                    label = "scale"
                )
                val myCardKey = "my_card_${card.id}"

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    with(sharedTransitionScope) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(280f / 373f)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = myCardKey),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    clipInOverlayDuringTransition = OverlayClip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                                )
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                                .combinedClickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = {
                                        if (isSelectionMode) {
                                            onToggleSelection(card.id)
                                        } else {
                                            onCardClick(card, myCardKey)
                                        }
                                    },
                                    onLongClick = {
                                        onToggleSelection(card.id)
                                    }
                                )
                                .then(
                                    if (isSelected) Modifier.border(2.dp, ContentBrand, SquircleShape(CornerMedium, CornerSmoothingDefault))
                                    else Modifier
                                )
                        ) {
                            CardItem(
                                data = card,
                                showControls = !isSelectionMode,
                                onShareClick = { onShareClick(card) },
                                onLikeClick = null,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (isSelectionMode) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.align(Alignment.TopEnd),
                                        shape = CircleShape,
                                        color = if (isSelected) SurfacePrimary else Color.Black.copy(0.3f)
                                    ) {
                                        CustomChecker(
                                            checked = isSelected,
                                            onCheckedChange = {
                                                onToggleSelection(card.id)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit_pen),
                            contentDescription = null,
                            tint = ContentSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Edited ${TimeUtils.getTimeAgo(card.lastEdited)}",
                            style = JasnifyTheme.typography.labelLarge,
                            color = if (isSelected) ContentBrand else ContentSecondary,
                            maxLines = 1,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LikedCardsContent(
    cards: List<CardData>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onBackClick: () -> Unit,
    onCardClick: (CardData, String) -> Unit,
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Saved cards yet.", style = JasnifyTheme.typography.bodyLarge, color = ContentSecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(cards, key = { it.id }) { card ->
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
                                    clipInOverlayDuringTransition = OverlayClip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                                )
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) { onCardClick(card, likedKey) }
                        ) {
                            CardItem(
                                data = card,
                                showControls = true,
                                isLiked = true,
                                onLikeClick = { onLikeToggle(card) },
                                onShareClick = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

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
        ){
            CustomTopBar(
                onBackClick = onBackClick,
                onMenuClick = { onShareTrigger(card) },
                backIcon = TopIcon.Predefined.BACK_2,
                menuIcon = TopIcon.CustomPainter(painterResource(R.drawable.ic_share)),
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
                            clipInOverlayDuringTransition = OverlayClip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                        )
                        .clip(SquircleShape(CornerMedium, CornerSmoothingDefault))
                ) {
                    CardItem(
                        data = card,
                        forCapture = false,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Bottom Action Button (Visible only when user can edit)
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
                    leadingIcon = painterResource(R.drawable.ic_edit),
                    shapeStyle = ButtonShapeStyle.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                )
            }
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