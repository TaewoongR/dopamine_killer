package com.example.repository

import com.example.local.record.RecordEntity

interface GoalRepository {
    suspend fun createGoal(appName: String, goalTime: Int)

    suspend fun deleteGoal(appName: String, date: String)

    suspend fun succeedGoal(appName: String, date: String, howLong: Int)

    suspend fun failGoal(appName: String, date: String)

    suspend fun getOnGoingList():List<RecordEntity>

    suspend fun getAllList():List<RecordEntity>

    suspend fun deleteGoal()
    suspend fun saveRecord(recordEntity: RecordEntity)
}