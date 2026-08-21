package com.harshdeep.jasnify.presentation.screens.moments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTabStyle
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.components.states.GenericLoadingState
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.presentation.viewmodels.MomentsViewModel
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.utils.TimeUtils
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.io.File
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

enum class MomentViewMode {
    AllPhotos,
    Folders,
    FolderImages
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MomentsScreen(
    eventId: String,
    onBackClick: () -> Unit,
    onManageRoomClick: () -> Unit,
    viewModel: MomentsViewModel
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("All Moments") }
    var viewMode by remember { mutableStateOf(MomentViewMode.AllPhotos) }
    var selectedMomentForFullView by remember { mutableStateOf<Moment?>(null) }
    var showFabMenu by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showCreateFolderSheet by remember { mutableStateOf(false) }
    var selectedFolderId by remember { mutableStateOf("") }
    var sheetMotionProgress by remember { mutableFloatStateOf(1.0f) }

    val foldersFromDb by viewModel.folders.collectAsStateWithLifecycle()
    val moments by viewModel.moments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val photosGridState = rememberLazyGridState()
    val foldersGridState = rememberLazyGridState()

    var isBottomTabVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }

    val isAnySheetVisible by remember {
        derivedStateOf { showFabMenu || showMoreMenu || showCreateFolderSheet }
    }

    val nestedScrollConnection = remember(viewMode, photosGridState, foldersGridState, isAnySheetVisible) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isAnySheetVisible || selectedMomentForFullView != null) return Offset.Zero

                val delta = available.y
                val activeGridState = when (viewMode) {
                    MomentViewMode.Folders -> foldersGridState
                    else -> photosGridState
                }

                val canScroll = activeGridState.canScrollForward || activeGridState.canScrollBackward
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

    if (isLoading && moments.isEmpty() && foldersFromDb.isEmpty()) {
        GenericLoadingState()
        return
    }

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

    val getTargetFolderId: () -> String = remember(selectedFolderId) {
        {
            if (selectedFolderId.isNotEmpty()) selectedFolderId else "all_moments_id"
        }
    }

    val isVideoUri: (Uri) -> Boolean = remember(context) {
        { uri ->
            val mimeType = context.contentResolver.getType(uri)
            mimeType?.startsWith("video", ignoreCase = true) == true
        }
    }

    val uploadUris: (List<Uri>) -> Unit = remember(eventId, viewModel, getTargetFolderId, isVideoUri) {
        { uris ->
            val targetId = getTargetFolderId()
            uris.forEach { uri ->
                val isVideo = isVideoUri(uri)
                viewModel.uploadMoment(eventId, targetId, uri, isVideo)
            }
        }
    }

    val createTempUri: () -> Uri = remember(context) {
        {
            val tempFile = File.createTempFile("moment_", ".jpg", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                uploadUris(uris)
            }
        }
    )

    val googlePhotosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
                val selectedUris = mutableListOf<Uri>()
                val clipData = result.data?.clipData
                val singleData = result.data?.data

                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        selectedUris.add(clipData.getItemAt(i).uri)
                    }
                } else if (singleData != null) {
                    selectedUris.add(singleData)
                }

                if (selectedUris.isNotEmpty()) {
                    uploadUris(selectedUris)
                }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempImageUri != null) {
                val targetId = getTargetFolderId()
                viewModel.uploadMoment(eventId, targetId, tempImageUri!!, false)
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

    val currentTitle = remember(viewMode, selectedFolderId, folders) {
        if (viewMode == MomentViewMode.FolderImages) {
            folders.find { it.id == selectedFolderId }?.name ?: "Moments"
        } else {
            "Moments"
        }
    }

    val galleryIconPainter = painterResource(R.drawable.ic_gallery)
    val fileIconPainter = painterResource(R.drawable.ic_file)
    val addIconPainter = rememberVectorPainter(Icons.Default.Add)
    val cameraIconPainter = painterResource(R.drawable.ic_camera)
    val uploadIconPainter = painterResource(R.drawable.ic_upload)
    val profileIconPainter = painterResource(R.drawable.ic_profile)
    val googleIconPainter = painterResource(R.drawable.ic_google)
    val heartIconPainter = painterResource(R.drawable.ic_top_bar_heart)
    val userProfileIconPainter = painterResource(R.drawable.ic_user_profile)

    val bottomTabItems = remember(galleryIconPainter, fileIconPainter) {
        listOf(
            TabItem(
                label = "",
                value = MomentViewMode.AllPhotos,
                icon = galleryIconPainter
            ),
            TabItem(
                label = "",
                value = MomentViewMode.Folders,
                icon = fileIconPainter
            )
        )
    }

    val addMenuItems = remember(
        cameraIconPainter,
        uploadIconPainter,
        fileIconPainter,
        profileIconPainter,
        googleIconPainter,
        context,
        photoPickerLauncher,
        googlePhotosPickerLauncher,
        cameraPermissionLauncher,
        cameraLauncher,
        createTempUri
    ) {
        listOf(
            listOf(
                MenuSheetActionItem(
                    text = "Camera",
                    icon = cameraIconPainter,
                    iconPlacement = IconPlacement.Top,
                    onClick = {
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
                    }
                ),
                MenuSheetActionItem(
                    text = "Upload",
                    icon = uploadIconPainter,
                    iconPlacement = IconPlacement.Top,
                    onClick = {
                        showFabMenu = false
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    }
                ),
                MenuSheetActionItem(
                    text = "Folder",
                    icon = fileIconPainter,
                    iconPlacement = IconPlacement.Top,
                    onClick = {
                        showFabMenu = false
                        showCreateFolderSheet = true
                    }
                )
            ),
            listOf(
                MenuSheetActionItem(
                    text = "iCloud",
                    icon = profileIconPainter,
                    iconPlacement = IconPlacement.Left,
                    onClick = {
                        showFabMenu = false
                        openICloudPhotos(context)
                    }
                )
            ),
            listOf(
                MenuSheetActionItem(
                    text = "Google Photos",
                    icon = googleIconPainter,
                    iconPlacement = IconPlacement.Left,
                    onClick = {
                        showFabMenu = false
                        pickMultiFromGooglePhotos(
                            context = context,
                            onLaunchPickerIntent = { intent ->
                                googlePhotosPickerLauncher.launch(intent)
                            },
                            onFallback = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            }
                        )
                    }
                )
            )
        )
    }

    val moreItems = remember(heartIconPainter, userProfileIconPainter, onManageRoomClick) {
        listOf(
            listOf(
                MenuSheetActionItem("Saved Cards", heartIconPainter) {
                    showMoreMenu = false
                }
            ),
            listOf(
                MenuSheetActionItem("Manage Room Access", userProfileIconPainter) {
                    showMoreMenu = false
                    onManageRoomClick()
                }
            )
        )
    }

    BackHandler {
        when {
            selectedMomentForFullView != null -> selectedMomentForFullView = null
            showFabMenu -> showFabMenu = false
            showMoreMenu -> showMoreMenu = false
            showCreateFolderSheet -> showCreateFolderSheet = false
            viewMode == MomentViewMode.FolderImages -> {
                viewMode = MomentViewMode.Folders
                selectedFolderId = ""
                viewModel.loadMoments(eventId, "")
            }
            else -> onBackClick()
        }
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = selectedMomentForFullView,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "MomentsScreenTransition"
        ) { targetMoment ->
            if (targetMoment != null) {
                MomentItemFullView(
                    moment = targetMoment,
                    transitionKey = "moment_${targetMoment.id}",
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onBackClick = { selectedMomentForFullView = null }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundPrimary)
                        .statusBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(nestedScrollConnection)
                    ) {
                        CustomTopBar(
                            title = currentTitle,
                            subtitle = if (viewMode == MomentViewMode.FolderImages) "${moments.size} items" else null,
                            buttonStyle = ButtonBackground.OPAQUE,
                            buttonColor = SurfaceSecondary,
                            menuIcon = TopIcon.Predefined.MENU_HORIZONTAL,
                            onBackClick = {
                                if (viewMode == MomentViewMode.FolderImages) {
                                    viewMode = MomentViewMode.Folders
                                    selectedFolderId = ""
                                    viewModel.loadMoments(eventId, "")
                                } else {
                                    onBackClick()
                                }
                            },
                            onMenuClick = { showMoreMenu = true }
                        )

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

                        Box(modifier = Modifier.weight(1f)) {
                            when (viewMode) {
                                MomentViewMode.AllPhotos, MomentViewMode.FolderImages -> {
                                    PhotosGrid(
                                        moments = moments,
                                        gridState = photosGridState,
                                        animatedVisibilityScope = this@AnimatedContent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        onMomentClick = { moment ->
                                            selectedMomentForFullView = moment
                                        }
                                    )
                                }
                                MomentViewMode.Folders -> {
                                    FoldersGrid(
                                        folders = folders,
                                        gridState = foldersGridState
                                    ) { folder ->
                                        selectedFolderId = folder.id
                                        viewMode = MomentViewMode.FolderImages
                                        viewModel.loadMoments(eventId, folder.id)
                                    }
                                }
                            }
                        }
                    }

                    // Floating Bottom Tab + Separate CustomIconButton (+)
                    AnimatedVisibility(
                        visible = isBottomTabVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 260)
                        ) + fadeIn(animationSpec = tween(durationMillis = 260)),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(durationMillis = 260)
                        ) + fadeOut(animationSpec = tween(durationMillis = 260)),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .zIndex(10f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(brush = BottomGradientBrush)
                                .navigationBarsPadding()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.wrapContentSize(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BottomTab(
                                    items = bottomTabItems,
                                    selectedValue = if (viewMode == MomentViewMode.FolderImages) MomentViewMode.Folders else viewMode,
                                    onItemSelected = { mode ->
                                        viewMode = mode
                                        if (mode == MomentViewMode.AllPhotos) {
                                            selectedFolderId = ""
                                            viewModel.loadMoments(eventId, "")
                                        }
                                    },
                                    style = BottomTabStyle.FLOATING,
                                    activeColor = ContentPrimary,
                                    activeBg = SurfaceSecondary
                                )

                                Surface(
                                    modifier = Modifier
                                        .size(64.dp)
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
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CustomIconButton(
                                            onClick = { showFabMenu = true },
                                            icon = addIconPainter,
                                            size = ButtonSize.Medium,
                                            type = ButtonType.Primary,
                                            shapeStyle = ButtonShapeStyle.Round,
                                            containerColor = ContentPrimary,
                                            contentColor = ContentInvPrimary,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .zIndex(20f)
                        )
                    }

                    if (showCreateFolderSheet) {
                        CreateFolderBottomSheet(
                            onDismiss = { showCreateFolderSheet = false },
                            onCreate = { folderName ->
                                viewModel.createFolder(eventId, folderName)
                                showCreateFolderSheet = false
                            },
                            onProgress = { sheetMotionProgress = it }
                        )
                    }

                    if (showFabMenu) {
                        MenuBottomSheet(
                            items = addMenuItems,
                            onCancelClick = { showFabMenu = false },
                            onProgress = { sheetMotionProgress = it }
                        )
                    }

                    if (showMoreMenu) {
                        MenuBottomSheet(
                            items = moreItems,
                            onCancelClick = { showMoreMenu = false },
                            onProgress = { sheetMotionProgress = it }
                        )
                    }
                }
            }
        }
    }
}

private fun pickMultiFromGooglePhotos(
    context: Context,
    onLaunchPickerIntent: (Intent) -> Unit,
    onFallback: () -> Unit
) {
    val googlePhotosPackage = "com.google.android.apps.photos"
    val pm = context.packageManager
    val isAppInstalled = try {
        pm.getPackageInfo(googlePhotosPackage, 0)
        true
    } catch (_: Exception) {
        false
    }

    if (isAppInstalled) {
        try {
            val pickIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                setPackage(googlePhotosPackage)
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            onLaunchPickerIntent(pickIntent)
            return
        } catch (_: Exception) {
            try {
                val actionPickIntent = Intent(Intent.ACTION_PICK).apply {
                    setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*,video/*")
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    setPackage(googlePhotosPackage)
                }
                onLaunchPickerIntent(actionPickIntent)
                return
            } catch (_: Exception) {
                // Fallback
            }
        }
    }

    try {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://photos.google.com/")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(webIntent)
    } catch (_: Exception) {
        onFallback()
    }
}

private fun openICloudPhotos(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, "https://www.icloud.com/photos".toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        android.util.Log.e("MomentsScreen", "Failed to open iCloud web: ${e.message}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    onProgress: (Float) -> Unit
) {
    CustomBottomSheet(
        heading = "Add new folder",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = false,
        showCloseButton = true
    ) {
        var folderName by remember { mutableStateOf(TextFieldValue("")) }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            delay(100.milliseconds)
            focusRequester.requestFocus()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryInput(
                value = folderName.text,
                onValueChange = { folderName = folderName.copy(text = it) },
                placeholder = "enter folder name",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .focusRequester(focusRequester)
            )

            CustomTextButton(
                onClick = {
                    if (folderName.text.isNotEmpty()) {
                        onCreate(folderName.text)
                    }
                },
                text = "Save",
                size = ButtonSize.Medium,
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotosGrid(
    moments: List<Moment>,
    gridState: LazyGridState = rememberLazyGridState(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onMomentClick: (Moment) -> Unit
) {
    if (moments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No moments yet", color = ContentSecondary)
        }
        return
    }

    val groupedMoments = remember(moments) {
        val dayGroups = moments.groupBy { moment ->
            val cal = Calendar.getInstance().apply { timeInMillis = moment.timestamp }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }

        dayGroups.values.associate { momentsInDay ->
            val latestMoment = momentsInDay.first()
            val timeHeader = TimeUtils.getTimeAgo(latestMoment.timestamp)
            timeHeader to momentsInDay
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        groupedMoments.forEach { (header, momentsInDate) ->
            item(span = { GridItemSpan(3) }) {
                Text(
                    header,
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(momentsInDate, key = { it.id }) { moment ->
                MomentItem(
                    moment = moment,
                    transitionKey = "moment_${moment.id}",
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    onClick = { onMomentClick(moment) }
                )
            }
        }
    }
}

@Composable
fun FoldersGrid(
    folders: List<MomentFolder>,
    gridState: LazyGridState = rememberLazyGridState(),
    onFolderClick: (MomentFolder) -> Unit
) {
    if (folders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No folders yet", color = ContentSecondary)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(folders, key = { it.id }) { folder ->
            FolderItem(folder, onClick = { onFolderClick(folder) })
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MomentItem(
    moment: Moment,
    transitionKey: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onClick: () -> Unit
) {
    val shape = SquircleShape(24.dp, CornerSmoothingDefault)

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = transitionKey),
                    animatedVisibilityScope = animatedVisibilityScope,
                    clipInOverlayDuringTransition = OverlayClip(shape)
                )
                .clip(shape)
                .clickable { onClick() }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(moment.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (moment.isVideo) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SurfaceSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_gallery),
                        contentDescription = null,
                        tint = ContentSecondary
                    )
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