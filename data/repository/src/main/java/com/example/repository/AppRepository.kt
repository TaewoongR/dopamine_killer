package com.example.repository

import com.example.local.AppData

interface AppRepository {
    fun updateAppName() : List<String>
    suspend fun updateAppTime(appName: String)
    suspend fun getAppDataByName(appName: String): AppData // 앱 데이터를 로드하는 메서드 추가
}