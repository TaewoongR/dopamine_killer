package com.example.network.appUsage.model

import android.content.Context
import com.example.local.dailyUsage.DailyEntity
import com.example.local.user.UserTokenStore

data class NetworkDailyEntity(
    val id: Long = 0L,
    val appName: String ="",
    val date: String = "00000000",
    val dayOfWeek: Int = 0, // Calendar 라이브러리 일 = 1, 토 = 7
    val dailyUsage: Int = 0,
    val userName: String ="",
    )

fun DailyEntity.asNetworkDailyEntity(context: Context) = NetworkDailyEntity(
    appName = appName,
    date = date,
    dayOfWeek = dayOfWeek,
    dailyUsage = dailyUsage,
    userName = UserTokenStore.getUserId(context)
)

fun NetworkDailyEntity.asDailyEntity() = DailyEntity(
    appName = appName,
    date = date,
    dayOfWeek = dayOfWeek,
    dailyUsage = dailyUsage
)