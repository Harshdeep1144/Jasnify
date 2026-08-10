package com.harshdeep.jasnify.domain.model

import com.harshdeep.jasnify.R
import java.util.UUID

data class InvitationCard(
    val id: String = UUID.randomUUID().toString(),
    val backgroundRes: Int = R.drawable.bg_invitation_card_01,
    val primaryHeader: String = "THE WEDDING CELEBRATION OF",
    val names: String = "Taylor & Travis",
    val description: String = "WE REQUEST YOUR PRESENCE AT THE CEREMONY OF THEIR WEDDING",
    val day: String = "SAT",
    val date: String = "14",
    val month: String = "SEPT",
    val year: String = "2026",
    val subHeader: String = "CEREMONY & RECEPTION",
    val timeAndVenue: String = "05:00 PM • TAJ HOTEL, MUMBAI",
    val rsvpDeadline: String = "KINDLY RESPOND BY AUG 1ST, 2026",
    val rsvpContact: String = "RSVP : ANAND K.",
    // Store as Long hexadecimal values
    val nameColorHex: Long = 0xFFA6852FL,
    val contentColorHex: Long = 0xFF444444L,
    val secondaryContentColorHex: Long = 0xFF8E8E8EL
)
