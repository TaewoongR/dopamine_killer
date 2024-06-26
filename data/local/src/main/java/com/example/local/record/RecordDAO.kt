package com.example.local.record

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RecordDAO {
    @Upsert
    fun upsert(recordEntity: RecordEntity)

    @Query("DELETE FROM record_data WHERE appName = :appName AND date = :date")
    fun delete(appName: String, date: String)

    @Query("UPDATE record_data SET howLong = :howLong  WHERE appName = :appName AND date = :date AND onGoing = :onGoing")
    fun succeedGoal(appName: String, date: String, howLong: Int, onGoing: Boolean = true)

    @Query("UPDATE record_data SET onGoing = :onGoing  WHERE appName = :appName AND date = :date AND onGoing != :onGoing")
    fun failGoal(appName: String, date: String, onGoing: Boolean = false)

    @Query("SELECT * FROM record_data WHERE onGoing = :onGoing")
    fun getOnGoingList(onGoing: Boolean = true): List<RecordEntity>

    @Query("SELECT * FROM record_data")
    fun getAllList(): List<RecordEntity>

    @Query("DELETE FROM record_data")
    fun clearAll()

}