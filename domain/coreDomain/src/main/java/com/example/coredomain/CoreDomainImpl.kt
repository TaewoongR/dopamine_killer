package com.example.coredomain

import androidx.compose.ui.graphics.ImageBitmap
import com.example.local.R
import com.example.repository.DailyRepository
import com.example.repository.GoalRepository
import com.example.repository.MonthlyRepository
import com.example.repository.SelectedAppRepository
import com.example.repository.WeeklyRepository
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoreDomainImpl @Inject constructor(
    private val appFetchingRepository: AppFetchingInfo,
    private val dailyRepository: DailyRepository,
    private val weeklyRepository: WeeklyRepository,
    private val monthlyRepository: MonthlyRepository,
    private val selectedAppRepository: SelectedAppRepository,
    private val goalRepository: GoalRepository,
    private val dateFactory: DateFactoryForData
) : CoreDomain {

    private val mutex = Mutex()

    override suspend fun initialUpdate(appNameList: List<String>) {
        mutex.withLock {
            withContext(Dispatchers.IO) {
                dailyRepository.initialHourlyDailyUpdate(appNameList)}
            withContext(Dispatchers.IO) {
                weeklyRepository.initialWeeklyUpdate(appNameList)}
            withContext(Dispatchers.IO) {
                monthlyRepository.initialMonthlyUpdate(appNameList)}
            }
    }

    override suspend fun updateInitialSelectedApp(appNameList: List<String>) {
        withContext(Dispatchers.IO) {
            selectedAppRepository.updateSelected(appNameList)
        }
    }

    override suspend fun updateInitialInstalledApp(appNameList: List<String>) {
        withContext(Dispatchers.IO) { selectedAppRepository.updatedInstalled(appNameList, true) }
    }

    override suspend fun updatePeriodicInstalledApp() {
        withContext(Dispatchers.IO) {
            val appNameList = mutableListOf<String>()
            val prefix = "app_" // 필터링에 사용할 접두사
            val fields = R.string::class.java.fields // R.string 클래스의 모든 필드를 가져옴
            for (field in fields) {
                if (field.name.startsWith(prefix)) {
                    val savedAppName = field.name.removePrefix(prefix).replace("_", " ") // 접두사를 제거한 이름
                    appNameList.add(savedAppName)
                }
            }
            selectedAppRepository.updatedInstalled(appNameList, false)
        }
    }

    override suspend fun updateHourlyDailyUsage() {
        withContext(Dispatchers.IO) {dailyRepository.periodicHourlyDailyUpdate()}
    }

    override suspend fun updateWeeklyUsage() {
        val isSunday = dateFactory.returnDayOfWeek(dateFactory.returnToday())
        if(isSunday == 1){
            val appNameList = selectedAppRepository.getAllInstalled()
            val weeklyList = mutableListOf<Triple<String,String,Int>>()
            appNameList.forEach {
                var totalWeeklyHour = 0
                for(i in 1..7){
                    totalWeeklyHour += dailyRepository.getDailyUsageFrom(it, i).first
                }
                weeklyList.add(Triple(it, dateFactory.returnStringDate(dateFactory.returnTheDayStart(7)),totalWeeklyHour))
            }
            weeklyRepository.periodicWeeklyUpdate(weeklyList)
        }
    }

    override suspend fun updateMonthlyUsage() {
        val isFirstOfMonth = dateFactory.returnDayOfMonth(dateFactory.returnToday())
        if(isFirstOfMonth == 1){
            val endDateOfMonth = dateFactory.returnLastMonthEndDate(dateFactory.returnTheDayStart(1))
            val appNameList = selectedAppRepository.getAllInstalled()
            val monthlyList = mutableListOf<Triple<String,String,Int>>()
            appNameList.forEach {
                var totalMonthlyHour = 0
                for(i in 1..endDateOfMonth){
                    totalMonthlyHour += weeklyRepository.getWeeklyUsageFrom(it, i).first
                }
                monthlyList.add(Triple(it, dateFactory.returnStringDate(dateFactory.returnTheDayStart(endDateOfMonth)),totalMonthlyHour))
            }
            monthlyRepository.periodicMonthlyUpdate(monthlyList)
        }
    }

    override suspend fun deleteUndetectedUsageObj() {
        withContext(Dispatchers.IO) {
            dailyRepository.deleteUndetected()
            weeklyRepository.deleteUndetected()
            monthlyRepository.deleteUndetected()
        }
    }

    override suspend fun getAllSelectedAppUsage(): List<FourUsageDomainData> {
        return mutex.withLock {
            val nameList = withContext(Dispatchers.IO) {selectedAppRepository.getAllSelected()}
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
            list
        }
    }

    override suspend fun getAppIconForAppSetting(appName: String): ImageBitmap?{
        if (appFetchingRepository.isAppInstalled(appFetchingRepository.getPackageNameBy(appName)) != "null") {
            return appFetchingRepository.getAppIcon(appName)
        } else
            return null
    }

    override suspend fun getAppIcon(appName: String): ImageBitmap {
        return appFetchingRepository.getAppIcon(appName)
    }

    override suspend fun clearAllDatabase() {
        withContext(Dispatchers.IO) {
            dailyRepository.deleteHourlyDaily()
            weeklyRepository.deleteWeekly()
            monthlyRepository.deleteMonthly()
            goalRepository.deleteGoal()
            selectedAppRepository.deleteSelected()
        }
    }
}
