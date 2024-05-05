package com.example.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
                label.contains(appName, ignoreCase = true)
            } catch (e: Exception) {
                Log.d("error","not found")
                false
            }
        }?.packageName.toString()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override suspend fun getHourlyTime(appName: String, startTime: Long): IntArray {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val appUsageTimeArray = IntArray(24) // 24시간에 대한 사용 시간을 저장할 배열
        var lastEvent: UsageEvents.Event? = null    // 사용 시간이 정각을 포함할 때 다음 for문 루프에게 정각 이전부터 지속적으로 사용한다는 것을 알려줘야하기 때문에 for문 밖에 선언

        // 전날 마지막 시간대에서 다음날로 넘어가는 시간 사이에 연속으로 앱을 사용하는지 확인
        val lastHourUsage = usageStatsManager.queryEvents(startTime + TimeUnit.HOURS.toMillis(-1), startTime)
        while(lastHourUsage.hasNextEvent()){
            val event = UsageEvents.Event()
            lastHourUsage.getNextEvent(event)
            if(event.packageName == appName && (
                        event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                        event.eventType == UsageEvents.Event.ACTIVITY_PAUSED ||
                        event.eventType == UsageEvents.Event.FOREGROUND_SERVICE_START
                    )
            ){
                lastEvent = event
            }else if(event.eventType == UsageEvents.Event.ACTIVITY_STOPPED){
                lastEvent = null
            }
        }

        for (hour in 0..23) {
            val beginTime = startTime + TimeUnit.HOURS.toMillis(hour.toLong())  // queryEvents - 밀리초 기준
            val endTime = beginTime + TimeUnit.HOURS.toMillis(1) - 1
            val usageEvents = usageStatsManager.queryEvents(beginTime, endTime) ?: continue // 관측하지 못하면 즉시 다음 루프로
            val sessions = mutableListOf<Pair<Long, Long>>()    // 특정 앱 실행 시작과 끝 시간 저장용 페어 리스트
            Log.d("time","begin Time : $beginTime, end Time : $endTime")
            while (usageEvents.hasNextEvent()) {
                val event = UsageEvents.Event()
                usageEvents.getNextEvent(event)

                //Log.d("AppUsage", "Event Type: ${event.eventType}, App Name : ${event.packageName} Time Stamp: ${event.timeStamp}")

                // 특정 앱의 이벤트만 처리
                if (event.packageName == appName) {
                    Log.d("AppUsage", "Event Type: ${event.eventType}, App Name : ${event.packageName} Time Stamp: ${event.timeStamp}")
                    if(
                        (
                            event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                            event.eventType == UsageEvents.Event.ACTIVITY_PAUSED ||
                            event.eventType == UsageEvents.Event.FOREGROUND_SERVICE_START
                        )
                        && lastEvent == null
                    ){
                        // 포그라운드 이벤트 시작 시간 기록
                        sessions.add(Pair(event.timeStamp, 0L))
                        Log.d("measure start", "start : $hour")
                        lastEvent = event
                    }else if((event.eventType == UsageEvents.Event.ACTIVITY_STOPPED) && lastEvent != null){
                        sessions.lastOrNull()?.let { session ->
                            if(session.second == 0L){
                                sessions[sessions.lastIndex] =
                                    Pair(session.first, event.timeStamp)
                                Log.d("measure end", "end : $hour, calculated time : $")
                                lastEvent = null
                            }
                        }
                        if(sessions.lastOrNull() == null){
                            // 정각을 포함해 연속적 이용시 새로운 다음 시간대의 사용량 계산
                            sessions.add(Pair(beginTime, event.timeStamp))
                            Log.d("measure end", "special end : $hour, calculated time : $")
                            lastEvent = null
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

    override suspend fun getAppIcon(packageName: String): ImageBitmap {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            Log.d("appInfoImpl", packageName)
            val drawable = packageManager.getApplicationIcon(appInfo)
            drawableToImageBitmap(drawable)
        } catch (e: Exception) {
            val bitmap = Bitmap.createBitmap(3, 3, Bitmap.Config.ARGB_8888)
            Log.d("appInfoImpl","Exception: ${e::class.java.simpleName}, Message: ${e.message}")
            return bitmap.asImageBitmap()
        }
    }

    private fun drawableToImageBitmap(drawable: Drawable): ImageBitmap {
        return if (drawable is BitmapDrawable) {
            // BitmapDrawable의 경우, 쉽게 Bitmap으로 변환 가능
            drawable.bitmap.asImageBitmap()
        } else {
            // 그 외의 Drawable 타입을 처리
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth.takeIf { it > 0 } ?: 1,
                drawable.intrinsicHeight.takeIf { it > 0 } ?: 1,
                Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap.asImageBitmap()
        }
    }
}

