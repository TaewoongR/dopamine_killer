package com.example.local.user

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.local.app.AppData

@Database(entities = [UserData::class, AppData::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDAO(): UserDAO
}