package com.harshdeep.jasnify.data.models

data class PhoneAuthUser(
    val userId: String = "",
    val phoneNumber: String = "",
    val name: String = "",
    val status: String = "",
    val profileImage: String? = null
) {
    // Default constructor required for calls to DataSnapshot.getValue(PhoneAuthUser::class.java)
    constructor() : this("", "", "", "", null)
}
