package com.example.network.appUsage

import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.weeklyUsage.WeeklyEntity
import com.example.network.appUsage.model.NetworkDailyEntity

interface NetworkDataSource {
    fun postHourlyData(hourlyEntity: HourlyEntity)
    fun postDailyData(dailyEntity: NetworkDailyEntity)
    fun postWeeklyData(weeklyEntity: WeeklyEntity)
    fun postMonthlyData(monthlyEntity: MonthlyEntity)
}