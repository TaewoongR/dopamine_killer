package com.example.coredomain

import androidx.compose.ui.graphics.ImageBitmap

interface CoreDomain {
    suspend fun initialUpdate(appNameList: List<String>)     // 최초 앱 실행시 실행
    suspend fun initialHourlyUpdate(appNameList: List<String>)
    suspend fun updateSelectedApp(appNameList: List<String>, isSelected: Boolean)      // 목표 앱 선택 항목 업데이트
    suspend fun updateInstalledApp(appNameList: List<String>)

    suspend fun deleteUndetectedUsageObj()

    suspend fun getAllSelectedAppName(): List<String>
    suspend fun getAllSelectedAppUsage(): List<FourUsageDomainData>

    suspend fun getAppIconForAppSetting(appName: String): Pair<ImageBitmap?, String>
    suspend fun getAppIcon(appName: String): ImageBitmap
}