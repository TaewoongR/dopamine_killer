package com.example.network.appUsage

import android.content.Context
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.record.RecordEntity
import com.example.local.weeklyUsage.WeeklyEntity

interface NetworkDataSource {
    fun postHourlyData(hourlyEntity: HourlyEntity, context: Context)
    fun postDailyData(dailyEntity: DailyEntity, context: Context)
    fun postWeeklyData(weeklyEntity: WeeklyEntity, context: Context)
    fun postMonthlyData(monthlyEntity: MonthlyEntity, context: Context)
    fun postRecordData(recordEntity: RecordEntity, context: Context)
    suspend fun getBadge(username: String): List<Triple<String, String, String>>
    suspend fun getFlaskApiResponse(token: String): String
    fun deleteAppTime(token: String, username: String)
    fun deleteDailyUsage(token: String, username: String)
    fun deleteWeeklyUsage(token: String, username: String)
    fun deleteMonthlyUsage(token: String, username: String)
    fun deleteGoalByUserName(token: String, username: String)
    fun deleteSelectedApp(token: String, username: String)
}
