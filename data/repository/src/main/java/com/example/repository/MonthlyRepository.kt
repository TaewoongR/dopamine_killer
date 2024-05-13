package com.example.repository

interface MonthlyRepository {
    suspend fun getMonthlyUsageFrom(appName: String, monthAgo: Int): Pair<Int,String>
    suspend fun updateLastMonthlyUsage(appName: String)
    suspend fun initialMonthlyUpdate()
    suspend fun deleteUndetected()
}