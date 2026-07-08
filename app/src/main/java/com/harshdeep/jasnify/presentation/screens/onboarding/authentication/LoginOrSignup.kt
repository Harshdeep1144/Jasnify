package com.harshdeep.jasnify.presentation.screens.onboarding.authentication

import android.app.Activity
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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.harshdeep.jasnify.presentation.components.buttons.*
import com.harshdeep.jasnify.presentation.components.inputfield.PhoneNumberInput
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.util.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.AuthState
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.Pattaya
import androidx.compose.foundation.clickable
import com.harshdeep.jasnify.presentation.components.others.ToastData

enum class AuthTab {
    SIGN_UP, LOG_IN
}

@Composable
fun LoginOrSignup(
    navController: NavController,
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

    // State for Custom Toast
    var toastData by remember { mutableStateOf(ToastData()) }

    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

    // --- Google Sign-In Configuration ---
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
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
                viewModel.signInWithGoogle(account)
            } else {
                toastData = ToastData("Sign-in failed", ToastType.ERROR)
            }

        } catch (e: ApiException) {
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

                val hasCompletedEventCreation = eventViewModel.checkIfUserHasEventsInDatabase()

                val destination = if (hasCompletedEventCreation) {
                    Screen.MainAppScreen.route
                } else {
                    Screen.EventCreationScreen.route
                }

                navController.navigate(destination) {
                    popUpTo(Screen.LoginOrSignUp.route) { inclusive = true }
                }
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
                                            trailingIconEnabled = true
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        PrimaryInput(
                                            value = password,
                                            onValueChange = { password = it },
                                            placeholder = "Create a password",
                                            keyboardType = KeyboardType.Password,
                                            trailingIconEnabled = true
                                        )
                                    }
                                    AuthTab.LOG_IN -> {
                                        PrimaryInput(
                                            value = email,
                                            onValueChange = { email = it },
                                            placeholder = "Enter email or username",
                                            keyboardType = KeyboardType.Email,
                                            trailingIconEnabled = true
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        PrimaryInput(
                                            value = password,
                                            onValueChange = { password = it },
                                            placeholder = "Enter password",
                                            keyboardType = KeyboardType.Password,
                                            trailingIconEnabled = true
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
                                    viewModel.handleEmailAuth(email, password)
                                } else {
                                    toastData = ToastData(errorMessage, ToastType.ERROR)
                                }
                            },
                            text = if (authState is AuthState.Loading) "Loading..." else if (selectedTab == AuthTab.SIGN_UP) "Sign up" else "Log in",
                            modifier = Modifier.fillMaxWidth(),
                            shapeStyle = ButtonShapeStyle.Square,
                            containerColor = ContentPrimary,
                        )

                        if (selectedTab == AuthTab.LOG_IN) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "Forgot Password?",
                                style = MaterialTheme.typography.bodyLarge,
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
                                val signInIntent = googleSignInClient.signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            },
                            text = "Continue with Google",
                            icon = painterResource(id = R.drawable.ic_google),
                            badgeText = "Fastest & Most Used",
                            borderColor = Color(0xFF008E11).copy(0.8f),
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