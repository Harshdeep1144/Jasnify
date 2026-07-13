package com.harshdeep.jasnify.di

import com.harshdeep.jasnify.data.repository.ChecklistRepositoryImpl
import com.harshdeep.jasnify.domain.repository.ChecklistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChecklistRepository(
        checklistRepositoryImpl: ChecklistRepositoryImpl
    ): ChecklistRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        budgetRepositoryImpl: com.harshdeep.jasnify.data.repository.BudgetRepositoryImpl
    ): com.harshdeep.jasnify.domain.repository.BudgetRepository
}
