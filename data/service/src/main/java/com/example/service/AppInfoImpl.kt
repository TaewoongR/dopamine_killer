package com.example.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInfoImpl @Inject constructor(
    @ApplicationContext val context: Context,   //context가 활동주기에 영향을 최소로 하기위해 이곳에 선언
) : AppInfo{

    override suspend fun getAppNameList(): List<String> {
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

    override suspend fun findAppByName(appName: String): String {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        // 패키지 목록을 필터링
        return packages.firstOrNull { pkg ->
            try {
                val label = packageManager.getApplicationLabel(pkg.applicationInfo).toString()
                Log.d("error","not found")
                label.contains(appName, ignoreCase = true)
            } catch (e: Exception) {
                false
            }
        }?.packageName.toString()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override suspend fun getHourlyTime(appName: String, startTime: Long, toTime: Long): IntArray {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val appUsageTimeArray = IntArray(24) // 24시간에 대한 사용 시간을 저장할 배열
        var lastEvent: UsageEvents.Event? = null    // 사용 시간이 정각을 포함할 때 다음 for문 루프에게 정각 이전부터 지속적으로 사용한다는 것을 알려줘야하기 때문에 for문 밖에 선언

        for (hour in 0..23) {
            val beginTime = startTime + TimeUnit.HOURS.toMillis(hour.toLong())  // queryEvents - 밀리초 기준
            val endTime = beginTime + TimeUnit.HOURS.toMillis(1) - 1
            val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
            val sessions = mutableListOf<Pair<Long, Long>>()    // 특정 앱 실행 시작과 끝 시간 저장용 페어 리스트
            Log.d("time","begin Time : $beginTime, end Time : $endTime")
            while (usageEvents.hasNextEvent()) {
                val event = UsageEvents.Event()
                usageEvents.getNextEvent(event)

                Log.d(
                    "AppUsage",
                    "Event Type: ${event.eventType}, App Name : ${event.packageName} Time Stamp: ${event.timeStamp}"
                )

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
                                    sessions[sessions.lastIndex] =
                                        Pair(session.first, event.timeStamp)
                                    lastEvent = null
                                } else if (session.second != 0L) {
                                    // 정각을 포함해 연속적 이용시 새로운 다음 시간대의 사용량 계산
                                    sessions[sessions.lastIndex] = Pair(startTime, event.timeStamp)
                                    lastEvent = null
                                }
                            }
                        }
                    }
                }
            }
            // 정각을 포함해 연속적 이용시 직전 시간대의 마지막 사용량
            if (lastEvent != null) {
                sessions.lastOrNull()?.let {
                    sessions[sessions.lastIndex] = Pair(it.first, endTime)
                }
            }

            // 시간대 사용 시간 합산
            var totalDuration = 0L
            sessions.forEach { (start, end) ->
                val duration = (end - start) / 1000     // 초 단위의 시간 차이
                totalDuration += duration
            }
            appUsageTimeArray[hour] = totalDuration.toInt()
        }

        return appUsageTimeArray

    }
}

