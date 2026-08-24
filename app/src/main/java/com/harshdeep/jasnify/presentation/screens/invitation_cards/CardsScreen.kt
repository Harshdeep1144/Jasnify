package com.harshdeep.jasnify.presentation.screens.invitation_cards

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
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
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTabStyle
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.viewmodels.CardViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

enum class CardsView {
    MAIN,
    EDIT_DETAILS,
    FULL_VIEW,
    LIKED_CARDS,
    ROOM,
    HELP_FEEDBACK
}

enum class CardsTab {
    EXPLORE,
    MY_CARDS
}

private val InitialCardThemes = listOf(
    CardTheme(id = "default_1", name = "Classic Elegance", resId = R.drawable.bg_invitation_card_01, isDefault = true),
    CardTheme(id = "default_2", name = "Floral Romance", resId = R.drawable.bg_invitation_card_02, isDefault = true),
    CardTheme(id = "default_3", name = "Golden Glamour", resId = R.drawable.bg_invitation_card_03, isDefault = true),
    CardTheme(id = "default_4", name = "Modern Minimalist", resId = R.drawable.bg_invitation_card_04, isDefault = true),
    CardTheme(id = "default_5", name = "Vintage Botanical", resId = R.drawable.bg_invitation_card_05, isDefault = true),
    CardTheme(id = "default_6", name = "Divine Blessings", resId = R.drawable.bg_invitation_card_06, isDefault = true),
    CardTheme(id = "default_7", name = "Royal Union", resId = R.drawable.bg_invitation_card_07, isDefault = true),
    CardTheme(id = "default_8", name = "Regal Heritage", resId = R.drawable.bg_invitation_card_08, isDefault = true)
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardsScreen(
    onBackClick: () -> Unit = {},
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    cardViewModel: CardViewModel = hiltViewModel(),
    profileViewModel: com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel = hiltViewModel()
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val myCards by cardViewModel.myCards.collectAsStateWithLifecycle()
    val likedCards by cardViewModel.likedCards.collectAsStateWithLifecycle()
    val jasnifyCards by cardViewModel.jasnifyCards.collectAsStateWithLifecycle()
    val cardRoomData by cardViewModel.cardRoomData.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUserUid = remember(auth.currentUser) { auth.currentUser?.uid.orEmpty() }

    val currentUserRole = remember(activeEvent, roomUsers, currentUserUid) {
        val isOwner = activeEvent?.ownerId == currentUserUid
        val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
        when {
            isOwner -> UserRole.OWNER
            currentUserInRoom != null -> currentUserInRoom.role
            else -> UserRole.VIEWER
        }
    }
    val isOwner = currentUserRole == UserRole.OWNER
    val isViewer = currentUserRole == UserRole.VIEWER
    val canEdit = !isViewer

    var currentView by remember { mutableStateOf(CardsView.MAIN) }
    var previousView by remember { mutableStateOf<CardsView?>(null) }
    var selectedCard by remember { mutableStateOf<CardData?>(null) }
    var activeTransitionKey by remember { mutableStateOf<String?>(null) }
    var editingCard by remember { mutableStateOf<CardData?>(null) }
    var showAiChat by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(CardsTab.EXPLORE) }

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

    var selectedCardIds by remember { mutableStateOf(emptySet<String>()) }

    val isAnySheetVisible by remember {
        derivedStateOf {
            showMenuSheet || showRoomMenuBottomSheet || userToRemove != null || showLeaveConfirmation || showDeleteConfirmation
        }
    }

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
                val canScroll = if (selectedTab == CardsTab.EXPLORE) {
                    exploreLazyListState.canScrollForward || exploreLazyListState.canScrollBackward
                } else {
                    myCardsGridState.canScrollForward || myCardsGridState.canScrollBackward
                }

                if (!canScroll) {
                    isBottomTabVisible = true
                    return Offset.Zero
                }

                if (delta > 0) {
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) {
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
            cardViewModel.initializeRoom(InitialCardThemes)
        }
    }

    LaunchedEffect(activeEventId) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val currentEventId = activeEventId
        if (currentEventId != null) {
            roomViewModel.verifyAccess(currentEventId, "Cards", uid)
            roomViewModel.loadRoomUsers(currentEventId, "Cards")
            cardViewModel.setEventId(currentEventId)
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
            showAiChat -> showAiChat = false
            showDeleteConfirmation -> showDeleteConfirmation = false
            showMenuSheet -> showMenuSheet = false
            showRoomMenuBottomSheet -> showRoomMenuBottomSheet = false
            userToRemove != null -> userToRemove = null
            showLeaveConfirmation -> showLeaveConfirmation = false
            selectedCardIds.isNotEmpty() -> selectedCardIds = emptySet()
            currentView == CardsView.HELP_FEEDBACK -> currentView = CardsView.MAIN
            currentView == CardsView.LIKED_CARDS -> currentView = CardsView.MAIN
            currentView == CardsView.FULL_VIEW -> {
                currentView = previousView ?: CardsView.MAIN
                previousView = null
            }
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
                                    jasnifyCards = jasnifyCards,
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
                                        previousView = CardsView.MAIN
                                        currentView = CardsView.FULL_VIEW
                                    },
                                    onLikeToggle = { card -> 
                                        val isJasnify = jasnifyCards.any { it.id == card.id }
                                        cardViewModel.toggleLikedCard(card, isJasnify) 
                                    },
                                    onShareIncrement = { card ->
                                        val isJasnify = jasnifyCards.any { it.id == card.id }
                                        cardViewModel.incrementCardShare(card, isJasnify)
                                    },
                                    onEditDetailsClick = { card ->
                                        editingCard = if (card.id.startsWith("template_") || jasnifyCards.any { it.id == card.id }) {
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
                                        onBackClick = {
                                            currentView = previousView ?: CardsView.MAIN
                                            previousView = null
                                        },
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
                                        previousView = CardsView.LIKED_CARDS
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
                                        currentUserRole = currentUserRole,
                                        onBackClick = { currentView = CardsView.MAIN },
                                        onMenuClick = { showRoomMenuBottomSheet = true },
                                        onRemove = { userToRemove = it },
                                        onLeave = { showLeaveConfirmation = true },
                                        onShowToast = { toastData = it }
                                    )
                                }
                            }

                            CardsView.HELP_FEEDBACK -> {
                                com.harshdeep.jasnify.presentation.screens.main.tabs.profile.HelpFeedbackScreen(
                                    profileViewModel = profileViewModel,
                                    onBack = { currentView = CardsView.MAIN },
                                    onShowAiChat = { showAiChat = true }
                                )
                            }
                        }
                    }
                }

                val bottomTabItems = listOf(
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
                )

                AnimatedVisibility(
                    visible = currentView == CardsView.MAIN && selectedCardIds.isEmpty() && isBottomTabVisible,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight * 2 },
                        animationSpec = spring(
                            dampingRatio = 0.85f,
                            stiffness = 380f
                        )
                    ) + fadeIn(animationSpec = tween(durationMillis = 200)),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight * 2 },
                        animationSpec = spring(
                            dampingRatio = 0.85f,
                            stiffness = 380f
                        )
                    ) + fadeOut(animationSpec = tween(durationMillis = 180)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(10f)
                ) {
                    BottomTab(
                        items = bottomTabItems,
                        selectedValue = selectedTab,
                        onItemSelected = { selectedTab = it },
                        style = BottomTabStyle.FLOATING,
                        activeColor = ContentPrimary,
                        activeBg = SurfaceSecondary
                    )
                }
            }
        }

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
            val menuItems = listOf(
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
                        text = if (isOwner) "Manage Room Access" else "Room Members",
                        icon = painterResource(R.drawable.ic_user_default),
                        onClick = {
                            showMenuSheet = false
                            currentView = CardsView.ROOM
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = painterResource(R.drawable.ic_help_feedback),
                        onClick = {
                            showMenuSheet = false
                            currentView = CardsView.HELP_FEEDBACK
                        }
                    )
                )
            )

            MenuBottomSheet(
                items = menuItems,
                onCancelClick = { showMenuSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showRoomMenuBottomSheet) {
            val roomMenuItems = listOf(
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
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = painterResource(R.drawable.ic_help_feedback),
                        onClick = {
                            showRoomMenuBottomSheet = false
                            currentView = CardsView.HELP_FEEDBACK
                        }
                    )
                )
            )

            MenuBottomSheet(
                items = roomMenuItems,
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

        userToRemove?.let { targetUser ->
            ConfirmationBottomSheet(
                heading = "Remove ${targetUser.name}?",
                subHeading = "They will not be able to access this room anymore.",
                confirmButtonText = "Remove",
                onDismiss = { userToRemove = null },
                onConfirm = {
                    val eventId = activeEventId
                    if (eventId != null) {
                        roomViewModel.removeAccess(eventId, "Cards", targetUser.uid)
                        toastData = ToastData("${targetUser.name} removed", ToastType.ERROR)
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
                        roomViewModel.removeAccess(eventId, "Cards", FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                    }
                    toastData = ToastData("You left the room", ToastType.DEFAULT)
                    currentView = CardsView.MAIN
                    showLeaveConfirmation = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        AnimatedVisibility(
            visible = showAiChat,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.zIndex(200f)
        ) {
            com.harshdeep.jasnify.presentation.screens.others.AiChatScreen(
                eventId = activeEventId,
                shouldStartNewSession = true,
                onBackClick = { showAiChat = false }
            )
        }
    }
}