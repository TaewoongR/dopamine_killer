package com.example.repository

import android.content.Context

interface NetworkRepository {
    suspend fun updateEntireNetworkHourly(context: Context)
    suspend fun updateEntireNetworkDaily(context: Context)
    suspend fun updateEntireNetworkWeekly(context: Context)
    suspend fun updateTodayNetworkHourly()
    suspend fun updateYesterdayAtMidnight()
    suspend fun postGoal(context: Context)
}