package com.harshdeep.jasnify.notifications.model

import com.google.firebase.firestore.PropertyName

data class NotificationConfig(
    @get:PropertyName("title") @set:PropertyName("title") var title: String = "",
    @get:PropertyName("body") @set:PropertyName("body") var body: String = "",
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl") var imageUrl: String? = null,
    @get:PropertyName("deepLink") @set:PropertyName("deepLink") var deepLink: String? = null,
    @get:PropertyName("priority") @set:PropertyName("priority") var priority: Int = 1, // 0: Low, 1: Default, 2: High
    @get:PropertyName("channelId") @set:PropertyName("channelId") var channelId: String = "default_channel",
    @get:PropertyName("uiType") @set:PropertyName("uiType") var uiType: String = "standard", // standard, bigText, bigPicture, promo, alert
    @get:PropertyName("backgroundColor") @set:PropertyName("backgroundColor") var backgroundColor: String? = null,
    @get:PropertyName("textColor") @set:PropertyName("textColor") var textColor: String? = null,
    @get:PropertyName("buttonColor") @set:PropertyName("buttonColor") var buttonColor: String? = null,
    @get:PropertyName("headerBackgroundImage") @set:PropertyName("headerBackgroundImage") var headerBackgroundImage: String? = null,
    @get:PropertyName("headerHeight") @set:PropertyName("headerHeight") var headerHeight: Int = 80,
    @get:PropertyName("buttonText") @set:PropertyName("buttonText") var buttonText: String? = null,
    @get:PropertyName("imageHeight") @set:PropertyName("imageHeight") var imageHeight: Int = 180,
    @get:PropertyName("showCloseButton") @set:PropertyName("showCloseButton") var showCloseButton: Boolean = true,
    @get:PropertyName("metadata") @set:PropertyName("metadata") var metadata: Map<String, String>? = null
)
