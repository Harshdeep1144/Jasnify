package com.harshdeep.jasnify.presentation.components.bottomdrawer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

enum class ReviewStep {
    RATING, DETAILS, SUCCESS
}

@Composable
fun ReviewBottomSheet(
    targetId: String,
    targetName: String,
    targetImageUrl: String?,
    targetCategory: String? = null, // null for Venue, category name for Vendor
    initialRating: Int = 0,
    initialReviewText: String = "",
    initialImages: List<Uri> = emptyList(),
    initialLikedOptions: Set<String> = emptySet(),
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onSubmit: (Int, String, List<String>, List<String>, List<String>) -> Unit, // rating, text, images, removedImages, likedOptions
    onDeleteReview: (() -> Unit)? = null,
    onProgress: ((Float) -> Unit)? = null
) {
    var currentStep by remember { mutableStateOf(if (initialRating > 0) ReviewStep.DETAILS else ReviewStep.RATING) }
    var rating by remember { mutableIntStateOf(initialRating) }
    var reviewText by remember { mutableStateOf(initialReviewText) }
    var selectedImages by remember { mutableStateOf(initialImages) }
    var removedImages by remember { mutableStateOf(emptyList<String>()) }
    var selectedLikedOptions by remember(initialLikedOptions) { mutableStateOf(initialLikedOptions) }
    var isSubmitting by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                selectedImages = (selectedImages + uris).take(5)
            }
        }
    )

    val likedOptions = remember(targetCategory) {
        if (targetCategory == null) {
            listOf("Hospitality", "Food", "Ambience", "Banquets")
        } else {
            when (targetCategory.lowercase()) {
                "grooming" -> listOf("Professionalism", "Service", "Hygiene", "Value")
                "makeup" -> listOf("Professionalism", "Quality of Work", "Punctuality", "Value")
                "photography" -> listOf("Professionalism", "Quality of Work", "Punctuality", "Creativity")
                "mehendi" -> listOf("Design", "Quality", "Professionalism", "Punctuality")
                "jewellery" -> listOf("Design", "Quality", "Professionalism", "Variety")
                "outfits" -> listOf("Design", "Quality", "Professionalism", "Fitting")
                "entertainment" -> listOf("Performance", "Professionalism", "Punctuality", "Value")
                "food" -> listOf("Taste", "Quality", "Professionalism", "Presentation")
                "gifts" -> listOf("Quality", "Variety", "Professionalism", "Value")
                else -> listOf("Quality", "Service", "Professionalism", "Value")
            }
        }
    }

    // Dynamic height corresponding to each step layout requirements
    val currentSheetHeight = when (currentStep) {
        ReviewStep.RATING -> 310.dp
        ReviewStep.DETAILS -> 680.dp
        ReviewStep.SUCCESS -> 540.dp
    }

    CustomBottomSheet(
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = currentSheetHeight,
        showDragHandle = true,
        showCloseButton = currentStep != ReviewStep.SUCCESS
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "ReviewStepTransition"
        ) { step ->
            when (step) {
                ReviewStep.RATING -> {
                    RatingStep(
                        targetName = targetName,
                        targetImageUrl = targetImageUrl,
                        onRatingSelected = {
                            rating = it
                            currentStep = ReviewStep.DETAILS
                        }
                    )
                }
                ReviewStep.DETAILS -> {
                    ReviewDetailsStep(
                        rating = rating,
                        onRatingChange = { rating = it },
                        reviewText = reviewText,
                        onReviewTextChange = { reviewText = it },
                        likedOptions = likedOptions,
                        selectedLikedOptions = selectedLikedOptions,
                        onLikedOptionToggle = { option ->
                            selectedLikedOptions = if (selectedLikedOptions.contains(option)) {
                                selectedLikedOptions - option
                            } else {
                                selectedLikedOptions + option
                            }
                        },
                        selectedImages = selectedImages,
                        onAttachPhotosClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onRemoveImage = { uri ->
                            if (uri.toString().contains("cloudinary.com")) {
                                removedImages = removedImages + uri.toString()
                            }
                            selectedImages = selectedImages - uri
                        },
                        isSubmitting = isSubmitting,
                        isEditMode = isEdit,
                        onDeleteClick = {
                            onDeleteReview?.invoke()
                            onDismiss()
                        },
                        onSubmit = {
                            isSubmitting = true
                            onSubmit(rating, reviewText, selectedImages.map { it.toString() }, removedImages, selectedLikedOptions.toList())
                            currentStep = ReviewStep.SUCCESS
                            isSubmitting = false
                        }
                    )
                }
                ReviewStep.SUCCESS -> {
                    ReviewSuccessStep(
                        rating = rating,
                        onDone = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingStep(
    targetName: String,
    targetImageUrl: String?,
    onRatingSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SurfaceSecondary, CircleShape)
        ) {
            AsyncImage(
                model = targetImageUrl ?: R.drawable.img_placeholder_venue_vendor,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = targetName,
            style = JasnifyTheme.typography.labelLarge,
            color = ContentSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "How was your experience?",
            style = JasnifyTheme.typography.headingMedium.copy(fontWeight = FontWeight.Medium),
            color = ContentPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) { index ->
                Icon(
                    painter = painterResource(R.drawable.ic_star_review),
                    contentDescription = null,
                    tint = ContentTertiary,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onRatingSelected(index + 1)
                        }
                )
            }
        }
    }
}

@Composable
private fun ReviewDetailsStep(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    reviewText: String,
    onReviewTextChange: (String) -> Unit,
    likedOptions: List<String>,
    selectedLikedOptions: Set<String>,
    onLikedOptionToggle: (String) -> Unit,
    selectedImages: List<Uri>,
    onAttachPhotosClick: () -> Unit,
    onRemoveImage: (Uri) -> Unit,
    isSubmitting: Boolean,
    isEditMode: Boolean = false,
    onDeleteClick: () -> Unit = {},
    onSubmit: () -> Unit
) {
    val reviewTitle = when (rating) {
        1 -> "Oh no! Tell us more"
        2 -> "Could be better! Tell us more"
        3 -> "Good! Tell us more"
        4 -> "Nice! Tell us more"
        5 -> "Loved it! Tell us more"
        else -> "Tell us more"
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(28.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) { index ->
                        Icon(
                            painter = if (index < rating) painterResource(R.drawable.ic_star_review_filled) else painterResource(R.drawable.ic_star_review),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    onRatingChange(index + 1)
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = `reviewTitle`,
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your rating has been saved",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(SurfaceSecondary, SquircleShape(CornerLargeIncrease))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.08f), SquircleShape(CornerLargeIncrease))
                    .padding(16.dp)
            ) {
                Column {
                    BasicTextField(
                        value = reviewText,
                        onValueChange = { if (it.length <= 500) onReviewTextChange(it) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        textStyle = JasnifyTheme.typography.bodyLarge.copy(color = ContentPrimary),
                        cursorBrush = SolidColor(ContentPrimary),
                        decorationBox = { innerTextField ->
                            if (reviewText.isEmpty()) {
                                Text(
                                    text = "Write here...",
                                    style = JasnifyTheme.typography.bodyLarge,
                                    color = ContentSecondary
                                )
                            }
                            innerTextField()
                        },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${reviewText.length}/500",
                            style = JasnifyTheme.typography.labelSmall,
                            color = ContentTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedImages.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .drawBehind {
                                    val stroke = Stroke(
                                        width = 1.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                    )
                                    drawRoundRect(
                                        color = ContentTertiary,
                                        style = stroke,
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(CornerMedium.toPx())
                                    )
                                }
                                .clip(SquircleShape(CornerMedium))
                                .clickable { onAttachPhotosClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = ContentPrimary)
                        }
                    }
                    items(selectedImages) { uri ->
                        Box(modifier = Modifier.size(72.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(SquircleShape(CornerMedium)),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .size(20.dp),
                                shape = CircleShape,
                                color = Color.Black
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clickable { onRemoveImage(uri) }
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .drawBehind {
                            val stroke = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                            drawRoundRect(
                                color = ContentTertiary,
                                style = stroke,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(CornerLarge.toPx())
                            )
                        }
                        .clip(SquircleShape(CornerLarge))
                        .background(SurfaceSecondary, SquircleShape(CornerLarge))
                        .clickable { onAttachPhotosClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_camera),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = ContentPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Attach Photos",
                            style = JasnifyTheme.typography.labelXLarge,
                            color = ContentPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider(thickness = 1f, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "What did you liked most?",
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Choose one or more options",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                likedOptions.forEachIndexed { index, option ->
                    val shape = when (index) {
                        0 -> SquircleShape(
                            topStart = CornerLargeIncrease,
                            topEnd = CornerLargeIncrease,
                            bottomStart = CornerExtraSmall,
                            bottomEnd = CornerExtraSmall
                        )
                        likedOptions.lastIndex -> SquircleShape(
                            topStart = CornerExtraSmall,
                            topEnd = CornerExtraSmall,
                            bottomStart = CornerLargeIncrease,
                            bottomEnd = CornerLargeIncrease
                        )
                        else -> SquircleShape(CornerExtraSmall)
                    }

                    LikedOptionRow(
                        label = option,
                        isSelected = selectedLikedOptions.contains(option),
                        shape = shape,
                        onToggle = { onLikedOptionToggle(option) }
                    )
                }
            }

            if (isEditMode) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Delete Review",
                    style = JasnifyTheme.typography.labelXLarge.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onDeleteClick() },
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

        CustomTextButton(
            onClick = onSubmit,
            text = if (isSubmitting) "Submitting..." else "Submit",
            enabled = !isSubmitting,
            shapeStyle = ButtonShapeStyle.Square,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp)
        )
    }
}

@Composable
private fun LikedOptionRow(
    label: String,
    isSelected: Boolean,
    shape: SquircleShape = SquircleShape(CornerExtraSmall),
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            ),
        color = SurfaceSecondary,
        shape = shape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = JasnifyTheme.typography.headingMedium,
                color = if (isSelected) ContentPrimary else ContentTertiary
            )

            CustomChecker(
                checked = isSelected,
                onCheckedChange = { onToggle() },
            )
        }
    }
}

@Composable
private fun ReviewSuccessStep(
    rating: Int,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) { index ->
                        Icon(
                            painter = if (index < rating) painterResource(R.drawable.ic_star_review_filled) else painterResource(R.drawable.ic_star_review),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "We got your review!",
                    style = JasnifyTheme.typography.headingXLarge.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Center,
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Thank you for giving your valuable time.",
                    style = JasnifyTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    color = ContentSecondary
                )
            }
        }

        CustomTextButton(
            onClick = onDone,
            text = "Okay",
            type = ButtonType.Secondary,
            shapeStyle = ButtonShapeStyle.Square,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRatingStep() {
    JasnifyTheme {
        RatingStep(
            targetName = "Hotel Imperial Inn",
            targetImageUrl = null,
            onRatingSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewReviewDetailsStep() {
    JasnifyTheme {
        ReviewDetailsStep(
            rating = 4,
            onRatingChange = {},
            reviewText = "Beautiful marriage hall with elegant decor, and attentive staff. The ambience was perfect, making it truly memorable and enjoyable for all our guests.",
            onReviewTextChange = {},
            likedOptions = listOf("Hospitality", "Food", "Ambience", "Banquets"),
            selectedLikedOptions = setOf("Hospitality", "Food"),
            onLikedOptionToggle = {},
            selectedImages = emptyList(),
            onAttachPhotosClick = {},
            onRemoveImage = {},
            isSubmitting = false,
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewReviewSuccessStep() {
    JasnifyTheme {
        ReviewSuccessStep(
            rating = 4,
            onDone = {}
        )
    }
}