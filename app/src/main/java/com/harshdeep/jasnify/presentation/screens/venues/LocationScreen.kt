package com.harshdeep.jasnify.presentation.screens.venues

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FamousCityChip
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sv.lib.squircleshape.SquircleShape
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LocationScreen(
    initialSearches: List<String>,
    currentAddress: String, // Hoisted global state (Simplified: "City, State")
    onAddressSelected: (String) -> Unit, // Callback to update global address state and pop back
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var text by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // local state to temporarily show geocoder feedback on the UI button row
    var exactAddressState by remember { mutableStateOf<String?>(null) }

    // Initialize Android Local Storage via SharedPreferences
    val sharedPrefs = remember {
        context.getSharedPreferences("jasnify_location_prefs", Context.MODE_PRIVATE)
    }

    // Persistent storage for the last known exact/full address to show in the picker
    var lastKnownFullAddress by remember {
        mutableStateOf(sharedPrefs.getString("exact_full_address_key", null))
    }

    // Load recent searches from Local Storage, using fallback default list if empty
    val recentSearches = remember {
        val savedString = sharedPrefs.getString("recent_searches_key", null)
        val initialList = if (!savedString.isNullOrEmpty()) {
            savedString.split("|||").filter { it.isNotBlank() }
        } else {
            initialSearches
        }
        mutableStateListOf<String>().apply { addAll(initialList) }
    }

    // Helper to persist the current state of recent searches list to local storage
    val saveRecentSearchesToStorage: (List<String>) -> Unit = { list ->
        sharedPrefs.edit()
            .putString("recent_searches_key", list.joinToString("|||"))
            .apply()
    }

    var selectedCityId by remember { mutableStateOf("") }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val searchDatabase = remember {
        listOf(
            "Hajipur, Bihar",
            "Patna, Bihar",
            "Muzaffarpur, Bihar",
        )
    }

    // Live filtering based on the search query
    val filteredSuggestions = remember(text) {
        if (text.isBlank()) {
            searchDatabase
        } else {
            searchDatabase.filter { it.contains(text, ignoreCase = true) }
        }
    }

    /**
     * Converts a detailed Address object into a simplified "Locality, State" string.
     */
    fun simplifyAddress(address: Address): String {
        val city = address.locality ?: address.subAdminArea ?: ""
        val state = address.adminArea ?: ""
        return when {
            city.isNotEmpty() && state.isNotEmpty() -> "$city, $state"
            city.isNotEmpty() -> city
            state.isNotEmpty() -> state
            else -> "Unknown Location"
        }
    }

    val handleLocationSelected: (String) -> Unit = { selectedAddress ->
        recentSearches.remove(selectedAddress)
        recentSearches.add(0, selectedAddress)

        // Trim history list to 10 items to save local memory
        if (recentSearches.size > 10) {
            recentSearches.removeLast()
        }

        // Persist to local SharedPreferences storage
        saveRecentSearchesToStorage(recentSearches)

        // Clear search inputs, close active search state, dismiss focus and bubble up selections
        text = ""
        isSearchActive = false
        focusManager.clearFocus()
        
        // Use a small delay before calling the callback to ensure stable navigation return
        coroutineScope.launch {
            delay(100.milliseconds)
            onAddressSelected(selectedAddress)
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocationAndResolveAddress() {
        exactAddressState = "Locating..."
        
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val addressObj = addresses[0]
                            val fullAddress = addressObj.getAddressLine(0) ?: ""
                            val simpleAddress = simplifyAddress(addressObj)

                            withContext(Dispatchers.Main) {
                                sharedPrefs.edit().putString("exact_full_address_key", fullAddress).apply()
                                lastKnownFullAddress = fullAddress
                                exactAddressState = null
                                handleLocationSelected(simpleAddress)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                exactAddressState = null
                                handleLocationSelected("Lat: ${location.latitude}, Lng: ${location.longitude}")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            exactAddressState = null
                            handleLocationSelected("Lat: ${location.latitude}, Lng: ${location.longitude}")
                        }
                    }
                }
            } else {
                // Request a fresh location update if last location is null
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .setMaxUpdates(1)
                    .build()
                
                fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val newLoc = locationResult.lastLocation
                        if (newLoc != null) {
                            fetchLocationAndResolveAddress() // retry once
                        } else {
                            exactAddressState = null
                            Toast.makeText(context, "Location not found. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }, context.mainLooper)
            }
        }.addOnFailureListener {
            exactAddressState = null
            Toast.makeText(context, "Failed to get location.", Toast.LENGTH_SHORT).show()
        }
    }

    val gpsResolutionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // User enabled GPS, automatically trigger location fetch
            // Add a slight delay to allow the system settings to propagate
            coroutineScope.launch {
                delay(300)
                fetchLocationAndResolveAddress()
            }
        }
    }

    fun checkSettingsAndFetchLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            fetchLocationAndResolveAddress()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    gpsResolutionLauncher.launch(intentSenderRequest)
                } catch (sendEx: Exception) {
                    // Ignore
                }
            } else {
                // If not resolvable, navigate to system settings
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            checkSettingsAndFetchLocation()
        } else {
            // Permission denied, redirect to app settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
    }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        text = ""
        focusManager.clearFocus()
    }

    data class City<T>(
        val name: String,
        val imageRes: Int,
        val value: T
    )

    val cities = listOf(
        City("Delhi NCR", R.drawable.ic_city_del, "delhi"),
        City("Bengaluru", R.drawable.ic_city_blr, "bengaluru"),
        City("Mumbai", R.drawable.ic_city_mum, "mumbai"),
        City("Hyderabad", R.drawable.ic_city_hyd, "hyderabad"),
        City("Chennai", R.drawable.ic_city_chn, "chennai"),
        City("Jaipur", R.drawable.ic_city_jpr, "jaipur"),
        City("Agra", R.drawable.ic_city_agr, "agra"),
        City("Kolkata", R.drawable.ic_city_kol, "kolkata"),
        City("Patna", R.drawable.ic_city_ptn, "patna")
    )


    Scaffold(modifier = Modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
                .padding(paddingValues)
                // Clear focus and hide the keyboard when tapping anywhere outside the SearchBar
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            // Smoothly collapse / show TopBar based on Search Bar active state
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CustomTopBar(
                    title = "Location",
                    onBackClick = onBackClick,
                    isLargeTitle = true,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        CustomSearchBar(
                            value = text,
                            onValueChange = { text = it },
                            onActiveChange = { isSearchActive = it },
                            modifier = Modifier.sharedBounds(
                                rememberSharedContentState(key = "location_picker"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    spring(
                                        dampingRatio = 0.85f,
                                        stiffness = 380f
                                    )
                                },
                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.FillWidth, Alignment.Center)
                            )
                        )
                    }
                } else {
                    CustomSearchBar(
                        value = text,
                        onValueChange = { text = it },
                        onActiveChange = { isSearchActive = it },
                        modifier = Modifier
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Background main content scroll layer
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                        LocationPicker(
                            exactLocationAddress = exactAddressState ?: lastKnownFullAddress ?: currentAddress,
                            onClick = {
                                val hasFinePermission = ActivityCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                                val hasCoarsePermission = ActivityCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasFinePermission || hasCoarsePermission) {
                                    checkSettingsAndFetchLocation()
                                } else {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        )
                    }

                    if (recentSearches.isNotEmpty()) {
                        Column(
                            modifier = Modifier.background(BackgroundPrimary),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Recent Searches",
                                    style = JasnifyTheme.typography.headingMedium,
                                    fontWeight = FontWeight.Medium
                                )

                                TextButton(
                                    onClick = {
                                        recentSearches.clear()
                                        saveRecentSearchesToStorage(emptyList())
                                    },
                                ) {
                                    Text(
                                        text = "Clear all",
                                        style = JasnifyTheme.typography.labelXLarge,
                                        color = ContentBrandDark
                                    )
                                }
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                items(recentSearches) { city ->
                                    FilterChip(
                                        label = city,
                                        trailingIcon = Icons.Default.Close,
                                        hasStroke = true,
                                        shapeStyle = ChipShapeStyle.Round,
                                        onTrailingIconClick = {
                                            recentSearches.remove(city)
                                            saveRecentSearchesToStorage(recentSearches)
                                        },
                                        onClick = {
                                            handleLocationSelected(city)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Popular Cities",
                        style = JasnifyTheme.typography.headingMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 4.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cities.chunked(3).forEach { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { city ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        FamousCityChip(
                                            cityName = city.name,
                                            cityImage = painterResource(id = city.imageRes),
                                            isSelected = selectedCityId == city.value,
                                            onClick = {
                                                selectedCityId = city.value
                                                handleLocationSelected(city.name)
                                            }
                                        )
                                    }
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

                androidx.compose.animation.AnimatedVisibility(
                    visible = isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundPrimary)
                            // Clear focus if tapping on the blank space in the overlay list
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    focusManager.clearFocus()
                                })
                            }
                    ) {
                        if (filteredSuggestions.isEmpty() && text.isNotBlank()) {
                            ListItem(
                                headlineContent = { Text("Search for \"$text\"") },
                                leadingContent = { Icon(painter = painterResource(R.drawable.ic_location_marker), contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { handleLocationSelected(text) },
                                colors = ListItemDefaults.colors(
                                    containerColor = BackgroundPrimary
                                ),
                            )
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredSuggestions) { suggestion ->
                                    ListItem(
                                        headlineContent = { Text(suggestion) },
                                        leadingContent = {
                                            Icon(
                                                painter = if (recentSearches.contains(suggestion)) painterResource(R.drawable.ic_clock_forward) else painterResource(R.drawable.ic_location_marker),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { handleLocationSelected(suggestion) },
                                        colors = ListItemDefaults.colors(
                                            containerColor = BackgroundPrimary
                                        ),
                                    )
                                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), modifier = Modifier.padding(horizontal = 12.dp))
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
fun LocationPicker(
    exactLocationAddress: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = ContentBrandDark,
                shape = SquircleShape(20.dp, CornerSmoothingDefault)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceBrandSecondary)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_target_location),
            contentDescription = "Location Icon",
            tint = ContentBrandDark,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Use Current Location",
                color = ContentBrandDark,
                style = JasnifyTheme.typography.bodyXLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = exactLocationAddress,
                color = ContentBrandDark,
                style = JasnifyTheme.typography.labelMedium,
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = ContentBrandDark,
            modifier = Modifier.size(24.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    var previewAddress by remember { mutableStateOf("Hajipur, Bihar") }
    JasnifyTheme {
        LocationScreen(
            initialSearches = listOf("Patna", "New Delhi", "Mumbai", "Pune", "Goa"),
            currentAddress = previewAddress,
            onAddressSelected = { previewAddress = it },
            onBackClick = {}
        )
    }
}
