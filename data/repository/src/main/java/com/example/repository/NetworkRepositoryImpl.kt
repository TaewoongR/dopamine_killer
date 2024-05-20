package com.example.repository

import com.example.local.horulyUsage.HourlyDAO
import com.example.network.appUsage.NetworkDataSource
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val hourlyDao: HourlyDAO,
    private val networkService: NetworkDataSource
): NetworkRepository {

    override suspend fun updateEntireNetworkHourly() {
        val listAllHourly = hourlyDao.getAll()
        // 서버에 데이터 PUT
        listAllHourly.forEach {
            networkService.postHourlyData(it)
        }
    }
}