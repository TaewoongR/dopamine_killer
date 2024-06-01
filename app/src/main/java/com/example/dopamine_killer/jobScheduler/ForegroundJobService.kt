package com.example.dopamine_killer.jobScheduler

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.dopamine_killer.foregroundService.ForegroundService

class ForegroundJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        // 작업이 아직 끝나지 않았으므로 true를 반환합니다.
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // 작업이 중단되었을 때 필요한 정리 작업을 수행합니다.
        return true
    }
}
