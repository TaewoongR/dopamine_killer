package com.example.local.dailyInfo

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface DailyInfoDAO {
    @Query("SELECT thisInfoUpdateTime FROM daily_info WHERE appName = :appName")
    fun isExist(appName: String): String?

    @Query("SELECT * FROM daily_info WHERE appName = :appName")
    fun get(appName: String): DailyInfoEntity

    @Upsert
    fun upsert(dailyInfo: DailyInfoEntity)

    @Query("UPDATE daily_info SET lastWeekAvgUsage = :newUpdateTime WHERE appName = :appName")
    fun updateLastWeekAvg(appName: String, newUpdateTime: Int)

    @Query("UPDATE daily_info SET lastMonthAvgUsage = :newUpdateTime WHERE appName = :appName")
    fun updateLastMonthAvg(appName: String, newUpdateTime: Int)

    @Query("UPDATE daily_info SET thisInfoUpdateTime = :newUpdateDate WHERE appName = :appName")
    fun updateDailyInfoDate(appName: String, newUpdateDate: String)

    @Query("SELECT thisInfoUpdateTime FROM daily_info WHERE appName = :appName")
    fun getUpdateTime(appName: String): String
}