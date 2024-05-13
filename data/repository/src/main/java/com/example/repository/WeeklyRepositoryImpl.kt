package com.example.repository


import com.example.local.weeklyUsage.WeeklyDAO
import com.example.local.weeklyUsage.WeeklyEntity
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeeklyRepositoryImpl @Inject constructor(
    private val appInfo: AppFetchingInfo,
    private val dateFactory: DateFactoryForData,
    private val weeklySource: WeeklyDAO,
    private val selectedAppRepository: SelectedAppRepository
) : WeeklyRepository {
    
    override suspend fun getWeeklyUsageFrom(appName: String, monthAgo: Int): Pair<Int, String> {
        val dateString = dateFactory.returnStringDate(dateFactory.returnWeekStartFrom(monthAgo))
        val entity = weeklySource.get(appName, dateString)
        return Pair(entity.weeklyUsage, entity.date )
    }

    override suspend fun updateLastWeeklyUsage(appName: String) {
        val usageNDate = appInfo.getWeeklyAvgUsage(appName, 1)
        weeklySource.upsert(
            WeeklyEntity(
                appName = appName,
                date = usageNDate.second,
                weeklyUsage = usageNDate.first
            )
        )
    }

    override suspend fun initialWeeklyUpdate() {
        val nameList = selectedAppRepository.getAllInstalled()
        nameList.forEach {appName ->
            for(i in 1..3) {   // 1~3주 전
                val usageNDate = appInfo.getWeeklyAvgUsage(appName, i)
                weeklySource.upsert(
                    WeeklyEntity(
                        appName = appName,
                        date = usageNDate.second,
                        weeklyUsage = usageNDate.first
                    )
                )
            }
        }
    }

    override suspend fun deleteUndetected() {
        weeklySource.delete()
    }
}