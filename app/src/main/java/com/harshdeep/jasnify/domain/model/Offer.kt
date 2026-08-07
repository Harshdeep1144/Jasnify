package com.harshdeep.jasnify.domain.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class Offer(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val code: String? = null,
    val termsAndConditions: List<String> = emptyList()
)
