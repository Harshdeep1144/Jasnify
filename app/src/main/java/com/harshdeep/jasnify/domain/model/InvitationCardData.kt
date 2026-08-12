package com.harshdeep.jasnify.domain.model

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.Pattaya
import java.util.UUID

enum class FontStyleType(val label: String, val fontFamily: FontFamily) {
    DEFAULT("Sans Serif", FontFamily.Default),
    SERIF("Classic Serif", FontFamily.Serif),
    CURSIVE("Script / Cursive", FontFamily.Cursive),
    MONOSPACE("Monospace", FontFamily.Monospace),
    PATTAYA("Pattaya (Header)", Pattaya)
}

data class TextElement(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "Sample Text",
    val xRatio: Float = 0.5f,
    val yRatio: Float = 0.5f,
    val widthRatio: Float = 1.0f,
    val fontSizeSp: Float = 16f,
    val colorHex: Long = 0xFF444444L,
    val fontStyle: FontStyleType = FontStyleType.DEFAULT,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val textAlign: TextAlign = TextAlign.Center,
    val letterSpacingSp: Float = 0f,
    val lineHeightSp: Float = 0f, // 0 means default
    val verticalPaddingSp: Float = 0f,
    val zIndex: Int = 0
)

data class InvitationCardData(
    val id: String = UUID.randomUUID().toString(),
    val backgroundRes: Int = R.drawable.bg_invitation_card_01,
    val backgroundColorHex: Long = 0xFFFFFDF9L,
    val elements: List<TextElement> = defaultElements()
)

fun defaultElements(): List<TextElement> = listOf(
    TextElement(
        text = "THE WEDDING CELEBRATION OF",
        xRatio = 0.5f,
        yRatio = 0.16f,
        fontSizeSp = 10f,
        colorHex = 0xFF444444L,
        fontStyle = FontStyleType.DEFAULT,
        letterSpacingSp = 1.2f,
        zIndex = 0
    ),
    TextElement(
        text = "Taylor & Travis",
        xRatio = 0.5f,
        yRatio = 0.28f,
        fontSizeSp = 36f,
        colorHex = 0xFFA6852FL,
        fontStyle = FontStyleType.PATTAYA,
        zIndex = 1
    ),
    TextElement(
        text = "WE REQUEST YOUR PRESENCE AT THE CEREMONY OF THEIR WEDDING",
        xRatio = 0.5f,
        yRatio = 0.42f,
        fontSizeSp = 9f,
        colorHex = 0xFF8E8E8EL,
        fontStyle = FontStyleType.DEFAULT,
        letterSpacingSp = 0.5f,
        zIndex = 2
    ),
    TextElement(
        text = "SAT • 14 SEPT • 2026",
        xRatio = 0.5f,
        yRatio = 0.56f,
        fontSizeSp = 14f,
        colorHex = 0xFF444444L,
        fontStyle = FontStyleType.SERIF,
        isBold = true,
        letterSpacingSp = 1.0f,
        zIndex = 3
    ),
    TextElement(
        text = "CEREMONY & RECEPTION",
        xRatio = 0.5f,
        yRatio = 0.68f,
        fontSizeSp = 10f,
        colorHex = 0xFF444444L,
        isBold = true,
        zIndex = 4
    ),
    TextElement(
        text = "05:00 PM • TAJ HOTEL, MUMBAI",
        xRatio = 0.5f,
        yRatio = 0.74f,
        fontSizeSp = 9f,
        colorHex = 0xFF8E8E8EL,
        zIndex = 5
    ),
    TextElement(
        text = "RSVP : ANAND K.",
        xRatio = 0.5f,
        yRatio = 0.86f,
        fontSizeSp = 10f,
        colorHex = 0xFF444444L,
        isBold = true,
        zIndex = 6
    )
)
