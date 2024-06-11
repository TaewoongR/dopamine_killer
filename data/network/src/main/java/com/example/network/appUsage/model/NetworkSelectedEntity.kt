package com.example.network.appUsage.model

import android.content.Context
import com.example.local.selectedApp.SelectedAppEntity
import com.example.local.user.UserTokenStore

data class NetworkSelectedEntity(
    val userName: String ="",
    val appName: String ="",
    val packageName: String ="",
    val isSelected: Int = 0,
)

fun SelectedAppEntity.asNetworkSelectedEntity(context: Context) = NetworkSelectedEntity(
    userName = UserTokenStore.getUserId(context),
    appName = appName,
    packageName = packageName,
    isSelected = if(isSelected) 1 else 0
)

fun NetworkSelectedEntity.asSelectedAppEntity() = SelectedAppEntity(
    appName = appName,
    packageName = packageName,
    isSelected = isSelected == 1
)