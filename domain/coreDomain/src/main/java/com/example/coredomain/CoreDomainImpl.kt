package com.example.coredomain

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.example.repository.DailyRepository
import com.example.repository.HourlyRepository
import com.example.repository.MonthlyRepository
import com.example.repository.SelectedAppRepository
import com.example.repository.WeeklyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoreDomainImpl @Inject constructor(
    private val hourlyRepository: HourlyRepository,
    private val dailyRepository: DailyRepository,
    private val weeklyRepository: WeeklyRepository,
    private val monthlyRepository: MonthlyRepository,
    private val selectedAppRepository: SelectedAppRepository
): CoreDomain{

    override suspend fun initialUpdate() {
        withContext(Dispatchers.IO){
            dailyRepository.initialDailyUpdate()
            weeklyRepository.initialWeeklyUpdate()
            monthlyRepository.initialMonthlyUpdate()
        }
    }

    override suspend fun updateSelectedApp(appNameList: List<String>, isSelected: Boolean) {
        Log.d("updateSelectedApp","updateSelectedApp")
        withContext(Dispatchers.IO){selectedAppRepository.updateSelected(appNameList, isSelected)}
    }

    override suspend fun updateInstalledApp(appNameList: List<String>) {
        withContext(Dispatchers.IO){selectedAppRepository.updateSelected(appNameList)}
    }

    override suspend fun deleteUndetectedUsageObj() {
        withContext(Dispatchers.IO){
            dailyRepository.deleteUndetected()
            weeklyRepository.deleteUndetected()
            monthlyRepository.deleteUndetected()
        }
    }

    override suspend fun getAppIconForAppSetting(appName: String): Pair<ImageBitmap?, String> {
        val packageName = hourlyRepository.getInstalledAppName(appName)
        if(packageName != "null"){
            return Pair(hourlyRepository.getAppIcon(appName), packageName)
        }else
            return Pair(null, packageName)
    }

    override suspend fun getAppIcon(appName: String): ImageBitmap {
        return hourlyRepository.getAppIcon(appName)
    }

    override suspend fun updateEntireHourlyUsage() {
        val nameList = selectedAppRepository.getAllSelected()
        for(name in nameList){
            val packageName = hourlyRepository.getInstalledAppName(name)
            Log.d("package Name found?",packageName)
            for(i in 0..6)
                hourlyRepository.updateHourlyTime(name,packageName, i)
        }
    }
}