package com.example.local.appUsage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppUsageEntity::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDAO(): AppDAO
}