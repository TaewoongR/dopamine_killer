package com.example.local.dailyInfo

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DailyInfoEntity::class], version = 1, exportSchema = false)
abstract class DailyDatabase : RoomDatabase() {
    abstract fun dailyDAO(): DailyDAO
}