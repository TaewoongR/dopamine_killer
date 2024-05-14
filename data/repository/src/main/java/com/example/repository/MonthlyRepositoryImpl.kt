package com.example.repository

import com.example.local.monthlyUsage.MonthlyDAO
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonthlyRepositoryImpl @Inject constructor(
    private val appInfo: AppFetchingInfo,
    private val monthlySource: MonthlyDAO,
    private val dateFactory: DateFactoryForData,
    private val selectedAppRepository: SelectedAppRepository
) : MonthlyRepository{

    override suspend fun getMonthlyUsageFrom(appName: String, monthAgo: Int): Pair<Int, String> {
        val dateString = dateFactory.returnStringDate(dateFactory.returnMonthStartFrom(monthAgo)).substring(0,6)
        val entity = monthlySource.get(appName, dateString)
        return Pair(entity.monthlyUsage, entity.date )
    }

    override suspend fun updateLastMonthlyUsage(appName: String) {
        val usageNDate = appInfo.getMonthlyAvgUsage(appName, 1)
        monthlySource.upsert(
            MonthlyEntity(
                appName = appName,
                date = usageNDate.second.substring(0,6),
                monthlyUsage = usageNDate.first
            )
        )
    }

    override suspend fun initialMonthlyUpdate(appNameList: List<String>) {
        appNameList.forEach {appName ->
            for(i in 1..2) {   // 1~3달 전
                val usageNDate = appInfo.getMonthlyAvgUsage(appName, i)
                monthlySource.upsert(
                    MonthlyEntity(
                        appName = appName,
                        date = usageNDate.second.substring(0,6),
                        monthlyUsage = usageNDate.first
                    )
                )
            }
        }
    }

    override suspend fun deleteUndetected() {
        monthlySource.delete()
    }
}