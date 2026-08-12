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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.FooterType
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
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
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape

data class MediaItemUiModel(
    val url: String,
    val video: Boolean = false,
    val videoDuration: String? = null
)

data class GalleryCategoryUiModel(
    val categoryName: String,
    val lastUpdated: String? = null,
    val mediaItems: List<MediaItemUiModel> = emptyList()
)

@Composable
fun GallerySection(
    galleryCategories: List<GalleryCategoryUiModel>,
    onSeeAllClick: () -> Unit,
    onMediaClick: (List<MediaItemUiModel>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
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
                    .clickable { onMediaClick(mediaItems, 0) }
            ) {
                val mediaItem = mediaItems.getOrNull(0)
                AsyncImage(
                    model = mediaItem?.url ?: "",
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
                        .clickable { onMediaClick(mediaItems, 1) }
                ) {
                    val mediaItem = mediaItems.getOrNull(1)
                    AsyncImage(
                        model = mediaItem?.url ?: "",
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
                        .clickable { onMediaClick(mediaItems, 2) }
                ) {
                    val mediaItem = mediaItems.getOrNull(2)
                    AsyncImage(
                        model = mediaItem?.url ?: "",
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
                        model = mediaItem?.url ?: "",
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
fun GalleryDetailScreen(
    title: String,
    onOpenAlbum: (GalleryCategoryUiModel) -> Unit,
    onMediaClick: (List<MediaItemUiModel>, Int) -> Unit,
    galleryCategories: List<GalleryCategoryUiModel>,
    onBack: () -> Unit,
    selectedTab: String = "Images",
    onTabSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tabs = remember {
        listOf(
            TabItem("Images", "Images"),
            TabItem("Albums", "Albums")
        )
    }

    val allMediaItems = remember(galleryCategories) {
        galleryCategories.flatMap { it.mediaItems }
    }

    Surface(
        color = BackgroundPrimary,
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (selectedTab == "Images") {
                        ImagesTabView(
                            mediaItems = allMediaItems,
                            lastUpdated = galleryCategories.firstOrNull()?.lastUpdated,
                            onImageClick = { item ->
                                val index = allMediaItems.indexOf(item)
                                onMediaClick(allMediaItems, index)
                            }
                        )
                    } else {
                        AlbumsTabView(
                            categories = galleryCategories,
                            onOpenAlbum = onOpenAlbum,
                            onMediaClick = onMediaClick
                        )
                    }
                }
            }

            // Bottom Navigation Tabs
            BottomTab(
                items = tabs,
                selectedValue = selectedTab,
                onItemSelected = onTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ImagesTabView(
    mediaItems: List<MediaItemUiModel>,
    lastUpdated: String? = null,
    onImageClick: (MediaItemUiModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "Images (${mediaItems.size})",
                    style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                    color = ContentPrimary
                )
                lastUpdated?.let { time ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Last updated $time",
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentSecondary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (mediaItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(0.6f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No images available",
                        style = JasnifyTheme.typography.labelLarge,
                        color = ContentSecondary
                    )
                }
            }
        } else {
            items(mediaItems.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(SquircleShape(CornerLargeIncrease))
                                .background(SurfaceSecondary)
                                .clickable { onImageClick(item) }
                        ) {
                            AsyncImage(
                                model = item.url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (item.video) {
                                VideoPlayOverlay(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Footer correctly aligned at the end of scrollable media list
        item {
            Spacer(modifier = Modifier.height(16.dp))
            FooterJansify(footerType = FooterType.PRIMARY)
        }
    }
}

@Composable
private fun AlbumsTabView(
    categories: List<GalleryCategoryUiModel>,
    onOpenAlbum: (GalleryCategoryUiModel) -> Unit,
    onMediaClick: (List<MediaItemUiModel>, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (categories.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(0.65f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No albums available",
                        style = JasnifyTheme.typography.labelLarge,
                        color = ContentSecondary
                    )
                }
            }
        } else {
            items(categories) { category ->
                AlbumCategorySection(
                    category = category,
                    onSeeAllClick = { onOpenAlbum(category) },
                    onImageClick = { item ->
                        val index = category.mediaItems.indexOf(item)
                        onMediaClick(category.mediaItems, index)
                    }
                )
            }
        }

        // Footer aligned at the end of albums list
        item {
            FooterJansify(footerType = FooterType.PRIMARY)
        }
    }
}

@Composable
fun AlbumDetailScreen(
    category: GalleryCategoryUiModel,
    onBack: () -> Unit,
    onMediaClick: (List<MediaItemUiModel>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BackgroundPrimary,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                TopBarIconButton(
                    onClick = onBack,
                    icon = TopIcon.Predefined.BACK,
                    backgroundStyle = ButtonBackground.OPAQUE
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                        Text(
                            text = category.categoryName,
                            style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                            color = ContentPrimary
                        )
                        category.lastUpdated?.let { lastUpdated ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Last updated $lastUpdated",
                                style = JasnifyTheme.typography.labelMedium,
                                color = ContentSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (category.mediaItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillParentMaxHeight(0.6f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No media available in this album",
                                style = JasnifyTheme.typography.labelLarge,
                                color = ContentSecondary
                            )
                        }
                    }
                } else {
                    items(category.mediaItems.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(SquircleShape(CornerLargeIncrease))
                                        .background(SurfaceSecondary)
                                        .clickable {
                                            val index = category.mediaItems.indexOf(item)
                                            onMediaClick(category.mediaItems, index)
                                        }
                                ) {
                                    AsyncImage(
                                        model = item.url,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (item.video) {
                                        VideoPlayOverlay(modifier = Modifier.align(Alignment.Center))
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Footer properly placed at the bottom of the album's grid items
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    FooterJansify(footerType = FooterType.PRIMARY)
                }
            }
        }
    }
}

@Composable
private fun AlbumCategorySection(
    category: GalleryCategoryUiModel,
    onSeeAllClick: () -> Unit,
    onImageClick: (MediaItemUiModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = category.categoryName,
                    style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                    color = ContentPrimary
                )
                category.lastUpdated?.let { time ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Last updated $time",
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentSecondary
                    )
                }
            }
            Row(
                modifier = Modifier.clickable { onSeeAllClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See all",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentBrandDark
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ContentBrandDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val itemsToShow = category.mediaItems.take(6)
        val totalCount = category.mediaItems.size
        val remainingCount = totalCount - 5

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsToShow.chunked(3).forEachIndexed { rowIndex, rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rowItems.forEachIndexed { colIndex, item ->
                        val itemIndex = rowIndex * 3 + colIndex
                        val isLastItem = itemIndex == 5 && totalCount > 6

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(SquircleShape(CornerLarge))
                                .background(SurfaceSecondary)
                                .clickable { onImageClick(item) }
                        ) {
                            AsyncImage(
                                model = item.url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (item.video) {
                                VideoPlayOverlay(modifier = Modifier.align(Alignment.Center), iconSize = 24.dp)
                            }

                            if (isLastItem) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+$remainingCount",
                                        color = Color.White,
                                        style = JasnifyTheme.typography.headingMedium
                                    )
                                }
                            }
                        }
                    }
                    if (rowItems.size < 3) {
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        DashedDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
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

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MediaViewerScreen(
    mediaItems: List<MediaItemUiModel>,
    initialIndex: Int = 0,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = initialIndex,
        pageCount = { mediaItems.size }
    )
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    SetStatusBarTheme(useDarkIcons = false, statusBarColor = Color.Transparent)

    Surface(
        color = ContentPrimary,
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Media Pager
            androidx.compose.foundation.pager.HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val item = mediaItems[page]
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = item.url,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (item.video) {
                        VideoPlayOverlay(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            // Top Bar
            TopBarIconButton(
                onClick = onBack,
                icon = TopIcon.Predefined.BACK_2,
                iconColor = ContentInvPrimary,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp)
            )

            // Thumbnail Strip at Bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                LazyRow(
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(mediaItems.size) { index ->
                        val item = mediaItems[index]
                        val isSelected = pagerState.currentPage == index

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(SquircleShape(CornerLargeIncrease))
                                .border(
                                    width = if (isSelected) 4.dp else 0.dp,
                                    color = if (isSelected) ContentInvPrimary else Color.Transparent,
                                    shape = SquircleShape(CornerLargeIncrease)
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        ) {
                            AsyncImage(
                                model = item.url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (item.video) {
                                VideoPlayOverlay(
                                    modifier = Modifier.align(Alignment.Center),
                                    iconSize = 16.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}