package com.harshdeep.jasnify.presentation.screens.invitation_cards

import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardTheme

enum class CardsView {
    MAIN,
    EDIT_DETAILS,
    FULL_VIEW,
    LIKED_CARDS,
    ROOM,
    HELP_FEEDBACK
}

enum class CardsTab {
    EXPLORE,
    MY_CARDS
}

val InitialCardThemes = listOf(
    CardTheme(id = "default_1", name = "Classic Elegance", resId = R.drawable.bg_invitation_card_01, isDefault = true),
    CardTheme(id = "default_2", name = "Floral Romance", resId = R.drawable.bg_invitation_card_02, isDefault = true),
    CardTheme(id = "default_3", name = "Golden Glamour", resId = R.drawable.bg_invitation_card_03, isDefault = true),
    CardTheme(id = "default_4", name = "Modern Minimalist", resId = R.drawable.bg_invitation_card_04, isDefault = true)
)
