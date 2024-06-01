package com.example.dopamine_killer.foregroundService

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.coredomain.CoreDomain
import com.example.dopamine_killer.R
import com.example.service.AppFetchingInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundService: Service() {
    @Inject
    lateinit var coreDomain: CoreDomain

    @Inject
    lateinit var appFetchingInfo: AppFetchingInfo

    private val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var foregroundAppChecker: ForegroundAppChecker

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification("Foreground service is running...")
        startForeground(1, notification)        // foregroundService를 실행하기 위한 필수적 실행 함수
        foregroundAppChecker = ForegroundAppChecker(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        jobScope.launch {
            while (isActive) {
                coreDomain.updateAutoHourlyDailyUsage()
                val warningType = coreDomain.monitoringUsageByGoal()
                val type = warningType.firstOrNull {
                    foregroundAppChecker.getForegroundApp() == appFetchingInfo.getPackageNameBy(it.second)
                }
                if(type?.first == 1) {
                    warningUsage(type.second)
                }
                warningType.forEach {
                    if(it.first == 2){
                        failGoal(it.second)
                    }
                }
                delay(60 * 1000)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        jobScope.cancel() // 서비스 종료 시 Coroutine 취소
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, "DEFAULT_CHANNEL")
            .setContentTitle("Dopamine Killer")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.dkapplogo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun updateUsage() {
        val updatedNotification = createNotification("앱 사용량 업데이트")
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, updatedNotification)
    }

    private fun warningUsage(appName: String) {
        val updatedNotification = createNotification("$appName 사용량이 목표 시간에 근접했습니다.")
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, updatedNotification)
    }

    private fun failGoal(appName: String) {
        val updatedNotification = createNotification("$appName 사용량 목표 시간 달성에 실패했습니다.")
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, updatedNotification)
    }
}
