package com.harshdeep.jasnify.di

import android.content.Context
import androidx.room.Room
import com.harshdeep.jasnify.data.local.AppDatabase
import com.harshdeep.jasnify.data.local.ChecklistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideChecklistDao(database: AppDatabase): ChecklistDao {
        return database.checklistDao()
    }

    @Provides
    @Singleton
    fun provideBudgetDao(database: AppDatabase): com.harshdeep.jasnify.data.local.BudgetDao {
        return database.budgetDao()
    }

    @Provides
    @Singleton
    fun provideCateringDao(database: AppDatabase): com.harshdeep.jasnify.data.local.CateringDao {
        return database.cateringDao()
    }
}
