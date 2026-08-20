package com.harshdeep.jasnify.presentation.screens.main.tabs.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun LegalScreen(title: String, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = title,
                    onBackClick = onBack,
                    buttonStyle = ButtonBackground.OPAQUE
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            if (title.contains("Privacy", ignoreCase = true)) {
                PrivacyPolicyContent()
            } else {
                TermsAndConditionsContent()
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PrivacyPolicyContent() {
    LegalSection(
        title = "Privacy Policy",
        content = "Last Updated: August 21, 2026\n\n" +
                "We respect your privacy and are committed to protecting your personal data. This Privacy Policy describes how we collect, use, and handle your information when you use our mobile application."
    )

    LegalSection(
        title = "1. Information We Collect",
        content = "We collect information to provide better services to all our users. This includes:\n" +
                "• Account Information: Name, email address, and profile picture provided via Google Sign-In or manual registration.\n" +
                "• User-Generated Content: Data you input for event planning, including guest lists, budgets, checklists, and collaborative chat messages.\n" +
                "• Media: Photos and videos you capture via the Camera or upload to the 'Moments' section.\n" +
                "• Device Information: Device model, operating system version, unique device identifiers (Android ID), and mobile network information."
    )

    LegalSection(
        title = "2. Sensitive Permissions & Data Usage",
        content = "Our app requests the following sensitive permissions to enable specific features:\n" +
                "• Camera: To allow you to capture 'Moments' directly within the app.\n" +
                "• Location: To help you find and select nearby event venues and vendors. We do not track your location in the background.\n" +
                "• Contacts: To simplify inviting guests to your events. We only access contacts you explicitly select and do not store your entire contact list on our servers."
    )

    LegalSection(
        title = "3. Third-Party Services",
        content = "We use trusted third-party services to ensure the app's functionality and security:\n" +
                "• Google Firebase: Used for user authentication, cloud database hosting (Firestore), analytics, and push notifications.\n" +
                "• Cloudinary: Used as a secure hosting provider for images and videos you upload.\n" +
                "These providers process your data in accordance with their respective privacy policies. We do not sell your personal data to third parties."
    )

    LegalSection(
        title = "4. Data Retention & Deletion",
        content = "We retain your personal data as long as your account is active. You have the right to request deletion of your account and all associated data at any time via the 'Account Settings' menu in the app.\n\n" +
                "Alternatively, you may request data deletion via our web portal: https://jasnify.com/delete-account (Placeholder). Upon deletion, all event records and media will be permanently removed from our active servers within 30 days."
    )

    LegalSection(
        title = "5. Children's Privacy",
        content = "Jasnify is not intended for children under the age of 13. We do not knowingly collect personal information from children. If we discover that a child has provided us with personal information, we will delete it immediately."
    )

    LegalSection(
        title = "6. Security",
        content = "We implement industry-standard security measures, including encryption and secure communication protocols, to protect your data from unauthorized access, alteration, or destruction."
    )

    LegalSectionCustom(
        title = "7. Changes & Contact"
    ) {
        val email = "support@jasnify.com"
        val annotatedText = buildAnnotatedString {
            append("We may update this policy from time to time. We will notify you of any changes by posting the new policy in the app. If you have questions, contact us at ")
            withLink(
                LinkAnnotation.Url(
                    url = "mailto:$email",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = ContentPrimary,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium
                        )
                    )
                )
            ) {
                append(email)
            }
            append(".")
        }

        Text(
            text = annotatedText,
            style = JasnifyTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp
            ),
            color = ContentSecondary
        )
    }
}

@Composable
private fun TermsAndConditionsContent() {
    LegalSection(
        title = "Terms and Conditions",
        content = "Last Updated: August 21, 2026\n\n" +
                "By downloading or using the Jasnify application, these terms will automatically apply to you – you should make sure therefore that you read them carefully before using the app."
    )

    LegalSection(
        title = "1. Use of the Service",
        content = "You are responsible for maintaining the confidentiality of your account credentials. You agree to use the app only for lawful purposes and in a way that does not infringe the rights of others or restrict their use of the app."
    )

    LegalSection(
        title = "2. User-Generated Content",
        content = "You retain ownership of the photos, videos, and text you upload to Jasnify. However, by using our collaborative features (such as 'Rooms'), you grant us a worldwide, non-exclusive license to store, process, and display your content to the other participants you have explicitly invited to your event."
    )

    LegalSection(
        title = "3. Prohibited Activities",
        content = "You agree not to:\n" +
                "• Upload any content that is illegal, defamatory, or violates intellectual property rights.\n" +
                "• Attempt to decompile or reverse engineer any part of the app.\n" +
                "• Use the app for spamming or unauthorized commercial solicitation."
    )

    LegalSection(
        title = "4. Third-Party Vendors",
        content = "Jasnify lists venues and vendors as part of its service. We do not guarantee the quality, safety, or legality of the services provided by these third parties. Any transactions or disputes are strictly between you and the respective vendor."
    )

    LegalSection(
        title = "5. Service Availability",
        content = "We aim to provide a reliable service but do not guarantee that the app will always be available or error-free. We reserve the right to modify or discontinue features at any time without notice."
    )

    LegalSection(
        title = "6. Limitation of Liability",
        content = "To the maximum extent permitted by law, Jasnify shall not be liable for any indirect, incidental, or consequential damages resulting from your use or inability to use the service."
    )

    LegalSection(
        title = "7. Governing Law",
        content = "These terms are governed by the laws of India. Any disputes shall be subject to the exclusive jurisdiction of the courts in Patna, Bihar."
    )
}

@Composable
private fun LegalSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = JasnifyTheme.typography.headingMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = ContentPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = JasnifyTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp
            ),
            color = ContentSecondary
        )
    }
}

@Composable
private fun LegalSectionCustom(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = JasnifyTheme.typography.headingMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = ContentPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}