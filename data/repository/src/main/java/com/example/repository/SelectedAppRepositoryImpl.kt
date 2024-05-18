package com.example.repository

import com.example.local.selectedApp.SelectedAppDAO
import com.example.local.selectedApp.SelectedAppEntity
import com.example.service.AppFetchingInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedAppRepositoryImpl @Inject constructor(
    private val selectedAppDAO: SelectedAppDAO,
    private val appFetchingInfo: AppFetchingInfo,
) : SelectedAppRepository{

    override suspend fun getAllInstalled(): List<String> {
        return selectedAppDAO.getAllInstalledAppList()
    }

    override suspend fun getAllSelected(): List<String> {
        return selectedAppDAO.getAllSelectedAppList()
    }

    override suspend fun updateSelected(appList: List<String>) {   // abstract 함수에서 Boolean은 이미 선언됨
        for (name in appList) {
            withContext(Dispatchers.IO){
                val getInstalledEntity = selectedAppDAO.getEntity(name)
                selectedAppDAO.upsert(
                    getInstalledEntity.copy(
                        isSelected = true
                    )
                )
            }
        }
    }

    override suspend fun updatedInstalled(appNameList: List<String>, isInitial: Boolean) {
        appNameList.forEach {
            val packageName = appFetchingInfo.getPackageNameBy(it)
            if(isInitial || !selectedAppDAO.isEntityExist(it)) {
                selectedAppDAO.upsert(
                    SelectedAppEntity(
                        appName = it,
                        packageName = packageName,
                        isSelected = false
                    )
                )
            }
        }
    }

    override suspend fun deleteSelected() {
        selectedAppDAO.clearAll()
    }
}