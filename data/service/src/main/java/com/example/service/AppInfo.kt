package com.example.service

interface AppInfo {
    suspend fun getAppName() : List<String>
    //suspend fun fetchAppUsage(appName: String)
    suspend fun getHourlyTime(appName: String, startTime: Long, endTime: Long): IntArray
}