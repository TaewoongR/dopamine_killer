package com.example.recorddomain

interface ReDomain{
    suspend fun getRecordList(): List<RecordDataDomain>
    suspend fun createGoal(goalList: List<GoalDataDomain>)
    suspend fun getInstalledSelected(): List<Pair<String, Boolean>>
    suspend fun updateSelected(selectedList: List<String>)
}