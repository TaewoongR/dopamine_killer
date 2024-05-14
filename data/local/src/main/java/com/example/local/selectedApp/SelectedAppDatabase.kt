package com.example.local.selectedApp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SelectedAppEntity::class], version = 3, exportSchema = false)
abstract class SelectedAppDatabase : RoomDatabase() {
    abstract fun selectedAppDAO(): SelectedAppDAO
}