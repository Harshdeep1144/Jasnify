package com.harshdeep.jasnify.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar

@Composable
fun EventDetailsScreen(
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
                    title = "Event Details",
                    onBackClick = {  },
                    isLargeTitle = true
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}