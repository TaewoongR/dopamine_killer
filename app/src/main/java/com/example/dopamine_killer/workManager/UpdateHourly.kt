package com.example.dopamine_killer.workManager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.coredomain.CoreDomain
import javax.inject.Inject

class UpdateHourly @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    private val coreDomain: CoreDomain
) : Worker(context, workerParams) {
    override fun doWork(): Result {


        return Result.success()
    }
}