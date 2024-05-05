package com.example.repository

import com.example.local.appUsage.AppDAO
import com.example.local.appUsage.AppUsageEntity
import com.example.network.appUsage.NetworkDataSource
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val appDao: AppDAO,
    private val networkService: NetworkDataSource
): NetworkRepository {

    override suspend fun updateUser(appUsage: List<AppUsageEntity>) {

        // 서버에 데이터 PUT
        networkService.saveAppUsage(listOf())
    }
}