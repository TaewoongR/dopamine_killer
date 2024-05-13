package com.example.local.horulyUsage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HourlyEntity::class], version = 7, exportSchema = false)
abstract class HourlyDatabase : RoomDatabase() {
    abstract fun hourlyDAO(): HourlyDAO
}