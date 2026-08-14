package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
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
    ROOM
}

enum class CardsTab {
    EXPLORE,
    SAVED
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
    val cloudCardData by cardViewModel.cardData.collectAsStateWithLifecycle()

    var currentView by remember { mutableStateOf(CardsView.MAIN) }
    var cardData by remember { mutableStateOf(CardData()) }
    var isInitialized by remember { mutableStateOf(false) }

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

    // Sync state with cloud data or initialize from active event
    LaunchedEffect(cloudCardData) {
        cloudCardData?.let {
            cardData = it
            isInitialized = true
        }
    }

    LaunchedEffect(activeEvent) {
        if (!isInitialized) {
            activeEvent?.let { event ->
                val date = event.date?.let { Date(it) } ?: Date()
                val day = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
                val dayOfMonth = SimpleDateFormat("dd", Locale.getDefault()).format(date)
                val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase()
                val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
                val formattedDateString = "$day • $dayOfMonth $month • $year"

                val updatedElements = cardData.elements.mapIndexed { index, element ->
                    when (index) {
                        1 -> element.copy(text = event.name.ifBlank { element.text })
                        3 -> element.copy(text = formattedDateString)
                        else -> element
                    }
                }
                cardData = cardData.copy(elements = updatedElements)
            }
        }
    }

    BackHandler {
        when {
            showMenuSheet -> showMenuSheet = false
            showRoomMenuBottomSheet -> showRoomMenuBottomSheet = false
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
                                cardData = cardData,
                                onBackClick = onBackClick,
                                onMenuClick = { showMenuSheet = true },
                                onEditDetailsClick = { currentView = CardsView.EDIT_DETAILS }
                            )
                        }
                        CardsView.EDIT_DETAILS -> {
                            EditCardDetailsScreen(
                                initialData = cardData,
                                onDataChange = {
                                    cardData = it
                                    cardViewModel.saveCardData(it)
                                },
                                onBackClick = { currentView = CardsView.MAIN }
                            )
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
                            text = "Edit Details",
                            icon = painterResource(R.drawable.ic_edit),
                            onClick = {
                                showMenuSheet = false
                                currentView = CardsView.EDIT_DETAILS
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
    cardData: CardData,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onEditDetailsClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    var selectedTab by remember { mutableStateOf(CardsTab.EXPLORE) }

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

    var cardToCapture by remember { mutableStateOf<CardData?>(null) }
    var likedCardRes by remember { mutableStateOf(setOf<Int>()) }

    val onLikeToggle = { resId: Int ->
        likedCardRes = if (likedCardRes.contains(resId)) {
            likedCardRes - resId
        } else {
            likedCardRes + resId
        }
    }

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
            val currentCarouselCard = cardData.copy(
                backgroundRes = backgrounds[pagerState.currentPage % backgrounds.size]
            )
            CardItem(
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
                        onMenuClick = onMenuClick,
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
                    label = "CardsTabContent"
                ) { tab ->
                    when (tab) {
                        CardsTab.EXPLORE -> {
                            ExploreCardsContent(
                                cardData = cardData,
                                backgrounds = backgrounds,
                                likedCardRes = likedCardRes,
                                onLikeToggle = onLikeToggle,
                                pagerState = pagerState,
                                onShareTrigger = onShareTrigger,
                                onEditDetailsClick = onEditDetailsClick
                            )
                        }
                        CardsTab.SAVED -> {
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
                    value = CardsTab.EXPLORE,
                    icon = painterResource(R.drawable.ic_notes)
                ),
                TabItem(
                    label = "Saved",
                    value = CardsTab.SAVED,
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
    cardData: CardData,
    backgrounds: List<Int>,
    likedCardRes: Set<Int>,
    onLikeToggle: (Int) -> Unit,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onShareTrigger: (CardData, Boolean) -> Unit,
    onEditDetailsClick: () -> Unit
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
                CardCarousel(
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
            CardItem(
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
    cardData: CardData,
    backgrounds: List<Int>,
    likedCardRes: Set<Int>,
    onLikeToggle: (Int) -> Unit,
    onShareClick: (CardData) -> Unit
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
                CardItem(
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
