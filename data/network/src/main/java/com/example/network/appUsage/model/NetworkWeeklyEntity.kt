package com.example.network.appUsage.model

import android.content.Context
import com.example.local.user.UserTokenStore
import com.example.local.weeklyUsage.WeeklyEntity

data class NetworkWeeklyEntity(
    val appName: String ="",
    val date: String = "00000000",  // 주차의 시작 날짜(일요일)
    val weeklyUsage: Int = 0,
    val user_name: String = ""
)

fun WeeklyEntity.asNetworkWeeklyEntity(context: Context) = NetworkWeeklyEntity(
    appName = appName,
    date = date,
    weeklyUsage = weeklyUsage ,
    user_name = UserTokenStore.getUserId(context)
)