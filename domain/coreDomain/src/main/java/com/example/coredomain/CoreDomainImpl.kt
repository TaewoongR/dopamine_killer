package com.example.coredomain

import com.example.local.selectedApp.AppNameStorage
import com.example.repository.AppRepository
import com.example.repository.DailyRepository
import javax.inject.Inject

class CoreDomainImpl @Inject constructor(
    private val appRepository: AppRepository,
    private val dailyRepository: DailyRepository,
    private val selectedAppRepository: AppNameStorage
): CoreDomain{

    override fun updateSelectedApp() {
        TODO("Not yet implemented")
    }

    override fun updateEntireAppUsage() {
        TODO("Not yet implemented")
    }

    override fun updateDailyInfo() {
        TODO("Not yet implemented")
    }


}