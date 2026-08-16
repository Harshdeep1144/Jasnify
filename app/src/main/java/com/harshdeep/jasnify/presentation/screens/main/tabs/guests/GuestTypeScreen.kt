package com.harshdeep.jasnify.presentation.screens.main.tabs.guests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.GuestType
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.GuestTypeCard
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.SurfacePrimary

@Composable
fun GuestTypeScreen(
    isViewer: Boolean = false,
    guestTypes: List<GuestType>,
    onBackClick: () -> Unit,
    onAddTypeClick: () -> Unit,
    onTypeClick: (GuestType) -> Unit,
    getGuestThumbnails: (String) -> List<String>
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(BackgroundPrimary)
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Guest Type",
                    onBackClick = onBackClick
                )
            }
        },
        containerColor = BackgroundSecondary,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(guestTypes, key = { it.name }) { type ->
                    GuestTypeCard(
                        label = type.name,
                        guestCount = type.guestCount,
                        showChecker = false,
                        imageUrls = getGuestThumbnails(type.name),
                        onClick = { onTypeClick(type) }
                    )
                }
            }

            if (!isViewer) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = BottomGradientBrush
                        )
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp)
                            .pill360Shadow(
                                ambientColor = Color.Black.copy(alpha = 0.10f),
                                ambientBlur = 12.dp,
                                ambientSpread = 2.dp,
                                spotColor = Color.Black.copy(alpha = 0.15f),
                                spotBlur = 18.dp,
                                spotOffsetY = 4.dp
                            ),
                        color = SurfacePrimary,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomTextButton(
                                onClick = onAddTypeClick,
                                text = "Add a Guest Type",
                                leadingIcon = painterResource(id = R.drawable.ic_plus),
                                modifier = Modifier.fillMaxWidth(),
                                shapeStyle = ButtonShapeStyle.Round
                            )
                        }
                    }
                }
            }
        }
    }
}