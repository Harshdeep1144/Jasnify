package com.harshdeep.jasnify.presentation.screens.catering

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.RoomAccessBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.chip.CateringItemChip
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.chip.FoodChip
import com.harshdeep.jasnify.presentation.components.filter.FilterBottomSheet
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
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.presentation.viewmodels.CateringViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.BackgroundBrand
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.Pattaya
import com.harshdeep.jasnify.theme.SurfacePrimary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

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

    val cateringItemsEntities by cateringViewModel.cateringItems.collectAsStateWithLifecycle()
    val isLoading by cateringViewModel.isLoading.collectAsStateWithLifecycle()
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()

    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""

    val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
    val isOwner = activeEvent?.ownerId == currentUserUid
    val currentUserRole = when {
        isOwner -> UserRole.OWNER
        currentUserInRoom != null -> currentUserInRoom.role
        else -> UserRole.VIEWER
    }
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
        if (activeEventId != null) {
            cateringViewModel.setEventId(activeEventId!!)
            roomViewModel.verifyAccess(activeEventId!!, "Catering", currentUserUid)
            roomViewModel.loadRoomUsers(activeEventId!!, "Catering")
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

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        searchText = ""
        focusManager.clearFocus()
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

    val mainListState = rememberLazyListState()

    val topBarMaxScrollPx = with(density) { 56.dp.toPx() }
    val topBarScrollProgress by remember {
        derivedStateOf {
            if (mainListState.firstVisibleItemIndex > 0) {
                1f
            } else {
                (mainListState.firstVisibleItemScrollOffset / topBarMaxScrollPx).coerceIn(0f, 1f)
            }
        }
    }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            showDetailsBottomSheet ||
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
        }
    }

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

    val cuisineOptions by remember(allMenuItems) {
        derivedStateOf {
            (listOf("Indian", "Japanese", "Mexican", "Italian", "Chinese", "French", "Thai", "Korean") +
                    allMenuItems.map { it.cuisine }).distinct().sorted()
        }
    }

    val typeOptions by remember(allMenuItems) {
        derivedStateOf {
            (listOf("Starters", "Beverages", "Main Course", "Desserts") +
                    allMenuItems.map { it.type }).distinct().sorted()
        }
    }

    val filteredItems by remember(allMenuItems, searchText, selectedFilterTab, selectedCuisines, selectedTypes) {
        derivedStateOf {
            allMenuItems.filter { item ->
                val matchesSearch = item.name.contains(searchText, ignoreCase = true) ||
                        item.type.contains(searchText, ignoreCase = true) ||
                        item.cuisine.contains(searchText, ignoreCase = true)

                val matchesTab = when (selectedFilterTab) {
                    "Veg" -> item.dietary == Dietary.Veg
                    "Non-Veg" -> item.dietary == Dietary.NonVeg
                    else -> true
                }

                val matchesCuisine = if (selectedCuisines.isEmpty()) true else selectedCuisines.contains(item.cuisine)
                val matchesType = if (selectedTypes.isEmpty()) true else selectedTypes.contains(item.type)

                matchesSearch && matchesTab && matchesCuisine && matchesType
            }
        }
    }

    val categorizedItems by remember(filteredItems) {
        derivedStateOf {
            filteredItems.groupBy { it.type }
        }
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
                                        .windowInsetsTopHeight(WindowInsets.statusBars)
                                        .background(BackgroundPrimary)
                                        .zIndex(100f)
                                )

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .statusBarsPadding(),
                                    state = mainListState,
                                ) {
                                    item(key = "top_bar") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .graphicsLayer {
                                                    alpha =
                                                        (1f - topBarScrollProgress).coerceIn(0f, 1f)
                                                    translationY = -topBarScrollProgress * 30f
                                                }
                                        ) {
                                            AnimatedContent(
                                                targetState = isSearchActive,
                                                transitionSpec = {
                                                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                                                },
                                                label = "CateringTopBarSearchTransition"
                                            ) { active ->
                                                CustomTopBar(
                                                    title = if (active) "Search Menu" else "Catering Menu",
                                                    onBackClick = if (active) {
                                                        {
                                                            isSearchActive = false
                                                            searchText = ""
                                                            focusManager.clearFocus()
                                                        }
                                                    } else {
                                                        {
                                                            focusManager.clearFocus()
                                                            onBackClick()
                                                        }
                                                    },
                                                    onMenuClick = if (active) null else {
                                                        {
                                                            focusManager.clearFocus()
                                                            showMenuBottomSheet = true
                                                        }
                                                    },
                                                    isLargeTitle = true,
                                                    buttonStyle = ButtonBackground.OPAQUE
                                                )
                                            }
                                        }
                                    }

                                    stickyHeader(key = "search_and_filters_header") {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(BackgroundPrimary)
                                                .zIndex(10f)
                                        ) {
                                            Spacer(Modifier.height(12.dp))
                                            CustomSearchBar(
                                                value = searchText,
                                                onValueChange = { searchText = it },
                                                onActiveChange = { isSearchActive = it },
                                                placeholder = "Search Menu",
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                            )

                                            LazyRow(
                                                state = rememberLazyListState(),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                item {
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
                                                        }
                                                    )
                                                }
                                                item {
                                                    FoodChip(
                                                        foodType = Dietary.Veg,
                                                        isSelected = selectedFilterTab == "Veg",
                                                        shapeStyle = ChipShapeStyle.Round,
                                                        onClick = {
                                                            focusManager.clearFocus()
                                                            selectedFilterTab = "Veg"
                                                        }
                                                    )
                                                }
                                                item {
                                                    FoodChip(
                                                        foodType = Dietary.NonVeg,
                                                        isSelected = selectedFilterTab == "Non-Veg",
                                                        shapeStyle = ChipShapeStyle.Round,
                                                        onClick = {
                                                            focusManager.clearFocus()
                                                            selectedFilterTab = "Non-Veg"
                                                        }
                                                    )
                                                }

                                                item {
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
                                                        }
                                                    )
                                                }

                                                item {
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
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (isLoading) {
                                        items(3) {
                                            Spacer(Modifier.height(12.dp))
                                            SkeletonMenuCategoryCard(brush = shimmerBrush())
                                        }
                                    } else if (categorizedItems.isEmpty()) {
                                        item {
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
                                        item {
                                            Spacer(Modifier.height(12.dp))
                                        }
                                        categorizedItems.forEach { (category, items) ->
                                            item(key = "category_$category") {
                                                MenuCategoryCard(
                                                    categoryTitle = category,
                                                    items = items,
                                                    onItemClick = { item ->
                                                        focusManager.clearFocus()
                                                        selectedItemForDetails = item
                                                        showDetailsBottomSheet = true
                                                    },
                                                    modifier = Modifier.padding(horizontal = 12.dp)
                                                )
                                                Spacer(Modifier.height(12.dp))
                                            }
                                        }

                                        item(key = "footer") {
                                            FooterJansify()
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            brush = BottomGradientBrush
                                        )
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
                    message = toastData.message ?: "",
                    type = toastData.type
                )
            }
        }
    }

    if (showDetailsBottomSheet && selectedItemForDetails != null) {
        CustomBottomSheet(
            heading = "Item Details",
            sheetHeight = if (!isViewer) 360.dp else 290.dp,
            onProgress = { progress -> sheetMotionProgress = progress },
            onDismiss = {
                focusManager.clearFocus()
                showDetailsBottomSheet = false
                selectedItemForDetails = null
            }
        ) {
            ItemDetailsSheetContent(
                item = selectedItemForDetails!!,
                canEdit = !isViewer,
                onDeleteClick = {
                    focusManager.clearFocus()
                    itemToDelete = selectedItemForDetails
                    showDetailsBottomSheet = false
                    showDeleteConfirmationSheet = true
                },
                onEditClick = {
                    focusManager.clearFocus()
                    editingItem = selectedItemForDetails
                    newItemName = selectedItemForDetails?.name ?: ""
                    newItemCuisine = selectedItemForDetails?.cuisine ?: "Indian"
                    newItemType = selectedItemForDetails?.type ?: "Starters"
                    newItemDietary = selectedItemForDetails?.dietary ?: Dietary.Veg

                    showDetailsBottomSheet = false
                    showAddItemSheet = true
                }
            )
        }
    }

    if (showDeleteConfirmationSheet && itemToDelete != null) {
        ConfirmationBottomSheet(
            heading = "Remove item?",
            subHeading = "The item will be removed from the Catering Menu.",
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showDeleteConfirmationSheet = false
                itemToDelete = null
            },
            onConfirm = {
                focusManager.clearFocus()
                itemToDelete?.let { cateringViewModel.deleteItem(it.id) }
                showDeleteConfirmationSheet = false
                itemToDelete = null
                toastData = ToastData("Item removed from menu", ToastType.SUCCESS)
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
        FilterBottomSheet(
            title = "Select Type",
            options = typeOptions,
            initialSelectedOptions = selectedTypes,
            showSearchBar = false,
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
            showDragHandle = true,
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
                        message = toastData.message ?: "",
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
        FilterBottomSheet(
            title = "Select Type",
            options = typeOptions,
            initialSelectedOptions = if (newItemType.isNotEmpty()) setOf(newItemType) else emptySet(),
            showSearchBar = false,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showAddTypeBottomSheet = false
            },
            onApply = { selectedOptions ->
                focusManager.clearFocus()
                newItemType = selectedOptions.firstOrNull() ?: "Starters"
                showAddTypeBottomSheet = false
            },
            isMultiSelect = false
        )
    }

    if (showMenuBottomSheet) {
        MenuBottomSheet(
            items = listOfNotNull(
                if (!isViewer) {
                    listOf(
                        MenuSheetActionItem(
                            text = "Add an item",
                            icon = painterResource(R.drawable.ic_plus),
                            onClick = {
                                showMenuBottomSheet = false
                                showAddItemSheet = true
                            }
                        )
                    )
                } else null,
                listOf(
                    MenuSheetActionItem(
                        text = if(isOwner) "Manage Room Access" else "Room Members",
                        icon = painterResource(R.drawable.ic_user_default),
                        onClick = {
                            showMenuBottomSheet = false
                            currentView = CateringMenuView.MANAGE_ROOM_ACCESS
                        }
                    )
                )
            ),
            onProgress = { sheetMotionProgress = it },
            onCancelClick = {
                showMenuBottomSheet = false
            }
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
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = ContentBrand, shape = SquircleShape(CornerExtraLarge)),
        shape = SquircleShape(CornerExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundBrand
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_pattern_source_catering_menu),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.3f
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = categoryTitle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    style = JasnifyTheme.typography.headingXLarge.copy(
                        fontFamily = Pattaya,
                        fontWeight = FontWeight.Normal,
                        color = ContentBrandDark,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                DashedDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                    dashLength = 20f,
                    gapLength = 6f
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items.forEach { item ->
                        CateringItemChip(
                            label = item.name,
                            foodType = item.dietary,
                            isMultiSelect = false,
                            onClick = {
                                focusManager.clearFocus()
                                onItemClick(item)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                    shape = SquircleShape(20.dp)
                ),
            shape = SquircleShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
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

                Column {
                    Text(
                        text = "ITEM",
                        style = JasnifyTheme.typography.labelSmall,
                        color = ContentSecondary,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.name,
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentBrandDark,
                        fontWeight = FontWeight.Medium,
                    )
                }

                DashedDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                    dashLength = 20f,
                    gapLength = 6f
                )

                Column {
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

                DashedDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                    dashLength = 12f,
                    gapLength = 6f
                )

                Column {
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
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error,
                    shapeStyle = ButtonShapeStyle.Square
                )

                CustomTextButton(
                    onClick = {
                        focusManager.clearFocus()
                        onEditClick()
                    },
                    text = "Edit Details",
                    type = ButtonType.Secondary,
                    shapeStyle = ButtonShapeStyle.Square,
                    leadingIcon = painterResource(R.drawable.ic_edit),
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
                                    .clip(SquircleShape(CornerLarge))
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
                                    .clip(SquircleShape(CornerLarge))
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

@Preview(showBackground = true)
@Composable
fun CateringMenuScreenPreview() {
    JasnifyTheme {
        CateringMenuScreen(
            onBackClick = {}
        )
    }
}