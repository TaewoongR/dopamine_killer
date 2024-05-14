package com.example.repository

import com.example.local.dailyUsage.DailyDAO
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyDAO
import com.example.local.horulyUsage.HourlyEntity
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyRepositoryImpl @Inject constructor(
    private val appInfo: AppFetchingInfo,
    private val selectedAppRepository: SelectedAppRepository,
    private val hourlySource: HourlyDAO,
    private val dailySource: DailyDAO,
    private val dateFactory: DateFactoryForData
) : DailyRepository{

    override suspend fun getDailyUsageFrom(appName: String, dayAgo: Int): Triple<Int, String, Int> {
        val dateString = dateFactory.returnStringDate(dateFactory.returnTheDayStart(dayAgo))
        repeat(5) {
            try {
                val entity = dailySource.get(appName, dateString)
                if (entity.appName != "") {
                    return Triple(entity.dailyUsage, entity.date, entity.dayOfWeek)
                }
            } catch (e: NullPointerException) {
                // 로그 출력 등 예외 처리
            }
            delay(1000L) // 재시도 전 대기
        }
        return Triple(0, dateString, -1)
    }

    override suspend fun updateDailyUsageFrom(appName: String, dayAgo: Int) {
        val usageNDate = appInfo.getDailyUsage(appName, dayAgo)
        dailySource.upsert(
            DailyEntity(
                appName = appName,
                date = usageNDate.second,
                dayOfWeek = usageNDate.third,
                dailyUsage = usageNDate.first
            )
        )
    }

    override suspend fun initialDailyUpdate(appNameList: List<String>) {
        appNameList.forEach {appName ->
            for(i in 0..9) {   // 1~9일 전
                val usageNDate = appInfo.getDailyUsage(appName, i)
                withContext(Dispatchers.IO) {dailySource.upsert(
                    DailyEntity(
                        appName = appName,
                        date = usageNDate.second,
                        dayOfWeek = usageNDate.third,
                        dailyUsage = usageNDate.first
                    )
                )
                }
            }
        }
    }

    override suspend fun initialHourlyUpdate(appNameList: List<String>) {
        appNameList.forEach {appName ->
            for(i in 0..9) {   // 1~9일 전
                val usageNDate = appInfo.getHourlyUsage(appName, i)
                hourlySource.upsert(
                    HourlyEntity(
                        appName = appName,
                        date = usageNDate.second,
                        dayOfWeek = usageNDate.third,
                        hour00 = usageNDate.first[0], hour01 = usageNDate.first[1],
                        hour02 = usageNDate.first[2], hour03 = usageNDate.first[3],
                        hour04 = usageNDate.first[4], hour05 = usageNDate.first[5],
                        hour06 = usageNDate.first[6], hour07 = usageNDate.first[7],
                        hour08 = usageNDate.first[8], hour09 = usageNDate.first[9],
                        hour10 = usageNDate.first[10], hour11 = usageNDate.first[11],
                        hour12 = usageNDate.first[12], hour13 = usageNDate.first[13],
                        hour14 = usageNDate.first[14], hour15 = usageNDate.first[15],
                        hour16 = usageNDate.first[16], hour17 = usageNDate.first[17],
                        hour18 = usageNDate.first[18], hour19 = usageNDate.first[19],
                        hour20 = usageNDate.first[20], hour21 = usageNDate.first[21],
                        hour22 = usageNDate.first[22], hour23 =usageNDate.first[23]
                    )
                )
            }
        }
    }

    override suspend fun deleteUndetected() {
        dailySource.delete()
    }
}