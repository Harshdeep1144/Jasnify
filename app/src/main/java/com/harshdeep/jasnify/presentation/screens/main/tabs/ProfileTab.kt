package com.harshdeep.jasnify.presentation.screens.main.tabs

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.UserEvent
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AppThemeBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AppThemeOption
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ChangePasswordBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.EditProfileBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.JoinEventBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.JoinEventSheetState
import com.harshdeep.jasnify.presentation.components.bottomdrawer.JoinOrCreateBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.NavBarStyleBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.NavBarStyleOption
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.EnquiryCard
import com.harshdeep.jasnify.presentation.components.cards.ManageEventCard
import com.harshdeep.jasnify.presentation.components.cards.PlanCard
import com.harshdeep.jasnify.presentation.components.cards.ProfileMenuCell
import com.harshdeep.jasnify.presentation.components.dialogs.AccountDeletionDialog
import com.harshdeep.jasnify.presentation.components.dialogs.ConfirmationDialog
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.utils.TimeUtils
import com.harshdeep.jasnify.presentation.viewmodels.AuthState
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.ProfileUpdateState
import com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel
import com.harshdeep.jasnify.presentation.viewmodels.UIViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

enum class ProfileScreen {
    Root,
    AccountSettings,
    Appearance,
    ManageEvents,
    MyEnquiries,
    Notifications,
    TermsAndConditions,
    PrivacyPolicy
}

@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTab(
    mainNavController: NavHostController,
    internalNavController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    venueViewModel: VenueViewModel = hiltViewModel(),
    enquiryViewModel: EnquiryViewModel = hiltViewModel()
) {
    val mainGraphEntry = remember(mainNavController) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
    val uiViewModel: UIViewModel = hiltViewModel(mainGraphEntry)
    val eventViewModel: EventViewModel = hiltViewModel(mainGraphEntry)
    val context = LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val firebaseUser = auth.currentUser

    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val ownedEvents by eventViewModel.userEvents.collectAsStateWithLifecycle()
    
    // Stop listening to enquiries if the user is null or being deleted
    val enquiries by remember(firebaseUser?.uid) {
        if (firebaseUser?.uid != null) {
            enquiryViewModel.getEnquiriesForUser(firebaseUser.uid)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.collectAsState(emptyList())

    // Ensure owned events are fetched for old accounts
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

    // Prioritize Cloudinary URL from profile. Fallback to Google only if no profile exists yet.
    val profilePic: Any = if (userProfile != null) {
        userProfile?.profilePictureUrl ?: R.drawable.ic_user_profile
    } else {
        firebaseUser?.photoUrl ?: R.drawable.ic_user_profile
    }

    var currentScreen by rememberSaveable { mutableStateOf(ProfileScreen.Root) }

    var showEditProfile by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showAppTheme by remember { mutableStateOf(false) }
    var showNavBarStyle by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    var showJoinOrCreateSheet by remember { mutableStateOf(false) }
    var showJoinEventSheet by remember { mutableStateOf(false) }
    var showEventMenu by remember { mutableStateOf(false) }
    var selectedEventForMenu by remember { mutableStateOf<UserEvent?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var joinSheetStateEnum by remember { mutableStateOf(JoinEventSheetState.ENTER_ID) }
    var enteredEventId by remember { mutableStateOf("") }
    var verifiedEvent by remember { mutableStateOf<Event?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    // Real-time drag progress ratio (0.0f = fully open sheet, 1.0f = fully dismissed sheet)
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
                    showLeaveConfirmation
        }
    }

    LaunchedEffect(currentScreen, isAnyBottomSheetOpen) {
        onBottomBarVisibilityChange(currentScreen == ProfileScreen.Root && !isAnyBottomSheetOpen)
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

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
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
        if (authState is AuthState.Success) {
            if ((authState as AuthState.Success).message == "Password updated successfully") {
                delay(2000.milliseconds)
                showChangePassword = false
                authViewModel.resetAuthState()
            } else if ((authState as AuthState.Success).message == "Account deleted successfully" || 
                (authState as AuthState.Success).message == "Account deletion requested") {
                // Important: clear local data immediately to trigger recomposition 
                // and stop listeners before the navigation delay
                eventViewModel.clearActiveEvent()
                
                toastData = ToastData((authState as AuthState.Success).message, ToastType.SUCCESS)
                delay(2000.milliseconds)
                
                mainNavController.navigate(Screen.OnboardingGraph.route) {
                    popUpTo(0) { inclusive = true }
                }
                authViewModel.resetAuthState()
            }
        } else if (authState is AuthState.Error) {
            toastData = ToastData((authState as AuthState.Error).message, ToastType.ERROR)
            authViewModel.resetAuthState()
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

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = backdropScale
                    scaleY = backdropScale
                    clip = isAnyBottomSheetOpen || backdropCornerRadius > 0.dp
                    shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                }
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    if (targetState == ProfileScreen.Root) {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
                    } else {
                        (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
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
                        ProfileTabContent(
                            userName = userName,
                            userHandle = userHandle,
                            profilePic = profilePic,
                            eventCount = eventCount,
                            enquiryCount = enquiryCount,
                            onEditProfile = { showEditProfile = true },
                            onNavigateTo = { currentScreen = it },
                            onLogout = { showLogoutDialog = true }
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
                            onBack = { currentScreen = ProfileScreen.Root }
                        )
                    }

                    ProfileScreen.TermsAndConditions -> {
                        LegalScreen(
                            title = "Terms & Conditions",
                            onBack = { currentScreen = ProfileScreen.Root }
                        )
                    }

                    ProfileScreen.PrivacyPolicy -> {
                        LegalScreen(
                            title = "Privacy Policy",
                            onBack = { currentScreen = ProfileScreen.Root }
                        )
                    }
                }
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
            EditProfileBottomSheet(
                onDismiss = { showEditProfile = false },
                userName = userName,
                userHandle = userHandle,
                profilePic = profilePic,
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
                onForgotPassword = { /* Need OTP Service */ },
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

            MenuBottomSheet(
                items = listOfNotNull(
                    if (!isCurrentEvent) {
                        listOf(
                            MenuSheetActionItem(
                                text = "Switch to Event",
                                icon = painterResource(R.drawable.ic_shuffle),
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
                            icon = painterResource(R.drawable.ic_info),
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
                                icon = painterResource(R.drawable.ic_logout),
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
                subHeading = "You will be removed from all rooms & will immediately loose access to all the information.",
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
    }
}


// ============================================================================================================================================
// ROOT SCREEN: PROFILE CONTENT
// ============================================================================================================================================

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileTabContent(
    userName: String,
    userHandle: String,
    profilePic: Any,
    eventCount: Int,
    enquiryCount: Int,
    onEditProfile: () -> Unit,
    onNavigateTo: (ProfileScreen) -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. User Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp, 28.dp, 28.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(SurfaceSecondary)
                ) {
                    AsyncImage(
                        model = profilePic,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_user_profile)
                    )
                }
                Spacer(Modifier.height(12.dp))

                Text(
                    text = userName,
                    style = JasnifyTheme.typography.displaySmall.copy(fontWeight = FontWeight.Medium),
                    color = ContentPrimary
                )
                Text(
                    text = userHandle,
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
                Spacer(Modifier.height(16.dp))

                CustomTextButton(
                    onClick = onEditProfile,
                    text = "Edit Profile",
                    size = ButtonSize.Small,
                    leadingIcon = painterResource(R.drawable.ic_edit),
                    type = ButtonType.Secondary
                )
            }
        }

        // 2. Plan Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlanCard(
                    planName = "Basic Plan",
                    price = "FREE",
                    backgroundColor = Color(0xFFF4E3E2),
                    isCurrentPlan = true,
                    onViewBenefitsClick = {}
                )
                PlanCard(
                    planName = "Pro",
                    price = "$5/month",
                    backgroundColor = Color(0xFFFFDAB9),
                    buttonText = "Upgrade Now",
                    onButtonClick = {},
                    onViewBenefitsClick = {}
                )
                PlanCard(
                    planName = "Ultimate",
                    price = "$20/month",
                    backgroundColor = Color(0xFFD3CDE8),
                    buttonText = "Upgrade Now",
                    onButtonClick = {},
                    onViewBenefitsClick = {}
                )
            }
        }

        // 3. Grid Actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileGridCell(
                    title = "Manage Events",
                    subtitle = if (eventCount == 1) "1 Event" else "$eventCount Events",
                    icon = painterResource(R.drawable.ic_events_stack),
                    onClick = { onNavigateTo(ProfileScreen.ManageEvents) },
                    modifier = Modifier.weight(1f)
                )
                ProfileGridCell(
                    title = "My Enquiries",
                    subtitle = if (enquiryCount == 1) "1 Enquiry" else "$enquiryCount Enquiries",
                    icon = painterResource(R.drawable.ic_message_typing),
                    onClick = { onNavigateTo(ProfileScreen.MyEnquiries) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Menu Items
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Account Settings Group
                ProfileMenuCell(
                    title = "Account Settings",
                    subtitle = null,
                    icon = painterResource(R.drawable.ic_profile),
                    hasBorder = true,
                    onClick = { onNavigateTo(ProfileScreen.AccountSettings) }
                )

                // Appearance & Notifications Group
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                        )
                        .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .background(SurfacePrimary)
                ) {
                    ProfileMenuCell(
                        title = "Appearance",
                        subtitle = null,
                        icon = painterResource(R.drawable.ic_paint),
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = { onNavigateTo(ProfileScreen.Appearance) }
                    )
                    ProfileMenuCell(
                        title = "Notifications",
                        subtitle = "On",
                        icon = painterResource(R.drawable.ic_notification),
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = { onNavigateTo(ProfileScreen.Notifications) }
                    )
                }

                // Legal Group
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                        )
                        .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .background(SurfacePrimary)
                ) {
                    ProfileMenuCell(
                        title = "Terms & Conditions",
                        subtitle = null,
                        icon = painterResource(R.drawable.ic_terms_and_conditions),
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = { onNavigateTo(ProfileScreen.TermsAndConditions) }
                    )
                    ProfileMenuCell(
                        title = "Privacy Policy",
                        subtitle = null,
                        icon = painterResource(R.drawable.ic_privacy_policy),
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = { onNavigateTo(ProfileScreen.PrivacyPolicy) }
                    )
                }

                // Logout
                ProfileMenuCell(
                    title = "Logout",
                    subtitle = null,
                    icon = painterResource(R.drawable.ic_logout),
                    hasBorder = true,
                    onClick = onLogout,
                    contentColor = MaterialTheme.colorScheme.error
                )
            }
        }

        // 5. Footer
        item {
            FooterJansify()
        }
    }
}

// ============================================================================================================================================
// SCREEN 1: ACCOUNT SETTINGS
// ============================================================================================================================================

@Composable
fun AccountSettingsScreen(
    email: String,
    isGoogleUser: Boolean,
    lastChangedText: String,
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Account Settings",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        SquircleShape(CornerLarge, CornerSmoothingDefault)
                    )
                    .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                    .background(SurfacePrimary)
            ) {
                ProfileMenuCell(
                    title = "Email ID",
                    subtitle = email,
                    icon = painterResource(R.drawable.ic_mail),
                    hasBorder = false,
                    showArrow = false,
                    shape = RectangleShape,
                    containerColor = Color.Transparent
                )
                if (!isGoogleUser) {
                    ProfileMenuCell(
                        title = "Password",
                        subtitle = lastChangedText,
                        icon = painterResource(R.drawable.ic_key),
                        hasBorder = false,
                        shape = RectangleShape,
                        containerColor = Color.Transparent,
                        onClick = onChangePassword
                    )
                }
            }

            ProfileMenuCell(
                title = "Delete Account",
                subtitle = null,
                icon = painterResource(R.drawable.ic_delete),
                containerColor = SurfacePrimary,
                contentColor = MaterialTheme.colorScheme.error,
                onClick = onDeleteAccount
            )
        }
    }
}

// ============================================================================================================================================
// SCREEN 2: APPEARANCE
// ============================================================================================================================================

@Composable
fun AppearanceScreen(
    currentTheme: AppThemeOption,
    currentNavBarStyle: NavBarStyleOption,
    onBack: () -> Unit,
    onChangeTheme: () -> Unit,
    onChangeNavBarStyle: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Appearance",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        SquircleShape(CornerLarge, CornerSmoothingDefault)
                    )
                    .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                    .background(SurfacePrimary)
            ) {
                ProfileMenuCell(
                    title = "Theme",
                    subtitle = currentTheme.label,
                    icon = painterResource(R.drawable.ic_paint),
                    hasBorder = false,
                    shape = RectangleShape,
                    containerColor = Color.Transparent,
                    onClick = onChangeTheme
                )
                ProfileMenuCell(
                    title = "Nav Bar Style",
                    subtitle = currentNavBarStyle.label,
                    icon = painterResource(R.drawable.ic_home),
                    hasBorder = false,
                    shape = RectangleShape,
                    containerColor = Color.Transparent,
                    onClick = onChangeNavBarStyle
                )
            }
        }
    }
}

// ============================================================================================================================================
// SCREEN 3: MANAGE EVENTS
// ============================================================================================================================================

@Composable
fun ManageEventsScreen(
    userProfile: com.harshdeep.jasnify.domain.model.User?,
    eventViewModel: EventViewModel,
    mainNavController: NavHostController,
    ownedEvents: List<com.harshdeep.jasnify.domain.model.Event>,
    onBack: () -> Unit,
    onEventClick: (String) -> Unit,
    onJoinOrCreateClick: () -> Unit,
    onShowMenu: (UserEvent) -> Unit
) {
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val isUserEventsLoading by eventViewModel.isUserEventsLoading.collectAsStateWithLifecycle()
    val joinedEvents = userProfile?.joinedEvents ?: emptyList()

    // Merge owned events for old accounts that don't have joinedEvents populated
    val allUserEvents = remember(ownedEvents, joinedEvents) {
        val ownedAsUserEvents = ownedEvents.map { event ->
            UserEvent(
                eventId = event.id,
                eventName = event.name,
                adminId = event.ownerId,
                roomRoles = mapOf(
                    "Budget" to UserRole.OWNER,
                    "Catering" to UserRole.OWNER,
                    "Checklist" to UserRole.OWNER,
                    "Vendors" to UserRole.OWNER,
                    "Venue" to UserRole.OWNER
                )
            )
        }
        // Deduplicate: Prioritize joinedEvents as they have more granular role/screen info if shared
        (ownedAsUserEvents + joinedEvents).distinctBy { it.eventId }
    }

    // Redirect to event creation if no events found and loading is complete
    LaunchedEffect(allUserEvents, isUserEventsLoading, userProfile) {
        if (!isUserEventsLoading && userProfile != null && allUserEvents.isEmpty()) {
            mainNavController.navigate(Screen.OnboardingType.route)
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Manage Events",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = BottomGradientBrush
                    )
                    .navigationBarsPadding()
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(62.dp)
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
                    CustomTextButton(
                        onClick = onJoinOrCreateClick,
                        text = "Join or Create Event",
                        type = ButtonType.Primary,
                        shapeStyle = ButtonShapeStyle.Round,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                }
            }
        }
    ) { padding ->
        if (allUserEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = ContentSecondary)
                    Text(
                        text = "Redirecting to event creation...",
                        color = ContentSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allUserEvents) { userEvent ->
                    ManageEventCard(
                        userEvent = userEvent,
                        isActive = userEvent.eventId == activeEventId,
                        isAdmin = userProfile?.uid == userEvent.adminId,
                        onEventClick = { onEventClick(userEvent.eventId) },
                        onMenuClick = {
                            onShowMenu(userEvent)
                        }
                    )
                }
            }
        }
    }
}

// ============================================================================================================================================
// SCREEN 4: MY ENQUIRIES
// ============================================================================================================================================

@Composable
fun MyEnquiriesScreen(
    enquiryViewModel: EnquiryViewModel,
    userId: String,
    onBack: () -> Unit,
    onEnquiryClick: (com.harshdeep.jasnify.domain.model.Enquiry) -> Unit
) {
    val enquiries by enquiryViewModel.getEnquiriesForUser(userId).collectAsState(emptyList())

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "My Enquiries",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(enquiries) { enquiry ->
                EnquiryCard(
                    enquiry = enquiry,
                    onClick = { onEnquiryClick(enquiry) },
                    currentUserId = userId
                )
            }
        }
    }
}

// ============================================================================================================================================
// SCREEN 5: NOTIFICATIONS
// ============================================================================================================================================

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Notifications",
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ProfileMenuCell(
                title = "Push Notifications",
                subtitle = "On",
                icon = painterResource(R.drawable.ic_info),
                hasBorder = true
            )
        }
    }
}

// ============================================================================================================================================
// LEGAL SCREEN: TERMS / PRIVACY
// ============================================================================================================================================

@Composable
fun LegalScreen(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = title,
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Legal content for $title goes here...",
                style = JasnifyTheme.typography.bodyLarge,
                color = ContentPrimary
            )
        }
    }
}

@Composable
fun ProfileGridCell(
    title: String,
    subtitle: String? = null,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
            )
            .clickable { onClick() },
        color = SurfacePrimary,
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = ContentPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ProfileTabPreview() {
    JasnifyTheme {
        ProfileTabContent(
            userName = "Anand K.",
            userHandle = "@viratanand",
            profilePic = R.drawable.ic_user_profile,
            eventCount = 2,
            enquiryCount = 5,
            onEditProfile = {},
            onNavigateTo = {},
            onLogout = {}
        )
    }
}
