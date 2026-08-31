package com.harshdeep.jasnify.presentation.screens.main.tabs.home

import androidx.compose.runtime.Immutable

@Immutable
sealed class HeaderMedia {
    abstract val actionType: String
    abstract val targetRoute: String
    abstract val contentColor: String?
    abstract val autoSlideDuration: Long?

    @Immutable
    data class ImageResource(
        val resId: Int,
        override val actionType: String = "",
        override val targetRoute: String = "",
        override val contentColor: String? = null,
        override val autoSlideDuration: Long? = null
    ) : HeaderMedia()

    @Immutable
    data class ImageUrl(
        val url: String,
        override val actionType: String = "",
        override val targetRoute: String = "",
        override val contentColor: String? = null,
        override val autoSlideDuration: Long? = null
    ) : HeaderMedia()

    @Immutable
    data class GifUrl(
        val url: String,
        override val actionType: String = "",
        override val targetRoute: String = "",
        override val contentColor: String? = null,
        override val autoSlideDuration: Long? = null
    ) : HeaderMedia()

    @Immutable
    data class VideoResource(
        val resId: Int,
        override val actionType: String = "",
        override val targetRoute: String = "",
        override val contentColor: String? = null,
        override val autoSlideDuration: Long? = null
    ) : HeaderMedia()

    @Immutable
    data class VideoUrl(
        val url: String,
        override val actionType: String = "",
        override val targetRoute: String = "",
        override val contentColor: String? = null,
        override val autoSlideDuration: Long? = null
    ) : HeaderMedia()

    @Immutable
    data class LottieUrl(
        val url: String,
        override val actionType: String = "",
        override val targetRoute: String = "",
        override val contentColor: String? = null,
        override val autoSlideDuration: Long? = null
    ) : HeaderMedia()

    @Immutable
    data class LottieResource(
        val resId: Int,
        override val actionType: String = "",
        override val targetRoute: String = "",
        override val contentColor: String? = null,
        override val autoSlideDuration: Long? = null
    ) : HeaderMedia()
}
