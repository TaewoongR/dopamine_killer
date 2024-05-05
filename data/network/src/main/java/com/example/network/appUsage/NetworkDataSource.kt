package com.example.network.appUsage

import com.example.network.appUsage.model.NetworkAppUsageEntity

interface NetworkDataSource {
    suspend fun saveAppUsage(appUsage: List<NetworkAppUsageEntity>)
}