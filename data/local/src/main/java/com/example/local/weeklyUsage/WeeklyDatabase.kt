package com.example.local.weeklyUsage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeeklyEntity::class], version = 2, exportSchema = false)
abstract class WeeklyDatabase : RoomDatabase() {
    abstract fun weeklyDAO(): WeeklyDAO
}