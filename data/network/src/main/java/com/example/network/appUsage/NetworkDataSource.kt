package com.example.network.appUsage

import android.content.Context
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.weeklyUsage.WeeklyEntity

interface NetworkDataSource {
    fun postHourlyData(hourlyEntity: HourlyEntity, context: Context)
    fun postDailyData(dailyEntity: DailyEntity, context: Context)
    fun postWeeklyData(weeklyEntity: WeeklyEntity, context: Context)
    fun postMonthlyData(monthlyEntity: MonthlyEntity, context: Context)
    suspend fun getBadge(username: String): List<Triple<String, String, String>>
    suspend fun getFlaskApiResponse(token: String): String
}