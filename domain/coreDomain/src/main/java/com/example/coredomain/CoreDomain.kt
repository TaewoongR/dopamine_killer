package com.example.coredomain

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap

interface CoreDomain {
    suspend fun initialUpdate(appNameList: List<String>)     // 최초 앱 실행시 실행
    suspend fun loginUpdate(token: String,username: String)     // 로그인시 실행
    suspend fun updateInitialSelectedApp(appNameList: List<String>)      // 목표 앱 선택 항목 업데이트
    suspend fun updateInitialInstalledApp(appNameList: List<String>)
    suspend fun updatePeriodicInstalledApp()
    suspend fun updateAutoHourlyDailyUsage()
    suspend fun updateHourlyDailyUsage()
    suspend fun updateWeeklyUsage()
    suspend fun updateMonthlyUsage()
    suspend fun updateRecord()
    suspend fun monitoringUsageByGoal(): List<Pair<Int, String>>

    suspend fun getAllSelectedAppUsage(fromAnalysis: Boolean): List<FourUsageDomainData>   // 월, 주, 어제, 오늘 사용 시간
    suspend fun getAppIconForAppSetting(appName: String): ImageBitmap?
    suspend fun getAppIcon(appName: String): ImageBitmap
    suspend fun clearAllDatabase()

    suspend fun postNetworkHourly(context: Context)
    suspend fun postNetworkDaily(context: Context)
    suspend fun postNetworkWeekly(context: Context)
    suspend fun postNetworkMonthly(context: Context)
    suspend fun postGoal(context: Context)
    suspend fun postSelected(context: Context)

    suspend fun postCoreData(context: Context)
}