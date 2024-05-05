package com.example.repository

import androidx.compose.ui.graphics.ImageBitmap
import com.example.local.appUsage.AppUsageEntity

interface LocalRepository {
    suspend fun updateHourlyTime(appName: String, startTime: Long, date: String, dayOfWeek: Int)

    suspend fun getHourlyDataByNameDate(appName: String, date: String): AppUsageEntity // 앱 데이터를 로드하는 메서드 추가

    suspend fun getInstalledNameList(): List<String>

    suspend fun findInstalledApp(appName: String): String

    suspend fun getAppIcon(appName: String): ImageBitmap
}