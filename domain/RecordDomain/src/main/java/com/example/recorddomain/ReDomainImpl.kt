package com.example.recorddomain

import com.example.local.record.RecordDAO
import com.example.local.record.RecordEntity
import com.example.repository.GoalRepository
import com.example.repository.SelectedAppRepository
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReDomainImpl @Inject constructor(
    private val recordDAO: RecordDAO,
    private val appRepository: AppFetchingInfo,
    private val goalRepository: GoalRepository,
    private val selectedAppRepository: SelectedAppRepository,
    private val dateFactoryForData: DateFactoryForData
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

    override suspend fun deleteGoal(goal: Pair<String, String>) {
        withContext(Dispatchers.IO) {
            recordDAO.delete(goal.first, goal.second)   //
        }
    }

    override suspend fun getInstalledSelected(): List<Pair<String, Boolean>> {
        val installedApps = selectedAppRepository.getAllInstalled()
        // 선택된 앱 목록을 가져옴
        val selectedApps = selectedAppRepository.getAllSelected().toSet()

        // 설치된 앱 목록을 순회하며 선택 여부를 확인하고 Pair로 변환
        val result = installedApps.map { app ->
            app to (app in selectedApps)
        }

        return result
    }

    override suspend fun updateSelected(selectedList: List<String>) {
        selectedAppRepository.updateSelected(selectedList)
    }
}