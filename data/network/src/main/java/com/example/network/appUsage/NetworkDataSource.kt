package com.example.network.appUsage

import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.weeklyUsage.WeeklyEntity

interface NetworkDataSource {
    fun postHourlyData(hourlyEntity: HourlyEntity)
    fun postDailyData(dailyEntity: DailyEntity)
    fun postWeeklyData(weeklyEntity: WeeklyEntity)
    fun postMonthlyData(monthlyEntity: MonthlyEntity)
    suspend fun getBadge(username: String): List<Triple<String, String, String>>
}