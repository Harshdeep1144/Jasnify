package com.harshdeep.jasnify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ChecklistEntity::class, 
        ExpenseEntity::class, 
        BudgetEntity::class, 
        CateringItemEntity::class, 
        CateringMetadataEntity::class,
        RoomAccessEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun checklistDao(): ChecklistDao
    abstract fun budgetDao(): BudgetDao
    abstract fun cateringDao(): CateringDao
    abstract fun roomAccessDao(): RoomAccessDao

    companion object {
        const val DATABASE_NAME = "jasnify_db"
    }
}
