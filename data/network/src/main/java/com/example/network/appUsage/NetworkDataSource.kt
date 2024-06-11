package com.example.network.appUsage

import android.content.Context
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.record.RecordEntity
import com.example.local.selectedApp.SelectedAppEntity
import com.example.local.weeklyUsage.WeeklyEntity

interface NetworkDataSource {
    fun postHourlyData(hourlyEntity: HourlyEntity, context: Context)
    fun postDailyData(dailyEntity: DailyEntity, context: Context)
    fun postWeeklyData(weeklyEntity: WeeklyEntity, context: Context)
    fun postMonthlyData(monthlyEntity: MonthlyEntity, context: Context)
    fun postRecordData(recordEntity: RecordEntity, context: Context)
    fun postSelectedData(selectedAppEntity: SelectedAppEntity, context: Context)
    suspend fun getBadge(username: String): List<Triple<String, String, String>>
    suspend fun getFlaskApiResponse(token: String): String
    fun deleteAppTime(token: String, username: String)
    fun deleteDailyUsage(token: String, username: String)
    fun deleteWeeklyUsage(token: String, username: String)
    fun deleteMonthlyUsage(token: String, username: String)
    fun deleteGoalByUserName(token: String, username: String)
    fun deleteSelectedApp(token: String, username: String)
    /*
    suspend fun getHourly(username: String, date: String): List<Int>
    suspend fun getDaily(username: String,date: String): Int
    suspend fun getWeekly(username: String,date: String): Int
    suspend fun getMonthly(username: String,date: String): Int
     */
    suspend fun getHourly(token: String,username: String): List<HourlyEntity>
    suspend fun getDaily(token: String,username: String): List<DailyEntity>
    suspend fun getWeekly(token: String,username: String): List<WeeklyEntity>
    suspend fun getMonthly(token: String,username: String): List<MonthlyEntity>
    suspend fun getRecord(token: String, username: String): List<RecordEntity>
    suspend fun getSelected(token: String, username: String): List<SelectedAppEntity>
}
