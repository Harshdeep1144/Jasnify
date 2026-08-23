package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.notifications.model.NotificationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _notifications = MutableStateFlow<List<NotificationConfig>>(emptyList())
    val notifications: StateFlow<List<NotificationConfig>> = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            _isLoading.value = True
            try {
                val snapshot = db.collection("notifications").get().await()
                val list = snapshot.toObjects(NotificationConfig::class.java)
                _notifications.value = list
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = False
            }
        }
    }
}
