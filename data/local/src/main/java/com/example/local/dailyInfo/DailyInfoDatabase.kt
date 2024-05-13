package com.example.local.dailyInfo

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DailyInfoEntity::class], version = 3, exportSchema = false)
abstract class DailyInfoDatabase : RoomDatabase() {
    abstract fun dailyInfoDAO(): DailyInfoDAO
}