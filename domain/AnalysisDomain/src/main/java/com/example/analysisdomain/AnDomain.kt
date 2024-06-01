package com.example.analysisdomain

interface AnDomain{

    suspend fun getEntireSelectedApp(): List<String>

    suspend fun getAllSelectedAppUsage(): List<AnalysisDataDomain>

    suspend fun getTodayUsage(appName: String): Int
}