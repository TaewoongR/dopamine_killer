package com.example.repository

import com.example.local.record.RecordData

interface GoalRepository {
    suspend fun createGoal(appName: String, goalTime: Int)

    suspend fun deleteGoal(appName: String, date: String)

    suspend fun succeedGoal(appName: String, date: String)

    suspend fun failGoal(appName: String, date: String)

    suspend fun getOnGoingList():List<RecordData>

    suspend fun getAllList():List<RecordData>
}