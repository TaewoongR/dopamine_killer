package com.example.network.appUsage.model

import com.example.local.weeklyUsage.WeeklyEntity

data class NetworkWeeklyEntity(
    val appName: String ="",
    val date: String = "00000000",  // 주차의 시작 날짜(일요일)
    val weeklyUsage: Int = 0,
)

fun WeeklyEntity.asNetworkWeeklyEntity() = NetworkWeeklyEntity(
    appName = appName,
    date = date,
    weeklyUsage = weeklyUsage
)