package com.example.repository

interface WeeklyRepository {
    suspend fun getWeeklyUsageFrom(appName: String, weekAgo: Int): Pair<Int, String>
    suspend fun initialWeeklyUpdate(appNameList: List<String>)
    suspend fun periodicWeeklyUpdate(appWeeklyList: List<Triple<String, String, Int>>)
    suspend fun deleteUndetected()
    suspend fun deleteWeekly()
}