package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.InfoCard
import com.harshdeep.jasnify.presentation.components.cards.InfoCardNature
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme

enum class ChangePasswordStep {
    CURRENT_PASSWORD,
    NEW_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    lastChangedText: String = "Last changed 3 months ago",
    onUpdatePassword: (String) -> Unit,
    onForgotPassword: () -> Unit
) {
    var currentStep by remember { mutableStateOf(ChangePasswordStep.CURRENT_PASSWORD) }

    val animatedSheetHeight by animateDpAsState(
        targetValue = if (currentStep == ChangePasswordStep.NEW_PASSWORD) 440.dp else 280.dp,
        animationSpec = tween(durationMillis = 300),
        label = "SheetHeightAnimation"
    )

    CustomBottomSheet(
        heading = "Change Password",
        sheetState = sheetState,
        sheetHeight = animatedSheetHeight,
        onDismiss = onDismiss
    ) {
        ChangePasswordContent(
            currentStep = currentStep,
            onStepChange = { currentStep = it },
            lastChangedText = lastChangedText,
            onUpdatePassword = onUpdatePassword,
            onForgotPassword = onForgotPassword
        )
    }
}

@Composable
fun ChangePasswordContent(
    currentStep: ChangePasswordStep = ChangePasswordStep.CURRENT_PASSWORD,
    onStepChange: (ChangePasswordStep) -> Unit = {},
    lastChangedText: String = "Last changed 3 months ago",
    onUpdatePassword: (String) -> Unit,
    onForgotPassword: () -> Unit
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
                    onNext = {
                        if (currentPassword.isNotEmpty()) {
                            onStepChange(ChangePasswordStep.NEW_PASSWORD)
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
                    onUpdate = {
                        if (newPassword.isNotEmpty() && newPassword == confirmPassword) {
                            onUpdatePassword(newPassword)
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
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
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
                onUpdatePassword = {},
                onForgotPassword = {}
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
                onUpdate = {}
            )
        }
    }
}