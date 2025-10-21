package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.viewmodels.AuthState
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel

@Composable
fun LogoutButton(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading

    // --- Logout Navigation Effect ---
    LaunchedEffect(authState) {
        if (authState is AuthState.Success && (authState as AuthState.Success).message.contains("Logged out successfully!")) {

            navController.navigate(Screen.LoginOrSignUp.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true // Clear all screens
                }
            }
            viewModel.resetAuthState()
        }
    }

    val text = if (isLoading) "Loading.." else "Logout"

    CustomTextButton(
        type = ButtonType.Primary,
        size = ButtonSize.Medium,
        shapeStyle = ButtonShapeStyle.Square,
        text = text,
        modifier = modifier,
        onClick = {
            if (!isLoading) {
                // Call the robust logout function
                viewModel.logout(context)
            }
        },
        enabled = !isLoading,
    )

}


@Preview(showBackground = true)
@Composable
fun LogoutButtonPreview() {

    val testNavController = rememberNavController()
    LogoutButton(
        navController = testNavController,
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    )
}