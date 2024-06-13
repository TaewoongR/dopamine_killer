package com.example.repository

import android.content.Context
import com.example.local.dailyUsage.DailyDAO
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyDAO
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyDAO
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.record.RecordDAO
import com.example.local.record.RecordEntity
import com.example.local.selectedApp.SelectedAppDAO
import com.example.local.selectedApp.SelectedAppEntity
import com.example.local.weeklyUsage.WeeklyDAO
import com.example.local.weeklyUsage.WeeklyEntity
import com.example.network.appUsage.NetworkDataSource
import com.example.service.DateFactoryForData
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val hourlyDao: HourlyDAO,
    private val dailyDao: DailyDAO,
    private val weeklyDao: WeeklyDAO,
    private val monthlyDAO: MonthlyDAO,
    private val recordDao: RecordDAO,
    private val selectedDao: SelectedAppDAO,
    private val networkService: NetworkDataSource,
    private val dateFactory: DateFactoryForData,
): NetworkRepository {

    override suspend fun postHourly(context: Context) {
        val listAllHourly = hourlyDao.getAll()
        // 서버에 데이터 PUT
        listAllHourly.forEach {
            networkService.postHourlyData(it, context)
        }
    }

    override suspend fun postDaily(context: Context) {
        val listAllDaily = dailyDao.getAll()
        // 서버에 데이터 PUT
        listAllDaily.forEach {
            networkService.postDailyData(it, context)
        }
    }

    override suspend fun postWeekly(context: Context) {
        val listAllWeekly = weeklyDao.getAll()
        // 서버에 데이터 PUT
        listAllWeekly.forEach {
            networkService.postWeeklyData(it, context)
        }
    }

    override suspend fun postMonthly(context: Context) {
        val listAllDaily = monthlyDAO.getAll()
        // 서버에 데이터 PUT
        listAllDaily.forEach {
            networkService.postMonthlyData(it, context)
        }
    }

    override suspend fun postGoal(context: Context){
        val listRecord = recordDao.getAllList()
        listRecord.forEach {
            networkService.postRecordData(it, context)
        }
    }

    override suspend fun postSelected(context: Context){
        val listSelected = selectedDao.getAll()
        listSelected.forEach {
            networkService.postSelectedData(it, context)
        }
    }

    override suspend fun getHourly(token: String, username: String):List<HourlyEntity> {
        return networkService.getHourly(token, username)
    }

    override suspend fun getDaily(token: String,username: String): List<DailyEntity> {
        return networkService.getDaily(token,username)
    }

    override suspend fun getWeekly(token: String,username: String): List<WeeklyEntity> {
        return networkService.getWeekly(token,username)
    }

    override suspend fun getMonthly(token: String,username: String): List<MonthlyEntity> {
        return networkService.getMonthly(token,username)
    }

    override suspend fun getRecord(token: String,username: String): List<RecordEntity> {
        return networkService.getRecord(token,username)
    }

    override suspend fun getSelected(token: String,username: String): List<SelectedAppEntity> {
        return networkService.getSelected(token,username)
    }

    override suspend fun deleteRecord(token: String, username: String) {
        networkService.deleteGoalByUserName(token, username)
    }
}