package com.example.repository

import com.example.local.horulyUsage.HourlyDAO
import com.example.local.horulyUsage.HourlyEntity
import com.example.network.appUsage.NetworkDataSource
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val appDao: HourlyDAO,
    private val networkService: NetworkDataSource
): NetworkRepository {

    override suspend fun updateUser(appUsage: List<HourlyEntity>) {

        // 서버에 데이터 PUT
        networkService.saveAppUsage(listOf())
    }
}