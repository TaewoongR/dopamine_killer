package com.example.local.selectedApp

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SelectedAppDAO {

    @Query("SELECT appName FROM selected_app WHERE isSelected = :isSelected")
    fun getAllSelected(isSelected: Boolean = true): List<String>

    @Query("SELECT appName FROM selected_app")
    fun getAllInstalled(): List<String>

    @Upsert
    fun upsert(selectedAppEntity: SelectedAppEntity)
}