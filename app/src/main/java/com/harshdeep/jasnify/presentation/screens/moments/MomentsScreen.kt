package com.harshdeep.jasnify.presentation.screens.moments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.profile.DownloadPreferencesBottomSheet
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
import com.harshdeep.jasnify.presentation.screens.chats.GroupChatScreen
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.presentation.utils.noRippleCombinedClickable
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.presentation.viewmodels.MomentsViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.utils.ShareUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sv.lib.squircleshape.SquircleShape
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.time.Duration.Companion.milliseconds

enum class MomentViewMode {
    AllMoments,
    Folders,
    FolderContent,
    Saved,
    ROOM,
    GROUP_CHAT
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MomentsScreen(
    onBackClick: () -> Unit,
    navController: androidx.navigation.NavHostController? = null,
    viewModel: MomentsViewModel,
    eventViewModel: com.harshdeep.jasnify.presentation.viewmodels.EventViewModel = hiltViewModel(),
    roomViewModel: com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel = hiltViewModel()
) {
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val eventId = activeEventId ?: ""
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var viewMode by remember { mutableStateOf(MomentViewMode.AllMoments) }
    var selectedMomentForFullView by remember { mutableStateOf<Moment?>(null) }
    var showFabMenu by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showCreateFolderSheet by remember { mutableStateOf(false) }
    var showDownloadPreferences by remember { mutableStateOf(false) }
    var selectedFolderId by remember { mutableStateOf("") }
    var folderNavigationStack by remember { mutableStateOf(listOf<MomentFolder>()) }
    var isForceMultiSelect by remember { mutableStateOf(false) }

    var sheetMotionProgress by remember { mutableFloatStateOf(1.0f) }
    var selectedMomentIds by remember { mutableStateOf(setOf<String>()) }
    var showDeleteMomentConfirmation by remember { mutableStateOf(false) }
    var showDeleteFolderConfirmation by remember { mutableStateOf(false) }

    val isSelectionMode by remember { derivedStateOf { selectedMomentIds.isNotEmpty() || isForceMultiSelect } }

    val foldersFromDb by viewModel.folders.collectAsStateWithLifecycle()
    val moments by viewModel.moments.collectAsStateWithLifecycle()
    val savedMoments by viewModel.savedMoments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()

    val canAddContent = remember(userRole) { userRole == UserRole.OWNER || userRole == UserRole.EDITOR }
    val canDeleteAny = remember(userRole) { userRole == UserRole.OWNER }
    val canDeleteOwn = remember(userRole) { userRole == UserRole.EDITOR }

    val isAnySheetVisible by remember {
        derivedStateOf {
            showFabMenu || showMoreMenu || showCreateFolderSheet || showDownloadPreferences ||
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
        if (selectedFolderId.isEmpty()) {
            val allMomentsFolder = MomentFolder(
                id = "all_moments_id",
                name = "All Moments",
                coverImageUrl = moments.firstOrNull()?.imageUrl ?: "",
                itemCount = moments.size,
                isNew = false
            )
            val filteredDbFolders = foldersFromDb.filter {
                !(it.name == "All Moments" && it.parentId == "")
            }
            listOf(allMomentsFolder) + filteredDbFolders
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

    val onDownloadTrigger: (Set<String>) -> Unit = remember(context, moments, savedMoments, coroutineScope) {
        { ids ->
            coroutineScope.launch {
                ids.forEach { id ->
                    val moment = moments.find { it.id == id } ?: savedMoments.find { it.id == id }
                    moment?.let {
                        withContext(Dispatchers.IO) {
                            try {
                                if (it.isVideo) {
                                    val tempFile = File(context.cacheDir, "download_${System.currentTimeMillis()}.mp4")
                                    URL(it.imageUrl).openStream().use { input ->
                                        FileOutputStream(tempFile).use { output ->
                                            input.copyTo(output)
                                        }
                                    }
                                    ShareUtils.downloadVideo(context, tempFile)
                                } else {
                                    val loader = ImageLoader(context)
                                    val request = ImageRequest.Builder(context)
                                        .data(it.imageUrl)
                                        .build()
                                    val result = (loader.execute(request) as SuccessResult).drawable
                                    val bitmap = (result as BitmapDrawable).bitmap
                                    ShareUtils.downloadImage(context, bitmap)
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("MomentsScreen", "Download failed: ${e.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    val createTempUri: () -> Uri = remember(context) {
        {
            val tempFile = File.createTempFile("moment_", ".jpg", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }
            FileProvider.getUriForFile(context, "${com.harshdeep.jasnify.BuildConfig.APPLICATION_ID}.fileprovider", tempFile)
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

    LaunchedEffect(eventId, selectedFolderId, viewMode) {
        if (eventId.isBlank()) return@LaunchedEffect
        
        when (viewMode) {
            MomentViewMode.Saved -> {
                viewModel.loadSavedMoments(eventId)
            }
            MomentViewMode.AllMoments -> {
                viewModel.loadFolders(eventId, "")
                viewModel.loadMoments(eventId, "all_moments_id")
            }
            MomentViewMode.FolderContent -> {
                viewModel.loadFolders(eventId, selectedFolderId)
                viewModel.loadMoments(eventId, selectedFolderId)
            }
            else -> {}
        }
    }

    val currentTitle = remember(viewMode, selectedFolderId, folderNavigationStack) {
        when (viewMode) {
            MomentViewMode.Saved -> "Saved Moments"
            MomentViewMode.FolderContent if selectedFolderId == "all_moments_id" -> "All Moments"
            MomentViewMode.FolderContent if folderNavigationStack.isNotEmpty() -> folderNavigationStack.last().name
            else -> "Moments"
        }
    }

    val galleryIconPainter = painterResource(R.drawable.ic_gallery_icon)
    val folderIconPainter = painterResource(R.drawable.ic_folder)
    val addIconPainter = rememberVectorPainter(Icons.Default.Add)
    val cameraIconPainter = painterResource(R.drawable.ic_camera)
    val uploadIconPainter = painterResource(R.drawable.ic_upload)
    val iCloudIconPainter = painterResource(R.drawable.ic_apple)
    val googlePhotosIconPainter = painterResource(R.drawable.ic_google_photos)
    val heartIconPainter = painterResource(R.drawable.ic_top_bar_heart)
    val userProfileIconPainter = painterResource(R.drawable.ic_user_default)
    val deleteIconPainter = painterResource(R.drawable.ic_delete)
    val multiSelectIconPainter = painterResource(R.drawable.ic_multi_select)
    val downloadIconPainter = painterResource(R.drawable.ic_download)

    val bottomTabItems = remember(galleryIconPainter, folderIconPainter) {
        listOf(
            TabItem(
                label = "",
                value = MomentViewMode.AllMoments,
                icon = galleryIconPainter
            ),
            TabItem(
                label = "",
                value = MomentViewMode.Folders,
                icon = folderIconPainter
            )
        )
    }

    val addMenuItems = remember(
        cameraIconPainter,
        uploadIconPainter,
        folderIconPainter,
        iCloudIconPainter,
        googlePhotosIconPainter,
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
                    icon = folderIconPainter,
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
                    icon = iCloudIconPainter,
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
                    icon = googlePhotosIconPainter,
                    iconColor = Color.Unspecified,
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

    val moreItems = remember(
        addIconPainter, multiSelectIconPainter, downloadIconPainter, userProfileIconPainter, heartIconPainter,
        canAddContent, canDeleteAny, moments.isNotEmpty()
    ) {
        val list = mutableListOf<List<MenuSheetActionItem>>()

        val primaryActions = mutableListOf<MenuSheetActionItem>()
        if (canAddContent) {
            primaryActions.add(
                MenuSheetActionItem(
                    text = "Add Moments",
                    icon = addIconPainter,
                    iconPlacement = IconPlacement.Top,
                    onClick = {
                        showMoreMenu = false
                        showFabMenu = true
                    }
                )
            )
        }

        if (moments.isNotEmpty()) {
            primaryActions.add(
                MenuSheetActionItem(
                    text = "Multi-Select",
                    icon = multiSelectIconPainter,
                    iconPlacement = IconPlacement.Top,
                    onClick = {
                        showMoreMenu = false
                        isForceMultiSelect = true
                    }
                )
            )
        }

        primaryActions.add(
            MenuSheetActionItem(
                text = "Saved Moments",
                icon = heartIconPainter,
                iconPlacement = IconPlacement.Top,
                onClick = {
                    showMoreMenu = false
                    viewMode = MomentViewMode.Saved
                }
            )
        )

        list.add(primaryActions)

        // Room Access card (if owner)
        if (canDeleteAny) {
            list.add(
                listOf(
                    MenuSheetActionItem(
                        text = "Manage Room Access",
                        icon = userProfileIconPainter,
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMoreMenu = false
                            viewMode = MomentViewMode.ROOM
                        }
                    )
                )
            )
        }

        // Download Preferences card
        list.add(
            listOf(
                MenuSheetActionItem(
                    text = "Download Preferences",
                    icon = downloadIconPainter,
                    iconPlacement = IconPlacement.Left,
                    onClick = {
                        showMoreMenu = false
                        showDownloadPreferences = true
                    }
                )
            )
        )

        list
    }

    BackHandler {
        when {
            selectedMomentForFullView != null -> selectedMomentForFullView = null
            showFabMenu -> showFabMenu = false
            showMoreMenu -> showMoreMenu = false
            showCreateFolderSheet -> showCreateFolderSheet = false
            showDownloadPreferences -> showDownloadPreferences = false
            isForceMultiSelect -> {
                isForceMultiSelect = false
                selectedMomentIds = emptySet()
            }
            selectedMomentIds.isNotEmpty() -> selectedMomentIds = emptySet()
            viewMode == MomentViewMode.ROOM -> viewMode = MomentViewMode.AllMoments
            folderNavigationStack.isNotEmpty() -> {
                val newStack = folderNavigationStack.dropLast(1)
                folderNavigationStack = newStack
                selectedFolderId = newStack.lastOrNull()?.id ?: ""
                if (selectedFolderId.isEmpty()) {
                    viewMode = MomentViewMode.Folders
                }
            }
            viewMode == MomentViewMode.FolderContent -> {
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
                    onBackClick = { selectedMomentForFullView = null },
                    onFavoriteClick = {
                        viewModel.toggleSaveMoment(eventId, targetMoment)
                    },
                    onDownloadClick = {
                        val savedQuality = viewModel.getDownloadPreference()
                        if (savedQuality != null) {
                            onDownloadTrigger(setOf(targetMoment.id))
                        } else {
                            showDownloadPreferences = true
                        }
                    }
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
                                subtitle = if (!isSelectionMode && (viewMode == MomentViewMode.FolderContent || viewMode == MomentViewMode.Saved)) "${if(viewMode == MomentViewMode.Saved) savedMoments.size else moments.size} items" else null,
                                buttonStyle = ButtonBackground.OPAQUE,
                                buttonColor = SurfaceSecondary,
                                secondaryIcon = if (isSelectionMode) null else TopIcon.Predefined.CHAT,
                                onSecondaryClick = {
                                    viewMode = MomentViewMode.GROUP_CHAT
                                },
                                menuIcon = if (isSelectionMode) {
                                    val canDeleteSelection = moments.filter { it.id in selectedMomentIds }.all {
                                        canDeleteAny || (canDeleteOwn && it.uploaderId == currentUserId)
                                    }
                                    if (canDeleteSelection) TopIcon.CustomPainter(deleteIconPainter) else TopIcon.Predefined.MENU_HORIZONTAL
                                } else TopIcon.Predefined.MENU_VERTICAL,
                                onBackClick = {
                                    if (isSelectionMode) {
                                        selectedMomentIds = emptySet()
                                        isForceMultiSelect = false
                                    } else if (selectedMomentForFullView != null) {
                                        selectedMomentForFullView = null
                                    } else if (viewMode == MomentViewMode.Saved || viewMode == MomentViewMode.ROOM) {
                                        viewMode = MomentViewMode.AllMoments
                                    } else if (folderNavigationStack.isNotEmpty()) {
                                        val newStack = folderNavigationStack.dropLast(1)
                                        folderNavigationStack = newStack
                                        selectedFolderId = newStack.lastOrNull()?.id ?: ""
                                        if (selectedFolderId.isEmpty()) {
                                            viewMode = MomentViewMode.Folders
                                        }
                                    } else if (viewMode == MomentViewMode.FolderContent) {
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
                                            showMoreMenu = true
                                        }
                                    } else {
                                        showMoreMenu = true
                                    }
                                }
                            )

                            // Large Banner with top large icon and bottom content
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(0.16f),
                                        shape = SquircleShape(24.dp, CornerSmoothingDefault)
                                    )
                                    .background(
                                        color = SurfaceSecondary,
                                        shape = SquircleShape(24.dp, CornerSmoothingDefault)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ill_work_in_progress),
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(156.dp)
                                    )

                                    Text(
                                        text = "Work in Progress",
                                        style = JasnifyTheme.typography.displayMedium,
                                        color = ContentPrimary,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "You can still upload your moments. Some actions may be temporarily limited.",
                                        style = JasnifyTheme.typography.bodyLarge,
                                        color = ContentSecondary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                when (viewMode) {
                                    MomentViewMode.AllMoments -> {
                                        MomentsGrid(
                                            moments = moments,
                                            subfolders = emptyList(),
                                            gridState = photosGridState,
                                            animatedVisibilityScope = this@AnimatedContent,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            selectedMomentIds = selectedMomentIds,
                                            onMomentClick = { moment ->
                                                if (isSelectionMode) {
                                                    selectedMomentIds = if (selectedMomentIds.contains(moment.id)) {
                                                        selectedMomentIds - moment.id
                                                    } else {
                                                        selectedMomentIds + moment.id
                                                    }
                                                } else {
                                                    selectedMomentForFullView = moment
                                                }
                                            },
                                            onMomentLongClick = { moment ->
                                                selectedMomentIds = selectedMomentIds + moment.id
                                            }
                                        )
                                    }
                                    MomentViewMode.FolderContent -> {
                                        MomentsGrid(
                                            moments = moments,
                                            subfolders = foldersFromDb,
                                            gridState = photosGridState,
                                            animatedVisibilityScope = this@AnimatedContent,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            selectedMomentIds = selectedMomentIds,
                                            onMomentClick = { moment ->
                                                if (isSelectionMode) {
                                                    selectedMomentIds = if (selectedMomentIds.contains(moment.id)) {
                                                        selectedMomentIds - moment.id
                                                    } else {
                                                        selectedMomentIds + moment.id
                                                    }
                                                } else {
                                                    selectedMomentForFullView = moment
                                                }
                                            },
                                            onMomentLongClick = { moment ->
                                                selectedMomentIds = selectedMomentIds + moment.id
                                            },
                                            onFolderClick = { folder ->
                                                selectedFolderId = folder.id
                                                folderNavigationStack = folderNavigationStack + folder
                                                viewMode = MomentViewMode.FolderContent
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
                                                viewMode = MomentViewMode.FolderContent
                                            } else {
                                                selectedFolderId = folder.id
                                                folderNavigationStack = folderNavigationStack + folder
                                                viewMode = MomentViewMode.FolderContent
                                            }
                                        }
                                    }
                                    MomentViewMode.Saved -> {
                                        MomentsGrid(
                                            moments = savedMoments,
                                            subfolders = emptyList(),
                                            gridState = photosGridState,
                                            animatedVisibilityScope = this@AnimatedContent,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            selectedMomentIds = selectedMomentIds,
                                            onMomentClick = { moment ->
                                                if (isSelectionMode) {
                                                    selectedMomentIds = if (selectedMomentIds.contains(moment.id)) {
                                                        selectedMomentIds - moment.id
                                                    } else {
                                                        selectedMomentIds + moment.id
                                                    }
                                                } else {
                                                    selectedMomentForFullView = moment
                                                }
                                            },
                                            onMomentLongClick = { moment ->
                                                selectedMomentIds = selectedMomentIds + moment.id
                                            }
                                        )
                                    }
                                    MomentViewMode.GROUP_CHAT -> {
                                        GroupChatScreen(
                                            eventId = eventId,
                                            roomType = "Moments",
                                            onBackClick = { viewMode = MomentViewMode.AllMoments },
                                            onMembersClick = { viewMode = MomentViewMode.ROOM }
                                        )
                                    }
                                    MomentViewMode.ROOM -> {
                                        MomentsRoomContent(
                                            eventId = eventId,
                                            onBackClick = { viewMode = MomentViewMode.AllMoments },
                                            roomViewModel = roomViewModel
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Floating Bottom Tab + Separate CustomIconButton (+)
                    AnimatedVisibility(
                        visible = isBottomTabVisible && sheetMotionProgress == 1.0f && !isSelectionMode && viewMode != MomentViewMode.ROOM && viewMode != MomentViewMode.GROUP_CHAT,
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
                                    selectedValue = if (viewMode == MomentViewMode.FolderContent) MomentViewMode.Folders else if (viewMode == MomentViewMode.Saved) MomentViewMode.AllMoments else viewMode,
                                    onItemSelected = { mode ->
                                        viewMode = mode
                                        if (mode == MomentViewMode.AllMoments) {
                                            selectedFolderId = ""
                                            viewModel.loadMoments(eventId, "all_moments_id")
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

                    // Multi-Select Action Bar (Pill)
                    AnimatedVisibility(
                        visible = isSelectionMode && sheetMotionProgress == 1.0f,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        ) + fadeIn(),
                        exit = slideOutVertically(
                            targetOffsetY = { it }
                        ) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = 24.dp)
                            .zIndex(10f)
                    ) {
                        MomentsActionBar(
                            onShareClick = { /* Share logic */ },
                            onFavoriteClick = {
                                selectedMomentIds.forEach { id ->
                                    (moments.find { it.id == id } ?: savedMoments.find { it.id == id })?.let { moment ->
                                        viewModel.toggleSaveMoment(eventId, moment)
                                    }
                                }
                                selectedMomentIds = emptySet()
                                isForceMultiSelect = false
                            },
                            onDownloadClick = {
                                val savedQuality = viewModel.getDownloadPreference()
                                if (savedQuality != null) {
                                    onDownloadTrigger(selectedMomentIds)
                                    selectedMomentIds = emptySet()
                                    isForceMultiSelect = false
                                } else {
                                    showDownloadPreferences = true
                                }
                            },
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
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

                    if (showDownloadPreferences) {
                        DownloadPreferencesBottomSheet(
                            fileCount = if (selectedMomentIds.isNotEmpty()) selectedMomentIds.size else 1,
                            initialQuality = viewModel.getSavedDownloadQuality(),
                            onDismiss = { showDownloadPreferences = false },
                            onDownload = { quality, remember ->
                                viewModel.saveDownloadPreference(quality, remember)
                                showDownloadPreferences = false

                                val idsToDownload = if (selectedMomentIds.isNotEmpty()) {
                                    selectedMomentIds
                                } else {
                                    selectedMomentForFullView?.id?.let { setOf(it) } ?: emptySet()
                                }
                                onDownloadTrigger(idsToDownload)

                                if (selectedMomentIds.isNotEmpty()) {
                                    selectedMomentIds = emptySet()
                                    isForceMultiSelect = false
                                }
                            },
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
                                isForceMultiSelect = false
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
    isSelectionMode: Boolean = false,
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
                .noRippleCombinedClickable(
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

            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(
                            color = if (isSelected) SurfaceBrandPrimary else Color.Black.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.5f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else if (moment.isVideo) {
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
    Column(modifier = Modifier.noRippleClickable { onClick() }) {
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
