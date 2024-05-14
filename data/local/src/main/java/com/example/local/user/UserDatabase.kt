package com.example.local.user

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserData::class, UserEntity::class], version = 2)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDAO(): UserDAO
}