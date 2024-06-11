package com.example.repository

import android.content.Context
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.record.RecordEntity
import com.example.local.selectedApp.SelectedAppEntity
import com.example.local.weeklyUsage.WeeklyEntity

interface NetworkRepository {
    suspend fun postHourly(context: Context)
    suspend fun postDaily(context: Context)
    suspend fun postWeekly(context: Context)
    suspend fun postMonthly(context: Context)
    suspend fun postGoal(context: Context)
    suspend fun postSelected(context: Context)
    suspend fun getHourly(token: String,username: String): List<HourlyEntity>
    suspend fun getDaily(token: String,username: String): List<DailyEntity>
    suspend fun getWeekly(token: String,username: String): List<WeeklyEntity>
    suspend fun getMonthly(token: String,username: String): List<MonthlyEntity>
    suspend fun getRecord(token: String,username: String): List<RecordEntity>
    suspend fun getSelected(token: String,username: String): List<SelectedAppEntity>
}