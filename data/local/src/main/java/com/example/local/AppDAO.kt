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
    suspend fun getAll(): List<AppData>

    @Query("SELECT * FROM app_data WHERE appName = :appName")
    suspend fun getByName(appName: String): AppData?

    @Upsert
    suspend fun upsert(app: AppData)

    @Query("DELETE FROM app_data WHERE appName = :appName")
    suspend fun deleteByName(appName: String): Int
}