package com.example.repository

import android.content.Context

interface AppRepository {
    fun getAppInfo()
    fun updateAppName() : List<String>
    fun updateAppTime(appName: String, context: Context)
}