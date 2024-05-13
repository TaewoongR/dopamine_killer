package com.example.repository

interface WeeklyRepository {
    suspend fun getWeeklyUsageFrom(appName: String, monthAgo: Int): Pair<Int, String>
    suspend fun updateLastWeeklyUsage(appName: String)
    suspend fun initialWeeklyUpdate()
    suspend fun deleteUndetected()

}