package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.InvitationCard
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.InvitationCardItem
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.*

@Composable
fun EditInvitationDetailsScreen(
    initialData: InvitationCard,
    onDataChange: (InvitationCard) -> Unit,
    onBackClick: () -> Unit
) {
    var editingData by remember { mutableStateOf(initialData) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CustomTopBar(
            title = "Design Studio",
            isLargeTitle = true,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Canva-like Interactive Canvas
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                InvitationCardItem(
                    data = editingData,
                    isEditable = true,
                    onUpdate = { editingData = it },
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(280f / 373f)
                )
            }

            // Simple Bottom Action Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfacePrimary,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tap on any text above to edit directly",
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextButton(
                        text = "Save Design",
                        onClick = { 
                            onDataChange(editingData)
                            onBackClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        size = ButtonSize.Large,
                        shapeStyle = ButtonShapeStyle.Square,
                        containerColor = Color(0xFF536E6D),
                        contentColor = Color.White
                    )
                }
            }
        }
    }
}
