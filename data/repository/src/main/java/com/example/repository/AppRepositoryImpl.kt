package com.example.repository

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import com.example.local.appUsage.AppDAO
import com.example.local.appUsage.AppUsageEntity
import com.example.service.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val appInfo: AppInfo,
    private val appUsageSource: AppDAO,
) : AppRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun updateHourlyTime(
        appName: String,
        startTime: Long,
        date: String,
        dayOfWeek: Int
    ) {
        val time = appInfo.getHourlyTime(appName, startTime)
        val appUsageEntity = AppUsageEntity(
            appName = appName, date = date, dayOfWeek = dayOfWeek,
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
            hour22 = time[22], hour23 = time[23],
            totalHour = time.sum(), isCompleted = true
        )
        scope.launch {
            appUsageSource.upsert(appUsageEntity)
        }
    }

    override suspend fun getHourlyDataByNameDate(appName: String, date: String): AppUsageEntity =
        withContext(Dispatchers.IO) {
            appUsageSource.getByNameDate(appName, date)  // 데이터베이스에서 모든 AppData 레코드를 가져옴
        }

    override suspend fun getInstalledNameList(): List<String> {
        return appInfo.getAppNameList()
    }

    override suspend fun getInstalledAppName(appName: String): String {
        return appInfo.findAppByName(appName)
    }

    override suspend fun getAppIcon(appName: String): ImageBitmap {
        return try {
            appInfo.getAppIcon(appName)
        } catch (e: Exception) {
            ImageBitmap(1, 1, ImageBitmapConfig.Alpha8)
        }
    }

    override suspend fun getDailyUsageByApp(appName: String, date: String): Int {
        return try {
            scope.async { appUsageSource.getTheDayUsage(appName, date) }.await()
        } catch (e: Exception) {
            0
        }
    }

}