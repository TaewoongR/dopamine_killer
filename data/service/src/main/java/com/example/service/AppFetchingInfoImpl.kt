package com.example.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.PowerManager
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppFetchingInfoImpl @Inject constructor(
    @ApplicationContext val context: Context,   //context가 활동주기에 영향을 최소로 하기위해 이곳에 선언
    private val dateFactory: DateFactoryForData
) : AppFetchingInfo{

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
        }?.packageName.toString()   // null일 경우 "null" String반환

    }

    override suspend fun getAppIcon(appName: String): ImageBitmap {
        return try {
            val packageManager = context.packageManager
            val packageName = findAppByName(appName)
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

    /*
    override suspend fun getDailyUsage(appName: String, numberAgo: Int): Triple<Int,String,Int> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val startTime = dateFactory.returnTheDayStart(numberAgo)
        val endTIme = dateFactory.returnTheDayEnd(startTime)
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTIme
        )

        val packageName = findAppByName(appName)
        val appUsage = usageStats.filter { it.packageName == packageName }
        var totalUsageTime = 0L
        appUsage.forEach {
            totalUsageTime += it.totalTimeVisible
        }
        Log.d("daily Hour", "$startTime ~ $endTIme : $totalUsageTime")
        Log.d("time", dateFactory.returnStringDate(startTime))
        return Triple(((totalUsageTime / 1000 / 60).toInt()), dateFactory.returnStringDate(startTime),dateFactory.returnDayOfWeek(startTime) )
    }
     */

    override suspend fun getHourlyUsage(appName: String, numberAgo: Int): Triple<List<Int>, String, Int> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val startTime = dateFactory.returnTheDayStart(numberAgo)
        val endTime = dateFactory.returnTheDayEnd(startTime)
        val packageName = findAppByName(appName)

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)

        val hourlyUsage = MutableList(24) { 0L }

        var lastEventTime = 0L
        var isAppInForeground = false

        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)

            if (event.packageName == packageName) {
                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        if (powerManager.isInteractive) { // Check if screen is on
                            isAppInForeground = true
                            lastEventTime = event.timeStamp
                        }
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED -> {
                        if (isAppInForeground) {
                            val endHour = (event.timeStamp - startTime) / (1000 * 60 * 60)
                            if (endHour < 24) {
                                hourlyUsage[endHour.toInt()] += (event.timeStamp - lastEventTime)
                            }
                            isAppInForeground = false
                        }
                    }
                }
            }
        }

        //
        if (isAppInForeground && powerManager.isInteractive) {
            val endHour = (endTime - startTime) / (1000 * 60 * 60)
            if (endHour < 24) {
                hourlyUsage[endHour.toInt()] += (endTime - lastEventTime)
            }
        }

        Log.d("hourly Usage", "$startTime ~ $endTime : ${hourlyUsage.map { it / 1000 / 60 }}")
        Log.d("time", dateFactory.returnStringDate(startTime))

        val hourlyUsageInMinutes = hourlyUsage.map { (it / 1000 / 60).toInt() }

        return Triple(hourlyUsageInMinutes, dateFactory.returnStringDate(startTime), dateFactory.returnDayOfWeek(startTime))
    }

    override suspend fun getDailyUsage(appName: String, numberAgo: Int): Triple<Int, String, Int> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val startTime = dateFactory.returnTheDayStart(numberAgo)
        val endTime = dateFactory.returnTheDayEnd(startTime)
        val packageName = findAppByName(appName)

        // Query usage events for the given time range
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)

        var totalUsageTime = 0L
        var lastEventTime = 0L
        var isAppInForeground = false

        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)

            if (event.packageName == packageName) {
                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        if (powerManager.isInteractive) { // Check if screen is on
                            isAppInForeground = true
                            lastEventTime = event.timeStamp
                        }
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED -> {
                        if (isAppInForeground) {
                            totalUsageTime += (event.timeStamp - lastEventTime)
                            isAppInForeground = false
                        }
                    }
                }
            }
        }

        if (isAppInForeground && powerManager.isInteractive) {
            totalUsageTime += (endTime - lastEventTime)
        }

        Log.d("daily Hour", "$startTime ~ $endTime : $totalUsageTime")
        Log.d("time", dateFactory.returnStringDate(startTime))

        return Triple((totalUsageTime / 1000 / 60).toInt(), dateFactory.returnStringDate(startTime), dateFactory.returnDayOfWeek(startTime))
    }

    override suspend fun getWeeklyAvgUsage(appName: String, numberAgo: Int): Pair<Int,String> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val startTime = dateFactory.returnWeekStartFrom(numberAgo)
        val endTIme = dateFactory.returnWeekEndFrom(numberAgo)
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_WEEKLY,
            startTime,
            endTIme
        )

        val packageName = findAppByName(appName)
        val appUsage = usageStats.filter { it.packageName == packageName }
        var totalUsageTime = 0L
        appUsage.forEach {
            totalUsageTime += it.totalTimeInForeground
        }
        Log.d("weekly Hour", "$startTime ~ $endTIme : $totalUsageTime")
        return Pair((totalUsageTime / 7 / 1000 / 60).toInt(),dateFactory.returnStringDate(startTime))
    }

    override suspend fun getMonthlyAvgUsage(appName: String, numberAgo: Int): Pair<Int, String> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val monthStartFromTime = dateFactory.returnMonthStartFrom(numberAgo)
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_MONTHLY,
            monthStartFromTime,
            dateFactory.returnMonthEndFrom(numberAgo)
        )

        val packageName = findAppByName(appName)
        val appUsage = usageStats.filter { it.packageName == packageName }
        var totalUsageTime = 0L
        appUsage.forEach {
            totalUsageTime += it.totalTimeInForeground
        }
        val totalDays = dateFactory.returnLastMonthEndDate(dateFactory.returnMonthStartFrom(numberAgo))
        Log.d("month Hour", totalUsageTime.toString())
        return Pair((totalUsageTime / totalDays / 1000 / 60).toInt(),dateFactory.returnStringDate(monthStartFromTime))
    }
}

