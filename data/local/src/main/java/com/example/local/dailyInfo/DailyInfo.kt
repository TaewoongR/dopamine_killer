package com.example.local.dailyInfo

data class DailyInfo (
    val appName: String,
    val thisInfoUpdateTime: String,     // 평균 사용량 마지막 업데이트 시간을 확인하기 위한 변수
    val lastUpdateTime: Long,
    val latestUpdateTIme: Long,
    val lastMonthAvgUsage: Int,
    val lastWeekAvgUsage: Int
)