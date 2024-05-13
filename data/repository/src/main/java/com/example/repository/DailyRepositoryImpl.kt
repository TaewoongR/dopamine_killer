package com.example.repository

import com.example.local.dailyUsage.DailyDAO
import com.example.local.dailyUsage.DailyEntity
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyRepositoryImpl @Inject constructor(
    private val appInfo: AppFetchingInfo,
    private val selectedAppRepository: SelectedAppRepository,
    private val dailySource: DailyDAO,
    private val dateFactory: DateFactoryForData
) : DailyRepository{

    override suspend fun getDailyUsageFrom(appName: String, dayAgo: Int): Triple<Int, String, Int> {
        val dateString = dateFactory.returnStringDate(dateFactory.returnTheDayStart(dayAgo))
        val entity = dailySource.get(appName, dateString)
        return Triple(entity.dailyUsage, entity.date, entity.dayOfWeek)
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

    override suspend fun initialDailyUpdate() {
        val nameList = selectedAppRepository.getAllInstalled()
        nameList.forEach {appName ->
            for(i in 0..9) {   // 1~9일 전
                val usageNDate = appInfo.getDailyUsage(appName, i)
                dailySource.upsert(
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

    override suspend fun deleteUndetected() {
        dailySource.delete()
    }
}