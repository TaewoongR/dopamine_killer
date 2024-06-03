package com.example.dopamine_killer.foregroundService

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.coredomain.CoreDomain
import com.example.dopamine_killer.R
import com.example.service.AppFetchingInfo
import com.example.service.DateFactoryForData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundService : Service() {
    @Inject
    lateinit var coreDomain: CoreDomain

    @Inject
    lateinit var appFetchingInfo: AppFetchingInfo

    @Inject
    lateinit var dateFactory: DateFactoryForData

    private val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var foregroundAppChecker: ForegroundAppChecker
    private lateinit var screenStateReceiver: ScreenStateReceiver
    private lateinit var customPopupView: CustomPopupView

    private val mainHandler = Handler(Looper.getMainLooper()) // 메인 스레드의 Handler
    private val hourlyUpdateRunnable = object : Runnable {
        override fun run() {
            jobScope.launch {
                coreDomain.updateAutoHourlyDailyUsage()
                val warningType = coreDomain.monitoringUsageByGoal()
                val type = warningType.firstOrNull {
                    foregroundAppChecker.getForegroundApp() == appFetchingInfo.getPackageNameBy(it.second)
                }
                if (type?.first == 1) {
                    showWarning(type.second)
                }
                warningType.forEach {
                    if (it.first == 2) {
                        showFailure(it.second)
                    }
                }
            }
            mainHandler.postDelayed(this, 60 * 1000) // 1분(60,000 밀리초)마다 실행
        }
    }

    private val weeklyUpdateRunnable = object : Runnable {
        override fun run() {
            if (dateFactory.returnDayOfWeek(System.currentTimeMillis()) == 1) {
                jobScope.launch {
                    coreDomain.updateWeeklyUsage()
                }
            }
            // 일주일(7일) 후에 다시 실행하도록 설정
            mainHandler.postDelayed(this, 7 * 24 * 60 * 60 * 1000L) // 7일 = 7 * 24 * 60 * 60 * 1000 밀리초
        }
    }

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification("Foreground service is running...")
        startForeground(1, notification)        // foregroundService를 실행하기 위한 필수적 실행 함수
        foregroundAppChecker = ForegroundAppChecker(this)

        mainHandler.post(hourlyUpdateRunnable)
        mainHandler.post(weeklyUpdateRunnable)

        // ScreenStateReceiver 초기화 및 등록
        screenStateReceiver = ScreenStateReceiver(
            onScreenOn = { mainHandler.post(hourlyUpdateRunnable) },
            onScreenOff = { mainHandler.removeCallbacks(hourlyUpdateRunnable) }
        )
        screenStateReceiver.register(this)

        // CustomPopupView 초기화
        customPopupView = CustomPopupView(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        jobScope.cancel() // 서비스 종료 시 Coroutine 취소
        mainHandler.removeCallbacks(hourlyUpdateRunnable) // 서비스 종료 시 Runnable 제거
        mainHandler.removeCallbacks(weeklyUpdateRunnable) // 서비스 종료 시 Runnable 제거
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

    private fun showWarning(appName: String) {
        val message = "$appName 사용량이 목표 시간에 근접했습니다."
        showNotification(message)
        showPopup(message)
    }

    private fun showFailure(appName: String) {
        val message = "$appName 사용량 목표 시간 달성에 실패했습니다."
        showNotification(message)
        showPopup(message)
    }

    private fun showNotification(message: String) {
        val updatedNotification = createNotification(message)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, updatedNotification)
    }

    private fun showPopup(message: String) {
        if (Settings.canDrawOverlays(this)) {
            mainHandler.post {
                customPopupView.showMessage(message)
            }
        }
    }
}
