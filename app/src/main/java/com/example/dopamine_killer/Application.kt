package com.example.dopamine_killer

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.dopamine_killer.workManager.CoreWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Hilt가 WorkManager를 자동으로 초기화
        // 정시에 반복 작업 예약
        scheduleHourlyDailyUsageUpdate()
        scheduleWeeklyUsageUpdate()
    }

    private fun scheduleHourlyDailyUsageUpdate() {
        val delay = calculateInitialDelay(1)

        val hourlyWorkRequest = PeriodicWorkRequestBuilder<CoreWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_HOURLY_DAILY_USAGE"))
            .addTag("HourlyDailyUsage")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "HourlyDailyUsageUpdate",
            ExistingPeriodicWorkPolicy.KEEP,  // 중복 예약 방지
            hourlyWorkRequest
        )
    }

    private fun scheduleWeeklyUsageUpdate() {
        val delay = calculateInitialDelay(24 * 7)

        val weeklyWorkRequest = PeriodicWorkRequestBuilder<CoreWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_WEEKLY_USAGE"))
            .addTag("WeeklyDailyUsage")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WeeklyUsageUpdate",
            ExistingPeriodicWorkPolicy.KEEP,  // 중복 예약 방지
            weeklyWorkRequest
        )
    }

    private fun calculateInitialDelay(hours: Int): Long {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTime
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, hours)
        }
        return calendar.timeInMillis - currentTime
    }
}
