package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.chip.VendorTypeChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
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

private fun clearRecentSearches(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit { remove(KEY_RECENT_SEARCHES) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsTab(
    mainNavController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    var recentSearchesNames by remember { mutableStateOf(getRecentSearches(context)) }

    // Observe selected location from NavBackStackEntry's savedStateHandle
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
    }

    LaunchedEffect(showMenuSheet, isSearchActive) {
        onBottomBarVisibilityChange(!showMenuSheet && !isSearchActive)
    }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        searchQuery = ""
        focusManager.clearFocus()
    }

    val targetScale = if (showMenuSheet) {
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
        targetValue = if (showMenuSheet) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

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
                    clip = showMenuSheet || backdropCornerRadius > 0.dp
                    shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Scaffold(
                topBar = {
                    Column(
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        if (isSearchActive) {
                            CustomTopBar(
                                title = "Search Vendors",
                                onBackClick = {
                                    isSearchActive = false
                                    searchQuery = ""
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
                                onMenuClick = { showMenuSheet = true },
                                onDropdownClick = { mainNavController.navigate(Screen.LocationSelector.route) },
                                isLargeTitle = true
                            )
                        }
                    }
                },
                containerColor = BackgroundPrimary
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding()),
                    contentPadding = PaddingValues(bottom = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CustomSearchBar(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            onActiveChange = { active -> isSearchActive = active },
                            placeholder = "Search Vendors",
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    if (!isSearchActive) {
                        item {
                            VendorCategoryGrid(categories = categories)
                        }

                        item {
                            OrDivider(
                                text = "EXPLORE",
                                dividerGap = 0.dp,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }

                        item {
                            VendorCarousel(
                                title = "Top Makeup Artists in $selectedCity",
                                vendors = MockData.sampleMakeupArtists,
                                onVendorClick = handleVendorClick,
                                onSeeAllClick = {}
                            )
                        }

                        item {
                            VendorCarousel(
                                title = "Best Photographers in $selectedCity",
                                vendors = MockData.samplePhotographers,
                                onVendorClick = handleVendorClick,
                                onSeeAllClick = {}
                            )
                        }

                        item {
                            VendorCarousel(
                                title = "Expert Mehendi Artists in $selectedCity",
                                vendors = MockData.sampleMehendiArtists,
                                onVendorClick = handleVendorClick,
                                onSeeAllClick = {}
                            )
                        }

                        item {
                            DashedDivider()
                            ExploreCategoriesHorizontal(categories = categories)
                        }

                        item {
                            FooterJansify()
                        }
                    } else {
                        item {
                            TrendingAiSearchesSection(
                                onTrendingClick = { query ->
                                    searchQuery = query
                                    focusManager.clearFocus()
                                }
                            )
                        }
                        if (recentVendorsList.isNotEmpty()) {
                            item {
                                RecentSearchesSection(
                                    recentVendors = recentVendorsList,
                                    onVendorClick = handleVendorClick,
                                    onRemoveVendor = { vendor ->
                                        val current = getRecentSearches(context).toMutableList()
                                        current.remove(vendor.name)
                                        val limited = current.take(8)
                                        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                                            putString(KEY_RECENT_SEARCHES, limited.joinToString("|||"))
                                        }
                                        recentSearchesNames = getRecentSearches(context)
                                    },
                                )
                            }
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }
        }

        if (showMenuSheet) {
            MenuBottomSheet(
                items = listOf(
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
                            onClick = {
                                showMenuSheet = false
                            }
                        )
                    ),
                    listOf(
                        MenuSheetActionItem(
                            text = "Help & Feedback",
                            icon = painterResource(R.drawable.ic_help_feedback),
                            iconPlacement = IconPlacement.Left,
                            onClick = {
                                showMenuSheet = false
                            }
                        )
                    )
                ),
                onCancelClick = { showMenuSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}

@Composable
fun VendorCategoryGrid(categories: List<VendorCategoryItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rows = categories.chunked(3)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    VendorTypeChip(
                        label = item.name,
                        icon = item.icon,
                        isLarge = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreCategoriesHorizontal(categories: List<VendorCategoryItem>) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Explore Categories",
            style = JasnifyTheme.typography.headingLarge,
            fontWeight = FontWeight.Medium,
            color = ContentPrimary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { item ->
                VendorTypeChip(
                    label = item.name,
                    icon = item.icon,
                    subLabel = "Explore Now",
                    isLarge = false
                )
            }
        }
    }
}
