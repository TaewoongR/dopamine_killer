package com.example.coredomain

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.example.local.R
import com.example.repository.DailyRepository
import com.example.repository.GoalRepository
import com.example.repository.MonthlyRepository
import com.example.repository.NetworkRepository
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
    private val dateFactory: DateFactoryForData,
    private val networkRepository: NetworkRepository
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

    override suspend fun loginUpdate(token: String,username: String){
        Log.d("core domain", "saving hourly usage from Server")

        withContext(Dispatchers.IO){
            networkRepository.getHourly(token,username).forEach {
                dailyRepository.saveHourlyUsage(it)
            }
        }
        withContext(Dispatchers.IO){
            networkRepository.getDaily(token,username).forEach {
                dailyRepository.saveDailyUsage(it)
            }
        }
        withContext(Dispatchers.IO){
            networkRepository.getWeekly(token,username).forEach {
                weeklyRepository.saveWeeklyUsage(it)
            }
        }
        withContext(Dispatchers.IO){
            networkRepository.getMonthly(token,username).forEach {
                monthlyRepository.saveMonthlyUsage(it)
            }
        }
        withContext(Dispatchers.IO){
            networkRepository.getRecord(token,username).forEach {
                goalRepository.saveRecord(it)
            }
        }
        withContext(Dispatchers.IO){
            networkRepository.getSelected(token, username).forEach {
                if (appFetchingRepository.isAppInstalled(it.packageName)) {
                    selectedAppRepository.saveSelected(it)
                }
            }
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
                    if(appFetchingRepository.isAppInstalled(appFetchingRepository.getPackageNameBy(savedAppName))) {
                        appNameList.add(savedAppName)
                    }
                }
            }
            selectedAppRepository.updatedInstalled(appNameList, false)
        }
    }

    override suspend fun updateHourlyDailyUsage() {
        withContext(Dispatchers.IO) {dailyRepository.periodicHourlyDailyUpdate()}
    }

    override suspend fun updateAutoHourlyDailyUsage(){
        withContext(Dispatchers.IO){dailyRepository.periodicAutoHourlyDailyUpdate()}
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
                weeklyList.add(Triple(it, dateFactory.returnStringDate(dateFactory.returnTheDayStart(7)),totalWeeklyHour / 7))
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

    override suspend fun updateRecord(accessOrPeriodic: Int) {  // 0-접속시 1-자동처리
        withContext(Dispatchers.IO) {
            val onGoingList = goalRepository.getOnGoingList().map{ Triple(it.appName,it.goalTime, it.date) }
            val realUsageYesterdayList = onGoingList.map {
                Pair(
                    it.first,
                    dailyRepository.getDailyUsageFrom(it.first , 1).first
                )
            }
            val realUsageTodayList = onGoingList.map {
                Pair(
                    it.first,
                    dailyRepository.getDailyUsageFrom(it.first , 0).first
                )
            }
            for (i in onGoingList.indices) {
                val appName = onGoingList[i].first
                val goalTime = onGoingList[i].second
                val realUsageYesterday = realUsageYesterdayList[i].second
                val realUsageToday = realUsageTodayList[i].second
                val date = onGoingList[i].third

                if(date < dateFactory.returnStringDate(System.currentTimeMillis()) && realUsageYesterday < goalTime){
                    goalRepository.succeedGoal(appName, date, dateFactory.calculateDayPassed(date))
                }
            }
        }
    }

    override suspend fun monitoringUsageByGoal(): List<Pair<Int, String>> {
        val resultList = mutableListOf<Pair<Int, String>>()
        val onGoingList = goalRepository.getOnGoingList().map { Triple(it.appName, it.goalTime, it.date) }
        val realUsageTodayList = onGoingList.map {
            Pair(
                it.first,
                dailyRepository.getDailyUsageFrom(it.first, 0).first
            )
        }

        for (i in onGoingList.indices) {
            val appName = onGoingList[i].first
            val goalTime = onGoingList[i].second
            val realUsageToday = realUsageTodayList[i].second
            val date = onGoingList[i].third

            if (realUsageToday < goalTime && (realUsageToday + 5 * 60) > goalTime) {
                resultList.add(Pair(1, appName))
            } else if (realUsageToday > goalTime) {
                goalRepository.failGoal(appName, date)
                resultList.add(Pair(2, appName))
            }
        }

        return resultList
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
        if (appFetchingRepository.isAppInstalled(appFetchingRepository.getPackageNameBy(appName))) {
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

    override suspend fun postNetworkHourly(context: Context) {
        networkRepository.postHourly(context)
    }

    override suspend fun postNetworkDaily(context: Context) {
        networkRepository.postDaily(context)
    }

    override suspend fun postNetworkWeekly(context: Context) {
        networkRepository.postWeekly(context)
    }

    override suspend fun postNetworkMonthly(context: Context){
        networkRepository.postMonthly(context)
    }

    override suspend fun postGoal(context: Context){
        networkRepository.postGoal(context)
    }

    override suspend fun postSelected(context: Context){
        networkRepository.postSelected(context)
    }

    override suspend fun postCoreData(context: Context){
        postNetworkHourly(context)
        postNetworkDaily(context)
        postNetworkWeekly(context)
        postNetworkMonthly(context)
        postGoal(context)
        postSelected(context)
    }
}
