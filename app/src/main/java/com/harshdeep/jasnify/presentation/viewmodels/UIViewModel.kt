package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.harshdeep.jasnify.data.local.prefs.PreferenceManager
import com.harshdeep.jasnify.presentation.components.bottomdrawer.NavBarStyleOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UIViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _navBarStyle = MutableStateFlow(preferenceManager.getNavBarStyle())
    val navBarStyle: StateFlow<NavBarStyleOption> = _navBarStyle.asStateFlow()

    fun updateNavBarStyle(style: NavBarStyleOption) {
        _navBarStyle.value = style
        preferenceManager.saveNavBarStyle(style)
    }
}
