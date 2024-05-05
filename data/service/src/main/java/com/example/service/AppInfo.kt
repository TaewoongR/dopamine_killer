package com.example.service

import androidx.compose.ui.graphics.ImageBitmap

interface AppInfo {
    suspend fun getAppNameList() : List<String>
    suspend fun getHourlyTime(appName: String, startTime: Long): IntArray
    suspend fun findAppByName(name: String): String
    suspend fun getAppIcon(packageName: String): ImageBitmap
}