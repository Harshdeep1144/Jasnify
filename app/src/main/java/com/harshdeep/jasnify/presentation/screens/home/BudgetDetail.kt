package com.harshdeep.jasnify.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.harshdeep.jasnify.theme.ContentPrimary

@Composable
fun BudgetDetail() {
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Text(
            text = "Budget Details",
            style = MaterialTheme.typography.displayMedium,
            color = ContentPrimary
        )
    }
}