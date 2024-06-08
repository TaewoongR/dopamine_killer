package com.example.dopamine_killer.foregroundService

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class YourAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Service connected
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events
    }

    override fun onInterrupt() {
        // Handle service interrupt
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_FORCE_STOP_APP) {
            val packageName = intent.getStringExtra(EXTRA_APP_PACKAGE)
            if (packageName != null) {
                forceStopApp(packageName)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun forceStopApp(packageName: String) {
        // Implement your logic to force stop the app
    }

    companion object {
        const val ACTION_FORCE_STOP_APP = "com.example.dopamine_killer.ACTION_FORCE_STOP_APP"
        const val EXTRA_APP_PACKAGE = "com.example.dopamine_killer.EXTRA_APP_PACKAGE"
    }
}
