package com.example.recorddomain

interface ReDomain{
    suspend fun getRecordList(): List<RecordDataDomain>
    suspend fun createGoal(goalList: List<GoalDataDomain>)
    suspend fun deleteGoal(goal: Pair<String,String>, token: String, username: String)
    suspend fun getInstalledSelected(): List<Pair<String, Boolean>>
    suspend fun updateSelected(selectedList: List<String>)
}