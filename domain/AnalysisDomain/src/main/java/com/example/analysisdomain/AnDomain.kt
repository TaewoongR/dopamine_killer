package com.example.analysisdomain

interface AnDomain{
    suspend fun getTodayHour(appName: String, date: String): Int

    suspend fun getYesterdayHour(appName: String, date: String): Int

    suspend fun getLastWeekAvgHour(appName: String, todayDate: String): Int

    suspend fun getLastMonthAvgHour(appName: String, todayDate: String): Int
}