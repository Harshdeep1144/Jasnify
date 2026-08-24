package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.SavedVendor
import com.harshdeep.jasnify.domain.model.Vendor
import kotlinx.coroutines.flow.Flow

interface VendorRepository {
    // Catalog
    fun getAllVendors(): Flow<List<Vendor>>
    fun getVendorById(vendorId: String): Flow<Vendor?>
    fun getVendorsByCategory(category: String): Flow<List<Vendor>>
    fun getVendorReviews(vendorId: String): Flow<List<com.harshdeep.jasnify.domain.model.VendorReview>>
    suspend fun addVendorReview(vendorId: String, review: com.harshdeep.jasnify.domain.model.VendorReview)
    suspend fun deleteVendorReview(vendorId: String, userId: String)
    
    // Saved Vendors
    fun getSavedVendors(eventId: String): Flow<List<SavedVendor>>
    fun getSavedVendorsByCategory(eventId: String, category: String): Flow<List<SavedVendor>>
    suspend fun saveVendor(savedVendor: SavedVendor, syncToCloud: Boolean)
    suspend fun removeSavedVendor(eventId: String, vendorName: String, category: String, syncToCloud: Boolean)

    // Seeding
    suspend fun seedMockVendors(vendors: List<Vendor>)
    suspend fun isCatalogEmpty(): Boolean
}
