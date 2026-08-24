package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.InfoCard
import com.harshdeep.jasnify.presentation.components.cards.InfoCardNature
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.presentation.viewmodels.AuthState
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds


enum class ChangePasswordStep {
    CURRENT_PASSWORD,
    NEW_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordBottomSheet(
    onDismiss: () -> Unit,
    lastChangedText: String = "Password never changed",
    onVerifyPassword: (String, () -> Unit) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onForgotPassword: () -> Unit,
    authState: AuthState = AuthState.Idle,
    resetAuthState: () -> Unit = {},
    onProgress: ((Float) -> Unit)? = null
) {
    var currentStep by remember { mutableStateOf(ChangePasswordStep.CURRENT_PASSWORD) }
    var toastData by remember { mutableStateOf(ToastData()) }
    var activeToastData by remember { mutableStateOf<ToastData?>(null) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            if (toastData.type == ToastType.ERROR) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (toastData.message?.contains("Please", ignoreCase = true) == true ||
                    toastData.message?.contains("enter", ignoreCase = true) == true ||
                    toastData.message?.contains("same", ignoreCase = true) == true ||
                    toastData.message?.contains("weak", ignoreCase = true) == true
                ) {
                    delay(80.milliseconds)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            activeToastData = toastData
            delay(2000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                toastData = ToastData(authState.message, ToastType.ERROR)
                resetAuthState()
            }
            is AuthState.Success -> {
                if (authState.message == "Password updated successfully") {
                    toastData = ToastData(authState.message, ToastType.SUCCESS)
                }
            }
            else -> {}
        }
    }

    val animatedSheetHeight = if (currentStep == ChangePasswordStep.NEW_PASSWORD) 440.dp else 280.dp

    CustomBottomSheet(
        heading = "Change Password",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = animatedSheetHeight,
        showDragHandle = true,
        showCloseButton = true,
        hasToast = toastData.message != null,
        toast = {
            AnimatedVisibility(
                visible = toastData.message != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(12.dp)
            ) {
                activeToastData?.let { data ->
                    CustomToast(
                        message = data.message ?: "",
                        type = data.type
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = animatedSheetHeight)
            ) {
                ChangePasswordContent(
                    currentStep = currentStep,
                    onStepChange = { currentStep = it },
                    lastChangedText = lastChangedText,
                    onVerifyPassword = onVerifyPassword,
                    onUpdatePassword = onUpdatePassword,
                    onForgotPassword = {
                        toastData = ToastData("otp service is currently not available", ToastType.DEFAULT)
                        onForgotPassword()
                    },
                    onWeakPassword = {
                        toastData = ToastData("password too weak", ToastType.ERROR)
                    },
                    onPasswordMismatch = {
                        toastData = ToastData("enter same password", ToastType.ERROR)
                    },
                    isLoading = authState is AuthState.Loading
                )
            }
        }
    }
}

private fun isStrongPassword(password: String): Boolean {
    val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^$*?])[A-Za-z\\d@$!%*?&#^$*?]{8,}$"
    return Regex(passwordPattern).matches(password)
}

@Composable
fun ChangePasswordContent(
    currentStep: ChangePasswordStep = ChangePasswordStep.CURRENT_PASSWORD,
    onStepChange: (ChangePasswordStep) -> Unit = {},
    lastChangedText: String = "Last changed 3 months ago",
    onVerifyPassword: (String, () -> Unit) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onForgotPassword: () -> Unit,
    onWeakPassword: () -> Unit = {},
    onPasswordMismatch: () -> Unit = {},
    isLoading: Boolean = false
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var logoutOtherDevices by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
            if (targetState == ChangePasswordStep.NEW_PASSWORD) {
                (slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn()).togetherWith(
                    slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut()
                )
            } else {
                (slideInHorizontally(animationSpec = tween(300)) { -it } + fadeIn()).togetherWith(
                    slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut()
                )
            }
        },
        label = "ChangePasswordStepTransition"
    ) { step ->
        when (step) {
            ChangePasswordStep.CURRENT_PASSWORD -> {
                CurrentPasswordContent(
                    password = currentPassword,
                    onPasswordChange = { currentPassword = it },
                    lastChangedText = lastChangedText,
                    onForgotPassword = onForgotPassword,
                    isLoading = isLoading,
                    onNext = {
                        if (currentPassword.isNotEmpty() && !isLoading) {
                            onVerifyPassword(currentPassword) {
                                onStepChange(ChangePasswordStep.NEW_PASSWORD)
                            }
                        }
                    }
                )
            }
            ChangePasswordStep.NEW_PASSWORD -> {
                NewPasswordContent(
                    newPassword = newPassword,
                    onNewPasswordChange = { newPassword = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    logoutOtherDevices = logoutOtherDevices,
                    onLogoutOtherDevicesChange = { logoutOtherDevices = it },
                    isLoading = isLoading,
                    onUpdate = {
                        if ((newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) && !isLoading) {
                            if (newPassword == confirmPassword) {
                                if (isStrongPassword(newPassword)) {
                                    onUpdatePassword(newPassword)
                                } else {
                                    onWeakPassword()
                                }
                            } else {
                                onPasswordMismatch()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CurrentPasswordContent(
    password: String,
    onPasswordChange: (String) -> Unit,
    lastChangedText: String,
    onForgotPassword: () -> Unit,
    isLoading: Boolean,
    onNext: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Scrollable content section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(12.dp)
        ) {
            Text(
                text = "Current Password",
                style = JasnifyTheme.typography.headingMedium,
                color = ContentSecondary,
            )
            Spacer(Modifier.height(8.dp))

            PrimaryInput(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "Enter Current Password",
                keyboardType = KeyboardType.Password
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = ContentSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = lastChangedText,
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CustomTextButton(
                    text = "Forgot Password?",
                    onClick = { onForgotPassword() },
                    type = ButtonType.Tertiary,
                    size = ButtonSize.Small,
                )
            }
        }

        // Sticky bottom action bar
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            CustomTextButton(
                onClick = onNext,
                text = "Change Password",
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading
            )
        }
    }
}

@Composable
private fun NewPasswordContent(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    logoutOtherDevices: Boolean,
    onLogoutOtherDevicesChange: (Boolean) -> Unit,
    isLoading: Boolean,
    onUpdate: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Scrollable content section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(scrollState)
                .padding(12.dp)
        ) {
            InfoCard(
                message = "Must contain at least 1 uppercase, lowercase, number and symbol. Minimum 8 characters required.",
                nature = InfoCardNature.Neutral
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "New Password",
                style = JasnifyTheme.typography.headingMedium,
                color = ContentSecondary,
            )
            Spacer(Modifier.height(8.dp))

            PrimaryInput(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                placeholder = "Enter New Password",
                keyboardType = KeyboardType.Password,
                modifier = Modifier.focusRequester(focusRequester)
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Confirm New Password",
                style = JasnifyTheme.typography.headingMedium,
                color = ContentSecondary,
            )
            Spacer(Modifier.height(8.dp))

            PrimaryInput(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = "Re-enter New Password",
                keyboardType = KeyboardType.Password
            )
            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogoutOtherDevicesChange(!logoutOtherDevices) }
            ) {
                CustomChecker(
                    checked = logoutOtherDevices,
                    onCheckedChange = onLogoutOtherDevicesChange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout from all other devices",
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentPrimary
                )
            }
        }

        // Sticky bottom action bar
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            CustomTextButton(
                onClick = onUpdate,
                text = "Change Password",
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
        }
    }
}

// =========================================== Preview ==============================================

@Preview(showBackground = true)
@Composable
private fun ChangePasswordContentPreview() {
    JasnifyTheme {
        Surface {
            var step by remember { mutableStateOf(ChangePasswordStep.CURRENT_PASSWORD) }
            ChangePasswordContent(
                currentStep = step,
                onStepChange = { step = it },
                onVerifyPassword = { _, _ -> },
                onUpdatePassword = { _ -> },
                onForgotPassword = {},
                onPasswordMismatch = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewPasswordContentPreview() {
    JasnifyTheme {
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var logoutOtherDevices by remember { mutableStateOf(false) }

        Surface {
            NewPasswordContent(
                newPassword = newPassword,
                onNewPasswordChange = { newPassword = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                logoutOtherDevices = logoutOtherDevices,
                onLogoutOtherDevicesChange = { logoutOtherDevices = it },
                isLoading = false,
                onUpdate = {}
            )
        }
    }
}