package com.harshdeep.jasnify.domain.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String? = null
)
