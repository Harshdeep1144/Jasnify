package com.harshdeep.jasnify.presentation.screens.onboarding.authentication

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.AuthButton
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.AuthState
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

enum class AuthTab {
    SIGN_UP, LOG_IN
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun LoginOrSignup(
    navController: NavController,
    eventId: String? = null,
    viewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {

    SetStatusBarTheme(useDarkIcons = true)

    val context = LocalContext.current
    val activity = context as? Activity
    val authState by viewModel.authState.collectAsState()

    var selectedTab by remember { mutableStateOf(AuthTab.SIGN_UP) }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Track which authentication method is currently active
    var activeAuthMethod by remember { mutableStateOf<String?>(null) }

    // State for Custom Toast
    var toastData by remember { mutableStateOf(ToastData()) }
    val haptic = LocalHapticFeedback.current

    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

    // --- Google Sign-In Configuration ---
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(com.harshdeep.jasnify.BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)

            if (account.idToken != null) {
                viewModel.signInWithGoogle(account, eventId)
            } else {
                android.util.Log.e("LoginOrSignup", "Google Sign-in failed: idToken is null")
                toastData = ToastData("Sign-in failed (null token)", ToastType.ERROR)
            }

        } catch (e: ApiException) {
            android.util.Log.e("LoginOrSignup", "Google Sign-in failed: code=${e.statusCode}, message=${e.message}")
            toastData = ToastData("Sign-in failed", ToastType.ERROR)
        }
    }


    // --- LaunchedEffect to observe AuthState and trigger navigation/feedback ---
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Loading -> {}

            is AuthState.CodeSent -> {
//                val route = Screen.OtpVerification.otpVerificationRoute(phoneNumber)
//                navController.navigate(route)
            }

            is AuthState.Success -> {
                val message = (authState as AuthState.Success).message
                toastData = ToastData(message, ToastType.SUCCESS)

                val hasEventParticipation = eventViewModel.checkIfUserParticipatesInAnyEvent()

                val destination = if (eventId != null) {
                    // If we joined via Event ID, pass it to MainAppScreen
                    android.util.Log.d("LoginOrSignup", "Navigating with Event ID: $eventId")
                    Screen.MainAppScreen.route + "?eventId=$eventId"
                } else if (hasEventParticipation) {
                    Screen.MainAppScreen.route
                } else {
                    Screen.EventCreationScreen.route
                }

                navController.navigate(destination) {
                    popUpTo(Screen.OnboardingGraph.route) { inclusive = true }
                }
                viewModel.setCompletedOnboarding(true)
                viewModel.resetAuthState()
            }

            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                toastData = ToastData(errorMessage, ToastType.ERROR)
                viewModel.resetAuthState()
            }

            else -> {}
        }
    }

    // --- LaunchedEffect to dismiss CustomToast automatically ---
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            if (toastData.type == ToastType.ERROR || toastData.message == "Authenticated with Google") {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (toastData.message?.contains("Please", ignoreCase = true) == true ||
                    toastData.message?.contains("enter", ignoreCase = true) == true ||
                    toastData.message?.contains("select", ignoreCase = true) == true ||
                    toastData.message?.contains("failed", ignoreCase = true) == true ||
                    toastData.message?.contains("password", ignoreCase = true) == true
                ) {
                    delay(80.milliseconds)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            delay(3000L.milliseconds) // Wait for 3 seconds
            toastData = toastData.copy(message = null) // Clear message to dismiss toast
        }
    }

    // Ensure bottomOffset is never negative using coerceAtLeast(0.dp)
    val imePadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val density = LocalDensity.current
    val isKeyboardOpen = WindowInsets.ime.getBottom(density) > 0

    val internalToastBottomPadding = 12.dp
    val bottomOffset = if (isKeyboardOpen) {
        // Subtract internal padding, but ensure the result is at least 0.dp
        (imePadding - internalToastBottomPadding).coerceAtLeast(0.dp)
    } else {
        10.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        Scaffold(
            containerColor = BackgroundPrimary,
            topBar = {
                Box(modifier = Modifier.statusBarsPadding()) {
                    CustomTopBar(
                        onBackClick = { navController.popBackStack() },
                        backIcon = TopIcon.Predefined.BACK_2
                    )
                }
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .width(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_app),
                                contentDescription = "App Logo"
                            )
                        }


                        // Segment Control
                        IosSegmentedControl(
                            options = AuthTab.entries.toList(),
                            selectedOption = selectedTab,
                            onOptionSelected = { selectedTab = it },
                            labelProvider = {
                                when (it) {
                                    AuthTab.SIGN_UP -> "Sign up"
                                    AuthTab.LOG_IN -> "Log in"
                                }
                            }
                        )

                        Spacer(Modifier.height(24.dp))

                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                (fadeIn() togetherWith fadeOut())
                                    .using(
                                        SizeTransform(clip = false)
                                    )
                            },
                            label = "input_mode_transition"
                        ) { tab ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                when (tab) {
                                    AuthTab.SIGN_UP -> {
                                        PrimaryInput(
                                            value = email,
                                            onValueChange = { email = it },
                                            placeholder = "Enter your email",
                                            keyboardType = KeyboardType.Email,
                                            trailingIconEnabled = true,
                                            shape = SquircleShape(CornerLarge, CornerLarge,
                                                CornerExtraSmall, CornerExtraSmall, CornerSmoothingDefault
                                            ),
                                            leadingIcon = painterResource(R.drawable.ic_mail)
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        PrimaryInput(
                                            value = password,
                                            onValueChange = { password = it },
                                            placeholder = "Create a password",
                                            keyboardType = KeyboardType.Password,
                                            trailingIconEnabled = true,
                                            shape = SquircleShape( CornerExtraSmall, CornerExtraSmall,
                                                CornerLarge, CornerLarge, CornerSmoothingDefault
                                            ),
                                            leadingIcon = painterResource(R.drawable.ic_key)
                                        )
                                    }
                                    AuthTab.LOG_IN -> {
                                        PrimaryInput(
                                            value = email,
                                            onValueChange = { email = it },
                                            placeholder = "Enter email or username",
                                            keyboardType = KeyboardType.Email,
                                            trailingIconEnabled = true,
                                            shape = SquircleShape(CornerLarge, CornerLarge,
                                                CornerExtraSmall, CornerExtraSmall, CornerSmoothingDefault
                                            ),
                                            leadingIcon = painterResource(R.drawable.ic_user_profile_circle)
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        PrimaryInput(
                                            value = password,
                                            onValueChange = { password = it },
                                            placeholder = "Enter password",
                                            keyboardType = KeyboardType.Password,
                                            trailingIconEnabled = true,
                                            shape = SquircleShape( CornerExtraSmall, CornerExtraSmall,
                                                CornerLarge, CornerLarge, CornerSmoothingDefault
                                            ),
                                            leadingIcon = painterResource(R.drawable.ic_key)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        CustomTextButton(
                            onClick = {
                                val isValid: Boolean = email.isNotBlank() && emailPattern.matches(email) && password.length >= 6
                                val errorMessage: String = if (!emailPattern.matches(email)) {
                                    "Please enter a valid email address"
                                } else {
                                    "Password must be at least 6 characters long"
                                }

                                if (isValid) {
                                    activeAuthMethod = "EMAIL"
                                    viewModel.handleEmailAuth(email, password, eventId)
                                } else {
                                    toastData = ToastData(errorMessage, ToastType.ERROR)
                                }
                            },
                            text = if (selectedTab == AuthTab.SIGN_UP) "Sign up" else "Log in",
                            modifier = Modifier.fillMaxWidth(),
                            shapeStyle = ButtonShapeStyle.Square,
                            containerColor = ContentPrimary,
                            enabled = authState !is AuthState.Loading || activeAuthMethod == "EMAIL",
                            isLoading = authState is AuthState.Loading && activeAuthMethod == "EMAIL"
                        )

                        if (selectedTab == AuthTab.LOG_IN) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "Forgot Password?",
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary,
                                modifier = Modifier.clickable { /* Do nothing for now */ }
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        OrDivider()

                        Spacer(Modifier.height(8.dp))

                        // Google Sign-In Button
                        AuthButton(
                            onClick = {
                                activeAuthMethod = "GOOGLE"
                                val signInIntent = googleSignInClient.signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            },
                            text = "Continue with Google",
                            icon = painterResource(id = R.drawable.ic_google),
                            badgeText = "Fastest & Most Used",
                            borderColor = Color(0xFF008E11).copy(0.8f),
                            enabled = authState !is AuthState.Loading || activeAuthMethod == "GOOGLE",
                            isLoading = authState is AuthState.Loading && activeAuthMethod == "GOOGLE"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Temporarily remove the phone number with otp login feature

    //                    if(isPhoneMode){
    //                        AuthButton(
    //                            onClick = {
    //                                isPhoneMode = false
    //                                viewModel.resetAuthState()
    //                            },
    //                            text = "Sign in with Email",
    //                            icon = rememberVectorPainter(Icons.Outlined.MailOutline),
    //                        )
    //                    } else {
    //                        AuthButton(
    //                            onClick = {
    //                                isPhoneMode = true
    //                                viewModel.resetAuthState()
    //                            },
    //                            text = "Sign in with Phone",
    //                            icon = rememberVectorPainter(Icons.Outlined.Phone),
    //                        )
    //                    }
                    }
                }
            }
        )

        // --- CustomToast Display  ---
        AnimatedVisibility(
            visible = toastData.message != null,
            enter = slideInVertically(initialOffsetY = { -it - 500 }),
            exit = slideOutVertically(targetOffsetY = { -it - 500 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .zIndex(99f)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            CustomToast(
                message = toastData.message ?: "",
                type = toastData.type,
                buttonText = null,
                onButtonClick = null,
            )
        }
    }
}

// --- Preview (without ViewModel) ---
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginOrSignupPreview() {
    val testNavController = rememberNavController()
    Text("Preview not supported due to Hilt ViewModel injection.")
}