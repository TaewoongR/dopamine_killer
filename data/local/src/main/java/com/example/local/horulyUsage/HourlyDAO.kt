package com.example.local.horulyUsage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/**
 * Data Access Object for the appdata table.
 */
@Dao
interface HourlyDAO {

    @Query("SELECT * FROM hourly_usage")
    fun getAll(): List<HourlyEntity>

    @Query("SELECT * FROM hourly_usage WHERE appName = :appName AND date = :date")
    fun getByNameDate(appName: String, date: String): HourlyEntity

    @Upsert
    fun upsert(app: HourlyEntity)

    @Query("DELETE FROM hourly_usage WHERE appName = :appName")
    fun deleteByName(appName: String): Int
}