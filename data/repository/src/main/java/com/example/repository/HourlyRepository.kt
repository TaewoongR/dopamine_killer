package com.example.repository

import androidx.compose.ui.graphics.ImageBitmap
import com.example.local.horulyUsage.HourlyEntity

interface HourlyRepository {
    suspend fun updateHourlyTime(appName: String,  packageName: String, dayFrom: Int)

    suspend fun getHourlyDataByNameDate(appName: String, date: String): HourlyEntity // 앱 데이터를 로드하는 메서드 추가

    suspend fun getInstalledAppName(appName: String): String

    suspend fun getAppIcon(appName: String): ImageBitmap

    suspend fun getTodayUsageByApp(appName: String): Int

    suspend fun getYesterdayUsageByApp(appName: String): Int

}