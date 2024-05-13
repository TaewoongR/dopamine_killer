package com.example.local.dailyUsage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DailyEntity::class], version = 2, exportSchema = false)
abstract class DailyDatabase : RoomDatabase() {
    abstract fun dailyDAO(): DailyDAO
}