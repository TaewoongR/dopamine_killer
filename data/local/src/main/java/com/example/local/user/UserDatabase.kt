package com.example.local.user

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.local.appUsage.AppUsageEntity

@Database(entities = [UserData::class, AppUsageEntity::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDAO(): UserDAO
}