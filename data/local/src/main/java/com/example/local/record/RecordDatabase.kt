package com.example.local.record

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecordEntity::class], version = 3, exportSchema = false)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDAO(): RecordDAO
}