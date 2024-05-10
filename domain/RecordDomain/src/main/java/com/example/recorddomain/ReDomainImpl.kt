package com.example.recorddomain

import com.example.local.selectedApp.AppNameStorageInterface
import javax.inject.Inject

class ReDomainImpl @Inject constructor(
    private val appNameStorage: AppNameStorageInterface
):ReDomain {

    override fun getSelectedAppName(): List<String> {
        return appNameStorage.getAll()
    }

    override fun saveAppName(appName: String) {
        appNameStorage.saveString("selected_app","youtube")
    }

}