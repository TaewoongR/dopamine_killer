package com.example.repository

import com.example.local.appUsage.AppDAO
import com.example.local.dailyInfo.DailyDAO
import com.example.service.AppInfo
import com.example.service.DateFactoryForData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyRepositoryImpl @Inject constructor(
    private val appInfo: AppInfo,
    private val dailyInfoSource: DailyDAO,
    private val appInfoSource: AppDAO,
    private val dateFactory: DateFactoryForData
) : DailyRepository{

    private fun dailyExist(appName: String): String?{
        return dailyInfoSource.isExist(appName = appName)
    }

    override suspend fun getLastWeekAvgUsageByApp(appName: String, todayDate: String): Int{
        if(dailyExist(appName) == null){
            updateDailyInfo(appName, todayDate)
        }
        if (dailyInfoSource.getUpdateTime(appName) != todayDate) {
            updateDailyInfo(appName, todayDate)
        }
        return dailyInfoSource.get(appName).lastWeekAvgUsage
    }

    override suspend fun getLastMonthAvgUsageByApp(appName: String, todayDate: String): Int{
        if(dailyExist(appName) == null){
            updateDailyInfo(appName, todayDate)
        }
        if (dailyInfoSource.get(appName).thisInfoUpdateTime != todayDate) {
            updateDailyInfo(appName, todayDate)
        }
        return dailyInfoSource.get(appName).lastMonthAvgUsage
    }

    private suspend fun updateDailyInfo(appName: String, todayDate: String){
        updateLastWeekAvgUsage(appName, todayDate)
        updateLastMonthAvgUsage(appName, todayDate)
        dailyInfoSource.updateDailyInfoDate(appName,todayDate)
    }

    private fun updateLastWeekAvgUsage(appName: String, todayDate: String){
        if(appInfoSource.getByNameDate(appName, todayDate).dayOfWeek == 1){
            var totalHour = 0
            for( i in 6 downTo 0){
                val lastDate = dateFactory.returnStringDate(dateFactory.returnTheDayStart(i))
                val usage = appInfoSource.getTheDayUsage(appName, lastDate)
                totalHour += usage
            }
            val weekAvg = totalHour / 7
            dailyInfoSource.updateLastWeekAvg(appName,weekAvg)
        }
    }

     private suspend fun updateLastMonthAvgUsage(appName: String, todayDate: String){
        val lastUpDate = dateFactory.returnStringDate(dailyInfoSource.get(appName).lastUpdateTime)
        val lastMonthStartMilli = dateFactory.returnLastMonthStart()
        val lastMonthStartString = dateFactory.returnStringDate(lastMonthStartMilli)

        if(lastUpDate <= lastMonthStartString && todayDate.substring(0,6) != dailyInfoSource.get(appName)!!.thisInfoUpdateTime.substring(0,6)){
            val lastDate = dateFactory.returnLastMonthEndDate(lastMonthStartMilli)
            val theDay = lastMonthStartString.toInt()
            var totalHour = 0
            for(i in 0..<lastDate){
                totalHour += appInfoSource.getTheDayUsage(appName, (theDay + i).toString())
            }
            dailyInfoSource.updateLastMonthAvg(appName,totalHour / lastDate)
        }else if(lastUpDate > lastMonthStartString){
            dailyInfoSource.updateLastMonthAvg(appName, appInfo.getLastMonthAvgUsage(appName))
        }
    }


}
