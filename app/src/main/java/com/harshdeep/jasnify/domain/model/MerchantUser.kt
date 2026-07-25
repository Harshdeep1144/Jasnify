package com.harshdeep.jasnify.domain.model

enum class MerchantCategory {
    VENUE, VENDOR, BOTH, NONE
}


data class MerchantUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profilePictureUrl: String? = null,
    val role: String = "MERCHANT",
    val category: MerchantCategory = MerchantCategory.NONE,
    val isProfileComplete: Boolean = false,
    val managedVenueIds: List<String> = emptyList(),
    val managedVendorIds: List<String> = emptyList(),
    val lastActive: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
