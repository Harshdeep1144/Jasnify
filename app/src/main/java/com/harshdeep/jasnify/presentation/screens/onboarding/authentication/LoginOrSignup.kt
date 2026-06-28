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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
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

// --- Toast State Management ---
data class ToastData(
    val message: String? = null,
    val type: ToastType = ToastType.DEFAULT
)

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

    var isPhoneMode by remember { mutableStateOf(false) } // true = phone, false = email
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
            delay(3000L) // Wait for 3 seconds
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

    Scaffold(
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
                    .background(BackgroundPrimary)
                    .padding(paddingValues)
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundPrimary),
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

                    Spacer(Modifier.height(16.dp))

                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "Log in or Sign up",
                        style = MaterialTheme.typography.displayMedium,
                        color = ContentPrimary
                    )

                    Spacer(Modifier.height(16.dp))

                    AnimatedContent(
                        targetState = isPhoneMode,
                        transitionSpec = {
                            (fadeIn() togetherWith fadeOut())
                                .using(
                                     SizeTransform(clip = false)
                                )
                        },
                        label = "input_mode_transition"
                    ) { phoneMode ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (phoneMode) {
                                PhoneNumberInput(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it }
                                )
                            } else {
                                PrimaryInput(
                                    value = email,
                                    onValueChange = { email = it },
                                    placeholder = "Enter email address",
                                    keyboardType = KeyboardType.Email
                                )
                                Spacer(Modifier.height(8.dp))
                                PrimaryInput(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = "Enter password",
                                    keyboardType = KeyboardType.Password
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    CustomTextButton(
                        onClick = {
                            val isValid: Boolean
                            val errorMessage: String

                            if (isPhoneMode) {
                                isValid = phoneNumber.isNotBlank() && phoneNumber.length in 10..15
                                errorMessage = "Please enter a valid phone number"
                            } else {
                                isValid = email.isNotBlank() && emailPattern.matches(email) && password.length >= 6
                                errorMessage = if (!emailPattern.matches(email)) {
                                    "Please enter a valid email address"
                                } else {
                                    "Password must be at least 6 characters long"
                                }
                            }

                            if (isValid) {
                                if (isPhoneMode && activity != null) {
                                    viewModel.sendVerificationCode(phoneNumber, activity)
                                } else if (!isPhoneMode) {
                                    viewModel.handleEmailAuth(email, password)
                                }
                            } else {
                                toastData = ToastData(errorMessage, ToastType.ERROR)
                            }
                        },
                        text = if (authState is AuthState.Loading) "Loading..." else "Continue",
                        size = ButtonSize.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        type = ButtonType.Primary,
                        shapeStyle = ButtonShapeStyle.Square,
                    )

                    OrDivider()

                    // Google Sign-In Button
                    AuthButton(
                        onClick = {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        },
                        text = "Sign in with Google",
                        icon = painterResource(id = R.drawable.ic_google)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if(isPhoneMode){
                        AuthButton(
                            onClick = {
                                isPhoneMode = false
                                viewModel.resetAuthState()
                            },
                            text = "Sign in with Email",
                            icon = rememberVectorPainter(Icons.Outlined.MailOutline),
                        )
                    } else {
                        AuthButton(
                            onClick = {
                                isPhoneMode = true
                                viewModel.resetAuthState()
                            },
                            text = "Sign in with Phone",
                            icon = rememberVectorPainter(Icons.Outlined.Phone),
                        )
                    }

                }

                // --- CustomToast Display  ---
                AnimatedVisibility(
                    visible = toastData.message != null,
                    enter = slideInVertically(initialOffsetY = { -it }),
                    exit = slideOutVertically(targetOffsetY = { -it }),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
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
    )
}

// --- Preview remains the same (without ViewModel) ---
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginOrSignupPreview() {
    val testNavController = rememberNavController()
    Text("Preview not supported due to Hilt ViewModel injection.")
}