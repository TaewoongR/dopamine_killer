package com.example.repository

import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyEntity

interface DailyRepository {
    suspend fun getDailyUsageFrom(appName: String, dayAgo: Int): Triple< Int,String, Int>
    suspend fun updateDailyUsageFrom(appName: String, dayAgo: Int)
    suspend fun initialHourlyDailyUpdate(appNameList: List<String>)
    suspend fun periodicHourlyDailyUpdate()
    suspend fun deleteHourlyDaily()
    suspend fun deleteOnDate(date: String)
    suspend fun periodicAutoHourlyDailyUpdate()
    suspend fun getHourlyUsage(appName: String, date: String): List<Int>
    suspend fun getDailyUsage(appName: String, date: String): Int
    suspend fun saveHourlyUsage(hourlyEntity: HourlyEntity)
    suspend fun saveDailyUsage(dailyEntity: DailyEntity)
}