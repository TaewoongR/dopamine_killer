package com.example.recorddomain

import com.example.local.record.RecordDAO
import com.example.local.record.RecordEntity
import com.example.repository.GoalRepository
import com.example.repository.HourlyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReDomainImpl @Inject constructor(
    private val recordDAO: RecordDAO,
    private val appRepository: HourlyRepository,
    private val goalRepository: GoalRepository
):ReDomain {
    override suspend fun getRecordList(): List<RecordDataDomain> {
        return withContext(Dispatchers.IO) {
            val appList = goalRepository.getAllList()
            return@withContext appList.map {
                RecordDataDomain(
                    appName = it.appName,
                    appIcon = appRepository.getAppIcon(it.appName),
                    date = it.date,
                    goalTime = it.goalTime,
                    howLong = it.howLong,
                    onGoing = it.onGoing
                )
            }
        }
    }


    override suspend fun createGoal(goalList: List<GoalDataDomain>) {
        withContext(Dispatchers.IO) {
            goalList.forEach { data ->
                recordDAO.upsert(
                    RecordEntity(
                        appName = data.appName,
                        date = data.date,
                        goalTime = data.goalTime
                    )
                )
            }
        }
    }
}