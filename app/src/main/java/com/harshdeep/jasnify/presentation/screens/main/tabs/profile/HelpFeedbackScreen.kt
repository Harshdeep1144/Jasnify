package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomSuccessBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.AskAiButton
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.FaqAccordion
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.viewmodels.ProfileUpdateState
import com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun HelpFeedbackScreen(
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit,
    onShowAiChat: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isImeVisible = WindowInsets.isImeVisible
    val buttonBringIntoViewRequester = remember { BringIntoViewRequester() }

    val updateState by profileViewModel.updateState.collectAsState()

    var rating by remember { mutableIntStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }
    var showSuccessSheet by remember { mutableStateOf(false) }

    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            delay(100.milliseconds)
            buttonBringIntoViewRequester.bringIntoView()
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is ProfileUpdateState.Success && (updateState as ProfileUpdateState.Success).message.contains("feedback", ignoreCase = true)) {
            showSuccessSheet = true
            rating = 0
            feedbackText = ""
        }
    }

    val faqs = remember {
        listOf(
            "What is Jasnify?" to "Jasnify is your ultimate wedding and event planning assistant, helping you manage venues, vendors, guests, budgets, and checklists all in one place.",
            "How do I invite collaborators?" to "You can invite collaborators to your event rooms (like Venue or Budget) by sharing the unique Event ID or using the 'Manage Room Access' option in the menu.",
            "Can I use Jasnify offline?" to "Yes, most features like viewing your checklist and budget are available offline. Data will sync once you're back online.",
            "How does the AI assistant work?" to "Jasnify AI uses your event context to provide smart suggestions, summarize budgets, and help you find the best choices for your big day."
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundPrimary)) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.statusBarsPadding()) {
                    CustomTopBar(
                        title = "Help & Feedback",
                        onBackClick = onBack,
                        buttonStyle = ButtonBackground.OPAQUE
                    )
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = BackgroundPrimary
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                // FAQ Section
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Frequently Asked Questions",
                        style = JasnifyTheme.typography.headingMedium,
                        color = ContentPrimary
                    )
                    faqs.forEach { (q, a) ->
                        FaqAccordion(question = q, answer = a)
                    }
                }

                DashedDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

                // Support Contact
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                        .background(SurfaceBrandSecondary)
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:support@jasnify.com".toUri()
                                putExtra(Intent.EXTRA_SUBJECT, "Support Request - Jasnify")
                            }
                            context.startActivity(intent)
                        }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mail),
                        contentDescription = null,
                        tint = ContentBrandDark,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Need more help?",
                        style = JasnifyTheme.typography.labelLarge,
                        color = ContentPrimary
                    )
                    Text(
                        text = "Contact us at support@jasnify.com",
                        style = JasnifyTheme.typography.bodyMedium,
                        color = ContentBrandDark
                    )
                }

                DashedDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                // Feedback Form
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Give us your feedback",
                        style = JasnifyTheme.typography.headingMedium,
                        color = ContentPrimary
                    )

                    // Rating Stars
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "How would you rate your experience?",
                            style = JasnifyTheme.typography.bodyLarge,
                            color = ContentSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(5) { index ->
                                val starRating = index + 1
                                Icon(
                                    painter = if (starRating <= rating) painterResource(R.drawable.ic_star_review_filled) else painterResource(R.drawable.ic_star_review),
                                    contentDescription = null,
                                    tint = if (starRating <= rating) Color.Unspecified else ContentTertiary,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { rating = starRating }
                                )
                            }
                        }
                    }

                    // Feedback Text Field using PrimaryInput
                    PrimaryInput(
                        value = feedbackText,
                        onValueChange = { if (it.length <= 1000) feedbackText = it },
                        placeholder = "Tell us what we can improve...",
                        singleLine = false,
                        minLines = 4,
                        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
                        textStyle = JasnifyTheme.typography.bodyLarge.copy(color = ContentPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val isSuccess = updateState is ProfileUpdateState.Success
                    val isLoading = updateState is ProfileUpdateState.Loading

                    CustomTextButton(
                        onClick = {
                            if (rating > 0) {
                                profileViewModel.submitFeedback(rating, feedbackText)
                            }
                        },
                        text = if (isSuccess) "Submitted Successfully" else "Submit Feedback",
                        enabled = rating > 0 && !isLoading && !isSuccess,
                        isLoading = isLoading,
                        shapeStyle = ButtonShapeStyle.Square,
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(buttonBringIntoViewRequester)
                    )
                }

                if (!isImeVisible) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FooterJansify()
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Ask AI Button
        if (!isImeVisible) {
            AskAiButton(
                onClick = onShowAiChat,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp)
                    .zIndex(100f)
            )
        }

        if (showSuccessSheet) {
            CustomSuccessBottomSheet(
                message = "Thanks for your feedback!",
                onDismiss = {
                    showSuccessSheet = false
                    profileViewModel.resetUpdateState()
                }
            )
        }
    }
}