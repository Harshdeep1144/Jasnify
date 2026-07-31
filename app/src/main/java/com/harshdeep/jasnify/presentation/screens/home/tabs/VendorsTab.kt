package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SaveListBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.VendorCardFull
import com.harshdeep.jasnify.presentation.components.chip.VendorTypeChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.sections.RecentSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.TrendingAiSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.VendorCarousel
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.JasnifyTheme

enum class VendorScreenState {
    MAIN, CATEGORY_DETAIL, ALL_SAVED, CATEGORY_SAVED, ROOM, VENDOR_DETAIL
}

data class VendorCategoryItem(val name: String, val icon: Int)

private const val PREFS_NAME = "vendor_search_prefs"
private const val KEY_RECENT_SEARCHES = "recent_searches"

private fun getRecentSearches(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val raw = prefs.getString(KEY_RECENT_SEARCHES, null) ?: return emptyList()
    return if (raw.isEmpty()) emptyList() else raw.split("|||")
}

private fun saveRecentSearch(context: Context, name: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val current = getRecentSearches(context).toMutableList()
    current.remove(name)
    current.add(0, name)
    val limited = current.take(8)
    prefs.edit { putString(KEY_RECENT_SEARCHES, limited.joinToString("|||"))}
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VendorsTab(
    mainNavController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    var currentScreenState by remember { mutableStateOf(VendorScreenState.MAIN) }
    var selectedCategory by remember { mutableStateOf<VendorCategoryItem?>(null) }
    var selectedVendor by remember { mutableStateOf<Vendor?>(null) }

    var recentSearchesNames by remember { mutableStateOf(getRecentSearches(context)) }

    val selectedCity by mainNavController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("selected_location", "City State")
        ?.collectAsState() ?: remember { mutableStateOf("City State") }

    val categories = listOf(
        VendorCategoryItem("Grooming", R.drawable.ill_vendor_grooming),
        VendorCategoryItem("Makeup", R.drawable.ill_vendor_makeup),
        VendorCategoryItem("Photography", R.drawable.ill_vendor_photographers),
        VendorCategoryItem("Mehendi", R.drawable.ill_vendor_mehendi),
        VendorCategoryItem("Jewellery", R.drawable.ill_vendor_jewellery),
        VendorCategoryItem("Outfits", R.drawable.ill_bride_and_groom),
        VendorCategoryItem("Entertainment", R.drawable.ill_vendor_entertainment),
        VendorCategoryItem("Food", R.drawable.ill_vendor_food),
        VendorCategoryItem("Gifts", R.drawable.ill_vendor_gifts)
    )

    val allSampleVendors = MockData.sampleVendors

    val recentVendorsList = remember(recentSearchesNames, allSampleVendors) {
        recentSearchesNames.mapNotNull { name ->
            allSampleVendors.find { it.name == name }
        }
    }

    val handleVendorClick: (Vendor) -> Unit = { vendor ->
        saveRecentSearch(context, vendor.name)
        recentSearchesNames = getRecentSearches(context)
        selectedVendor = vendor
        currentScreenState = VendorScreenState.VENDOR_DETAIL
    }

    var showSaveListBottomSheet by remember { mutableStateOf(false) }

    val handleFavoriteToggle: (Vendor) -> Unit = { vendor ->
        // In a real app, you might update the favorite state here
        showSaveListBottomSheet = true
    }

    LaunchedEffect(showMenuSheet, isSearchActive, currentScreenState, showSaveListBottomSheet) {
        val isBottomBarVisible = !showMenuSheet && !isSearchActive && !showSaveListBottomSheet && currentScreenState == VendorScreenState.MAIN
        onBottomBarVisibilityChange(isBottomBarVisible)
    }

    BackHandler {
        if (showSaveListBottomSheet) {
            showSaveListBottomSheet = false
        } else if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
            focusManager.clearFocus()
        } else if (currentScreenState != VendorScreenState.MAIN) {
            currentScreenState = VendorScreenState.MAIN
        } else {
            // Default behavior
        }
    }

    val targetScale = if (showMenuSheet) 0.92f + (0.08f * sheetMotionProgress) else 1.0f
    val backdropScale by animateFloatAsState(targetValue = targetScale, animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f), label = "backdropScale")
    val backdropCornerRadius by animateDpAsState(targetValue = if (showMenuSheet) CornerExtraLarge else 0.dp, animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy), label = "backdropCornerRadius")

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier.fillMaxSize()
                .graphicsLayer {
                    scaleX = backdropScale
                    scaleY = backdropScale
                    clip = showMenuSheet || backdropCornerRadius > 0.dp
                    shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                }
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            AnimatedContent(
                targetState = currentScreenState,
                transitionSpec = { fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300)) },
                label = "VendorTabTransition"
            ) { state ->
                when (state) {
                    VendorScreenState.MAIN -> {
                        VendorMainContent(
                            selectedCity = selectedCity,
                            categories = categories,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            isSearchActive = isSearchActive,
                            onSearchActiveChange = { isSearchActive = it },
                            onMenuClick = { showMenuSheet = true },
                            onLocationClick = { mainNavController.navigate(Screen.LocationSelector.route) },
                            onCategoryClick = { category ->
                                selectedCategory = category
                                currentScreenState = VendorScreenState.CATEGORY_DETAIL
                            },
                            onVendorClick = handleVendorClick,
                            onFavoriteToggle = handleFavoriteToggle,
                            recentVendorsList = recentVendorsList,
                            focusManager = focusManager,
                            context = context,
                            onRecentSearchesUpdate = { recentSearchesNames = it }
                        )
                    }
                    VendorScreenState.CATEGORY_DETAIL -> {
                        selectedCategory?.let { category ->
                            VendorCategoryDetailContent(
                                category = category,
                                selectedCity = selectedCity,
                                onBackClick = { currentScreenState = VendorScreenState.MAIN },
                                onVendorClick = handleVendorClick,
                                onFavoriteToggle = handleFavoriteToggle,
                                sharedTransitionScope = sharedTransitionScope
                            )
                        }
                    }
                    VendorScreenState.VENDOR_DETAIL -> {
                        selectedVendor?.let { vendor ->
                            VendorDetailScreen(
                                vendorDetail = vendor,
                                onBackClick = { currentScreenState = VendorScreenState.MAIN },
                                onFavoriteToggle = { handleFavoriteToggle(it) },
                                sharedTransitionScope = sharedTransitionScope
                            )
                        }
                    }
                    VendorScreenState.ALL_SAVED -> {
                        AllSavedVendorsContent(
                            onBackClick = { currentScreenState = VendorScreenState.MAIN },
                            onVendorClick = handleVendorClick,
                            onFavoriteToggle = handleFavoriteToggle,
                            sharedTransitionScope = sharedTransitionScope
                        )
                    }
                    else -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Screen for $state coming soon")
                        }
                    }
                }
            }
        }

        if (showMenuSheet) {
            val commonMenuItems = listOf(
                listOf(
                    MenuSheetActionItem(
                        text = "Change Location",
                        icon = painterResource(R.drawable.ic_location_marker),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuSheet = false
                            mainNavController.navigate(Screen.LocationSelector.route)
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Manage Room Access",
                        icon = painterResource(R.drawable.ic_user_default),
                        iconPlacement = IconPlacement.Left,
                        onClick = { showMenuSheet = false }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = painterResource(R.drawable.ic_help_feedback),
                        iconPlacement = IconPlacement.Left,
                        onClick = { showMenuSheet = false }
                    )
                )
            )

            val menuItems = if (currentScreenState == VendorScreenState.MAIN) {
                val savedVendorsItem = listOf(
                    MenuSheetActionItem(
                        text = "Saved Vendors",
                        icon = painterResource(R.drawable.ic_heart),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuSheet = false
                            currentScreenState = VendorScreenState.ALL_SAVED
                        }
                    )
                )
                listOf(savedVendorsItem) + commonMenuItems
            } else commonMenuItems

            MenuBottomSheet(
                items = menuItems,
                onCancelClick = { showMenuSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }
        if (showSaveListBottomSheet) {
            SaveListBottomSheet(
                timelineEvents = emptyList(), // Mock or real data
                isMySavedListChecked = false,
                onMySavedListToggled = {},
                selectedEventId = null,
                onEventSelected = {},
                onAddNewEvent = {},
                isViewer = false,
                onDismiss = { showSaveListBottomSheet = false },
                onDone = { showSaveListBottomSheet = false },
                onProgress = { }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorMainContent(
    selectedCity: String,
    categories: List<VendorCategoryItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    onMenuClick: () -> Unit,
    onLocationClick: () -> Unit,
    onCategoryClick: (VendorCategoryItem) -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    recentVendorsList: List<Vendor>,
    focusManager: androidx.compose.ui.focus.FocusManager,
    context: Context,
    onRecentSearchesUpdate: (List<String>) -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                if (isSearchActive) {
                    CustomTopBar(
                        title = "Search Vendors",
                        onBackClick = {
                            onSearchActiveChange(false)
                            onSearchQueryChange("")
                            focusManager.clearFocus()
                        },
                        backIcon = TopIcon.Predefined.DOWN,
                        buttonStyle = ButtonBackground.OPAQUE,
                        isLargeTitle = true
                    )
                } else {
                    CustomTopBar(
                        title = "Vendors",
                        subtitle = selectedCity,
                        onBackClick = {},
                        onMenuClick = onMenuClick,
                        onDropdownClick = onLocationClick,
                        isLargeTitle = true
                    )
                }
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CustomSearchBar(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    onActiveChange = onSearchActiveChange,
                    placeholder = "Search Vendors",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            if (!isSearchActive) {
                item {
                    VendorCategoryGrid(categories = categories, onCategoryClick = onCategoryClick)
                }
                item {
                    OrDivider(text = "EXPLORE", dividerGap = 0.dp, modifier = Modifier.padding(horizontal = 24.dp))
                }
                item {
                    VendorCarousel(title = "Top Makeup Artists in $selectedCity", vendors = MockData.sampleMakeupArtists, onVendorClick = onVendorClick, onFavoriteToggle = onFavoriteToggle, onSeeAllClick = {})
                }
                item {
                    VendorCarousel(title = "Best Photographers in $selectedCity", vendors = MockData.samplePhotographers, onVendorClick = onVendorClick, onFavoriteToggle = onFavoriteToggle, onSeeAllClick = {})
                }
                item {
                    VendorCarousel(title = "Expert Mehendi Artists in $selectedCity", vendors = MockData.sampleMehendiArtists, onVendorClick = onVendorClick, onFavoriteToggle = onFavoriteToggle, onSeeAllClick = {})
                }
                item {
                    DashedDivider()
                    ExploreCategoriesHorizontal(categories = categories, onCategoryClick = onCategoryClick)
                }
                item { FooterJansify() }
            } else {
                item {
                    TrendingAiSearchesSection(onTrendingClick = { query ->
                        onSearchQueryChange(query)
                        focusManager.clearFocus()
                    })
                }
                if (recentVendorsList.isNotEmpty()) {
                    item {
                        RecentSearchesSection(
                            recentVendors = recentVendorsList,
                            onVendorClick = onVendorClick,
                            onRemoveVendor = { vendor ->
                                val current = getRecentSearches(context).toMutableList()
                                current.remove(vendor.name)
                                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit { putString(KEY_RECENT_SEARCHES, current.joinToString("|||")) }
                                onRecentSearchesUpdate(getRecentSearches(context))
                            },
                        )
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VendorCategoryDetailContent(
    category: VendorCategoryItem,
    selectedCity: String,
    onBackClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    var selectedViewType by remember { mutableStateOf("By List") }
    val viewOptions = listOf("By Timeline", "By List")
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = category.name,
                    subtitle = selectedCity,
                    onBackClick = onBackClick,
                    backIcon = TopIcon.Predefined.BACK,
                    onMenuClick = { },
                    isLargeTitle = true
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomSearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        onActiveChange = { isSearchActive = it },
                        modifier = Modifier.weight(1f),
                        isAiSearch = true,
                        placeholder = "Search ${category.name}"
                    )
                    AnimatedVisibility(
                        visible = !isSearchActive,
                        enter = fadeIn(animationSpec = tween(200)) + expandHorizontally(expandFrom = Alignment.End, animationSpec = tween(250)),
                        exit = fadeOut(animationSpec = tween(150)) + shrinkHorizontally(shrinkTowards = Alignment.End, animationSpec = tween(250))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.size(44.dp).background(Color.White, RoundedCornerShape(12.dp)).clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(painter = painterResource(R.drawable.ic_filter), contentDescription = "Filter", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            if (!isSearchActive) {
                item {
                    VendorCarousel(
                        title = "Top-Rated ${category.name}",
                        vendors = MockData.samplePhotographers, // Use actual filtered data
                        onVendorClick = onVendorClick,
                        onFavoriteToggle = onFavoriteToggle,
                        onSeeAllClick = {}
                    )
                }

                item {
                    OrDivider(text = "EXPLORE", dividerGap = 0.dp, modifier = Modifier.padding(horizontal = 24.dp))
                }

                item {
                    IosSegmentedControl(
                        options = viewOptions,
                        selectedOption = selectedViewType,
                        onOptionSelected = { selectedViewType = it },
                        modifier = Modifier.padding(horizontal = 12.dp).height(44.dp)
                    )
                }

                items(MockData.samplePhotographers) { vendor ->
                    VendorCardFull(
                        vendor = vendor,
                        onCardClick = { onVendorClick(vendor) },
                        onFavoriteToggle = { onFavoriteToggle(vendor) },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        sharedTransitionScope = sharedTransitionScope
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AllSavedVendorsContent(
    onBackClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    var selectedViewType by remember { mutableStateOf("By List") }
    val viewOptions = listOf("By Timeline", "By List")

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Saved Vendors",
                    onBackClick = onBackClick,
                    backIcon = TopIcon.Predefined.BACK,
                    isLargeTitle = true
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                IosSegmentedControl(
                    options = viewOptions,
                    selectedOption = selectedViewType,
                    onOptionSelected = { selectedViewType = it },
                    modifier = Modifier.padding(horizontal = 12.dp).height(44.dp)
                )
            }

            if (selectedViewType == "By List") {
                val savedVendors = MockData.sampleVendors.filter { it.favorite }
                if (savedVendors.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxHeight(0.7f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No saved vendors yet", style = JasnifyTheme.typography.bodyLarge, color = ContentPrimary)
                        }
                    }
                } else {
                    items(savedVendors) { vendor ->
                        VendorCardFull(
                            vendor = vendor,
                            onCardClick = { onVendorClick(vendor) },
                            onFavoriteToggle = { onFavoriteToggle(vendor) },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            sharedTransitionScope = sharedTransitionScope
                        )
                    }
                }
            } else {
                // Timeline implementation
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Timeline view coming soon")
                    }
                }
            }
        }
    }
}

@Composable
fun VendorCategoryGrid(categories: List<VendorCategoryItem>, onCategoryClick: (VendorCategoryItem) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val rows = categories.chunked(3)
        rows.forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    VendorTypeChip(label = item.name, icon = item.icon, isLarge = true, modifier = Modifier.weight(1f).clickable { onCategoryClick(item) })
                }
                if (rowItems.size < 3) repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun ExploreCategoriesHorizontal(categories: List<VendorCategoryItem>, onCategoryClick: (VendorCategoryItem) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(text = "Explore Categories", style = JasnifyTheme.typography.headingLarge, fontWeight = FontWeight.Medium, color = ContentPrimary, modifier = Modifier.padding(horizontal = 12.dp))
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { item ->
                VendorTypeChip(label = item.name, icon = item.icon, subLabel = "Explore Now", isLarge = false, modifier = Modifier.clickable { onCategoryClick(item) })
            }
        }
    }
}
