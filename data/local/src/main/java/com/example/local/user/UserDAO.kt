package com.example.local.user

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface UserDAO {
    @Insert
    fun insertUser(userData: UserData)
/*
    @Query("SELECT * FROM app_data WHERE id = :userId")
    fun getAppDataById(userId: String): List<AppData>

 */
}
