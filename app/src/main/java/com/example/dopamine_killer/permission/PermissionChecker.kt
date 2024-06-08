package com.example.dopamine_killer.permission

import android.content.Context

interface PermissionChecker {
    fun checkAndRequestPermissions(context: Context)
}