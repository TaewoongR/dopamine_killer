package com.example.network.appUsage.model

import android.content.Context
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.user.UserTokenStore

data class NetworkMonthlyEntity (
    val id: Long = 0L,
    val appName: String ="",
    val date: String = "00000000",  // 주차의 시작 날짜(일요일)
    val monthlyUsage: Int = 0,
    val userName: String
)

fun MonthlyEntity.asNetworkMonthlyEntity(context: Context) = NetworkMonthlyEntity(
    appName = appName,
    date = date,
    monthlyUsage = monthlyUsage,
    userName = UserTokenStore.getUserId(context)
)

fun NetworkMonthlyEntity.asMonthlyEntity() = MonthlyEntity(
    appName = appName,
    date = date,
    monthlyUsage = monthlyUsage
)