package com.example.local.monthlyUsage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MonthlyEntity::class], version = 1, exportSchema = false)
abstract class MonthlyDatabase : RoomDatabase() {
    abstract fun monthlyDAO(): MonthlyDAO
}