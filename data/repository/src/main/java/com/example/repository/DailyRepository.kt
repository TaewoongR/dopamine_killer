package com.example.repository

interface DailyRepository {
    suspend fun getLastWeekAvgUsageByApp(appName: String, todayDate: String): Int

    suspend fun getLastMonthAvgUsageByApp(appName: String, todayDate: String): Int
}