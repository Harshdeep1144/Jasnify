package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.domain.model.UserEvent
import com.harshdeep.jasnify.presentation.components.bottomdrawer.profile.AppThemeBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.profile.AppThemeOption
import com.harshdeep.jasnify.presentation.components.bottomdrawer.auth.ChangePasswordBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.profile.EditProfileBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.event.JoinEventBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.event.JoinEventSheetState
import com.harshdeep.jasnify.presentation.components.bottomdrawer.event.JoinOrCreateBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.profile.NavBarStyleBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.plansheet.BasicPlanSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.plansheet.PlanType
import com.harshdeep.jasnify.presentation.components.bottomdrawer.plansheet.ProPlanSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.plansheet.UltimatePlanSheet
import com.harshdeep.jasnify.presentation.components.dialogs.AccountDeletionDialog
import com.harshdeep.jasnify.presentation.components.dialogs.ConfirmationDialog
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.chats.AiChatScreen
import com.harshdeep.jasnify.presentation.utils.TimeUtils
import com.harshdeep.jasnify.presentation.viewmodels.AuthState
import com.harshdeep.jasnify.presentation.components.states.ProfileLoadingState
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.ProfileUpdateState
import com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel
import com.harshdeep.jasnify.presentation.viewmodels.UIViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

enum class ProfileScreen {
    Root,
    AccountSettings,
    Appearance,
    ManageEvents,
    MyEnquiries,
    Notifications,
    TermsOfUse,
    PrivacyPolicy,
    HelpFeedback
}

@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTab(
    mainNavController: NavHostController,
    internalNavController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    enquiryViewModel: EnquiryViewModel = hiltViewModel(),
) {
    val mainGraphEntry = remember(mainNavController) {
        mainNavController.getBackStackEntry(Screen.MainAppGraph.route)
    }
    val uiViewModel: UIViewModel = hiltViewModel(mainGraphEntry)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val firebaseUser = auth.currentUser

    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val ownedEvents by eventViewModel.userEvents.collectAsStateWithLifecycle()

    if (userProfile == null) {
        ProfileLoadingState()
        return
    }

    val enquiries by remember(firebaseUser?.uid) {
        if (firebaseUser?.uid != null) {
            enquiryViewModel.getEnquiriesForUser(firebaseUser.uid)
        } else {
            flowOf(emptyList())
        }
    }.collectAsState(emptyList())

    LaunchedEffect(firebaseUser) {
        if (firebaseUser != null) {
            eventViewModel.fetchUserEvents()
        }
    }

    val eventCount = remember(ownedEvents, userProfile?.joinedEvents) {
        val joined = userProfile?.joinedEvents ?: emptyList()
        (ownedEvents.map { it.id } + joined.map { it.eventId }).distinct().size
    }
    val enquiryCount = enquiries.size

    val userName = userProfile?.name ?: firebaseUser?.displayName ?: "Name"
    val userEmail = userProfile?.email ?: firebaseUser?.email ?: "User Gmail"
    val userHandle = "@${userProfile?.username ?: userEmail.substringBefore("@")}"

    val profilePic: Any = if (userProfile != null) {
        userProfile?.profilePictureUrl ?: R.drawable.img_profile_placeholder
    } else {
        firebaseUser?.photoUrl ?: R.drawable.img_profile_placeholder
    }

    var currentScreen by rememberSaveable { mutableStateOf(ProfileScreen.Root) }

    val navEntry = internalNavController.currentBackStackEntry
    val targetScreenName by navEntry?.savedStateHandle?.getStateFlow<String?>("target_screen", null)?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(targetScreenName) {
        if (targetScreenName == "help_feedback") {
            currentScreen = ProfileScreen.HelpFeedback
            navEntry?.savedStateHandle?.remove<String>("target_screen")
        }
    }

    val profileLazyListState = rememberLazyListState()

    var showAiChatByHelp by remember { mutableStateOf(false) }

    var showEditProfile by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showAppTheme by remember { mutableStateOf(false) }
    var showNavBarStyle by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPlanSheet by remember { mutableStateOf(false) }
    var selectedPlanForSheet by remember { mutableStateOf(PlanType.BASIC) }

    var showJoinOrCreateSheet by remember { mutableStateOf(false) }
    var showJoinEventSheet by remember { mutableStateOf(false) }
    var showEventMenu by remember { mutableStateOf(false) }
    var selectedEventForMenu by remember { mutableStateOf<UserEvent?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var joinSheetStateEnum by remember { mutableStateOf(JoinEventSheetState.ENTER_ID) }
    var enteredEventId by remember { mutableStateOf("") }
    var verifiedEvent by remember { mutableStateOf<Event?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            showEditProfile ||
                    showChangePassword ||
                    showAppTheme ||
                    showNavBarStyle ||
                    showJoinOrCreateSheet ||
                    showJoinEventSheet ||
                    showEventMenu ||
                    showLeaveConfirmation ||
                    showPlanSheet
        }
    }

    LaunchedEffect(currentScreen, isAnyBottomSheetOpen) {
        onBottomBarVisibilityChange((currentScreen == ProfileScreen.Root && !isAnyBottomSheetOpen))
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

    var toastData by remember { mutableStateOf(ToastData()) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            if (toastData.type == ToastType.ERROR) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (toastData.message?.contains("Please", ignoreCase = true) == true ||
                    toastData.message?.contains("enter", ignoreCase = true) == true ||
                    toastData.message?.contains("select", ignoreCase = true) == true ||
                    toastData.message?.contains("invalid", ignoreCase = true) == true ||
                    toastData.message?.contains("don't have access", ignoreCase = true) == true
                ) {
                    delay(80.milliseconds)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            delay(2000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    var selectedTheme by remember { mutableStateOf(AppThemeOption.LIGHT_MODE) }
    val selectedNavBarStyle by uiViewModel.navBarStyle.collectAsState()

    val profileUpdateState by profileViewModel.updateState.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(profileUpdateState) {
        if (profileUpdateState is ProfileUpdateState.Success) {
            delay(2000.milliseconds)
            showEditProfile = false
            profileViewModel.resetUpdateState()
        }
    }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                if (state.message == "Password Updated") {
                    delay(2000.milliseconds)
                    showChangePassword = false
                    authViewModel.resetAuthState()
                } else if (state.message == "Account deleted" ||
                    state.message == "Account deletion requested"
                ) {
                    eventViewModel.clearActiveEvent()
                    toastData = ToastData(state.message, ToastType.SUCCESS)
                    delay(2000.milliseconds)
                    mainNavController.navigate(Screen.OnboardingGraph.route) {
                        popUpTo(0) { inclusive = true }
                    }
                    authViewModel.resetAuthState()
                }
            }
            is AuthState.Error -> {
                toastData = ToastData(state.message, ToastType.ERROR)
                authViewModel.resetAuthState()
            }
            else -> Unit
        }
    }

    if (showLogoutDialog) {
        ConfirmationDialog(
            onDismissRequest = { showLogoutDialog = false },
            onConfirm = {
                authViewModel.logout(context)
                eventViewModel.clearActiveEvent()
                mainNavController.navigate(Screen.LoginOrSignUp.route) {
                    popUpTo(Screen.MainAppGraph.route) { inclusive = true }
                }
            },
            title = "Are you sure?",
            description = "You will be logged out from the app.",
            confirmButtonText = "Log Out",
            dismissButtonText = "Cancel",
            isDestructive = true
        )
    }

    if (showDeleteAccountDialog) {
        AccountDeletionDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            onConfirm = {
                authViewModel.requestAccountDeletion()
                showDeleteAccountDialog = false
            }
        )
    }

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
                .background(BackgroundPrimary)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    val duration = 400
                    val easing = FastOutSlowInEasing
                    if (targetState == ProfileScreen.Root) {
                        // Sliding BACK to Root (Incoming from Left, Outgoing to Right)
                        (slideInHorizontally(
                            initialOffsetX = { -it / 3 },
                            animationSpec = tween(duration, easing = easing)
                        ) + fadeIn(tween(duration, easing = easing)))
                            .togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(duration, easing = easing)
                                ) + fadeOut(tween(duration, easing = easing))
                            )
                    } else {
                        // Sliding FORWARD to Sub-screen (Incoming from Right, Outgoing to Left)
                        (slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(duration, easing = easing)
                        ) + fadeIn(tween(duration, easing = easing)))
                            .togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { -it / 3 },
                                    animationSpec = tween(duration, easing = easing)
                                ) + fadeOut(tween(duration, easing = easing))
                            )
                    }
                },
                label = "ProfileTabNavigation",
                modifier = Modifier.fillMaxSize()
            ) { screen ->
                if (screen != ProfileScreen.Root) {
                    BackHandler {
                        currentScreen = ProfileScreen.Root
                    }
                }
                when (screen) {
                    ProfileScreen.Root -> {
                        val notificationEnabled by profileViewModel.isNotificationsEnabled.collectAsState()
                        ProfileRootScreen(
                            userName = userName,
                            userHandle = userHandle,
                            profilePic = profilePic,
                            eventCount = eventCount,
                            enquiryCount = enquiryCount,
                            notificationEnabled = notificationEnabled,
                            onEditProfile = { showEditProfile = true },
                            onNavigateTo = { currentScreen = it },
                            onLogout = { showLogoutDialog = true },
                            onPlanClick = { plan ->
                                selectedPlanForSheet = plan
                                showPlanSheet = true
                            },
                            lazyListState = profileLazyListState
                        )
                    }

                    ProfileScreen.AccountSettings -> {
                        val firebaseUserCurrent = FirebaseAuth.getInstance().currentUser
                        val isGoogleUser = firebaseUserCurrent?.providerData?.any { it.providerId == "google.com" } ?: false
                        val lastChangedText = TimeUtils.formatPasswordLastChanged(userProfile?.lastPasswordChangeTimestamp)
                        AccountSettingsScreen(
                            email = userEmail,
                            isGoogleUser = isGoogleUser,
                            lastChangedText = lastChangedText,
                            onBack = { currentScreen = ProfileScreen.Root },
                            onChangePassword = { showChangePassword = true },
                            onDeleteAccount = { showDeleteAccountDialog = true }
                        )
                    }

                    ProfileScreen.Appearance -> {
                        AppearanceScreen(
                            currentTheme = selectedTheme,
                            currentNavBarStyle = selectedNavBarStyle,
                            onBack = { currentScreen = ProfileScreen.Root },
                            onChangeTheme = { showAppTheme = true },
                            onChangeNavBarStyle = { showNavBarStyle = true }
                        )
                    }

                    ProfileScreen.ManageEvents -> {
                        ManageEventsScreen(
                            userProfile = userProfile,
                            eventViewModel = eventViewModel,
                            mainNavController = mainNavController,
                            ownedEvents = ownedEvents,
                            onBack = { currentScreen = ProfileScreen.Root },
                            onEventClick = { eventId ->
                                eventViewModel.fetchAndSetActiveEvent(eventId)
                                internalNavController.navigate(Screen.HomeTabScreen.Home.route) {
                                    popUpTo(Screen.HomeTabScreen.Home.route) { inclusive = true }
                                }
                                currentScreen = ProfileScreen.Root
                            },
                            onJoinOrCreateClick = { showJoinOrCreateSheet = true },
                            onShowMenu = { event ->
                                selectedEventForMenu = event
                                showEventMenu = true
                            }
                        )
                    }

                    ProfileScreen.MyEnquiries -> {
                        MyEnquiriesScreen(
                            enquiryViewModel = enquiryViewModel,
                            userId = firebaseUser?.uid ?: "",
                            onBack = { currentScreen = ProfileScreen.Root },
                            onEnquiryClick = { enquiry ->
                                mainNavController.navigate("chat_screen/${enquiry.merchantId}/${enquiry.venueId}")
                            }
                        )
                    }

                    ProfileScreen.Notifications -> {
                        NotificationsScreen(
                            profileViewModel = profileViewModel,
                            onBack = { currentScreen = ProfileScreen.Root }
                        )
                    }

                    ProfileScreen.TermsOfUse -> {
                        LegalScreen(
                            title = "Terms of Use",
                            onBack = { currentScreen = ProfileScreen.Root }
                        )
                    }

                    ProfileScreen.PrivacyPolicy -> {
                        LegalScreen(
                            title = "Privacy Policy",
                            onBack = { currentScreen = ProfileScreen.Root }
                        )
                    }

                    ProfileScreen.HelpFeedback -> {
                        HelpFeedbackScreen(
                            profileViewModel = profileViewModel,
                            onBack = { currentScreen = ProfileScreen.Root },
                            onShowAiChat = { showAiChatByHelp = true }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showAiChatByHelp,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.zIndex(200f)
            ) {
                AiChatScreen(
                    eventId = userProfile?.currentEventId,
                    shouldStartNewSession = true,
                    onBackClick = { showAiChatByHelp = false },
                    mainNavController = mainNavController
                )
            }
        }

        val isSheetWithToastShowing = showJoinEventSheet || showEditProfile || showChangePassword

        AnimatedVisibility(
            visible = toastData.message != null && !isSheetWithToastShowing && !isAnyBottomSheetOpen,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(12.dp)
                .zIndex(1001f)
        ) {
            CustomToast(
                message = toastData.message ?: "",
                type = toastData.type
            )
        }

        if (showEditProfile) {
            val editProfilePic: Any = userProfile?.profilePictureUrl?.takeIf { it.isNotBlank() }
                ?: firebaseUser?.photoUrl
                ?: R.drawable.ic_user_profile

            EditProfileBottomSheet(
                onDismiss = { showEditProfile = false },
                userName = userName,
                userHandle = userHandle,
                profilePic = editProfilePic,
                updateState = profileUpdateState,
                resetUpdateState = { profileViewModel.resetUpdateState() },
                onUpdateProfile = { name, handle, uri, shouldRemove ->
                    profileViewModel.updateProfile(name, handle, uri, shouldRemove)
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showChangePassword) {
            val lastChangedText = TimeUtils.formatPasswordLastChanged(userProfile?.lastPasswordChangeTimestamp)
            ChangePasswordBottomSheet(
                onDismiss = {
                    showChangePassword = false
                    authViewModel.resetAuthState()
                },
                lastChangedText = lastChangedText,
                authState = authState,
                resetAuthState = { authViewModel.resetAuthState() },
                onVerifyPassword = { password, onSuccess ->
                    authViewModel.verifyPassword(password, onSuccess)
                },
                onUpdatePassword = { newPassword ->
                    authViewModel.updatePassword(newPassword)
                },
                onForgotPassword = { /* Handled via OTP service */ },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showAppTheme) {
            AppThemeBottomSheet(
                onDismiss = { showAppTheme = false },
                currentTheme = selectedTheme,
                onThemeSelected = { selectedTheme = it },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showNavBarStyle) {
            NavBarStyleBottomSheet(
                onDismiss = { showNavBarStyle = false },
                currentStyle = selectedNavBarStyle,
                onStyleSelected = { uiViewModel.updateNavBarStyle(it) },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showJoinOrCreateSheet) {
            JoinOrCreateBottomSheet(
                onDismiss = { showJoinOrCreateSheet = false },
                onCreateNewEvent = {
                    showJoinOrCreateSheet = false
                    mainNavController.navigate(Screen.EventCreationScreen.route.replace("{fromProfile}", "true"))
                },
                onJoinWithId = {
                    showJoinOrCreateSheet = false
                    enteredEventId = ""
                    verifiedEvent = null
                    joinSheetStateEnum = JoinEventSheetState.ENTER_ID
                    showJoinEventSheet = true
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showJoinEventSheet) {
            JoinEventBottomSheet(
                onDismiss = { showJoinEventSheet = false },
                currentState = joinSheetStateEnum,
                eventId = enteredEventId,
                onEventIdChange = { enteredEventId = it },
                verifiedEvent = verifiedEvent,
                isVerifying = isVerifying,
                onVerify = {
                    if (enteredEventId.isBlank()) {
                        toastData = ToastData("Please enter an Event ID", ToastType.ERROR)
                        return@JoinEventBottomSheet
                    }
                    isVerifying = true
                    coroutineScope.launch {
                        val event = eventViewModel.getEventById(enteredEventId)
                        isVerifying = false
                        if (event != null) {
                            verifiedEvent = event
                            joinSheetStateEnum = JoinEventSheetState.EVENT_DETAILS
                        } else {
                            toastData = ToastData("Invalid Event ID", ToastType.ERROR)
                        }
                    }
                },
                onJoin = {
                    val targetId = verifiedEvent?.id ?: enteredEventId
                    coroutineScope.launch {
                        val hasAccess = eventViewModel.checkUserHasAccess(targetId)
                        if (hasAccess) {
                            eventViewModel.fetchAndSetActiveEvent(targetId)
                            showJoinEventSheet = false
                            internalNavController.navigate(Screen.HomeTabScreen.Home.route) {
                                popUpTo(Screen.HomeTabScreen.Home.route) { inclusive = true }
                            }
                            currentScreen = ProfileScreen.Root
                        } else {
                            toastData = ToastData("You don't have access to this event", ToastType.ERROR)
                        }
                    }
                },
                onEditId = {
                    joinSheetStateEnum = JoinEventSheetState.ENTER_ID
                },
                toastData = toastData,
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showEventMenu && selectedEventForMenu != null) {
            val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
            val isCurrentEvent = selectedEventForMenu!!.eventId == activeEventId
            val isAdminOfEvent = userProfile?.uid == selectedEventForMenu!!.adminId

            val shufflePainter = painterResource(R.drawable.ic_shuffle)
            val infoPainter = painterResource(R.drawable.ic_info)
            val logoutPainter = painterResource(R.drawable.ic_logout)

            MenuBottomSheet(
                items = listOfNotNull(
                    if (!isCurrentEvent) {
                        listOf(
                            MenuSheetActionItem(
                                text = "Switch to Event",
                                icon = shufflePainter,
                                iconPlacement = IconPlacement.Left,
                                onClick = {
                                    eventViewModel.fetchAndSetActiveEvent(selectedEventForMenu!!.eventId)
                                    internalNavController.navigate(Screen.HomeTabScreen.Home.route) {
                                        popUpTo(Screen.HomeTabScreen.Home.route) { inclusive = true }
                                    }
                                    currentScreen = ProfileScreen.Root
                                    showEventMenu = false
                                }
                            )
                        )
                    } else null,
                    listOf(
                        MenuSheetActionItem(
                            text = "Event Detail",
                            icon = infoPainter,
                            iconPlacement = IconPlacement.Left,
                            onClick = {
                                mainNavController.navigate(Screen.EventDetail.route)
                                showEventMenu = false
                            }
                        )
                    ),
                    if (!isAdminOfEvent) {
                        listOf(
                            MenuSheetActionItem(
                                text = "Leave Event",
                                icon = logoutPainter,
                                iconPlacement = IconPlacement.Left,
                                contentColor = MaterialTheme.colorScheme.error,
                                onClick = {
                                    showEventMenu = false
                                    showLeaveConfirmation = true
                                }
                            )
                        )
                    } else null
                ),
                onCancelClick = { showEventMenu = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showLeaveConfirmation && selectedEventForMenu != null) {
            ConfirmationBottomSheet(
                heading = "Are you sure?",
                subHeading = "You will be removed from all rooms & will immediately lose access to all the information.",
                confirmButtonText = "Leave Event",
                onDismiss = { showLeaveConfirmation = false },
                isDestructive = true,
                onConfirm = {
                    eventViewModel.leaveEvent(selectedEventForMenu!!.eventId)
                    showLeaveConfirmation = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showPlanSheet) {
            when (selectedPlanForSheet) {
                PlanType.BASIC -> {
                    BasicPlanSheet(
                        onDismiss = { showPlanSheet = false },
                        onProgress = { sheetMotionProgress = it }
                    )
                }
                PlanType.PRO -> {
                    ProPlanSheet(
                        onDismiss = { showPlanSheet = false },
                        onProgress = { sheetMotionProgress = it }
                    )
                }
                PlanType.ULTIMATE -> {
                    UltimatePlanSheet(
                        onDismiss = { showPlanSheet = false },
                        onProgress = { sheetMotionProgress = it }
                    )
                }
            }
        }
    }
}