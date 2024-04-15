package com.example.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/**
 * Data Access Object for the appdata table.
 */
@Dao
interface AppDAO {

    @Query("SELECT * FROM app_data")
    fun getAll(): List<AppData>

    @Query("SELECT * FROM app_data WHERE appName = :appName")
    fun getByName(appName: String): AppData?

    @Upsert
    fun upsert(app: AppData)

    @Query("DELETE FROM app_data WHERE appName = :appName")
    fun deleteByName(appName: String): Int
}