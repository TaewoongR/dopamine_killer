package com.example.repository

interface DailyInfoRepository {
    suspend fun createDailyInfo(appName: String)

    suspend fun getLastWeekAvgUsageByApp(appName: String): Int

    suspend fun getLastMonthAvgUsageByApp(appName: String): Int
}