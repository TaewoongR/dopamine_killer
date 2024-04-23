package com.example.repository

import com.example.local.app.AppData

interface AppRepository {
    suspend fun updateHourlyTime(appName: String, startTime: Long, endTime: Long, date: String)
    suspend fun getHourlyDataByName(appName: String): AppData // 앱 데이터를 로드하는 메서드 추가
}