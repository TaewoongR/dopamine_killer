package com.example.analysisdomain

import com.example.repository.DailyRepository
import com.example.repository.HourlyRepository
import com.example.repository.MonthlyRepository
import com.example.repository.SelectedAppRepository
import com.example.repository.WeeklyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnDomainImpl @Inject constructor(
    private val hourlyRepository: HourlyRepository,
    private val dailyRepository: DailyRepository,
    private val weeklyRepository: WeeklyRepository,
    private val monthlyRepository: MonthlyRepository,
    private val selectedAppRepository: SelectedAppRepository
): AnDomain {

    override suspend fun getEntireSelectedApp(): List<String> {
        return withContext(Dispatchers.IO){selectedAppRepository.getAllSelected()}
    }

    override suspend fun getAllSelectedAppUsage(): List<AnalysisDataDomain> {
        val nameList = withContext(Dispatchers.IO){selectedAppRepository.getAllSelected()}
        val list = mutableListOf<AnalysisDataDomain>()
        nameList.forEach {
            withContext(Dispatchers.IO){
                list.add(
                    AnalysisDataDomain(
                        appName = it,
                        dailyTime = dailyRepository.getDailyUsageFrom(it, 0).first,
                        yesterdayTime = dailyRepository.getDailyUsageFrom(it, 1).first,
                        lastWeekAvgTime = weeklyRepository.getWeeklyUsageFrom(it,1).first,
                        lastMonthAvgTime = monthlyRepository.getMonthlyUsageFrom(it,1).first
                    )
                )
            }
        }
        return list
    }
}