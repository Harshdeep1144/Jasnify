package com.harshdeep.jasnify.presentation.components.bottomdrawer.selection

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.Offer
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.cards.OfferCard
import com.harshdeep.jasnify.presentation.components.cards.OfferCardType
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme

enum class OfferSheetState {
    OFFER_LIST,
    OFFER_DETAILS
}

@Composable
fun OfferBottomSheet(
    offers: List<Offer>,
    initialOffer: Offer? = null,
    onDismiss: () -> Unit,
    onProgress: (Float) -> Unit,
) {
    var currentState by remember(initialOffer) {
        mutableStateOf(if (initialOffer != null) OfferSheetState.OFFER_DETAILS else OfferSheetState.OFFER_LIST)
    }
    var selectedOffer by remember(initialOffer) { mutableStateOf(initialOffer) }
    var searchQuery by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val handleBackPress: () -> Unit = {
        if (currentState == OfferSheetState.OFFER_DETAILS) {
            currentState = OfferSheetState.OFFER_LIST
        } else {
            onDismiss()
        }
    }

    BackHandler(enabled = currentState == OfferSheetState.OFFER_DETAILS, onBack = handleBackPress)

    val filteredOffers = remember(searchQuery, offers) {
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            offers
        } else {
            offers.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
    }

    CustomBottomSheet(
        heading = if (currentState == OfferSheetState.OFFER_LIST) "Offers" else "Offer Details",
        onDismiss = handleBackPress,
        onProgress = onProgress,
        sheetHeight = 500.dp
    ) {
        AnimatedContent(
            targetState = currentState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "OfferSheetTransition"
        ) { state ->
            when (state) {
                OfferSheetState.OFFER_LIST -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (offers.size > 4) {
                            CustomSearchBar(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = "Search Offers",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = filteredOffers,
                                key = { it.id ?: (it.title + it.code) },
                                contentType = { "offer_card" }
                            ) { offer ->
                                OfferCard(
                                    title = offer.title,
                                    description = offer.description,
                                    code = offer.code,
                                    type = OfferCardType.FULL,
                                    onViewDetailsClick = {
                                        selectedOffer = offer
                                        currentState = OfferSheetState.OFFER_DETAILS
                                    },
                                    onCopyCodeClick = {
                                        offer.code?.let { text -> clipboardManager.setText(AnnotatedString(text)) }
                                    }
                                )
                            }
                        }
                    }
                }
                OfferSheetState.OFFER_DETAILS -> {
                    selectedOffer?.let { offer ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            OfferCard(
                                title = offer.title,
                                description = offer.description,
                                code = offer.code,
                                type = OfferCardType.FULL,
                                onCopyCodeClick = {
                                    offer.code?.let { text -> clipboardManager.setText(AnnotatedString(text)) }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Terms of Use",
                                style = JasnifyTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = ContentSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = offer.termsAndConditions,
                                    key = { it },
                                    contentType = { "term_item" }
                                ) { term ->
                                    Text(
                                        text = "• $term",
                                        style = JasnifyTheme.typography.labelMedium,
                                        color = ContentSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}