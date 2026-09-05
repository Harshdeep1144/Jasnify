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
import com.harshdeep.jasnify.presentation.components.states.MomentsLoadingState
import androidx.compose.ui.geometry.Rect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import com.harshdeep.jasnify.data.local.prefs.PreferenceManager
import com.harshdeep.jasnify.presentation.components.others.FeatureOnboardingOverlay
import com.harshdeep.jasnify.presentation.components.others.OnboardingStep
import com.harshdeep.jasnify.presentation.components.others.onboardingTarget
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import com.harshdeep.jasnify.theme.CornerExtraLarge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import com.harshdeep.jasnify.presentation.components.others.ThreeDotsWaveLoadingIndicator
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
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
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
import com.harshdeep.jasnify.theme.CornerLargeIncrease
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

    if (selectedMomentForFullView == null) {
        SetStatusBarTheme(useDarkIcons = true, statusBarColor = Color.Transparent)
    }
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

    val rootFolders by viewModel.rootFolders.collectAsStateWithLifecycle()
    val allMoments by viewModel.allMoments.collectAsStateWithLifecycle()
    val subFolders by viewModel.subFolders.collectAsStateWithLifecycle()
    val folderMoments by viewModel.folderMoments.collectAsStateWithLifecycle()
    val savedMoments by viewModel.savedMoments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val uploadProgress by viewModel.uploadProgress.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()

    val activeMoments = if (viewMode == MomentViewMode.FolderContent && selectedFolderId != "all_moments_id") folderMoments else allMoments

    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val firebaseUser = remember { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser }

    val prefManager = remember { PreferenceManager(context) }
    var isMomentsOnboardingActive by remember { mutableStateOf(!prefManager.hasCompletedScreenOnboarding("moments")) }
    var momentsStepIndex by remember { mutableIntStateOf(0) }
    val momentsTargetRects = remember { mutableStateMapOf<String, Rect>() }

    val hasSeenGroupChat = prefManager.hasSeenGroupChatOnboarding()
    val momentsOnboardingSteps = remember(hasSeenGroupChat) {
        val steps = mutableListOf<OnboardingStep>()
        if (!hasSeenGroupChat) {
            steps.add(
                OnboardingStep(
                    stepKey = "room_group_chat",
                    title = "Room Group Chat",
                    description = "Tap here to open group chat with your room members and stay connected!",
                    iconRes = R.drawable.ic_message,
                    isCircleHighlight = true
                )
            )
        }
        steps.add(
            OnboardingStep(
                stepKey = "moments_add_button",
                title = "Add Moments & Albums",
                description = "Tap the + button to upload photos, videos, or create custom photo albums!",
                iconRes = R.drawable.ill_vendor_grooming,
                isCircleHighlight = true
            )
        )
        steps
    }

    val currentMomentsStepKey = if (isMomentsOnboardingActive && momentsStepIndex in momentsOnboardingSteps.indices) {
        momentsOnboardingSteps[momentsStepIndex].stepKey
    } else null

    val isOwner = remember(activeEvent, firebaseUser) {
        val uid = firebaseUser?.uid
        !uid.isNullOrEmpty() && (activeEvent?.ownerId == uid || activeEvent == null)
    }
    val currentUserInRoom = remember(roomUsers, firebaseUser) {
        roomUsers.find { it.uid == firebaseUser?.uid }
    }
    val effectiveUserRole = remember(isOwner, currentUserInRoom, userRole) {
        when {
            isOwner -> UserRole.OWNER
            currentUserInRoom != null -> currentUserInRoom.role
            else -> userRole
        }
    }

    val canAddContent = remember(effectiveUserRole) { effectiveUserRole == UserRole.OWNER || effectiveUserRole == UserRole.EDITOR }
    val canDeleteAny = remember(effectiveUserRole) { effectiveUserRole == UserRole.OWNER }
    val canDeleteOwn = remember(effectiveUserRole) { effectiveUserRole == UserRole.EDITOR }

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

    val isZoomedBackdrop by remember {
        derivedStateOf {
            isAnySheetVisible || viewMode == MomentViewMode.FolderContent
        }
    }

    val targetScale = if (isZoomedBackdrop) 0.92f + (0.08f * sheetMotionProgress) else 1.0f

    val backdropScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadius by animateDpAsState(
        targetValue = if (isZoomedBackdrop) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
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

    if (isLoading && allMoments.isEmpty() && rootFolders.isEmpty()) {
        MomentsLoadingState()
        return
    }

    val displayFolders = remember(rootFolders, allMoments) {
        val allMomentsFolder = MomentFolder(
            id = "all_moments_id",
            name = "All Moments",
            coverImageUrl = allMoments.firstOrNull()?.imageUrl ?: "",
            itemCount = allMoments.size,
            isNew = false
        )
        val filteredDbFolders = rootFolders.filter {
            !(it.name == "All Moments" && it.parentId == "")
        }
        listOf(allMomentsFolder) + filteredDbFolders
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

    val getEffectiveEventId = remember(eventId, eventViewModel) {
        { eventId.ifBlank { eventViewModel.getLocalActiveEventId().orEmpty() } }
    }

    val uploadUris: (List<Uri>) -> Unit = remember(eventId, viewModel, getTargetFolderId, isVideoUri, eventViewModel) {
        { uris ->
            val targetId = getTargetFolderId()
            val targetEventId = getEffectiveEventId()
            if (targetEventId.isNotBlank() && uris.isNotEmpty()) {
                val urisWithTypes = uris.map { uri -> uri to isVideoUri(uri) }
                viewModel.uploadMultipleMoments(targetEventId, targetId, urisWithTypes)
            }
        }
    }

    val onDownloadTrigger: (Set<String>) -> Unit = remember(context, activeMoments, savedMoments, coroutineScope) {
        { ids ->
            coroutineScope.launch {
                ids.forEach { id ->
                    val moment = activeMoments.find { it.id == id } ?: savedMoments.find { it.id == id }
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
                val targetEventId = getEffectiveEventId()
                if (targetEventId.isNotBlank()) {
                    viewModel.uploadMoment(targetEventId, targetId, tempImageUri!!, false)
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
        if (eventId.isNotBlank()) {
            eventViewModel.fetchAndSetActiveEvent(eventId)
            roomViewModel.loadRoomUsers(eventId, "Moments")
        }
    }

    LaunchedEffect(eventId, selectedFolderId, viewMode) {
        if (eventId.isBlank()) return@LaunchedEffect
        
        viewModel.loadRootContent(eventId)
        if (viewMode == MomentViewMode.Saved) {
            viewModel.loadSavedMoments(eventId)
        } else if (viewMode == MomentViewMode.FolderContent && selectedFolderId.isNotEmpty() && selectedFolderId != "all_moments_id") {
            viewModel.loadFolderContent(eventId, selectedFolderId)
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

    val isInsideFolder = remember(viewMode, selectedFolderId) {
        viewMode == MomentViewMode.FolderContent && selectedFolderId.isNotEmpty() && selectedFolderId != "all_moments_id"
    }
    val currentFolder = remember(folderNavigationStack, displayFolders, selectedFolderId) {
        folderNavigationStack.lastOrNull() ?: displayFolders.find { it.id == selectedFolderId }
    }
    val canDeleteFolder = remember(isInsideFolder, canDeleteAny, canDeleteOwn, currentFolder, currentUserId) {
        isInsideFolder && (canDeleteAny || (canDeleteOwn && (currentFolder?.uploaderId == currentUserId || currentFolder?.uploaderId.isNullOrEmpty())))
    }

    val moreItems = remember(
        addIconPainter, multiSelectIconPainter, downloadIconPainter, userProfileIconPainter, heartIconPainter, deleteIconPainter,
        canAddContent, canDeleteAny, canDeleteFolder, activeMoments.isNotEmpty()
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

        if (activeMoments.isNotEmpty()) {
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

        if (primaryActions.isNotEmpty()) {
            list.add(primaryActions)
        }

        if (canDeleteFolder) {
            list.add(
                listOf(
                    MenuSheetActionItem(
                        text = "Delete Folder",
                        icon = deleteIconPainter,
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMoreMenu = false
                            showDeleteFolderConfirmation = true
                        }
                    )
                )
            )
        }

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

        list.add(
            listOf(
                MenuSheetActionItem(
                    text = "Saved Moments",
                    icon = heartIconPainter,
                    iconPlacement = IconPlacement.Left,
                    onClick = {
                        showMoreMenu = false
                        viewMode = MomentViewMode.Saved
                    }
                )
            )
        )

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
            viewMode == MomentViewMode.Saved -> viewMode = MomentViewMode.AllMoments
            viewMode == MomentViewMode.ROOM -> viewMode = MomentViewMode.GROUP_CHAT
            viewMode == MomentViewMode.GROUP_CHAT -> viewMode = MomentViewMode.AllMoments
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
                val isFavorite = savedMoments.any { it.id == targetMoment.id }
                MomentItemFullView(
                    moment = targetMoment,
                    transitionKey = "moment_${targetMoment.id}",
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onBackClick = { selectedMomentForFullView = null },
                    onFavoriteClick = {
                        viewModel.toggleSaveMoment(eventId, targetMoment)
                    },
                    isFavorite = isFavorite,
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
                                scaleX = backdropScale
                                scaleY = backdropScale
                                clip = isZoomedBackdrop || backdropCornerRadius > 0.dp
                                shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                            }
                            .background(BackgroundPrimary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(nestedScrollConnection)
                        ) {
                            if (viewMode != MomentViewMode.GROUP_CHAT && viewMode != MomentViewMode.ROOM) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .statusBarsPadding()
                                ) {
                                    CustomTopBar(
                                        title = if (isSelectionMode) "${selectedMomentIds.size} selected" else currentTitle,
                                        subtitle = if (!isSelectionMode && viewMode == MomentViewMode.FolderContent) "${activeMoments.size} items" else null,
                                        buttonStyle = ButtonBackground.OPAQUE,
                                        buttonColor = SurfaceSecondary,
                                        secondaryIcon = if (isSelectionMode) null else TopIcon.Predefined.CHAT,
                                        onSecondaryClick = {
                                            viewMode = MomentViewMode.GROUP_CHAT
                                        },
                                        secondaryIconModifier = Modifier.onboardingTarget("room_group_chat", currentMomentsStepKey) {
                                            momentsTargetRects["room_group_chat"] = it
                                        },
                                        menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                                        onBackClick = {
                                            if (isSelectionMode) {
                                                selectedMomentIds = emptySet()
                                                isForceMultiSelect = false
                                            } else if (selectedMomentForFullView != null) {
                                                selectedMomentForFullView = null
                                            } else if (folderNavigationStack.isNotEmpty()) {
                                                val newStack = folderNavigationStack.dropLast(1)
                                                folderNavigationStack = newStack
                                                selectedFolderId = newStack.lastOrNull()?.id ?: ""
                                                if (selectedFolderId.isEmpty()) {
                                                    viewMode = MomentViewMode.Folders
                                                } else {
                                                    viewModel.loadFolderContent(eventId, selectedFolderId)
                                                }
                                            } else if (viewMode == MomentViewMode.FolderContent) {
                                                viewMode = MomentViewMode.Folders
                                                selectedFolderId = ""
                                            } else {
                                                onBackClick()
                                            }
                                        },
                                        onMenuClick = {
                                            showMoreMenu = true
                                        }
                                    )
                                }
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                if (isLoading && allMoments.isEmpty() && rootFolders.isEmpty()) {
                                    MomentsLoadingState()
                                } else {
                                    when (viewMode) {
                                        MomentViewMode.AllMoments -> {
                                            MomentsGrid(
                                                moments = allMoments,
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
                                                moments = activeMoments,
                                                subfolders = subFolders,
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
                                                    viewModel.loadFolderContent(eventId, folder.id)
                                                }
                                            )
                                        }
                                        MomentViewMode.Folders -> {
                                            FoldersGrid(
                                                folders = displayFolders,
                                                gridState = foldersGridState
                                            ) { folder ->
                                                if (folder.id == "all_moments_id") {
                                                    selectedFolderId = "all_moments_id"
                                                    viewMode = MomentViewMode.FolderContent
                                                } else {
                                                    selectedFolderId = folder.id
                                                    folderNavigationStack = folderNavigationStack + folder
                                                    viewMode = MomentViewMode.FolderContent
                                                    viewModel.loadFolderContent(eventId, folder.id)
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
                                                onBackClick = { viewMode = MomentViewMode.GROUP_CHAT },
                                                roomViewModel = roomViewModel
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Floating Bottom Tab + Separate CustomIconButton (+)
                    AnimatedVisibility(
                        visible = isBottomTabVisible && sheetMotionProgress == 1.0f && !isSelectionMode && viewMode != MomentViewMode.ROOM && viewMode != MomentViewMode.GROUP_CHAT && viewMode != MomentViewMode.FolderContent,
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
                                            )
                                            .onboardingTarget("moments_add_button", currentMomentsStepKey) {
                                                momentsTargetRects["moments_add_button"] = it
                                            },
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
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                            .zIndex(10f)
                    ) {
                        val canDeleteSelection = activeMoments.filter { it.id in selectedMomentIds }.all {
                            canDeleteAny || (canDeleteOwn && it.uploaderId == currentUserId)
                        }
                        val isSelectionFavorite = remember(selectedMomentIds, savedMoments) {
                            selectedMomentIds.isNotEmpty() && selectedMomentIds.all { id -> savedMoments.any { it.id == id } }
                        }

                        MomentsActionBar(
                            onShareClick = {
                                val firstMoment = activeMoments.find { it.id in selectedMomentIds }
                                firstMoment?.let { item ->
                                    ShareUtils.shareText(context, item.imageUrl)
                                }
                            },
                            onDeleteClick = if (canDeleteSelection) {
                                { showDeleteMomentConfirmation = true }
                            } else null,
                            onFavoriteClick = {
                                selectedMomentIds.forEach { id ->
                                    activeMoments.find { it.id == id }?.let { moment ->
                                        viewModel.toggleSaveMoment(eventId, moment)
                                    }
                                }
                                selectedMomentIds = emptySet()
                                isForceMultiSelect = false
                            },
                            isFavorite = isSelectionFavorite,
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
                            containerColor = Color.White,
                            contentColor = ContentPrimary
                        )
                    }

                    if (isLoading || uploadProgress != null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .zIndex(20f)
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
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ThreeDotsWaveLoadingIndicator(
                                    dotSize = 6.dp,
                                    dotColor = SurfaceBrandPrimary,
                                    travelDistance = 4.dp
                                )
                                val progressText = if (uploadProgress != null) {
                                    "Uploading... (${uploadProgress!!.first}/${uploadProgress!!.second})"
                                } else {
                                    "Uploading..."
                                }
                                Text(
                                    text = progressText,
                                    style = JasnifyTheme.typography.headingMedium,
                                    color = ContentPrimary
                                )
                            }
                        }
                    }

                    if (showCreateFolderSheet) {
                        CreateFolderBottomSheet(
                            onDismiss = { showCreateFolderSheet = false },
                            onCreate = { folderName ->
                                val targetEventId = getEffectiveEventId()
                                if (targetEventId.isNotBlank()) {
                                    viewModel.createFolder(targetEventId, folderName, selectedFolderId)
                                }
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
                        val folderToDelete = folderNavigationStack.lastOrNull() ?: displayFolders.find { it.id == selectedFolderId }
                        val hasSubfolders = subFolders.isNotEmpty()
                        val hasMoments = activeMoments.isNotEmpty()

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

                    if (isMomentsOnboardingActive && momentsOnboardingSteps.isNotEmpty() && viewMode != MomentViewMode.GROUP_CHAT && viewMode != MomentViewMode.ROOM) {
                        FeatureOnboardingOverlay(
                            steps = momentsOnboardingSteps,
                            currentStepIndex = momentsStepIndex,
                            targetRectMap = momentsTargetRects,
                            onNextStep = {
                                if (momentsStepIndex < momentsOnboardingSteps.lastIndex) {
                                    momentsStepIndex++
                                } else {
                                    isMomentsOnboardingActive = false
                                    prefManager.setCompletedScreenOnboarding("moments", true)
                                    prefManager.setHasSeenGroupChatOnboarding(true)
                                }
                            },
                            onPreviousStep = {
                                if (momentsStepIndex > 0) momentsStepIndex--
                            }
                        )
                    }
                }
            }
        }
    }
}


// ========================================================== Helper Functions =============================================


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
                placeholder = "Enter folder name",
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
    val shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .size(126.66.dp)
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
                    width = if (isSelected) 2.dp else 0.dp,
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
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Color.Black.copy(alpha = 0.25f))
                        .border(
                            width = if (isSelected) 0.dp else 1.5.dp,
                            color = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            painter = painterResource(R.drawable.ic_tick),
                            contentDescription = "Selected",
                            tint = SurfaceBrandPrimary,
                            modifier = Modifier.size(24.dp)
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
    val isWithin24Hours = remember(folder.createdAt, folder.isNew) {
        folder.isNew || (folder.createdAt > 0 && (System.currentTimeMillis() - folder.createdAt) <= 24 * 60 * 60 * 1000L)
    }

    Column(modifier = Modifier.noRippleClickable { onClick() }) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .size(190.dp)
                .clip(SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
        ) {
            if (folder.itemCount > 0 && folder.coverImageUrl.isNotEmpty()) {
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
                        painter = painterResource(R.drawable.ic_gallery_icon),
                        modifier = Modifier.size(56.dp),
                        contentDescription = null,
                        tint = ContentSecondary
                    )
                }
            }
            if (isWithin24Hours) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.White, RoundedCornerShape(100))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "NEW",
                        style = JasnifyTheme.typography.labelSmall,
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = folder.name,
            style = JasnifyTheme.typography.headingMedium,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "${folder.itemCount} items",
            style = JasnifyTheme.typography.labelMedium,
            color = ContentSecondary
        )
    }
}
