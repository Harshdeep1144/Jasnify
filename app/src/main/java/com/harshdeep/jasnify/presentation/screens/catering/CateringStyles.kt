package com.harshdeep.jasnify.presentation.screens.catering

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import sv.lib.squircleshape.SquircleShape

@Immutable
data class CategoryStyle(
    val containerBrush: Brush,
    val sheetBrush: Brush,
    val headerTextColor: Color,
    val subtitleTextColor: Color,
    val illustrationRes: Int
)

fun createCategoryGradient(colors: List<Color>): Brush {
    if (colors.isEmpty()) return SolidColor(Color.Transparent)
    if (colors.size == 1) return SolidColor(colors.first())

    val colorStops = when (colors.size) {
        2 -> arrayOf(
            0.0f to colors[0],
            1.0f to colors[1]
        )
        3 -> arrayOf(
            0.0f to colors[0],
            0.68f to colors[1],
            1.0f to colors[2]
        )
        else -> colors.mapIndexed { index, color ->
            val stop = if (index == colors.lastIndex) {
                1.0f
            } else {
                (index.toFloat() / (colors.size - 1)) * 0.68f
            }
            stop to color
        }.toTypedArray()
    }

    return Brush.linearGradient(
        colorStops = colorStops,
        start = Offset.Zero,
        end = Offset.Infinite
    )
}

val AppetizerCategoryStyle = CategoryStyle(
    containerBrush = createCategoryGradient(listOf(Color(0xFFFFAF90), Color(0xFFFED8CA), Color(0xFFFFF2EC))),
    sheetBrush = createCategoryGradient(listOf(Color(0xFFFFC9B8), Color(0xFFFFE8E0), Color(0xFFFFFFFF))),
    headerTextColor = Color(0xFF621E09),
    subtitleTextColor = Color(0xFF621E09),
    illustrationRes = R.drawable.ill_appetizers
)

val BeverageCategoryStyle = CategoryStyle(
    containerBrush = createCategoryGradient(listOf(Color(0xFF91DBFF), Color(0xFFBAEAFF), Color(0xFFEBF7FD))),
    sheetBrush = createCategoryGradient(listOf(Color(0xFFB9E5FA), Color(0xFFE3F5FD), Color(0xFFFFFFFF))),
    headerTextColor = Color(0xFF0A405F),
    subtitleTextColor = Color(0xFF0A405F),
    illustrationRes = R.drawable.ill_beverages
)

val MainCourseCategoryStyle = CategoryStyle(
    containerBrush = createCategoryGradient(listOf(Color(0xFFFF9E99), Color(0xFFFCDAD7), Color(0xFFFFF3F2))),
    sheetBrush = createCategoryGradient(listOf(Color(0xFFFFC2BF), Color(0xFFFFE7E5), Color(0xFFFFFFFF))),
    headerTextColor = Color(0xFF6B1515),
    subtitleTextColor = Color(0xFF6B1515),
    illustrationRes = R.drawable.ill_main_courses
)

val DessertCategoryStyle = CategoryStyle(
    containerBrush = createCategoryGradient(listOf(Color(0xFFFFA9D5), Color(0xFFFBDBEC), Color(0xFFFFF3FA))),
    sheetBrush = createCategoryGradient(listOf(Color(0xFFFFC8E2), Color(0xFFFFE9F4), Color(0xFFFFFFFF))),
    headerTextColor = Color(0xFF631034),
    subtitleTextColor = Color(0xFF631034),
    illustrationRes = R.drawable.ill_desserts
)

@Stable
fun getCategoryStyle(categoryName: String): CategoryStyle {
    val normalized = categoryName.lowercase().trim()
    return when {
        normalized.contains("appetizer") || normalized.contains("starter") -> AppetizerCategoryStyle
        normalized.contains("beverage") || normalized.contains("drink") -> BeverageCategoryStyle
        normalized.contains("main") -> MainCourseCategoryStyle
        normalized.contains("dessert") || normalized.contains("sweet") -> DessertCategoryStyle
        else -> AppetizerCategoryStyle
    }
}

val CardSquircleShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
val CategoryCardShape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault)
