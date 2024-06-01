package com.example.dopamine_killer.foregroundService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.dopamine_killer.R
import com.example.dopamine_killer.alarmReceiver.NotificationUtils

class ForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannel(this)
        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 서비스 작업 수행
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // 서비스 종료 시 작업 처리
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "DEFAULT_CHANNEL")
            .setContentTitle("Foreground Service")
            .setContentText("Foreground service is running...")
            .setSmallIcon(R.drawable.dkapplogo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }
}
