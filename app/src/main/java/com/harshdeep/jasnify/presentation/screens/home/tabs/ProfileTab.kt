package com.harshdeep.jasnify.presentation.screens.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.theme.*

@Composable
fun ProfileTab(
    mainNavController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile Screen",
            style = JasnifyTheme.typography.displayLarge,
            color = ContentPrimary
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = {
            authViewModel.logout(context)
            mainNavController.navigate(Screen.LoginOrSignUp.route) {
                popUpTo(Screen.MainAppGraph.route) { inclusive = true }
            }
        }) {
            Text("Logout")
        }
    }
}