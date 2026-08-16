package com.harshdeep.jasnify.domain.model

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as GoogleFontRes
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.harshdeep.jasnify.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

fun getGoogleFontFamily(fontName: String): FontFamily {
    val googleFont = GoogleFont(fontName)

    return FontFamily(
        listOf(
            GoogleFontRes(
                googleFont = googleFont,
                fontProvider = provider,
                weight = FontWeight.Normal
            ),
            GoogleFontRes(
                googleFont = googleFont,
                fontProvider = provider,
                weight = FontWeight.Bold
            ),
            // High-quality bundled fallbacks to prevent "flash of unstyled text"
            Font(R.font.outfit_regular, FontWeight.Normal),
            Font(R.font.outfit_bold, FontWeight.Bold)
        )
    )
}

val googleFontNames = listOf(
    "Roboto", "Open Sans", "Lato", "Montserrat", "Oswald", "Roboto Condensed", "Source Sans Pro",
    "Raleway", "PT Sans", "Merriweather", "Roboto Slab", "Noto Sans", "Playfair Display",
    "Muli", "Itim", "Lobster", "Pacifico", "Dancing Script", "Caveat", "Great Vibes",
    "Playball", "Sacramento", "Satisfy", "Cookie", "Courgette", "Tangerine", "Parisienne",
    "Alex Brush", "Kaushan Script", "Yellowtail", "Grand Hotel", "Rochester", "Allura",
    "Pinyon Script", "Arizonia", "Clicker Script", "Stalemate", "Homemade Apple",
    "Cedarville Cursive", "Reenie Beanie", "Rock Salt", "Shadows Into Light", "Architects Daughter",
    "Indie Flower", "Coming Soon", "Handlee", "Patrick Hand", "Kalam", "Nanum Pen Script",
    "Gloria Hallelujah", "Permanent Marker", "Special Elite", "Fredericka the Great",
    "Press Start 2P", "Bungee", "Monoton", "Megrim", "Faster One", "Londrina Shadow",
    "Cabin Sketch", "Finger Paint", "Ribeye Marrow", "Nosifer", "Creepster", "Butcherman",
    "Metal Mania", "Piedra", "Trade Winds", "Shojumaru", "Rye", "Sancreek", "Henny Penny",
    "Jolly Lodger", "Frijole", "Abril Fatface", "Arvo", "Josefin Sans", "Quicksand",
    "Anton", "Varela Round", "Bebas Neue", "Cinzel", "Righteous", "Amatic SC", "Bangers", "Afacad Flux"
)
