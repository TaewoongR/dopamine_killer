package com.example.repository

import android.content.Context
import com.example.local.dailyUsage.DailyDAO
import com.example.local.horulyUsage.HourlyDAO
import com.example.local.record.RecordDAO
import com.example.local.weeklyUsage.WeeklyDAO
import com.example.network.appUsage.NetworkDataSource
import com.example.service.DateFactoryForData
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val hourlyDao: HourlyDAO,
    private val dailyDao: DailyDAO,
    private val weeklyDao: WeeklyDAO,
    private val recordDao: RecordDAO,
    private val networkService: NetworkDataSource,
    private val dateFactory: DateFactoryForData,
): NetworkRepository {

    override suspend fun updateEntireNetworkHourly(context: Context) {
        val listAllHourly = hourlyDao.getAll()
        // 서버에 데이터 PUT
        listAllHourly.forEach {
            networkService.postHourlyData(it, context)
        }
    }

    override suspend fun updateEntireNetworkDaily(context: Context) {
        val listAllDaily = dailyDao.getAll()
        // 서버에 데이터 PUT
        listAllDaily.forEach {
            networkService.postDailyData(it, context)
        }
    }

    override suspend fun updateEntireNetworkWeekly(context: Context) {
        val listAllDaily = weeklyDao.getAll()
        // 서버에 데이터 PUT
        listAllDaily.forEach {
            networkService.postWeeklyData(it, context)
        }
    }

    override suspend fun updateTodayNetworkHourly() {
        val today = dateFactory.returnStringDate(dateFactory.returnToday())
    }

    override suspend fun updateYesterdayAtMidnight() {
        TODO("Not yet implemented")
    }

    override suspend fun postGoal(context: Context){
        val listRecord = recordDao.getAllList()
        listRecord.forEach {
            networkService.postRecordData(it, context)
        }
    }
}