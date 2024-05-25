package com.example.repository

import com.example.local.dailyUsage.DailyDAO
import com.example.local.horulyUsage.HourlyDAO
import com.example.network.appUsage.NetworkDataSource
import com.example.service.DateFactoryForData
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val hourlyDao: HourlyDAO,
    private val dailyDao: DailyDAO,
    private val networkService: NetworkDataSource,
    private val dateFactory: DateFactoryForData,
): NetworkRepository {

    override suspend fun updateEntireNetworkHourly() {
        val listAllHourly = hourlyDao.getAll()
        // 서버에 데이터 PUT
        listAllHourly.forEach {
            networkService.postHourlyData(it)
        }
    }

    override suspend fun updateTodayNetworkHourly() {
        val today = dateFactory.returnStringDate(dateFactory.returnToday())
    }

    override suspend fun updateYesterdayAtMidnight() {
        TODO("Not yet implemented")
    }

    override suspend fun updateEntireNetworkDaily() {
        val listAllDaily = dailyDao.getAll()
        // 서버에 데이터 PUT
        listAllDaily.forEach {
            networkService.postDailyData(it)
        }
    }
}