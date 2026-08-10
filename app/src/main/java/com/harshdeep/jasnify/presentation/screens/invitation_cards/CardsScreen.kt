package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.InvitationCard
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.carousels.InvitationCardCarousel
import com.harshdeep.jasnify.presentation.components.cards.InvitationCardItem
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class InvitationCardsView {
    MAIN,
    EDIT_DETAILS
}

@Composable
fun CardsScreen(
    onBackClick: () -> Unit = {},
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    var currentView by remember { mutableStateOf(InvitationCardsView.MAIN) }

    // Unified state for invitation card data using domain model
    var cardData by remember { mutableStateOf(InvitationCard()) }

    // Sync state with active event once loaded
    LaunchedEffect(activeEvent) {
        activeEvent?.let { event ->
            val date = event.date?.let { Date(it) } ?: Date()
            cardData = cardData.copy(
                names = event.name,
                day = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase(),
                date = SimpleDateFormat("dd", Locale.getDefault()).format(date),
                month = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase(),
                year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
            )
        }
    }

    AnimatedContent(
        targetState = currentView,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "InvitationCardsViewContent"
    ) { view ->
        when (view) {
            InvitationCardsView.MAIN -> {
                InvitationCardsMainContent(
                    cardData = cardData,
                    onBackClick = onBackClick,
                    onEditDetailsClick = { currentView = InvitationCardsView.EDIT_DETAILS }
                )
            }
            InvitationCardsView.EDIT_DETAILS -> {
                EditInvitationDetailsScreen(
                    initialData = cardData,
                    onDataChange = { cardData = it },
                    onBackClick = { currentView = InvitationCardsView.MAIN }
                )
            }
        }
    }
}

@Composable
fun InvitationCardsMainContent(
    cardData: InvitationCard,
    onBackClick: () -> Unit,
    onEditDetailsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Cards",
                    isLargeTitle = true,
                    onBackClick = onBackClick,
                    onMenuClick = { },
                    menuIcon = TopIcon.Predefined.MENU_VERTICAL
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InvitationCardCarousel(
                        cardData = cardData,
                        cardWidth = 280.dp,
                        cardHeight = 373.dp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CustomIconButton(
                            icon = painterResource(id = R.drawable.ic_edit),
                            onClick = onEditDetailsClick,
                            type = ButtonType.Secondary
                        )
                        CustomTextButton(
                            text = "Share Card",
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            containerColor = ContentPrimary,
                            contentColor = ContentInvPrimary,
                            trailingIcon = painterResource(id = R.drawable.ic_share)
                        )
                        CustomIconButton(
                            icon = painterResource(id = R.drawable.ic_whatsapp),
                            onClick = { },
                            containerColor = Color(0xFF1BA911),
                            contentColor = ContentInvPrimary
                        )
                    }
                }
            }

            item {
                DashedDivider()
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "Explore Cards",
                        style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                        color = ContentPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Large single random preview
                    InvitationCardItem(
                        data = cardData.copy(backgroundRes = R.drawable.bg_invitation_card_04),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(280f / 373f)
                    )
                }
            }
        }
    }
}