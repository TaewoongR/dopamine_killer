package com.example.network.appUsage.model

import com.example.local.dailyUsage.DailyEntity

data class NetworkDailyEntity(
    val userName: String ="",
    val appName: String ="",
    val date: String = "00000000",
    val dayOfWeek: Int = 0, // Calendar 라이브러리 일 = 1, 토 = 7
    val dailyUsage: Int = 0
)

fun DailyEntity.asNetworkDailyEntity() = NetworkDailyEntity(
    appName = appName,
    date = date,
    dayOfWeek = dayOfWeek,
    dailyUsage = dailyUsage
)
