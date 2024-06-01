package com.example.repository

import com.example.local.dailyUsage.DailyDAO
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyDAO
import com.example.local.horulyUsage.HourlyEntity
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import kotlinx.coroutines.Dispatchers
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
        try {
            val entity = dailySource.get(appName, dateString)
            if (entity.appName != "") {
                return Triple(entity.dailyUsage, entity.date, entity.dayOfWeek)
            }
        } catch (e: NullPointerException) {

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

    override suspend fun initialHourlyDailyUpdate(appNameList: List<String>) {
        appNameList.forEach {appName ->
            for(i in 0..9) {   // 1~9일 전
                val usageNDate = appInfo.getHourlyUsage(appName, i)
                withContext(Dispatchers.IO) {
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
                            hour22 = usageNDate.first[22], hour23 = usageNDate.first[23]
                        )
                    )
                    dailySource.upsert(
                        DailyEntity(
                            appName = appName,
                            date = usageNDate.second,
                            dayOfWeek = usageNDate.third,
                            dailyUsage = usageNDate.first.sum()
                        )
                    )
                }
            }
        }
    }

    override suspend fun periodicHourlyDailyUpdate() {
        val appNameList = selectedAppRepository.getAllInstalled()
        appNameList.forEach {appName ->
            for(i in 0..1) {   // 1~9일 전
                val usageNDate = appInfo.getHourlyUsage(appName, i)
                withContext(Dispatchers.IO) {
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
                            hour22 = usageNDate.first[22], hour23 = usageNDate.first[23]
                        )
                    )
                    dailySource.upsert(
                        DailyEntity(
                            appName = appName,
                            date = usageNDate.second,
                            dayOfWeek = usageNDate.third,
                            dailyUsage = usageNDate.first.sum()
                        )
                    )
                }
            }
        }
        /*
        appNameList.forEach {app ->
            val usageNDate = withContext(Dispatchers.IO) {appInfo.getHourlyUsage(app,0, false)}
            val hourlyUsage = usageNDate.first
            val date = usageNDate.second

            withContext(Dispatchers.IO) {
                // 기존의 HourlyEntity를 데이터베이스에서 가져오기
                val existingHourlyEntity = hourlySource.getHourlyEntity(app, date)
                // 새로운 HourlyEntity를 생성하고 현재 시간대 필드 업데이트
                val updatedHourlyEntity = when (currentHour) {
                    0 -> existingHourlyEntity.copy(
                        hour23 = hourlyUsage[dateFactory.returnTheHour(dateFactory.returnRightBeforeFixedTime())]
                    )
                    1 -> existingHourlyEntity.copy(
                        hour00 = hourlyUsage[currentHour-1],
                        hour01 = hourlyUsage[currentHour]
                    )
                    2 -> existingHourlyEntity.copy(
                        hour01 = hourlyUsage[currentHour-1],
                        hour02 = hourlyUsage[currentHour]
                    )
                    3 -> existingHourlyEntity.copy(
                        hour02 = hourlyUsage[currentHour-1],
                        hour03 = hourlyUsage[currentHour]
                    )
                    4 -> existingHourlyEntity.copy(
                        hour03 = hourlyUsage[currentHour-1],
                        hour04 = hourlyUsage[currentHour]
                    )
                    5 -> existingHourlyEntity.copy(
                        hour04 = hourlyUsage[currentHour-1],
                        hour05 = hourlyUsage[currentHour]
                    )
                    6 -> existingHourlyEntity.copy(
                        hour05 = hourlyUsage[currentHour-1],
                        hour06 = hourlyUsage[currentHour]
                    )
                    7 -> existingHourlyEntity.copy(
                        hour06 = hourlyUsage[currentHour-1],
                        hour07 = hourlyUsage[currentHour]
                    )
                    8 -> existingHourlyEntity.copy(
                        hour07 = hourlyUsage[currentHour-1],
                        hour08 = hourlyUsage[currentHour]
                    )
                    9 -> existingHourlyEntity.copy(
                        hour08 = hourlyUsage[currentHour-1],
                        hour09 = hourlyUsage[currentHour]
                    )
                    10 -> existingHourlyEntity.copy(
                        hour09 = hourlyUsage[currentHour-1],
                        hour10 = hourlyUsage[currentHour]
                    )
                    11 -> existingHourlyEntity.copy(
                        hour10 = hourlyUsage[currentHour-1],
                        hour11 = hourlyUsage[currentHour]
                    )
                    12 -> existingHourlyEntity.copy(
                        hour11 = hourlyUsage[currentHour-1],
                        hour12 = hourlyUsage[currentHour]
                    )
                    13 -> existingHourlyEntity.copy(
                        hour12 = hourlyUsage[currentHour-1],
                        hour13 = hourlyUsage[currentHour]
                    )
                    14 -> existingHourlyEntity.copy(
                        hour13 = hourlyUsage[currentHour-1],
                        hour14 = hourlyUsage[currentHour]
                    )
                    15 -> existingHourlyEntity.copy(
                        hour14 = hourlyUsage[currentHour-1],
                        hour15 = hourlyUsage[currentHour]
                    )
                    16 -> existingHourlyEntity.copy(
                        hour15 = hourlyUsage[currentHour-1],
                        hour16 = hourlyUsage[currentHour]
                    )
                    17 -> existingHourlyEntity.copy(
                        hour16 = hourlyUsage[currentHour-1],
                        hour17 = hourlyUsage[currentHour]
                    )
                    18 -> existingHourlyEntity.copy(
                        hour17 = hourlyUsage[currentHour-1],
                        hour18 = hourlyUsage[currentHour]
                    )
                    19 -> existingHourlyEntity.copy(
                        hour18 = hourlyUsage[currentHour-1],
                        hour19 = hourlyUsage[currentHour]
                    )
                    20 -> existingHourlyEntity.copy(
                        hour19 = hourlyUsage[currentHour-1],
                        hour20 = hourlyUsage[currentHour]
                    )
                    21 -> existingHourlyEntity.copy(
                        hour20 = hourlyUsage[currentHour-1],
                        hour21 = hourlyUsage[currentHour]
                    )
                    22 -> existingHourlyEntity.copy(
                        hour21 = hourlyUsage[currentHour-1],
                        hour22 = hourlyUsage[currentHour]
                    )
                    23 -> existingHourlyEntity.copy(
                        hour22 = hourlyUsage[currentHour-1],
                        hour23 = hourlyUsage[currentHour]
                        )
                    else -> existingHourlyEntity // 기본적으로 변경이 없는 경우
                }
                hourlySource.upsert(updatedHourlyEntity)
            }
            if(currentHour != 0) {
                var totalHour = 0
                hourlySource.getHourlyEntity(app, date).apply {
                    totalHour += this.hour00
                    totalHour += this.hour01
                    totalHour += this.hour02
                    totalHour += this.hour03
                    totalHour += this.hour04
                    totalHour += this.hour05
                    totalHour += this.hour06
                    totalHour += this.hour07
                    totalHour += this.hour08
                    totalHour += this.hour09
                    totalHour += this.hour10
                    totalHour += this.hour11
                    totalHour += this.hour12
                    totalHour += this.hour13
                    totalHour += this.hour14
                    totalHour += this.hour15
                    totalHour += this.hour16
                    totalHour += this.hour17
                    totalHour += this.hour18
                    totalHour += this.hour19
                    totalHour += this.hour20
                    totalHour += this.hour21
                    totalHour += this.hour22
                    totalHour += this.hour23
                }
                dailySource.upsert(
                    DailyEntity(
                        appName = app,
                        date = usageNDate.second,
                        dayOfWeek = usageNDate.third,
                        dailyUsage = totalHour
                    )
                )
            }else{
                withContext(Dispatchers.IO) {
                    val newToday = dateFactory.returnStringDate(dateFactory.returnToday())
                    val newDayOfWeek = dateFactory.returnDayOfWeek(dateFactory.returnToday())
                    hourlySource.upsert(
                        HourlyEntity(
                            appName = app,
                            date = newToday,
                            dayOfWeek = newDayOfWeek,
                            hour00 = hourlyUsage[currentHour]
                        )
                    )
                    dailySource.upsert(
                        DailyEntity(
                            appName = app,
                            date = newToday,
                            dayOfWeek = newDayOfWeek,
                            dailyUsage = hourlyUsage[currentHour]
                        )
                    )
                }
            }
        }

         */
    }


    override suspend fun deleteOnDate(date:String) {
        dailySource.deleteOnDate(date)
    }

    override suspend fun deleteHourlyDaily() {
        dailySource.clearAll()
        hourlySource.clearAll()
    }
}