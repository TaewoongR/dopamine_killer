package com.example.analysisdomain

interface AnDomain{

    suspend fun getEntireSelectedApp(): List<String>

    suspend fun getAllSelectedAppUsage(): List<AnalysisDataDomain>

    suspend fun getTodayUsage(appName: String): Int

    suspend fun get7daysAvgHourlyUsage(appName: String): List<Int>
    suspend fun get30DailyUsage(appName: String): List<Int>
}