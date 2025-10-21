package com.harshdeep.jasnify.presentation.util

fun String.toFlagEmoji(): String {
    if (length != 2) return ""

    val flagOffset = 0x1F1E6
    val asciiOffset = 'A'.code

    val firstChar = Character.codePointAt(this, 0) - asciiOffset + flagOffset
    val secondChar = Character.codePointAt(this, 1) - asciiOffset + flagOffset

    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}