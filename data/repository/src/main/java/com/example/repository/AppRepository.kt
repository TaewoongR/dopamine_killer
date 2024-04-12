package com.example.repository

import android.content.Context

interface AppRepository {
    suspend fun getAppInfo()

    suspend fun updateAppName() : List<String>

    suspend fun updateAppTime(appName: String, context: Context)

}