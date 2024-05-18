package com.example.repository

import com.example.local.record.RecordDAO
import com.example.local.record.RecordData
import com.example.local.record.RecordEntity
import com.example.local.record.asExternalModel
import com.example.service.DateFactoryForData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val recordDao: RecordDAO,
    private val dateFactory: DateFactoryForData
): GoalRepository{
    private val mutex = Mutex()

    override suspend fun createGoal(appName: String, goalTime: Int) {
        mutex.withLock {
            val today = dateFactory.returnStringDate(dateFactory.returnToday())
            val newRecord = RecordEntity(
                appName = appName,
                date = today,
                goalTime = goalTime,
                howLong = 0
            )
            withContext(Dispatchers.IO){recordDao.upsert(newRecord)}
        }
    }

    override suspend fun deleteGoal(appName: String, date: String) {
        recordDao.delete(appName, date)
    }

    override suspend fun succeedGoal(appName: String, date: String) {
        recordDao.succeedGoal(appName, date)
    }

    override suspend fun failGoal(appName: String, date: String) {
        recordDao.failGoal(appName, date)
    }

    override suspend fun getOnGoingList(): List<RecordData> {
        val list = recordDao.getOnGoingList()
        return list.map { it.asExternalModel() }
    }

    override suspend fun getAllList(): List<RecordData> {
        mutex.withLock {
            val list = recordDao.getAllList()
            return list.map { it.asExternalModel() }
        }
    }

    override suspend fun deleteGoal() {
        recordDao.clearAll()
    }
}