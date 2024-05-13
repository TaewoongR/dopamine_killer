package com.example.repository

import com.example.local.record.RecordData

interface GoalRepository {
    fun createGoal(appName: String, goalTime: Int)

    fun deleteGoal(appName: String, date: String)

    fun succeedGoal(appName: String, date: String)

    fun failGoal(appName: String, date: String)

    fun getOnGoingList():List<RecordData>

    fun getAllList():List<RecordData>
}