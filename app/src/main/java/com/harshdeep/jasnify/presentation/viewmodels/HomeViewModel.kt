package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.HomeScreenConfig
import com.harshdeep.jasnify.domain.repository.ConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val configRepository: ConfigRepository
) : ViewModel() {

    val homeScreenConfig: StateFlow<HomeScreenConfig?> = configRepository.getHomeScreenConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
