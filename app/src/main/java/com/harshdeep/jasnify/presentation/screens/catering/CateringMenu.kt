package com.harshdeep.jasnify.presentation.screens.catering

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomSuccessBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomDeleteSheet
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.presentation.components.filter.FilterBottomSheet
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.CateringItemChip
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.components.chip.FoodChip
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.inputfield.CornerType
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

data class MenuItem(
    val name: String,
    val dietary: Dietary,
    val type: String,
    val cuisine: String = "Indian"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CateringMenuScreen(
    onBackClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        focusManager.clearFocus()
    }

    var selectedFilterTab by remember { mutableStateOf("All Items") }

    // --- State-driven Custom Toast Setup ---
    var toastData by remember { mutableStateOf(ToastData()) }
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(2000L.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    // --- Bottom Sheet Details States ---
    var selectedItemForDetails by remember { mutableStateOf<MenuItem?>(null) }
    val detailsBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDetailsBottomSheet by remember { mutableStateOf(false) }

    // --- Delete Confirmation Bottom Sheet States ---
    var showDeleteConfirmationSheet by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<MenuItem?>(null) }
    val deleteBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // --- Filter Bottom Sheets States ---
    val cuisineBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val typeBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCuisineBottomSheet by remember { mutableStateOf(false) }
    var showTypeBottomSheet by remember { mutableStateOf(false) }

    // --- Add/Edit Catering Item Bottom Sheets States ---
    var showAddItemSheet by remember { mutableStateOf(false) }
    val addItemBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSuccessSheet by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Edit item tracker
    var editingItem by remember { mutableStateOf<MenuItem?>(null) }

    // Add/Edit item form state variables
    var newItemName by remember { mutableStateOf("") }
    var newItemCuisine by remember { mutableStateOf("Indian") }
    var newItemType by remember { mutableStateOf("Starters") }
    var newItemDietary by remember { mutableStateOf(Dietary.Veg) }

    // Dedicated Add Form Bottom Sheets for Cuisine & Type selection
    var showAddCuisineBottomSheet by remember { mutableStateOf(false) }
    var showAddTypeBottomSheet by remember { mutableStateOf(false) }

    // Selected state trackers for specific filters
    var selectedCuisines by remember { mutableStateOf(emptySet<String>()) }
    var selectedTypes by remember { mutableStateOf(emptySet<String>()) }

    val allMenuItems = remember {
        mutableStateListOf(
            // Starters
            MenuItem("Chicken Malai Tikka", Dietary.NonVeg, "Starters", "Mughlai"),
            MenuItem("Crispy Chilli Potato", Dietary.Veg, "Starters", "Chinese"),
            MenuItem("Mutton Seekh Kebab", Dietary.NonVeg, "Starters", "Mughlai"),
            MenuItem("Amritsari Fish Fry", Dietary.NonVeg, "Starters", "Punjabi"),
            MenuItem("Hara Bhara Kebab", Dietary.Veg, "Starters", "North Indian"),
            MenuItem("Chicken 65", Dietary.NonVeg, "Starters", "South Indian"),
            MenuItem("Cheese Corn Balls", Dietary.Veg, "Starters", "Continental"),
            MenuItem("Garlic Butter Prawns", Dietary.NonVeg, "Starters", "Continental"),

            // Beverages
            MenuItem("Mango Lassi", Dietary.Veg, "Beverages", "Punjabi"),
            MenuItem("Masala Lemonade", Dietary.Veg, "Beverages", "North Indian"),
            MenuItem("Virgin Mojito", Dietary.Veg, "Beverages", "Italian"),
            MenuItem("Cold Coffee with Ice Cream", Dietary.Veg, "Beverages", "Continental"),
            MenuItem("Iced Peach Tea", Dietary.Veg, "Beverages", "Continental"),
            MenuItem("Blue Lagoon Mocktail", Dietary.Veg, "Beverages", "Continental"),

            // Desserts
            MenuItem("Warm Chocolate Brownie", Dietary.Veg, "Desserts", "Continental"),
            MenuItem("Kesari Phirni", Dietary.Veg, "Desserts", "North Indian"),
            MenuItem("Tiramisu Cups", Dietary.Veg, "Desserts", "Italian"),
            MenuItem("Fresh Fruit Cream", Dietary.Veg, "Desserts", "Continental"),
            MenuItem("Shahi Tukda", Dietary.Veg, "Desserts", "Awadhi"),
            MenuItem("Vanilla Bean Ice Cream", Dietary.Veg, "Desserts", "Continental")
        )
    }

    val cuisineOptions by remember {
        derivedStateOf {
            (listOf("Indian", "Japanese", "Mexican", "Italian", "Chinese", "French", "Thai", "Korean") +
                    allMenuItems.map { it.cuisine }).distinct().sorted()
        }
    }

    val typeOptions by remember {
        derivedStateOf {
            (listOf("Starters", "Beverages", "Main Course", "Desserts") +
                    allMenuItems.map { it.type }).distinct().sorted()
        }
    }

    val filteredItems by remember {
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

    val categorizedItems by remember {
        derivedStateOf {
            filteredItems.groupBy { it.type }
        }
    }

    val searchBarParentBg by animateColorAsState(
        targetValue = if (isSearchActive) SurfaceBrandPrimary else Color.Transparent,
        animationSpec = tween(durationMillis = 250),
        label = "SearchBarParent_Bg"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = tween(durationMillis = 250))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceBrandPrimary)
                            .animateContentSize(animationSpec = tween(durationMillis = 250))
                    ) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsTopHeight(WindowInsets.statusBars)
                        )

                        AnimatedVisibility(
                            visible = !isSearchActive,
                            enter = fadeIn(animationSpec = tween(150)) + expandVertically(animationSpec = tween(250)),
                            exit = fadeOut(animationSpec = tween(100)) + shrinkVertically(animationSpec = tween(250))
                        ) {
                            CustomTopBar(
                                title = "Catering Menu",
                                onBackClick = {
                                    focusManager.clearFocus()
                                    onBackClick()
                                },
                                onMenuClick = { focusManager.clearFocus() },
                                isLargeTitle = true,
                                buttonStyle = ButtonBackground.TRANSLUCENT,
                                textColor = ContentInvPrimary,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(searchBarParentBg)
                            .padding(
                                top = 12.dp,
                                bottom = if (isSearchActive) 12.dp else 0.dp,
                                start = 12.dp,
                                end = 12.dp
                            )
                    ) {
                        CustomSearchBar(
                            value = searchText,
                            onValueChange = { searchText = it },
                            onActiveChange = { isSearchActive = it }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundPrimary)
                    // Clear focus when tapping anywhere on the main empty background
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        state = rememberLazyListState(),
                        contentPadding = PaddingValues(horizontal = 12.dp),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    if (categorizedItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "No items match your filters",
                                    style = JasnifyTheme.typography.headingMedium,
                                    color = ContentSecondary
                                )
                                Text(
                                    text = "Try adjusting your search query or categories.",
                                    style = JasnifyTheme.typography.bodyMedium,
                                    color = ContentTertiary
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(
                                bottom = 80.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            categorizedItems.forEach { (category, items) ->
                                item {
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
                                }
                            }

                            item {
                                FooterJansify()
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Transparent,
                                    0.55f to Color.Transparent,
                                    0.85f to BackgroundPrimary.copy(alpha = 0.85f),
                                    1.0f to BackgroundPrimary
                                )
                            )
                        )
                        .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextButton(
                            onClick = {
                                focusManager.clearFocus()
                                /* Handle Suggestions */
                            },
                            text = "AI Suggestions",
                            type = ButtonType.Secondary,
                            shapeStyle = ButtonShapeStyle.Round,
                            leadingIcon = painterResource(id = R.drawable.ic_ai),
                        )
                        Spacer(Modifier.width(8.dp))

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

        // --- Screen-level CustomToast Display (Shown ONLY when Bottom Sheets are hidden) ---
        AnimatedVisibility(
            visible = toastData.message != null && !showAddItemSheet && !showDetailsBottomSheet && !showDeleteConfirmationSheet,
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

    // --- Detail Bottom Sheet ---
    if (showDetailsBottomSheet && selectedItemForDetails != null) {
        CustomBottomSheet(
            heading = "Item Details",
            sheetState = detailsBottomSheetState,
            sheetHeight = 340.dp,
            onDismiss = {
                focusManager.clearFocus()
                showDetailsBottomSheet = false
                selectedItemForDetails = null
            }
        ) {
            ItemDetailsSheetContent(
                item = selectedItemForDetails!!,
                onDeleteClick = {
                    focusManager.clearFocus()
                    itemToDelete = selectedItemForDetails
                    showDetailsBottomSheet = false
                    showDeleteConfirmationSheet = true
                },
                onEditClick = {
                    focusManager.clearFocus()
                    // Populate existing details into state variables
                    editingItem = selectedItemForDetails
                    newItemName = selectedItemForDetails?.name ?: ""
                    newItemCuisine = selectedItemForDetails?.cuisine ?: "Indian"
                    newItemType = selectedItemForDetails?.type ?: "Starters"
                    newItemDietary = selectedItemForDetails?.dietary ?: Dietary.Veg

                    // Close details view, open edit form directly
                    showDetailsBottomSheet = false
                    showAddItemSheet = true
                }
            )
        }
    }

    // --- Delete Confirmation Bottom Sheet ---
    if (showDeleteConfirmationSheet && itemToDelete != null) {
        CustomDeleteSheet(
            heading = "Remove item?",
            subHeading = "The item will be removed from the Catering Menu.",
            onDismiss = {
                focusManager.clearFocus()
                showDeleteConfirmationSheet = false
                itemToDelete = null
            },
            onConfirmRemove = {
                focusManager.clearFocus()
                allMenuItems.remove(itemToDelete)
                showDeleteConfirmationSheet = false
                itemToDelete = null
                toastData = ToastData("Item removed from menu", ToastType.SUCCESS)
            }
        )
    }

    // --- Cuisine Filter Bottom Sheet ---
    if (showCuisineBottomSheet) {
        FilterBottomSheet(
            title = "Select Cuisine",
            sheetState = cuisineBottomSheetState,
            options = cuisineOptions,
            initialSelectedOptions = selectedCuisines,
            showSearchBar = true,
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

    // --- Type Filter Bottom Sheet ---
    if (showTypeBottomSheet) {
        FilterBottomSheet(
            title = "Select Type",
            sheetState = typeBottomSheetState,
            options = typeOptions,
            initialSelectedOptions = selectedTypes,
            showSearchBar = false,
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

    // --- Add/Edit Catering Item Bottom Sheet ---
    if (showAddItemSheet) {

        ModalBottomSheet(
            onDismissRequest = { showAddItemSheet = false },
            sheetState = addItemBottomSheetState,
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
            dragHandle = null,
            sheetGesturesEnabled = true
        ) {
            // --- Custom Window Setup for Sheet's Dialog Window ---
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

            // Aligns components and positions the custom toast correctly above the sheet container
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Aligns right above the sheet container ---
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

                // Actual visible bottom sheet container layout
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(999f)
                        .clip(SquircleShape(CornerExtraLarge, CornerExtraLarge, 0.dp, 0.dp))
                        .background(SurfacePrimary)
                ) {
                    // Title and Close Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(12.dp, 12.dp, 12.dp, 0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (editingItem != null) "Edit menu item" else "Add an item to menu",
                            style = JasnifyTheme.typography.displayLarge,
                            color = ContentPrimary
                        )

                        TopBarIconButton(
                            backgroundStyle = ButtonBackground.OPAQUE,
                            icon = TopIcon.Predefined.CLOSE,
                            onClick = {
                                coroutineScope.launch { addItemBottomSheetState.hide() }.invokeOnCompletion {
                                    showAddItemSheet = false
                                }
                            }
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
                                val updatedOrNewItem = MenuItem(
                                    name = newItemName,
                                    dietary = newItemDietary,
                                    type = newItemType,
                                    cuisine = newItemCuisine
                                )

                                successMessage = if (editingItem != null) {
                                    "Item has been updated"
                                } else {
                                    "Item added to menu"
                                }

                                if (editingItem != null) {
                                    val editIndex = allMenuItems.indexOf(editingItem)
                                    if (editIndex != -1) {
                                        allMenuItems[editIndex] = updatedOrNewItem
                                    }
                                } else {
                                    allMenuItems.add(updatedOrNewItem)
                                }

                                showAddItemSheet = false
                                showSuccessSheet = true

                                newItemName = ""
                                newItemCuisine = "Indian"
                                newItemType = "Starters"
                                newItemDietary = Dietary.Veg
                                editingItem = null
                            } else {
                                // Trigger validation warning toast
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
    }

    // --- Success Bottom Sheet ---
    if (showSuccessSheet) {
        CustomSuccessBottomSheet(
            message = successMessage,
            onDismiss = {
                focusManager.clearFocus()
                showSuccessSheet = false
            }
        )
    }

    // --- Dynamic Form Cuisine bottom selector sheet ---
    if (showAddCuisineBottomSheet) {
        FilterBottomSheet(
            title = "Select Cuisine",
            sheetState = cuisineBottomSheetState,
            options = cuisineOptions,
            initialSelectedOptions = if (newItemCuisine.isNotEmpty()) setOf(newItemCuisine) else emptySet(),
            showSearchBar = true,
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

    // --- Dynamic Form Type bottom selector sheet ---
    if (showAddTypeBottomSheet) {
        FilterBottomSheet(
            title = "Select Type",
            sheetState = typeBottomSheetState,
            options = typeOptions,
            initialSelectedOptions = if (newItemType.isNotEmpty()) setOf(newItemType) else emptySet(),
            showSearchBar = false,
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
}


// --------------- Category Card components & other helpers ---------------------

@Composable
fun MenuCategoryCard(
    categoryTitle: String,
    items: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Card(
        modifier = modifier.fillMaxWidth()
            .border(width = 1.dp, color = ContentBrand, shape = SquircleShape(28.dp)),
        shape = SquircleShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundBrand
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Clear focus when tapping empty areas of the category card
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
                    modifier = Modifier.fillMaxWidth()
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
                    verticalArrangement = Arrangement.spacedBy(0.0.dp)
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
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), shape = SquircleShape(20.dp)),
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

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
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

    // Set up a scroll state to handle viewport scaling when keyboard opens or screen is small.
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 300))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        // Scrollable column with optional max height weight to prevent button compression
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(scrollState)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Item Name Input using custom PrimaryInput
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
                    cornerType = CornerType.DEFAULT,
                    textStyle = MaterialTheme.typography.labelLarge.copy(color = ContentPrimary)
                )
            }

            // Animating the entry/exit of Cuisine and Type sections to match height adjustments
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
                                cornerType = CornerType.DEFAULT,
                                trailingIconEnabled = true,
                                textStyle = MaterialTheme.typography.labelLarge.copy(color = ContentPrimary)
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

                    // Type Selection Block using PrimaryInput with overlay intercepts
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
                                cornerType = CornerType.DEFAULT,
                                textStyle = MaterialTheme.typography.labelLarge.copy(color = ContentPrimary),
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

            // Veg / Non-Veg Chip selector
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

        // Fixed non-shrinking action bar bottom area
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