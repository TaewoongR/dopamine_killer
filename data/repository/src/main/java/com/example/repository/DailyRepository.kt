package com.example.repository

interface DailyRepository {
    suspend fun getDailyUsageFrom(appName: String, dayAgo: Int): Triple< Int,String, Int>
    suspend fun updateDailyUsageFrom(appName: String, dayAgo: Int)
    suspend fun initialHourlyDailyUpdate(appNameList: List<String>)
    suspend fun periodicHourlyDailyUpdate()
    suspend fun deleteUndetected()
    suspend fun deleteHourlyDaily()
}