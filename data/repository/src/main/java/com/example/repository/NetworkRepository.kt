package com.example.repository

interface NetworkRepository {
    suspend fun updateEntireNetworkHourly()
    suspend fun updateTodayNetworkHourly()
    suspend fun updateYesterdayAtMidnight()
}