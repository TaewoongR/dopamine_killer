package com.example.dopamine_killer.alarmReceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dopamine_killer.foregroundService.UpdateGoalService
import java.util.Calendar

class UpdateGoalReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "UPDATE_PERIODIC_GOAL") {
            val serviceIntent = Intent(context, UpdateGoalService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}

fun scheduleUpdatePeriodicGoal(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, UpdateGoalReceiver::class.java).apply {
        action = "UPDATE_PERIODIC_GOAL"
    }
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val triggerTime = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.MINUTE, 0) // 00분으로 설정
        set(Calendar.SECOND, 0) // 00초로 설정
        set(Calendar.MILLISECOND, 0) // 00밀리초로 설정
        add(Calendar.HOUR_OF_DAY, 1) // 다음 정각으로 설정
    }.timeInMillis

    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}

