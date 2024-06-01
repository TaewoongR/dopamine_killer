package com.example.network.appUsage.model

import android.content.Context
import com.example.local.record.RecordEntity
import com.example.local.user.UserTokenStore

data class NetworkRecordEntity(
    val userName: String,
    val appName: String,
    val date: String,
    val goalTime: Int,
    val howLong: Int = 0,
    val onGoing: Int = 1
)

fun RecordEntity.asNetworkRecordEntity(context: Context) = NetworkRecordEntity(
    userName = UserTokenStore.getUserId(context),
    appName = appName,
    date = date,
    goalTime = goalTime,
    howLong = howLong,
    onGoing = if(onGoing) 1 else 0
)



