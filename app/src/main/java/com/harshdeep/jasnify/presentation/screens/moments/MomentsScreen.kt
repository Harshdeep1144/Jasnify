package com.harshdeep.jasnify.presentation.screens.moments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.states.GenericLoadingState
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.viewmodels.MomentsViewModel
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MomentsScreen(
    eventId: String,
    onBackClick: () -> Unit,
    onManageRoomClick: () -> Unit,
    viewModel: MomentsViewModel
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("All Moments") }
    var viewMode by remember { mutableStateOf("Photos") } // "Photos" or "Folders"
    var showFabMenu by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var selectedFolderId by remember { mutableStateOf("") }

    val foldersFromDb by viewModel.folders.collectAsStateWithLifecycle()
    val moments by viewModel.moments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    if (isLoading) {
        GenericLoadingState()
        return
    }

    // Virtual "All Moments" folder
    val folders = remember(foldersFromDb, moments) {
        val allMomentsFolder = MomentFolder(
            id = "all_moments_id",
            name = "All Moments",
            coverImageUrl = moments.firstOrNull()?.imageUrl ?: "",
            itemCount = moments.size,
            isNew = false
        )
        listOf(allMomentsFolder) + foldersFromDb
    }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    fun getTargetFolderId(): String? {
        if (selectedFolderId.isNotEmpty()) return selectedFolderId
        // Default to "All Moments" (which will map to "General" or similar in repo)
        return "all_moments_id"
    }

    fun createTempUri(): Uri {
        val tempFile = File.createTempFile("moment_", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val targetId = getTargetFolderId()
                if (targetId != null) {
                    viewModel.uploadMoment(eventId, targetId, it, false)
                } else {
                    android.util.Log.e("MomentsScreen", "No folder found for upload")
                }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempImageUri != null) {
                val targetId = getTargetFolderId()
                if (targetId != null) {
                    viewModel.uploadMoment(eventId, targetId, tempImageUri!!, false)
                }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                try {
                    tempImageUri = createTempUri()
                    cameraLauncher.launch(tempImageUri!!)
                } catch (e: Exception) {
                    android.util.Log.e("MomentsScreen", "Failed to launch camera: ${e.message}")
                }
            }
        }
    )

    LaunchedEffect(eventId) {
        viewModel.loadFolders(eventId)
        viewModel.loadMoments(eventId)
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = if (selectedFolderId.isEmpty()) "Moments" else folders.find { it.id == selectedFolderId }?.name ?: "Moments",
                onBackClick = {
                    if (selectedFolderId.isNotEmpty()) {
                        selectedFolderId = ""
                        viewMode = "Folders"
                        viewModel.loadMoments(eventId, "")
                    } else {
                        onBackClick()
                    }
                },
                menuIcon = TopIcon.Predefined.MENU_HORIZONTAL,
                onMenuClick = { showMoreMenu = true }
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All Moments", "Recent First", "Oldest First").forEach { tab ->
                        FilterChip(
                            label = tab,
                            isSelected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            shapeStyle = ChipShapeStyle.Round
                        )
                    }
                }

                if (viewMode == "Photos") {
                    PhotosGrid(moments)
                } else {
                    FoldersGrid(folders) { folder ->
                        selectedFolderId = folder.id
                        viewMode = "Photos"
                        viewModel.loadMoments(eventId, folder.id)
                    }
                }
            }

            // Bottom Switcher and FAB
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Photos/Folders Switcher
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(100))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { 
                                viewMode = "Photos" 
                                selectedFolderId = ""
                                viewModel.loadMoments(eventId, "")
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (viewMode == "Photos") SurfaceSecondary else Color.Transparent)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_gallery),
                                contentDescription = "Photos",
                                tint = if (viewMode == "Photos") ContentPrimary else ContentSecondary
                            )
                        }
                        IconButton(
                            onClick = { viewMode = "Folders" },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (viewMode == "Folders") SurfaceSecondary else Color.Transparent)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_file),
                                contentDescription = "Folders",
                                tint = if (viewMode == "Folders") ContentPrimary else ContentSecondary
                            )
                        }
                    }

                    // FAB
                    FloatingActionButton(
                        onClick = { showFabMenu = true },
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        if (showCreateFolderDialog) {
            AlertDialog(
                onDismissRequest = { showCreateFolderDialog = false },
                title = { Text("Create Folder") },
                text = {
                    TextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        placeholder = { Text("Folder Name") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newFolderName.isNotEmpty()) {
                            viewModel.createFolder(eventId, newFolderName)
                            showCreateFolderDialog = false
                            newFolderName = ""
                        }
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateFolderDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showFabMenu) {
            val fabItems = listOf(
                listOf(
                    MenuSheetActionItem("Camera", painterResource(R.drawable.ic_camera), iconPlacement = IconPlacement.Top) { 
                        showFabMenu = false 
                        val hasCameraPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasCameraPermission) {
                            try {
                                tempImageUri = createTempUri()
                                cameraLauncher.launch(tempImageUri!!)
                            } catch (e: Exception) {
                                android.util.Log.e("MomentsScreen", "Failed to launch camera: ${e.message}")
                            }
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    MenuSheetActionItem("Upload", painterResource(R.drawable.ic_upload), iconPlacement = IconPlacement.Top) { 
                        showFabMenu = false 
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                    },
                    MenuSheetActionItem("Folder", painterResource(R.drawable.ic_file), iconPlacement = IconPlacement.Top) { 
                        showFabMenu = false 
                        showCreateFolderDialog = true
                    }
                ),
                listOf(
                    MenuSheetActionItem("Import from other places", painterResource(R.drawable.ic_download)) { showFabMenu = false }
                )
            )
            MenuBottomSheet(items = fabItems, onCancelClick = { showFabMenu = false })
        }

        if (showMoreMenu) {
            val moreItems = listOf(
                listOf(
                    MenuSheetActionItem("Saved Cards", painterResource(R.drawable.ic_top_bar_heart)) { showMoreMenu = false }
                ),
                listOf(
                    MenuSheetActionItem("Manage Room Access", painterResource(R.drawable.ic_user_profile)) { 
                        showMoreMenu = false
                        onManageRoomClick()
                    }
                )
            )
            MenuBottomSheet(items = moreItems, onCancelClick = { showMoreMenu = false })
        }
    }
}

@Composable
fun PhotosGrid(moments: List<Moment>) {
    if (moments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No moments yet", color = ContentSecondary)
        }
        return
    }

    val groupedMoments = remember(moments) {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }

        val displaySdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val timeSdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Group by a unique day key
        val dayGroups = moments.groupBy { moment ->
            val cal = Calendar.getInstance().apply { timeInMillis = moment.timestamp }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }

        // Map to display strings: "Day, Time" (Time from the latest moment in group)
        dayGroups.values.associate { momentsInDay ->
            val latestMoment = momentsInDay.first() // Already sorted descending
            val cal = Calendar.getInstance().apply { timeInMillis = latestMoment.timestamp }

            val dayPart = when {
                isSameDay(cal, today) -> "Today"
                isSameDay(cal, yesterday) -> "Yesterday"
                else -> displaySdf.format(Date(latestMoment.timestamp))
            }
            val timePart = timeSdf.format(Date(latestMoment.timestamp))

            "$dayPart, $timePart" to momentsInDay
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        groupedMoments.forEach { (header, momentsInDate) ->
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
                Text(
                    header,
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(momentsInDate) { moment ->
                MomentItem(moment)
            }
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun FoldersGrid(folders: List<MomentFolder>, onFolderClick: (MomentFolder) -> Unit) {
    if (folders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No folders yet", color = ContentSecondary)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(folders) { folder ->
            FolderItem(folder, onClick = { onFolderClick(folder) })
        }
    }
}

@Composable
fun MomentItem(moment: Moment) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(SquircleShape(20.dp, CornerSmoothingDefault))
    ) {
        AsyncImage(
            model = moment.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (moment.isVideo) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun FolderItem(folder: MomentFolder, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(SquircleShape(24.dp, CornerSmoothingDefault))
        ) {
            if (folder.coverImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = folder.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(SurfaceSecondary), contentAlignment = Alignment.Center) {
                    Icon(painterResource(R.drawable.ic_gallery), contentDescription = null, tint = ContentSecondary)
                }
            }
            if (folder.isNew) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.White, RoundedCornerShape(100))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        "NEW",
                        style = JasnifyTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            folder.name,
            style = JasnifyTheme.typography.headingMedium,
            maxLines = 1
        )
        Text(
            "${folder.itemCount} items",
            style = JasnifyTheme.typography.labelMedium,
            color = ContentSecondary
        )
    }
}
