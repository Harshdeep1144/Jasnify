package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.domain.model.HomeScreenConfig
import com.harshdeep.jasnify.domain.repository.ConfigRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ConfigRepository {

    override fun getHomeScreenConfig(): Flow<HomeScreenConfig?> = callbackFlow {
        val listener = firestore.collection("app_config")
            .document("home_screen")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val config = snapshot?.toObject(HomeScreenConfig::class.java)
                trySend(config)
            }
        awaitClose { listener.remove() }
    }
}
