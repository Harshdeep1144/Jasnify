package com.harshdeep.jasnify.presentation.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.ContentPrimary

@Composable
fun BudgetScreen(
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.Transparent)
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Venue",
                    onBackClick = { onBackClick() },
                    onMenuClick = null,
                    isLargeTitle = true
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
                .background(Color.Transparent),
        ){

        }
    }
}
