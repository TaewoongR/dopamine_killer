package com.example.dopamine_killer.foregroundService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
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

    private val prefs: SharedPreferences by lazy {
        getSharedPreferences("user_token_prefs", Context.MODE_PRIVATE)
    }
    private val isBackgroundRunning: Boolean    // 앱 재실행 후에도 값이 초기화 되지 않도록 영구저장소에 값 저장
        get() = prefs.getBoolean("isBackgroundRunning", false)

    private val hourlyUpdateRunnable = object : Runnable {
        override fun run() {
            jobScope.launch {
                coreDomain.updateAutoHourlyDailyUsage()
                val warningType = coreDomain.monitoringUsageByGoal()
                val type = warningType.firstOrNull {
                    foregroundAppChecker.getForegroundApp() == appFetchingInfo.getPackageNameBy(it.second)
                }
                if (type?.first == 1) {
                    startWarningRoutine(type.second)
                } else {
                    stopWarningRoutine()
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

    private val warningRunnable = object : Runnable {
        var appName: String = ""
        override fun run() {
            showWarning(appName)
            mainHandler.postDelayed(this, 5000) // 5초마다 실행
        }
    }

    private val prefsChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == "token") {
            val token = sharedPreferences.getString(key, null)
            if (token != null) {
                if (!isBackgroundRunning) {        // 하나의 쓰레드만을 보장하는 플래그
                    mainHandler.post(hourlyUpdateRunnable)
                    mainHandler.post(weeklyUpdateRunnable)
                    prefs.edit().putBoolean("isBackgroundRunning", true).apply()
                }
                // ScreenStateReceiver 초기화 및 등록
                screenStateReceiver = ScreenStateReceiver(
                    onScreenOn = {
                        if(!isBackgroundRunning) {
                            mainHandler.post(hourlyUpdateRunnable)
                            prefs.edit().putBoolean("isBackgroundRunning", true).apply()
                        }
                    },
                    onScreenOff = {
                        if(isBackgroundRunning) {
                            mainHandler.removeCallbacks(hourlyUpdateRunnable)
                            prefs.edit().putBoolean("isBackgroundRunning", false).apply()
                        }
                    }
                )
                screenStateReceiver.register(this)

                // CustomPopupView 초기화
                customPopupView = CustomPopupView(this)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 알림 채널 생성
        createNotificationChannel()
        val notification = createNotification("도파민 킬러 대기중")
        startForeground(1, notification)        // foregroundService를 실행하기 위한 필수적 실행 함수
        foregroundAppChecker = ForegroundAppChecker(this)

        // SharedPreferences 리스너 등록
        prefs.registerOnSharedPreferenceChangeListener(prefsChangeListener)
        // 초기 SharedPreferences 값 확인 및 필요한 초기화 작업 수행
        prefsChangeListener.onSharedPreferenceChanged(prefs, "token")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY         // 재부팅 후 자동 실행 설정
    }

    override fun onDestroy() {
        super.onDestroy()
        jobScope.cancel() // 서비스 종료 시 jobScope 범위에서 실행되는 모든 Coroutine 취소
        mainHandler.removeCallbacks(hourlyUpdateRunnable) // 서비스 종료 시 Runnable 제거
        mainHandler.removeCallbacks(weeklyUpdateRunnable) // 서비스 종료 시 Runnable 제거
        mainHandler.removeCallbacks(warningRunnable) // 서비스 종료 시 warning Runnable 제거
        unregisterReceiver(screenStateReceiver) // 서비스 종료 시 리시버 해제
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("DEFAULT_CHANNEL", "과도한 도파민 방지중", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for foreground service"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
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

    private fun startWarningRoutine(appName: String) {
        warningRunnable.appName = appName
        showNotification("$appName 사용량이 목표 시간에 근접했습니다.")
        mainHandler.post(warningRunnable)
    }

    private fun stopWarningRoutine() {
        mainHandler.removeCallbacks(warningRunnable)
    }
}
