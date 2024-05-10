package com.example.repository

interface GoalRepository {
    fun createGoal(appName: String, goalTime: Int)

    fun deleteGoal(appName: String, date: String)

    fun succeedGoal(appName: String, date: String)

    fun failGoal(appName: String, date: String)

    fun getOnGoingList():List<TempRecordData>
}