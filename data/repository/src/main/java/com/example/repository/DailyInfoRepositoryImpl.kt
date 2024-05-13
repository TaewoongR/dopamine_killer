package com.example.repository

import android.util.Log
import com.example.local.dailyInfo.DailyInfoDAO
import com.example.local.dailyInfo.DailyInfoEntity
import com.example.local.horulyUsage.HourlyDAO
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyInfoRepositoryImpl @Inject constructor(
    private val appInfo: AppFetchingInfo,
    private val dailyInfoSource: DailyInfoDAO,
    private val appInfoSource: HourlyDAO,
    private val dateFactory: DateFactoryForData
) : DailyInfoRepository{
    private val todayMilli = dateFactory.returnToday()
    private val todayDate = dateFactory.returnStringDate(todayMilli)

    override suspend fun createDailyInfo(appName: String) {
        dailyInfoSource.upsert(
            DailyInfoEntity(
                appName = appName,
                lastUpdateTime = dateFactory.returnTheDayStart(6),
                latestUpdateTIme = todayMilli
            )
        )
    }

    private suspend fun dailyExist(appName: String): String?{
        val name = appInfo.findAppByName(appName)
        return dailyInfoSource.isExist(appName = name)
    }

    override suspend fun getLastWeekAvgUsageByApp(appName: String): Int{
        if(dailyExist(appName) == null){
            updateDailyInfo(appName)
        }
        if (dailyInfoSource.getUpdateTime(appName) != todayDate) {
            updateDailyInfo(appName)
        }
        return dailyInfoSource.get(appName).lastWeekAvgUsage
    }

    override suspend fun getLastMonthAvgUsageByApp(appName: String): Int{
        if(dailyExist(appName) == null){
            updateDailyInfo(appName)
        }
        if (dailyInfoSource.get(appName).thisInfoUpdateTime != todayDate) {
            updateDailyInfo(appName)
        }
        return dailyInfoSource.get(appName).lastMonthAvgUsage
    }

    private suspend fun updateDailyInfo(appName: String){
        updateLastWeekAvgUsage(appName)
        updateLastMonthAvgUsage(appName)
        dailyInfoSource.updateDailyInfoDate(appName,todayDate)
    }

    private fun updateLastWeekAvgUsage(appName: String){
        if(appInfoSource.getByNameDate(appName, todayDate).dayOfWeek != 1){     // 최초 업데이트시 조건 추가 필요
            var totalHour = 0
            for( i in 6 downTo 0){
                val lastDate = dateFactory.returnStringDate(dateFactory.returnTheDayStart(i))
                val usage = appInfoSource.getTheDayUsage(appName, lastDate)
                totalHour += usage
                Log.d("lastWeek",totalHour.toString())
            }
            val weekAvg = totalHour / 7
            dailyInfoSource.updateLastWeekAvg(appName,weekAvg)
        }
    }

     private suspend fun updateLastMonthAvgUsage(appName: String){
        val lastUpDate = dateFactory.returnStringDate(dailyInfoSource.get(appName).lastUpdateTime)
        val lastMonthStartMilli = dateFactory.returnLastMonthStart()
        val lastMonthStartString = dateFactory.returnStringDate(lastMonthStartMilli)
        val packageName = appInfo.findAppByName(appName)

        if(lastUpDate <= lastMonthStartString && todayDate.substring(0,6) != dailyInfoSource.get(appName).thisInfoUpdateTime.substring(0,6)){
            val lastDate = dateFactory.returnLastMonthEndDate(lastMonthStartMilli)
            val theDay = lastMonthStartString.toInt()
            var totalHour = 0
            for(i in 0..<lastDate){
                totalHour += appInfoSource.getTheDayUsage(appName, (theDay + i).toString())
            }
            Log.d("number1","number1")
            dailyInfoSource.updateLastMonthAvg(appName,totalHour / lastDate)
        }else if(lastUpDate > lastMonthStartString){
            Log.d("number2","number2")
            dailyInfoSource.updateLastMonthAvg(appName, appInfo.getLastMonthAvgUsage(packageName))
        }
    }


}
