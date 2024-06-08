package com.example.analysisdomain

import com.example.repository.DailyRepository
import com.example.repository.MonthlyRepository
import com.example.repository.SelectedAppRepository
import com.example.repository.WeeklyRepository
import com.example.service.DateFactoryForData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnDomainImpl @Inject constructor(
    private val dailyRepository: DailyRepository,
    private val weeklyRepository: WeeklyRepository,
    private val monthlyRepository: MonthlyRepository,
    private val selectedAppRepository: SelectedAppRepository,
    private val dateFactory: DateFactoryForData
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

    override suspend fun getTodayUsage(appName: String): Int {
        return withContext(Dispatchers.IO){dailyRepository.getDailyUsageFrom(appName, 0).first}
    }

    override suspend fun get7daysAvgHourlyUsage(appName: String): List<Int> {
        val listOfLists: MutableList<List<Int>> = mutableListOf()
        val returnList = MutableList(24) { 0 }
        for(i in 0..6){
            listOfLists.add(
                withContext(Dispatchers.IO) {
                    dailyRepository.getHourlyUsage(
                        appName,
                        dateFactory.returnStringDate(dateFactory.returnTheDayStart(i))
                    )
                })
        }

        for (i in 0 until 24) {
            var sum = 0
            for (list in listOfLists) {
                sum += list[i]
            }
            returnList[i] = sum / 7
        }
        return returnList
    }

    override suspend fun get30DailyUsage(appName: String): List<Int> {
        val listOfLists: MutableList<Int> = mutableListOf()
        for(i in 0..29){
            listOfLists.add(
                withContext(Dispatchers.IO) {
                    dailyRepository.getDailyUsage(appName, dateFactory.returnStringDate(dateFactory.returnTheDayStart(i)))
                })
        }
        listOfLists.reverse()
        return listOfLists
    }
}