package com.example.local.monthlyUsage

import androidx.room.Entity

@Entity(
    tableName = "monthly_usage", primaryKeys = ["appName","date"])
data class MonthlyEntity(
    val appName: String ="",
    val date: String = "000000",
    val monthlyUsage: Int = 0,
)
