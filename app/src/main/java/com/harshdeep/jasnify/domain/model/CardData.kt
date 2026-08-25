package com.harshdeep.jasnify.domain.model

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.Pattaya
import java.util.UUID

enum class FontStyleType(val label: String, val fontFamily: FontFamily) {
    DEFAULT("Sans Serif", FontFamily.Default),
    SERIF("Classic Serif", FontFamily.Serif),
    PATTAYA("Pattaya", Pattaya),

    // Modern Sans
    ROBOTO("Roboto", getGoogleFontFamily("Roboto")),
    OPEN_SANS("Open Sans", getGoogleFontFamily("Open Sans")),
    LATO("Lato", getGoogleFontFamily("Lato")),
    MONTSERRAT("Montserrat", getGoogleFontFamily("Montserrat")),
    OSWALD("Oswald", getGoogleFontFamily("Oswald")),
    RALWAY("Raleway", getGoogleFontFamily("Raleway")),
    QUICKSAND("Quicksand", getGoogleFontFamily("Quicksand")),
    VARELA_ROUND("Varela Round", getGoogleFontFamily("Varela Round")),
    JOSEFIN_SANS("Josefin Sans", getGoogleFontFamily("Josefin Sans")),

    // Serif
    MERRIWEATHER("Merriweather", getGoogleFontFamily("Merriweather")),
    ROBOTO_SLAB("Roboto Slab", getGoogleFontFamily("Roboto Slab")),
    PLAYFAIR_DISPLAY("Playfair Display", getGoogleFontFamily("Playfair Display")),
    ARVO("Arvo", getGoogleFontFamily("Arvo")),
    CINZEL("Cinzel", getGoogleFontFamily("Cinzel")),

    // Script / Cursive
    LOBSTER("Lobster", getGoogleFontFamily("Lobster")),
    PACIFICO("Pacifico", getGoogleFontFamily("Pacifico")),
    DANCING_SCRIPT("Dancing Script", getGoogleFontFamily("Dancing Script")),
    CAVEAT("Caveat", getGoogleFontFamily("Caveat")),
    GREAT_VIBES("Great Vibes", getGoogleFontFamily("Great Vibes")),
    SACRAMENTO("Sacramento", getGoogleFontFamily("Sacramento")),
    SATISFY("Satisfy", getGoogleFontFamily("Satisfy")),
    COOKIE("Cookie", getGoogleFontFamily("Cookie")),
    COURGETTE("Courgette", getGoogleFontFamily("Courgette")),
    TANGERINE("Tangerine", getGoogleFontFamily("Tangerine")),
    PARISIENNE("Parisienne", getGoogleFontFamily("Parisienne")),
    ALEX_BRUSH("Alex Brush", getGoogleFontFamily("Alex Brush")),
    KAUSHAN_SCRIPT("Kaushan Script", getGoogleFontFamily("Kaushan Script")),
    YELLOWTAIL("Yellowtail", getGoogleFontFamily("Yellowtail")),
    ALLURA("Allura", getGoogleFontFamily("Allura")),
    PINYON_SCRIPT("Pinyon Script", getGoogleFontFamily("Pinyon Script")),
    ARIZONIA("Arizonia", getGoogleFontFamily("Arizonia")),
    CLICKER_SCRIPT("Clicker Script", getGoogleFontFamily("Clicker Script")),

    // Handwritten
    SHADOWS_INTO_LIGHT("Shadows Into Light", getGoogleFontFamily("Shadows Into Light")),
    ARCHITECTS_DAUGHTER("Architects Daughter", getGoogleFontFamily("Architects Daughter")),
    INDIE_FLOWER("Indie Flower", getGoogleFontFamily("Indie Flower")),
    COMING_SOON("Coming Soon", getGoogleFontFamily("Coming Soon")),
    HANDLEE("Handlee", getGoogleFontFamily("Handlee")),
    PATRICK_HAND("Patrick Hand", getGoogleFontFamily("Patrick Hand")),
    KALAM("Kalam", getGoogleFontFamily("Kalam")),
    GLORIA_HALLELUJAH("Gloria Hallelujah", getGoogleFontFamily("Gloria Hallelujah")),
    PERMANENT_MARKER("Permanent Marker", getGoogleFontFamily("Permanent Marker")),

    // Display / Decorative
    ABRIL_FATFACE("Abril Fatface", getGoogleFontFamily("Abril Fatface")),
    ANTON("Anton", getGoogleFontFamily("Anton")),
    BEBAS_NEUE("Bebas Neue", getGoogleFontFamily("Bebas Neue")),
    RIGHTEOUS("Righteous", getGoogleFontFamily("Righteous")),
    BANGERS("Bangers", getGoogleFontFamily("Bangers")),
    SPECIAL_ELITE("Special Elite", getGoogleFontFamily("Special Elite")),
    PRESS_START_2P("Press Start 2P", getGoogleFontFamily("Press Start 2P")),
    MONOTON("Monoton", getGoogleFontFamily("Monoton")),
    BUNGEE("Bungee", getGoogleFontFamily("Bungee")),
    MEGRIM("Megrim", getGoogleFontFamily("Megrim")),
    FASTER_ONE("Faster One", getGoogleFontFamily("Faster One")),
    LONDRINA_SHADOW("Londrina Shadow", getGoogleFontFamily("Londrina Shadow")),
    FINGER_PAINT("Finger Paint", getGoogleFontFamily("Finger Paint")),
    CREEPSTER("Creepster", getGoogleFontFamily("Creepster")),
    METAL_MANIA("Metal Mania", getGoogleFontFamily("Metal Mania")),
    TRADE_WINDS("Trade Winds", getGoogleFontFamily("Trade Winds")),
    RYE("Rye", getGoogleFontFamily("Rye")),
    FRIJOLE("Frijole", getGoogleFontFamily("Frijole")),
    JOLLY_LODGER("Jolly Lodger", getGoogleFontFamily("Jolly Lodger"))
}

enum class CardTextAlign {
    LEFT, CENTER, RIGHT, JUSTIFY;

    fun toComposeTextAlign(): TextAlign = when (this) {
        LEFT -> TextAlign.Left
        CENTER -> TextAlign.Center
        RIGHT -> TextAlign.Right
        JUSTIFY -> TextAlign.Justify
    }

    companion object {
        fun fromComposeTextAlign(textAlign: TextAlign): CardTextAlign = when (textAlign) {
            TextAlign.Left -> LEFT
            TextAlign.Center -> CENTER
            TextAlign.Right -> RIGHT
            TextAlign.Justify -> JUSTIFY
            else -> CENTER
        }
    }
}

enum class CardPaddingMode {
    BOTH, TOP, BOTTOM
}

data class TextElement(
    var id: String = UUID.randomUUID().toString(),
    var text: String = "Sample Text",
    var xRatio: Float = 0.5f,
    var yRatio: Float = 0.5f,
    var widthRatio: Float = 1.0f,
    var fontSizeSp: Float = 16f,
    var colorHex: Long = 0xFF444444L,
    var fontStyle: FontStyleType = FontStyleType.DEFAULT,
    var isBold: Boolean = false,
    var isItalic: Boolean = false,
    var isUnderline: Boolean = false,
    var textAlign: CardTextAlign = CardTextAlign.CENTER,
    var letterSpacingSp: Float = 0f,
    var lineHeightSp: Float = 0f, // 0 means default
    var verticalPaddingSp: Float = 0f,
    var paddingMode: CardPaddingMode = CardPaddingMode.BOTH,
    var zIndex: Int = 0,
    var isEditable: Boolean = true // If false, element cannot be selected, moved, or edited
)

data class CardTheme(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var resId: Int = 0,
    var url: String? = null,
    var isDefault: Boolean = false,
    var adminName: String? = null,
    var adminUsername: String? = null
)

data class CardRoomData(
    var themes: List<CardTheme> = emptyList()
)

data class CardData(
    var id: String = UUID.randomUUID().toString(),
    var bgName: String = "",
    var backgroundRes: Int = R.drawable.bg_invitation_card_01,
    var backgroundUrl: String? = null,
    var backgroundColorHex: Long = 0xFFFFFDF9L,
    var elements: List<TextElement> = defaultElements(),
    var theme: CardTheme? = null,
    var eventType: String = "all events",
    var likesCount: Int = 0,
    var sharesCount: Int = 0,
    var lastEdited: Long = System.currentTimeMillis(),
    var adminName: String? = null,
    var adminUsername: String? = null,
    var likedBy: List<String> = emptyList()
)

fun defaultElements(
    userName: String = "Taylor & Travis",
    eventDate: String = "SEPTEMBER 14TH, 2026",
    venue: String = "TAJ HOTEL, MUMBAI"
): List<TextElement> = listOf(
    TextElement(
        text = "THE WEDDING CELEBRATION OF",
        xRatio = 0.5f,
        yRatio = 0.16f,
        fontSizeSp = 10f,
        colorHex = 0xFF2C2C2CL,
        fontStyle = FontStyleType.SERIF,
        letterSpacingSp = 1.2f,
        verticalPaddingSp = 2f,
        zIndex = 0
    ),
    TextElement(
        text = userName,
        xRatio = 0.5f,
        yRatio = 0.25f,
        fontSizeSp = 38f,
        colorHex = 0xFF9E7118L,
        fontStyle = FontStyleType.PATTAYA,
        isBold = true,
        verticalPaddingSp = 4f,
        zIndex = 1
    ),
    TextElement(
        text = "WE REQUEST YOUR PRESENCE ON",
        xRatio = 0.5f,
        yRatio = 0.33f,
        fontSizeSp = 10f,
        colorHex = 0xFF2C2C2CL,
        fontStyle = FontStyleType.SERIF,
        letterSpacingSp = 1.2f,
        verticalPaddingSp = 2f,
        zIndex = 2
    ),
    TextElement(
        text = eventDate.split(",").firstOrNull()?.split(" ")?.firstOrNull() ?: "SEPTEMBER",
        xRatio = 0.5f,
        yRatio = 0.40f,
        fontSizeSp = 13f,
        colorHex = 0xFF2C2C2CL,
        fontStyle = FontStyleType.SERIF,
        isBold = true,
        letterSpacingSp = 1.5f,
        verticalPaddingSp = 1f,
        zIndex = 3
    ),
    TextElement(
        text = eventDate.split(",").firstOrNull()?.split(" ")?.lastOrNull() ?: "14TH",
        xRatio = 0.5f,
        yRatio = 0.47f,
        fontSizeSp = 32f,
        colorHex = 0xFF111111L,
        fontStyle = FontStyleType.SERIF,
        isBold = true,
        letterSpacingSp = 1.0f,
        verticalPaddingSp = 2f,
        zIndex = 4
    ),
    TextElement(
        text = eventDate.split(",").lastOrNull()?.trim() ?: "2026",
        xRatio = 0.5f,
        yRatio = 0.52f,
        fontSizeSp = 12f,
        colorHex = 0xFF2C2C2CL,
        fontStyle = FontStyleType.SERIF,
        letterSpacingSp = 1.0f,
        verticalPaddingSp = 1f,
        zIndex = 5
    ),
    TextElement(
        text = "SATURDAY",
        xRatio = 0.5f,
        yRatio = 0.56f,
        fontSizeSp = 12f,
        colorHex = 0xFF2C2C2CL,
        fontStyle = FontStyleType.SERIF,
        isBold = true,
        letterSpacingSp = 1.5f,
        verticalPaddingSp = 2f,
        zIndex = 6
    ),
    TextElement(
        text = "CEREMONY & RECEPTION",
        xRatio = 0.5f,
        yRatio = 0.65f,
        fontSizeSp = 13f,
        colorHex = 0xFF111111L,
        fontStyle = FontStyleType.SERIF,
        isBold = true,
        letterSpacingSp = 1.2f,
        verticalPaddingSp = 2f,
        zIndex = 7
    ),
    TextElement(
        text = venue,
        xRatio = 0.5f,
        yRatio = 0.70f,
        fontSizeSp = 11f,
        colorHex = 0xFF333333L,
        fontStyle = FontStyleType.SERIF,
        letterSpacingSp = 0.8f,
        verticalPaddingSp = 2f,
        zIndex = 8
    ),
    TextElement(
        text = "RSVP : ANAND  K.",
        xRatio = 0.5f,
        yRatio = 0.80f,
        fontSizeSp = 10f,
        colorHex = 0xFF2C2C2CL,
        fontStyle = FontStyleType.SERIF,
        isBold = true,
        letterSpacingSp = 1.0f,
        verticalPaddingSp = 2f,
        zIndex = 9
    ),
    // Uneditable Jasnify Branding Watermark anchored independently
    TextElement(
        text = "Jasnify",
        xRatio = 0.5f,
        yRatio = 0.85f,
        fontSizeSp = 18f,
        colorHex = 0x559E9E9EL,
        fontStyle = FontStyleType.PATTAYA,
        verticalPaddingSp = 0f,
        zIndex = 10,
        isEditable = false
    )
)

fun getTemplateElements(
    index: Int,
    userName: String = "Taylor & Travis",
    eventDate: String = "SEPTEMBER 14TH, 2026",
    venue: String = "TAJ HOTEL, MUMBAI"
): List<TextElement> {
    return when (index % 8) {
        0 -> defaultElements(userName, eventDate, venue) // Wedding
        1 -> listOf( // Reception
            TextElement(text = "JOIN US FOR THE RECEPTION OF", yRatio = 0.18f, fontSizeSp = 10f, fontStyle = FontStyleType.SERIF),
            TextElement(text = userName, yRatio = 0.28f, fontSizeSp = 36f, fontStyle = FontStyleType.PATTAYA, colorHex = 0xFF8E44ADL, isBold = true),
            TextElement(text = "A NIGHT OF DINNER & DANCE", yRatio = 0.38f, fontSizeSp = 11f, fontStyle = FontStyleType.SERIF),
            TextElement(text = eventDate, yRatio = 0.48f, fontSizeSp = 14f, fontStyle = FontStyleType.SERIF, isBold = true),
            TextElement(text = "AT $venue", yRatio = 0.58f, fontSizeSp = 12f, fontStyle = FontStyleType.SERIF),
            TextElement(text = "DRESS CODE: BLACK TIE", yRatio = 0.68f, fontSizeSp = 10f, fontStyle = FontStyleType.SERIF),
            TextElement(text = "Jasnify", xRatio = 0.5f, yRatio = 0.85f, fontSizeSp = 18f, colorHex = 0x559E9E9EL, fontStyle = FontStyleType.PATTAYA, isEditable = false)
        )
        2 -> listOf( // Engagement
            TextElement(text = "THE ENGAGEMENT OF", yRatio = 0.20f, fontSizeSp = 12f, fontStyle = FontStyleType.SERIF),
            TextElement(text = userName, yRatio = 0.30f, fontSizeSp = 34f, fontStyle = FontStyleType.PACIFICO, colorHex = 0xFFE67E22L),
            TextElement(text = "WE'RE GETTING HITCHED!", yRatio = 0.40f, fontSizeSp = 14f, fontStyle = FontStyleType.MONTSERRAT, isBold = true),
            TextElement(text = eventDate, yRatio = 0.50f, fontSizeSp = 13f, fontStyle = FontStyleType.SERIF),
            TextElement(text = "AT $venue", yRatio = 0.60f, fontSizeSp = 11f, fontStyle = FontStyleType.SERIF),
            TextElement(text = "Jasnify", xRatio = 0.5f, yRatio = 0.85f, fontSizeSp = 18f, colorHex = 0x559E9E9EL, fontStyle = FontStyleType.PATTAYA, isEditable = false)
        )
        3 -> listOf( // Birthday
            TextElement(text = "IT'S A BIRTHDAY PARTY!", yRatio = 0.22f, fontSizeSp = 14f, fontStyle = FontStyleType.BANGERS, colorHex = 0xFFE74C3CL),
            TextElement(text = userName, yRatio = 0.35f, fontSizeSp = 30f, fontStyle = FontStyleType.QUICKSAND, isBold = true),
            TextElement(text = "JOIN US FOR FUN, GAMES & CAKE", yRatio = 0.45f, fontSizeSp = 12f, fontStyle = FontStyleType.ROBOTO),
            TextElement(text = eventDate, yRatio = 0.55f, fontSizeSp = 14f, fontStyle = FontStyleType.ROBOTO, isBold = true),
            TextElement(text = venue, yRatio = 0.65f, fontSizeSp = 11f, fontStyle = FontStyleType.ROBOTO),
            TextElement(text = "Jasnify", xRatio = 0.5f, yRatio = 0.85f, fontSizeSp = 18f, colorHex = 0x559E9E9EL, fontStyle = FontStyleType.PATTAYA, isEditable = false)
        )
        4 -> listOf( // Mehendi
            TextElement(text = "MEHENDI CELEBRATION", yRatio = 0.18f, fontSizeSp = 13f, fontStyle = FontStyleType.SERIF, colorHex = 0xFF27AE60L, isBold = true),
            TextElement(text = userName, yRatio = 0.30f, fontSizeSp = 40f, fontStyle = FontStyleType.PATTAYA, colorHex = 0xFF27AE60L),
            TextElement(text = "HENNA, MUSIC & CELEBRATION", yRatio = 0.42f, fontSizeSp = 11f, fontStyle = FontStyleType.SERIF),
            TextElement(text = eventDate, yRatio = 0.52f, fontSizeSp = 14f, fontStyle = FontStyleType.SERIF, isBold = true),
            TextElement(text = venue, yRatio = 0.62f, fontSizeSp = 11f, fontStyle = FontStyleType.SERIF),
            TextElement(text = "Jasnify", xRatio = 0.5f, yRatio = 0.85f, fontSizeSp = 18f, colorHex = 0x559E9E9EL, fontStyle = FontStyleType.PATTAYA, isEditable = false)
        )
        5 -> listOf( // Anniversary
            TextElement(text = "TO CELEBRATE 25 YEARS OF LOVE", yRatio = 0.20f, fontSizeSp = 11f, fontStyle = FontStyleType.SERIF),
            TextElement(text = userName, yRatio = 0.32f, fontSizeSp = 32f, fontStyle = FontStyleType.PLAYFAIR_DISPLAY, colorHex = 0xFFD4AF37L, isBold = true),
            TextElement(text = "SILVER JUBILEE ANNIVERSARY", yRatio = 0.45f, fontSizeSp = 13f, fontStyle = FontStyleType.SERIF, isBold = true),
            TextElement(text = eventDate, yRatio = 0.55f, fontSizeSp = 14f, fontStyle = FontStyleType.SERIF),
            TextElement(text = venue, yRatio = 0.65f, fontSizeSp = 11f, fontStyle = FontStyleType.SERIF),
            TextElement(text = "Jasnify", xRatio = 0.5f, yRatio = 0.85f, fontSizeSp = 18f, colorHex = 0x559E9E9EL, fontStyle = FontStyleType.PATTAYA, isEditable = false)
        )
        6 -> listOf( // Haldi
            TextElement(text = "SHOWER THE GROOM WITH YELLOW", yRatio = 0.20f, fontSizeSp = 11f, fontStyle = FontStyleType.SERIF, colorHex = 0xFFF1C40FL),
            TextElement(text = userName, yRatio = 0.32f, fontSizeSp = 35f, fontStyle = FontStyleType.PATTAYA, colorHex = 0xFFF39C12L, isBold = true),
            TextElement(text = "A SPLASH OF COLOR & JOY", yRatio = 0.45f, fontSizeSp = 12f, fontStyle = FontStyleType.SERIF),
            TextElement(text = eventDate, yRatio = 0.55f, fontSizeSp = 14f, fontStyle = FontStyleType.SERIF, isBold = true),
            TextElement(text = "9:00 AM ONWARDS", yRatio = 0.65f, fontSizeSp = 11f, fontStyle = FontStyleType.SERIF),
            TextElement(text = "Jasnify", xRatio = 0.5f, yRatio = 0.85f, fontSizeSp = 18f, colorHex = 0x559E9E9EL, fontStyle = FontStyleType.PATTAYA, isEditable = false)
        )
        else -> listOf( // General Party
            TextElement(text = "YOU'RE INVITED TO A", yRatio = 0.20f, fontSizeSp = 12f, fontStyle = FontStyleType.MONTSERRAT),
            TextElement(text = userName, yRatio = 0.32f, fontSizeSp = 38f, fontStyle = FontStyleType.DANCING_SCRIPT, colorHex = 0xFF2C3E50L, isBold = true),
            TextElement(text = "DRINKS, DINNER & GOOD TIMES", yRatio = 0.45f, fontSizeSp = 12f, fontStyle = FontStyleType.MONTSERRAT),
            TextElement(text = eventDate, yRatio = 0.55f, fontSizeSp = 15f, fontStyle = FontStyleType.MONTSERRAT, isBold = true),
            TextElement(text = venue, yRatio = 0.65f, fontSizeSp = 11f, fontStyle = FontStyleType.MONTSERRAT),
            TextElement(text = "Jasnify", xRatio = 0.5f, yRatio = 0.85f, fontSizeSp = 18f, colorHex = 0x559E9E9EL, fontStyle = FontStyleType.PATTAYA, isEditable = false)
        )
    }
}



fun getJasnifyCardsMock(): List<CardData> = listOf(
    CardData(
        id = "jasnify_01",
        bgName = "Royal Gold",
        backgroundRes = R.drawable.bg_invitation_card_01,
        theme = CardTheme(name = "Royal", resId = R.drawable.bg_invitation_card_01),
        eventType = "Wedding",
        elements = getTemplateElements(0, "Aarav & Ishani", "AUGUST 15, 2026", "THE UMAID BHAWAN, JODHPUR"),
        likesCount = 1240,
        sharesCount = 450
    ),
    CardData(
        id = "jasnify_02",
        bgName = "Floral Bliss",
        backgroundRes = R.drawable.bg_invitation_card_02,
        theme = CardTheme(name = "Floral", resId = R.drawable.bg_invitation_card_02),
        eventType = "Reception",
        elements = getTemplateElements(1, "Kabir & Zoya", "OCTOBER 10, 2026", "JW MARRIOTT, MUMBAI"),
        likesCount = 890,
        sharesCount = 210
    ),
    CardData(
        id = "jasnify_03",
        bgName = "Modern Slate",
        backgroundRes = R.drawable.bg_invitation_card_03,
        theme = CardTheme(name = "Modern", resId = R.drawable.bg_invitation_card_03),
        eventType = "Party",
        elements = getTemplateElements(7, "Rohan's Bash", "NOV 14, 2026", "SKY LOUNGE, AER"),
        likesCount = 2300,
        sharesCount = 670
    )
)
