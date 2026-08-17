package com.harshdeep.jasnify.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.data.remote.CloudinaryManager
import com.harshdeep.jasnify.domain.model.SavedVendor
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.VendorReview
import com.harshdeep.jasnify.domain.repository.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class VendorViewModel @Inject constructor(
    private val repository: VendorRepository,
    private val cloudinaryManager: CloudinaryManager
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isReviewSubmitting = MutableStateFlow(false)
    val isReviewSubmitting: StateFlow<Boolean> = _isReviewSubmitting.asStateFlow()

    fun submitReview(
        vendorId: String,
        rating: Double,
        text: String,
        imageUris: List<Uri>,
        removedImageUrls: List<String>,
        likedOptions: List<String>
    ) {
        viewModelScope.launch {
            _isReviewSubmitting.value = true
            try {
                // 0. Delete removed images from Cloudinary
                removedImageUrls.forEach { url ->
                    cloudinaryManager.deleteImageByUrl(url)
                }

                // 1. Upload images to Cloudinary (only those that are not already uploaded)
                val uploadedUrls = imageUris.map { uri ->
                    async {
                        if (uri.toString().contains("cloudinary.com")) {
                            uri.toString()
                        } else {
                            cloudinaryManager.uploadVendorReviewImage(uri, vendorId)
                        }
                    }
                }.awaitAll()

                // 2. Create and Submit review
                val user = auth.currentUser
                val review = VendorReview(
                    userId = user?.uid ?: "",
                    userName = user?.displayName ?: "Anonymous",
                    userAvatarUrl = user?.photoUrl?.toString(),
                    rating = rating,
                    reviewText = text,
                    attachedImages = uploadedUrls,
                    likedOptions = likedOptions,
                    createdAt = System.currentTimeMillis(),
                    relativeTime = "Just now"
                )
                repository.addVendorReview(vendorId, review)
                
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isReviewSubmitting.value = false
            }
        }
    }

    fun deleteReview(vendorId: String, imageUrls: List<String>) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                imageUrls.forEach { url ->
                    cloudinaryManager.deleteImageByUrl(url)
                }
                repository.deleteVendorReview(vendorId, userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    init {
        viewModelScope.launch {
            if (repository.isCatalogEmpty()) {
                seedMockData(com.harshdeep.jasnify.data.mock.MockData.sampleVendors)
            }
            _isLoading.value = false
        }
    }

    val allVendors: StateFlow<List<Vendor>> = repository.getAllVendors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedVendorId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val vendorReviews: StateFlow<List<VendorReview>> = _selectedVendorId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getVendorReviews(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedVendorId(id: String?) {
        if (_selectedVendorId.value != id) {
            _selectedVendorId.value = id
        }
    }

    private val _eventId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val savedVendors: StateFlow<List<SavedVendor>> = _eventId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getSavedVendors(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setEventId(id: String) {
        if (_eventId.value != id) {
            _eventId.value = id
        }
    }

    fun toggleSaveVendor(vendor: Vendor, isViewer: Boolean, destination: String? = null) {
        val eventId = _eventId.value ?: return
        val currentSaved = savedVendors.value.find { it.vendorName == vendor.name && it.category == vendor.category }
        val syncToCloud = !isViewer
        
        viewModelScope.launch {
            if (destination == null && currentSaved != null) {
                repository.removeSavedVendor(eventId, vendor.name, vendor.category, syncToCloud)
            } else {
                val newSave = SavedVendor(
                    vendorId = vendor.id,
                    vendorName = vendor.name,
                    eventId = eventId,
                    category = vendor.category,
                    destination = destination ?: "mysaved"
                )
                repository.saveVendor(newSave, syncToCloud)
            }
        }
    }

    fun getVendorsByCategory(category: String): Flow<List<Vendor>> {
        return repository.getVendorsByCategory(category)
    }

    fun getSavedVendorsByCategory(category: String): Flow<List<SavedVendor>> {
        val eventId = _eventId.value ?: return flowOf(emptyList())
        return repository.getSavedVendorsByCategory(eventId, category)
    }

    fun seedMockData(vendors: List<Vendor>) {
        viewModelScope.launch {
            repository.seedMockVendors(vendors)
        }
    }
}
