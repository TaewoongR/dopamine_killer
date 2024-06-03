package com.example.dopamine_killer.foregroundService

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class YourAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            packageNames = null // All packages
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }
        this.serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Handle accessibility events here if needed
    }

    override fun onInterrupt() {
        // Handle service interruption if needed
    }

    fun forceStopApp(packageName: String) {

    }
}
