package com.harshdeep.jasnify.presentation.screens.catering

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.models.eventTypes
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomSuccessBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.RoomAccessBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.chip.CateringItemChip
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.chip.FoodChip
import com.harshdeep.jasnify.presentation.components.filter.FilterBottomSheet
import com.harshdeep.jasnify.presentation.components.filter.FilterFoodTypeBottomSheet
import com.harshdeep.jasnify.presentation.components.filter.FoodTypeOption
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.states.SkeletonMenuCategoryCard
import com.harshdeep.jasnify.presentation.components.states.shimmerBrush
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.presentation.viewmodels.CateringViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

private val DEFAULT_CUISINES = listOf("Indian", "Japanese", "Mexican", "Italian", "Chinese", "French", "Thai", "Korean")
private val DEFAULT_TYPES = listOf("Starters", "Beverages", "Main Course", "Desserts")

private val TopHeaderGradientBrush = Brush.verticalGradient(
    0.0f to Color(0xFFCCE3CB),
    0.75f to Color(0xFFE1EFE0),
    1.0f to BackgroundPrimary
)

private val StickyHeaderSolidBrush = Brush.verticalGradient(
    0.0f to Color(0xFFD8EBD7),
    0.45f to Color(0xFFE3F0E2),
    1.0f to Color(0xFFF0F7EF)
)

private val CardTranslucentGradientBrush = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.25f)
    )
)

private val CardSquircleShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
private val CategoryCardShape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault)

@Immutable
data class MenuItem(
    val id: String,
    val name: String,
    val dietary: Dietary,
    val type: String,
    val cuisine: String = "Indian"
)

enum class CateringMenuView {
    MENU,
    MANAGE_ROOM_ACCESS
}

fun Modifier.detectCombinedClicks(
    key: Any,
    onTap: () -> Unit,
    onLongPress: () -> Unit
): Modifier = this.pointerInput(key) {
    awaitEachGesture {
        val down = awaitFirstDown(pass = PointerEventPass.Initial)
        val longPressTimeout = viewConfiguration.longPressTimeoutMillis

        try {
            withTimeout(longPressTimeout) {
                val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                if (up != null) {
                    onTap()
                }
            }
        } catch (_: PointerEventTimeoutCancellationException) {
            onLongPress()
        }
    }
}

@Immutable
data class CategoryStyle(
    val containerBrush: Brush,
    val sheetBrush: Brush,
    val headerTextColor: Color,
    val subtitleTextColor: Color,
    val illustrationRes: Int
)

fun createCategoryGradient(colors: List<Color>): Brush {
    if (colors.isEmpty()) return SolidColor(Color.Transparent)
    if (colors.size == 1) return SolidColor(colors.first())

    val colorStops = when (colors.size) {
        2 -> arrayOf(
            0.0f to colors[0],
            1.0f to colors[1]
        )
        3 -> arrayOf(
            0.0f to colors[0],
            0.68f to colors[1],
            1.0f to colors[2]
        )
        else -> colors.mapIndexed { index, color ->
            val stop = if (index == colors.lastIndex) {
                1.0f
            } else {
                (index.toFloat() / (colors.size - 1)) * 0.68f
            }
            stop to color
        }.toTypedArray()
    }

    return Brush.linearGradient(
        colorStops = colorStops,
        start = Offset.Zero,
        end = Offset.Infinite
    )
}

private val AppetizerCategoryStyle = CategoryStyle(
    containerBrush = createCategoryGradient(listOf(Color(0xFFFFAF90), Color(0xFFFED8CA), Color(0xFFFFF2EC))),
    sheetBrush = createCategoryGradient(listOf(Color(0xFFFFC9B8), Color(0xFFFFE8E0), Color(0xFFFFFFFF))),
    headerTextColor = Color(0xFF621E09),
    subtitleTextColor = Color(0xFF621E09),
    illustrationRes = R.drawable.ill_appetizers
)

private val BeverageCategoryStyle = CategoryStyle(
    containerBrush = createCategoryGradient(listOf(Color(0xFF91DBFF), Color(0xFFBAEAFF), Color(0xFFEBF7FD))),
    sheetBrush = createCategoryGradient(listOf(Color(0xFFB9E5FA), Color(0xFFE3F5FD), Color(0xFFFFFFFF))),
    headerTextColor = Color(0xFF0A405F),
    subtitleTextColor = Color(0xFF0A405F),
    illustrationRes = R.drawable.ill_beverages
)

private val MainCourseCategoryStyle = CategoryStyle(
    containerBrush = createCategoryGradient(listOf(Color(0xFFFF9E99), Color(0xFFFCDAD7), Color(0xFFFFF3F2))),
    sheetBrush = createCategoryGradient(listOf(Color(0xFFFFC2BF), Color(0xFFFFE7E5), Color(0xFFFFFFFF))),
    headerTextColor = Color(0xFF6B1515),
    subtitleTextColor = Color(0xFF6B1515),
    illustrationRes = R.drawable.ill_main_courses
)

private val DessertCategoryStyle = CategoryStyle(
    containerBrush = createCategoryGradient(listOf(Color(0xFFFFA9D5), Color(0xFFFBDBEC), Color(0xFFFFF3FA))),
    sheetBrush = createCategoryGradient(listOf(Color(0xFFFFC8E2), Color(0xFFFFE9F4), Color(0xFFFFFFFF))),
    headerTextColor = Color(0xFF631034),
    subtitleTextColor = Color(0xFF631034),
    illustrationRes = R.drawable.ill_desserts
)

@Stable
fun getCategoryStyle(categoryName: String): CategoryStyle {
    val normalized = categoryName.lowercase().trim()
    return when {
        normalized.contains("appetizer") || normalized.contains("starter") -> AppetizerCategoryStyle
        normalized.contains("beverage") || normalized.contains("drink") -> BeverageCategoryStyle
        normalized.contains("main") -> MainCourseCategoryStyle
        normalized.contains("dessert") || normalized.contains("sweet") -> DessertCategoryStyle
        else -> AppetizerCategoryStyle
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CateringMenuScreen(
    onBackClick: () -> Unit,
    cateringViewModel: CateringViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val cateringItemsEntities by cateringViewModel.cateringItems.collectAsStateWithLifecycle()
    val isLoading by cateringViewModel.isLoading.collectAsStateWithLifecycle()
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()

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

    val allMenuItems = remember(cateringItemsEntities) {
        cateringItemsEntities.map { entity ->
            MenuItem(
                id = entity.id,
                name = entity.name,
                dietary = entity.dietary,
                type = entity.type,
                cuisine = entity.cuisine
            )
        }
    }

    LaunchedEffect(Unit) {
        roomViewModel.resetAccessState()
        eventViewModel.fetchUserEvents()
    }

    LaunchedEffect(activeEventId) {
        val id = activeEventId
        if (id != null) {
            cateringViewModel.setEventId(id)
            roomViewModel.verifyAccess(id, "Catering", currentUserUid)
            roomViewModel.loadRoomUsers(id, "Catering")
        } else {
            roomViewModel.setAccessState(true)
        }
    }

    LaunchedEffect(activeEvent) {
        activeEvent?.let { event ->
            val eventTypeLabel = eventTypes.find { it.id == event.typeId }?.label ?: "Others"
            cateringViewModel.seedDefaultMenu(eventTypeLabel, event.id)
        }
    }

    var searchText by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var isMultiSelectActive by remember { mutableStateOf(false) }
    var selectedItemIds by remember { mutableStateOf(emptySet<String>()) }
    val isSelectionMode = isMultiSelectActive || selectedItemIds.isNotEmpty()

    val mainListState = rememberLazyListState()

    LaunchedEffect(isSearchActive) {
        if (!isSearchActive && (mainListState.firstVisibleItemIndex > 0 || mainListState.firstVisibleItemScrollOffset > 0)) {
            mainListState.animateScrollToItem(0)
        }
    }

    BackHandler(enabled = isSelectionMode) {
        selectedItemIds = emptySet()
        isMultiSelectActive = false
        focusManager.clearFocus()
    }

    BackHandler(enabled = isSearchActive && !isSelectionMode) {
        isSearchActive = false
        searchText = ""
        focusManager.clearFocus()
        coroutineScope.launch {
            mainListState.animateScrollToItem(0)
        }
    }

    var currentView by remember { mutableStateOf(CateringMenuView.MENU) }

    BackHandler(enabled = currentView != CateringMenuView.MENU) {
        currentView = CateringMenuView.MENU
    }

    var selectedFilterTab by remember { mutableStateOf("All Items") }

    var toastData by remember { mutableStateOf(ToastData()) }
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(2000L.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    var selectedItemForDetails by remember { mutableStateOf<MenuItem?>(null) }
    var showDetailsBottomSheet by remember { mutableStateOf(false) }

    var showDeleteConfirmationSheet by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<MenuItem?>(null) }

    var showCuisineBottomSheet by remember { mutableStateOf(false) }
    var showTypeBottomSheet by remember { mutableStateOf(false) }

    var showAddItemSheet by remember { mutableStateOf(false) }
    var showSuccessSheet by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    var editingItem by remember { mutableStateOf<MenuItem?>(null) }

    var newItemName by remember { mutableStateOf("") }
    var newItemCuisine by remember { mutableStateOf("Indian") }
    var newItemType by remember { mutableStateOf("Starters") }
    var newItemDietary by remember { mutableStateOf(Dietary.Veg) }

    var showAddCuisineBottomSheet by remember { mutableStateOf(false) }
    var showAddTypeBottomSheet by remember { mutableStateOf(false) }

    var selectedCuisines by remember { mutableStateOf(emptySet<String>()) }
    var selectedTypes by remember { mutableStateOf(emptySet<String>()) }

    var showMenuBottomSheet by remember { mutableStateOf(false) }
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showRoomAccessBottomSheet by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }

    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    val topBarMaxScrollPx = remember(density) { with(density) { 56.dp.toPx() } }

    val isAnyBottomSheetOpen = showDetailsBottomSheet ||
            showDeleteConfirmationSheet ||
            showCuisineBottomSheet ||
            showTypeBottomSheet ||
            showAddItemSheet ||
            showSuccessSheet ||
            showAddCuisineBottomSheet ||
            showAddTypeBottomSheet ||
            showMenuBottomSheet ||
            showRoomMenuBottomSheet ||
            showRoomAccessBottomSheet ||
            userToRemove != null ||
            showLeaveConfirmation

    val targetScale = if (isAnyBottomSheetOpen) {
        0.92f + (0.08f * sheetMotionProgress)
    } else {
        1.0f
    }

    val backdropScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadius by animateDpAsState(
        targetValue = if (isAnyBottomSheetOpen) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    val cuisineOptions = remember(allMenuItems) {
        (DEFAULT_CUISINES + allMenuItems.map { it.cuisine }).distinct().sorted()
    }

    val typeOptions = remember(allMenuItems) {
        (DEFAULT_TYPES + allMenuItems.map { it.type }).distinct().sorted()
    }

    val foodTypeOptions = remember(typeOptions, allMenuItems) {
        typeOptions.map { typeName ->
            FoodTypeOption(
                name = typeName,
                count = allMenuItems.count { it.type.equals(typeName, ignoreCase = true) }
            )
        }
    }

    val filteredItems = remember(allMenuItems, searchText, selectedFilterTab, selectedCuisines, selectedTypes) {
        val query = searchText.trim()
        val hasQuery = query.isNotEmpty()
        val hasCuisineFilter = selectedCuisines.isNotEmpty()
        val hasTypeFilter = selectedTypes.isNotEmpty()

        allMenuItems.filter { item ->
            val matchesSearch = !hasQuery ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.type.contains(query, ignoreCase = true) ||
                    item.cuisine.contains(query, ignoreCase = true)

            val matchesTab = when (selectedFilterTab) {
                "Veg" -> item.dietary == Dietary.Veg
                "Non-Veg" -> item.dietary == Dietary.NonVeg
                else -> true
            }

            val matchesCuisine = !hasCuisineFilter || selectedCuisines.contains(item.cuisine)
            val matchesType = !hasTypeFilter || selectedTypes.contains(item.type)

            matchesSearch && matchesTab && matchesCuisine && matchesType
        }
    }

    val categorizedItems = remember(filteredItems) {
        filteredItems.groupBy { it.type }
    }

    RoomAccessGuardian(
        hasAccess = hasAccess,
        roomName = "Catering",
        onBackClick = onBackClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = backdropScale
                        scaleY = backdropScale
                        clip = isAnyBottomSheetOpen || backdropCornerRadius > 0.dp
                        shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                    }
            ) {
                AnimatedContent(
                    targetState = currentView,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    label = "CateringMenuTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        CateringMenuView.MENU -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(BackgroundPrimary)
                                    .pointerInput(Unit) {
                                        detectTapGestures(onTap = { focusManager.clearFocus() })
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .graphicsLayer {
                                            val progress = if (isSearchActive) {
                                                0f
                                            } else if (mainListState.firstVisibleItemIndex > 0) {
                                                1f
                                            } else {
                                                (mainListState.firstVisibleItemScrollOffset / topBarMaxScrollPx).coerceIn(0f, 1f)
                                            }
                                            translationY = -progress * topBarMaxScrollPx
                                        }
                                        .background(brush = TopHeaderGradientBrush)
                                        .zIndex(0f)
                                )

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .statusBarsPadding(),
                                    state = mainListState,
                                ) {
                                    item(key = "top_bar", contentType = "top_bar") {
                                        AnimatedVisibility(
                                            visible = !isSearchActive,
                                            enter = fadeIn(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                )
                                            ) + slideInVertically(
                                                initialOffsetY = { -it / 2 },
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                )
                                            ) + expandVertically(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            ),
                                            exit = fadeOut(
                                                animationSpec = tween(
                                                    durationMillis = 180,
                                                    easing = FastOutSlowInEasing
                                                )
                                            ) + slideOutVertically(
                                                targetOffsetY = { -it / 2 },
                                                animationSpec = tween(
                                                    durationMillis = 180,
                                                    easing = FastOutSlowInEasing
                                                )
                                            ) + shrinkVertically(
                                                animationSpec = tween(
                                                    durationMillis = 180,
                                                    easing = FastOutSlowInEasing
                                                )
                                            )
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Transparent)
                                                    .graphicsLayer {
                                                        val progress = if (isSearchActive) {
                                                            0f
                                                        } else if (mainListState.firstVisibleItemIndex > 0) {
                                                            1f
                                                        } else {
                                                            (mainListState.firstVisibleItemScrollOffset / topBarMaxScrollPx).coerceIn(0f, 1f)
                                                        }
                                                        alpha = (1f - progress).coerceIn(0f, 1f)
                                                        translationY = -progress * 30f
                                                    }
                                            ) {
                                                CustomTopBar(
                                                    title = "Catering Menu",
                                                    onBackClick = {
                                                        focusManager.clearFocus()
                                                        onBackClick()
                                                    },
                                                    onMenuClick = {
                                                        focusManager.clearFocus()
                                                        showMenuBottomSheet = true
                                                    },
                                                    menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                                                    isLargeTitle = true,
                                                    buttonStyle = ButtonBackground.TRANSLUCENT
                                                )
                                            }
                                        }
                                    }

                                    stickyHeader(key = "search_and_filters_header", contentType = "header") {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .drawBehind {
                                                    val alpha = if (isSearchActive) {
                                                        1f
                                                    } else if (mainListState.firstVisibleItemIndex > 0) {
                                                        1f
                                                    } else {
                                                        (mainListState.firstVisibleItemScrollOffset / topBarMaxScrollPx).coerceIn(0f, 1f)
                                                    }
                                                    if (alpha > 0f) {
                                                        drawRect(
                                                            brush = if (alpha >= 1f) {
                                                                StickyHeaderSolidBrush
                                                            } else {
                                                                Brush.verticalGradient(
                                                                    0.0f to Color(0xFFD8EBD7).copy(alpha = alpha),
                                                                    0.45f to Color(0xFFE3F0E2).copy(alpha = alpha),
                                                                    1.0f to Color(0xFFF0F7EF).copy(alpha = alpha)
                                                                )
                                                            }
                                                        )
                                                    }
                                                }
                                                .zIndex(10f)
                                        ) {
                                            Spacer(Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                CustomSearchBar(
                                                    value = searchText,
                                                    onValueChange = { searchText = it },
                                                    onActiveChange = { active ->
                                                        isSearchActive = active
                                                        if (!active) {
                                                            coroutineScope.launch {
                                                                mainListState.animateScrollToItem(0)
                                                            }
                                                        }
                                                    },
                                                    placeholder = "Search Menu",
                                                    modifier = Modifier.weight(1f),
                                                    isTranslucent = true
                                                )

                                                AnimatedVisibility(
                                                    visible = isSelectionMode && !isViewer,
                                                    enter = fadeIn(animationSpec = tween(200)) + expandHorizontally(
                                                        expandFrom = Alignment.Start,
                                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                                    ),
                                                    exit = fadeOut(animationSpec = tween(150)) + shrinkHorizontally(
                                                        shrinkTowards = Alignment.Start,
                                                        animationSpec = tween(150)
                                                    )
                                                ) {
                                                    TopBarIconButton(
                                                        onClick = {
                                                            focusManager.clearFocus()
                                                            showDeleteConfirmationSheet = true
                                                        },
                                                        icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_delete)),
                                                        borderColor = Color(0xFFB5CEB2),
                                                        backgroundStyle = ButtonBackground.TRANSLUCENT,
                                                        size = 56.dp,
                                                        iconSize = 24.dp
                                                    )
                                                }
                                            }

                                            LazyRow(
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                item(key = "all_items_chip", contentType = "chip") {
                                                    FilterChip(
                                                        label = "All Items",
                                                        isSelected = selectedFilterTab == "All Items" && selectedCuisines.isEmpty() && selectedTypes.isEmpty(),
                                                        shapeStyle = ChipShapeStyle.Round,
                                                        hasStroke = true,
                                                        onClick = {
                                                            focusManager.clearFocus()
                                                            selectedFilterTab = "All Items"
                                                            selectedCuisines = emptySet()
                                                            selectedTypes = emptySet()
                                                        },
                                                        isTransparent = true
                                                    )
                                                }
                                                item(key = "veg_chip", contentType = "chip") {
                                                    FoodChip(
                                                        foodType = Dietary.Veg,
                                                        isSelected = selectedFilterTab == "Veg",
                                                        shapeStyle = ChipShapeStyle.Round,
                                                        onClick = {
                                                            focusManager.clearFocus()
                                                            selectedFilterTab = "Veg"
                                                        },
                                                        isTransparent = true
                                                    )
                                                }
                                                item(key = "non_veg_chip", contentType = "chip") {
                                                    FoodChip(
                                                        foodType = Dietary.NonVeg,
                                                        isSelected = selectedFilterTab == "Non-Veg",
                                                        shapeStyle = ChipShapeStyle.Round,
                                                        onClick = {
                                                            focusManager.clearFocus()
                                                            selectedFilterTab = "Non-Veg"
                                                        },
                                                        isTransparent = true
                                                    )
                                                }
                                                item(key = "cuisine_chip", contentType = "chip") {
                                                    val hasSelectedCuisines = selectedCuisines.isNotEmpty()
                                                    val cuisineLabel = if (hasSelectedCuisines) {
                                                        "Cuisine (${selectedCuisines.size})"
                                                    } else {
                                                        "Cuisine"
                                                    }
                                                    FilterChip(
                                                        label = cuisineLabel,
                                                        isSelected = hasSelectedCuisines,
                                                        shapeStyle = ChipShapeStyle.Round,
                                                        hasStroke = true,
                                                        hasDropdown = true,
                                                        onClick = {
                                                            focusManager.clearFocus()
                                                            showCuisineBottomSheet = true
                                                        },
                                                        isTransparent = true
                                                    )
                                                }
                                                item(key = "type_chip", contentType = "chip") {
                                                    val hasSelectedTypes = selectedTypes.isNotEmpty()
                                                    val typeLabel = if (hasSelectedTypes) {
                                                        "Type (${selectedTypes.size})"
                                                    } else {
                                                        "Type"
                                                    }
                                                    FilterChip(
                                                        label = typeLabel,
                                                        isSelected = hasSelectedTypes,
                                                        shapeStyle = ChipShapeStyle.Round,
                                                        hasStroke = true,
                                                        hasDropdown = true,
                                                        onClick = {
                                                            focusManager.clearFocus()
                                                            showTypeBottomSheet = true
                                                        },
                                                        isTransparent = true
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (isLoading) {
                                        items(count = 3, contentType = { "skeleton" }) {
                                            Spacer(Modifier.height(12.dp))
                                            SkeletonMenuCategoryCard(brush = shimmerBrush())
                                        }
                                    } else if (categorizedItems.isEmpty()) {
                                        item(key = "empty_state", contentType = "empty") {
                                            Spacer(Modifier.height(12.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(320.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Top,
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_catering),
                                                        contentDescription = null,
                                                        tint = ContentTertiary,
                                                        modifier = Modifier.size(84.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Text(
                                                        text = if (allMenuItems.isEmpty()) "Your menu will\nappear here" else "No items match\nyour search",
                                                        style = JasnifyTheme.typography.displayMedium.copy(
                                                            textAlign = TextAlign.Center,
                                                            fontWeight = FontWeight.Medium
                                                        ),
                                                        color = ContentTertiary
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        item(key = "spacer_top", contentType = "spacer") {
                                            Spacer(Modifier.height(12.dp))
                                        }
                                        categorizedItems.forEach { (category, items) ->
                                            item(key = "category_$category", contentType = "category_card") {
                                                MenuCategoryCard(
                                                    categoryTitle = category,
                                                    items = items,
                                                    selectedItemIds = selectedItemIds,
                                                    isSelectionMode = isSelectionMode,
                                                    onItemClick = { item ->
                                                        focusManager.clearFocus()
                                                        if (isSelectionMode) {
                                                            selectedItemIds = if (selectedItemIds.contains(item.id)) {
                                                                selectedItemIds - item.id
                                                            } else {
                                                                selectedItemIds + item.id
                                                            }
                                                        } else {
                                                            selectedItemForDetails = item
                                                            showDetailsBottomSheet = true
                                                        }
                                                    },
                                                    onItemLongClick = { item ->
                                                        focusManager.clearFocus()
                                                        selectedItemIds = if (selectedItemIds.contains(item.id)) {
                                                            selectedItemIds - item.id
                                                        } else {
                                                            selectedItemIds + item.id
                                                        }
                                                    },
                                                    modifier = Modifier.padding(horizontal = 12.dp)
                                                )
                                                Spacer(Modifier.height(12.dp))
                                            }
                                        }

                                        item(key = "footer", contentType = "footer") {
                                            FooterJansify()
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
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
                                                onClick = {
                                                    focusManager.clearFocus()
                                                },
                                                text = "Ask AI",
                                                type = ButtonType.Secondary,
                                                shapeStyle = ButtonShapeStyle.Round,
                                                leadingIcon = painterResource(id = R.drawable.ic_ai),
                                                modifier = if (isViewer) Modifier.weight(1f) else Modifier
                                            )

                                            if (!isViewer) {
                                                Spacer(Modifier.width(4.dp))

                                                CustomTextButton(
                                                    onClick = {
                                                        focusManager.clearFocus()
                                                        editingItem = null
                                                        newItemName = ""
                                                        newItemCuisine = "Indian"
                                                        newItemType = "Starters"
                                                        newItemDietary = Dietary.Veg
                                                        showAddItemSheet = true
                                                    },
                                                    text = "Add an Item",
                                                    type = ButtonType.Primary,
                                                    shapeStyle = ButtonShapeStyle.Round,
                                                    leadingIcon = painterResource(R.drawable.ic_plus),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        CateringMenuView.MANAGE_ROOM_ACCESS -> {
                            activeEvent?.id?.let { id ->
                                CateringRoomContent(
                                    eventId = id,
                                    roomViewModel = roomViewModel,
                                    currentUserRole = currentUserRole,
                                    onBackClick = {
                                        currentView = CateringMenuView.MENU
                                        focusManager.clearFocus()
                                    },
                                    onMenuClick = {
                                        showRoomMenuBottomSheet = true
                                        focusManager.clearFocus()
                                    },
                                    onRemove = { targetUser ->
                                        userToRemove = targetUser
                                    },
                                    onLeave = {
                                        showLeaveConfirmation = true
                                    },
                                    onShowToast = { toastData = it }
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = toastData.message != null && !showAddItemSheet && !showDetailsBottomSheet && !showDeleteConfirmationSheet && !showMenuBottomSheet && !showRoomMenuBottomSheet && userToRemove == null,
                enter = slideInVertically(initialOffsetY = { -it - 500 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it - 500 }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .zIndex(99f)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                CustomToast(
                    message = toastData.message.orEmpty(),
                    type = toastData.type
                )
            }
        }
    }

    if (showDetailsBottomSheet && selectedItemForDetails != null) {
        val currentItem = selectedItemForDetails!!
        val itemCategoryStyle = remember(currentItem.type) { getCategoryStyle(currentItem.type) }

        CustomBottomSheet(
            heading = "Item Details",
            sheetHeight = if (!isViewer) 248.dp else 178.dp,
            onProgress = { progress -> sheetMotionProgress = progress },
            onDismiss = {
                focusManager.clearFocus()
                showDetailsBottomSheet = false
                selectedItemForDetails = null
            },
            containerBrush = itemCategoryStyle.sheetBrush,
            showDragHandle = false,
            closeButtonBackgroundStyle = ButtonBackground.TRANSLUCENT
        ) {
            ItemDetailsSheetContent(
                item = currentItem,
                canEdit = !isViewer,
                onDeleteClick = {
                    focusManager.clearFocus()
                    itemToDelete = currentItem
                    showDetailsBottomSheet = false
                    showDeleteConfirmationSheet = true
                },
                onEditClick = {
                    focusManager.clearFocus()
                    editingItem = currentItem
                    newItemName = currentItem.name
                    newItemCuisine = currentItem.cuisine
                    newItemType = currentItem.type
                    newItemDietary = currentItem.dietary

                    showDetailsBottomSheet = false
                    showAddItemSheet = true
                }
            )
        }
    }

    if (showDeleteConfirmationSheet) {
        val count = selectedItemIds.size
        val deleteHeading = if (isSelectionMode) {
            if (count == 1) "Remove 1 item?" else "Remove $count items?"
        } else {
            "Remove item?"
        }

        val deleteSubheading = if (isSelectionMode) {
            "The selected items will be removed from the Catering Menu."
        } else {
            "The item will be removed from the Catering Menu."
        }

        ConfirmationBottomSheet(
            heading = deleteHeading,
            subHeading = deleteSubheading,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showDeleteConfirmationSheet = false
                if (!isSelectionMode) itemToDelete = null
            },
            onConfirm = {
                focusManager.clearFocus()
                if (isSelectionMode) {
                    val removedCount = selectedItemIds.size
                    selectedItemIds.forEach { id -> cateringViewModel.deleteItem(id) }
                    selectedItemIds = emptySet()
                    isMultiSelectActive = false
                    toastData = ToastData("$removedCount item${if (removedCount > 1) "s" else ""} removed from menu", ToastType.SUCCESS)
                } else {
                    itemToDelete?.let { cateringViewModel.deleteItem(it.id) }
                    itemToDelete = null
                    toastData = ToastData("Item removed from menu", ToastType.SUCCESS)
                }
                showDeleteConfirmationSheet = false
            }
        )
    }

    if (showCuisineBottomSheet) {
        FilterBottomSheet(
            title = "Select Cuisine",
            options = cuisineOptions,
            initialSelectedOptions = selectedCuisines,
            showSearchBar = true,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showCuisineBottomSheet = false
            },
            onApply = { selectedOptions ->
                focusManager.clearFocus()
                selectedCuisines = selectedOptions
                showCuisineBottomSheet = false
            }
        )
    }

    if (showTypeBottomSheet) {
        FilterFoodTypeBottomSheet(
            options = foodTypeOptions,
            initialSelectedOptions = selectedTypes,
            isMultiSelect = true,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showTypeBottomSheet = false
            },
            onApply = { selectedOptions ->
                focusManager.clearFocus()
                selectedTypes = selectedOptions
                showTypeBottomSheet = false
            }
        )
    }

    if (showAddItemSheet) {
        CustomBottomSheet(
            heading = if (editingItem != null) "Edit menu item" else "Add an item to menu",
            onDismiss = { showAddItemSheet = false },
            onProgress = { sheetMotionProgress = it },
            sheetHeight = null,
            showDragHandle = false,
            showCloseButton = true
        ) {
            val view = LocalView.current
            DisposableEffect(view) {
                var parent = view.parent
                var dialogWindow: android.view.Window? = null
                while (parent != null) {
                    if (parent is DialogWindowProvider) {
                        dialogWindow = parent.window
                        break
                    }
                    parent = parent.parent
                }

                dialogWindow?.let { w ->
                    val colorInt = SurfacePrimary.toArgb()
                    w.navigationBarColor = colorInt

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        w.isNavigationBarContrastEnforced = false
                    }

                    val isLightBackground = ColorUtils.calculateLuminance(colorInt) > 0.5
                    WindowCompat.getInsetsController(w, view).isAppearanceLightNavigationBars = isLightBackground
                }
                onDispose {}
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = toastData.message != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .zIndex(998f)
                ) {
                    CustomToast(
                        message = toastData.message.orEmpty(),
                        type = toastData.type
                    )
                }

                AddItemSheetContent(
                    itemName = newItemName,
                    onItemNameChange = { newItemName = it },
                    cuisine = newItemCuisine,
                    onCuisineClick = {
                        focusManager.clearFocus()
                        showAddCuisineBottomSheet = true
                    },
                    type = newItemType,
                    onTypeClick = {
                        focusManager.clearFocus()
                        showAddTypeBottomSheet = true
                    },
                    dietary = newItemDietary,
                    onDietaryChange = {
                        focusManager.clearFocus()
                        newItemDietary = it
                    },
                    isEditMode = editingItem != null,
                    onSubmitClick = {
                        focusManager.clearFocus()
                        if (newItemName.isNotBlank()) {
                            successMessage = if (editingItem != null) {
                                "Item has been updated"
                            } else {
                                "Item added to menu"
                            }

                            if (editingItem != null) {
                                cateringViewModel.updateItem(
                                    id = editingItem!!.id,
                                    name = newItemName,
                                    dietary = newItemDietary,
                                    type = newItemType,
                                    cuisine = newItemCuisine
                                )
                            } else {
                                cateringViewModel.addItem(
                                    name = newItemName,
                                    dietary = newItemDietary,
                                    type = newItemType,
                                    cuisine = newItemCuisine
                                )
                            }

                            showAddItemSheet = false
                            showSuccessSheet = true

                            newItemName = ""
                            newItemCuisine = "Indian"
                            newItemType = "Starters"
                            newItemDietary = Dietary.Veg
                            editingItem = null
                        } else {
                            toastData = ToastData(
                                message = "Please type or search a dish!",
                                type = ToastType.ERROR
                            )
                        }
                    }
                )
            }
        }
    }

    if (showSuccessSheet) {
        CustomSuccessBottomSheet(
            message = successMessage,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showSuccessSheet = false
            }
        )
    }

    if (showAddCuisineBottomSheet) {
        FilterBottomSheet(
            title = "Select Cuisine",
            options = cuisineOptions,
            initialSelectedOptions = if (newItemCuisine.isNotEmpty()) setOf(newItemCuisine) else emptySet(),
            showSearchBar = true,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showAddCuisineBottomSheet = false
            },
            onApply = { selectedOptions ->
                focusManager.clearFocus()
                newItemCuisine = selectedOptions.firstOrNull() ?: "Indian"
                showAddCuisineBottomSheet = false
            },
            isMultiSelect = false
        )
    }

    if (showAddTypeBottomSheet) {
        FilterFoodTypeBottomSheet(
            options = foodTypeOptions,
            initialSelectedOptions = if (newItemType.isNotEmpty()) setOf(newItemType) else emptySet(),
            isMultiSelect = false,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showAddTypeBottomSheet = false
            },
            onApply = { selectedOptions ->
                focusManager.clearFocus()
                newItemType = selectedOptions.firstOrNull() ?: "Starters"
                showAddTypeBottomSheet = false
            }
        )
    }

    if (showMenuBottomSheet) {
        val plusPainter = painterResource(R.drawable.ic_plus)
        val checkPainter = painterResource(R.drawable.ic_multi_select)
        val userDefaultPainter = painterResource(R.drawable.ic_user_default)

        val menuItems = remember(isViewer, isOwner, plusPainter, checkPainter, userDefaultPainter) {
            listOfNotNull(
                if (!isViewer) {
                    listOf(
                        MenuSheetActionItem(
                            text = "Add an item",
                            icon = plusPainter,
                            onClick = {
                                showMenuBottomSheet = false
                                showAddItemSheet = true
                            },
                            iconPlacement = IconPlacement.Top
                        ),
                        MenuSheetActionItem(
                            text = "Multi Select",
                            icon = checkPainter,
                            onClick = {
                                showMenuBottomSheet = false
                                isMultiSelectActive = true
                            },
                            iconPlacement = IconPlacement.Top
                        )
                    )
                } else null,
                listOf(
                    MenuSheetActionItem(
                        text = if (isOwner) "Manage Room Access" else "Room Members",
                        icon = userDefaultPainter,
                        onClick = {
                            showMenuBottomSheet = false
                            currentView = CateringMenuView.MANAGE_ROOM_ACCESS
                        }
                    )
                )
            )
        }

        MenuBottomSheet(
            items = menuItems,
            onProgress = { sheetMotionProgress = it },
            onCancelClick = {
                showMenuBottomSheet = false
            }
        )
    }

    if (showRoomMenuBottomSheet) {
        val logoutPainter = painterResource(R.drawable.ic_logout)
        val errorColor = MaterialTheme.colorScheme.error

        val roomMenuItems = remember(logoutPainter, errorColor) {
            listOf(
                listOf(
                    MenuSheetActionItem(
                        text = "Leave Room",
                        icon = logoutPainter,
                        contentColor = errorColor,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            showLeaveConfirmation = true
                        }
                    )
                )
            )
        }

        MenuBottomSheet(
            items = roomMenuItems,
            onProgress = { sheetMotionProgress = it },
            onCancelClick = {
                showRoomMenuBottomSheet = false
            }
        )
    }

    if (showRoomAccessBottomSheet) {
        RoomAccessBottomSheet(
            onDismissRequest = { showRoomAccessBottomSheet = false },
            onGrantAccess = { email, role ->
                activeEvent?.id?.let { eventId ->
                    roomViewModel.grantAccess(eventId, "Catering", email, role)
                }
            },
            onProgress = { sheetMotionProgress = it },
            searchResults = searchResults,
            onSearch = { roomViewModel.searchUsers(it) }
        )
    }

    userToRemove?.let { user ->
        ConfirmationBottomSheet(
            heading = "Remove ${user.name} from Catering Menu?",
            subHeading = "They will not be able to access this room anymore.",
            confirmButtonText = "Remove",
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                userToRemove = null
            },
            onConfirm = {
                val target = userToRemove
                if (target != null && activeEvent != null) {
                    roomViewModel.removeAccess(activeEvent!!.id, "Catering", target.uid)
                    toastData = ToastData("${target.name} removed from room", ToastType.SUCCESS)
                }
                userToRemove = null
            }
        )
    }

    if (showLeaveConfirmation) {
        ConfirmationBottomSheet(
            heading = "Leaving Catering Room?",
            subHeading = "You will lose access to this room and won't be able to see updates.",
            confirmButtonText = "Leave",
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                showLeaveConfirmation = false
            },
            onConfirm = {
                activeEvent?.id?.let { eventId ->
                    roomViewModel.removeAccess(eventId, "Catering", currentUserUid)
                }
                toastData = ToastData("You left the room", ToastType.DEFAULT)
                currentView = CateringMenuView.MENU
                showLeaveConfirmation = false
            }
        )
    }
}

@Composable
fun MenuCategoryCard(
    categoryTitle: String,
    items: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit,
    onItemLongClick: (MenuItem) -> Unit = {},
    selectedItemIds: Set<String> = emptySet(),
    isSelectionMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val categoryStyle = remember(categoryTitle) { getCategoryStyle(categoryTitle) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = categoryStyle.containerBrush)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = categoryTitle.uppercase(),
                            style = JasnifyTheme.typography.displayLarge.copy(
                                fontFamily = FontFamily(Font(R.font.facadflux_bold)),
                                fontWeight = FontWeight.Bold,
                                color = categoryStyle.headerTextColor,
                                lineHeight = JasnifyTheme.typography.displayLarge.fontSize
                            )
                        )
                        Text(
                            text = "${items.size} ITEMS",
                            style = JasnifyTheme.typography.labelSmall.copy(
                                color = categoryStyle.subtitleTextColor,
                                letterSpacing = 2.sp
                            )
                        )
                    }

                    Image(
                        painter = painterResource(id = categoryStyle.illustrationRes),
                        contentDescription = categoryTitle,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardSquircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(brush = CardTranslucentGradientBrush, shape = CardSquircleShape)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            items.forEach { item ->
                                val isChecked = selectedItemIds.contains(item.id)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .detectCombinedClicks(
                                            key = item.id,
                                            onTap = {
                                                focusManager.clearFocus()
                                                onItemClick(item)
                                            },
                                            onLongPress = {
                                                focusManager.clearFocus()
                                                onItemLongClick(item)
                                            }
                                        )
                                ) {
                                    CateringItemChip(
                                        label = item.name,
                                        foodType = item.dietary,
                                        isMultiSelect = isSelectionMode,
                                        checked = isChecked,
                                        onCheckedChange = { onItemClick(item) },
                                        onClick = {},
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemDetailsSheetContent(
    item: MenuItem,
    canEdit: Boolean = true,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val itemCategoryStyle = remember(item.type) { getCategoryStyle(item.type) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = CardSquircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = CardTranslucentGradientBrush, shape = CardSquircleShape)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isVeg = item.dietary == Dietary.Veg
                    val drawableRes = if (isVeg) R.drawable.ic_veg else R.drawable.ic_non_veg

                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = if (isVeg) "Vegetarian" else "Non-Vegetarian",
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = item.name,
                        style = JasnifyTheme.typography.labelXLarge,
                        color = itemCategoryStyle.headerTextColor,
                        fontWeight = FontWeight.Medium,
                    )

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CUISINE",
                                style = JasnifyTheme.typography.labelSmall,
                                color = ContentSecondary,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.cuisine,
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary,
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TYPE",
                                style = JasnifyTheme.typography.labelSmall,
                                color = ContentSecondary,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.type,
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary,
                            )
                        }
                    }
                }
            }
        }

        if (canEdit) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomIconButton(
                    onClick = {
                        focusManager.clearFocus()
                        onDeleteClick()
                    },
                    icon = painterResource(R.drawable.ic_delete),
                    containerColor = SurfacePrimary,
                    contentColor = MaterialTheme.colorScheme.error,
                )

                CustomTextButton(
                    onClick = {
                        focusManager.clearFocus()
                        onEditClick()
                    },
                    text = "Edit Details",
                    leadingIcon = painterResource(R.drawable.ic_edit),
                    containerColor = ContentPrimary,
                    contentColor = ContentInvPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AddItemSheetContent(
    itemName: String,
    onItemNameChange: (String) -> Unit,
    cuisine: String,
    onCuisineClick: () -> Unit,
    type: String,
    onTypeClick: () -> Unit,
    dietary: Dietary,
    onDietaryChange: (Dietary) -> Unit,
    onSubmitClick: () -> Unit,
    isEditMode: Boolean = false
) {
    val isNameEntered = itemName.isNotBlank()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val inputSquircleShape = remember { SquircleShape(CornerLarge) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 300))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(scrollState)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ITEM",
                    style = JasnifyTheme.typography.labelSmall,
                    color = ContentSecondary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                PrimaryInput(
                    value = itemName,
                    onValueChange = onItemNameChange,
                    placeholder = "Type or Search a dish",
                    trailingIcon = painterResource(id = R.drawable.ic_ai),
                    textStyle = JasnifyTheme.typography.labelXLarge.copy(color = ContentPrimary)
                )
            }

            AnimatedVisibility(
                visible = isNameEntered,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 300))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "CUISINE",
                            style = JasnifyTheme.typography.labelSmall,
                            color = ContentSecondary,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            PrimaryInput(
                                value = "",
                                onValueChange = {},
                                placeholder = cuisine,
                                trailingIcon = painterResource(R.drawable.ic_edit),
                                trailingIconEnabled = true,
                                textStyle = JasnifyTheme.typography.labelXLarge.copy(color = ContentPrimary)
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(inputSquircleShape)
                                    .clickable {
                                        focusManager.clearFocus()
                                        onCuisineClick()
                                    }
                            )
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "TYPE",
                            style = JasnifyTheme.typography.labelSmall,
                            color = ContentSecondary,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            PrimaryInput(
                                value = "",
                                onValueChange = {},
                                placeholder = type,
                                trailingIcon = painterResource(R.drawable.ic_edit),
                                textStyle = JasnifyTheme.typography.labelXLarge.copy(color = ContentPrimary),
                                trailingIconEnabled = true
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(inputSquircleShape)
                                    .clickable {
                                        focusManager.clearFocus()
                                        onTypeClick()
                                    }
                            )
                        }
                    }
                }
            }

            DashedDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                dashLength = 12f,
                gapLength = 6f
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FoodChip(
                    foodType = Dietary.Veg,
                    isSelected = dietary == Dietary.Veg,
                    shapeStyle = ChipShapeStyle.Square,
                    size = ChipSize.Large,
                    onClick = {
                        focusManager.clearFocus()
                        onDietaryChange(Dietary.Veg)
                    }
                )
                FoodChip(
                    foodType = Dietary.NonVeg,
                    isSelected = dietary == Dietary.NonVeg,
                    shapeStyle = ChipShapeStyle.Square,
                    size = ChipSize.Large,
                    onClick = {
                        focusManager.clearFocus()
                        onDietaryChange(Dietary.NonVeg)
                    }
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            CustomTextButton(
                onClick = {
                    focusManager.clearFocus()
                    onSubmitClick()
                },
                text = if (isEditMode) "Save Changes" else "Add to Menu",
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                size = ButtonSize.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}