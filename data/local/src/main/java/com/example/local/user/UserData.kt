package com.example.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UserData(
    @PrimaryKey // 유저 ID를 주 키로 설정
    val userId: String,
    val password: String
)
