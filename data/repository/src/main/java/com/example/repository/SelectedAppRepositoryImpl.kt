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
        return withContext(Dispatchers.IO){selectedAppDAO.getAllInstalledAppList()}
    }

    override suspend fun getAllSelected(): List<String> {
        return withContext(Dispatchers.IO){selectedAppDAO.getAllSelectedAppList()}
    }

    override suspend fun updateSelected(appList: List<String>) {   // abstract 함수에서 Boolean은 이미 선언됨
        val list = withContext(Dispatchers.IO){selectedAppDAO.getAllInstalledAppList()}
        withContext(Dispatchers.IO){selectedAppDAO.clearAll()}
        list.forEach {
            withContext(Dispatchers.IO){
                selectedAppDAO.upsert(
                    SelectedAppEntity(
                        appName = it,
                        packageName = appFetchingInfo.getPackageNameBy(it),
                        isSelected = false
                    )
                )
            }
        }
        for (name in appList) {
            withContext(Dispatchers.IO){
                selectedAppDAO.upsert(
                    SelectedAppEntity(
                        appName = name,
                        packageName = appFetchingInfo.getPackageNameBy(name),
                        isSelected = true
                    )
                )
            }
        }
    }

    override suspend fun updatedInstalled(appNameList: List<String>, isInitial: Boolean) {
        if(isInitial) {
            appNameList.forEach {
                val packageName = appFetchingInfo.getPackageNameBy(it)
                selectedAppDAO.upsert(
                    SelectedAppEntity(
                        appName = it,
                        packageName = packageName,
                        isSelected = false
                    )
                )
            }
        }else{
            val selected = try{
                selectedAppDAO.getAllSelectedAppList()
            }catch(e: NullPointerException){
                listOf()
            }
            selectedAppDAO.clearAll()
            appNameList.forEach {
                val packageName = appFetchingInfo.getPackageNameBy(it)
                selectedAppDAO.upsert(
                    SelectedAppEntity(
                        appName = it,
                        packageName = packageName,
                        isSelected = false
                    )
                )
            }
            if(selected.isNotEmpty()) {
                selected.forEach {
                    val packageName = appFetchingInfo.getPackageNameBy(it)
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
    }

    override suspend fun deleteSelected() {
        selectedAppDAO.clearAll()
    }
}