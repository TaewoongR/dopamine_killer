package com.example.service

interface AppInfo {
    suspend fun getAppNameList() : List<String>
    suspend fun getHourlyTime(appName: String, startTime: Long, endTime: Long): IntArray
    suspend fun findAppByName(name: String): String
}