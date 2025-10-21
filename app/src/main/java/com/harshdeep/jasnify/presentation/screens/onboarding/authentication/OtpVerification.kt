//package com.harshdeep.jasnify.presentation.screens.onboarding.authentication
//
//import android.app.Activity
//import android.view.KeyEvent
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.imePadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.SolidColor
//import androidx.compose.ui.input.key.KeyEventType
//import androidx.compose.ui.input.key.onKeyEvent
//import androidx.compose.ui.input.key.type
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalFocusManager
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.text.isDigitsOnly
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.harshdeep.jasnify.R
//import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
//import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
//import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
//import com.harshdeep.jasnify.presentation.navigation.Screen
//import com.harshdeep.jasnify.presentation.viewmodels.AuthState
//import com.harshdeep.jasnify.presentation.viewmodels.PhoneAuthViewModel
//import com.harshdeep.jasnify.theme.BackgroundPrimary
//import com.harshdeep.jasnify.theme.ContentPrimary
//import com.harshdeep.jasnify.theme.ContentSecondary
//import com.harshdeep.jasnify.theme.SurfaceSecondary
//import kotlinx.coroutines.delay
//
//// --- Utility Constants and Colors ---
//val CustomGreen = Color(0xFF1B5735)
//private const val OTP_LENGTH = 4 // Standard OTP length is 6 digits for Firebase
//private const val TIMER_SECONDS = 60 // Firebase default timeout is 60 seconds
//
//// --- OTP Verification Screen ---
//
//@Composable
//fun OtpVerification(
//    navController: NavController,
//    phoneNumber: String,
//    viewModel: PhoneAuthViewModel = hiltViewModel() // Inject ViewModel
//) {
//    val context = LocalContext.current
//    val activity = context as? Activity
//    val authState by viewModel.authState.collectAsState()
//
//    // State for the OTP input
//    var otpCode by remember { mutableStateOf(List<Int?>(OTP_LENGTH) { null }) }
//    var focusedIndex by remember { mutableStateOf<Int?>(0) }
//    var enteredCode by remember { mutableStateOf("") }
//
//    // Resend Timer State
//    var timeLeft by remember { mutableIntStateOf(TIMER_SECONDS) }
//    var isTimerRunning by remember { mutableStateOf(true) }
//
//    // Side effects for managing focus and keyboard
//    val focusRequesters = remember { List(OTP_LENGTH) { FocusRequester() } }
//    val focusManager = LocalFocusManager.current
//    val keyboardController = LocalSoftwareKeyboardController.current
//
//
//    // Automatic Keyboard/Focus on Launch
//    LaunchedEffect(Unit) {
//        // Request focus on the first field immediately.
//        focusRequesters.first().requestFocus()
//        delay(100)
//        keyboardController?.show()
//        // Reset timer if we navigate back to this screen
//        timeLeft = TIMER_SECONDS
//        isTimerRunning = true
//    }
//
//    // Automatically focus the correct field when 'focusedIndex' changes
//    LaunchedEffect(focusedIndex) {
//        focusedIndex?.let { index ->
//            focusRequesters.getOrNull(index)?.requestFocus()
//        }
//    }
//
//    // Handle verification when all fields are filled
//    LaunchedEffect(otpCode) {
//        val allNumbersEntered = otpCode.none { it == null }
//        enteredCode = otpCode.joinToString("")
//        if (allNumbersEntered) {
//            // Clear focus and hide keyboard, but do NOT automatically submit.
//            // Wait for user to tap the button.
//            focusManager.clearFocus()
//            keyboardController?.hide()
//        }
//    }
//
//    // Resend Timer Logic
//    LaunchedEffect(isTimerRunning) {
//        if (isTimerRunning && timeLeft > 0) {
//            while (timeLeft > 0) {
//                delay(1000)
//                timeLeft--
//            }
//            isTimerRunning = false
//        }
//    }
//
//    // --- LaunchedEffect to observe AuthState and trigger navigation/feedback ---
//    LaunchedEffect(authState) {
//        when (authState) {
//            is AuthState.Success -> {
//                Toast.makeText(context, "Sign-in successful!", Toast.LENGTH_SHORT).show()
//                navController.navigate(Screen.EventType.route) {
//                    // Clear back stack so user can't navigate back to login/OTP screens
//                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
//                }
//                viewModel.resetAuthState()
//            }
//            is AuthState.Error -> {
//                val errorMessage = (authState as AuthState.Error).message
//                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
//                // Clear OTP input on error
//                otpCode = List<Int?>(OTP_LENGTH) { null }
//                focusedIndex = 0 // Return focus
//                viewModel.resetAuthState()
//            }
//            is AuthState.Loading -> {
//                // Button handles loading state
//            }
//            is AuthState.CodeSent -> {
//                // If the user navigates back to this screen and we are already in CodeSent,
//                // we don't need to do anything, just ensure the fields are ready.
//            }
//            else -> {}
//        }
//    }
//
//    val buttonEnabled = enteredCode.length == OTP_LENGTH && authState !is AuthState.Loading
//
//    Scaffold(
//        topBar = {
//            CustomTopBar(
//                onBackClick = { navController.popBackStack() },
//                navigationIcon = painterResource(R.drawable.ic_left),
//                actionIcon = null,
//                navigationShape = ButtonShapeStyle.Round
//            )
//        },
//        content = { paddingValues ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .imePadding()
//                    .padding(paddingValues)
//                    .background(BackgroundPrimary)
//                    .padding(horizontal = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                // Header Text
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 16.dp, bottom = 32.dp),
//                    horizontalAlignment = Alignment.Start
//                ) {
//                    Text(
//                        text = "Enter the OTP",
//                        style = MaterialTheme.typography.displayLarge,
//                        color = ContentPrimary
//                    )
//                    Text(
//                        text = "For verification purpose, we have sent an OTP to your phone number $phoneNumber",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = ContentSecondary,
//                        modifier = Modifier.padding(top = 4.dp)
//                    )
//                }
//
//                // OTP Input Field
//                OtpInput(
//                    otpCode = otpCode,
//                    focusRequesters = focusRequesters,
//                    onCodeChanged = { newCode ->
//                        otpCode = newCode
//                    },
//                    onFocusedIndexChanged = { newIndex ->
//                        focusedIndex = newIndex
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                        .padding(30.dp, 10.dp)
//                )
//
//                // Resend Timer/Link
//                ResendOtpLink(
//                    timeLeft = timeLeft,
//                    isTimerRunning = isTimerRunning,
//                    onResendClick = {
//                        // Resend logic: Call viewModel function to resend code
//                        if (activity != null) {
//                            viewModel.sendVerificationCode(phoneNumber, activity)
//                            // Reset local UI state
//                            timeLeft = TIMER_SECONDS
//                            isTimerRunning = true
//                            otpCode = List<Int?>(OTP_LENGTH) { null } // Clear OTP input
//                            focusedIndex = 0 // Return focus to the first box
//                            Toast.makeText(context, "Resending code to $phoneNumber", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(context, "Activity context error.", Toast.LENGTH_SHORT).show()
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                // Validation Text (optional, can be replaced by Toast on error)
//                if (authState is AuthState.Error) {
//                    Text(
//                        text = (authState as AuthState.Error).message,
//                        color = Color.Red,
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.SemiBold,
//                        modifier = Modifier.padding(vertical = 16.dp)
//                    )
//                }
//
//
//                // Spacer to push the button to the bottom/above the keyboard
//                Spacer(modifier = Modifier.weight(1f))
//
//                // Verify Button
//                CustomTextButton(
//                    onClick = {
//                        // Call ViewModel to verify the code
//                        viewModel.verifyCode(enteredCode, context)
//                    },
//                    text = if (authState is AuthState.Loading) "Verifying..." else "Verify & Continue",
//                    shapeStyle = ButtonShapeStyle.Square,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp),
//                    enabled = buttonEnabled,
//                )
//            }
//        }
//    )
//}
//
//// --- Resend OTP Timer Composable (Unchanged) ---
//
//@Composable
//fun ResendOtpLink(
//    timeLeft: Int,
//    isTimerRunning: Boolean,
//    onResendClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val minutes = timeLeft / 60
//    val seconds = timeLeft % 60
//    val timeText = String.format("%d:%02d", minutes, seconds)
//
//    Row(
//        modifier = modifier,
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        if (isTimerRunning) {
//            Text(
//                text = "Resend OTP in: $timeText",
//                fontSize = 14.sp,
//                color = ContentSecondary,
//                fontWeight = FontWeight.SemiBold
//            )
//        } else {
//            // Clickable link when the timer runs out
//            Text(
//                text = "Resend OTP",
//                fontSize = 14.sp,
//                color = CustomGreen,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier
//                    .clickable { onResendClick() }
//            )
//        }
//    }
//}
//
//// --- OTP Input Composable (Minor change to reflect OTP_LENGTH=6) ---
//
//@Composable
//private fun OtpInput(
//    otpCode: List<Int?>,
//    focusRequesters: List<FocusRequester>,
//    onCodeChanged: (List<Int?>) -> Unit,
//    onFocusedIndexChanged: (Int?) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier.padding(vertical = 16.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
//    ) {
//        otpCode.forEachIndexed { index, number ->
//            OtpInputField(
//                number = number,
//                focusRequester = focusRequesters[index],
//                onFocusChanged = { isFocused ->
//                    if (isFocused) {
//                        onFocusedIndexChanged(index)
//                    }
//                },
//                onNumberChanged = { newNumber ->
//                    val newCode = otpCode.toMutableList().apply { this[index] = newNumber }
//
//                    // Auto-advance logic: move to next field if a digit was entered
//                    val nextIndex = if (newNumber != null && index < OTP_LENGTH - 1) {
//                        index + 1
//                    } else {
//                        null // Finished or stay put
//                    }
//
//                    onCodeChanged(newCode)
//                    if (newNumber != null) {
//                        onFocusedIndexChanged(nextIndex)
//                    }
//                },
//                onKeyboardBack = {
//                    // Backspace on an empty field: move to previous and clear it
//                    val previousIndex = (index - 1).coerceAtLeast(0)
//
//                    if (index > 0) {
//                        val newCode = otpCode.toMutableList().apply { this[previousIndex] = null }
//                        onCodeChanged(newCode)
//                        onFocusedIndexChanged(previousIndex)
//                    }
//                },
//                modifier = Modifier
//                    .weight(1f)
//                    .aspectRatio(1f)
//            )
//        }
//    }
//}
//
//@Composable
//private fun OtpInputField(
//    number: Int?,
//    focusRequester: FocusRequester,
//    onFocusChanged: (Boolean) -> Unit,
//    onNumberChanged: (Int?) -> Unit,
//    onKeyboardBack: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var isFocused by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = modifier
//            .border(
//                width = 1.dp,
//                color = if (isFocused) ContentPrimary else Color.Unspecified,
//                shape = RoundedCornerShape(12.dp)
//            )
//            .background(SurfaceSecondary, RoundedCornerShape(8.dp)), // Use white background for visibility
//        contentAlignment = Alignment.Center
//    ) {
//        BasicTextField(
//            value = number?.toString().orEmpty(),
//            onValueChange = { newText ->
//                val newNumberText = newText
//                // Only accept a single digit and ensure it's a digit
//                if (newNumberText.length <= 1 && newNumberText.isDigitsOnly()) {
//                    onNumberChanged(newNumberText.toIntOrNull())
//                }
//                // Handle pasting: only take the first digit
//                else if (newNumberText.length > 1 && newNumberText.isDigitsOnly()) {
//                    onNumberChanged(newNumberText.substring(0, 1).toIntOrNull())
//                }
//            },
//            cursorBrush = SolidColor(ContentPrimary),
//            singleLine = true,
//            textStyle = MaterialTheme.typography.displayLarge.copy(
//                textAlign = TextAlign.Center,
//                color = ContentPrimary
//            ),
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Number // Use numeric keyboard
//            ),
//            modifier = Modifier
//                .padding(10.dp)
//                .focusRequester(focusRequester)
//                .onFocusChanged {
//                    isFocused = it.isFocused
//                    onFocusChanged(it.isFocused)
//                }
//                .onKeyEvent { event ->
//                    val didPressDelete = event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL
//                    val isKeyUp = event.type == KeyEventType.KeyUp
//
//                    // Handle backspace only when the current field is empty and key is released
//                    if (didPressDelete && isKeyUp && number == null) {
//                        onKeyboardBack()
//                        return@onKeyEvent true // Consume the event
//                    }
//                    false // Allow other events (like typing) to be processed
//                },
//            decorationBox = { innerBox ->
//                // Display content when a number not is present
//                innerBox()
//                if(!isFocused && number == null) {
//                    Text(
//                        text = "-",
//                        textAlign = TextAlign.Center,
//                        color = ContentPrimary,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Light,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .wrapContentSize()
//                    )
//                }
//            }
//        )
//    }
//}
//
//
//// --- Preview ---
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun OtpVerificationPreview() {
//    val testNavController = rememberNavController()
//    OtpVerification(testNavController, phoneNumber = "+919876543210")
//}
//
