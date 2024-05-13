package com.example.repository

import com.example.local.record.RecordDAO
import com.example.local.record.RecordData
import com.example.local.record.RecordEntity
import com.example.local.record.asExternalModel
import com.example.service.DateFactoryForData
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val recordDao: RecordDAO,
    private val dateFactory: DateFactoryForData
): GoalRepository{
    override fun createGoal(appName: String, goalTime: Int) {
        val today = dateFactory.returnStringDate(dateFactory.returnToday())
        val newRecord = RecordEntity(
            appName = appName,
            date = today,
            goalTime = goalTime,
            howLong = 0
        )
        recordDao.upsert(newRecord)
    }

    override fun deleteGoal(appName: String, date: String) {
        recordDao.delete(appName, date)
    }

    override fun succeedGoal(appName: String, date: String) {
        recordDao.succeedGoal(appName, date)
    }

    override fun failGoal(appName: String, date: String) {
        recordDao.failGoal(appName, date)
    }

    override fun getOnGoingList(): List<RecordData> {
        val list = recordDao.getOnGoingList()
        return list.map { it.asExternalModel() }
    }

    override fun getAllList(): List<RecordData> {
        val list = recordDao.getAllList()
        return list.map { it.asExternalModel() }
    }
}