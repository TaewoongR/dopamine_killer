package com.example.repository

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.local.app.AppDAO
import com.example.local.app.AppData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,   //packageManager를 사용하기 위해 context를 쓰며 활동주기에 영향을 최소로 하기위해 이곳에 선언
    private val localDataSource: AppDAO,
) : AppRepository{
    private val scope = CoroutineScope(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val calendar = Calendar.getInstance().apply {// 당일 최초 시간
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    private val startTime = calendar.timeInMillis // 시작 시간 밀리초 변환,
    private val currentTime = Calendar.getInstance()
    private val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)


    override suspend fun getAppName(): List<String> {
        val packageManager = context.packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        return apps.mapNotNull { app ->
            try {
                packageManager.getApplicationLabel(app).toString()
            } catch (e: Exception) {
                null // 앱 이름을 가져오는데 실패할 경우 null을 반환하여 리스트에 포함시키지 않음
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun updateYesterdayTotalUsage(appName: String): Int{
        // 당일의 각각 모든 앱의 사용량
        val yesterdayStart = calendar
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            startTime + TimeUnit.HOURS.toMillis(24))    // UsageStatsManager의 para는 밀리초 기준
        val appStat = stats.firstOrNull { it.packageName == appName }   // appName의 당일 사용시간
        val dailyHour = appStat?.let {it.totalTimeInForeground} ?: 0     // appName이 없으면 0 반환
        return (dailyHour / 1000).toInt()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override suspend fun updateHourlyTime(appName: String) {
        val appUsageTimeArray = IntArray(24) // 24시간에 대한 사용 시간을 저장할 배열
        var lastEvent: UsageEvents.Event? = null    // 사용 시간이 정각을 포함할 때 다음 for문 루프에게 정각 이전부터 지속적으로 사용한다는 것을 알려줘야하기 때문에 for문 밖에 선언

        for(hour in 0.. currentHour){
            val beginTime = startTime + TimeUnit.HOURS.toMillis(hour.toLong())  // queryEvents - 밀리초 기준
            val endTime = beginTime + TimeUnit.HOURS.toMillis(1) - 1

            val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
            val sessions = mutableListOf<Pair<Long, Long>>()    // 특정 앱 실행 시작과 끝 시간 저장용 페어 리스트

            while (usageEvents.hasNextEvent()) {
                val event = UsageEvents.Event()
                usageEvents.getNextEvent(event)

                Log.d("AppUsage", "Event Type: ${event.eventType}, App Name : ${event.packageName} Time Stamp: ${event.timeStamp}")

                // 특정 앱의 이벤트만 처리
                if (event.packageName == appName) {
                    Log.d("hour", "hour : $hour")
                    if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                        if (sessions.lastOrNull()?.second == 0L) {
                            continue
                        }
                        // 포그라운드 이벤트 시작 시간 기록
                        sessions.add(Pair(event.timeStamp, 0L))
                        lastEvent = event
                    } else {
                        lastEvent?.let {
                            sessions.lastOrNull()?.let { session ->
                                if (session.second == 0L) {
                                    sessions[sessions.lastIndex] = Pair(session.first, event.timeStamp)
                                    lastEvent = null
                                }else if(session == null){  // lastEvent(foreground에서 실행중)가 null이 아닌경우 계속 실행중이지만 새로운 루프이기 때문에 session이 null임
                                    sessions.add(Pair(startTime,event.timeStamp))
                                }
                            }
                        }
                    }
                }
            }
            // 앱 지속 사용 시간이 정시에 걸칠 때
            sessions.lastOrNull()?.let { session ->
                if (session.second == 0L) {
                    sessions[sessions.lastIndex] =
                        if(hour != currentHour){
                            Pair(session.first, endTime)
                        }else{
                            Log.d("","")
                            Pair(session.first, currentTime.timeInMillis)
                        }
                }
            }

            // 시간대 사용 시간 합산
            var totalDuration = 0L
            sessions.forEach { (start, end) ->
                val duration = (end - start) / 1000// 초 단위의 시간 차이
                totalDuration += duration
            }
            appUsageTimeArray[hour] = totalDuration.toInt()
        }

        val appData = AppData(
            appName = appName,
            date = SimpleDateFormat("yyyyMMdd").format(calendar.time),
            hour00 = appUsageTimeArray[0], hour01 = appUsageTimeArray[1],
            hour02 = appUsageTimeArray[2], hour03 = appUsageTimeArray[3],
            hour04 = appUsageTimeArray[4], hour05 = appUsageTimeArray[5],
            hour06 = appUsageTimeArray[6], hour07 = appUsageTimeArray[7],
            hour08 = appUsageTimeArray[8], hour09 = appUsageTimeArray[9],
            hour10 = appUsageTimeArray[10], hour11 = appUsageTimeArray[11],
            hour12 = appUsageTimeArray[12], hour13 = appUsageTimeArray[13],
            hour14 = appUsageTimeArray[14], hour15 = appUsageTimeArray[15],
            hour16 = appUsageTimeArray[16], hour17 = appUsageTimeArray[17],
            hour18 = appUsageTimeArray[18], hour19 = appUsageTimeArray[19],
            hour20 = appUsageTimeArray[20], hour21 = appUsageTimeArray[21],
            hour22 = appUsageTimeArray[22], hour23 = updateYesterdayTotalUsage(appName),
            totalHour = appUsageTimeArray.sum(), isCompleted = true
        )
        scope.launch {
            localDataSource.upsert(appData)
        }
    }

    override suspend fun getHourlyDataByName(appName: String): AppData = withContext(Dispatchers.IO) {
        localDataSource.getByName(appName)  // 데이터베이스에서 모든 AppData 레코드를 가져옴
    }
}