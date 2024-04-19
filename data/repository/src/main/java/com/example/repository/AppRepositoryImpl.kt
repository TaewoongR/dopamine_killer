package com.example.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.local.AppDAO
import com.example.local.AppData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override fun updateAppName(): List<String> {
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
    override suspend fun updateAppTime(appName: String) {
        val packageManager = context.packageManager
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val appUsageTimeArray = IntArray(24) // 24시간에 대한 사용 시간을 저장할 배열
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val startTime = calendar.timeInMillis // 당일 0시

        // 현재 시간까지의 각 시간대별로 queryUsageStats()를 호출
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var totalHour = 0
        for (hour in 0..currentHour) {
            val beginTime = startTime + TimeUnit.HOURS.toMillis(hour.toLong())
            val endTime = beginTime + TimeUnit.HOURS.toMillis(1)

            // 해당 시간대에 대한 사용 통계를 조회
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                beginTime,
                endTime
            )

            // 특정 앱에 대한 사용 시간을 찾아서 배열에 저장
            val appStat = stats.firstOrNull { it.packageName == appName }

                appUsageTimeArray[hour] = appStat?.let { (it.totalTimeInForeground / 10).toInt() } ?: 0
                totalHour += appUsageTimeArray[hour]
        }
        val endTime = System.currentTimeMillis()

        // 유튜브의 패키지 이름은 "com.google.android.youtube" 입니다.
        val packageName = "com.google.android.youtube"

        // 지정된 기간 동안의 사용 통계를 조회합니다.
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        val youtubeStats = stats.firstOrNull { it.packageName == packageName }
        val total = youtubeStats?.totalTimeInForeground ?: 0L

        val appData = AppData(
            appName = appName,
            date = Calendar.DATE.toString(),
            hour00 = appUsageTimeArray[0],
            hour01 = appUsageTimeArray[1],
            hour02 = appUsageTimeArray[2],
            hour03 = appUsageTimeArray[3],
            hour04 = appUsageTimeArray[4],
            hour05 = appUsageTimeArray[5],
            hour06 = appUsageTimeArray[6],
            hour07 = appUsageTimeArray[7],
            hour08 = appUsageTimeArray[8],
            hour09 = appUsageTimeArray[9],
            hour10 = appUsageTimeArray[10],
            hour11 = appUsageTimeArray[11],
            hour12 = appUsageTimeArray[12],
            hour13 = appUsageTimeArray[13],
            hour14 = appUsageTimeArray[14],
            hour15 = appUsageTimeArray[15],
            hour16 = appUsageTimeArray[16],
            hour17 = appUsageTimeArray[17],
            hour18 = appUsageTimeArray[18],
            hour19 = appUsageTimeArray[19],
            hour20 = appUsageTimeArray[20],
            hour21 = appUsageTimeArray[21],
            hour22 = appUsageTimeArray[22],
            hour23 = appUsageTimeArray[23],
            totalHour = total,
            isCompleted = true,
        )
        scope.launch {
            localDataSource.upsert(appData)
        }
    }

    override suspend fun getAppDataByName(appName: String): AppData = withContext(Dispatchers.IO) {
        localDataSource.getByName(appName)  // 데이터베이스에서 모든 AppData 레코드를 가져옴
    }
}