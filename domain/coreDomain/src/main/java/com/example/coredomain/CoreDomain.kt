package com.example.coredomain

import androidx.compose.ui.graphics.ImageBitmap

interface CoreDomain {
    suspend fun initialUpdate(appNameList: List<String>)     // 최초 앱 실행시 실행
    suspend fun initialHourlyUpdate(appNameList: List<String>)
    suspend fun updateInitialSelectedApp(appNameList: List<String>, isSelected: Boolean)      // 목표 앱 선택 항목 업데이트
    suspend fun updateInitialInstalledApp(appNameList: List<String>)
    suspend fun updateInstalledApp()
    suspend fun updateHourlyUsage()
    suspend fun updateDailyUsage()
    suspend fun updateWeeklyUsage()
    suspend fun updateMonthlyUsage()

    suspend fun deleteUndetectedUsageObj()

    suspend fun getAllSelectedAppUsage(): List<FourUsageDomainData>     // 월, 주, 어제, 오늘 사용 시간

    suspend fun getAppIconForAppSetting(appName: String): Pair<ImageBitmap?, String>
    suspend fun getAppIcon(appName: String): ImageBitmap
}