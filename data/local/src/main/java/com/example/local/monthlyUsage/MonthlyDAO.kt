package com.example.local.monthlyUsage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MonthlyDAO {

    @Query("SELECT * FROM monthly_usage WHERE appName = :appName AND date = :date")
    fun get(appName: String, date: String): MonthlyEntity

    @Query("DELETE FROM monthly_usage WHERE monthlyUsage = 0")
    fun delete()

    @Upsert
    fun upsert(monthlyEntity: MonthlyEntity)
}