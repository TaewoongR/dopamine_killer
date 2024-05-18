package com.example.local.user

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDAO {
    @Upsert
    fun upsert(user: UserEntity)

    @Query("DELETE FROM user_info")
    fun clearAll()
}