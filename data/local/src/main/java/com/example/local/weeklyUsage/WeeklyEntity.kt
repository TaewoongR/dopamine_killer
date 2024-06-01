package com.example.local.weeklyUsage

import androidx.room.Entity

@Entity(
    tableName = "weekly_usage", primaryKeys = ["appName","date"])
data class WeeklyEntity(
    val appName: String ="",
    val date: String = "00000000",  // 주차의 시작 날짜(일요일)
    val weeklyUsage: Int = 0,
)
