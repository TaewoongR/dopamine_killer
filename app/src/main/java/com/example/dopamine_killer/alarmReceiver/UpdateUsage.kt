package com.example.dopamine_killer.alarmReceiver

import android.app.Notification
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.dopamine_killer.R
import com.example.dopamine_killer.jobScheduler.ForegroundJobService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateUsage : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {
        // 알림 생성
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = NotificationCompat.Builder(context, "DEFAULT_CHANNEL")
            .setContentTitle("앱 사용량 업데이트")
            .setContentText("앱 사용량이 업데이트 되었습니다.")
            .setSmallIcon(R.drawable.dkapplogo)
            .build()
        notificationManager.notify(1, notification)

        // JobScheduler를 사용하여 작업 예약
        val componentName = ComponentName(context, ForegroundJobService::class.java)
        val jobInfo = JobInfo.Builder(123, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)  // 디바이스 재부팅 후에도 작업을 유지합니다.
            .setMinimumLatency(1 * 1000)  // 작업 시작까지 최소 지연 시간 (1초)
            .build()

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }
}