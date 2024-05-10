package com.example.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_info")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val password: String
)