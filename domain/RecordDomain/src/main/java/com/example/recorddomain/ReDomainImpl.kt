package com.example.recorddomain

import com.example.local.selectedApp.AppNameStorageInterface
import javax.inject.Inject

class ReDomainImpl @Inject constructor(
    private val appNameStorage: AppNameStorageInterface
):ReDomain {


}