package com.example.dopamine_killer.permission

import android.accessibilityservice.AccessibilityService
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.NotificationManagerCompat
import com.example.dopamine_killer.foregroundService.YourAccessibilityService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionUtils @Inject constructor() : PermissionChecker {

    override fun checkAndRequestPermissions(context: Context) {
        showNotificationPermissionDialog(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showOverlayPermissionDialog(context)
        }
        showAccessibilityPermissionDialog(context)
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        return notificationManagerCompat.areNotificationsEnabled()
    }

    private fun showNotificationPermissionDialog(context: Context) {
        val message = if (hasNotificationPermission(context)) {
            "앱 알림이 이미 설정되었습니다.\n설정 화면으로 이동하여 알림 설정을 확인하거나 변경하세요."
        } else {
            "앱 알림을 설정하려면 알림 설정 화면으로 이동하세요."
        }

        AlertDialog.Builder(context)
            .setTitle("알림 설정")
            .setMessage(message)
            .setPositiveButton("설정") { _, _ ->
                openNotificationSettings(context)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun openNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }

    private fun canDrawOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    private fun showOverlayPermissionDialog(context: Context) {
        val message = if (canDrawOverlays(context)) {
            "오버레이 권한이 이미 설정되었습니다.\n설정 화면으로 이동하여 오버레이 권한을 확인하거나 변경하세요."
        } else {
            "팝업창이 표시될 수 있도록 오버레이 권한을 설정하세요."
        }

        AlertDialog.Builder(context)
            .setTitle("오버레이 권한 설정")
            .setMessage(message)
            .setPositiveButton("설정") { _, _ ->
                openOverlaySettings(context)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun openOverlaySettings(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
        val expectedComponentName = ComponentName(context, service)
        val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledComponentName = ComponentName.unflattenFromString(componentNameString)
            if (enabledComponentName != null && enabledComponentName == expectedComponentName) {
                return true
            }
        }
        return false
    }

    private fun showAccessibilityPermissionDialog(context: Context) {
        val message = if (isAccessibilityServiceEnabled(context, YourAccessibilityService::class.java)) {
            "접근성 권한이 이미 설정되었습니다.\n설정 화면으로 이동하여 접근성 권한을 확인하거나 변경하세요."
        } else {
            "앱의 접근성 권한을 설정하세요.\n팝업창을 통해 앱으로 이동 가능하게 돕습니다."
        }

        AlertDialog.Builder(context)
            .setTitle("접근성 권한 설정")
            .setMessage(message)
            .setPositiveButton("설정") { _, _ ->
                openAccessibilitySettings(context)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}