package com.harshdeep.jasnify.presentation.screens.moments

import android.Manifest
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
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
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTabStyle
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.components.states.GenericLoadingState
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.presentation.viewmodels.MomentsViewModel
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material3.ExperimentalMaterial3Api

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
    var viewMode by remember { mutableStateOf(MomentViewMode.AllPhotos) }
    var selectedMomentForFullView by remember { mutableStateOf<Moment?>(null) }
    var showFabMenu by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showCreateFolderSheet by remember { mutableStateOf(false) }
    var selectedFolderId by remember { mutableStateOf("") }
    var folderNavigationStack by remember { mutableStateOf(listOf<MomentFolder>()) }

    var sheetMotionProgress by remember { mutableFloatStateOf(1.0f) }
    var selectedMomentIds by remember { mutableStateOf(setOf<String>()) }
    var showDeleteMomentConfirmation by remember { mutableStateOf(false) }
    var showDeleteFolderConfirmation by remember { mutableStateOf(false) }

    val isSelectionMode by remember { derivedStateOf { selectedMomentIds.isNotEmpty() } }

    val foldersFromDb by viewModel.folders.collectAsStateWithLifecycle()
    val moments by viewModel.moments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()

    val canAddContent = remember(userRole) { userRole == UserRole.OWNER || userRole == UserRole.EDITOR }
    val canDeleteAny = remember(userRole) { userRole == UserRole.OWNER }
    val canDeleteOwn = remember(userRole) { userRole == UserRole.EDITOR }

    val isAnySheetVisible by remember {
        derivedStateOf { 
            showFabMenu || showMoreMenu || showCreateFolderSheet || 
            showDeleteMomentConfirmation || showDeleteFolderConfirmation 
        }
    }

    LaunchedEffect(isAnySheetVisible) {
        if (!isAnySheetVisible) {
            sheetMotionProgress = 1.0f
        }
    }

    val statusBarColor by animateColorAsState(
        targetValue = if (isAnySheetVisible) Color.Black.copy(alpha = 0.4f) else Color.Transparent,
        animationSpec = tween(300),
        label = "statusBarColor"
    )

    val photosGridState = rememberLazyGridState()
    val foldersGridState = rememberLazyGridState()

    var isBottomTabVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }

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

    val folders = remember(foldersFromDb, moments, selectedFolderId) {
        // Only show "All Moments" at the root level (when no folder is selected)
        if (selectedFolderId.isEmpty()) {
            val allMomentsFolder = MomentFolder(
                id = "all_moments_id",
                name = "All Moments",
                coverImageUrl = moments.firstOrNull()?.imageUrl ?: "",
                itemCount = moments.size,
                isNew = false
            )
            listOf(allMomentsFolder) + foldersFromDb
        } else {
            foldersFromDb
        }
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

    LaunchedEffect(eventId, selectedFolderId) {
        // If selectedFolderId is empty, we are at root.
        // Otherwise, we are looking at content of selectedFolderId.
        viewModel.loadFolders(eventId, selectedFolderId)
        viewModel.loadMoments(eventId, selectedFolderId)
    }

    val currentTitle = remember(viewMode, selectedFolderId, folderNavigationStack) {
        when {
            viewMode == MomentViewMode.FolderImages && selectedFolderId == "all_moments_id" -> "All Moments"
            viewMode == MomentViewMode.FolderImages && folderNavigationStack.isNotEmpty() -> folderNavigationStack.last().name
            else -> "Moments"
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
    val deleteIconPainter = painterResource(R.drawable.ic_delete)

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

    val moreItems = remember(heartIconPainter, userProfileIconPainter, deleteIconPainter, onManageRoomClick, viewMode, selectedFolderId, folders, folderNavigationStack, canDeleteAny, canDeleteOwn, currentUserId) {
        val baseItems = mutableListOf<List<MenuSheetActionItem>>()
        
        baseItems.add(listOf(
            MenuSheetActionItem("Saved Cards", heartIconPainter) {
                showMoreMenu = false
            }
        ))
        
        // Only owners can manage room access
        if (canDeleteAny) {
            baseItems.add(listOf(
                MenuSheetActionItem("Manage Room Access", userProfileIconPainter) {
                    showMoreMenu = false
                    onManageRoomClick()
                }
            ))
        }
        
        // Find the current folder object. 
        // If we're inside a folder, it's either the last in stack or in the folders list (for all moments)
        val currentFolderObj = if (selectedFolderId == "all_moments_id") {
            folders.find { it.id == "all_moments_id" }
        } else {
            folderNavigationStack.lastOrNull()
        }
        
        val canDeleteThisFolder = currentFolderObj != null && 
                                 currentFolderObj.id != "all_moments_id" && 
                                 (canDeleteAny || (canDeleteOwn && currentFolderObj.uploaderId == currentUserId))

        if (viewMode == MomentViewMode.FolderImages && canDeleteThisFolder) {
            baseItems.add(listOf(
                MenuSheetActionItem("Delete Folder", deleteIconPainter) {
                    showMoreMenu = false
                    showDeleteFolderConfirmation = true
                }
            ))
        }
        
        baseItems
    }

    BackHandler {
        when {
            selectedMomentForFullView != null -> selectedMomentForFullView = null
            showFabMenu -> showFabMenu = false
            showMoreMenu -> showMoreMenu = false
            showCreateFolderSheet -> showCreateFolderSheet = false
            folderNavigationStack.isNotEmpty() -> {
                val newStack = folderNavigationStack.dropLast(1)
                folderNavigationStack = newStack
                selectedFolderId = newStack.lastOrNull()?.id ?: ""
                if (selectedFolderId.isEmpty()) {
                    viewMode = MomentViewMode.Folders
                }
            }
            viewMode == MomentViewMode.FolderImages -> {
                viewMode = MomentViewMode.Folders
                selectedFolderId = ""
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
                        .background(Color.Black)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val scale = if (isAnySheetVisible) 0.92f + (sheetMotionProgress * 0.08f) else 1.0f
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(SquircleShape(if (isAnySheetVisible && sheetMotionProgress < 1f) 32.dp else 0.dp, CornerSmoothingDefault))
                            .background(BackgroundPrimary)
                            .statusBarsPadding()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(nestedScrollConnection)
                        ) {
                            CustomTopBar(
                                title = if (isSelectionMode) "${selectedMomentIds.size} selected" else currentTitle,
                                subtitle = if (!isSelectionMode && viewMode == MomentViewMode.FolderImages) "${moments.size} items" else null,
                                buttonStyle = ButtonBackground.OPAQUE,
                                buttonColor = SurfaceSecondary,
                                menuIcon = if (isSelectionMode) {
                                    val canDeleteSelection = moments.filter { it.id in selectedMomentIds }.all { 
                                        canDeleteAny || (canDeleteOwn && it.uploaderId == currentUserId)
                                    }
                                    if (canDeleteSelection) TopIcon.CustomPainter(deleteIconPainter) else TopIcon.Predefined.MENU_HORIZONTAL
                                } else TopIcon.Predefined.MENU_HORIZONTAL,
                                onBackClick = {
                                    if (isSelectionMode) {
                                        selectedMomentIds = emptySet()
                                    } else if (selectedMomentForFullView != null) {
                                        selectedMomentForFullView = null
                                    } else if (folderNavigationStack.isNotEmpty()) {
                                        val newStack = folderNavigationStack.dropLast(1)
                                        folderNavigationStack = newStack
                                        selectedFolderId = newStack.lastOrNull()?.id ?: ""
                                        if (selectedFolderId.isEmpty()) {
                                            viewMode = MomentViewMode.Folders
                                        }
                                    } else if (viewMode == MomentViewMode.FolderImages) {
                                        viewMode = MomentViewMode.Folders
                                        selectedFolderId = ""
                                    } else {
                                        onBackClick()
                                    }
                                },
                                onMenuClick = {
                                    if (isSelectionMode) {
                                        val canDeleteSelection = moments.filter { it.id in selectedMomentIds }.all { 
                                            canDeleteAny || (canDeleteOwn && it.uploaderId == currentUserId)
                                        }
                                        if (canDeleteSelection) {
                                            showDeleteMomentConfirmation = true
                                        } else {
                                            // Optional: Show a toast or feedback that some items can't be deleted
                                            showMoreMenu = true 
                                        }
                                    } else {
                                        showMoreMenu = true
                                    }
                                }
                            )

                            Box(modifier = Modifier.weight(1f)) {
                                when (viewMode) {
                                    MomentViewMode.AllPhotos, MomentViewMode.FolderImages -> {
                                        PhotosGrid(
                                            moments = moments,
                                            subfolders = foldersFromDb,
                                            gridState = photosGridState,
                                            animatedVisibilityScope = this@AnimatedContent,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            selectedMomentIds = selectedMomentIds,
                                            onMomentClick = { moment ->
                                                if (isSelectionMode) {
                                                    val canDeleteThis = canDeleteAny || (canDeleteOwn && moment.uploaderId == currentUserId)
                                                    if (canDeleteThis) {
                                                        selectedMomentIds = if (selectedMomentIds.contains(moment.id)) {
                                                            selectedMomentIds - moment.id
                                                        } else {
                                                            selectedMomentIds + moment.id
                                                        }
                                                    }
                                                } else {
                                                    selectedMomentForFullView = moment
                                                }
                                            },
                                            onMomentLongClick = { moment ->
                                                val canDeleteThis = canDeleteAny || (canDeleteOwn && moment.uploaderId == currentUserId)
                                                if (canDeleteThis) {
                                                    selectedMomentIds = selectedMomentIds + moment.id
                                                }
                                            },
                                            onFolderClick = { folder ->
                                                selectedFolderId = folder.id
                                                folderNavigationStack = folderNavigationStack + folder
                                                viewMode = MomentViewMode.FolderImages
                                            }
                                        )
                                    }
                                    MomentViewMode.Folders -> {
                                        FoldersGrid(
                                            folders = folders,
                                            gridState = foldersGridState
                                        ) { folder ->
                                            if (folder.id == "all_moments_id") {
                                                selectedFolderId = "all_moments_id"
                                                viewMode = MomentViewMode.FolderImages
                                            } else {
                                                selectedFolderId = folder.id
                                                folderNavigationStack = folderNavigationStack + folder
                                                viewMode = MomentViewMode.FolderImages
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Floating Bottom Tab + Separate CustomIconButton (+)
                    AnimatedVisibility(
                        visible = isBottomTabVisible && sheetMotionProgress == 1.0f,
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

                                if (canAddContent) {
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
                                // If we are inside a folder, create it as a subfolder
                                viewModel.createFolder(eventId, folderName, selectedFolderId)
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

                    if (showDeleteMomentConfirmation) {
                        ConfirmationBottomSheet(
                            heading = "Delete selected moments?",
                            subHeading = "These items will be permanently deleted from the room.",
                            confirmButtonText = "Delete",
                            onDismiss = { showDeleteMomentConfirmation = false },
                            onConfirm = {
                                selectedMomentIds.forEach { momentId ->
                                    viewModel.deleteMoment(eventId, selectedFolderId, momentId)
                                }
                                selectedMomentIds = emptySet()
                                showDeleteMomentConfirmation = false
                            },
                            onProgress = { sheetMotionProgress = it }
                        )
                    }

                    if (showDeleteFolderConfirmation) {
                        val folderToDelete = folderNavigationStack.lastOrNull() ?: folders.find { it.id == selectedFolderId }
                        val hasSubfolders = foldersFromDb.isNotEmpty()
                        val hasMoments = moments.isNotEmpty()
                        
                        val heading = if (hasSubfolders) "Delete folder hierarchy?" else "Delete folder?"
                        val subHeading = when {
                            hasSubfolders && hasMoments -> "This folder contains sub-folders and images. Everything inside will be permanently deleted."
                            hasSubfolders -> "This folder contains nested sub-folders. All of them will be permanently deleted."
                            hasMoments -> "This folder contains images/videos. All contents will be permanently deleted."
                            else -> "This folder and its contents will be permanently deleted."
                        }

                        ConfirmationBottomSheet(
                            heading = heading,
                            subHeading = subHeading,
                            confirmButtonText = "Delete",
                            onDismiss = { showDeleteFolderConfirmation = false },
                            onConfirm = {
                                viewModel.deleteFolder(eventId, selectedFolderId)
                                
                                // Navigate up one level
                                val newStack = folderNavigationStack.dropLast(1)
                                folderNavigationStack = newStack
                                selectedFolderId = newStack.lastOrNull()?.id ?: ""
                                if (selectedFolderId.isEmpty()) {
                                    viewMode = MomentViewMode.Folders
                                }
                                showDeleteFolderConfirmation = false
                            },
                            onProgress = { sheetMotionProgress = it }
                        )
                    }
                }
            }
        }
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

@OptIn(ExperimentalSharedTransitionApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
internal fun MomentItem(
    moment: Moment,
    transitionKey: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit
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
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) SurfaceBrandPrimary else Color.Transparent,
                    shape = shape
                )
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
internal fun FolderItem(folder: MomentFolder, onClick: () -> Unit) {
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
