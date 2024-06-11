package com.example.repository

import com.example.local.monthlyUsage.MonthlyEntity

interface MonthlyRepository {
    suspend fun getMonthlyUsageFrom(appName: String, monthAgo: Int): Pair<Int,String>
    suspend fun updateLastMonthlyUsage(appName: String)
    suspend fun initialMonthlyUpdate(appNameList: List<String>)
    suspend fun periodicMonthlyUpdate(appMonthlyList: List<Triple<String, String, Int>>)
    suspend fun deleteUndetected()
    suspend fun deleteMonthly()
    suspend fun saveMonthlyUsage(monthlyEntity: MonthlyEntity)
}