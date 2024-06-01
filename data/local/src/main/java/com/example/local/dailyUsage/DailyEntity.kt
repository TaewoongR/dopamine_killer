package com.example.local.dailyUsage

import androidx.room.Entity

@Entity(
    tableName = "daily_usage", primaryKeys = ["appName","date"])
data class DailyEntity(
    val appName: String ="",
    val date: String = "00000000",
    val dayOfWeek: Int = 0, // Calendar 라이브러리 일 = 1, 토 = 7
    val dailyUsage: Int = 0,
)
