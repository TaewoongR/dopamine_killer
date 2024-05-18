package com.example.local.selectedApp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SelectedAppDAO {

    @Query("SELECT appName FROM selected_app WHERE isSelected = :isSelected")
    fun getAllSelectedAppList(isSelected: Boolean = true): List<String>

    @Query("SELECT appName FROM selected_app")
    fun getAllInstalledAppList(): List<String>

    @Query("SELECT * FROM selected_app WHERE appName = :appName")
    fun getEntity(appName: String): SelectedAppEntity

    @Upsert
    fun upsert(selectedAppEntity: SelectedAppEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun updateInstalled(selectedAppEntity: SelectedAppEntity)

    @Query("SELECT COUNT(*) FROM selected_app WHERE appName = :appName")
    fun isEntityExist(appName: String): Boolean

    @Query("DELETE FROM selected_app")
    fun clearAll()
}