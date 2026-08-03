package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.SavedVendor
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.repository.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class VendorViewModel @Inject constructor(
    private val repository: VendorRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            if (repository.isCatalogEmpty()) {
                seedMockData(com.harshdeep.jasnify.data.mock.MockData.sampleVendors)
            }
            delay(2000.milliseconds) // Small delay for shimmer effect visibility
            _isLoading.value = false
        }
    }

    val allVendors: StateFlow<List<Vendor>> = repository.getAllVendors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _eventId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val savedVendors: StateFlow<List<SavedVendor>> = _eventId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getSavedVendors(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setEventId(id: String) {
        _eventId.value = id
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
