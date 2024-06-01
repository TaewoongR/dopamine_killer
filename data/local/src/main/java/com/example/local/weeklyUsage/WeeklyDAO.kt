package com.example.local.weeklyUsage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface WeeklyDAO {

    @Query("SELECT * FROM weekly_usage WHERE appName = :appName AND date = :date")
    fun get(appName: String, date: String): WeeklyEntity

    @Query("SELECT * FROM weekly_usage")
    fun getAll(): List<WeeklyEntity>

    @Query("DELETE FROM weekly_usage WHERE weeklyUsage = 0")
    fun delete()

    @Upsert
    fun upsert(weeklyEntity: WeeklyEntity)

    @Query("DELETE FROM weekly_usage")
    fun clearAll()
}