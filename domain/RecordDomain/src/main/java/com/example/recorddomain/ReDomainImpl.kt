package com.example.recorddomain

import com.example.local.selectedApp.AppNameStorageInterface
import com.example.repository.AppRepository
import com.example.repository.GoalRepository
import javax.inject.Inject

class ReDomainImpl @Inject constructor(
    private val appNameStorage: AppNameStorageInterface,
    private val appRepository: AppRepository,
    private val goalRepository: GoalRepository
):ReDomain {
    override suspend fun getRecordList(): List<RecordDataDomain> {
        val appList = goalRepository.getOnGoingList()
        return appList.map {
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