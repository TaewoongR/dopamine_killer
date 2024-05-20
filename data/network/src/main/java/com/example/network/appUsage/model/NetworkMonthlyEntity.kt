package com.example.network.appUsage.model

import com.example.local.monthlyUsage.MonthlyEntity

data class NetworkMonthlyEntity (
    val appName: String ="",
    val date: String = "00000000",  // 주차의 시작 날짜(일요일)
    val monthlyUsage: Int = 0,
)

fun MonthlyEntity.asNetworkMonthlyEntity() = NetworkMonthlyEntity(
    appName = appName,
    date = date,
    monthlyUsage = monthlyUsage
)