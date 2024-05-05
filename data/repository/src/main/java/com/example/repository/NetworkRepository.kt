package com.example.repository

import com.example.local.appUsage.AppUsageEntity

interface NetworkRepository {
    suspend fun updateUser(appUsage: List<AppUsageEntity>)
}