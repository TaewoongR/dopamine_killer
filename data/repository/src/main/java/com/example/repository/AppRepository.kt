package com.example.repository

import android.content.Context
import com.example.local.AppData

interface AppRepository {
    fun updateAppName() : List<String>
    fun updateAppTime(appName: String, context: Context)
    suspend fun getAppData(): List<AppData> // 앱 데이터를 로드하는 메서드 추가
}