package com.example.repository

import com.example.local.horulyUsage.HourlyEntity

interface NetworkRepository {
    suspend fun updateUser(appUsage: List<HourlyEntity>)
}