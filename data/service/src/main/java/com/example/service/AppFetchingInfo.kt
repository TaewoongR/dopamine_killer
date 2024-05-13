package com.example.service

import androidx.compose.ui.graphics.ImageBitmap

interface AppFetchingInfo {
    suspend fun getAppNameList() : List<String>
    suspend fun findAppByName(name: String): String
    suspend fun getAppIcon(packageName: String): ImageBitmap
    suspend fun getHourlyTime(appName: String, startTime: Long): IntArray
    suspend fun getLastMonthAvgUsage(appName: String): Int
    suspend fun getDailyUsage(appName: String, numberAgo: Int): Triple<Int,String,Int>
    suspend fun getWeeklyAvgUsage(appName: String, numberAgo: Int): Pair<Int,String>
    suspend fun getMonthlyAvgUsage(appName: String, numberAgo: Int): Pair<Int, String>
}