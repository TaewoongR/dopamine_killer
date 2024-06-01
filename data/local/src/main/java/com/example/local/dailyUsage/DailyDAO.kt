package com.example.local.dailyUsage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface DailyDAO {

    @Query("SELECT * FROM daily_usage")
    fun getAll(): List<DailyEntity>

    @Query("SELECT * FROM daily_usage WHERE appName = :appName AND date = :date")
    fun get(appName: String, date: String): DailyEntity

    @Query("DELETE FROM daily_usage WHERE date =:date")
    fun deleteOnDate(date: String)

    @Upsert
    fun upsert(dailyEntity: DailyEntity)

    @Query("DELETE FROM daily_usage")
    fun clearAll()
}