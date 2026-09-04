package com.harshdeep.jasnify.presentation.screens.invitation_cards

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTabStyle
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.components.states.CardsLoadingState
import com.harshdeep.jasnify.presentation.screens.chats.AiChatScreen
import com.harshdeep.jasnify.presentation.screens.chats.GroupChatScreen
import com.harshdeep.jasnify.presentation.viewmodels.CardViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardsScreen(
    onBackClick: () -> Unit = {},
    navController: androidx.navigation.NavHostController? = null,
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    cardViewModel: CardViewModel = hiltViewModel(),
    profileViewModel: com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel = hiltViewModel()
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val isLoading by cardViewModel.isLoading.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val myCards by cardViewModel.myCards.collectAsStateWithLifecycle()
    val likedCards by cardViewModel.likedCards.collectAsStateWithLifecycle()
    val jasnifyCards by cardViewModel.jasnifyCards.collectAsStateWithLifecycle()
    val availableStyles by cardViewModel.availableStyles.collectAsStateWithLifecycle()
    val selectedStyle by cardViewModel.selectedStyle.collectAsStateWithLifecycle()
    val globalCardThemes by cardViewModel.globalCardThemes.collectAsStateWithLifecycle()
    val isCardAdmin by cardViewModel.isCardAdmin.collectAsStateWithLifecycle()
    val adminDetails by cardViewModel.adminDetails.collectAsStateWithLifecycle()
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
    var selectedCardId by remember { mutableStateOf<String?>(null) }
    
    val liveSelectedCard by remember(selectedCardId, jasnifyCards, myCards, likedCards) {
        derivedStateOf {
            selectedCardId?.let { id ->
                jasnifyCards.find { it.id == id } 
                    ?: myCards.find { it.id == id }
                    ?: likedCards.find { it.id == id }
            }
        }
    }

    var activeTransitionKey by remember { mutableStateOf<String?>(null) }
    var editingCard by remember { mutableStateOf<CardData?>(null) }
    var showAiChat by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(CardsTab.EXPLORE) }

    val exploreLazyListState = rememberLazyListState()
    val myCardsGridState = rememberLazyGridState()
    val explorePagerState = rememberPagerState(
        initialPage = (Int.MAX_VALUE / 2),
        pageCount = { Int.MAX_VALUE }
    )

    var showMenuSheet by remember { mutableStateOf(false) }
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showMetadataSheet by remember { mutableStateOf(false) }
    var cardForMetadata by remember { mutableStateOf<CardData?>(null) }
    var toastData by remember { mutableStateOf(ToastData()) }
    var sheetMotionProgress by remember { mutableFloatStateOf(1.0f) }

    var selectedCardIds by remember { mutableStateOf(emptySet<String>()) }
    var isEntering by remember { mutableStateOf(true) }

    val isAnySheetVisible by remember {
        derivedStateOf {
            showMenuSheet || showRoomMenuBottomSheet || userToRemove != null || showLeaveConfirmation || showDeleteConfirmation || showMetadataSheet
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

    LaunchedEffect(Unit) {
        delay(400.milliseconds)
        isEntering = false
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
            showMetadataSheet -> showMetadataSheet = false
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
            currentView == CardsView.ROOM -> currentView = CardsView.GROUP_CHAT
            currentView == CardsView.GROUP_CHAT -> currentView = CardsView.MAIN
            currentView != CardsView.MAIN -> currentView = CardsView.MAIN
            else -> onBackClick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (isLoading || isEntering) {
            CardsLoadingState()
        } else {
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
                                        onTabSelected = remember {
                                            {
                                                selectedTab = it
                                                selectedCardIds = emptySet()
                                            }
                                        },
                                        myCards = myCards,
                                        likedCards = likedCards,
                                        jasnifyCards = jasnifyCards,
                                        availableStyles = availableStyles,
                                        selectedStyle = selectedStyle,
                                        onStyleClick = { cardViewModel.setSelectedStyle(it) },
                                        activeEvent = activeEvent,
                                        selectedCardIds = selectedCardIds,
                                        exploreLazyListState = exploreLazyListState,
                                        myCardsGridState = myCardsGridState,
                                        explorePagerState = explorePagerState,
                                        animatedVisibilityScope = this@AnimatedContent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        canEdit = canEdit,
                                        isCardAdmin = isCardAdmin,
                                        nestedScrollConnection = nestedScrollConnection,
                                        onToggleCardSelection = remember {
                                            { id ->
                                                selectedCardIds = if (selectedCardIds.contains(id)) {
                                                    selectedCardIds - id
                                                } else {
                                                    selectedCardIds + id
                                                }
                                            }
                                        },
                                        onBackClick = remember {
                                            {
                                                if (selectedCardIds.isNotEmpty()) {
                                                    selectedCardIds = emptySet()
                                                } else {
                                                    onBackClick()
                                                }
                                            }
                                        },
                                        onMenuClick = remember(selectedTab, selectedCardIds) {
                                            {
                                                if (selectedCardIds.isNotEmpty()) {
                                                    showDeleteConfirmation = true
                                                } else if (selectedTab == CardsTab.EXPLORE) {
                                                    showMenuSheet = true
                                                } else {
                                                    currentView = CardsView.LIKED_CARDS
                                                }
                                            }
                                        },
                                        onChatClick = {
                                            currentView = CardsView.GROUP_CHAT
                                        },
                                        onCardClick = remember {
                                            { card, transitionKey ->
                                                selectedCardId = card.id
                                                activeTransitionKey = transitionKey
                                                previousView = CardsView.MAIN
                                                currentView = CardsView.FULL_VIEW
                                            }
                                        },
                                        onLikeToggle = remember {
                                            { card ->
                                                val isJasnify = jasnifyCards.any { it.id == card.id }
                                                cardViewModel.toggleLikedCard(card, isJasnify)
                                            }
                                        },
                                        onShareIncrement = remember {
                                            { card ->
                                                val isJasnify = jasnifyCards.any { it.id == card.id }
                                                cardViewModel.incrementCardShare(card, isJasnify)
                                            }
                                        },
                                        onEditDetailsClick = remember(isCardAdmin) {
                                            { card ->
                                                val isGlobalCard = jasnifyCards.any { it.id == card.id } || card.id.startsWith("template_")
                                                editingCard = if (isGlobalCard && !isCardAdmin) {
                                                    card.copy(id = UUID.randomUUID().toString())
                                                } else {
                                                    card
                                                }
                                                currentView = CardsView.EDIT_DETAILS
                                            }
                                        },
                                        onAddNewClick = remember {
                                            {
                                                editingCard = CardData(id = UUID.randomUUID().toString())
                                                currentView = CardsView.EDIT_DETAILS
                                            }
                                        },
                                        onPublishSelectedToJasnify = remember {
                                            {
                                                val cardsToPublish = myCards.filter { selectedCardIds.contains(it.id) }
                                                cardViewModel.publishCardsToJasnify(cardsToPublish) {
                                                    val count = cardsToPublish.size
                                                    selectedCardIds = emptySet()
                                                    toastData = ToastData(
                                                        message = if (count == 1) "1 card published to Explore" else "$count cards published to Explore",
                                                        type = ToastType.DEFAULT
                                                    )
                                                }
                                            }
                                        },
                                        onMetadataClick = { card ->
                                            cardForMetadata = card
                                            showMetadataSheet = true
                                        }
                                    )
                                }
                                CardsView.FULL_VIEW -> {
                                    liveSelectedCard?.let { card ->
                                        CardFullView(
                                            card = card,
                                            transitionKey = activeTransitionKey ?: "card_${card.id}",
                                            animatedVisibilityScope = this@AnimatedContent,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            canEdit = canEdit,
                                            onBackClick = remember {
                                                {
                                                    currentView = previousView ?: CardsView.MAIN
                                                    previousView = null
                                                }
                                            },
                                            onEditDetailsClick = remember(card.id, isCardAdmin) {
                                                {
                                                    val isGlobalCard = jasnifyCards.any { it.id == card.id } || card.id.startsWith("template_")
                                                    editingCard = if (isGlobalCard && !isCardAdmin) {
                                                        card.copy(id = UUID.randomUUID().toString())
                                                    } else {
                                                        card
                                                    }
                                                    currentView = CardsView.EDIT_DETAILS
                                                }
                                            },
                                            onLikeToggle = { cardViewModel.toggleLikedCard(it, jasnifyCards.any { c -> c.id == it.id }) },
                                            onShareIncrement = { cardViewModel.incrementCardShare(it, jasnifyCards.any { c -> c.id == it.id }) }
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
                                            selectedCardId = card.id
                                            activeTransitionKey = transitionKey
                                            previousView = CardsView.LIKED_CARDS
                                            currentView = CardsView.FULL_VIEW
                                        },
                                        onLikeToggle = { card ->
                                            val isJasnify = jasnifyCards.any { it.id == card.id }
                                            cardViewModel.toggleLikedCard(card, isJasnify)
                                        },
                                        onShareIncrement = { card ->
                                            val isJasnify = jasnifyCards.any { it.id == card.id }
                                            cardViewModel.incrementCardShare(card, isJasnify)
                                        }
                                    )
                                }
                                CardsView.EDIT_DETAILS -> {
                                    editingCard?.let { card ->
                                        EditCardDetailsScreen(
                                            initialData = card,
                                            cardRoomData = cardRoomData,
                                            globalCardThemes = globalCardThemes,
                                            onDataChange = { updated ->
                                                // ALWAYS save to user's event collection so it shows in "My Edits"
                                                cardViewModel.saveMyCard(updated)

                                                selectedCardId = updated.id
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
                                            onBackClick = { currentView = CardsView.GROUP_CHAT },
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
                                CardsView.GROUP_CHAT -> {
                                    activeEventId?.let { id ->
                                        com.harshdeep.jasnify.presentation.screens.chats.GroupChatScreen(
                                            eventId = id,
                                            roomType = "Cards",
                                            onBackClick = { currentView = CardsView.MAIN },
                                            onMembersClick = { currentView = CardsView.ROOM }
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
                            val currentAdminUsername = (adminDetails?.get("username") as? String).orEmpty()
                            var unauthorizedAttempt = false
                            var totalDeleted = 0
                            
                            selectedCardIds.forEach { id ->
                                val globalCard = jasnifyCards.find { it.id == id }
                                val isGlobal = globalCard != null
                                
                                if (selectedTab == CardsTab.MY_CARDS) {
                                    if (isGlobal) {
                                        // This is a global card being managed from My Edits
                                        if (isCardAdmin && globalCard.adminUsername == currentAdminUsername) {
                                            cardViewModel.deleteJasnifyCard(id)
                                            cardViewModel.deleteMyCard(id)
                                            totalDeleted++
                                        } else {
                                            unauthorizedAttempt = true
                                        }
                                    } else {
                                        // Just a regular personal card
                                        cardViewModel.deleteMyCard(id)
                                        totalDeleted++
                                    }
                                } else if (selectedTab == CardsTab.EXPLORE) {
                                    // Specifically trying to delete from the global collection
                                    if (isCardAdmin && globalCard?.adminUsername == currentAdminUsername) {
                                        cardViewModel.deleteJasnifyCard(id)
                                        // Also clean up the local version if it exists
                                        cardViewModel.deleteMyCard(id)
                                        totalDeleted++
                                    } else {
                                        unauthorizedAttempt = true
                                    }
                                }
                            }

                            if (unauthorizedAttempt) {
                                toastData = ToastData(
                                    message = if (selectedCardIds.size == 1) "You can't delete this card" else "Some cards couldn't be deleted",
                                    type = ToastType.ERROR
                                )
                            } else if (totalDeleted > 0) {
                                toastData = ToastData(
                                    message = if (totalDeleted == 1) "Card deleted" else "$totalDeleted cards deleted",
                                    type = ToastType.DEFAULT
                                )
                            }

                            selectedCardIds = emptySet()
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
                                roomViewModel.removeAccess(
                                    eventId,
                                    "Cards",
                                    FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                                )
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
                    AiChatScreen(
                        eventId = activeEventId,
                        initialContext = "Cards & Invitation Cards",
                        shouldStartNewSession = true,
                        onBackClick = { showAiChat = false }
                    )
                }

                if (showMetadataSheet && cardForMetadata != null) {
                    CustomBottomSheet(
                        heading = "Edit Card Metadata",
                        onDismiss = { showMetadataSheet = false },
                        sheetHeight = null,
                        onProgress = { sheetMotionProgress = it }
                    ) {
                        var eventType by remember { mutableStateOf(cardForMetadata!!.eventType) }
                        var cardStyle by remember { mutableStateOf(cardForMetadata!!.cardStyle) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Event Type",
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentPrimary
                                )
                                PrimaryInput(
                                    value = eventType,
                                    onValueChange = { eventType = it },
                                    placeholder = "e.g. Wedding, Birthday"
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Card Style",
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentPrimary
                                )
                                PrimaryInput(
                                    value = cardStyle,
                                    onValueChange = { cardStyle = it },
                                    placeholder = "e.g. Classic, Modern"
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            CustomTextButton(
                                text = "Update Metadata",
                                onClick = {
                                    val updated = cardForMetadata!!.copy(
                                        eventType = eventType,
                                        cardStyle = cardStyle
                                    )
                                    cardViewModel.saveMyCard(updated)
                                    showMetadataSheet = false
                                    toastData =
                                        ToastData("Metadata updated locally", ToastType.SUCCESS)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shapeStyle = ButtonShapeStyle.Square
                            )
                        }
                    }
                }
            }
        }
    }
}
