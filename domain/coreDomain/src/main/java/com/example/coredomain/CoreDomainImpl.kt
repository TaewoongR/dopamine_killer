package com.example.coredomain

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.example.repository.DailyRepository
import com.example.repository.MonthlyRepository
import com.example.repository.SelectedAppRepository
import com.example.repository.WeeklyRepository
import com.example.service.AppFetchingInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoreDomainImpl @Inject constructor(
    private val appRepository: AppFetchingInfo,
    private val dailyRepository: DailyRepository,
    private val weeklyRepository: WeeklyRepository,
    private val monthlyRepository: MonthlyRepository,
    private val selectedAppRepository: SelectedAppRepository
): CoreDomain {

    override suspend fun initialUpdate(appNameList: List<String>) {
        withContext(Dispatchers.IO) {dailyRepository.initialDailyUpdate(appNameList)}
        withContext(Dispatchers.IO) {weeklyRepository.initialWeeklyUpdate(appNameList)}
        withContext(Dispatchers.IO) {monthlyRepository.initialMonthlyUpdate(appNameList) }
    }

    override suspend fun initialHourlyUpdate(appNameList: List<String>) {
        withContext(Dispatchers.IO) {
            dailyRepository.initialHourlyUpdate(appNameList)
        }
    }

    override suspend fun updateSelectedApp(appNameList: List<String>, isSelected: Boolean) {
        Log.d("updateSelectedApp", "updateSelectedApp")
        withContext(Dispatchers.IO) {
            selectedAppRepository.updateSelected(
                appNameList,
                isSelected
            )
        }
    }

    override suspend fun updateInstalledApp(appNameList: List<String>) {
        withContext(Dispatchers.IO) { selectedAppRepository.updateSelected(appNameList) }
    }

    override suspend fun deleteUndetectedUsageObj() {
        withContext(Dispatchers.IO) {
            dailyRepository.deleteUndetected()
            weeklyRepository.deleteUndetected()
            monthlyRepository.deleteUndetected()
        }
    }

    override suspend fun getAllSelectedAppName(): List<String> {
        return withContext(Dispatchers.IO) { selectedAppRepository.getAllSelected() }
    }

    override suspend fun getAllSelectedAppUsage(): List<FourUsageDomainData> {
        val nameList = withContext(Dispatchers.IO) { getAllSelectedAppName() }
        val list = mutableListOf<FourUsageDomainData>()
        nameList.forEach {
            withContext(Dispatchers.IO) {
                list.add(
                    FourUsageDomainData(
                        appName = it,
                        dailyTime = dailyRepository.getDailyUsageFrom(it, 0).first,
                        yesterdayTime = dailyRepository.getDailyUsageFrom(it, 1).first,
                        lastWeekAvgTime = weeklyRepository.getWeeklyUsageFrom(it, 1).first,
                        lastMonthAvgTime = monthlyRepository.getMonthlyUsageFrom(it, 1).first
                    )
                )
            }
        }
        return list
    }

    override suspend fun getAppIconForAppSetting(appName: String): Pair<ImageBitmap?, String> {
        val packageName = appRepository.findAppByName(appName)
        if(packageName != "null"){
            return Pair(appRepository.getAppIcon(appName), appName)
        }else
            return Pair(null, packageName)
    }

    override suspend fun getAppIcon(appName: String): ImageBitmap {
        return appRepository.getAppIcon(appName)
    }
}