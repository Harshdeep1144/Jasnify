package com.harshdeep.jasnify.di

import com.harshdeep.jasnify.data.repository.ChecklistRepositoryImpl
import com.harshdeep.jasnify.data.repository.EnquiryRepositoryImpl
import com.harshdeep.jasnify.domain.repository.ChecklistRepository
import com.harshdeep.jasnify.domain.repository.EnquiryRepository
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

    @Binds
    @Singleton
    abstract fun bindCateringRepository(
        cateringRepositoryImpl: com.harshdeep.jasnify.data.repository.CateringRepositoryImpl
    ): com.harshdeep.jasnify.domain.repository.CateringRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: com.harshdeep.jasnify.data.repository.UserRepositoryImpl
    ): com.harshdeep.jasnify.domain.repository.UserRepository

    @Binds
    @Singleton
    abstract fun bindVenueRepository(
        venueRepositoryImpl: com.harshdeep.jasnify.data.repository.VenueRepositoryImpl
    ): com.harshdeep.jasnify.domain.repository.VenueRepository

    @Binds
    @Singleton
    abstract fun bindEnquiryRepository(
        enquiryRepositoryImpl: EnquiryRepositoryImpl
    ): EnquiryRepository
}
