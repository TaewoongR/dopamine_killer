package com.example.network.appUsage.model

import android.content.Context
import com.example.local.record.RecordEntity
import com.example.local.user.UserTokenStore

data class NetworkRecordEntity(
    val appName: String,
    val date: String,
    val goalTime: Int,
    val howLong: Int = 0,
    val onGoing: Boolean = true,
    val userName: String
)

fun RecordEntity.asNetworkRecordEntity(context: Context) = NetworkRecordEntity(
    appName = appName,
    date = date,
    goalTime = goalTime,
    howLong = howLong,
    onGoing = onGoing,
    userName = UserTokenStore.getUserId(context)
)



