package com.example.recorddomain

interface ReDomain{
    suspend fun getRecordList(): List<RecordDataDomain>
    suspend fun createGoal(goalList: List<GoalDataDomain>)
}