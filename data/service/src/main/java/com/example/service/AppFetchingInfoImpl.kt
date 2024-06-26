package com.example.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import android.os.PowerManager
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import com.example.local.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppFetchingInfoImpl @Inject constructor(
    @ApplicationContext val context: Context,   //context가 활동주기에 영향을 최소로 하기위해 이곳에 선언
    private val dateFactory: DateFactoryForData
) : AppFetchingInfo{

    override suspend fun getAppNameList(){
        val packageManager = context.packageManager
        val apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        apps.mapNotNull { app ->
            try {
                val appName = packageManager.getApplicationLabel(app.applicationInfo).toString()
                val packageName = app.packageName
                Log.d("App Info", "App Name: $appName, Package Name: $packageName")
                appName
            } catch (e: Exception) {
                null // 앱 이름을 가져오는데 실패할 경우 null을 반환하여 리스트에 포함시키지 않음
            }
        }
    }

    override suspend fun getPackageNameBy(appName: String):String {
        val fields = R.string::class.java.fields // R.string 클래스의 모든 필드를 가져옴
        val prefix = "app_" // 필터링에 사용할 접두사
        var packageName = ""
        for (field in fields) {
            // 특정 접두사로 시작하는 문자열 리소스만 처리
            if (field.name.startsWith(prefix)) {
                val savedAppName = field.name.removePrefix(prefix).replace("_", " ") // 접두사를 제거한 이름
                if(savedAppName == appName) {
                    packageName = context.getString(field!!.getInt(null))
                    break
                }
            }
        }
        return packageName
    }

    override suspend fun isAppInstalled(packageName: String): Boolean {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        // 패키지 목록을 필터링
        val isExist = packages.firstOrNull { pkg ->
            try {
                pkg.packageName == packageName
            } catch (e: Exception) {
                Log.d("error","not found")
                false
            }
        }
        return isExist?.packageName == packageName
    }

    override suspend fun getAppIcon(appName: String): ImageBitmap {
        return try {
            val packageName = getPackageNameBy(appName)
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val drawable = packageManager.getApplicationIcon(appInfo)
            val bitmap = drawableToBitmap(drawable)
/*
            saveBitmapToExternalStorage(bitmap.asImageBitmap(), "${getPackageNameBy(appName)
                    .replace(".", "").lowercase(Locale.getDefault())}.png")

 */
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            return try {
                val resId = context.resources.getIdentifier(
                    getPackageNameBy(appName)
                        .replace(".", ""), "drawable", context.packageName
                )
                val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                bitmap.asImageBitmap()
            }catch(e: NullPointerException){
                val fallbackDrawable = ContextCompat.getDrawable(context, R.drawable.dkapplogo)
                val fallbackBitmap = drawableToBitmap(fallbackDrawable!!)
                fallbackBitmap.asImageBitmap()
            }
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is AdaptiveIconDrawable -> {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
            else -> {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
        }
    }

/*  앱 아이콘 이미지 추출 코드
    private fun saveBitmapToExternalStorage(bitmap: ImageBitmap, fileName: String) {
        val bitmapToSave = bitmap.asAndroidBitmap()
        val appExternalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (appExternalFilesDir != null) {
            val file = File(appExternalFilesDir, fileName)
            FileOutputStream(file).use { out ->
                bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Log.d("AppInfoImpl", "Bitmap saved to external storage: ${file.absolutePath}")
        } else {
            Log.e("AppInfoImpl", "Failed to get external storage directory")
        }
    }
 */

    override suspend fun getHourlyUsage(appName: String, numberAgo: Int, isInitialSetting: Boolean): Triple<List<Int>, String, Int> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val startTime = if(numberAgo == 0 && !isInitialSetting) {
            dateFactory.returnRightBeforeFixedTime()
        }else{
            dateFactory.returnTheDayStart(numberAgo)
        }
        val endTime = if(numberAgo == 0 && !isInitialSetting) {
            System.currentTimeMillis()
            }else{
            dateFactory.returnTheDayEnd(startTime)
        }
        val packageName = getPackageNameBy(appName)

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
                            val isInclude = dateFactory.getIncludedHourlyMark(lastEventTime, event.timeStamp)
                            if(isInclude != null){
                                val beforeHour = dateFactory.returnTheHour(lastEventTime)
                                val afterHour = dateFactory.returnTheHour(event.timeStamp)
                                hourlyUsage[beforeHour] += isInclude - lastEventTime
                                hourlyUsage[afterHour] += event.timeStamp - isInclude
                            }else{
                                val arrHour = dateFactory.returnTheHour(event.timeStamp)
                                hourlyUsage[arrHour] += (event.timeStamp - lastEventTime)
                            }
                            isAppInForeground = false
                        }
                    }
                }
            }
        }

        if (isAppInForeground && powerManager.isInteractive) {
            if(numberAgo == 0 && !isInitialSetting){
                val endHour = dateFactory.returnTheHour(endTime)
                hourlyUsage[endHour] += (endTime - lastEventTime)
            }else{
                val endHour = ((endTime - startTime) / (1000 * 60 * 60 )).toInt()
                if (endHour < 24) {
                    hourlyUsage[endHour] += (endTime - lastEventTime)
                }
            }
        }

        Log.d("hourly Usage", "$packageName : $startTime ~ $endTime : ${hourlyUsage.map { it / 1000 }}")

        val hourlyUsageInMinutes = hourlyUsage.map { (it / 1000 ).toInt() }

        return Triple(hourlyUsageInMinutes, dateFactory.returnStringDate(startTime), dateFactory.returnDayOfWeek(startTime))
    }

    override suspend fun getDailyUsage(appName: String, numberAgo: Int): Triple<Int, String, Int> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val startTime = dateFactory.returnTheDayStart(numberAgo)
        val endTime = dateFactory.returnTheDayEnd(startTime)
        val packageName = getPackageNameBy(appName)

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
                        if (powerManager.isInteractive) { // 스크린이 켜져있는지 여부
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

        return Triple((totalUsageTime / 1000 ).toInt(), dateFactory.returnStringDate(startTime), dateFactory.returnDayOfWeek(startTime))
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

        val packageName = getPackageNameBy(appName)
        val appUsage = usageStats.filter { it.packageName == packageName }
        var totalUsageTime = 0L
        appUsage.forEach {
            totalUsageTime += it.totalTimeInForeground
        }
        Log.d("weekly Hour", "$startTime ~ $endTIme : $totalUsageTime")
        return Pair((totalUsageTime / 7 / 1000 ).toInt(),dateFactory.returnStringDate(startTime))
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

        val packageName = getPackageNameBy(appName)
        val appUsage = usageStats.filter { it.packageName == packageName }
        var totalUsageTime = 0L
        appUsage.forEach {
            totalUsageTime += it.totalTimeInForeground
        }
        val totalDays = dateFactory.returnLastMonthEndDate(dateFactory.returnMonthStartFrom(numberAgo))
        Log.d("month Hour", totalUsageTime.toString())
        return Pair((totalUsageTime / totalDays / 1000 ).toInt(),dateFactory.returnStringDate(monthStartFromTime))
    }
}

