package com.example.repository

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import com.example.local.horulyUsage.HourlyDAO
import com.example.local.horulyUsage.HourlyEntity
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HourlyRepositoryImpl @Inject constructor(
    private val appInfo: AppFetchingInfo,
    private val appUsageSource: HourlyDAO,
    private val dateFactory: DateFactoryForData
) : HourlyRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun updateHourlyTime(
        appName: String,
        packageName: String,
        dayFrom: Int,
    ) {
        val startTime = dateFactory.returnTheDayStart(dayFrom)
        val time = appInfo.getHourlyTime(packageName, startTime)
        val date = dateFactory.returnStringDate(startTime)
        val dayOfWeek = dateFactory.returnDayOfWeek(startTime)
        val appUsageEntity = HourlyEntity(
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

    override suspend fun getHourlyDataByNameDate(appName: String, date: String): HourlyEntity =
        withContext(Dispatchers.IO) {
            appUsageSource.getByNameDate(appName, date)  // 데이터베이스에서 모든 AppData 레코드를 가져옴
        }

    override suspend fun getInstalledAppName(appName: String): String {
        return appInfo.findAppByName(appName)
    }

    override suspend fun getAppIcon(appName: String): ImageBitmap {
        val packageName = appInfo.findAppByName(appName)
        return try {
            appInfo.getAppIcon(packageName)
        } catch (e: Exception) {
            ImageBitmap(1, 1, ImageBitmapConfig.Alpha8)
        }
    }

    override suspend fun getTodayUsageByApp(appName: String): Int {
        val today = dateFactory.returnStringDate(dateFactory.returnToday())
        return try {
            scope.async { appUsageSource.getTheDayUsage(appName, today) }.await()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getYesterdayUsageByApp(appName: String): Int {
        val today = dateFactory.returnStringDate(dateFactory.returnTheDayStart(1))
        return try {
            scope.async { appUsageSource.getTheDayUsage(appName, today) }.await()
        } catch (e: Exception) {
            0
        }
    }
}