package com.example.service

import androidx.compose.ui.graphics.ImageBitmap

interface AppFetchingInfo {
    suspend fun getAppNameList()
    suspend fun getPackageNameBy(appName: String): String
    suspend fun isAppInstalled(packageName: String): Boolean
    suspend fun getAppIcon(appName: String): ImageBitmap
    suspend fun getHourlyUsage(appName: String, numberAgo: Int,  isInitialSetting: Boolean = true): Triple<List<Int>, String, Int>
    suspend fun getDailyUsage(appName: String, numberAgo: Int): Triple<Int,String,Int>
    suspend fun getWeeklyAvgUsage(appName: String, numberAgo: Int): Pair<Int,String>
    suspend fun getMonthlyAvgUsage(appName: String, numberAgo: Int): Pair<Int, String>
}