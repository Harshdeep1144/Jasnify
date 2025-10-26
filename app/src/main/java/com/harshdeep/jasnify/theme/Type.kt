package com.harshdeep.jasnify.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R


val Outfit = FontFamily(
    Font(R.font.outfit_light, FontWeight.Light),
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_bold, FontWeight.Bold)
)

val Pattaya = FontFamily(
    Font(
        resId = R.font.pattaya_regular,
        weight = FontWeight.Normal,
    )
)


data class JasnifyTypography(
    // Display
    val displayLarge: TextStyle, // 28px, Medium
    val displayMedium: TextStyle, // 24px, Medium
    val displaySmall: TextStyle, // 20px, Regular
    // Heading
    val headingXLarge: TextStyle, // 24px, Regular
    val headingLarge: TextStyle, // 20px, Regular/Medium
    val headingMedium: TextStyle, // 18px, Regular/Medium
    val headingSmall: TextStyle, // 16px, Regular
    // Body
    val bodyXLarge: TextStyle, // 18px, Regular
    val bodyLarge: TextStyle, // 16px, Regular
    val bodyMedium: TextStyle, // 14px, Regular
    val bodySmall: TextStyle, // 12px, Regular
    // Label
    val labelXLarge: TextStyle, // 18px, Regular
    val labelLarge: TextStyle, // 16px, Regular
    val labelMedium: TextStyle, // 14px, Regular
    val labelSmall: TextStyle, // 12px, Regular
    val labelXSmall: TextStyle, // 8px, Regular
)

val JasnifyCustomTypography = JasnifyTypography(
    displayLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Medium, fontSize = 28.sp),
    displayMedium = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Medium, fontSize = 24.sp),
    displaySmall = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 20.sp),

    headingXLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 24.sp),
    headingLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 20.sp),
    headingMedium = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 18.sp),
    headingSmall = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 16.sp),

    bodyXLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 18.sp),
    bodyLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 12.sp),

    labelXLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 18.sp),
    labelLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    labelMedium = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelSmall = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelXSmall = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Normal, fontSize = 8.sp),
)


val Typography = Typography(
    displayLarge = JasnifyCustomTypography.displayLarge,
    displayMedium = JasnifyCustomTypography.displayMedium,
    displaySmall = JasnifyCustomTypography.displaySmall,

    headlineLarge = JasnifyCustomTypography.headingXLarge,
    headlineMedium = JasnifyCustomTypography.headingLarge,
    headlineSmall = JasnifyCustomTypography.headingMedium,

    titleLarge = JasnifyCustomTypography.headingSmall, // 16px
    titleMedium = JasnifyCustomTypography.bodySmall, // 12px
    titleSmall = JasnifyCustomTypography.labelXSmall, // 8px

    bodyLarge = JasnifyCustomTypography.bodyXLarge, // 18px
    bodyMedium = JasnifyCustomTypography.bodyLarge, // 16px
    bodySmall = JasnifyCustomTypography.bodyMedium, // 14px

    labelLarge = JasnifyCustomTypography.labelLarge, // 16px
    labelMedium = JasnifyCustomTypography.labelMedium, // 14px
    labelSmall = JasnifyCustomTypography.labelSmall // 12px
)

val LocalJasnifyTypography = staticCompositionLocalOf {
    JasnifyCustomTypography
}

object JasnifyTheme {
    val typography: JasnifyTypography
        @Composable
        get() = LocalJasnifyTypography.current
}


