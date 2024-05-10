package com.example.local.dailyInfo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_info")
data class DailyInfoEntity(
    @PrimaryKey
    val appName: String ="",
    val thisInfoUpdateTime: String = "",
    val lastUpdateTime: Long = 0L,
    val latestUpdateTIme: Long = 0L,
    val lastMonthAvgUsage: Int = 0,
    val lastWeekAvgUsage: Int = 0
)