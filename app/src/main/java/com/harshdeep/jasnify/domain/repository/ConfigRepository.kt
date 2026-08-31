package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.HomeScreenConfig
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun getHomeScreenConfig(): Flow<HomeScreenConfig?>
}
