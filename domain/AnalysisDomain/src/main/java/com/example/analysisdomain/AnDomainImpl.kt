package com.example.analysisdomain

import com.example.repository.AppRepository
import com.example.repository.DailyRepository
import javax.inject.Inject

class AnDomainImpl @Inject constructor(
    private val appRepository: AppRepository,
    private val dailyRepository: DailyRepository
): AnDomain {
    override suspend fun getTodayHour(appName: String, date: String): Int{
        return appRepository.getDailyUsageByApp(appName,date)
    }

    override suspend fun getYesterdayHour(appName: String, date: String): Int {
        return appRepository.getDailyUsageByApp(appName, date)
    }

    override suspend fun getLastWeekAvgHour(appName: String, todayDate: String): Int {
        return dailyRepository.getLastWeekAvgUsageByApp(appName, todayDate)
    }

    override suspend fun getLastMonthAvgHour(appName: String, todayDate: String): Int {
        return dailyRepository.getLastMonthAvgUsageByApp(appName, todayDate)
    }
}