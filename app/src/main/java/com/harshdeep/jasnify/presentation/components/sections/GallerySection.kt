package com.harshdeep.jasnify.presentation.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.domain.model.VenueGalleryCategory
import com.harshdeep.jasnify.domain.model.VenueMediaItem
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun VenueGallerySection(
    galleryCategories: List<VenueGalleryCategory>,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val activeCategory = galleryCategories.getOrNull(selectedCategoryIndex)
    val mediaItems = activeCategory?.mediaItems.orEmpty()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gallery",
                style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                color = ContentPrimary
            )
            Row(
                modifier = Modifier.clickable { onSeeAllClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See all",
                    color = ContentBrandDark,
                    style = JasnifyTheme.typography.labelLarge,
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "See All Gallery",
                    tint = ContentBrandDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(galleryCategories.size) { index ->
                val category = galleryCategories[index]
                val isSelected = selectedCategoryIndex == index

                FilterChip(
                    label = category.categoryName,
                    isSelected = isSelected,
                    shapeStyle = ChipShapeStyle.Round,
                    hasStroke = true,
                    onClick = { selectedCategoryIndex = index }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Large focal image container
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .clip(SquircleShape(CornerExtraLarge))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        shape = SquircleShape(CornerExtraLarge)
                    )
                    .background(SurfaceSecondary)
                    .clickable { onSeeAllClick() }
            ) {
                val mediaItem = mediaItems.getOrNull(0)
                AsyncImage(
                    model = mediaItem?.url ?: "image_2e5379.jpg",
                    contentDescription = "Main Gallery",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (mediaItem?.video == true) {
                    VideoPlayOverlay(modifier = Modifier.align(Alignment.Center))
                }
            }

            // Supporting images stack
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(SquircleShape(CornerLarge))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = SquircleShape(CornerLarge)
                        )
                        .background(SurfaceSecondary)
                        .clickable { onSeeAllClick() }
                ) {
                    val mediaItem = mediaItems.getOrNull(1)
                    AsyncImage(
                        model = mediaItem?.url ?: "image_2e5379.jpg",
                        contentDescription = "Gallery Row 2",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (mediaItem?.video == true) {
                        VideoPlayOverlay(modifier = Modifier.align(Alignment.Center), iconSize = 24.dp)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(SquircleShape(CornerLarge))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = SquircleShape(CornerLarge)
                        )
                        .background(SurfaceSecondary)
                        .clickable { onSeeAllClick() }
                ) {
                    val mediaItem = mediaItems.getOrNull(2)
                    AsyncImage(
                        model = mediaItem?.url ?: "image_2e5379.jpg",
                        contentDescription = "Gallery Row 3",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (mediaItem?.video == true) {
                        VideoPlayOverlay(modifier = Modifier.align(Alignment.Center), iconSize = 24.dp)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(SquircleShape(CornerLarge))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = SquircleShape(CornerLarge)
                        )
                        .background(SurfaceSecondary)
                        .clickable { onSeeAllClick() }
                ) {
                    val mediaItem = mediaItems.getOrNull(3)
                    AsyncImage(
                        model = mediaItem?.url ?: "image_2e5379.jpg",
                        contentDescription = "Gallery Row OverView",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (mediaItem?.video == true) {
                        VideoPlayOverlay(modifier = Modifier.align(Alignment.Center), iconSize = 24.dp)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ContentPrimary.copy(alpha = 0.75f))
                    )
                    Surface(
                        color = Color.Transparent,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            text = "+${mediaItems.size}",
                            color = ContentInvPrimary,
                            style = JasnifyTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoPlayOverlay(
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp
) {
    Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.4f),
        modifier = modifier.size(iconSize * 1.5f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Video",
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun VenueGalleryDetailScreen(
    title: String,
    onOpenAlbum: (VenueGalleryCategory) -> Unit,
    galleryCategories: List<VenueGalleryCategory>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Top primary horizontal section uses the first category ("All" or "Images")
    val topSection = galleryCategories.firstOrNull()
    val topMediaItems = topSection?.mediaItems.orEmpty()
    val topTitle = topSection?.categoryName ?: "Images"

    // Remaining categories represent customized albums (e.g., Outdoor Area, Indoor Area, Rooms)
    val albumCategories = if (galleryCategories.size > 1) {
        galleryCategories.drop(1)
    } else {
        emptyList()
    }

    Surface(
        color = BackgroundPrimary,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            CustomTopBar(
                title = title,
                onBackClick = onBack,
                isLeftAligned = true,
                buttonStyle = ButtonBackground.TRANSPARENT,
                textColor = ContentPrimary
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "$topTitle (${topMediaItems.size})",
                                    style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                                    color = ContentPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Last updated 2 weeks ago",
                                    style = JasnifyTheme.typography.labelMedium,
                                    color = ContentSecondary
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { topSection?.let { onOpenAlbum(it) } }
                            ) {
                                Text(
                                    text = "See all",
                                    style = JasnifyTheme.typography.labelLarge,
                                    color = ContentBrandDark
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowRight,
                                    contentDescription = "See All",
                                    tint = ContentBrandDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(topMediaItems) { mediaItem ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 160.dp, height = 160.dp)
                                        .clip(SquircleShape(CornerLargeIncrease))
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                            shape = SquircleShape(CornerLargeIncrease)
                                        )
                                        .background(SurfaceSecondary)
                                ) {
                                    AsyncImage(
                                        model = mediaItem.url,
                                        contentDescription = "Media Item",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (mediaItem.video) {
                                        VideoPlayOverlay(modifier = Modifier.align(Alignment.Center))
                                    }
                                }
                            }
                        }
                    }
                }

                item{
                    DashedDivider(modifier.padding(horizontal = 12.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Albums (${albumCategories.size})",
                            style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                            color = ContentPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Last updated 2 weeks ago",
                            style = JasnifyTheme.typography.labelMedium,
                            color = ContentSecondary
                        )
                    }
                }

                items(albumCategories) { album ->
                    VenueAlbumGridCard(
                        album = album,
                        onAlbumClick = { onOpenAlbum(album) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VenueAlbumGridCard(
    album: VenueGalleryCategory,
    onAlbumClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemsToShow = album.mediaItems.take(6)
    val totalCount = album.mediaItems.size
    val remainingCount = totalCount - 5

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(CornerLargeIncrease))
            .clickable { onAlbumClick() }
            .background(SurfaceSecondary)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(CornerLargeIncrease)
            )
            .padding(12.dp)
    ) {
        // Album inner card header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = album.categoryName,
                    style = JasnifyTheme.typography.headingMedium.copy(fontWeight = FontWeight.Medium),
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last updated 2 weeks ago",
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAlbumClick() }
            ) {
                Text(
                    text = "See all",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentBrandDark
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "See All",
                    tint = ContentBrandDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Multirow 3x2 Album Grid Implementation
        val columnsCount = 3
        val chunkedMedia = itemsToShow.chunked(columnsCount)

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            chunkedMedia.forEachIndexed { rowIndex, rowMedia ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowMedia.forEachIndexed { colIndex, mediaItem ->
                        val itemIndex = rowIndex * columnsCount + colIndex
                        val isLastPlaceholder = itemIndex == 5 && remainingCount > 0

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(SquircleShape(CornerLarge))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                        ) {
                            AsyncImage(
                                model = mediaItem.url,
                                contentDescription = "Album item $itemIndex",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (mediaItem.video) {
                                VideoPlayOverlay(modifier = Modifier.align(Alignment.Center), iconSize = 20.dp)
                            }

                            // Show standard dark translucent overlay showing remaining images count (+X)
                            if (isLastPlaceholder) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(ContentPrimary.copy(alpha = 0.75f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+$remainingCount",
                                        color = ContentInvPrimary,
                                        style = JasnifyTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }

                    // Fill remaining empty columns in case the last chunk isn't full (preserves alignment)
                    if (rowMedia.size < columnsCount) {
                        val emptySlots = columnsCount - rowMedia.size
                        for (i in 0 until emptySlots) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}


// ======================================== Preview ==============================================

@Preview(showBackground = true, name = "Album Grid Card")
@Composable
fun AlbumGridCardPreview() {
    val sampleAlbum = VenueGalleryCategory(
        categoryName = "Outdoor Area",
        mediaItems = listOf(
            VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500"),
            VenueMediaItem("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?w=500"),
            VenueMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500", video = true),
            VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500"),
            VenueMediaItem("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?w=500"),
            VenueMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500"),
            VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500")
        )
    )
    JasnifyTheme {
        VenueAlbumGridCard(album = sampleAlbum, onAlbumClick = {})
    }
}


@Preview(showBackground = true, name = "Gallery Details - Hotel Imperial Inn")
@Composable
fun GalleryDetailScreenPreview() {
    val sampleCategoriesData = listOf(
        VenueGalleryCategory(
            categoryName = "Images",
            mediaItems = listOf(
                VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500")
            )
        ),
        VenueGalleryCategory(
            categoryName = "Outdoor Area",
            mediaItems = listOf(
                VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500") // yields +2
            )
        ),
        VenueGalleryCategory(
            categoryName = "Indoor Area",
            mediaItems = List(20) { VenueMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500") } // yields +15
        ),
        VenueGalleryCategory(
            categoryName = "Rooms",
            mediaItems = List(26) { VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500") } // yields +21
        )
    )

    JasnifyTheme {
        VenueGalleryDetailScreen(
            title = "Hotel Imperial Inn",
            galleryCategories = sampleCategoriesData,
            onBack = {},
            onOpenAlbum = {}
        )
    }
}

@Preview(showBackground = true, name = "Gallery Section - Dashboard Component")
@Composable
fun GallerySectionPreview() {
    val sampleData = listOf(
        VenueGalleryCategory(
            categoryName = "All",
            mediaItems = listOf(
                VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?w=500", video = true),
                VenueMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500")
            )
        ),
        VenueGalleryCategory(
            categoryName = "Interior",
            mediaItems = listOf(
                VenueMediaItem("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500")
            )
        ),
        VenueGalleryCategory(
            categoryName = "Food",
            mediaItems = listOf(
                VenueMediaItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?w=500"),
                VenueMediaItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500")
            )
        )
    )
    JasnifyTheme {
        VenueGallerySection(
            galleryCategories = sampleData,
            onSeeAllClick = {}
        )
    }
}
