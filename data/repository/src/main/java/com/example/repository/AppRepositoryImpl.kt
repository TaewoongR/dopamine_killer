package com.example.repository

import com.example.local.app.AppDAO
import com.example.local.app.AppData
import com.example.service.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val appInfo: AppInfo,
    private val localDataSource: AppDAO
) : AppRepository{
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun updateHourlyTime(appName: String, startTime: Long, endTime: Long, date: String) {
        val time = appInfo.getHourlyTime(appName, startTime, endTime)
        val appData = AppData(
            appName = appName, date = date,
            hour00 = time[0], hour01 = time[1],
            hour02 = time[2], hour03 = time[3],
            hour04 = time[4], hour05 = time[5],
            hour06 = time[6], hour07 = time[7],
            hour08 = time[8], hour09 = time[9],
            hour10 = time[10], hour11 = time[11],
            hour12 = time[12], hour13 = time[13],
            hour14 = time[14], hour15 = time[15],
            hour16 = time[16], hour17 = time[17],
            hour18 = time[18], hour19 = time[19],
            hour20 = time[20], hour21 = time[21],
            hour22 = time[22], hour23 = time[22],
            totalHour = time.sum(), isCompleted = true
        )
        withContext(Dispatchers.IO) {
            localDataSource.upsert(appData)
        }
    }

    override suspend fun getHourlyDataByNameDate(appName: String, date: String): AppData = withContext(Dispatchers.IO) {
        localDataSource.getByNameDate(appName, date)  // 데이터베이스에서 모든 AppData 레코드를 가져옴
    }

    override suspend fun getInstalledNameList(): List<String>{
        return appInfo.getAppNameList()
    }

    override suspend fun findInstalledApp(appName: String): String{
        return appInfo.findAppByName(appName)
    }
}