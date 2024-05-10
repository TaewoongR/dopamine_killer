package com.example.local.appUsage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/**
 * Data Access Object for the appdata table.
 */
@Dao
interface AppDAO {

    @Query("SELECT * FROM app_usage")
    fun getAll(): List<AppUsageEntity>

    @Query("SELECT * FROM app_usage WHERE appName = :appName AND date = :date")
    fun getByNameDate(appName: String, date: String): AppUsageEntity

    @Upsert
    fun upsert(app: AppUsageEntity)

    @Query("DELETE FROM app_usage WHERE appName = :appName")
    fun deleteByName(appName: String): Int

    @Query("SELECT totalHour FROM app_usage WHERE appName = :appName AND date = :date")
    fun getTheDayUsage(appName: String, date: String):Int
}