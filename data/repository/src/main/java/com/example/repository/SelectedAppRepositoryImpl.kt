package com.example.repository

import com.example.local.selectedApp.SelectedAppDAO
import com.example.local.selectedApp.SelectedAppEntity
import com.example.service.AppFetchingInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedAppRepositoryImpl @Inject constructor(
    private val selectedAppDAO: SelectedAppDAO,
    private val appFetchingInfo: AppFetchingInfo,
) : SelectedAppRepository{
    private val mutex = Mutex()

    override suspend fun getAllInstalled(): List<String> {
        return selectedAppDAO.getAllInstalled()
    }

    override suspend fun getAllSelected(): List<String> {
        mutex.withLock {
            return selectedAppDAO.getAllSelected()
        }
    }

    override suspend fun updateSelected(appList: List<String>, isSelected: Boolean) {   // abstract 함수에서 Boolean은 이미 선언됨
        mutex.withLock {
            for (name in appList) {
                withContext(Dispatchers.IO){
                    selectedAppDAO.upsert(
                        SelectedAppEntity(
                            appName = name,
                            packageName = appFetchingInfo.findAppByName(name),
                            isSelected = isSelected
                        )
                    )
                }
            }
        }
    }
}